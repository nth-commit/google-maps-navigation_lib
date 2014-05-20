package com.navidroidgms.model.map;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.navidroidgms.Util;

public class CameraPositionUtil {
	
	public static CameraPosition newCameraPosition(CameraPosition pos, LatLng location) {
		return newCameraPosition(location, pos.zoom, pos.tilt, pos.bearing);
	}
	
	public static CameraPosition newCameraPositionFromZoom(CameraPosition pos, float zoom) {
		return newCameraPosition(pos.target, zoom, pos.tilt, pos.bearing);
	}
	
	public static CameraPosition newCameraPositionFromTilt(CameraPosition pos, float tilt) {
		return newCameraPosition(pos.target, pos.zoom, tilt, pos.bearing);
	}
	
	public static CameraPosition newCameraPositionFromBearing(CameraPosition pos, float bearing) {
		return newCameraPosition(pos.target, pos.zoom, pos.tilt, bearing);
	}
	
	public static CameraPosition newCameraPosition(LatLng target, float zoom, float tilt, float bearing) {
		return new CameraPosition(target, zoom, tilt, bearing);
	}
	
	public static boolean equals(CameraPosition a, CameraPosition b) {
		return a.zoom == b.zoom &&
				a.tilt == b.tilt &&
				a.bearing == b.bearing &&
				Util.fromGoogleLatLng(a.target).equals(Util.fromGoogleLatLng(b.target), 0.000001);
	}
}
