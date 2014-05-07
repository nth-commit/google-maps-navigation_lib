package com.gmnav;

import java.util.ArrayList;
import java.util.List;

import com.gmnav.R;
import com.gmnav.model.directions.Direction;
import com.gmnav.model.navigation.NavigationOptions;
import com.gmnav.model.navigation.Navigator;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

public class DirectionFragment extends Fragment {
	
	private Direction direction;
	private GridLayout view;
	
	private static List<Direction> directionsById = new ArrayList<Direction>();
	
	public static final DirectionFragment newInstance(Direction direction) {
		DirectionFragment fragment = new DirectionFragment();
		int id = directionsById.size();
		directionsById.add(id, direction);
		Bundle args = new Bundle();
		args.putInt("index", id);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		int id = getArguments().getInt("index");
		direction = directionsById.get(id);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = (GridLayout)inflater.inflate(R.layout.direction_fragment, container, false);
		}
		
		TextView directionDescription = null;
		for (int i = 0; i < view.getChildCount(); i++) {
			View currentChild = view.getChildAt(i);
			if (currentChild.getId() == R.id.direction_description_text) {
				directionDescription = (TextView)currentChild;
			}
		}
		directionDescription.setText(direction.text);
		return view;
	}
	
	public void setDirectionDescription(String text) {
		if (view != null) {
			TextView directionDescription = (TextView)getChild(R.id.direction_description_text);
			directionDescription.setText(text);
		}
	}
	
	public void setDirectionDistance(double distanceMeters) {
		if (view != null) {
			TextView directionDistance = (TextView)getChild(R.id.distance_to_direction);
			String distance = String.format("%sm", (int)distanceMeters);
			directionDistance.setText(distance);
		}
	}
	
	private View getChild(int id) {
		for (int i = 0; i < view.getChildCount(); i++) {
			View currentChild = view.getChildAt(i);
			if (currentChild.getId() == id) {
				return currentChild;
			}
		}
		return null;
	}
}
