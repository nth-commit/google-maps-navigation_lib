package com.gmnav.model.navigation;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

import com.gmnav.DirectionFragment;
import com.gmnav.DirectionsOverlayFragment;
import com.gmnav.R;
import com.gmnav.model.directions.Direction;
import com.gmnav.model.directions.Point;
import com.gmnav.model.util.LatLngUtil;

public class DefaultNavigatorStateListener implements INavigatorStateListener {
	
	private DirectionsOverlayFragment directionsOverlayFragment;
	private DirectionFragment currentDirectionFragment;
	private Activity parentActivity;
	
	public DefaultNavigatorStateListener(Fragment fragment) {
		parentActivity = fragment.getActivity();
		directionsOverlayFragment = new DirectionsOverlayFragment();
	}

	@Override
	public void OnDeparture() {
		FragmentTransaction ft = parentActivity.getFragmentManager().beginTransaction();
		ft.add(R.id.directions_overlay_container, directionsOverlayFragment);
		ft.commit();
	}

	@Override
	public void OnArrival() {
		// TODO Auto-generated method stub
	}

	@Override
	public void OnVehicleOffPath() {
		// TODO: Do rerouting here (default behaviour), not in Navigator class
	}
	
	@Override
	public void OnNewDirection(Direction direction) {
		FragmentTransaction ft = parentActivity.getFragmentManager().beginTransaction();
		if (currentDirectionFragment != null) {
			ft.remove(currentDirectionFragment);
		}
		currentDirectionFragment = DirectionFragment.newInstance(direction);
		ft.add(R.id.direction_fragment_container, currentDirectionFragment);
		ft.commit();
	}
	
	@Override
	public void OnNewPathFound() {
		// TODO Auto-generated method stub
		
	}
	
	public void OnNavigatorTick(NavigationState state) {
		Point currentPoint = state.getCurrentPoint();
		Point nextPoint = currentPoint.nextPoint;
		double distanceToDirection = LatLngUtil.distanceInMeters(state.getLocationOnPath(), nextPoint.location) +
				nextPoint.distanceToCurrentDirectionMeters;
		currentDirectionFragment.setDirectionDistance(distanceToDirection);
	}

}
