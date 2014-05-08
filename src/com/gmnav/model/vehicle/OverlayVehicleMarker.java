package com.gmnav.model.vehicle;

import com.gmnav.model.map.NavigationMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;

public class OverlayVehicleMarker implements IVehicleMarker {
	
	public OverlayVehicleMarker(Vehicle vehicle, NavigationMap map) {
		map.getInnerMap().setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				setTilt(position.tilt);
			}
		});
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
	
	private void setTilt(float tilt) {
		
	}
}
