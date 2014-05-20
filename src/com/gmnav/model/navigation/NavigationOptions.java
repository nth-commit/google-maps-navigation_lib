package com.gmnav.model.navigation;

import com.gmnav.model.positioning.GpsOptions;
import com.gmnav.model.vehicle.VehicleOptions;
import com.gmnav.model.directions.IDirectionsFactory;
import com.gmnav.model.map.MapOptions;

public class NavigationOptions {
	
	private VehicleOptions vehicleOptions = new VehicleOptions();
	private MapOptions mapOptions = new MapOptions();
	private GpsOptions gpsOptions = new GpsOptions();
	private IDirectionsFactory directionsFactory;
	
	public NavigationOptions vehicleOptions(VehicleOptions options) {
		vehicleOptions = options;
		return this;
	}
	
	public VehicleOptions vehicleOptions() {
		return vehicleOptions;
	}
	
	public NavigationOptions mapOptions(MapOptions options) {
		mapOptions = options;
		return this;
	}
	
	public MapOptions mapOptions() {
		return mapOptions;
	}
	
	public NavigationOptions gpsOptions(GpsOptions options) {
		gpsOptions = options;
		return this;
	}
	
	public GpsOptions gpsOptions() {
		return gpsOptions;
	}
	
	public NavigationOptions directionsFactory(IDirectionsFactory directionsFactory) {
		this.directionsFactory = directionsFactory;
		return this;
	}
	
	public IDirectionsFactory directionsFactory() {
		return directionsFactory;
	}
}
