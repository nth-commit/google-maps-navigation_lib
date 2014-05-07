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

public class DirectionFragment extends Fragment {
	
	private Direction direction;
	
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
		View view = inflater.inflate(R.layout.direction_fragment, container, false);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
