package com.dronet.testing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.dronet.exceptions.TaskException;
import com.dronet.exceptions.UtilityException;
import com.dronet.mas.entities.Drone;
import com.dronet.mas.entities.Task;
import com.dronet.mas.entities.Drone.Role;
import com.dronet.mas.entities.Task.Action;
import com.dronet.mas.entities.Task.Priority;
import com.dronet.mas.utils.Location;

public class TaskTest {

	private Task t1, t2;
	private Drone d1, d2, d3, d4;
	private Location l1, l2, l3, l4;
	
	@Before
	public void setUp() throws Exception {
		l1 = new Location(0, 2);
		l2 = new Location(-4, -1);
		l3 = new Location(0, 0);
		l4 = new Location(-2,-2);
		
		t1 = new Task(l3, Priority.HIGH, Action.DELIVER, 10000);
		t2 = new Task(l4, Priority.LOW, Action.SEARCH, 0);
		
		d1 = new Drone(l1, Role.DELIVER_WITH_CAMERA, 50000, l3, 3, 1, 10000);
		d2 = new Drone(l2, Role.SEARCH, 50000, l4, 5, 1, 0);
		d3 = new Drone(l1, Role.DELIVER_NO_CAMERA, 10000, l1, 1, 1, 10000);
		d4 = new Drone(l2, Role.SEARCH, 600, l4, 5, 1, 0);
	}

	@Test(expected=TaskException.class)
	public final void testTaskConstructorBadAction() throws TaskException {
		new Task(l1, Priority.HIGH, Action.SEARCH, 1);
	}

	@Test(expected=TaskException.class)
	public final void testTaskConstructorBadPayload() throws TaskException {
		new Task(l1, Priority.HIGH, Action.SENSOR, 1);
	}
	
	@Test
	public final void testTaskConstructor() {
		assertNotEquals(t1, t2);
		assertEquals(t1.getLocation(), l3);
		assertEquals(t1.getAction(), Action.DELIVER);
		assertEquals(t2.getPriority(), Priority.LOW);
		assertNotEquals(t2.getTimestamp(), new java.util.Date());
	}
	
	@Test
	public final void testUtility() throws UtilityException {
		// We wait for 1 second for avoiding exact timestamps between tasks and drones.
		try {
		    Thread.sleep(1000);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		assertTrue(t1.computeUtility(d1) > 0);
		assertTrue(t1.computeUtility(d2) == 0);
		assertTrue(t1.computeUtility(d3) == 0);
		assertTrue(t2.computeUtility(d1) > 0);
		assertTrue(t2.computeUtility(d2) > 0);
		assertTrue(t2.computeUtility(d3) == 0);
		assertTrue(t2.computeUtility(d4) > 0);
	}
	
}
