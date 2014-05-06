package com.gmnav.positioning;

import com.google.android.gms.maps.model.LatLng;

public class GpsPosition extends Position {
	
	public long timestamp;

	public GpsPosition(LatLng location, double bearing, long timestamp) {
		super(location, bearing);
		this.timestamp = timestamp;
	}

}
