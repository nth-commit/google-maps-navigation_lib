package com.navidroidgms.model.map;

import java.util.ArrayList;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;

import com.navidroid.NavigationFragment;
import com.navidroid.R;
import com.navidroidgms.Util;
import com.navidroidgms.model.map.NotifyingMapFragment.WhenMapReady;
import com.navidroid.model.LatLng;
import com.navidroid.model.PointD;
import com.navidroid.model.map.IMap;
import com.navidroid.model.map.PolylineOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Polyline;

public class GoogleMapWrapper implements IMap {
	
	public interface WhenGoogleMapReadyCallback {
		void invoke(GoogleMap googleMap);
	}
	
	private GoogleMapReadyCallbacks mapReadyCallbacks;
	private MapFragment mapFragment;
	private MapEventsListener eventsListener;
	private CameraPositionFactory cameraPositionFactory;
	private CameraPosition cameraPosition;
	private PointD anchor;
	private Polyline polyline;
	
	public GoogleMapWrapper(NavigationFragment fragment) {
		cameraPosition = new CameraPosition(Util.toGoogleLatLng(new LatLng(0, 0)), 0, 0, 0);
		anchor = new PointD(0.5, 0.5);
		mapReadyCallbacks = new GoogleMapReadyCallbacks();
		initialiseEventsListener(fragment);
		initialiseGoogleMap(fragment);
		cameraPositionFactory = new CameraPositionFactory();
	}
	
	private void initialiseEventsListener(NavigationFragment fragment) {
		ViewGroup mapContainer = (ViewGroup)fragment.getActivity().findViewById(R.id.navigation_fragment);
		eventsListener = new MapEventsListener(mapContainer.getContext());
		mapContainer.addView(eventsListener);
	}
	
	private void initialiseGoogleMap(NavigationFragment fragment) {
		mapFragment = NotifyingMapFragment.newInstance(new WhenMapReady() {
			@Override
			public void invoke(GoogleMap map) {
				initialiseMapUiSettings(map.getUiSettings());
				mapReadyCallbacks.signalMapReady(map);
			}
		});
		FragmentManager fm = fragment.getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.map_container, mapFragment);
		ft.commit();
	}

	private void initialiseMapUiSettings(UiSettings mapUiSettings) {
		mapUiSettings.setZoomControlsEnabled(false);
	}

	@Override
	public void invalidate() {
		mapReadyCallbacks.whenMapReady(new WhenGoogleMapReadyCallback() {
			@Override
			public void invoke(GoogleMap googleMap) {
				googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			}
		});
	}
	
	@Override
	public void invalidate(final int animationTime) {
		invalidate(animationTime, null);
	}
	
	@Override
	public void invalidate(final int animationTime, final OnInvalidationAnimationFinished invalidationAnimationFinished) {
		mapReadyCallbacks.whenMapReady(new WhenGoogleMapReadyCallback() {
			@Override
			public void invoke(GoogleMap googleMap) {
				googleMap.animateCamera(
						CameraUpdateFactory.newCameraPosition(cameraPosition),
						animationTime,
						new CancelableCallback() {
							@Override
							public void onFinish() {
								if (invalidationAnimationFinished != null) {
									invalidationAnimationFinished.invoke();
								}
							}
							@Override
							public void onCancel() {
								if (invalidationAnimationFinished != null) {
									invalidationAnimationFinished.invoke();
								}
							}
						});
			}
		});
	}
	
	public void whenGoogleMapReady(WhenGoogleMapReadyCallback callback) {
		mapReadyCallbacks.whenMapReady(callback);
	}

	@Override
	public void setLocation(LatLng location) {
		cameraPosition = cameraPositionFactory.newCameraPosition(cameraPosition, Util.toGoogleLatLng(getOffsetLocation(location)));
	}

	@Override
	public void setBearing(double bearing) {
		cameraPosition = cameraPositionFactory.newCameraPositionFromBearing(cameraPosition, (float)bearing);
	}

	@Override
	public void setTilt(double tilt) {
		cameraPosition = cameraPositionFactory.newCameraPositionFromTilt(cameraPosition, (float)tilt);
	}

	@Override
	public void setZoom(double zoom) {
		cameraPosition = cameraPositionFactory.newCameraPositionFromZoom(cameraPosition, (float)zoom);
	}
	
	@Override
	public void setOnTouchEventHandler(OnTouchEventHandler handler) {
		eventsListener.setOnTouchEventHandler(handler);
	}
	
	@Override
	public void setOnUpdateEventHandler(final OnUpdate handler) {
		mapReadyCallbacks.whenMapReady(new WhenGoogleMapReadyCallback() {
			@Override
			public void invoke(GoogleMap googleMap) {
				googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
					@Override
					public void onCameraChange(CameraPosition arg0) {
						handler.invoke();
					}
				});
			}
		});
	}

	@Override
	public void setAnchor(PointD anchor) {
		this.anchor = anchor;
	}

	@Override
	public LatLng getLocation() {
		return Util.fromGoogleLatLng(cameraPosition.target);
	}

	@Override
	public double getBearing() {
		return cameraPosition.bearing;
	}

	@Override
	public double getTilt() {
		return cameraPosition.tilt;
	}

	@Override
	public double getZoom() {
		return cameraPosition.zoom;
	}

	@Override
	public PointD getAnchor() {
		return anchor;
	}
	
	public Point getSize() {
		View mapView = mapFragment.getView();
		return new Point(mapView.getMeasuredWidth(), mapView.getMeasuredHeight());
	}
	
	private LatLng getOffsetLocation(LatLng location) {
		Point size = getSize();
		PointD anchorOffset = new PointD(size.x * (0.5 - anchor.x), size.y * (0.5 - anchor.y));
		PointD screenCenterWorldXY = SphericalMercatorProjection.latLngToWorldXY(location, getZoom());
		PointD newScreenCenterWorldXY = new PointD(screenCenterWorldXY.x + anchorOffset.x, screenCenterWorldXY.y + anchorOffset.y);
		newScreenCenterWorldXY.rotate(screenCenterWorldXY, cameraPosition.bearing);
		return SphericalMercatorProjection.worldXYToLatLng(newScreenCenterWorldXY, getZoom());
	}

	@Override
	public void addPolyline(final PolylineOptions options) {
		mapReadyCallbacks.whenMapReady(new WhenGoogleMapReadyCallback() {
			@Override
			public void invoke(GoogleMap googleMap) {
				ArrayList<com.google.android.gms.maps.model.LatLng> path = new ArrayList<com.google.android.gms.maps.model.LatLng>();
				for (int i = 0; i < options.path().size(); i++) {
					path.add(Util.toGoogleLatLng(options.path().get(i)));
				}
				
				com.google.android.gms.maps.model.PolylineOptions googlePolylineOptions =
						new com.google.android.gms.maps.model.PolylineOptions()
							.addAll(path)
							.color(options.color());
				
				polyline = googleMap.addPolyline(googlePolylineOptions);
			}
		});
	}

	@Override
	public void removePolyline() {
		if (polyline != null) {
			polyline.remove();
			polyline = null;
		}
	}
}
