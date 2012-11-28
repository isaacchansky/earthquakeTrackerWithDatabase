package com.example.earthquakedatabase;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class LongPressDialogFragment extends DialogFragment {

	/*
	 * Dialog Fragment is for long presses on the Earthquake data from XML fragment...
	 */
	
	private TextView details, magnitude, date;
	
	static LongPressDialogFragment newInstance(String details, String magnitude, String date) {
        LongPressDialogFragment f = new LongPressDialogFragment();
        
        //bundle data for the dialog fragment
        Bundle args = new Bundle();
        args.putString("details", details);
        args.putString("magnitude",magnitude);
        args.putString("date", date);
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
		
		View v = inflater.inflate(R.layout.dialogfrag, container);
		
		details = (TextView)v.findViewById(R.id.details_text_view);
		details.setText(getArguments().getString("details"));
		magnitude = (TextView)v.findViewById(R.id.magnitude_text_view);		
		magnitude.setText(getArguments().getString("magnitude"));
		date = (TextView)v.findViewById(R.id.date_text_view);
		date.setText(getArguments().getString("date"));
		
		Button dismissButton = (Button)v.findViewById(R.id.dismiss_button);
		dismissButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				dismissFrag();
			}
			
		});
		
		
		return v;
	}
	
	public void dismissFrag(){
		this.dismiss();
	}
	
}

