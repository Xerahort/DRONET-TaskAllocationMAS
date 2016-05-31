package com.dronet.mas.behaviours;

import java.io.IOException;

import com.dronet.mas.agents.TaskAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

final class TaskDoneBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 3414201186385085098L;
	
	public TaskDoneBehaviour(TaskAgent a) {
		super(a);
	}

	@Override
	public void action() {
			System.out.println(new java.util.Date().toString() + " - " + getAgent().getLocalName() + " DONE. Good Job :)"); // DEBUGGING
			try {
				DFService.deregister(this.getAgent());
			} catch (FIPAException e) {
				e.printStackTrace();
			}
			
			ACLMessage msg = null;
			do {

				try {

					msg = this.getAgent().blockingReceive();
					if (msg!=null) {
						switch (msg.getPerformative()) {
						case ACLMessage.INFORM:
						case ACLMessage.PROPOSE:
							ACLMessage response = msg.createReply();
							response.setContentObject(new Double(0));
							response.setPerformative(ACLMessage.INFORM);
							this.getAgent().send(response);
							System.out.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives " + ACLMessage.getPerformative(msg.getPerformative()) + " message and sends utility value 0 (done) to " + msg.getSender().getLocalName()); // DEBUGGING
							break;
						default:
							System.err.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives " + ACLMessage.getPerformative(msg.getPerformative()) + "??? message from " + msg.getSender().getLocalName()); // DEBUGGING
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			} while (msg!=null);
	}

}
