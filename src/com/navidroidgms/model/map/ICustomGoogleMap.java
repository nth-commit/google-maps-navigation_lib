package com.navidroidgms.model.map;

import android.view.View;

import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.navidroid.model.map.IMap;

public interface ICustomGoogleMap extends IMap {
	
	public interface OnMapLongClick {
		void invoke(LatLng location);
	}
	
	public interface OnMarkerCreated {
		public void invoke(Marker marker);
	}
	
	public void addMarker(MarkerOptions options, OnMarkerCreated onMarkerCreated);
	
	public void setOnMarkerClickListener(OnMarkerClickListener listener);
	
	public void addView(View view);
	
	public void setPadding(int left, int top, int right, int bottom);
	
	public void setOnMapLongClick(OnMapLongClick handler);
}
