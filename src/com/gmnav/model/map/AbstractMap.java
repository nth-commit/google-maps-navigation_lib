package com.gmnav.model.map;

import com.gmnav.model.LatLng;
import com.gmnav.model.PointD;

public abstract class AbstractMap implements IMap {
	
	protected LatLng location;
	protected double bearing;
	protected double tilt;
	protected double zoom;
	protected PointD anchor;

	@Override
	public void setLocation(LatLng location) {
		this.location = location;		
	}

	@Override
	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	@Override
	public void setTilt(double tilt) {
		this.tilt = tilt;
	}

	@Override
	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	@Override
	public void setAnchor(PointD anchor) {
		this.anchor = anchor;
	}

	@Override
	public LatLng getLocation() {
		return location;
	}

	@Override
	public double getBearing() {
		return bearing;
	}

	@Override
	public double getTilt() {
		return tilt;
	}

	@Override
	public double getZoom() {
		return zoom;
	}

	@Override
	public PointD getAnchor() {
		return anchor;
	}
}
