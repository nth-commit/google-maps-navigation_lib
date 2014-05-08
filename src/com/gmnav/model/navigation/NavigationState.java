package com.gmnav.model.navigation;

import java.io.InvalidObjectException;
import java.util.List;

import com.gmnav.model.directions.Directions;
import com.gmnav.model.directions.Point;
import com.gmnav.model.positioning.Position;
import com.gmnav.model.util.LatLngUtil;
import com.google.android.gms.maps.model.LatLng;

public class NavigationState {
	
	private final int LOOK_AHEAD_POINTS = 5;
	private final int LOOK_BEHIND_POINTS = 2;
	
	private List<Point> path;
	private Position position;
	private LatLng locationOnPath;
	private double bearingOnPath;
	private double distanceOffPath;
	private double bearingDifferenceFromPath;
	private int currentIndex;
	private Point currentPoint;
	private boolean isOnPath;
	private long offPathStartTime;
	private boolean isSnapshot;
	
	public NavigationState(Directions directions) {
		path = directions.getPath();
		currentIndex = 0;
		currentPoint = null;
		isOnPath = true;
		isSnapshot = false;
	}
	
	private NavigationState(NavigationState navigationStateSnapshot) {
		position = navigationStateSnapshot.getPosition();
		locationOnPath = navigationStateSnapshot.getLocationOnPath();
		bearingOnPath = navigationStateSnapshot.getBearingOnPath();
		distanceOffPath = navigationStateSnapshot.getDistanceOffPath();
		bearingDifferenceFromPath = navigationStateSnapshot.getBearingDifferenceFromPath();
		currentPoint = navigationStateSnapshot.getCurrentPoint();
		isOnPath = navigationStateSnapshot.isOnPath();
		offPathStartTime = navigationStateSnapshot.getOffPathStartTime();
		isSnapshot = true;
	}
	
	NavigationState snapshot() {
		return new NavigationState(this);
	}
	
	void update(Position position) throws InvalidObjectException {
		if (isSnapshot) {
			throw new InvalidObjectException("A NavigationState snapshot cannot be updated");
		} else {
			this.position = position;
			calculateLocationOnPath();
			calculateBearingOnPath();
		}
	}
	
	public Point getCurrentPoint() {
		return currentPoint;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public LatLng getLocation() {
		return position.location;
	}
	
	public double getBearing() {
		return position.bearing;
	}
	
	public long getTime() {
		return position.timestamp;
	}
	
	public LatLng getLocationOnPath() {
		return locationOnPath;
	}
	
	public double getBearingOnPath() {
		return bearingOnPath;
	}
	
	public double getDistanceOffPath() {
		return distanceOffPath;
	}
	
	public double getBearingDifferenceFromPath() {
		return bearingDifferenceFromPath;
	}
	
	public boolean isOnPath() {
		return isOnPath;
	}
	
	public void signalOnPath() {
		isOnPath = true;
	}
	
	public void signalOffPath() {
		offPathStartTime = position.timestamp;
		isOnPath = false;
	}
	
	public long getOffPathStartTime() {
		return offPathStartTime;
	}
	
	private void calculateLocationOnPath() {
		double bestDistanceOffPath = Double.MAX_VALUE;
		int bestIndex = 0;
		LatLng bestLocation = null;
		Point bestPoint = null;
		
		for (int i = Math.max(0, currentIndex - LOOK_BEHIND_POINTS);
				i <= Math.min(path.size() - 1, currentIndex + LOOK_AHEAD_POINTS);
				i++) {
			
			Point currentPoint = path.get(i);
			LatLng currentLocationOnPath = currentPoint.nextPoint == null ? currentPoint.location :
				LatLngUtil.closestLocationOnLine(currentPoint.location, currentPoint.nextPoint.location, position.location);
			double currentDistance = LatLngUtil.distanceInMeters(position.location, currentLocationOnPath);

			if (currentDistance < bestDistanceOffPath) {
				bestDistanceOffPath = currentDistance;
				bestIndex = i;
				bestPoint = currentPoint;
				bestLocation = currentLocationOnPath;
			}
		}
		
		distanceOffPath = bestDistanceOffPath;
		currentIndex = bestIndex;
		currentPoint = bestPoint;
		locationOnPath = bestLocation;
	}
	
	private void calculateBearingOnPath() {
		bearingOnPath = LatLngUtil.initialBearing(currentPoint.location, currentPoint.nextPoint.location);
		bearingDifferenceFromPath = Math.max(bearingOnPath, position.bearing) - Math.min(bearingOnPath, position.bearing);
	}
}
