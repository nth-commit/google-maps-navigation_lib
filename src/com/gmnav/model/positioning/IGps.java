package com.gmnav.model.positioning;

import com.google.android.gms.maps.model.LatLng;

public interface IGps {
	
	public interface OnTickHandler {
		void invoke(Position position);
	}
	
	void enableTracking();
	
	void disableTracking();
	
	void forceTick();
	
	void setOnTickHandler(OnTickHandler handler);
	
	LatLng getLastLocation();
}
