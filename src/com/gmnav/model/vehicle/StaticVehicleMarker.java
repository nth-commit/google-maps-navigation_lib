package com.gmnav.model.vehicle;

import android.graphics.Bitmap;
import android.graphics.Point;
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
	private NavigationMap map;
	private ImageView markerImageView;
	private Bitmap image;
	private boolean isVisible;
	private float currentTilt = -1.0f;
	
	public StaticVehicleMarker(NavigationFragment navigationFragment, Vehicle vehicle, NavigationMap map) {
		this.vehicle = vehicle;
		this.map = map;

		ViewGroup view = (ViewGroup)navigationFragment.getView();
		LinearLayout container = (LinearLayout)LayoutUtil.getChildViewById(view, R.id.static_vehicle_marker_container);
		markerImageView = (ImageView)LayoutUtil.getChildViewById(container, R.id.static_vehicle_marker);
		image = vehicle.getImage();
		isVisible = markerImageView.getVisibility() == View.VISIBLE;
		
		map.getInnerMap().setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				setLayoutParams(position);
			}
		});
		updateLayoutParams();
	}
	
	private void updateLayoutParams() {
		setLayoutParams(map.getInnerMap().getCameraPosition());
	}

	@Override
	public void show() {
		if (!isVisible) {
			isVisible = true;
			updateLayoutParams();
			markerImageView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void hide() {
		if (isVisible) {
			isVisible = false;
			markerImageView.setVisibility(View.INVISIBLE);
		}
	}

	private void setLayoutParams(CameraPosition position) {
		float tilt = position.tilt;
		if (isVisible && tilt != currentTilt) {
			final int imageHeight = (int)(image.getHeight() * Math.cos(Math.toRadians(position.tilt)));
			final int imageWidth = image.getWidth();
			final Bitmap flattenedImage = Bitmap.createScaledBitmap(image, imageWidth, imageHeight, true);
			markerImageView.setImageBitmap(flattenedImage);
			
			final Point mapSize = map.getSize();
			final PointD anchor = vehicle.getScreenAnchor();
			LayoutParams layout = new LayoutParams(imageWidth, imageHeight) {{
				leftMargin = (int)((mapSize.x * anchor.x) - width / 2);
				topMargin = (int)((mapSize.y * anchor.y) - height / 2);
			}};
			markerImageView.setLayoutParams(layout);
			currentTilt = tilt;
		}
	}
}
