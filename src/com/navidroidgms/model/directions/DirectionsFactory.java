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
	public String createRequestUrl(LatLng origin, LatLng destination, LatLng rerouteWaypoint) {
		String url = GOOGLE_DIRECTIONS_URL;
		url += "origin=" + origin.latitude + "," + origin.longitude;
		url += "&destination=" + destination.latitude + "," + destination.longitude;
		if (rerouteWaypoint != null) {
			url += "&waypoints=" + rerouteWaypoint.latitude + "," + rerouteWaypoint.longitude;
		}
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
		JSONArray legs = route.getJSONArray("legs");
		String originAddress = getOriginAddress(legs);
		String destinationAddress = getDestinationAddress(legs);
		List<JSONObject> steps = getSteps(legs);
		List<Direction> directions = createDirectionsList(origin, destination, destinationAddress, steps);
		return new Directions(origin, destination, originAddress, destinationAddress, directions);
	}

	private String getOriginAddress(JSONArray legs) throws JSONException {
		return legs.getJSONObject(0).getString("start_address");
	}
	
	private String getDestinationAddress(JSONArray legs) throws JSONException {
		return legs.getJSONObject(legs.length() - 1).getString("end_address");
	}
	
	private List<JSONObject> getSteps(JSONArray legs) throws JSONException {
		ArrayList<JSONObject> steps = new ArrayList<JSONObject>();
		int numberOfLegs = legs.length();
		for (int i = 0; i < numberOfLegs; i++) {
			JSONArray currentSteps = legs.getJSONObject(i).getJSONArray("steps");
			int numberOfSteps = currentSteps.length();
			
			if (i == 0) {
				steps.add(currentSteps.getJSONObject(0));
			}
			
			for (int j = 1; j < numberOfSteps - 1; j++) {
				steps.add(currentSteps.getJSONObject(j));
			}
			
			if (i == numberOfLegs - 1) {
				steps.add(currentSteps.getJSONObject(numberOfSteps - 1));
			}
		}
		return steps;
	}
	
	private List<Direction> createDirectionsList(LatLng origin, LatLng destination, String destinationAddress, List<JSONObject> steps) throws JSONException {
		List<Direction> directions = new ArrayList<Direction>();
		Direction previousDirection = null;
		List<LatLng> nextDirectionPath = new ArrayList<LatLng>();
		nextDirectionPath.add(origin);
		int nextDirectionTime = 0;
		int nextDirectionDistance = 0;
		
		for (int i = 0; i < steps.size(); i++) {
			JSONObject googleStep = steps.get(i);
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
		String description = getDescriptionFromHtmlText(htmlText);
		String[] significantInfo = parseHtmlTextForSignificantInfo(htmlText);
		String target = getTargetStreet(significantInfo);
		return new Direction(path, timeSeconds, distanceMeters, description, null, target);
	}
	
	private Direction createArrivalDirection(List<LatLng> path, int timeSeconds, int distanceMeters, String destinationAddress, Direction previousDirection) {
		String description = "";
		String current = previousDirection.getTarget();
		return new Direction(path, timeSeconds, distanceMeters, description, current, destinationAddress);
	}
	
	private Direction createTransitDirection(List<LatLng> path, int timeSeconds, int distanceMeters, String htmlText, Direction previousDirection) {
		String description = getDescriptionFromHtmlText(htmlText).toLowerCase(Locale.US);
		String[] splitByDestination = description.split("destination");
		description = splitByDestination[0];
		String[] significantInfo = parseHtmlTextForSignificantInfo(htmlText);
		String current = previousDirection.getTarget();
		String target = getTargetStreet(significantInfo);
		return new Direction(path, timeSeconds, distanceMeters, description, current, target);
	}
	
	private String getDescriptionFromHtmlText(String htmlText) {
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
	
	private String getTargetStreet(String[] significantInfo) {
		String target = significantInfo.length == 1 ? significantInfo[0] : significantInfo[1];
		if (isStringDirectionPrompt(target)) {
			return "?";
		}
		target = formatAcronyms(target);
		return target;
	}
	
	private boolean isStringDirectionPrompt(String value) {
		String lowerCaseValue = value.toLowerCase(Locale.US);
		if (lowerCaseValue.equals("right") ||
			lowerCaseValue.equals("left") ||
			lowerCaseValue.equals("north") ||
			lowerCaseValue.equals("east") ||
			lowerCaseValue.equals("south") ||
			lowerCaseValue.equals("west")) {
			return true;
		}
		return false;
	}
	
	private String formatAcronyms(String target) {
		String[] targetParts = target.split(" ");
		String newTargetString = "";
		for (int i = 0; i < targetParts.length; i++) {
			String part = targetParts[i];
			if (isUpperCase(part)) {
				part = splitStringWithPeriod(part);
			}
			newTargetString += part;
			
			if (i < targetParts.length - 1) {
				newTargetString += " ";
			}
		}
		return newTargetString;
	}
	
	private boolean isUpperCase(String value) {
		for (int i = 0; i < value.length(); i++) {
			if (Character.isLetter(value.charAt(i))) {
				return false;
			}
		}
		String upperCaseValue = new String(value).toUpperCase(Locale.US);
		return upperCaseValue.equals(value);
	}
	
	private String splitStringWithPeriod(String value) {
		String newValue = "";
		for (int j = 0; j < value.length(); j++) {
			newValue += value.charAt(j);
			if (j < value.length() - 1) {
				newValue += ".";
			}
		}
		return newValue;
	}
}