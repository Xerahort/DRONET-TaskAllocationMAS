package com.dronet.mas.agents;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import com.dronet.frontend.Scenario;

import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * TODO Description
 * @author Jose María R. Barambones
 * @version 0.1
 */
public class ControllerAgent extends Agent {

	private static final long serialVersionUID = 6131999702134027851L;
	
	private static Stack<AgentController> tasks;
	private static Stack<AgentController> drones;
	private static ContainerController cc;
	
	public ControllerAgent() {
		tasks = new Stack<AgentController>();
		drones = new Stack<AgentController>();
	}
	 
	@Override
	protected void setup() {
		
			SequentialBehaviour controllerBehaviour = new SequentialBehaviour(this);
			
			controllerBehaviour.addSubBehaviour(new OneShotBehaviour() {

				private static final long serialVersionUID = 4540147097671788397L;

				@Override
				public void action() {
					Scenario gui = Scenario.getInstance();
					gui.setVisible(true);
					
					System.out.println("The simulation will start in 5 seconds.");
					this.getAgent().blockingReceive(2000);
					System.out.println("3...");
					this.getAgent().blockingReceive(1000);
					System.out.println("2...");
					this.getAgent().blockingReceive(1000);
					System.out.println("1...");
					this.getAgent().blockingReceive(1000);

					try {
						
						/* SCENARIO RESOLUTION 1060x600
						 *  
						 * Drone(location, role, battery, center, ratio, velocity, payload);
						 * Task(location, priority, action, payload);
						 */
						
						cc = getContainerController();

						BufferedReader br = new BufferedReader(new FileReader("tasks-2.txt"));
						String line;
						while ((line = br.readLine()) != null) {
							
							String[] params = line.split(":");
							//cc.createNewAgent(params[0], TaskAgent.class.getName(), params[1].split(",")).start();
							tasks.add(cc.createNewAgent(params[0], TaskAgent.class.getName(), params[1].split(",")));
							
						}
						br.close();
						
						br = new BufferedReader(new FileReader("drones-3.txt"));
						while ((line = br.readLine()) != null) {
							
							String[] params = line.split(":");
							cc.createNewAgent(params[0], DroneAgent.class.getName(), params[1].split(",")).start();
							//drones.add(cc.createNewAgent(params[0], DroneAgent.class.getName(), params[1].split(",")));

							
						}
						br.close();
						
						//hardTest1();
						
					} catch (IOException | StaleProxyException e) {
						this.getAgent().doDelete();
						e.printStackTrace();
					}
				}
			});
			
			controllerBehaviour.addSubBehaviour(new TickerBehaviour(this,1000) {

				private static final long serialVersionUID = -4988422109062946811L;

				@Override
				protected void onTick() {
					/*int index = (this.getTickCount()-2);
					if (index>=0 && index < tasks.size())
						try {
							tasks.get(index).start();
							//Looking for registered DRONE agents
							DFAgentDescription template=new DFAgentDescription();
							ServiceDescription templateSd=new ServiceDescription();
							templateSd.setType(DroneAgent.DRONE_SERVICE);
							template.addServices(templateSd);
							DFAgentDescription [] results = DFService.search(this.getAgent(), template);
							
							for (DFAgentDescription drone : results) {
								AID provider = drone.getName();
								ACLMessage aclMessage = new ACLMessage(ACLMessage.CFP);
								aclMessage.addReceiver(provider);
								aclMessage.setOntology("new task");
								aclMessage.setLanguage(new SLCodec().getName());
								aclMessage.setEnvelope(new Envelope());
								aclMessage.getEnvelope().setPayloadEncoding("ISO8859_1");
								this.getAgent().send(aclMessage);
							}
						} catch (StaleProxyException | FIPAException e) {
							e.printStackTrace();
						}//*/
					
					/**/
					try {	
						tasks.pop().start();
						//Looking for registered DRONE agents
						DFAgentDescription template=new DFAgentDescription();
						ServiceDescription templateSd=new ServiceDescription();
						templateSd.setType(DroneAgent.DRONE_SERVICE);
						template.addServices(templateSd);
						DFAgentDescription [] results = DFService.search(this.getAgent(), template);
						
						for (DFAgentDescription drone : results) {
							AID provider = drone.getName();
							ACLMessage aclMessage = new ACLMessage(ACLMessage.CFP);
							aclMessage.addReceiver(provider);
							aclMessage.setOntology("new task");
							aclMessage.setLanguage(new SLCodec().getName());
							aclMessage.setEnvelope(new Envelope());
							aclMessage.getEnvelope().setPayloadEncoding("ISO8859_1");
							this.getAgent().send(aclMessage);
						}
					} catch (Exception e) {
						//System.err.println("Empty task stack.");
					}//*/
					
					try {
						drones.pop().start();
					} catch (Exception exp) {
						//System.err.println("Empty drone stack.");
					}	
					
					Scenario.getInstance().getCanvas().repaint();
				}
			});
			
			this.addBehaviour(controllerBehaviour);

	}

	@SuppressWarnings("unused")
	private void hardTest1() throws StaleProxyException {
		// -gui task1:com.dronet.mas.agents.TaskAgent(1,1,high,deliver,5)
		AgentController task1 = cc.createNewAgent("task1", TaskAgent.class.getName(), new String[] {
				"600",		// Latitude
				"350",		// Longitude
				"high",		// Priority
				"deliver",	// Action
				"500"		// Payload
				});
		
		AgentController task2 = cc.createNewAgent("task2", TaskAgent.class.getName(), new String[] {
				"600",		// Latitude
				"600",		// Longitude
				"high",		// Priority
				"deliver",	// Action
				"500"		// Payload
				});
		
		AgentController task3 = cc.createNewAgent("task3", TaskAgent.class.getName(), new String[] {
				"500",		// Latitude
				"600",		// Longitude
				"high",		// Priority
				"deliver",	// Action
				"50"		// Payload
				});
		
		AgentController task4 = cc.createNewAgent("task4", TaskAgent.class.getName(), new String[] {
				"350",		// Latitude
				"350",		// Longitude
				"high",		// Priority
				"deliver",	// Action
				"50"		// Payload
				});
		
		AgentController drone1 = cc.createNewAgent("drone1", DroneAgent.class.getName(), new String[] {
				"500",					// Latitude
				"400",					// Longitude
				"deliver_with_camera",	// Role
				"50000",				// Battery
				"500",					// Center Latitude
				"300",					// Center Longitude
				"200",					// Ratio
				"10",					// Max. Speed
				"10000"					// Max. Payload 
				});
		
		AgentController drone2 = cc.createNewAgent("drone2", DroneAgent.class.getName(), new String[] {
				"450",					// Latitude
				"500",					// Longitude
				"deliver_with_camera",	// Role
				"50000",				// Battery
				"400",					// Center Latitude
				"600",					// Center Longitude
				"300",					// Ratio
				"10",					// Max. Speed
				"10000"					// Max. Payload 
				});
		
		AgentController drone3 = cc.createNewAgent("drone3", DroneAgent.class.getName(), new String[] {
				"250",					// Latitude
				"400",					// Longitude
				"deliver_with_camera",	// Role
				"50000",				// Battery
				"400",					// Center Latitude
				"400",					// Center Longitude
				"175",					// Ratio
				"10",					// Max. Speed
				"10000"					// Max. Payload 
				});
				
		task1.start();
		task2.start();
		task3.start();
		task4.start();
		
		drone1.start();
		drone2.start();
		drone3.start();//*/
	}

	public static void addTask(String name, String [] params) throws StaleProxyException {
		tasks.push(cc.createNewAgent(name, TaskAgent.class.getName(), params));
	}

	public static void addDrone(String name, String [] params) throws StaleProxyException {
		drones.push(cc.createNewAgent(name, DroneAgent.class.getName(), params));
	}
	
	
	
}
