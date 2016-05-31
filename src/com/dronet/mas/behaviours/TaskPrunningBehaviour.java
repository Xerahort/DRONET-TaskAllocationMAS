package com.dronet.mas.behaviours;

import java.io.IOException;
import java.util.Random;

import com.dronet.exceptions.UtilityException;
import com.dronet.mas.agents.TaskAgent;
import com.dronet.mas.entities.Drone;
import com.dronet.mas.entities.Task;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/**
 * @author Jose María R. Barambones
 * @version 0.1
 */
final class TaskPrunningBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = -4932326971914856843L;
	
	private Task task;
	int flag;
	
	public TaskPrunningBehaviour(TaskAgent a) {
		super(a);
		this.task = a.getTask();
		this.flag = TaskAutomatonBehaviour.TRANSITION_PRUN_TO_PRUN;
	}

	@Override
	public void action() {
		
		this.flag = TaskAutomatonBehaviour.TRANSITION_PRUN_TO_PRUN;
		
		//ACLMessage msg = this.getAgent().blockingReceive(MessageTemplate.or(TaskAgent.MT_ADD, TaskAgent.MT_PRUNE));
		ACLMessage msg = this.getAgent().blockingReceive(new Random().nextInt(1000));
		if (msg != null) {
			try {
				switch (msg.getPerformative()) {
				case ACLMessage.PROPOSE:
					System.out.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives ADD message from " + msg.getSender().getLocalName()); // DEBUGGING
					sendUtility(msg);
					break;
				case ACLMessage.REFUSE:
					System.err.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives REFUSE(prunning) message from " + msg.getSender().getLocalName()); // DEBUGGING
					this.task.getDomain().remove(msg.getSender().getLocalName());
					break;
				default:
					System.err.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives " + ACLMessage.getPerformative(msg.getPerformative()) + "??? message from " + msg.getSender().getLocalName()); // DEBUGGING
				}

			} catch (UnreadableException | UtilityException | IOException e) {
				e.printStackTrace();
				this.getAgent().doSuspend();
			}
		}
		else if (!this.task.getDomain().isEmpty()) {
			flag = TaskAutomatonBehaviour.TRANSITION_PRUN_TO_ALLO;
			
			// DEBUGGING
			/*for (String key : this.task.getDomain().keySet()) {
	            System.out.println(this.getAgent().getLocalName() + " " + key + " HIS " + this.task.getDomain().get(key));  
			}//*/
			
			System.out.println(new java.util.Date().toString() + " - " + getAgent().getLocalName() + " changes from PRUNNING to ALLOCATING"); // DEBUGGING
		}
	}

	private void sendUtility(ACLMessage msg) throws UnreadableException, UtilityException, IOException {
		Drone drone = (Drone)msg.getContentObject();
		if (drone!=null) {
			double utility = this.task.computeUtility(drone);
			if (utility > 0) {
				this.task.getDomain().put(msg.getSender().getLocalName(), utility);
				
				// DEBUGGING
				/*System.out.println(this.getAgent().getLocalName());
				for (String key : this.task.getDomain().keySet()) {
		            System.out.println(key + " " + this.task.getDomain().get(key));  
				}//*/
				
			}
			ACLMessage response = msg.createReply();
			response.setContentObject(utility);
			response.setPerformative(ACLMessage.INFORM);
			this.getAgent().send(response);
			System.out.println(new java.util.Date().toString() + " - " + getAgent().getLocalName() + " sends utility value " + utility + " to " + msg.getSender().getLocalName()); // DEBUGGING
		}
	}

	@Override
	public int onEnd() { return flag; }
	
}
