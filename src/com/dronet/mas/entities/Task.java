package com.dronet.mas.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import com.dronet.exceptions.TaskException;
import com.dronet.exceptions.UtilityException;
import com.dronet.mas.entities.Drone.Role;
import com.dronet.mas.utils.Location;
import com.dronet.mas.utils.shapes.CircleArea;

final public class Task implements Serializable {

	private static final long serialVersionUID = 536833761570078059L;
	
	private Location location;
	private Priority priority;
	private Action action;
	private Date timestamp;
	private double payload;
	private State state;
	
	private HashMap<String,Double> droneDomain;
	
	private String droneAllocated;
	
	// Auxiliar values for Payload-Speed Relationship.
	private static final double NORMAL_DISTRIBUTION_MEAN = 0;
	private static final double NORMAL_DISTRIBUTION_SD = Math.sqrt(0.2);
	
	public Task(double latitude, double longitude, Priority priority, Action action, double payload) throws TaskException{
		new Task(new Location(latitude,longitude),priority,action,payload);
	}
	
	public Task(Location location, Priority priority, Action action, double payload) throws TaskException {
		if (payload < 0)
			throw new TaskException("The payload cannot be less than 0.");
		else if (action != Action.DELIVER && payload > 0)
			throw new TaskException("There is no payload in non-delivering tasks.");
		else {
			this.action = action;
			this.payload = payload;
			this.location = location;
			this.priority = priority;
			this.state = State.PRUNNING;
			this.timestamp = new java.util.Date(); // Set the timestamp according to the instantiation moment.
			this.droneDomain = new HashMap<String,Double>();
			this.droneAllocated = null;
		}
	}
	
	/**
	 * We define the Utility as the profitableness value of a Drone allocated 
	 * to a single Event. The Utility is determined relative to the available 
	 * resources of the drone (battery, payload, role, …) and the Event 
	 * conditions (Priority, location, action, …).
	 * @param drone
	 * @return utility value
	 * @throws UtilityException 
	 */
	public double computeUtility(Drone drone) throws UtilityException{
		/* Utility = Priority * Availability * RemainingResources / DistanceToTask
		 * For avoiding useless computation, the availability value is calculated
		 * first. Only if it is different than 0, the remaining calculations will 
		 * be done.
		 */
		double availability = computeAvailability(drone.getArea(),drone.getRole());
		//System.err.println("availability: " + availability); // DEBUGGING
		if (availability <= 0)
			return 0;
		else {
			double priority = computePriority();
			//System.err.println("priority: " + priority); // DEBUGGING
			double distance = this.location.distanceTo(drone.getLocation());
			//System.err.println("distance: " + distance); // DEBUGGING
			double remainingResources = computeRemainingResources(
					drone.getBattery(),
					drone.getPayload(),
					drone.getVelocity(),
					drone.getLocation(),
					Drone.BATTERY_THRESHOLD, 
					this.action == Action.DELIVER ? true : false);
			//System.err.println("remaining: " + remainingResources); // DEBUGGING

			double utility = priority * remainingResources / distance;
			//System.err.println("utility: " + utility); // DEBUGGING
			return utility;
		}		
	}

	/**
	 * Measure the priority value given the priority level and the timestamp:
	 * For avoid computational overhead and communication burden, priority should 
	 * be recalculated at the same rate as the negotiation phase of the algorithm.
	 * @return priority value
	 */
	private double computePriority() {
		// P(Priority) = pi * (T – ti) Where T is Current Time (seconds).
		return this.priority.getNumVal() * (Math.abs(new java.util.Date().getTime() - this.timestamp.getTime()));
	}
	
	/**
	 * Measure the availability of the drone related with the task. The drone is 
	 * capable to do the action if their location is in its availability area and 
	 * its role includes the action to perform. This value configures the 
	 * constraint graph.
	 * @param area availability area of the drone
	 * @param role actions the drone can perform
	 * @return availability value
	 */
	private double computeAvailability(CircleArea area, Role role) throws UtilityException{
		// Firstly, the action-role is checked.
		switch (role) {
		case SEARCH:
			// Search drones cannot deliver
			if (this.action == Action.DELIVER) 
				return 0;
			break;
		case DELIVER_NO_CAMERA:
			// Deliver drones with no camera cannot search or record.
			if (this.action == Action.SEARCH || this.action == Action.SENSOR)
				return 0;
			break;
		case DELIVER_WITH_CAMERA:
			// Deliver drones with camera can do all actions.
			break;
		default:
			// If the execution reaches this line, something is wrong in the code.
			throw new UtilityException("Wrong computing of Availability. This exception must not be reached.");
		}
		/* Secondly, the distance drone-task is checked. Given a circle area, the
		 * task is available if the distance between the task ant the center is 
		 * less or equal than the ratio.
		 */
		//System.out.println("DISTANCE: " + this.location.toString() + "|" + area.getCenter().toString() + " = " + this.location.distanceTo(area.getCenter()));
		if (this.location.distanceTo(area.getCenter()) > area.getRatio())
			return 0;
		return 1;
	}
	
	/**
	 * It measures the remaining resources based on the consumed resource time if
	 * the drone is assigned to the task. Is a first approximation of available 
	 * resources of the drone for this scenario. The comsumed resource time is 
	 * based on speed and distance. The remaining resource cannot be less than a
	 * battery threshold for avoiding risks to the drone.
	 * @param droneBattery currentBattery of the drone
	 * @param dronePayload max drone payload
	 * @param droneVelocity max drone speed
	 * @param droneLocation drone location
	 * @param batteryThreshold battery threshold
	 * @param isDelivering 
	 * @return remaining resources in seconds
	 */
	private double computeRemainingResources(double droneBattery, double dronePayload, double droneVelocity, Location droneLocation, int batteryThreshold, boolean isDelivering) {
		// If the consumed resources cross the threshold, the value is 0.
		double remaining = droneBattery;
		if (!isDelivering)
			remaining -= this.location.distanceTo(droneLocation)/computeSpeedPayloadRelationship(droneVelocity);
		else 
			remaining -= this.location.distanceTo(droneLocation)/computeSpeedPayloadRelationship(this.payload, dronePayload, droneVelocity);
		// Secondly, the threshold is checked.
		if (remaining <= batteryThreshold)
			return 0;
		else
			return remaining;
	}

	/**
	 * Estimates the expected speed of the non-deliver drone during the event depending on the 
	 * weight it carries.
	 * @param droneVelocity max speed of the drone
	 * @return normalized percentage of speed
	 */
	public double computeSpeedPayloadRelationship(double droneVelocity) {
		return computeSpeedPayloadRelationship(0,1, droneVelocity);
	}

	/**
	 * Estimates the expected speed of the drone during the event depending on the 
	 * weight it carries.
	 * @param taskPayload payload of the task
	 * @param dronePayload max payload of the drone
	 * @param droneVelocity max speed of the drone
	 * @return normalized percentage of speed
	 */
	public double computeSpeedPayloadRelationship(double taskPayload, double dronePayload, double droneVelocity) {
		/* value = DroneVelocity * relPV(taskPayload/dronePayload)
		 * For simplicity, the relationship is defined from the density function of a normal distribution.
		 * Payload normalized related with the max payload. The function is only evaluated in the interval [0;1].
		 */
		double normalizedPayload;
		if (dronePayload > 0)
			normalizedPayload = taskPayload/dronePayload;
		else 
			normalizedPayload = 0;
		org.apache.commons.math3.distribution.NormalDistribution relPV = 
				new org.apache.commons.math3.distribution.NormalDistribution(NORMAL_DISTRIBUTION_MEAN,NORMAL_DISTRIBUTION_SD);
		//System.err.println(relPV.density(normalizedPayload) * 100); // DEBUGGING
		return droneVelocity * relPV.density(normalizedPayload);
	}

	// Getters & Setters
	public Location getLocation() { return location; }
	public Priority getPriority() { return priority; }
	public Action getAction() { return action; }
	public Date getTimestamp() { return timestamp; }
	public double getPayload() { return payload; }
	public State getState() { return state; }
	public HashMap<String,Double> getDomain() { return this.droneDomain; }
	public String getAllocatedDrone () { return this.droneAllocated; }
	
	public void setLocation(Location location) { this.location = location; }
	public void setPriority(Priority priority) { this.priority = priority; }
	public void setAction(Action action) { this.action = action; }
	public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
	public void setPayload(double payload) { this.payload = payload; }
	public void setState(State state) { this.state = state; }
	public void setAllocatedDrone(String drone) { this.droneAllocated = drone; }

	/**
	 * The Priority value defines the level of emergency of the task.
	 */
	public static enum Priority { 
		LOW(1), NORMAL(2), HIGH(3); 
		
	    private int numVal;

	    Priority(int numVal) { this.numVal = numVal; }

	    public int getNumVal() { return numVal; }
	}
	
	/**
	 * The Action that needs to be performed by the drone for completion.
	 */
	public static enum Action { 
		SEARCH("search"), SENSOR("sensor"), DELIVER("deliver"); 
		
	    private String strVal;

	    Action(String strVal) { this.strVal = strVal; }

	    public String getStrVal() { return strVal; }	
	}
	
	/**
	 * The State value defines task phase of the algorithm.
	 */
	public static enum State { PRUNNING, ALLOCATING, PERFORMING, DONE; }
	
	@Override
	public String toString() {
		return
				"Location: " + this.location.toString() +
				"Priority: " + this.priority.toString() + "\n" + 
				"Action: " + this.action.toString() + "\n" +
				"Payload: " + this.payload + "\n" +
				"Timestamp: " + this.timestamp.toString() + " (" + (new Date().getTime()-timestamp.getTime())/1000 + " secs ago)";
	}
		
}
