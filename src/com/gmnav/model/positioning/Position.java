package com.gmnav.model.positioning;

import com.google.android.gms.maps.model.LatLng;

public class Position {

	public LatLng location;
	public double bearing;
	
	public Position(LatLng location, double bearing) {
		this.location = location;
		this.bearing = bearing;
	}
}
