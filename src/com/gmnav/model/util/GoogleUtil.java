package com.gmnav.model.util;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;

import com.gmnav.model.LatLng;

public class GoogleUtil {
	
	public static LatLng toLatLng(Location location) {
		return new LatLng(location.getLatitude(), location.getLongitude());
	}
}
