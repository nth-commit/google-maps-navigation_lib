package com.navidroidgms.model.map;

import java.util.ArrayList;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.navidroid.NavigationFragment;
import com.navidroid.R;
import com.navidroid.model.LatLng;
import com.navidroid.model.PointD;
import com.navidroid.model.map.PolylineOptions;
import com.navidroidgms.Util;
import com.navidroidgms.model.map.ICustomGoogleMap.OnMarkerCreated;
import com.navidroidgms.model.map.NotifyingMapFragment.WhenMapReady;

public class InternalCustomGoogleMap implements ICustomGoogleMap {
	
	private boolean isAnimating = false;
	private GoogleMap googleMap;
	private MapFragment mapFragment;
	private MapEventsListener eventsListener;
	private CameraPosition cameraPosition;
	private PointD anchor;
	private Polyline polyline;
	private int paddingLeft;
	private int paddingTop;
	private int paddingRight;
	private int paddingBottom;
	
	public InternalCustomGoogleMap(NavigationFragment fragment, final CustomGoogleMap wrapper) {
		cameraPosition = new CameraPosition(Util.toGoogleLatLng(new LatLng(0, 0)), 0, 0, 0);
		anchor = new PointD(0.5, 0.5);
		
		ViewGroup mapContainer = (ViewGroup)fragment.getActivity().findViewById(R.id.navigation_fragment);
		eventsListener = new MapEventsListener(mapContainer.getContext());
		mapContainer.addView(eventsListener);
		
		final InternalCustomGoogleMap map = this;
		mapFragment = NotifyingMapFragment.newInstance(new WhenMapReady() {
			@Override
			public void invoke(NotifyingMapFragment fragment) {
				googleMap = fragment.getMap();
				googleMap.getUiSettings().setZoomControlsEnabled(false);
				wrapper.setInnerObject(map);
			}
		});
		FragmentManager fm = fragment.getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.map_container, mapFragment);
		ft.commit();
	}

	@Override
	public void setLocation(LatLng location) {
		cameraPosition = CameraPositionUtil.newCameraPosition(cameraPosition, Util.toGoogleLatLng(getOffsetLocation(location)));		
	}

	@Override
	public void setBearing(double bearing) {
		cameraPosition = CameraPositionUtil.newCameraPositionFromBearing(cameraPosition, (float)bearing);
	}

	@Override
	public void setTilt(double tilt) {
		cameraPosition = CameraPositionUtil.newCameraPositionFromTilt(cameraPosition, (float)tilt);
	}

	@Override
	public void setZoom(double zoom) {
		cameraPosition = CameraPositionUtil.newCameraPositionFromZoom(cameraPosition, (float)zoom);
	}

	@Override
	public void setAnchor(PointD anchor) {
		this.anchor = anchor;
	}

	@Override
	public void setOnTouchEventHandler(OnTouchEventHandler handler) {
		eventsListener.setOnTouchEventHandler(handler);
	}

	@Override
	public void setOnUpdateEventHandler(final OnUpdate handler) {
		googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition arg0) {
				handler.invoke();
			}
		});
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

	@Override
	public Point getSize() {
		View mapView = mapFragment.getView();
		return mapView == null ? new Point(0, 0) :
			new Point(mapView.getMeasuredWidth() + paddingLeft - paddingRight,
					mapView.getMeasuredHeight() + paddingTop - paddingBottom);
	}

	@Override
	public void invalidate() {
		if (!isAnimating) {
			googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		}
	}

	@Override
	public void invalidate(int animationTime) {
		if (!isAnimating) {
			invalidate(animationTime, null);
		}
	}

	@Override
	public void invalidate(int animationTime, final OnInvalidationAnimationFinished invalidationAnimationFinished) {
		if (CameraPositionUtil.equals(cameraPosition, googleMap.getCameraPosition())) {
			if (invalidationAnimationFinished != null) {
				invalidationAnimationFinished.invoke();
			}
		} else {
			isAnimating = true;
			googleMap.animateCamera(
					CameraUpdateFactory.newCameraPosition(cameraPosition),
					animationTime,
					new CancelableCallback() {
						
						@Override
						public void onFinish() {
							if (invalidationAnimationFinished != null) {
								invalidationAnimationFinished.invoke();
							}
							isAnimating = false;
						}
						
						@Override
						public void onCancel() {
							isAnimating = false;
						}
					});
		}		
	}

	@Override
	public void addPolyline(PolylineOptions options) {
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

	@Override
	public void removePolyline() {
		if (polyline != null) {
			polyline.remove();
			polyline = null;
		}
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
	public void addMarker(MarkerOptions options, OnMarkerCreated onMarkerCreated) {
		onMarkerCreated.invoke(googleMap.addMarker(options));
	}

	@Override
	public void setOnMarkerClickListener(OnMarkerClickListener listener) {
		googleMap.setOnMarkerClickListener(listener);
	}

	@Override
	public void addView(View view) {
		((ViewGroup)mapFragment.getView()).addView(view);
	}
	
	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		paddingLeft = left;
		paddingTop = top;
		paddingRight = right;
		paddingBottom = bottom;
		googleMap.setPadding(left, top, right, bottom);
	}
}
