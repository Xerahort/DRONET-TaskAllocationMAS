package com.dronet.mas.behaviours;

import com.dronet.mas.agents.TaskAgent;
import com.dronet.mas.entities.Task;

import jade.core.behaviours.FSMBehaviour;

/**
 * Drone Automaton Behaviour. Deploys a finite states machine that includes the different sub-behaviours
 * for each state and transitions between them given concrete conditions.
 * @author Jose María R. Barambones
 * @version 0.1
 */
final public class TaskAutomatonBehaviour extends FSMBehaviour {
	
	private static final long serialVersionUID = 8184128312973073292L;
	
	protected final static int TRANSITION_PRUN_TO_PRUN = 0;
	protected final static int TRANSITION_PRUN_TO_ALLO = 1;
	protected final static int TRANSITION_ALLO_TO_ALLO = 2;
	protected final static int TRANSITION_ALLO_TO_PERF = 3;
	protected final static int TRANSITION_ALLO_TO_PRUN = 4;
	protected final static int TRANSITION_PERF_TO_DONE = 5;
	protected static final int TRANSITION_PERF_TO_PERF = 6;
	
	private TaskAgent taskAgent;

	public TaskAutomatonBehaviour(TaskAgent a) {
		super(a);
		this.taskAgent = a;
		setupAutomaton();
	}
	
	/**
	 * Configures the drone automaton, registering its transitions, statuses and attached behaviours.
	 */
	private void setupAutomaton() {
		
		// Register of drone states
		this.registerFirstState(new TaskPrunningBehaviour(this.taskAgent), Task.State.PRUNNING.toString());
		this.registerState(new TaskFindMaxBehaviour(this.taskAgent), Task.State.ALLOCATING.toString());
		this.registerState(new TaskPerformingBehaviour(this.taskAgent), Task.State.PERFORMING.toString());
		this.registerLastState(new TaskDoneBehaviour(this.taskAgent), Task.State.DONE.toString());
		
		// Register of transitions
		this.registerTransition(Task.State.PRUNNING.toString(), Task.State.PRUNNING.toString(), TRANSITION_PRUN_TO_PRUN);
		this.registerTransition(Task.State.PRUNNING.toString(), Task.State.ALLOCATING.toString(), TRANSITION_PRUN_TO_ALLO);
		this.registerTransition(Task.State.ALLOCATING.toString(), Task.State.ALLOCATING.toString(), TRANSITION_ALLO_TO_ALLO);
		this.registerTransition(Task.State.ALLOCATING.toString(), Task.State.PERFORMING.toString(), TRANSITION_ALLO_TO_PERF);
		this.registerTransition(Task.State.ALLOCATING.toString(), Task.State.PRUNNING.toString(), TRANSITION_ALLO_TO_PRUN);
		this.registerTransition(Task.State.PERFORMING.toString(), Task.State.DONE.toString(), TRANSITION_PERF_TO_DONE);
		this.registerTransition(Task.State.PERFORMING.toString(), Task.State.PERFORMING.toString(), TRANSITION_PERF_TO_PERF);

	}

	@Override
	public int onEnd() {
		// If the behaviour finishes, the attached agent is suspended.
		getAgent().doSuspend();
		return super.onEnd();
	}

}
