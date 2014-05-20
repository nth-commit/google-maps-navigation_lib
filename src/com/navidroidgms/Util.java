package com.navidroidgms;

public class Util {
	
	public static com.google.android.gms.maps.model.LatLng toGoogleLatLng(com.navidroid.model.LatLng navLatLng) {
		return new com.google.android.gms.maps.model.LatLng(navLatLng.latitude, navLatLng.longitude);
	}
	
	public static com.navidroid.model.LatLng fromGoogleLatLng(com.google.android.gms.maps.model.LatLng googleLatLng) {
		return new com.navidroid.model.LatLng(googleLatLng.latitude, googleLatLng.longitude);
	}

}
