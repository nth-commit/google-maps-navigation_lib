package com.gmnav.model.vehicle;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.gmnav.model.map.NavigationMap;
import com.gmnav.model.map.NavigationMap.MapMode;
import com.gmnav.model.map.NavigationMap.OnMapModeChangedListener;
import com.gmnav.model.positioning.Position;
import com.gmnav.model.util.AsyncTaskExecutor;
import com.gmnav.model.util.LatLngUtil;
import com.gmnav.model.util.ListUtil;
import com.gmnav.model.util.ListUtil.Predicate;
import com.gmnav.model.util.MathUtil;
import com.gmnav.model.util.PointD;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Vehicle {
	
	private static final int TARGET_FRAMES_PER_S = 20;
	private static final int MS_PER_FRAME = 1000 / TARGET_FRAMES_PER_S;
	private static final int GPS_DELAY_MS = 2000;
	
	private Bitmap image;
	private PointD anchor;
	private NavigationMap map;
	
	private List<Position> targetPositions;
	private LatLng location;
	private double bearing;
	
	private LatLngVehicleMarker latLngMarker;
	private OverlayVehicleMarker overlayMarker;
	
	private Object targetPositionsLock = new Object();
		
	public Vehicle(NavigationMap map, VehicleOptions options) {
		this.map = map;
		location = options.location();
		image = options.image();
		anchor = options.anchor();
		targetPositions = new ArrayList<Position>();
		
		latLngMarker = new LatLngVehicleMarker(this, map);
		overlayMarker = new OverlayVehicleMarker(this, map);
		listenForMapModeChange();
		onMapModeChanged(map.getMapMode());
		
		startUpdateTask();
	}
	
	private void startUpdateTask() {
		AsyncTask<Void, Void, Void> vehicleUpdateTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				while (true) {
					calculateLocation();
					calculateBearing();
					publishProgress();					
					try {
						Thread.sleep(MS_PER_FRAME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			protected void onProgressUpdate(Void... values) {
				map.setVehiclePosition(location, bearing);
				latLngMarker.setBearing(bearing);
				latLngMarker.setLocation(location);
			}
			
		};
		AsyncTaskExecutor.execute(vehicleUpdateTask);
	}
	
	private void listenForMapModeChange() {
		map.setOnMapModeChangedListener(new OnMapModeChangedListener() {
			@Override
			public void invoke(MapMode mode) {
				onMapModeChanged(mode);			
			}
		});
	}
	
	private void onMapModeChanged(MapMode mode) {
		if (mode == MapMode.FOLLOW) {
			latLngMarker.hide();
			overlayMarker.show();
		} else {
			overlayMarker.hide();
			latLngMarker.show();
		}
	}
	
	public void setPosition(Position position) {
		synchronized (targetPositionsLock) {
			targetPositions.add(position);
		}
	}
	
	private void calculateLocation() {
		Position a = null;
		Position b = null;
		final long currentDelayedTime = System.currentTimeMillis() - GPS_DELAY_MS;
		synchronized (targetPositionsLock) {
			targetPositions = ListUtil.removeAll(targetPositions, new Predicate<Position>() {
				@Override
				public boolean check(Position currentPosition, int index) {
					Position nextPosition = null;
					if (targetPositions.size() > index + 2) {
						nextPosition = targetPositions.get(index + 1);
					}
					return currentPosition.timestamp > currentDelayedTime ||
						nextPosition == null ||
						nextPosition.timestamp > currentDelayedTime;
				}
			});
			
			if (targetPositions.size() > 0) {
				a = targetPositions.get(0);
			}
			if (targetPositions.size() > 1) {
				b = targetPositions.get(1);
			}
		}
		
		if (a != null) {
			if (b == null) {
				location = a.location;
			} else {
				double deltaDist = LatLngUtil.distanceInMeters(a.location, b.location);
				double deltaTime = b.timestamp - a.timestamp;
				double timeFromA = currentDelayedTime - a.timestamp;
				double interpolationFactor = MathUtil.clamp(timeFromA / deltaTime, 0, 1);
				double distFromA = deltaDist * interpolationFactor;
				location = LatLngUtil.travel(a.location, a.bearing, distFromA);
			}
		}
	}
	
	private void calculateBearing() {
		synchronized (targetPositions) {
			if (targetPositions.size() > 0) {
				bearing = targetPositions.get(0).bearing;
			}
		}
	}
	
	public LatLng getLocation() {
		return location;
	}
	
	public Bitmap getImage() {
		return image;
	}
	
	public PointD getAnchor() {
		return anchor;
	}
}
