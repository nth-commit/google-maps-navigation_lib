package com.gmnav.map;

import com.gmnav.Defaults;
import com.gmnav.util.PointD;
import com.google.android.gms.maps.model.LatLng;

public class NavigationMapOptions {
	
	private LatLng location = Defaults.LOCATION;
	private PointD anchor = new PointD(0.5, 0.5);
	
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
