package com.example.earthquakedatabase;

/*
 * Author: Isaac Chansky
 * Date: 11-11-2012
 * 
 * Here's a lengthy comment which explains all of the exceedingly useless 'features' of my app...
 * 
 * I have essentially 2 "views" (not necessarily in the android sense, but UI views)
 * 1. List fragment of earthquake data pulled from XML & mapview
 * 2. list fragment of dates which we have earthquake data for, & mapview
 * 
 * 
 * "view" 1. EarthquakeListFragment:
 * a. tapping a listview item animates the map to the geopoint and draws a red dot, 
 *    the size of which depends on magnitude. no fancy drawing, just loading a different .png
 * 
 * b. long-pressing a listview item brings up a dialog fragment which displays date, magnitude, location name
 * 
 * c. Every time this listview fragment is added to the frame (whenever you navigate to this "view"),
 *    the xml data is re-parsed and any new earthquakes are added to the database, any existing ones are ignored
 *    to avoid duplicates in the database
 * 
 * 
 * "view" 2. DBInfoFragment:
 * a. tapping a listview item animates the map so you can see the full map, 
 *    and plots all geopoints of earthquakes which occurred on the date of the listview item
 * 
 * b. long-pressing a listview item brings up a dialogfragment which contains a list (not fragment listview thing, 
 *    just text) of all the earthquakes on that date, and gives you an option to delete all earthquakes on that date
 *    from the database.
 * 
 * c. the deleting of the earthquakes from the dialogfragment sort of works... sometimes none are successfully deleted, 
 *    other times all but 4 or 5 are deleted... I have no idea why.
 *    - to see this weird deleting bug in action, use the menu to log the db row length and then try a delete and log again.
 * 
 * 
 *  Other Stuff:
 *  
 *  -button that toggles "views" (switches fragments). is it best practice to use '.add' & '.remove'  or  '.show' & '.hide' ?
 *  -option to totally clear the database in the menu, just drops and recreates the table.
 *  -option to "log" database in the menu as well, but it's pretty lame... just says how many rows are in the database.
 * 
 */


import java.util.List;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Main extends MapActivity implements EarthquakeListFragment.EarthquakeListListener,  DBInfoFragment.EarthquakeDBListListener {

	public QuakeData database;
	public MapView mapView;
	public MapController mapController;

	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        database = new QuakeData(this);     
        
        
        //Set up Map stuff
        String apikey = getString(R.string.map_apikey);
        mapView = new MapView(this, apikey);
        mapController = mapView.getController();
        mapController.setZoom(5);
        
        //fragment stuff...
        final FragmentManager manager = getFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.map_container, new MapFragment());
        transaction.add(R.id.quake_info_container, new EarthquakeListFragment());
        //transaction.add(R.id.db_info_container, new DBInfoFragment());
        transaction.commit();
        
        
        final Button toggleList = (Button)findViewById(R.id.toggleListButton);
        toggleList.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//add or remove fragments based on what the button says... 
				//easier to keep track of that than the state the fragments happen to be in...
				
				final FragmentTransaction button_transaction = manager.beginTransaction();
				
				if(toggleList.getText().toString().equalsIgnoreCase(getString(R.string.toggle_to_db))){
					button_transaction.remove(manager.findFragmentById(R.id.quake_info_container));
					button_transaction.add(R.id.quake_info_container, new DBInfoFragment());
					
					toggleList.setText(R.string.toggle_to_xml);
					
				}else{
					button_transaction.remove(manager.findFragmentById(R.id.quake_info_container));
					button_transaction.add(R.id.quake_info_container, new EarthquakeListFragment());
					
					toggleList.setText(R.string.toggle_to_db);
				}
				
				button_transaction.commit();
				
			}
		});
        
    }
   

	@Override
	public void onListItemChosen(QuakeInfo q) {
		int lat = (int)(q.getLocation().getLatitude()*1e6);
		int lon = (int)(q.getLocation().getLongitude()*1e6);
		double mag = q.getMagnitude();
		mapController.setZoom(7);
		GeoPoint gp = new GeoPoint(lat, lon);
		mapController.animateTo(gp);
		
		List<Overlay> mapOverlay = mapView.getOverlays();
		mapOverlay.clear();
		
		
		int dot;
		//get different drawable img based on magnitude
		if(mag<=1){
			dot = R.drawable.magonedot;
		}else if(mag<=2){
			dot = R.drawable.magtwodot;
		}else if(mag<=3){
			dot = R.drawable.magthreedot;
		}else{
			dot = R.drawable.magfourdot;
		}
		Drawable drawable = this.getResources().getDrawable(dot);
		
		//add overlay 'red dot' to map
		InfoOverlay itemizedoverlay = new InfoOverlay(drawable, this);
		OverlayItem overlayitem = new OverlayItem(gp, "Earthquake", "It's an earthquake!");
		itemizedoverlay.addOverlay(overlayitem);
		mapOverlay.add(itemizedoverlay);
		
	}
	
	
	@Override
	public void onDBItemChosen(QuakeGroup qg){
		List<Overlay> mapOverlay = mapView.getOverlays();
		//take care of any existing overlay dots recently added to the map...
		mapOverlay.clear();
		
		int lat;
		int lon;
		GeoPoint gp;
		
		Drawable dot = this.getResources().getDrawable(R.drawable.magonedot);
		
		InfoOverlay dotItem = new InfoOverlay(dot, this);

		for(QuakeInfo q : qg){
			lat = q.getLatitude();
			lon = q.getLongitude();
			gp  = new GeoPoint(lat, lon);
			
			OverlayItem overlayitem = new OverlayItem(gp, "Earthquake", "It's an earthquake!");
			dotItem.addOverlay(overlayitem);
		}
	
		mapOverlay.add(dotItem);
		mapController.setZoom(3);
		mapController.setCenter(new GeoPoint(0,0));
	}


	@Override
	public void onLongClickDBItemChosen(QuakeGroup qg) {
		
		final FragmentManager m = getFragmentManager();
		FragmentTransaction transaction = m.beginTransaction();
		QuakeListByDateDialogFrag fragment = QuakeListByDateDialogFrag.newInstance(qg);
		transaction.add(fragment, "dbdialogfrag");
		transaction.show(fragment);
		transaction.commit();
		
	}


	@Override
	public void onLongClickItemChosen(QuakeInfo q) {
		final FragmentManager m = getFragmentManager();
		FragmentTransaction transaction = m.beginTransaction();
		LongPressDialogFragment fragment = LongPressDialogFragment.newInstance(q.getDetails(), ""+q.getMagnitude(), q.getDate().toString());
		transaction.add(fragment, "dialogFrag");
		transaction.show(fragment);
		transaction.commit();
	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.log_db_info:
	            database.logDBInfo();
	            return true;
	        case R.id.clear_db:
	        	database.clearDatabase();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
    
}
