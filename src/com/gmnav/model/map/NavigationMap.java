package com.gmnav.model.map;

import java.util.List;

import android.graphics.Point;
import android.view.View;

import com.gmnav.model.LatLng;
import com.gmnav.model.PointD;
import com.gmnav.model.map.IMap.OnTouchEventHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.CameraPosition;

public class NavigationMap {
	
	public interface OnMapModeChangedListener {
		void invoke(MapMode mode);
	}
	
	public enum MapMode {
		FOLLOW,
		FREE
	}
	
	private static final int FOLLOW_VEHICLE_ANIMATION_TIME = 1000;
	private static final float NAVIGATING_TILT = 60;
	private static final float NAVIGATING_ZOOM = 19;
	private static final float IDLE_TILT = 0;
	private static final float IDLE_BEARING = 0;
	private static final float IDLE_ZOOM = 3;
	
	private MapFragment mapFragment;
	private IMap map;
	private UiSettings mapUiSettings;
	private LatLng vehicleLocation;
	private double vehicleBearing;
	private PointD anchor;
	private MapMode mapMode;
	
	private OnMapModeChangedListener mapModeChangedListener;
	
	private boolean trackLocation = true;
	
	public NavigationMap(IMap map, NavigationMapOptions options) {
		vehicleLocation = options.location();
		anchor = options.anchor();
		this.map = map;
		
		map.setOnTouchEventHandler(new OnTouchEventHandler() {
			@Override
			public void invoke() {
				setTrackLocationEnabled(false);
				if (mapMode != MapMode.FREE) {
					setMapMode(MapMode.FREE);
				}
			}
		});
	}
	
	public NavigationMap(MapFragment mapFragment, NavigationMapOptions options) {
		this.mapFragment = mapFragment;
		vehicleLocation = options.location();
		anchor = options.anchor();
		
		initialiseUiSettings();
		initialiseLocationButtonInteractions();
		setMapMode(MapMode.FREE);
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
				if (mapMode == MapMode.FOLLOW) {
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
	
	public IMap getMap() {
		return map;
	}
	
	public void setOnMapModeChangedListener(OnMapModeChangedListener listener){
		mapModeChangedListener = listener;
	}
	
	public void addPathPolyline(List<LatLng> path) {
		map.addPolyline(new PolylineOptions().path(path).color(0xff3073F0));
	}
	
	public void removePolylinePath() {
		map.removePolyline();
	}
	
	public MapMode getMapMode() {
		return mapMode;
	}
	
	public void setMapMode(MapMode mode) {
		mapMode = mode;
		boolean isFollowing = mode == MapMode.FOLLOW;
		if (isFollowing) {
			setCameraPositionNavigatingDefault();
		}
		
		if (mapModeChangedListener != null) {
			mapModeChangedListener.invoke(mode);
		}
	}
	
	private void setCameraPositionNavigatingDefault() {
		map.setZoom(NAVIGATING_ZOOM);
		map.setTilt(NAVIGATING_TILT);
		map.setBearing(vehicleBearing);
		map.invalidate(FOLLOW_VEHICLE_ANIMATION_TIME);
	}
	
	public void setVehiclePosition(LatLng location, double bearing) {
		vehicleLocation = location;
		vehicleBearing = bearing;
		
		if (trackLocation) {
			map.setLocation(location);
			map.setBearing(bearing);
			map.invalidate();
		}
	}
}
