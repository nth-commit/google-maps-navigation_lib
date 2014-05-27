package com.navidroidgms.model.map;

import com.navidroid.NavigationFragment;
import com.navidroid.model.map.IMap;
import com.navidroid.model.map.IMapFactory;

public class GoogleMapFactory implements IMapFactory {

	@Override
	public IMap createMap(NavigationFragment fragment) {
		CustomGoogleMap wrapper = new CustomGoogleMap();
		@SuppressWarnings("unused")
		InternalCustomGoogleMap customGoogleMap = new InternalCustomGoogleMap(fragment, wrapper);
		return wrapper;
	}
	
}
