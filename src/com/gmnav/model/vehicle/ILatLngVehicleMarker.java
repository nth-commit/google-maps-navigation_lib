package com.gmnav.model.vehicle;

import com.gmnav.model.LatLng;

public interface ILatLngVehicleMarker extends IVehicleMarker {
	void setLocation(LatLng location);
	void setBearing(double bearing);
}
