package com.dronet.mas.behaviours;

import com.dronet.frontend.Scenario;
import com.dronet.mas.agents.DroneAgent;
import com.dronet.mas.entities.Drone;
import com.dronet.mas.entities.Task;
import com.dronet.mas.utils.Location;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * Busy behaviour of the drone agent. In this behaviour, the agent has a task already allocated 
 * and represents the time interval that includes the travelling to the task location plus the 
 * time to perform the action. Once the action has been performed, the agent can be return to 
 * idle behaviour again.
 * @author Jose María R. Barambones
 * @version 0.1
 */
final class DroneBusyBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 6876828517894913649L;

	private Drone drone;
	private Task taskToPerform;
	private ACLMessage sender;
	private int flag;
	private int remainingTicks;
	private double moveLatitudePerTick;
	private double moveLongitudePerTick;
	
	public DroneBusyBehaviour(DroneAgent a) {
		super(a);
		this.flag = DroneAutomatonBehaviour.TRANSITION_BUSY_TO_BUSY;
		this.drone = a.getDrone();
		this.remainingTicks = 0;
		this.sender = null;
	}
	
	private void getTimeToPerform() {
		try {
			this.sender = this.getAgent().blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
			this.taskToPerform = (Task)this.sender.getContentObject();
			double currentSpeed = taskToPerform.computeSpeedPayloadRelationship(taskToPerform.getPayload(), this.drone.getPayload(), this.drone.getVelocity());
			this.remainingTicks = (int) (this.drone.getLocation().distanceTo(this.taskToPerform.getLocation())/currentSpeed);
			this.moveLatitudePerTick = taskToPerform.getLocation().signedLatitudeDistanceTo(this.drone.getLocation())/this.remainingTicks;
			this.moveLongitudePerTick = taskToPerform.getLocation().signedLongitudeDistanceTo(this.drone.getLocation())/this.remainingTicks;
			System.out.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " will reach " + this.drone.getAllocatedTask() + " in " + this.remainingTicks + " seconds");
		} catch (UnreadableException e) {
			e.printStackTrace();
			this.getAgent().doSuspend();
		}
	}

	@Override
	public void action() {
		
		this.flag = DroneAutomatonBehaviour.TRANSITION_BUSY_TO_BUSY;
		
		if (this.sender == null)
			getTimeToPerform();

		Scenario.getInstance().getCanvas().setAllocation(this.getAgent().getLocalName(), this.drone.getRole(), this.drone.getAllocatedTask(), this.taskToPerform.getAction());
		
		ACLMessage msg = this.getAgent().receive();
		if (msg  != null) {
			switch (msg.getPerformative()) {
			case ACLMessage.REQUEST:
				System.err.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives REQUEST(Allocate during busy) message from " + msg.getSender().getLocalName()); // DEBUGGING
				ACLMessage response = msg.createReply();
				response.setPerformative(ACLMessage.REFUSE);
				this.getAgent().send(response);
				break;
			case ACLMessage.REFUSE:
				System.err.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives REFUSE(Allocate during busy) message from " + msg.getSender().getLocalName()); // DEBUGGING
				this.drone.getDomain().remove(msg.getSender().getLocalName());
				break;
			case ACLMessage.FAILURE:
				System.err.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives " + ACLMessage.getPerformative(msg.getPerformative()) + " message from " + msg.getSender().getLocalName()); // DEBUGGING
				break;
			case ACLMessage.CFP:
				System.err.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives new task message from " + msg.getSender().getLocalName()); // DEBUGGING
				break;
			default:
				System.err.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " receives " + ACLMessage.getPerformative(msg.getPerformative()) + "??? message from " + msg.getSender().getLocalName()); // DEBUGGING
			}
		}
		
		translateDrone();
		if (--this.remainingTicks<=0 || this.taskToPerform.getLocation().equals(this.drone.getLocation(), 0))
			sendFinishToTask();
		else	
			this.block(1000);
	}

	private void translateDrone() {
		double latitude = this.drone.getLocation().getLatitude();
		double longitude = this.drone.getLocation().getLongitude();
		this.drone.setLocation(
				new Location(
						latitude+this.moveLatitudePerTick, 
						longitude+this.moveLongitudePerTick
						));
		
		Scenario.getInstance().getCanvas().setDrone((int)this.drone.getLocation().getLongitude(), (int)this.drone.getLocation().getLatitude(), this.drone.getArea(), this.getAgent().getLocalName(), this.drone.getRole()); // Add to canvas

	}
	
	private void sendFinishToTask() {
		ACLMessage response = this.sender.createReply();
		response.setPerformative(ACLMessage.INFORM);
		this.getAgent().send(response);
		System.out.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName()  + " has reached and performed " + this.drone.getAllocatedTask());

		Scenario.getInstance().getCanvas().removeAllocation(this.getAgent().getLocalName(), this.drone.getRole()); // Remove from canvas
		
		this.drone.getHistory().put(this.drone.getAllocatedTask(), new java.util.Date());
		this.drone.getDomain().remove(this.drone.getAllocatedTask());
		this.drone.setAllocatedTask(null);
		this.sender = null;
		this.taskToPerform = null;
		
		flag = DroneAutomatonBehaviour.TRANSITION_BUSY_TO_IDLE;
		System.out.println(new java.util.Date().toString() + " - " + this.getAgent().getLocalName() + " changes from BUSY to IDLE"); // DEBUGGING
		
	}

	@Override
	public int onEnd() {
		return flag;
	}

}
