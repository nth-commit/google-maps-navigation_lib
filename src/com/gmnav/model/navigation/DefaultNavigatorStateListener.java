package com.gmnav.model.navigation;

import android.app.Activity;
import android.app.FragmentTransaction;

import com.gmnav.DirectionFragment;
import com.gmnav.DirectionsOverlayFragment;
import com.gmnav.NavigationFragment;
import com.gmnav.R;
import com.gmnav.model.directions.Direction;
import com.gmnav.model.directions.Directions;
import com.gmnav.model.directions.Point;
import com.gmnav.model.util.LatLngUtil;

public class DefaultNavigatorStateListener implements INavigatorStateListener {
	
	private boolean DEBUG_DISABLE_DIRECTIONS = false;
	
	private DirectionsOverlayFragment directionsOverlayFragment;
	private DirectionFragment currentDirectionFragment;
	private Activity parentActivity;
	private Navigator navigator;
	
	public DefaultNavigatorStateListener(NavigationFragment fragment) {
		parentActivity = fragment.getActivity();
		navigator = fragment.getNavigator();
		directionsOverlayFragment = new DirectionsOverlayFragment();
	}
	
	@Override
	public void OnNewPathFound(Directions directions) {
	}

	@Override
	public void OnDeparture(NavigationState state) {
		if (!DEBUG_DISABLE_DIRECTIONS) {
			FragmentTransaction ft = parentActivity.getFragmentManager().beginTransaction();
			ft.add(R.id.directions_overlay_container, directionsOverlayFragment);
			ft.commit();
		}
	}

	@Override
	public void OnArrival(NavigationState state) {
		if (!DEBUG_DISABLE_DIRECTIONS) {
			FragmentTransaction ft = parentActivity.getFragmentManager().beginTransaction();
			ft.remove(directionsOverlayFragment);
			ft.commit();
		}
	}

	@Override
	public void OnVehicleOffPath(NavigationState state) {
		navigator.go(navigator.getDestination());
	}
	
	@Override
	public void OnNewDirection(NavigationState state) {
		if (!DEBUG_DISABLE_DIRECTIONS) {
			Direction direction = state.getCurrentPoint().direction;
			FragmentTransaction ft = parentActivity.getFragmentManager().beginTransaction();
			if (currentDirectionFragment != null) {
				ft.remove(currentDirectionFragment);
			}
			currentDirectionFragment = DirectionFragment.newInstance(direction);
			ft.add(R.id.direction_fragment_container, currentDirectionFragment);
			ft.commit();
		}
	}
	
	public void OnNavigatorTick(NavigationState state) {
		if (!DEBUG_DISABLE_DIRECTIONS) {
			Point currentPoint = state.getCurrentPoint();
			Point nextPoint = currentPoint.nextPoint;
			double distanceToDirection = LatLngUtil.distanceInMeters(state.getLocationOnPath(), nextPoint.location) +
					nextPoint.distanceToCurrentDirectionMeters;
			currentDirectionFragment.setDirectionDistance(distanceToDirection);
		}
	}

}
