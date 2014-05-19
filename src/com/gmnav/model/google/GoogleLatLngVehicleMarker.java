package com.gmnav.model.google;

import com.gmnav.model.LatLng;
import com.gmnav.model.map.IMap;
import com.gmnav.model.map.NavigationMap;
import com.gmnav.model.map.NavigationMap.MapMode;
import com.gmnav.model.vehicle.ILatLngVehicleMarker;
import com.gmnav.model.vehicle.IVehicleMarker;
import com.gmnav.model.vehicle.Vehicle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleLatLngVehicleMarker implements ILatLngVehicleMarker {
	
	private Marker marker;
	
	public GoogleLatLngVehicleMarker(Vehicle vehicle, final NavigationMap navigationMap) {
		GoogleMap map = (GoogleMap)((GoogleMapWrapper)navigationMap.getMap()).getGoogleMap();
		
		marker = map.addMarker(new MarkerOptions()
				.position(Util.toGoogleLatLng(vehicle.getLocation()))
				.icon(BitmapDescriptorFactory.fromBitmap(vehicle.getImage()))
				.flat(true)
				.anchor(0.5f, 0.5f)
				.visible(false));

		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker candidateMarker) {
				// TODO: fix me!
				if (candidateMarker == marker) {
					//map.setMapMode(MapMode.FOLLOW);
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
	
	@Override
	public void setLocation(LatLng location) {
		marker.setPosition(Util.toGoogleLatLng(location));
	}
	
	@Override
	public void setBearing(double bearing) {
		marker.setRotation((float)bearing);
	}
}
