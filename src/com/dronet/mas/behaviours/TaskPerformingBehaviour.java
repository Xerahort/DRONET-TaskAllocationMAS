package com.dronet.mas.behaviours;

import java.io.IOException;

import com.dronet.frontend.Scenario;
import com.dronet.mas.agents.TaskAgent;
import com.dronet.mas.entities.Task;

import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.lang.acl.ACLMessage;

final class TaskPerformingBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = -7956219951922758355L;

	private int flag;
	private Task task;
	
	public TaskPerformingBehaviour(TaskAgent a) {
		super(a);
		this.task = a.getTask();
		this.flag = TaskAutomatonBehaviour.TRANSITION_PERF_TO_PERF;
	}
	
	@Override
	public void action() {
		
		try {
			sendTaskRequirements();

			do {

				ACLMessage msg = this.getAgent().blockingReceive();
				if (msg!=null) {
					switch (msg.getPerformative()) {
					case ACLMessage.INFORM:
						this.flag = TaskAutomatonBehaviour.TRANSITION_PERF_TO_DONE;
						System.out.println(new java.util.Date().toString() + " - " + getAgent().getLocalName() + " changes from PERFORMING to DONE"); // DEBUGGING
						Scenario.getInstance().getCanvas().removeWaipoint(this.getAgent().getLocalName(), this.task.getAction()); // Remove from canvas
						break;
					case ACLMessage.PROPOSE:
						ACLMessage response = msg.createReply();
						response.setContentObject(new Double(0));
						response.setPerformative(ACLMessage.INFORM);
						this.getAgent().send(response);
						System.out.println(new java.util.Date().toString() + " - " + getAgent().getLocalName() + " sends utility value 0 (already allocated) to " + msg.getSender().getLocalName()); // DEBUGGING
						break;
					case ACLMessage.REJECT_PROPOSAL:
						System.out.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives PRUNE message from " + msg.getSender().getLocalName()); // DEBUGGING
						this.task.getDomain().remove(msg.getSender().getLocalName());
						break;
					default:
						System.err.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives " + ACLMessage.getPerformative(msg.getPerformative()) + "??? message from " + msg.getSender().getLocalName()); // DEBUGGING
					}
				}

			} while (this.flag == TaskAutomatonBehaviour.TRANSITION_PERF_TO_PERF);

		} catch (IOException e) {
			e.printStackTrace();
			this.getAgent().doSuspend();
		}
	}

	private void sendTaskRequirements() throws IOException {
		ACLMessage aclMessage = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
		aclMessage.addReceiver(new AID(this.task.getAllocatedDrone(), AID.ISLOCALNAME));
		aclMessage.setOntology("allocation");
		aclMessage.setLanguage(new SLCodec().getName());
		aclMessage.setEnvelope(new Envelope());
		aclMessage.getEnvelope().setPayloadEncoding("ISO8859_1");
		aclMessage.setContentObject(this.task);
		this.getAgent().send(aclMessage);
	}

	@Override
	public int onEnd() { return flag; }
	
}
