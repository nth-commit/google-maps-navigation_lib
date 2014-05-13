package com.gmnav.model.directions;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;

import com.google.android.gms.maps.model.LatLng;

public class Direction {
	
	private List<LatLng> path;
	private int timeSeconds;
	private int distanceMeters;
	private String text;
	private String htmlText;
	
	public Direction(List<LatLng> path) {
		this.path = path;
	}
	
	public Direction(List<LatLng> path, JSONObject googleStep) throws JSONException {
		this(path);
		timeSeconds = googleStep.getJSONObject("duration").getInt("value");
		distanceMeters = googleStep.getJSONObject("distance").getInt("value");
		htmlText = googleStep.getString("html_instructions");
		text = Html.fromHtml(htmlText).toString();
	}
	
	public static Direction createArrivalDirection(List<LatLng> path) {
		Direction arrivalDirection = new Direction(path);
		return arrivalDirection;
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
	
	public String getHtmlText() {
		return htmlText;
	}
	
	public String getText() {
		return text;
	}
}
