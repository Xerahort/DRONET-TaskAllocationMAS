package com.dronet.mas.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import com.dronet.exceptions.DroneException;
import com.dronet.mas.utils.Location;
import com.dronet.mas.utils.shapes.CircleArea;

/**
 * Drone class definition. A drone is defined by its location, role, state
 * and availability. The drone receives Event notifications and evaluate 
 * its utility given the event requirements and its own condition. The 
 * involved drones decide the task allocation. If the drone is selected, it 
 * performs the task and it cannot participate in other task allocation 
 * until finish the current one. If the drone is no selected, it continues 
 * receiving Event notifications. For each environment change that involves 
 * the drone, its condition must be checked and updated (new notifications, 
 * drones, battery use, change of configured conditions, etc.).
 * @author Jose María R. Barambones
 * @version 0.1
 */
final public class Drone implements Serializable {
	
	private static final long serialVersionUID = 6655933310278853142L;
	
	private Location location;
	private Role role;
	private State state;
	private Availability availability;
	
	private HashMap<String,Double> taskDomain;
	
	private HashMap<String,Date> taskHistory;
	
	private String allocatedTask;
	
	/**
	 * Defines the low battery threshold for the drones. It is useful for
	 * the utility calculation. Given in seconds.
	 */
	public final static int BATTERY_THRESHOLD = 10; // 10 seconds.
	
	public Drone (Location location, Role role, double battery, Location center, double ratio, double velocity, double payload) throws DroneException{
		if (role.equals(Role.SEARCH) && payload>0)
			throw new DroneException("The payload must be 0 for Search Drones.");
		if (location.distanceTo(center)>ratio)
			throw new DroneException("The drone cannot be located out of the availability area.");
		this.availability = new Availability(battery, center, ratio, velocity, payload);
		this.location = location;
		this.role = role;
		this.state = State.IDLE;
		this.taskDomain = new HashMap<String,Double>();
		this.taskHistory = new HashMap<String,Date>();
		this.allocatedTask = null;
	}
	
	// Getters & Setters
	public Location getLocation() { return location; }
	public Role getRole() { return role; }
	public State getState() { return state; }
	public Availability getAvailability() { return availability; }
	public double getBattery() { return this.getAvailability().getBattery(); }
	public CircleArea getArea() { return this.getAvailability().getArea(); }
	public double getVelocity() { return this.getAvailability().getVelocity(); }
	public double getPayload() { return this.getAvailability().getPayload(); }
	public HashMap<String,Double> getDomain() { return this.taskDomain; }
	public HashMap<String,Date> getHistory() { return this.taskHistory; }
	public String getAllocatedTask() { return this.allocatedTask; }
	
	public void setLocation(Location location) { this.location = location; }
	public void setRole(Role role) { this.role = role; }
	public void setState(State state) { this.state = state; }
	public void setAvailability(Availability availability) { this.availability = availability; }
	public void setBattery(double battery) { this.getAvailability().setBattery(battery); }
	public void setArea(CircleArea area) { this.getAvailability().setArea(area); }
	public void setVelocity(double velocity) { this.getAvailability().setVelocity(velocity); }
	public void setPayload(double payload) { this.getAvailability().setPayload(payload); }
	public void setAllocatedTask(String task) { this.allocatedTask = task; }
	
	/**
	 * The Role value defines the drone features related with actions.
	 */
	public static enum Role { SEARCH, DELIVER_WITH_CAMERA, DELIVER_NO_CAMERA; }
	
	/**
	 * The State value defines drone availability in the allocation.
	 */
	public static enum State { IDLE, BUSY, CRITICAL; }
	
	/**
	 * The availability provides several values for measuring its utility
	 * related with a specific task: Battery time, the available area,
	 * speed and max payload.
	 */
	class Availability implements Serializable {

		private static final long serialVersionUID = -2600589041856965847L;
		
		private double battery, maxBattery;
		private CircleArea area;
		private double velocity;
		private double payload;
		
		public Availability(double battery, Location center, double ratio, double velocity, double payload) throws DroneException{
			if (payload<0)
				throw new DroneException("The payload cannot be less than 0.");
			else if (battery<0)
				throw new DroneException("The battery cannot be less than 0.");		
			else if (velocity<=0)
					throw new DroneException("The battery cannot be less or equal than 0.");
			else if (ratio<=0)
				throw new DroneException("The ratio cannot be less or equal than 0.");
			else { 
				// The drone is setted with full battery.
				this.battery = battery;
				this.maxBattery = battery;
				this.area = new CircleArea(center, ratio);
				this.velocity = velocity;
				this.payload = payload;
			}
		}

		// Getters & Setters
		private double getBattery() { return battery; }
		private double getMaxBattery() { return this.maxBattery; }
		private CircleArea getArea() { return area; }
		private double getVelocity() { return velocity; }
		private double getPayload() { return payload; }
		
		private void setArea(CircleArea area) { this.area = area; }
		private void setVelocity(double velocity) { this.velocity = velocity; }
		private void setPayload(double payload) { this.payload = payload; }
		private void setBattery(double battery) { 
			if (battery > this.maxBattery) 
				this.battery = this.maxBattery; 
			else if (this.battery < 0) 
				this.battery = 0;
			else 
				this.battery = battery;
		}
	
	}
	
	@Override
	public String toString() {
		return
				"Location: " + this.location.toString() +
				"Role: " + this.role.toString() + "\n" + 
				"State: " + this.state.toString() + "\n" +
				"Availability:\n" + 
				"\tBattery:\n" + 
				"\t\tTotal: " + this.availability.getBattery() + "\n" +
				"\t\tRemaining: " + this.availability.getMaxBattery() + "\n" +
				"\tArea:\n" + this.availability.getArea() +
				"\tMax. Speed: " + this.availability.getVelocity() + "\n" +
				"\tMax. Payload: " + this.availability.getPayload();
	}

	/**
	 * Service for updating the battery through time. If the battery
	 * decreases under the threshold, low battery flag is sended.
	 * @param l time in seconds to substract.
	 * @return TRUE if low battery.
	 */
	public boolean updateBattery(long seconds) {
		this.setBattery(this.getBattery()-seconds);
		if (this.getBattery()<Drone.BATTERY_THRESHOLD) 
			return true;
		else 
			return false;
	}
}
