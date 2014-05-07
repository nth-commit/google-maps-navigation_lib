package com.gmnav.model.directions;

import com.google.android.gms.maps.model.LatLng;

public class Point {
	public LatLng location;
	public double distanceToNextPoint;
	public double timeToNextPoint;
	public double distanceToCurrentDirectionMeters;
	public double timeToCurrentDirectionMinutes;
	public double distanceToNextDirectionMeters;
	public double timeToNextDirectionMinutes;
	public double distanceToArrivalMeters;
	public double timeToArrivalMinutes;
	public Direction direction;
	public Direction nextDirection;
	public Point nextPoint;
}
