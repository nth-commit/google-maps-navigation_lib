package com.gmnav.model.navigation;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.gmnav.DirectionFragment;
import com.gmnav.DirectionsOverlayFragment;
import com.gmnav.R;
import com.gmnav.model.directions.Direction;

public class DefaultNavigatorStateListener implements INavigatorStateListener {
	
	private DirectionsOverlayFragment directionsOverlayFragment;
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
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void OnNewDirection(Direction direction) {
		FragmentTransaction ft = parentActivity.getFragmentManager().beginTransaction();
		ft.add(R.id.direction_fragment_container, DirectionFragment.newInstance(direction));
		ft.commit();
	}
	
	@Override
	public void OnNewPathFound() {
		// TODO Auto-generated method stub
		
	}

}
