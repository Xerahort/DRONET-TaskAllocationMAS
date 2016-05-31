package com.dronet.mas.behaviours;

import java.io.IOException;

import com.dronet.mas.agents.TaskAgent;
import com.dronet.mas.entities.Task;

import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.lang.acl.ACLMessage;
/**
 * @author Jose María R. Barambones
 * @version 0.1
 */
final class TaskFindMaxBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 3559641065006596689L;

	private Task task;
	int flag;
	private String waitingResponse;
	
	public TaskFindMaxBehaviour(TaskAgent a) {
		super(a);
		this.task = a.getTask();
		this.flag = TaskAutomatonBehaviour.TRANSITION_ALLO_TO_ALLO;
		this.waitingResponse = null;
	}

	@Override
	public void action() {
		
		if (this.waitingResponse == null)
			sendAllocation();
		
		ACLMessage msg = this.getAgent().blockingReceive();
		if (msg != null) {
			try {
				switch (msg.getPerformative()) {
				case ACLMessage.AGREE:
					System.out.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives AGREE message from " + msg.getSender().getLocalName()); // DEBUGGING
					onPerform();
					break;
				case ACLMessage.REFUSE:
					System.out.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives REFUSE message from " + msg.getSender().getLocalName()); // DEBUGGING
					discard(msg);
					break;
				case ACLMessage.PROPOSE:
					System.out.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives ADD (allocating) message from " + msg.getSender().getLocalName()); // DEBUGGING
					ACLMessage response = msg.createReply();
					response.setContentObject(new Double(0));
					response.setPerformative(ACLMessage.INFORM);
					this.getAgent().send(response);
					System.out.println(new java.util.Date().toString() + " - " + getAgent().getLocalName() + " sends utility value 0 (allocating) to " + msg.getSender().getLocalName()); // DEBUGGING
					break;
				case ACLMessage.REJECT_PROPOSAL:
					System.out.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives PRUNE message from " + msg.getSender().getLocalName()); // DEBUGGING
					this.task.getDomain().remove(msg.getSender().getLocalName());
					break;
				default:
					System.err.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives " + ACLMessage.getPerformative(msg.getPerformative()) + "??? message from " + msg.getSender().getLocalName()); // DEBUGGING
				}	

			} catch (IOException e) {
				e.printStackTrace();
				this.getAgent().doSuspend();
			}
		}
	}

	private void sendAllocation() {
		
		this.waitingResponse = findMax();
		if (this.waitingResponse != null)
			sendMessageToDrone(this.waitingResponse, ACLMessage.REQUEST);
		else {
			flag = TaskAutomatonBehaviour.TRANSITION_ALLO_TO_PRUN;
			System.out.println(new java.util.Date().toString() + " - " + getAgent().getLocalName() + " changes from ALLOCATING to PRUNNING"); // DEBUGGING
		}
		
	}

	private String findMax() {
		
		String drone = null;
		double utility = 0;
		
		// DEBUGGING
		/*System.out.println(this.getAgent().getLocalName());
		for (String key : this.task.getDomain().keySet()) {
            System.out.println(this.getAgent().getLocalName() + " " + key + " " + this.task.getDomain().get(key));  
		}//*/
		
		for (String key : this.task.getDomain().keySet()) {
			
			double nextUtility = this.task.getDomain().get(key);
			if (utility < nextUtility) {
				drone = key;
				utility = nextUtility;
			}
		}
		return drone;
	}
	
	private void discard(ACLMessage msg) {
		
		this.task.getDomain().remove(msg.getSender().getLocalName());
		
		this.waitingResponse = null;
		
		if (this.task.getDomain().isEmpty()) {
			flag = TaskAutomatonBehaviour.TRANSITION_ALLO_TO_PRUN;
			System.out.println(new java.util.Date().toString() + " - " + getAgent().getLocalName() + " changes from ALLOCATING to PRUNING"); // DEBUGGING
		}
	}

	private void onPerform() {
		for (String drone : this.task.getDomain().keySet())
			if (!drone.equals(this.waitingResponse))
				sendMessageToDrone(drone, ACLMessage.REFUSE);
				
		this.task.setAllocatedDrone(this.waitingResponse);
		flag = TaskAutomatonBehaviour.TRANSITION_ALLO_TO_PERF;
		System.out.println(new java.util.Date().toString() + " - " + getAgent().getLocalName() + " changes from ALLOCATING to PERFORMING"); // DEBUGGING
	}

	private void sendMessageToDrone(String drone, int flag) {
		ACLMessage aclMessage = new ACLMessage(flag);
		aclMessage.addReceiver(new AID(drone, AID.ISLOCALNAME));
		aclMessage.setOntology("allocation");
		aclMessage.setLanguage(new SLCodec().getName());
		aclMessage.setEnvelope(new Envelope());
		aclMessage.getEnvelope().setPayloadEncoding("ISO8859_1");
		this.getAgent().send(aclMessage);
		System.out.println(new java.util.Date().toString() + " - " + getAgent().getLocalName() + " send message " + ACLMessage.getPerformative(flag) + "(Allocate) to " + drone); // DEBUGGING
	}
	
	@Override
	public int onEnd() { return flag; }
}
