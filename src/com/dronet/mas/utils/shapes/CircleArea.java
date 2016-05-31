package com.dronet.mas.utils.shapes;

import java.io.Serializable;

import com.dronet.mas.utils.Area;
import com.dronet.mas.utils.Location;

/**
 * @author Jose María R. Barambones
 * @version 0.1
 */
public class CircleArea implements Area, Serializable {

	private static final long serialVersionUID = 3698403421268557480L;
	
	private Location center;
	private double ratio;
	
	/**
	 * Constructor with Location & ratio
	 * @param location
	 * @param ratio
	 */
	public CircleArea(Location location, double ratio) {
		this.CreateCircleArea(location,ratio);
	}
	
	/**
	 * Constructor with coordinates & ratio
	 * @param latitude
	 * @param longitude
	 * @param ratio
	 */
	public CircleArea(double latitude, double longitude, double ratio) {
		this.CreateCircleArea(new Location (latitude, longitude), ratio);
	}

	/**
	 * Unified object instantiation for any constructor
	 * @param location
	 * @param ratio
	 */
	private void CreateCircleArea(Location location, double ratio) {
		this.center = location;
		this.ratio = ratio;
	}

	@Override
	public boolean isLocationWithinArea(Location location) {
		return this.center.distanceTo(location) <= ratio;
	}
	
	@Override
	public String toString() {
		return 
				"\t\tShape: Circle\n" +
				"\t\tCenter: " + this.center.toString() +
				"\t\tRatio: " + this.ratio + "\n";
	}

	public Location getCenter() { return center; }
	public void setCenter(Location center) { this.center = center; }
	public double getRatio() { return ratio; }
	public void setRatio(double ratio) { this.ratio = ratio; }
	
}
