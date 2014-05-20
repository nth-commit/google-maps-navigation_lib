package com.navidroidgms.model.map;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class NotifyingMapFragment extends MapFragment {
	
	public interface WhenMapReady {
		void invoke(GoogleMap map);
	}
	
	private static List<WhenMapReady> mapReadyCallbacksById = new ArrayList<WhenMapReady>();
	
	public static NotifyingMapFragment newInstance(WhenMapReady whenMapReady) {
		NotifyingMapFragment fragment = new NotifyingMapFragment();
		int id = mapReadyCallbacksById.size();
		mapReadyCallbacksById.add(id, whenMapReady);
		Bundle args = new Bundle();
		args.putInt("index", id);
		fragment.setArguments(args);
		return fragment;
	}
	
	private WhenMapReady whenMapReady;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		int id = getArguments().getInt("index");
		whenMapReady = mapReadyCallbacksById.get(id);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		whenMapReady.invoke(getMap());
	}

}
