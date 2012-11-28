package com.example.earthquakedatabase;

import java.util.ArrayList;

public class QuakeGroup extends ArrayList<QuakeInfo>{

	/*
	 * object is just an arraylist of QuakeInfo objects...
	 * need this for the array Adapter. 
	 * you are responsible for making sure that the dates 
	 * are all the same. must call set date yourself 
	 */
	
	private String date;
	private ArrayList<QuakeInfo> group;
	
	public QuakeGroup(ArrayList<QuakeInfo> qg){
		
	}
	
	public void addToGroup(QuakeInfo qi){
		group.add(qi);
	}
	
	public void setDate(String date){
		this.date = date;
	}
	
	public String getDate(){
		return date;
	}

	public String toString(){
		return date;
	}
}
