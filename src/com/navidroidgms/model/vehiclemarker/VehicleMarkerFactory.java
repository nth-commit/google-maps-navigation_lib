package com.navidroidgms.model.vehiclemarker;

import com.navidroid.model.map.NavigationMap;
import com.navidroid.model.vehicle.IVehicleMarker;
import com.navidroid.model.vehicle.IVehicleMarkerFactory;
import com.navidroid.model.vehicle.Vehicle;

public class VehicleMarkerFactory implements IVehicleMarkerFactory {

	@Override
	public IVehicleMarker createVehicleMarker(Vehicle vehicle, NavigationMap navigationMap) {
		return new VehicleMarker(vehicle, navigationMap);
	}

}
