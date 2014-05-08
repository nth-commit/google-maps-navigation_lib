package com.gmnav.model.navigation;

import android.graphics.Bitmap;

import com.gmnav.Defaults;
import com.gmnav.model.positioning.Position;
import com.gmnav.model.util.PointD;
import com.google.android.gms.maps.model.LatLng;

public class VehicleOptions {
	private LatLng location = Defaults.LOCATION;
	private Bitmap image;
	private PointD anchor = new PointD(0.5d, 0.8d);
	
	public VehicleOptions location(LatLng location) {
		this.location = location;
		return this;
	}
	
	public LatLng location() {
		return location;
	}
	
	public VehicleOptions image(Bitmap image) {
		this.image = image;
		return this;
	}
	
	public Bitmap image() {
		return image;
	}
	
	public VehicleOptions anchor(PointD anchor) {
		this.anchor = anchor;
		return this;
	}
	
	public PointD anchor() {
		return anchor;
	}
}