package com.gmnav.model.vehicle;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.opengl.Visibility;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.gmnav.NavigationFragment;
import com.gmnav.R;
import com.gmnav.model.map.NavigationMap;
import com.gmnav.model.util.LayoutUtil;
import com.gmnav.model.util.PointD;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;

public class StaticVehicleMarker implements IVehicleMarker {
	
	private Vehicle vehicle;
	
	private ImageView markerImage;
	
	public StaticVehicleMarker(NavigationFragment navigationFragment, Vehicle vehicle, NavigationMap map) {
		this.vehicle = vehicle;
		map.getInnerMap().setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				setTilt(position.tilt);
			}
		});
		
		ViewGroup view = (ViewGroup)navigationFragment.getView();
		LinearLayout container = (LinearLayout)LayoutUtil.getChildViewById(view, R.id.static_vehicle_marker_container);
		markerImage = (ImageView)LayoutUtil.getChildViewById(container, R.id.static_vehicle_marker);
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		
		Bitmap image = vehicle.getImage();
		markerImage.setImageBitmap(image);
		final Point mapSize = map.getSize();
		final PointD anchor = vehicle.getAnchor();
		LayoutParams layout = new LayoutParams(image.getWidth(), image.getHeight()) {{
			leftMargin = (int)((mapSize.x * anchor.x) - width / 2);
			topMargin = (int)((mapSize.y * anchor.y) - height / 2);
		}};
		markerImage.setLayoutParams(layout);
	}

	@Override
	public void show() {
		markerImage.setVisibility(View.VISIBLE);
	}

	@Override
	public void hide() {
		markerImage.setVisibility(View.INVISIBLE);
	}
	
	public Vehicle getVehicle() {
		return vehicle;
	}
	
	private void setTilt(float tilt) {
		
	}
}
