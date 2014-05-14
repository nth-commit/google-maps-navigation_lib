package com.gmnav.model.directions;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gmnav.model.util.GoogleUtil;
import com.gmnav.model.util.LatLngUtil;
import com.google.android.gms.maps.model.LatLng;

public class Directions {
	
	private ArrayList<Direction> directions;
	private ArrayList<Point> path;
	private ArrayList<LatLng> latLngPath;
	private String originAddress;
	private String destinationAddress;
	private LatLng origin;
	private LatLng destination;
	
	public Directions(LatLng origin, LatLng destination, String jsonString) throws JSONException {
		this.origin = origin;
		this.destination = destination;
		JSONObject route = new JSONObject(jsonString).getJSONArray("routes").getJSONObject(0); // Only one route supported
		JSONObject leg = route.getJSONArray("legs").getJSONObject(0); // Only one leg supported
		originAddress = leg.getString("start_address");
		destinationAddress = leg.getString("end_address");
		createDirections(leg.getJSONArray("steps"));
		createPath();
		createLatLngPath();
	}
	
	private void createDirections(JSONArray steps) throws JSONException {
		directions = new ArrayList<Direction>();
		
		Direction previousDirection = null;
		List<LatLng> nextDirectionPath = new ArrayList<LatLng>();
		nextDirectionPath.add(origin);
		int nextDirectionTime = 0;
		int nextDirectionDistance = 0;
		
		for (int i = 0; i < steps.length(); i++) {
			JSONObject googleStep = steps.getJSONObject(i);
			String htmlText = googleStep.getString("html_instructions");
			Direction currentDirection = i == 0 ? 
					DirectionFactory.createDepartureDirection(nextDirectionPath, nextDirectionTime, nextDirectionDistance, htmlText, originAddress) :
						DirectionFactory.createTransitDirection(nextDirectionPath, nextDirectionTime, nextDirectionDistance, htmlText, previousDirection);
			directions.add(currentDirection);
			
			nextDirectionPath = GoogleUtil.decodePolyline(googleStep.getJSONObject("polyline").getString("points"));
			nextDirectionTime = googleStep.getJSONObject("duration").getInt("value");
			nextDirectionDistance = googleStep.getJSONObject("distance").getInt("value");
			previousDirection = currentDirection;
		}
		
		nextDirectionPath.add(destination);
		Direction arrivalDirection = DirectionFactory.createArrivalDirection(nextDirectionPath, nextDirectionTime, nextDirectionDistance, destinationAddress, previousDirection);
		directions.add(arrivalDirection);
	}
	
	private void createPath() {
		Direction currentDirection;
		Point currentPoint;
		Point prevPoint = createLastPoint();
		
		path = new ArrayList<Point>();
		path.add(prevPoint);
		
		for (int i = directions.size() - 1; i >= 0; i--) {
			currentDirection = directions.get(i);
			List<LatLng> currentDirectionPoints = currentDirection.getPath();
			for (int j = currentDirectionPoints.size() - 1; j >= 0; j--) {
				currentPoint = createPoint(currentDirectionPoints.get(j), currentDirection, prevPoint);
				path.add(0, currentPoint);
				prevPoint = currentPoint;
			}
		}
	}
	
	private Point createLastPoint() {
		return new Point() {{
			location = destination;
			distanceToNextPointMeters = 0;
			timeToNextPointSeconds = 0;
			distanceToCurrentDirectionMeters = 0;
			timeToCurrentDirectionSeconds = 0;
			distanceToNextDirectionMeters = 0;
			timeToNextDirectionSeconds = 0;
			distanceToArrivalMeters = 0;
			timeToArrivalSeconds = 0;
			direction = directions.get(directions.size() - 1);
			nextDirection = null;
			nextPoint = null;
		}};
	}
	
	private Point createPoint(final LatLng loc, final Direction dir, final Point next) {
		final boolean isNewDirection = next.direction != dir;
		final double distanceToNext = LatLngUtil.distanceInMeters(loc, next.location);
		double directionDistance = dir.getDistanceInMeters();
		double ratioOfTotalDistance = directionDistance == 0 ? 0 : distanceToNext / directionDistance;
		final double timeToNext = ratioOfTotalDistance == 0 ? 0 : dir.getTimeInSeconds() * ratioOfTotalDistance;
		
		return new Point() {{
			location = loc;
			distanceToNextPointMeters = distanceToNext;
			timeToNextPointSeconds = timeToNext;
			distanceToCurrentDirectionMeters = isNewDirection ? 0 : next.distanceToCurrentDirectionMeters + distanceToNext;
			timeToCurrentDirectionSeconds = isNewDirection ? 0 : next.timeToCurrentDirectionSeconds + timeToNext;
			distanceToNextDirectionMeters = isNewDirection ? next.distanceToCurrentDirectionMeters + distanceToNext : next.distanceToNextDirectionMeters + distanceToNext;
			timeToNextDirectionSeconds = isNewDirection ? next.timeToCurrentDirectionSeconds + timeToNext : next.timeToNextDirectionSeconds + timeToNext;
			distanceToArrivalMeters = next.distanceToArrivalMeters + distanceToNext;
			timeToArrivalSeconds = next.timeToArrivalSeconds + timeToNext;
			direction = dir;
			nextDirection = isNewDirection ? next.direction : next.nextDirection;
			nextPoint = next;
		}};
	}
	
	private void createLatLngPath() {
		latLngPath = new ArrayList<LatLng>();
		LatLng lastLocation = path.get(0).location;
		for (int i = 1; i < path.size(); i++) {
			LatLng currentLocation = path.get(i).location;
			if (!lastLocation.equals(currentLocation)) {
				latLngPath.add(currentLocation);
			}
			lastLocation = currentLocation;
		}
	}
	
	public List<Direction> getDirectionsList() {
		return directions;
	}
	
	public List<Point> getPath() {
		return path;
	}
	
	public List<LatLng> getLatLngPath() {
		return latLngPath;
	}
}
