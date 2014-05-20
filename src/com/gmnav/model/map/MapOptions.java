package com.gmnav.model.map;

import com.gmnav.Defaults;
import com.gmnav.model.LatLng;

public class MapOptions {
	
	private LatLng location = Defaults.LOCATION;
	private IMapFactory mapFactory;
	
	public LatLng location() {
		return location;
	}
	
	public MapOptions location(LatLng location) {
		this.location = location;
		return this;
	}
	
	public IMapFactory mapFactory() {
		return mapFactory;
	}
	
	public MapOptions mapFactory(IMapFactory mapFactory) {
		this.mapFactory = mapFactory;
		return this;
	}
}
