package com.gmnav.model.vehicle;

import java.util.ArrayList;
import java.util.ListIterator;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.gmnav.NavigationFragment;
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

public class Vehicle {
	
	private static final int TARGET_FRAMES_PER_S = 20;
	private static final int MS_PER_FRAME = 1000 / TARGET_FRAMES_PER_S;
	private static final int GPS_DELAY_MS = 2000;
	
	private Bitmap image;
	private PointD anchor;
	private NavigationMap map;
	
	private ArrayList<Position> targetPositions;
	private LatLng location;
	private double bearing;
	
	private LatLngVehicleMarker latLngMarker;
	private StaticVehicleMarker overlayMarker;
	
	private Object targetPositionsLock = new Object();
		
	public Vehicle(NavigationFragment navigationFragment, NavigationMap map, VehicleOptions options) {
		this.map = map;
		location = options.location();
		image = options.image();
		anchor = options.anchor();
		targetPositions = new ArrayList<Position>();
		
		latLngMarker = new LatLngVehicleMarker(this, map);
		overlayMarker = new StaticVehicleMarker(navigationFragment, this, map);
		listenForMapModeChange();
		onMapModeChanged(map.getMapMode());
		
		startUpdateTask();
	}
	
	private void startUpdateTask() {
		AsyncTask<Void, Void, Void> vehicleUpdateTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... arg0) {
				while (true) {
					long timeDelayed;
					Position[] positions;
					synchronized (targetPositionsLock) {
						timeDelayed = System.currentTimeMillis() - GPS_DELAY_MS;
						positions = getPositionsAroundTime(timeDelayed);
					}
					
					calculateLocation(timeDelayed, positions[0], positions[1]);
					calculateBearing(timeDelayed, positions[0], positions[1]);
					publishProgress();
					try {
						Thread.sleep(MS_PER_FRAME); // TODO: adjust sleep time based on real processing time.
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
	
	private Position[] getPositionsAroundTime(final long time) {
		Position[] currentPositions = new Position[2];
		synchronized (targetPositionsLock) {
			int startIndex = ListUtil.lastIndexOf(targetPositions, new Predicate<Position>() {
				@Override
				public boolean check(Position item, int index) {
					return item.timestamp <= time;
				}
			});
			
			if (targetPositions.size() > startIndex && startIndex > -1) {
				currentPositions[0] = targetPositions.get(startIndex);
				currentPositions[1] = ListUtil.find(targetPositions, new Predicate<Position>() {
					@Override
					public boolean check(Position item, int index) {
						return item.timestamp > time;
					}
				});
			}
			
			for (int i = 0; i < startIndex && i < targetPositions.size(); i++) {
				targetPositions.remove(i);
			}
			targetPositions.trimToSize();
		}
		return currentPositions;
	}
	
	private void calculateLocation(long time, Position a, Position b) {
		if (a != null) {
			if (b == null) {
				location = a.location;
			} else {
				double deltaDist = LatLngUtil.distanceInMeters(a.location, b.location);
				double deltaTime = b.timestamp - a.timestamp;
				double timeFromA = time - a.timestamp;
				double interpolationFactor = MathUtil.clamp(timeFromA / deltaTime, 0, 1);
				double distFromA = deltaDist * interpolationFactor;
				location = LatLngUtil.travel(a.location, a.bearing, distFromA);
			}
		}
	}
	
	private void calculateBearing(long time, Position a, Position b) {
		if (a != null) {
			bearing = a.bearing;
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
