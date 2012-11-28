package com.example.earthquakedatabase;

import java.util.ArrayList;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;


public class DBInfoFragment extends ListFragment {

	
	public interface EarthquakeDBListListener {
		public void onDBItemChosen(QuakeGroup qg);
		public void onLongClickDBItemChosen(QuakeGroup qg);
	}// end interface

	ArrayAdapter<QuakeGroup> adapter;
	ArrayList<QuakeGroup> earthquakeGroups = new ArrayList<QuakeGroup>();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
		// set up long click stuff
		OnItemLongClickListener listener = new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
				onLongItemClick(v,pos,id);
				return false;
			}
		};
		
		getListView().setOnItemLongClickListener(listener);
		
		adapter = new ArrayAdapter<QuakeGroup>(getActivity(),
				android.R.layout.simple_list_item_1, earthquakeGroups);
		setListAdapter(adapter);
		adapter.setNotifyOnChange(true);
		
		//async task to group earthquakes by date
		GroupQuakes makeGroups = new GroupQuakes();
		makeGroups.execute();
	}
	
	
	public void onLongItemClick(View v,int pos, long id){
		QuakeGroup qg = earthquakeGroups.get(pos);
		((Main)getActivity()).onLongClickDBItemChosen(qg);
	}
	
	
	public void onListItemClick(ListView l, View v, int position, long id) {
		QuakeGroup group = earthquakeGroups.get(position);
		((Main) getActivity()).onDBItemChosen(group);
	}

	
	/*
	 * Async task to populate list and create groupings
	 * ==========================================================================
	 */

	private class GroupQuakes extends AsyncTask<Void, Void, ArrayList<QuakeGroup>>{

		@Override
		protected ArrayList<QuakeGroup> doInBackground(Void... params) {
			ArrayList<QuakeGroup> quakeGroupList = new ArrayList<QuakeGroup>();
			
			// use cursor to get at the database
			// for each unique date, create a new quakeGroup and add all of the
			// dates to it.
			((Main) getActivity()).database.open();
			Cursor currentQuake = ((Main) getActivity()).database.getAllQuakes();
			
			// parse each date, if unique date(day) start a new group
			while (currentQuake.moveToNext()) {
				// only care about separating dates by day, not time.
				// substring returns "WEEKDAY MONTH NUMBER"
				String fullDateString = currentQuake.getString(1);
				String dateString = fullDateString.substring(0, 10);
				Log.e("GroupQuakes AsyncTask","current Quake date is "+dateString);
				boolean gotAddedToGroup = false;

				// create a new quakeInfo object to add to the arraylist...
				double mag = Double.parseDouble(currentQuake.getString(2));
				String details = currentQuake.getString(3);
				int lat = (int) Double.parseDouble((currentQuake.getString(4)));
				int lon = (int) Double.parseDouble(currentQuake.getString(5));
				QuakeInfo quakeFromDB = new QuakeInfo(dateString, mag, details,lat, lon);

				for(QuakeGroup group : quakeGroupList){
					String groupDate = group.getDate().substring(0,10);
					if(groupDate.equalsIgnoreCase(dateString)){
						group.add(quakeFromDB);
						gotAddedToGroup = true;
						Log.e("GroupQuakes AsyncTask", "found a group, added");
					}
				}
				
				//if we never added it, then create a new quakeGroup
				if(!gotAddedToGroup){
					QuakeGroup qg = new QuakeGroup(new ArrayList<QuakeInfo>());
					qg.setDate(dateString);
					qg.add(quakeFromDB);
					quakeGroupList.add(qg);
					Log.e("GroupQuakes AsyncTask", "created a new group");
				}

				//Log.e("GroupQuakes AsyncTask", "now we have "+earthquakeGroups.size()+" groups");

			}// end adding quakes to groups

			((Main) getActivity()).database.close();
			
			return quakeGroupList;
		}//end doInBackground
		
		
		@Override
		protected void onPostExecute(ArrayList<QuakeGroup> earthquakeGroupList) {
			
			for(QuakeGroup g : earthquakeGroupList){
				Log.e("onPostExecute", "group: "+g.getDate());
				//adapter.add(g);
				earthquakeGroups.add(g);
			}
			
			adapter.notifyDataSetChanged();
			//Log.e("onPostExecute","changed data set");
			//Log.e("onPostExecute", "adapter length is : "+adapter.getCount());
			
		}
	}
	
}
