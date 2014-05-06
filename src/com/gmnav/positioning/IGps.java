package com.gmnav.positioning;

import com.google.android.gms.maps.model.LatLng;

public interface IGps {
	
	public interface OnTickHandler {
		void invoke(GpsPosition position);
	}
	
	void enableTracking();
	
	void disableTracking();
	
	void forceTick();
	
	void onTick(OnTickHandler handler);
	
	LatLng getLastLocation();
}
