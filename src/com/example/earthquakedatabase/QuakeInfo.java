package com.example.earthquakedatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.location.Location;

public class QuakeInfo {

	private Date date;
	private String details;
	private Location location;
	private double magnitude;
	private String link;
	
	private String dateString;
	private int latitude;
	private int longitude;
	/*
	 * I need two different quakeinfo objects. one (w/ date object) because that's how I started this.
	 * and on with date as a string because I am recreating objects from database info... and strings are easier
	 */
	
	public QuakeInfo(Date date, String details, Location location, double magnitude, String link) {
		
		this.date = date;
		this.details = details;
		this.location = location;
		this.magnitude = magnitude;
		this.link = link;
	} // end constructor()

	public QuakeInfo(String dateString, double magnitude, String details, int latitude, int longitude) {
		
		this.dateString = dateString;
		this.magnitude = magnitude;
		this.details = details;
		this.latitude = latitude;
		this.longitude = longitude;
	} // end constructor()
	
	public Date getDate() {
		return date;
	}

	public String getDetails() {
		return details;
	}

	public Location getLocation() {
		return location;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public String getLink() {
		return link;
	}
	
	public int getLatitude(){
		return latitude;
	}

	public int getLongitude(){
		return longitude;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
		String dateString = sdf.format(this.date);
		return  this.details;
	} // end toString()
	
	public String infoString(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sdf.format(this.date);
		return  "DATE: "+dateString+" | MAG: "+this.magnitude+" | LOC: "+this.details;
	
	}

	
} // end class QuakeInfo
