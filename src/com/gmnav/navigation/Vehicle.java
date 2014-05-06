package com.gmnav.navigation;

import android.graphics.Bitmap;

import com.gmnav.map.NavigationMap;
import com.gmnav.positioning.Position;
import com.gmnav.util.PointD;
import com.google.android.gms.maps.model.Marker;

public class Vehicle {
	
	private Position position;
	private Bitmap image;
	private PointD anchor;
	private Marker marker;
	private NavigationMap map;
		
	public Vehicle(NavigationMap map, VehicleOptions options) {
		this.map = map;
		position = options.position();
		image = options.image();
		anchor = options.anchor();
		marker = map.addVehicleMarker(this);
	}
	
	public void setPosition(Position position) {
		this.position = position;
		marker.setRotation((float)position.bearing);
		marker.setPosition(position.location);
		map.setVehiclePosition(position);
	}
	
	public Position getPosition() {
		return position;
	}
	
	public Bitmap getImage() {
		return image;
	}
	
	public PointD getAnchor() {
		return anchor;
	}
}
