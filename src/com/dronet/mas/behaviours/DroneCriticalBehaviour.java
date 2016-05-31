package com.dronet.mas.behaviours;

import com.dronet.mas.agents.DroneAgent;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Critical behaviour of the drone agent. In this behaviour, the agent has risk and cannot perform any allocation.
 * @author Jose María R. Barambones
 * @version 0.1
 */
final class DroneCriticalBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 4155515583703435762L;

	int flag;
	
	public DroneCriticalBehaviour(DroneAgent a) {
		super(a);
		this.flag = -1;
	}

	@Override
	public void action() {
		
		flag = DroneAutomatonBehaviour.TRANSITION_CRIT_TO_IDLE;
		System.out.println(getAgent().getLocalName() + " changes from CRITICAL to IDLE at " + new java.util.Date().toString()); // DEBUGGING
		
	}
	
	@Override
	public int onEnd() {
		return flag;
	}

}
