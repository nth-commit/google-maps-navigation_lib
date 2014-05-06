package com.gmnav.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class CameraPositionFactory {
	
	private GoogleMap map;
	
	public CameraPositionFactory(GoogleMap map) {
		this.map = map;
	}

	public CameraPosition newCameraPosition(LatLng location) {
		CameraPosition pos = map.getCameraPosition();
		return newCameraPosition(location, pos.zoom, pos.tilt, pos.bearing);
	}
	
	public CameraPosition newCameraPosition(float zoom) {
		CameraPosition pos = map.getCameraPosition();
		return newCameraPosition(pos.target, zoom, pos.tilt, pos.bearing);
	}
	
	public CameraPosition newCameraPosition(LatLng location, float bearing) {
		CameraPosition pos = map.getCameraPosition();
		return newCameraPosition(location, pos.zoom, pos.tilt, bearing);
	}
	
	public CameraPosition newCameraPosition(float zoom, float tilt, float bearing) {
		return newCameraPosition(map.getCameraPosition().target, zoom, tilt, bearing);
	}
	
	public CameraPosition newCameraPosition(LatLng target, float zoom, float tilt, float bearing) {
		return new CameraPosition(target, zoom, tilt, bearing);
	}
}
