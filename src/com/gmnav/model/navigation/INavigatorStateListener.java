package com.gmnav.model.navigation;

import com.gmnav.model.directions.Direction;

public interface INavigatorStateListener {
	
	void OnDeparture();
	
	void OnArrival();
	
	void OnVehicleOffPath();
	
	void OnNewDirection(Direction direction);
	
	void OnNewPathFound();
	
	void OnNavigatorTick(NavigationState state);
}
