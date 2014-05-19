package com.gmnav.model.navigation;

import com.gmnav.model.directions.Directions;
import com.google.android.gms.maps.model.LatLng;

public interface INavigatorStateListener {
	
	void OnNewPathFoundFailed(String message, LatLng origin, LatLng destination);
	
	void OnNewPathFound(Directions directions, LatLng origin, LatLng destination);
	
	void OnDeparture(NavigationState state);
	
	void OnArrival(NavigationState state);
	
	void OnVehicleOffPath(NavigationState state);
	
	void OnNewDirection(NavigationState state);
	
	void OnNavigatorTick(NavigationState state);
}
