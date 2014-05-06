package com.gmnav.navigation;

import java.io.InvalidObjectException;

import com.gmnav.directions.Direction;
import com.gmnav.directions.Directions;
import com.gmnav.directions.Point;
import com.gmnav.directions.Route;
import com.gmnav.directions.Route.DirectionsRetrieved;
import com.gmnav.map.NavigationMap;
import com.gmnav.map.NavigationMap.MapMode;
import com.gmnav.positioning.AbstractSimulatedGps;
import com.gmnav.positioning.GpsPosition;
import com.gmnav.positioning.IGps;
import com.gmnav.positioning.Position;
import com.gmnav.positioning.IGps.OnTickHandler;
import com.gmnav.util.LatLngUtil;
import com.google.android.gms.maps.model.LatLng;
import android.util.Log;

public class Navigator {
	
	private final int MIN_ARRIVAL_DIST_METERS = 10;
	private final int OFF_PATH_TOLERANCE_METERS = 10;
	private final int OFF_PATH_TOLERANCE_BEARING = 45;
	private final int MAX_TIME_OFF_PATH_MS = 5000;
	
	private NavigationMap map;
	private Vehicle vehicle;
	private IGps gps;
	private NavigatorEvents events;
	private Position idlePosition;
	private NavigationState navigationState;
	private NavigationState lastNavigationState;
	private LatLng destination;
	
	public Navigator(final IGps gps, NavigationMap map, VehicleOptions vehicleMarkerOptions, NavigatorEvents events) {
		this.gps = gps;
		this.map = map;
		this.vehicle = new Vehicle(map,
				vehicleMarkerOptions.position(new Position(gps.getLastLocation(), 0)));
		this.events = events;
		listenToGps();
	}
	
	private void listenToGps() {
		gps.onTick(new OnTickHandler() {
			@Override
			public void invoke(GpsPosition position) {
				onGpsTick(position);
			}
		});
		gps.enableTracking();
		gps.forceTick();
	}

	public void navigateTo(final LatLng location) {
		Route request = new Route(idlePosition.location, location);
		request.getDirections(new DirectionsRetrieved() {
			@Override
			public void invoke(Directions directions) {
				destination = location;
				startNavigation(directions);
			}
		});
	}
	
	public boolean isNavigating() {
		return navigationState != null;
	}
	
	private void startNavigation(Directions directions) {
		if (!isNavigating()) {
			navigationState = new NavigationState(directions);
			map.addPathPolyline(directions.getLatLngPath());
			map.setMapMode(MapMode.NAVIGATING);
			
			if (gps instanceof AbstractSimulatedGps) {
				((AbstractSimulatedGps)gps).followPath(directions.getLatLngPath());
			}
		}
	}
	
	private void onGpsTick(GpsPosition position) {
		if (isNavigating()) {
			try {
				navigationState.update(position);
			} catch (InvalidObjectException e) {
				e.printStackTrace();
				Log.e("Fatal exception in Navigator", e.getMessage());
			}
			checkArrival();
			checkDirectionChanged();
			checkOffPath();
			updateVehicleMarker();
			lastNavigationState = navigationState.snapshot();
		} else {
			idlePosition = position;
		}
	}
	
	private void checkArrival() {
		if (LatLngUtil.distanceInMeters(navigationState.getLocation(), destination) <= MIN_ARRIVAL_DIST_METERS) {
			endNavigation();
			events.OnArrival();
		}
	}
	
	private void checkDirectionChanged() {
		Point currentPoint = navigationState.getCurrentPoint();
		if (lastNavigationState != null) { 
			Point lastPoint = lastNavigationState.getCurrentPoint();
			if (currentPoint != lastPoint) {
				Direction currentDirection = currentPoint.nextDirection;
				if (currentDirection != lastPoint.nextDirection) {
					events.OnNewDirection(currentDirection);
				}
			}
		}
	}
	
	private void checkOffPath() {
		if (navigationState.getDistanceOffPath() > OFF_PATH_TOLERANCE_METERS ||
				navigationState.getBearingDifferenceFromPath() > OFF_PATH_TOLERANCE_BEARING) {
			
			if (lastNavigationState != null && lastNavigationState.isOnPath()) {
				navigationState.signalOffPath();
			} else if (navigationState.getTime() - navigationState.getOffPathStartTime() > MAX_TIME_OFF_PATH_MS) {
				events.OnVehicleOffRoute();
			}
		} else {
			navigationState.signalOnPath();
		}
	}
	
	private void updateVehicleMarker() {
		vehicle.setPosition(navigationState.isOnPath() ?
				new Position(navigationState.getLocationOnPath(), navigationState.getBearingOnPath()) :
				new Position(navigationState.getLocation(), navigationState.getBearing()));
	}
	
	private void endNavigation() {
		destination = null;
		navigationState = null;
		lastNavigationState = null;
		map.setMapMode(MapMode.IDLE);
		map.removePolylinePath();
	}
}
