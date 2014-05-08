package com.gmnav.model.vehicle;

import com.gmnav.model.map.NavigationMap;
import com.gmnav.model.map.NavigationMap.MapMode;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LatLngVehicleMarker implements IVehicleMarker {
	
	private Marker marker;
	
	public LatLngVehicleMarker(Vehicle vehicle, final NavigationMap map) {
		marker = map.getInnerMap().addMarker(new MarkerOptions()
				.position(vehicle.getLocation())
				.icon(BitmapDescriptorFactory.fromBitmap(vehicle.getImage()))
				.flat(true)
				.anchor(0.5f, 0.5f)
				.visible(false));
		
		map.getInnerMap().setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker candidateMarker) {
				// TODO: fix me!
				if (candidateMarker == marker) {
					map.setMapMode(MapMode.FOLLOW);
				}
				return false;
			}
		});
	}

	@Override
	public void show() {
		marker.setVisible(true);
	}

	@Override
	public void hide() {
		marker.setVisible(false);		
	}
	
	public void setLocation(LatLng location) {
		marker.setPosition(location);
	}
	
	public void setBearing(double bearing) {
		marker.setRotation((float)bearing);
	}
}
