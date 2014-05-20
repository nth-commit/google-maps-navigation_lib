package com.navidroidgms.model.map;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class CameraPositionFactory {
	
	public CameraPosition newCameraPosition(CameraPosition pos, LatLng location) {
		return newCameraPosition(location, pos.zoom, pos.tilt, pos.bearing);
	}
	
	public CameraPosition newCameraPositionFromZoom(CameraPosition pos, float zoom) {
		return newCameraPosition(pos.target, zoom, pos.tilt, pos.bearing);
	}
	
	public CameraPosition newCameraPositionFromTilt(CameraPosition pos, float tilt) {
		return newCameraPosition(pos.target, pos.zoom, tilt, pos.bearing);
	}
	
	public CameraPosition newCameraPositionFromBearing(CameraPosition pos, float bearing) {
		return newCameraPosition(pos.target, pos.zoom, pos.tilt, bearing);
	}
	
	public CameraPosition newCameraPosition(LatLng target, float zoom, float tilt, float bearing) {
		return new CameraPosition(target, zoom, tilt, bearing);
	}
}
