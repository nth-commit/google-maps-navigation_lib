package com.navidroidgms.model.directions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;

import com.navidroid.model.directions.Direction;
import com.navidroid.model.directions.Directions;
import com.navidroid.model.directions.IDirectionsFactory;
import com.navidroid.model.directions.Movement;
import com.navidroid.model.LatLng;

public class DirectionsFactory implements IDirectionsFactory {
	
	private static final String GOOGLE_DIRECTIONS_URL = "http://maps.googleapis.com/maps/api/directions/json?";

	@Override
	public String createRequestUrl(LatLng origin, LatLng destination) {
		String url = GOOGLE_DIRECTIONS_URL;
		url += "origin=" + origin.latitude + "," + origin.longitude;
		url += "&destination=" + destination.latitude + "," + destination.longitude;
		url += "&sensor=false";
		return url;
	}

	@Override
	public Directions createDirections(LatLng origin, LatLng destination, String response) throws Exception {
		JSONObject responseObject = new JSONObject(response);
		if (responseObject.getString("status").equals("OK")) {
			return createDirections(origin, destination, responseObject);
		} else {
			throw new Exception("Failed to find directions, response was: " + response);
		}
	}
	
	private Directions createDirections(LatLng origin, LatLng destination, JSONObject responseObject) throws Exception {
		JSONObject route = responseObject.getJSONArray("routes").getJSONObject(0); // Only one route supported
		JSONObject leg = route.getJSONArray("legs").getJSONObject(0); // Only one leg supported
		String originAddress = leg.getString("start_address");
		String destinationAddress = leg.getString("end_address");
		List<Direction> directions = createDirectionsList(origin, destination, destinationAddress, leg.getJSONArray("steps"));
		return new Directions(origin, destination, originAddress, destinationAddress, directions);
	}
	
	private List<Direction> createDirectionsList(LatLng origin, LatLng destination, String destinationAddress, JSONArray steps) throws JSONException {
		List<Direction> directions = new ArrayList<Direction>();
		Direction previousDirection = null;
		List<LatLng> nextDirectionPath = new ArrayList<LatLng>();
		nextDirectionPath.add(origin);
		int nextDirectionTime = 0;
		int nextDirectionDistance = 0;
		
		for (int i = 0; i < steps.length(); i++) {
			JSONObject googleStep = steps.getJSONObject(i);
			String htmlText = googleStep.getString("html_instructions");
			Direction currentDirection = i == 0 ? 
					createDepartureDirection(nextDirectionPath, nextDirectionTime, nextDirectionDistance, htmlText) :
					createTransitDirection(nextDirectionPath, nextDirectionTime, nextDirectionDistance, htmlText, previousDirection);
			directions.add(currentDirection);
			
			nextDirectionPath = decodePolyline(googleStep.getJSONObject("polyline").getString("points"));
			nextDirectionTime = googleStep.getJSONObject("duration").getInt("value");
			nextDirectionDistance = googleStep.getJSONObject("distance").getInt("value");
			previousDirection = currentDirection;
		}
		
		nextDirectionPath.add(destination);
		Direction arrivalDirection = createArrivalDirection(nextDirectionPath, nextDirectionTime, nextDirectionDistance, destinationAddress, previousDirection);
		directions.add(arrivalDirection);
		return directions;
	}
	
	private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
 
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
 
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
 
            LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
            poly.add(p);
        }
 
        return poly;
	}
	
	private Direction createDepartureDirection(List<LatLng> path, int timeSeconds, int distanceMeters, String htmlText) {
		String text = getTextFromHtmlText(htmlText);
		String[] significantInfo = parseHtmlTextForSignificantInfo(htmlText);
		String current = significantInfo[1];
		String target = significantInfo[2];
		Movement movement = Movement.DEPARTURE;
		return new Direction(path, timeSeconds, distanceMeters, text, current, target, movement);
	}
	
	private Direction createArrivalDirection(List<LatLng> path, int timeSeconds, int distanceMeters, String destinationAddress, Direction previousDirection) {
		String text = "You have reached your destination.";
		String current = previousDirection.getTarget();
		Movement movement = Movement.ARRIVAL;
		return new Direction(path, timeSeconds, distanceMeters, text, current, destinationAddress, movement);
	}
	
	private Direction createTransitDirection(List<LatLng> path, int timeSeconds, int distanceMeters, String htmlText, Direction previousDirection) {
		String text = getTextFromHtmlText(htmlText);
		String[] significantInfo = parseHtmlTextForSignificantInfo(htmlText);
		Movement movement = getMovementType(htmlText, significantInfo);
		String current = previousDirection.getTarget();
		String target = significantInfo.length == 1 ? null : movement == Movement.CONTINUE ? significantInfo[0] : significantInfo[1];
		return new Direction(path, timeSeconds, distanceMeters, text, current, target, movement);
	}
	
	private String getTextFromHtmlText(String htmlText) {
		return Html.fromHtml(htmlText).toString();
	}

	private String[] parseHtmlTextForSignificantInfo(String htmlText) {
		if (htmlText.contains("<b>")) { // Google places important info in HTML bold tags.
			String[] splitOnStartTags = htmlText.split("<b>");
			String[] result = new String[splitOnStartTags.length - 1];
			for (int i = 1; i <= result.length; i++) {
				result[i - 1] = splitOnStartTags[i].split("</b>")[0];
			}
			return result;
		}
		return null;
	}
	
	private Movement getMovementType(String htmlText, String[] significantInfo) {
		if (htmlText.toLowerCase(Locale.US).startsWith("continue")) {
			return Movement.CONTINUE;
		} else if (significantInfo[0].toLowerCase(Locale.US).equals("left")) {
			return Movement.TURN_LEFT;
		} else if (significantInfo[0].toLowerCase(Locale.US).equals("right")) {
			return Movement.TURN_RIGHT;
		}
		return Movement.UNKNOWN;
	}
}