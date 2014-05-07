package com.gmnav.model.util;

import com.google.android.gms.maps.model.LatLng;

public class LatLngUtil {
	
	private final static int EARTH_RADIUS_METERS = 6371000;
	
	public static class Bearing {
		public static final double NORTH = 0;
		public static final double NORTH_EAST = 45;
		public static final double EAST = 90;
		public static final double SOUTH_EAST = 135;
		public static final double SOUTH = 180;
		public static final double SOUTH_WEST = 225;
		public static final double WEST = 270;
		public static final double NORTH_WEST = 315;
	}
    
    public static double distanceInMeters(LatLng point1, LatLng point2) {
    	return distanceInRadians(point1, point2) * EARTH_RADIUS_METERS;
    }

    public static double distanceInRadians(LatLng point1, LatLng point2) {
            double lat1R = Math.toRadians(point1.latitude);
            double lat2R = Math.toRadians(point2.latitude);
            double dLatR = Math.abs(lat2R - lat1R);
            double dLngR = Math.abs(Math.toRadians(point2.longitude - point1.longitude));
            double a = Math.sin(dLatR / 2) * Math.sin(dLatR / 2) + Math.cos(lat1R) * Math.cos(lat2R)
                            * Math.sin(dLngR / 2) * Math.sin(dLngR / 2);
            return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public static double initialBearing(LatLng start, LatLng end) {
            return normalizeBearing(Math.toDegrees(initialBearingInRadians(start, end)));
    }

    public static double initialBearingInRadians(LatLng start, LatLng end) {
            double lat1R = Math.toRadians(start.latitude);
            double lat2R = Math.toRadians(end.latitude);
            double dLngR = Math.toRadians(end.longitude - start.longitude);
            double a = Math.sin(dLngR) * Math.cos(lat2R);
            double b = Math.cos(lat1R) * Math.sin(lat2R) - Math.sin(lat1R) * Math.cos(lat2R)
                            * Math.cos(dLngR);
            return Math.atan2(a, b);
    }

    public static LatLng travel(LatLng start, double initialBearing, double distance) {
            double bR = Math.toRadians(initialBearing);
            double lat1R = Math.toRadians(start.latitude);
            double lon1R = Math.toRadians(start.longitude);
            double dR = distance / EARTH_RADIUS_METERS;

            double a = Math.sin(dR) * Math.cos(lat1R);
            double lat2 = Math.asin(Math.sin(lat1R) * Math.cos(dR) + a * Math.cos(bR));
            double lon2 = lon1R
                            + Math.atan2(Math.sin(bR) * a, Math.cos(dR) - Math.sin(lat1R) * Math.sin(lat2));
            return new LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2));
    }

    public static double normalizeLatitude(double latitude) {
            if (Double.isNaN(latitude))
                    return Double.NaN;
            if (latitude > 0) {
                    return Math.min(latitude, 90.0);
            } else {
                    return Math.max(latitude, -90.0);
            }
    }

    public static double normalizeLongitude(double longitude) {
            if (Double.isNaN(longitude) || Double.isInfinite(longitude))
                    return Double.NaN;
            double longitudeResult = longitude % 360;
            if (longitudeResult > 180) {
                    double diff = longitudeResult - 180;
                    longitudeResult = -180 + diff;
            } else if (longitudeResult < -180) {
                    double diff = longitudeResult + 180;
                    longitudeResult = 180 + diff;
            }
            return longitudeResult;
    }

    public static double normalizeBearing(double bearing) {
            if (Double.isNaN(bearing) || Double.isInfinite(bearing))
                    return Double.NaN;
            double bearingResult = bearing % 360;
            if (bearingResult < 0)
                    bearingResult += 360;
            return bearingResult;
    }
    
    public static LatLng closestLocationOnLine(LatLng start, LatLng end, LatLng location) {
    	double squareDistance = Math.pow(start.longitude - end.longitude, 2) + Math.pow(start.latitude - end.latitude, 2);
    	if (squareDistance == 0) {
    		return start;
    	}
    	
    	double interpolationFactor = ((location.longitude - start.longitude) * (end.longitude - start.longitude) +
    			((location.latitude - start.latitude) * (end.latitude - start.latitude)) / squareDistance);
    	double boundedIF = Math.min(1, Math.max(0, interpolationFactor));
    	
    	return new LatLng(
    			start.latitude + boundedIF * (end.latitude - start.latitude),
    			start.longitude + boundedIF * (end.longitude - start.longitude));
    }    
}
