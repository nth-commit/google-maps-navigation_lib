package com.gmnav.model.map;

import com.google.android.gms.maps.model.LatLng;
import com.gmnav.model.util.PointD;

public class SphericalMercatorProjection {
	
	public static PointD latLngToWorldXY(LatLng latLng, double zoomLevel) {
		final double worldWidthPixels = toWorldWidthPixels(zoomLevel);
		final double x = latLng.longitude / 360 + .5;
		final double siny = Math.sin(Math.toRadians(latLng.latitude));
		final double y = 0.5 * Math.log((1 + siny) / (1 - siny)) / -(2 * Math.PI) + .5;
		return new PointD(x * worldWidthPixels, y * worldWidthPixels);
	}
	
	public static LatLng worldXYToLatLng(PointD point, double zoomLevel) {
		final double worldWidthPixels = toWorldWidthPixels(zoomLevel);
		final double x = point.x / worldWidthPixels - 0.5;
		final double lng = x * 360;
		
		double y = .5 - (point.y / worldWidthPixels);
		final double lat = 90 - Math.toDegrees(Math.atan(Math.exp(-y * 2 * Math.PI)) * 2);
		
		return new LatLng(lat, lng);
	}
	
	private static double toWorldWidthPixels(double zoomLevel) {
    	return 256 * Math.pow(2, zoomLevel);
    }
}