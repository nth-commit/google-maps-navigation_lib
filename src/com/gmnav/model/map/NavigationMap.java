package com.gmnav.model.map;

import java.util.List;

import android.graphics.Point;
import android.view.View;

import com.gmnav.model.map.MapEventsListener.OnTouchEventHandler;
import com.gmnav.model.navigation.Vehicle;
import com.gmnav.model.positioning.Position;
import com.gmnav.model.util.PointD;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class NavigationMap {
	
	public enum MapMode {
		NAVIGATING,
		IDLE
	}
	
	private static final float NAVIGATING_TILT = 60;
	private static final float NAVIGATING_ZOOM = 19;
	private static final float IDLE_TILT = 0;
	private static final float IDLE_BEARING = 0;
	private static final float IDLE_ZOOM = 3;
	
	private MapFragment mapFragment;
	private GoogleMap map;
	private CameraPositionFactory cameraPositionFactory;
	private UiSettings mapUiSettings;
	
	private LatLng vehicleLocation;
	private float vehicleBearing;
	private PointD anchor;
	private Marker vehicleMarker;
	private Polyline polylinePath;
	private MapMode mapMode;
	
	private boolean trackLocation = true;
	
	public NavigationMap(MapFragment mapFragment, MapEventsListener mapEventsListener, NavigationMapOptions options) {
		this.mapFragment = mapFragment;
		map = mapFragment.getMap();
		cameraPositionFactory = new CameraPositionFactory(map);
		vehicleLocation = options.location();
		anchor = options.anchor();
		
		mapEventsListener.setOnTouchEventHandler(new OnTouchEventHandler() {
			@Override
			public void invoke() {
				setTrackLocationEnabled(false);		
			}
		});
		
		initialiseUiSettings();
		initialiseLocationButtonInteractions();
	}
	
	private void initialiseUiSettings() {
		mapUiSettings = map.getUiSettings();
		mapUiSettings.setZoomControlsEnabled(false);
		mapUiSettings.setCompassEnabled(false);
		mapUiSettings.setRotateGesturesEnabled(false);
	}
	
	private void initialiseLocationButtonInteractions() {
		map.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
			@Override
			public boolean onMyLocationButtonClick() {
				if (mapMode == MapMode.NAVIGATING) {
					setTrackLocationEnabled(true);
					setCameraPositionNavigatingDefault();
					return true;
				}
				return false;
			}
		});
	}
	
	private void setTrackLocationEnabled(boolean enabled) {
		trackLocation = enabled;
		map.setMyLocationEnabled(!enabled);
	}

	public Marker addVehicleMarker(Vehicle vehicle) {
		removeVehicleMarker();
		vehicleMarker = map.addMarker(new MarkerOptions()
				.position(vehicle.getLocation())
				.icon(BitmapDescriptorFactory.fromBitmap(vehicle.getImage()))
				.flat(true)
				.anchor(0.5f, 0.5f));
		anchor = vehicle.getAnchor();
		return vehicleMarker;
	}
	
	public void removeVehicleMarker() {
		if (vehicleMarker != null) {
			vehicleMarker.remove();
		}
	}
	
	public Polyline addPathPolyline(List<LatLng> path) {
		removePolylinePath();
		polylinePath = map.addPolyline(new PolylineOptions().addAll(path).color(0xff3073F0));
		return polylinePath;
	}
	
	public void removePolylinePath() {
		if (polylinePath != null) {
			polylinePath.remove();
		}
	}
	
	public void setMapMode(MapMode mode) {
		mapMode = mode;
		boolean isNavigating = mode == MapMode.NAVIGATING;
		mapUiSettings.setTiltGesturesEnabled(isNavigating);
		if (isNavigating) {
			setCameraPositionNavigatingDefault();
		} else {
			setCameraPositionIdleDefault();
		}
	}
	
	private void setCameraPositionNavigatingDefault() {
		CameraPosition defaultPosition = cameraPositionFactory.newCameraPosition(NAVIGATING_ZOOM, NAVIGATING_TILT, vehicleBearing);
		map.moveCamera(CameraUpdateFactory.newCameraPosition(defaultPosition));
	}
	
	private void setCameraPositionIdleDefault() {
		CameraPosition defaultPosition = cameraPositionFactory.newCameraPosition(IDLE_ZOOM, IDLE_TILT, IDLE_BEARING);
		map.moveCamera(CameraUpdateFactory.newCameraPosition(defaultPosition));
	}
	
	public Point getSize() {
		View mapView = mapFragment.getView();
		return new Point(mapView.getMeasuredWidth(), mapView.getMeasuredHeight());
	}
	
	public void setVehiclePosition(LatLng location, double bearing) {
		vehicleBearing = (float)bearing;
		vehicleLocation = getOffsetLocation(location);
		if (trackLocation) {
			map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPositionFactory.newCameraPosition(vehicleLocation, vehicleBearing)));
		}
	}
	
	private LatLng getOffsetLocation(LatLng location) {
		Point size = getSize();
		PointD anchorOffset = new PointD(size.x * (0.5 - anchor.x), size.y * (0.5 - anchor.y));
		PointD screenCenterWorldXY = SphericalMercatorProjection.latLngToWorldXY(location, getZoom());
		PointD newScreenCenterWorldXY = new PointD(screenCenterWorldXY.x + anchorOffset.x, screenCenterWorldXY.y + anchorOffset.y);
		newScreenCenterWorldXY.rotate(screenCenterWorldXY, vehicleBearing);
		return SphericalMercatorProjection.worldXYToLatLng(newScreenCenterWorldXY, getZoom());
	}
	
	public float getZoom() {
		return map.getCameraPosition().zoom;
	}

	public void setOnCameraChangeListener(OnCameraChangeListener listener) {
		map.setOnCameraChangeListener(listener);
	}
}
