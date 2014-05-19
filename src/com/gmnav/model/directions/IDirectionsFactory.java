package com.gmnav.model.directions;

import com.google.android.gms.maps.model.LatLng;

public interface IDirectionsFactory {
	
	String createRequestUrl(LatLng origin, LatLng destination);
	
	Directions createDirections(LatLng origin, LatLng destination, String response) throws Exception;

}
