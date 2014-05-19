package com.gmnav.model.vehicle;

import com.gmnav.model.map.NavigationMap;

public interface ILatLngVehicleMarkerFactory {
	
	ILatLngVehicleMarker createLatLngVehicleMarker(Vehicle vehicle, NavigationMap navigationMap);

}
