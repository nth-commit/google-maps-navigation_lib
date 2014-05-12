package com.gmnav.model.navigation;

import com.gmnav.model.directions.Directions;

public interface INavigatorStateListener {
	
	void OnNewPathFound(Directions directions);
	
	void OnDeparture(NavigationState state);
	
	void OnArrival(NavigationState state);
	
	void OnVehicleOffPath(NavigationState state);
	
	void OnNewDirection(NavigationState state);
	
	void OnNavigatorTick(NavigationState state);
}
