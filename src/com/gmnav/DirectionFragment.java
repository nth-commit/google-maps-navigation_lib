package com.gmnav;

import java.util.ArrayList;
import java.util.List;

import com.gmnav.R;
import com.gmnav.model.directions.Direction;
import com.gmnav.model.directions.DistanceFormatter;
import com.gmnav.model.directions.ImageFactory;
import com.gmnav.model.util.LayoutUtil;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.GridLayout.LayoutParams;
import android.widget.TextView;

public class DirectionFragment extends Fragment {
	
	private final static int LAYOUT_PADDING = 10;
	
	private Direction direction;
	private GridLayout view;
	
	private ImageView directionImage;
	private TextView directionDistance;
	private View directionDivider;
	private TextView directionDescription;
	
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
		view.setPadding(LAYOUT_PADDING, LAYOUT_PADDING, LAYOUT_PADDING, LAYOUT_PADDING);
		createChildReferences();
		arrangeChildViews(container);
		
		setDirectionDescription(generateDirectionDescription());
		setDirectionImage();
		return view;
	}
	
	private void createChildReferences() {
		directionImage = (ImageView)LayoutUtil.getChildViewById(view, R.id.direction_image);
		directionDistance = (TextView)LayoutUtil.getChildViewById(view, R.id.distance_to_direction);
		directionDivider = LayoutUtil.getChildViewById(view, R.id.direction_divider);
		directionDescription = (TextView)LayoutUtil.getChildViewById(view, R.id.direction_description_text); 
	}
	
	private void arrangeChildViews(ViewGroup container) {
		int paddingSize = 2 * LAYOUT_PADDING;
		int workingWidth = container.getMeasuredWidth() - paddingSize;
		int workingHeight = container.getMeasuredHeight() - paddingSize;
		
		LayoutParams imageLayoutParams = arrangeDirectionImage(workingWidth, workingHeight);
		arrangeDirectionDescription(workingWidth, imageLayoutParams);
	}
	
	private LayoutParams arrangeDirectionImage(int workingWidth, int workingHeight) {
		int remainingHeight = workingHeight; 
		int directionImageMargin = (int)(0.1 * workingHeight);
		remainingHeight -= directionImageMargin;
		int directionImageSize = (int)(0.75 * remainingHeight);
		remainingHeight -= directionImageSize;
		
		LayoutParams imageLayoutParams = (LayoutParams)directionImage.getLayoutParams();
		imageLayoutParams.width = directionImageSize;
		imageLayoutParams.height = directionImageSize;
		imageLayoutParams.setMargins(directionImageMargin, directionImageMargin, directionImageMargin, 0);
		directionImage.setLayoutParams(imageLayoutParams);
		return imageLayoutParams;
	}
	
	private void arrangeDirectionDescription(int workingWidth, LayoutParams imageLayoutParams) {
		int directionImageWidth = imageLayoutParams.width + imageLayoutParams.leftMargin + imageLayoutParams.rightMargin;
		LayoutParams dividerLayoutParams = (LayoutParams)directionDivider.getLayoutParams();
		int directionDescriptionWorkingWidth = workingWidth - directionImageWidth - dividerLayoutParams.width - LAYOUT_PADDING;
		
		int directionDescriptionHorizontalMargin = (int)(0.05 * directionDescriptionWorkingWidth);
		directionDescriptionWorkingWidth -= directionDescriptionHorizontalMargin;
		
		LayoutParams descriptionParams = (LayoutParams)directionDescription.getLayoutParams();
		descriptionParams.width = directionDescriptionWorkingWidth;
		descriptionParams.setMargins(directionDescriptionHorizontalMargin, 0, directionDescriptionHorizontalMargin, 0);
		directionDescription.setLayoutParams(descriptionParams);
	}
	
	private String generateDirectionDescription() {
		String directionDescription;
		switch (direction.getMovement()) {
			case DEPARTURE:
				directionDescription = direction.getCurrent() + " toward " + direction.getTarget();
				break;
			case TURN_LEFT:
			case TURN_RIGHT:
			case CONTINUE:
			case ARRIVAL:
				directionDescription = direction.getTarget();
				break;
			default:
				directionDescription = "Unknown";
		}
		return directionDescription;
	}
	
	public void setDirectionImage() {
		if (view != null) {
			directionImage = (ImageView)LayoutUtil.getChildViewById(view, R.id.direction_image);
			directionImage.setImageResource(ImageFactory.getImageResource(directionImage.getContext(), direction, "87ceeb"));
		}
	}
	
	public void setDirectionDescription(String text) {
		if (view != null) {
			directionDescription = (TextView)LayoutUtil.getChildViewById(view, R.id.direction_description_text); 
			directionDescription.setText(text);
		}
	}
	
	public void setDirectionDistance(double distanceMeters) {
		if (view != null) {
			directionDistance = (TextView)LayoutUtil.getChildViewById(view, R.id.distance_to_direction);
			String distance = DistanceFormatter.formatMeters(distanceMeters);
			directionDistance.setText(distance);
		}
	}
}
