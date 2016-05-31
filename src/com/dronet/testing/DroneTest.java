package com.dronet.testing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.dronet.exceptions.DroneException;
import com.dronet.mas.entities.Drone;
import com.dronet.mas.entities.Drone.Role;
import com.dronet.mas.entities.Drone.State;
import com.dronet.mas.utils.Location;

public class DroneTest {
	
	private Drone d1, d2;
	private Location l1, l2, l3, l4;

	@Before
	public void setUp() throws Exception {
		l1 = new Location(0, 2);
		l2 = new Location(-4, -1);
		l3 = new Location(0, 0);
		l4 = new Location(-2,-2);
		
		d1 = new Drone(l1, Role.DELIVER_WITH_CAMERA, 50000, l3, 5, 1, 10000);
		d2 = new Drone(l2, Role.SEARCH, 50000, l4, 5, 1, 0);
	}

	@Test(expected=DroneException.class)
	public final void testDroneConstructorBadPayload() throws DroneException {
		new Drone(l1, Role.SEARCH, 1000, l3, 5, 1, 10000);
	}
	
	@Test(expected=DroneException.class)
	public final void testDroneConstructorLocationOutOfArea() throws DroneException {
		new Drone(l1, Role.DELIVER_NO_CAMERA, 1000, l3, 1, 1, 10000);
	}
	
	@Test
	public final void testDroneConstructor() {
		assertNotEquals(d1, d2);
		assertEquals(d1.getLocation(), l1);
		assertEquals(d2.getRole(), Role.SEARCH);
		assertEquals(d1.getState(), State.IDLE);
	}

}
