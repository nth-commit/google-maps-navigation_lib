package com.gmnav.model.positioning;

import com.google.android.gms.maps.model.LatLng;

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
