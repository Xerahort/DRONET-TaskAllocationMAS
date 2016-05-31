package com.dronet.mas.behaviours;

import com.dronet.mas.agents.DroneAgent;
import com.dronet.mas.entities.Drone;
import jade.core.behaviours.FSMBehaviour;

/**
 * Drone Automaton Behaviour. Deploys a finite states machine that includes the different sub-behaviours
 * for each state and transitions between them given concrete conditions.
 * @author Jose María R. Barambones
 * @version 0.1
 */
final public class DroneAutomatonBehaviour extends FSMBehaviour {

	private static final long serialVersionUID = 8912927776390006050L;
	
	protected final static int TRANSITION_IDLE_TO_IDLE = 0;
	protected final static int TRANSITION_IDLE_TO_BUSY = 1;
	protected final static int TRANSITION_BUSY_TO_BUSY = 2;
	protected final static int TRANSITION_BUSY_TO_IDLE = 3;
	protected final static int TRANSITION_IDLE_TO_CRIT = 4;
	protected final static int TRANSITION_BUSY_TO_CRIT = 5;
	protected final static int TRANSITION_CRIT_TO_IDLE = 6;
	
	private DroneAgent droneAgent;

	public DroneAutomatonBehaviour(DroneAgent a) {
		super(a);
		this.droneAgent = a;
		setupAutomaton();
	}
	
	/**
	 * Configures the drone automaton, registering its transitions, statuses and attached behaviours.
	 */
	private void setupAutomaton() {
		
		// Register of drone states
		this.registerFirstState(new DroneIdleBehaviour(this.droneAgent), Drone.State.IDLE.toString());
		this.registerState(new DroneBusyBehaviour(this.droneAgent), Drone.State.BUSY.toString());
		this.registerState(new DroneCriticalBehaviour(this.droneAgent), Drone.State.CRITICAL.toString());
		
		// Register of transitions
		this.registerTransition(Drone.State.IDLE.toString(), Drone.State.IDLE.toString(), TRANSITION_IDLE_TO_IDLE);
		this.registerTransition(Drone.State.IDLE.toString(), Drone.State.BUSY.toString(), TRANSITION_IDLE_TO_BUSY);
		this.registerTransition(Drone.State.BUSY.toString(), Drone.State.BUSY.toString(), TRANSITION_BUSY_TO_BUSY);
		this.registerTransition(Drone.State.BUSY.toString(), Drone.State.IDLE.toString(), TRANSITION_BUSY_TO_IDLE);
		this.registerTransition(Drone.State.IDLE.toString(), Drone.State.CRITICAL.toString(), TRANSITION_IDLE_TO_CRIT);
		this.registerTransition(Drone.State.BUSY.toString(), Drone.State.CRITICAL.toString(), TRANSITION_BUSY_TO_CRIT);
		this.registerTransition(Drone.State.CRITICAL.toString(), Drone.State.IDLE.toString(), TRANSITION_CRIT_TO_IDLE);

	}
	
}
