package com.gmnav.directions;

import com.google.android.gms.maps.model.LatLng;

public class Point {
	public LatLng location;
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
