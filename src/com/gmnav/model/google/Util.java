package com.gmnav.model.google;

public class Util {
	
	public static com.google.android.gms.maps.model.LatLng toGoogleLatLng(com.gmnav.model.LatLng navLatLng) {
		return new com.google.android.gms.maps.model.LatLng(navLatLng.latitude, navLatLng.longitude);
	}
	
	public static com.gmnav.model.LatLng fromGoogleLatLng(com.google.android.gms.maps.model.LatLng googleLatLng) {
		return new com.gmnav.model.LatLng(googleLatLng.latitude, googleLatLng.longitude);
	}

}
