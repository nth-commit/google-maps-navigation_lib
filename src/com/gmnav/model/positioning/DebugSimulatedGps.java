package com.gmnav.model.positioning;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class DebugSimulatedGps extends AbstractSimulatedGps {
	
	public DebugSimulatedGps(LatLng location) {
		super(location);
	}

	@Override
	public void followPath(List<LatLng> path) {
		currentPosition = new GpsPosition(currentPosition.location, 0, System.currentTimeMillis());
		onTickHandler.invoke(currentPosition);		
		while (path.size() > 0) {
			advancePosition(path);
			onTickHandler.invoke(currentPosition);				
		}
	}
}
