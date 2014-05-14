package com.gmnav.model.directions;

import java.util.List;
import java.util.Locale;

import android.text.Html;

import com.google.android.gms.maps.model.LatLng;

public class DirectionFactory {
	
	public static Direction createDepartureDirection(List<LatLng> path, int timeSeconds, int distanceMeters, String htmlText) {
		String text = getTextFromHtmlText(htmlText);
		String[] significantInfo = parseHtmlTextForSignificantInfo(htmlText);
		String current = significantInfo[1];
		String target = significantInfo[2];
		Movement movement = Movement.DEPARTURE;
		return new Direction(path, timeSeconds, distanceMeters, text, current, target, movement);
	}
	
	public static Direction createArrivalDirection(List<LatLng> path, int timeSeconds, int distanceMeters, String destinationAddress, Direction previousDirection) {
		String text = "You have reached your destination.";
		String current = previousDirection.getTarget();
		Movement movement = Movement.ARRIVAL;
		return new Direction(path, timeSeconds, distanceMeters, text, current, destinationAddress, movement);
	}
	
	public static Direction createTransitDirection(List<LatLng> path, int timeSeconds, int distanceMeters, String htmlText, Direction previousDirection) {
		String text = getTextFromHtmlText(htmlText);
		String[] significantInfo = parseHtmlTextForSignificantInfo(htmlText);
		Movement movement = getMovementType(htmlText, significantInfo);
		String current = previousDirection.getTarget();
		String target = movement == Movement.CONTINUE ? significantInfo[0] : significantInfo[1];
		return new Direction(path, timeSeconds, distanceMeters, text, current, target, movement);
	}
	
	private static String getTextFromHtmlText(String htmlText) {
		return Html.fromHtml(htmlText).toString();
	}

	private static String[] parseHtmlTextForSignificantInfo(String htmlText) {
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
	
	private static Movement getMovementType(String htmlText, String[] significantInfo) {
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
