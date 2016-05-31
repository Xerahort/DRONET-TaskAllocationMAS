package com.dronet.testing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.dronet.mas.utils.Location;
import com.dronet.mas.utils.shapes.CircleArea;

/**
 * @author Jose María R. Barambones
 * @version 0.1
 */
public class CircleAreaTest {
	
	private CircleArea a1, a2;
	private Location l;
	
	/**
	 * Setup the values before testing.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		a1 = new CircleArea(new Location(8,8),2);
		a2 = new CircleArea(-5,9,20);
		l = new Location(10,10);
	}

	/**
	 * Test method for {@link com.dronet.mas.utils.CircleArea#isLocationWithinCircleArea(com.dronet.mas.utils.CircleArea)}.
	 */
	@Test
	public final void isLocationWithinCircleAreaTest() {
		assertFalse("The location must be out of the area", a1.isLocationWithinArea(l));
		assertTrue("The location must be inside of the area", a2.isLocationWithinArea(l));
	}

}
