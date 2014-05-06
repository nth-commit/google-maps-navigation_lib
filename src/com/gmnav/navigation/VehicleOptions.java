package com.gmnav.navigation;

import android.graphics.Bitmap;

import com.gmnav.Defaults;
import com.gmnav.positioning.Position;
import com.gmnav.util.PointD;

public class VehicleOptions {
	private Position position = new Position(Defaults.LOCATION, 0);
	private Bitmap image;
	private PointD anchor = new PointD(0.5d, 0.8d);
	
	public VehicleOptions position(Position position) {
		this.position = position;
		return this;
	}
	
	public Position position() {
		return position;
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