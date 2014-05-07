package com.gmnav.model.positioning;

import java.util.List;

import com.gmnav.model.util.LatLngUtil;
import com.google.android.gms.maps.model.LatLng;

public abstract class AbstractSimulatedGps extends AbstractGps {
	
	protected GpsPosition currentPosition;
	
	protected final int TICK_MS = 500;
	
	private final int SPEED_LIMIT_KPH = 50;
	private final double KPH_TO_MPS = 0.277778;
	private final double SPEED_LIMIT_MPS = SPEED_LIMIT_KPH * KPH_TO_MPS;
	private final double S_TO_MS = 1000;
	
	public AbstractSimulatedGps(LatLng location) {
		currentPosition = new GpsPosition(location, 0, System.currentTimeMillis());
	}
	
	public abstract void followPath(final List<LatLng> path);
	
	@Override
	public void enableTracking() {
		forceTick();
	}

	@Override
	public void disableTracking() {
	}
	
	@Override
	public void forceTick() {
		onTickHandler.invoke(currentPosition);
	}
	
	@Override
	public LatLng getLastLocation() {
		return currentPosition.location;
	}
	
	protected void advancePosition(List<LatLng> path) {
		long newTime = System.currentTimeMillis();
		long timePassedMillisconds = TICK_MS; // newTime - currentPosition.timestamp;
		double distanceRemaining = (timePassedMillisconds / S_TO_MS) * SPEED_LIMIT_MPS;
		LatLng currentLocation = currentPosition.location;
		double currentBearing = 0;
		
		while (path.size() > 0 && distanceRemaining > 0) {
			LatLng nextLocationInPath = path.get(0);
			double distanceToNextPoint = LatLngUtil.distanceInMeters(currentLocation, nextLocationInPath);
			double distanceToTravel = Math.min(distanceToNextPoint, distanceRemaining);
			currentBearing = LatLngUtil.initialBearing(currentLocation, nextLocationInPath);
			currentLocation = LatLngUtil.travel(currentLocation, currentBearing, distanceToTravel);
			
			distanceRemaining -= distanceToTravel;
			if (distanceRemaining > 0) {
				path.remove(0);
			}
		}
		
		currentPosition = new GpsPosition(currentLocation, currentBearing, newTime);
	}

}
