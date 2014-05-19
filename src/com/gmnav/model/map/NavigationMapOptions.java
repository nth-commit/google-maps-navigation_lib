package com.gmnav.model.map;

import com.gmnav.Defaults;
import com.gmnav.model.LatLng;
import com.gmnav.model.PointD;

public class NavigationMapOptions {
	
	private LatLng location = Defaults.LOCATION;
	private PointD anchor = new PointD(0.5, 0.75); // TODO: get from VehicleOptions
	
	public LatLng location() {
		return location;
	}
	
	public NavigationMapOptions location(LatLng location) {
		this.location = location;
		return this;
	}
	
	public PointD anchor() {
		return this.anchor;
	}
	
	public NavigationMapOptions anchor(PointD anchor) {
		this.anchor = anchor;
		return this;
	}
}
