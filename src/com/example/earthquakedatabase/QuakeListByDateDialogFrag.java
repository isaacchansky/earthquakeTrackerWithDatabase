package com.example.earthquakedatabase;


import java.util.Arrays;

import android.app.Activity;
import android.app.DialogFragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class QuakeListByDateDialogFrag extends DialogFragment{

	private Activity baseActivity;
	
	static QuakeListByDateDialogFrag newInstance(QuakeGroup qg){
		QuakeListByDateDialogFrag f = new QuakeListByDateDialogFrag();
		String quakeList = "";
		String magnitudes = "";
		for(QuakeInfo qi : qg){
			quakeList += qi.getDetails()+"\n";
			magnitudes += qi.getMagnitude()+" ";
		}
		
		Bundle args = new Bundle();
		args.putString("list", quakeList);
		args.putString("date", qg.getDate());
		args.putString("magnitudes", magnitudes);
		f.setArguments(args);
		
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.dbdialogfrag, container);
		
		TextView quakelistc1 = (TextView)v.findViewById(R.id.quake_list_c1);
		TextView quakelistc2 = (TextView)v.findViewById(R.id.quake_list_c2);
		
		String[] datesList = getArguments().getString("list").split("\n");
		String[] magList = getArguments().getString("magnitudes").split(" ");
		
		String theDates="", theMags="";
		
		for(String s: datesList){
			theDates += s+"\n";
		}
		for(String s: magList){
			theMags += s+"\n";
		}
		
		quakelistc1.setText(theDates);
		quakelistc2.setText(theMags);
		
		
		final String[] thedate = new String[1];
		
		//split list in two for 2 column 
		thedate[0] = getArguments().getString("date");
		
		Button confirm = (Button)v.findViewById(R.id.confirm_button);
		confirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//run async task to clear thosedates from db
				clearDateFromDB clear = new clearDateFromDB();
				clear.execute(thedate[0]);
				dismissFrag();
			}
			
		});
		
		
		Button cancel = (Button)v.findViewById(R.id.cancel_button);
		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				dismissFrag();
			}
			
		});
		
		
		return v;
		
	}
	
	@Override
	public void onAttach(Activity a){
		super.onAttach(a);
		baseActivity = a;
	}
	
	public void dismissFrag(){
		this.dismiss();
		
	}
	
	

	/*
	 * Async task to remove from earthquakes of that date from db
	 * ==========================================================================
	 */
	private class clearDateFromDB extends AsyncTask<String,Void,Void>{

		@Override
		protected Void doInBackground(String... date) {
			
			try {
				((Main)baseActivity).database.open();
				
				Cursor aQuake = ((Main)baseActivity).database.getAllQuakes();
				
				while(aQuake.moveToNext()){
					String dbDateString = aQuake.getString(1).substring(0,10);
					if(date[0].equalsIgnoreCase(dbDateString)){
						Log.e("asyncTask QuakeListByDateDialogFrag", "looking at :"+date[0]+" delete the row of date "+dbDateString);
						if(((Main) baseActivity).database.deleteRow(aQuake.getPosition())){
							Log.e("delete row", "Delete was successful");
						}else{
							Log.e("delete row", "Delete unsuccessful");
						}
					}else{
						Log.e("asyncTask QuakeListByDateDialogFrag", "looking at the wrong one");
						//Log.e("date selected",date[0]);
						//Log.e("date in db",dbDateString);
					}
				}
				
				((Main)baseActivity).database.close();
				
			} catch (Exception e) {
				Log.wtf("Nice! I get to use Log.wtf !!!!","something got messed up in the async task... ");
				e.printStackTrace();
			}
			
			return null;
		}
		 
		@Override
		protected void onPostExecute(Void v){
			
		}
		
	}//end asynctask
	
}
