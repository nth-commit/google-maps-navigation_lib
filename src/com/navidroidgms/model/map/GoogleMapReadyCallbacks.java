package com.navidroidgms.model.map;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.GoogleMap;
import com.navidroidgms.model.map.GoogleMapWrapper.WhenGoogleMapReadyCallback;

public class GoogleMapReadyCallbacks {
	
	private List<WhenGoogleMapReadyCallback> callbacks = new ArrayList<WhenGoogleMapReadyCallback>();
	private GoogleMap googleMap;
	
	public void signalMapReady(GoogleMap googleMap) {
		this.googleMap = googleMap;
		for (int i = 0; i < callbacks.size(); i++) {
			callbacks.get(i).invoke(googleMap);
		}
	}
	
	public void whenMapReady(WhenGoogleMapReadyCallback callback) {
		if (googleMap == null) {
			callbacks.add(callback);
		} else {
			callback.invoke(googleMap);
		}
	}
}
