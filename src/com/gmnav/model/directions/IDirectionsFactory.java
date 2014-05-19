package com.gmnav.model.directions;

import com.gmnav.model.LatLng;

public interface IDirectionsFactory {
	
	String createRequestUrl(LatLng origin, LatLng destination);
	
	Directions createDirections(LatLng origin, LatLng destination, String response) throws Exception;

}
