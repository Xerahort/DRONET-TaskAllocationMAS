package com.dronet.mas.behaviours;

import com.dronet.mas.agents.DroneAgent;
import com.dronet.mas.entities.Drone;
import com.dronet.mas.entities.Drone.State;

import jade.core.behaviours.TickerBehaviour;

/**
 * Ticker Behavior for drone battery updating.
 * @author Jose María R. Barambones
 * @version 0.1
 */
final public class DroneBatteryBehaviour extends TickerBehaviour {

	private static final long serialVersionUID = -859958090440169200L;
	
	private Drone drone;
	
	public DroneBatteryBehaviour(DroneAgent a, long periodInSeconds) {
		super(a, periodInSeconds*1000);
		this.drone = a.getDrone();
		this.setFixedPeriod(true);
	}

	/* (non-Javadoc)
	 * @see jade.core.behaviours.TickerBehaviour#onTick()
	 */
	@Override
	protected void onTick() {
		/* The update is the period in seconds. The state is changed if low battery flag
		 * is received. If the battery is empty, the drone is out of service and deleted
		 * from the container.
		 */
		if (drone.updateBattery(this.getPeriod()/1000)) {
			System.out.println(getAgent().getLocalName() + " battery: " + drone.getBattery());
			drone.setState(State.CRITICAL);
		}
		if (drone.getBattery()<=0)
			this.stop();
	}
	
	@Override
	public int onEnd() {
		this.getAgent().doDelete();
		return super.onEnd();
	}

}
