package com.dronet.testing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.dronet.mas.utils.Location;

/**
 * @author Jose María R. Barambones
 * @version 0.1
 */
public class LocationTest {
	
	private Location l1, l2;
	private final double x1 = 2;
	private final double x2 = -1;
	private final double y1 = 0;
	private final double y2 = -4;
	
	/**
	 * Setup the values before testing.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception{
		l1 = new Location(y1, x1);
		l2 = new Location(y2, x2);
	}

	/**
	 * Test method for {@link com.dronet.mas.utils.Location#distanceTo(com.dronet.mas.utils.Location)}.
	 */
	@Test
	public final void testDistanceTo() {
		double distance = 5;
		assertTrue("The distance must be " + distance + " and it is (" + l1.distanceTo(l2) + "," + l2.distanceTo(l1) + ")", 
				distance == l1.distanceTo(l2) && distance == l2.distanceTo(l1));
	}
	
	/**
	 * Test method for {@link com.dronet.mas.utils.Location#equals(com.dronet.mas.utils.Location)}.
	 */
	@Test
	public final void testEquals() {
		double margin = -1;
		assertFalse("The locations must be different", l1.equals(l2,0));
		assertTrue("The locations must be equals", l1.equals(l1,0));
		assertTrue("The locations must be equals with some margin", l1.equals(new Location(l1.getLatitude()+margin, l1.getLongitude()-margin),margin));
		assertFalse("The locations must be differents with some margin", l1.equals(new Location(l1.getLatitude(), l1.getLongitude()-margin*2),margin));
	}

}
