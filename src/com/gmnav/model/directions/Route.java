package com.gmnav.model.directions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.gmnav.model.util.AsyncTaskExecutor;
import com.google.android.gms.maps.model.LatLng;

public class Route extends AsyncTask<Void, Void, String> {
	
	public interface DirectionsRetrieved {
		void onSuccess(Directions directions, LatLng origin, LatLng destination);
		void onFailure(String message, LatLng origin, LatLng destination);
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
		AsyncTaskExecutor.execute(this);
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
			String error = "Failed to retrieve directions with exception: " + ex.getMessage();
			Log.e("DefinedError", error);
			directionsRetrieved.onFailure(error, origin, destination);
			ex.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(String directionsJson) {
		try {
			JSONObject responseObject = new JSONObject(directionsJson);
			if (responseObject.getString("status") == "OK") {
				JSONObject route = responseObject.getJSONArray("routes").getJSONObject(0); // Only one route supported
				directionsRetrieved.onSuccess(new Directions(origin, destination, route), origin, destination);
			} else {
				String message =  "Failed to find directions, response was: " + directionsJson;
				Log.e("DefinedError", message);
				directionsRetrieved.onFailure(message, origin, destination);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
