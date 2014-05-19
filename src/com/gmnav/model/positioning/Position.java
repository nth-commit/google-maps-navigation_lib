package com.gmnav.model.positioning;

import com.gmnav.model.LatLng;

public class Position {

	public LatLng location;
	public double bearing;
	public long timestamp;
	
	public Position(LatLng location, double bearing, long timestamp) {
		this.location = location;
		this.bearing = bearing;
		this.timestamp = timestamp;
	}
}
