package com.gmnav.navigation;

public interface NavigationEvents {
	
	void OnNavigatorReady(Navigator navigator);
	
	void OnGpsFound();
	
	void OnGpsSignalLost();
	
}
