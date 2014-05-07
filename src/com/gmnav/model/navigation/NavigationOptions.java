package com.gmnav.model.navigation;

import com.gmnav.model.map.NavigationMapOptions;

public class NavigationOptions {
	
	private VehicleOptions vehicleOptions = new VehicleOptions();
	private NavigationMapOptions mapOptions = new NavigationMapOptions();
	
	public NavigationOptions vehicleOptions(VehicleOptions options) {
		vehicleOptions = options;
		return this;
	}
	
	public VehicleOptions vehicleOptions() {
		return vehicleOptions;
	}
	
	public NavigationOptions mapOptions(NavigationMapOptions options) {
		mapOptions = options;
		return this;
	}
	
	public NavigationMapOptions mapOptions() {
		return mapOptions;
	}
}
