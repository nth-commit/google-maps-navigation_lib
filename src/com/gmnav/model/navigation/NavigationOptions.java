package com.gmnav.model.navigation;

import com.gmnav.model.map.NavigationMapOptions;
import com.gmnav.model.positioning.GpsOptions;
import com.gmnav.model.vehicle.VehicleOptions;

public class NavigationOptions {
	
	private VehicleOptions vehicleOptions = new VehicleOptions();
	private NavigationMapOptions mapOptions = new NavigationMapOptions();
	private GpsOptions gpsOptions = new GpsOptions();
	
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
	
	public NavigationOptions gpsOptions(GpsOptions options) {
		gpsOptions = options;
		return this;
	}
	
	public GpsOptions gpsOptions() {
		return gpsOptions;
	}
}
