package com.gmnav.model.navigation;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

/**
 * A lazy wrapper for the InternalNavigator class.
 * Stacks up callbacks and calls them, in-order,
 * so a user can do operations on a Navigator,
 * via NavigationFragment.getNavigator(), as soon
 * as the NavigationFragment is instantiated.
 */
public class Navigator {
	
	private interface WhenNavigatorReady {
		void invoke(InternalNavigator navigator);
	}
	
	private List<WhenNavigatorReady> callbacks;
	private InternalNavigator navigator;
	
	public Navigator() {
		callbacks = new ArrayList<WhenNavigatorReady>();
	}
	
	public void setInternalNavigator(InternalNavigator navigator) {
		this.navigator = navigator;
		for (int i = 0; i < callbacks.size(); i++) {
			callbacks.get(i).invoke(navigator);
		}
	}
	
	public void navigateTo(final LatLng location) {
		if (navigator == null) {
			callbacks.add(new WhenNavigatorReady() {
				@Override
				public void invoke(InternalNavigator navigator) {
					navigator.navigateTo(location);
				}
			});
		} else {
			navigator.navigateTo(location);
		}
	}
}
