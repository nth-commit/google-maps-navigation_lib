package com.gmnav.model.directions;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class Direction {
	
	public int index;
	public String text;
	public List<LatLng> path;
	
	public Direction(int index, String text, List<LatLng> path) {
		this.index = index;
		this.text = text;
		this.path = path;
	}
}
