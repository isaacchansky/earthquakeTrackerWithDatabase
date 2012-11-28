package com.example.earthquakedatabase;

import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MapFragment extends Fragment{
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		MapView theMap = ((Main)getActivity()).mapView;
		return theMap; 
	}

}
