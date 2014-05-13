package com.gmnav.model.directions;

import java.util.List;
import android.text.Html;
import com.google.android.gms.maps.model.LatLng;

public class Direction {
	
	private List<LatLng> path;
	private int timeSeconds;
	private int distanceMeters;
	private double distanceMetersCalculated;
	private String text;
	private String htmlText;
	
	public Direction(List<LatLng> path, int timeSeconds, int distanceMeters, String htmlText) {
		this.timeSeconds = timeSeconds;
		this.distanceMeters = distanceMeters;
		this.htmlText = htmlText;
		text = Html.fromHtml(htmlText).toString();
	}
	
	public List<LatLng> getPath() {
		return path;
	}
	
	public int getTimeInSeconds() {
		return timeSeconds;
	}
	
	public int getDistanceInMeters() {
		return distanceMeters;
	}
	
	public double getDistanceInMetersCalculated() {
		return distanceMetersCalculated;
	}
	
	public String getHtmlText() {
		return htmlText;
	}
	
	public String getText() {
		return text;
	}
}
