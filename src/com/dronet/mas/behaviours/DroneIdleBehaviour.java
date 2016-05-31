package com.dronet.mas.behaviours;

import java.io.IOException;

import com.dronet.mas.agents.DroneAgent;
import com.dronet.mas.entities.Drone;

import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/**
 * Idle behaviour of the drone agent as a first state. In this behaviour, the agent is looking 
 * for tasks to allocate, waiting for task responses.
 * @author Jose María R. Barambones
 * @version 0.1
 */
final class DroneIdleBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 8649044040754320845L;

	private Drone drone;
	private int flag;
	
	public DroneIdleBehaviour(DroneAgent a) {
		super(a);
		this.drone = a.getDrone();
		this.flag = DroneAutomatonBehaviour.TRANSITION_IDLE_TO_IDLE;
	}
	
	@Override
	public void action() {	
		
		try {
			
			startUp();
			
			boolean done = false;
			
			do {
				
				ACLMessage msg = this.getAgent().blockingReceive();//new Random().nextInt(2000) + 10000);
				if (msg != null) {

					switch (msg.getPerformative()) {
					case ACLMessage.CFP:
						startUp();
						break;
					case ACLMessage.INFORM:
						System.out.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives INFORM(utility) message from " + msg.getSender().getLocalName()); // DEBUGGING
						onReceiptUtility(msg);
						break;
					case ACLMessage.REQUEST:
						done = sendAgree(msg.getSender().getLocalName());
						System.out.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives REQUEST(Allocate) message from " + msg.getSender().getLocalName()); // DEBUGGING
						break;
					case ACLMessage.REFUSE:
						System.out.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives REFUSE(Allocate) message from " + msg.getSender().getLocalName()); // DEBUGGING
						this.drone.getDomain().remove(msg.getSender().getLocalName());
						//requestsCounter++;
						break;
					default:
						System.err.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives " + ACLMessage.getPerformative(msg.getPerformative()) + "??? message from " + msg.getSender().getLocalName()); // DEBUGGING
					}
				}
				else {
					System.err.println(this.getAgent().getLocalName() + " wait too much time."); // DEBUGGING
					done = true;
				}
	
			} while (!done);
				
			
		} catch (UnreadableException | IOException | FIPAException e) {
			e.printStackTrace();
			this.getAgent().doSuspend();
		}

	}

	@Override
	public int onEnd() { return flag; }
	
	/**
	 * TODO Description
	 * @throws IOException
	 * @throws FIPAException 
	 */
	private void startUp() throws IOException, FIPAException {
		this.flag = DroneAutomatonBehaviour.TRANSITION_IDLE_TO_IDLE;
		
		System.out.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " looking for tasks."); // DEBUGGING
		
		((DroneAgent)this.getAgent()).setDroneDomain();
		
		for (String task : this.drone.getDomain().keySet()) 
			sendMessageToTask(task,ACLMessage.PROPOSE);
		
	}

	/**
	 * Prunning of Drone Node. The drone adds to its domain the task if their utility is greater than 0.
	 * Otherwise, there is no addition to domain and the drone sends a prune message to the task.
	 * @param msg Message received
	 * @throws UnreadableException
	 */
	private void onReceiptUtility(ACLMessage msg) throws UnreadableException {
		String task = msg.getSender().getLocalName();
		Double utility = (Double)msg.getContentObject();
		
		//this.drone.getDomain().put(msg.getSender().getLocalName(), utility);
		
		if (utility != null) {
			System.out.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives the utility value " + utility + " from " + task); // DEBUGGING
			if (utility <= 0) {
				this.drone.getDomain().remove(msg.getSender().getLocalName());
			}
			else {
				this.drone.getDomain().put(msg.getSender().getLocalName(), utility);
			}
		}
	}
	
	/**
	 * TODO Description
	 * @return 
	 * @throws IOException 
	 */
	private boolean sendAgree(String task) throws IOException {
		sendMessageToTask(task, ACLMessage.AGREE);

		this.drone.setAllocatedTask(task);

		flag = DroneAutomatonBehaviour.TRANSITION_IDLE_TO_BUSY;
		System.out.println(new java.util.Date().toString() + " - " + getAgent().getLocalName() + " changes from IDLE to BUSY"); // DEBUGGING
		
		return true;
	}

	/**
	 * TODO Description
	 * @param task
	 * @throws IOException
	 */
	private void sendMessageToTask(String task, int flag) throws IOException {
		ACLMessage aclMessage = new ACLMessage(flag);
		aclMessage.addReceiver(new AID(task, AID.ISLOCALNAME));
		aclMessage.setOntology("allocation");
		aclMessage.setLanguage(new SLCodec().getName());
		aclMessage.setEnvelope(new Envelope());
		aclMessage.getEnvelope().setPayloadEncoding("ISO8859_1");
		aclMessage.setContentObject(this.drone);
		this.getAgent().send(aclMessage);
		System.out.println(new java.util.Date().toString() + " - " + getAgent().getLocalName() + " sends message " + ACLMessage.getPerformative(flag) + " to task " + task); // DEBUGGING
	}
	
}
