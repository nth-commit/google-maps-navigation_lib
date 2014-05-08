package com.gmnav.model.navigation;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.gmnav.model.map.NavigationMap;
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
	private static final int GPS_DELAY_MS = 500;
	
	private LatLng location;
	private Bitmap image;
	private PointD anchor;
	private Marker marker;
	private NavigationMap map;
	
	private List<Position> targetPositions;
	private LatLng currentLocation;
	private double currentBearing;
	
	private Object targetPositionsLock = new Object();
		
	public Vehicle(NavigationMap map, VehicleOptions options) {
		this.map = map;
		location = options.location();
		image = options.image();
		anchor = options.anchor();
		marker = map.addVehicleMarker(this);
		targetPositions = new ArrayList<Position>();
		startUpdateTask();
	}
	
	private void startUpdateTask() {
		AsyncTask<Void, Void, Void> vehicleUpdateTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				while (true) {
					calculateCurrentLocation();
					calculateCurrentBearing();
					publishProgress();					
					try {
						Thread.sleep(MS_PER_FRAME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			@Override
			protected void onProgressUpdate(Void... values) {
				map.setVehiclePosition(currentLocation, currentBearing);
				marker.setRotation((float)currentBearing);
				marker.setPosition(currentLocation);
			}
			
		};
		AsyncTaskExecutor.execute(vehicleUpdateTask);
	}
	
	public void setPosition(Position position) {
		synchronized (targetPositionsLock) {
			targetPositions.add(position);
		}
	}
	
	private void calculateCurrentLocation() {
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
						nextPosition != null && nextPosition.timestamp > currentDelayedTime;
				}
			});
			
			if (targetPositions.size() > 0) {
				a = targetPositions.get(0);
			}
			if (targetPositions.size() > 1) {
				b = targetPositions.get(1);
			}
		}
		
		if (a == null) {
			currentLocation = location;
		} else if (b == null) {
			currentLocation = a.location;
		} else {
			double deltaDist = LatLngUtil.distanceInMeters(a.location, b.location);
			double deltaTime = b.timestamp - a.timestamp;
			double timeFromA = currentDelayedTime - a.timestamp;
			double interpolationFactor = MathUtil.clamp(timeFromA / deltaTime, 0, 1);
			double distFromA = deltaDist * interpolationFactor;
			currentLocation = LatLngUtil.travel(a.location, a.bearing, distFromA);
		}
	}
	
	private void calculateCurrentBearing() {
		Position a = null;
		synchronized (targetPositions) {
			if (targetPositions.size() > 0) {
				a = targetPositions.get(0);
			}
		}
		currentBearing = a == null ? 0 : a.bearing;
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
