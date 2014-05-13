package com.gmnav.model.positioning;

import com.gmnav.Defaults;
import com.gmnav.model.positioning.GpsOptions.GpsType;
import com.google.android.gms.location.LocationClient;

public class GpsFactory {
	
	public static IGps create(GpsOptions options) {
		return create(options, null);
	}
	
	public static IGps create(GpsOptions options, LocationClient locationClient) {
		if (options.gpsType() == GpsType.SIMULATED) {
			if (options.debugMode()) {
				return new DebugSimulatedGps(options, Defaults.LOCATION);
			} else {
				return new SimulatedGps(options, Defaults.LOCATION);
			}
		} else {
			return new Gps(options, locationClient);
		}
	}
}
