package com.gmnav.model.map;

import com.gmnav.model.LatLng;

public class MapState {
	
	public LatLng location;
	public double bearing;
	public double tilt;
	public double zoom;
	
	public MapState(LatLng location, double bearing, double tilt, double zoom) {
		this.location = location;
		this.bearing = bearing;
		this.tilt = tilt;
		this.zoom = zoom;
	}
}
