package com.navidroidgms.model.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.navidroid.model.map.IMap;

public interface ICustomGoogleMap extends IMap {
	
	public interface OnMarkerCreated {
		public void invoke(Marker marker);
	}
	
	public GoogleMap getGoogleMap();
	
	public void addMarker(MarkerOptions options, OnMarkerCreated onMarkerCreated);
	
	public void setOnMarkerClickListener(OnMarkerClickListener listener);
}
