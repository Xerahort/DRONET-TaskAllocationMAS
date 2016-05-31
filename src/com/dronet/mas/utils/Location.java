package com.dronet.mas.utils;

import java.io.Serializable;

/**
 * The Location class allows the entitity positioning in the coordinate plane.
 * @author Jose María R. Barambones
 * @version 0.1
 */
public final class Location implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private double latitude;
	private double longitude;
	
	/**
	 * @param latitude
	 * @param longitude
	 */
	public Location(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * @return location latitude
	 */
	public double getLatitude() { return this.latitude; }
	
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) { this.latitude = latitude; }
	
	/**
	 * @return location longitude
	 */
	public double getLongitude() { return this.longitude; }
	
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) { this.longitude = longitude; }
	
	/**
	 * Measures the distance between this location relative to another.
	 * @param location
	 * @return distance
	 */
	public double distanceTo(Location location){
		return 
				Math.sqrt(
						Math.pow(location.getLongitude() - this.longitude,2) +
						Math.pow(location.getLatitude() - this.latitude,2));
	}
	
	/**
	 * Equal locations with circular error margin.
	 * @param location
	 * @param error
	 * @return boolean true if both locations have the same coordinates.
	 */
	public boolean equals(Location location, double margin){
		margin = Math.abs(margin);
		return 
				this.latitude >= location.getLatitude() - margin && 
				this.latitude <= location.getLatitude() + margin && 
				this.longitude >= location.getLongitude() - margin &&
				this.longitude <= location.getLongitude() - margin;
	}
	
    public String toString() {
        return "(" + latitude + ", " + longitude + ")\n" ;
    }

    public double signedLatitudeDistanceTo(Location location) {
    	return this.latitude - location.getLatitude();
    }
    
    public double signedLongitudeDistanceTo(Location location) {
    	return this.longitude - location.getLongitude();
    }
}
