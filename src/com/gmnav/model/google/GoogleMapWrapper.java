package com.gmnav.model.google;

import java.util.ArrayList;

import android.app.FragmentTransaction;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;

import com.gmnav.NavigationFragment;
import com.gmnav.R;
import com.gmnav.model.LatLng;
import com.gmnav.model.PointD;
import com.gmnav.model.map.AbstractMap;
import com.gmnav.model.map.IMap;
import com.gmnav.model.map.PolylineOptions;
import com.gmnav.model.util.LayoutUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Polyline;

public class GoogleMapWrapper implements IMap {
	
	private MapFragment mapFragment;
	private GoogleMap googleMap;
	private GoogleMapEventsListener eventsListener;
	private CameraPositionFactory cameraPositionFactory;
	private CameraPosition cameraPosition;
	private PointD anchor;
	private Polyline polyline;
	
	public GoogleMapWrapper(NavigationFragment fragment) {
		mapFragment = MapFragment.newInstance();
		FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
		ft.add(R.id.map_container, mapFragment);
		ft.commit();
		
		ViewGroup mapContainer = (ViewGroup)mapFragment.getActivity().findViewById(R.id.map_container);
		eventsListener = new GoogleMapEventsListener(mapFragment.getActivity());	
		mapContainer.addView(eventsListener);
		
		googleMap = mapFragment.getMap();
		cameraPositionFactory = new CameraPositionFactory(googleMap);
	}

	@Override
	public void invalidate() {
		googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));		
	}
	
	@Override
	public void invalidate(int animationTime) {
		googleMap.animateCamera(
				CameraUpdateFactory.newCameraPosition(cameraPosition),
				animationTime,
				new CancelableCallback() {
					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
						
					}
				});
	}
	
	public GoogleMap getGoogleMap() {
		return googleMap;
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
		googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition arg0) {
				handler.invoke();
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
}
