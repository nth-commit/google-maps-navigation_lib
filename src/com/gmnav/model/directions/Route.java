package com.gmnav.model.directions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;

public class Route extends AsyncTask<Void, Void, String> {
	
	public interface DirectionsRetrieved {
		void invoke(Directions directions);
	}
	
	private static final String GOOGLE_DIRECTIONS_URL = "http://maps.googleapis.com/maps/api/directions/json?";
	
	private LatLng origin;
	private LatLng destination;
	private DirectionsRetrieved directionsRetrieved;

	public Route(LatLng origin, LatLng destination) {
		this.origin = origin;
		this.destination = destination;		
	}
	
	public void getDirections(DirectionsRetrieved directionsRetrieved) {
		this.directionsRetrieved = directionsRetrieved;
		this.execute();
	}
	
	private String getRequestUrl() {
		String url = GOOGLE_DIRECTIONS_URL;
		url += "origin=" + origin.latitude + "," + origin.longitude;
		url += "&destination=" + destination.latitude + "," + destination.longitude;
		url += "&sensor=false";
		return url;
	}

	@Override
	protected String doInBackground(Void... arg0) {
		try {
			HttpClient http = new DefaultHttpClient();
			HttpResponse response = http.execute(new HttpGet(getRequestUrl()));
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(String directionsJson) {
		try {
			this.directionsRetrieved.invoke(new Directions(origin, destination, directionsJson));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
