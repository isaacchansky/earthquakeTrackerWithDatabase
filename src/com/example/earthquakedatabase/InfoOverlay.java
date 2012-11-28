package com.example.earthquakedatabase;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class InfoOverlay extends ItemizedOverlay<OverlayItem>{

	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
	Context context;
	public InfoOverlay(Drawable defaultMarker,Context c) {
		super(boundCenterBottom(defaultMarker));
		context = c;
	}

	public void addOverlay(OverlayItem overlay){
		overlays.add(overlay);
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}
	
	public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, false);

	}
	
	//still never got this to work...
    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
    	Log.e("TOUCHEVENT","stop touching me");
    	long systemTime = System.currentTimeMillis();
    	switch (event.getAction()) {
    	case MotionEvent.ACTION_DOWN:
    		if ((System.currentTimeMillis() - systemTime) < 200) {
    			mapView.getController().zoomIn();
    		}
    		systemTime = System.currentTimeMillis();
    		break;
    	}

    	return false;
    }
	

}

