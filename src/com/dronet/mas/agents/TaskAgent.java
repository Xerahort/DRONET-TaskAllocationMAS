package com.dronet.mas.agents;

import com.dronet.exceptions.TaskAgentException;
import com.dronet.exceptions.TaskException;
import com.dronet.frontend.Scenario;
import com.dronet.mas.behaviours.TaskAutomatonBehaviour;
import com.dronet.mas.entities.Task;
import com.dronet.mas.entities.Task.Action;
import com.dronet.mas.entities.Task.Priority;
import com.dronet.mas.utils.Location;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class TaskAgent extends Agent {

	private static final long serialVersionUID = 4064591457906397048L;
	
	public static final String TASK_SERVICE = "TASK";
	
	private Task task;
	
	@Override
	protected void setup() {
		super.setup();
		
		System.out.println(new java.util.Date().toString() + " - NEW TASK: " + this.getLocalName() + " created"); // DEBUGGING
		
		try {
			setTaskConfig(); // Link task to agent.
			registerTask(); // Register task into DF.
			//setTaskDomain(); // Looking for drones into DF.
			addBehaviour(new TaskBehaviour(this)); // Set the behaviour to agent.
			
			Scenario.getInstance().getCanvas().setWaypoint((int)this.task.getLocation().getLongitude(), (int)this.task.getLocation().getLatitude(), this.getLocalName(), this.task.getAction()); // Add to canvas
			
			System.out.println(this.task.toString());
			
		} catch (TaskAgentException | TaskException | NumberFormatException | FIPAException e) {
			System.out.println(e); // ERROR DEBUGGING
			e.getMessage();
			doDelete();
		}
	}

	/**
	 * Configures the task agent given its parameters location, priority, action and payload.
	 * @throws TaskAgentException
	 * @throws TaskException
	 * @throws NumberFormatException
	 */
	private void setTaskConfig() throws TaskAgentException, TaskException, NumberFormatException {
		Object [] params = getArguments();
		// Params array size must be 5, according to the drone constructor.
		if(params == null || params.length != 5)
			throw new TaskAgentException("Argument array size incorrect.");
		else {
			// Parse of arguments
			Location location = new Location(Double.parseDouble(params[0].toString()), Double.parseDouble(params[1].toString()));
			Priority priority = Priority.valueOf(params[2].toString().toUpperCase());
			Action action = Action.valueOf(params[3].toString().toUpperCase());
			double payload = Double.parseDouble(params[4].toString());
			// Call to constructor
			this.task = new Task(location, priority, action, payload);
				
			//this.doWait(1000); // DEBUGGING
			//System.out.println("Here are the params of " + getLocalName() + ":"); // DEBUGGING
			//System.out.println(task.toString()); // DEBUGGING
		}
	}
	
	/**
	 * Register of task in Directory service. The task is exposed for being searched by drones.
	 * @throws FIPAException 
	 */
	private void registerTask() throws FIPAException {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(this.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(TASK_SERVICE);
		sd.setName(TASK_SERVICE);
		dfd.addServices(sd);
		DFService.register(this,dfd);
	}
	
	/**
	 * TODO Description
	 * @throws FIPAException
	 */
	@SuppressWarnings("unused")
	private void setTaskDomain() throws FIPAException {
		
		//Looking for registered DRONE agents
		DFAgentDescription template=new DFAgentDescription();
		ServiceDescription templateSd=new ServiceDescription();
		templateSd.setType(DroneAgent.DRONE_SERVICE);
		template.addServices(templateSd);
		DFAgentDescription [] results = DFService.search(this, template);
		
		for (DFAgentDescription drone : results) {
			AID provider = drone.getName();
			this.task.getDomain().put(provider.getLocalName(),null);
		}
	}
	
	@Override
	protected void takeDown() {
		System.out.println(new java.util.Date().toString() + " - TASK: " + this.getLocalName() + " finished."); // DEBUGGING
		super.takeDown();
	}
	
	public Task getTask() { return this.task; }
	
	private class TaskBehaviour extends SequentialBehaviour {

		private static final long serialVersionUID = -5848646191180934845L;

		public TaskBehaviour(TaskAgent taskAgent) {
			super(taskAgent);
			this.addSubBehaviour(new TaskAutomatonBehaviour(taskAgent));
		}
	}
}
