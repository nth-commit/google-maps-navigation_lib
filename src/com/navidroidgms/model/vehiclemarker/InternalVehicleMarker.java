package com.navidroidgms.model.vehiclemarker;

import android.os.Handler;

import com.navidroid.model.LatLng;
import com.navidroid.model.WhenReadyWrapper;
import com.navidroid.model.map.NavigationMap;
import com.navidroid.model.vehicle.IVehicleMarker;
import com.navidroid.model.vehicle.Vehicle;
import com.navidroidgms.Util;
import com.navidroidgms.model.map.CustomGoogleMap;
import com.navidroidgms.model.map.ICustomGoogleMap.OnMarkerCreated;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class InternalVehicleMarker implements IVehicleMarker {
	
	// Animation gets cancelled if we start straight away, due to internal GestureDetector.onSingleTapConfirmed listener.
	private final static int FOLLOW_VEHICLE_DELAY_MS = 500;
	
	private Marker marker;
	private CustomGoogleMap map;
	private Handler handler;
	
	public InternalVehicleMarker(final Vehicle vehicle, final NavigationMap navigationMap, final VehicleMarker wrapper) {
		handler = new Handler();
		map = (CustomGoogleMap)navigationMap.getMap();
		
		MarkerOptions options = new MarkerOptions()
			.position(Util.toGoogleLatLng(vehicle.getLocation()))
			.flat(true)
			.anchor(0.5f, 0.5f)
			.visible(false);
		
		final InternalVehicleMarker object = this;
		map.addMarker(options, new OnMarkerCreated() {
			@Override
			public void invoke(Marker createdMarker) {
				marker = createdMarker;
				marker.setIcon(BitmapDescriptorFactory.fromBitmap(vehicle.getImage()));
				wrapper.setInnerObject(object);
			}
		});
		
		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker candidateMarker) {
				if (candidateMarker.equals(marker)) {
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							navigationMap.followVehicle();
						}
					}, FOLLOW_VEHICLE_DELAY_MS);
					return true;
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
	public void setLocation(final LatLng location) {
		marker.setPosition(Util.toGoogleLatLng(location));
	}
	
	@Override
	public void setBearing(final double bearing) {
		marker.setRotation((float)bearing);
	}
}
