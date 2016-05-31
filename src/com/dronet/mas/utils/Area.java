package com.dronet.mas.utils;

/**
 * Interface for defining the available area of a drone, abstracting the shape of the area.
 * @author Jose María R. Barambones
 * @version 0.1
 */
public interface Area {
	
	/**
	 * Returns if a location is inside of the shape, abstracting the shape form.
	 * @param location
	 * @return true if the location is inside the of the shape.
	 */
	public boolean isLocationWithinArea(Location location);
	
}
