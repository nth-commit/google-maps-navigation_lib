package com.navidroidgms;

import com.navidroid.NavigationFragment;
import com.navidroidgms.model.directions.DirectionsFactory;
import com.navidroidgms.model.map.GoogleMapFactory;
import com.navidroidgms.model.vehiclemarker.VehicleMarkerFactory;
import com.navidroid.model.navigation.NavigationOptions;

public class NavigationFragmentFactory {
	
	private static DirectionsFactory directionsFactory;
	private static GoogleMapFactory googleMapFactory;
	private static VehicleMarkerFactory vehicleMarkerFactory;
	
	static {
		directionsFactory = new DirectionsFactory();
		googleMapFactory = new GoogleMapFactory();
		vehicleMarkerFactory = new VehicleMarkerFactory();
	}
	
	public static NavigationFragment createNavigationFragment() {
		return createNavigationFragment(new NavigationOptions());
	}
	
	public static NavigationFragment createNavigationFragment(NavigationOptions options) {
		return NavigationFragment.newInstance(directionsFactory, googleMapFactory, vehicleMarkerFactory, options);
	}
}
