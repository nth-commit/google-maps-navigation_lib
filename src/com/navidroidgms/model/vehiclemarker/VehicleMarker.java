package com.navidroidgms.model.vehiclemarker;

import com.navidroid.model.LatLng;
import com.navidroid.model.WhenReadyWrapper;
import com.navidroid.model.vehicle.IVehicleMarker;

public class VehicleMarker extends WhenReadyWrapper<IVehicleMarker> implements IVehicleMarker {

	@Override
	public void show() {
		whenReady(new WhenReady<IVehicleMarker>() {
			@Override
			public void invoke(IVehicleMarker object) {
				object.show();
			}
		});
	}

	@Override
	public void hide() {
		whenReady(new WhenReady<IVehicleMarker>() {
			@Override
			public void invoke(IVehicleMarker object) {
				object.hide();
			}
		});
	}

	@Override
	public void setLocation(final LatLng location) {
		whenReady(new WhenReady<IVehicleMarker>() {
			@Override
			public void invoke(IVehicleMarker object) {
				object.setLocation(location);
			}
		});
	}

	@Override
	public void setBearing(final double bearing) {
		whenReady(new WhenReady<IVehicleMarker>() {
			@Override
			public void invoke(IVehicleMarker object) {
				object.setBearing(bearing);
			}
		});
	}
}
