package com.dronet.mas.agents;

import com.dronet.exceptions.DroneAgentException;
import com.dronet.exceptions.DroneException;
import com.dronet.frontend.Scenario;
import com.dronet.mas.behaviours.DroneAutomatonBehaviour;
import com.dronet.mas.behaviours.DroneBatteryBehaviour;
import com.dronet.mas.entities.Drone;
import com.dronet.mas.entities.Drone.Role;
import com.dronet.mas.utils.Location;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Main agent class attached to a drone. The agent is configured given its
 * parameters. Then, its behaviours are setted and the agent is finally
 * registered in the MAS container. If the agent has been bad configured, it is
 * removed from the container.
 * @author Jose María R. Barambones
 * @version 0.1
 */
public class DroneAgent extends Agent {

	private static final long serialVersionUID = 1744995520055011786L;
	
	// Message Filters
	public static final MessageTemplate MT_UTILITY = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
	public static final MessageTemplate MT_ALLOCATE = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

	public static final String DRONE_SERVICE = "DRONE";
	
	private Drone drone;
	
	@Override
	protected void setup() {
		super.setup();
		
		System.out.println(new java.util.Date().toString() + " - NEW DRONE: " + this.getLocalName() + " created"); // DEBUGGING
		
		try {
			setDroneConfig(); // Link drone to agent.
			registerDrone(); // Register drone into DF.
			setDroneDomain(); // Looking for tasks into DF.
			addBehaviour(new DroneBehaviour(this)); // Set the behaviour to agent.
			
			Scenario.getInstance().getCanvas().setDrone((int)this.drone.getLocation().getLongitude(), (int)this.drone.getLocation().getLatitude(), this.drone.getArea(), this.getLocalName(), this.drone.getRole()); // Add to canvas
			
			System.out.println(this.drone.toString());
			
		} catch (DroneAgentException | DroneException | NumberFormatException | FIPAException e) {
			System.out.println(e); // ERROR DEBUGGING
			e.getMessage();
			doDelete();
		}
	}

	/**
	 * Configures the drone agent given its parameters location, role, battery, 
	 * center, ratio, velocity and payload).
	 * @throws DroneAgentException if bad size of arguments.
	 * @throws DroneException if bad conditions of arguments.
	 * @throws NumberFormatException if bad parsing of arguments.
	 */
	private void setDroneConfig() throws DroneAgentException, DroneException, NumberFormatException{
		Object [] params = getArguments();
		// Params array size must be 9, according to the drone constructor.
		if(params == null || params.length != 9)
			throw new DroneAgentException("Argument array size incorrect.");
		else {
			// Parse of arguments
			Location location = new Location(Double.parseDouble(params[0].toString()), Double.parseDouble(params[1].toString()));
			Location center = new Location(Double.parseDouble(params[4].toString()), Double.parseDouble(params[5].toString()));
			Role role = Role.valueOf(params[2].toString().toUpperCase());
			double battery = Double.parseDouble(params[3].toString());
			double ratio = Double.parseDouble(params[6].toString());
			double velocity = Double.parseDouble(params[7].toString());
			double payload = Double.parseDouble(params[8].toString());
			// Call to constructor
			this.drone = new Drone(location, role, battery, center, ratio, velocity, payload);
			//System.out.println("Here are the params of " + getLocalName() + ":"); // DEBUGGING
			//System.out.println(drone.toString()); // DEBUGGING
		}
	}	

	/**
	 * Register of drone in Directory service.
	 * @throws FIPAException 
	 */
	private void registerDrone() throws FIPAException {		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(this.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(DRONE_SERVICE);
		sd.setName(DRONE_SERVICE);
		
		dfd.addServices(sd);
		DFAgentDescription [] results = DFService.search(this, dfd);
		
		if (results == null || results.length == 0)
			DFService.register(this,dfd);
	}
	
	/**
	 * TODO Description
	 * @throws FIPAException
	 */
	public void setDroneDomain() throws FIPAException {
		
		this.drone.getDomain().clear();
		
		//Looking for registered TASK agents
		DFAgentDescription template=new DFAgentDescription();
		ServiceDescription templateSd=new ServiceDescription();
		templateSd.setType(TaskAgent.TASK_SERVICE);
		template.addServices(templateSd);
		DFAgentDescription [] results = DFService.search(this, template);
		
		for (DFAgentDescription task : results) {
			AID provider = task.getName();
			this.drone.getDomain().put(provider.getLocalName(),Double.NaN);
		}
		
		// DEBUGGING
		/*for (String key : this.drone.getDomain().keySet()) {
            System.out.println(this.getLocalName() + " " + key + " DOMAIN " + this.drone.getDomain().get(key));  
		}*/
		
		for (String task : this.drone.getHistory().keySet()) {
			this.drone.getDomain().remove(task);
		}
		
		// DEBUGGING
		/*for (String key : this.drone.getHistory().keySet()) {
            System.out.println(this.getLocalName() + " " + key + " HIS " + this.drone.getDomain().get(key));  
		}
		
		// DEBUGGING
		for (String key : this.drone.getDomain().keySet()) {
            System.out.println(this.getLocalName() + " " + key + " DOM-HIS " + this.drone.getDomain().get(key));  
		}*/
	}

	@Override
	public void doWait() {
		System.out.println(new java.util.Date().toString() + " - DRONE: " + this.getLocalName() + " waiting for orders"); // DEBUGGING
		super.doWait();
	}
	
	@Override
	protected void takeDown() {
		System.out.println(new java.util.Date().toString() + " - DRONE: " + this.getLocalName() + " out of service"); // DEBUGGING
		super.takeDown();
	}

	public Drone getDrone() { return this.drone; }
	
	/**
	 * Main drone behaviour that contains the sub-behaviours.
	 * @author Jose María R. Barambones
	 * @version 0.1
	 */
	private class DroneBehaviour extends ParallelBehaviour {

		private static final long serialVersionUID = 307101784423498396L;
		
		public DroneBehaviour(DroneAgent droneAgent) {
			super(droneAgent, ParallelBehaviour.WHEN_ANY); // The behaviour is deleted if any sub-behaviour is stopped.
			
			/* The main behaviour is parallel-composed of the battery updating and the drone automaton.
			 * The behaviours are executed in the same agent thread through scheduling. The battery updating
			 * should not be affected for that schedule. Having said that, the battery behaviour is executed 
			 * on a independent thread for avoiding delays and improving in time performance. */
			this.addSubBehaviour(new ThreadedBehaviourFactory().wrap(new DroneBatteryBehaviour(droneAgent,1)));
			this.addSubBehaviour(new DroneAutomatonBehaviour(droneAgent));
		}
		
		@Override
		public int onEnd() {
			// If the behaviour finishes, the attached agent is deleted.
			getAgent().doDelete();
			return super.onEnd();
		}
	}
}
