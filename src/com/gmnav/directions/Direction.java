package com.gmnav.directions;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class Direction {
	
	public String text;
	public List<LatLng> path;
	
	public Direction(String text, List<LatLng> path) {
		this.text = text;
		this.path = path;
	}
}
