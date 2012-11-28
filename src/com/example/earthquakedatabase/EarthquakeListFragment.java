package com.example.earthquakedatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class EarthquakeListFragment extends ListFragment {

	public interface EarthquakeListListener {
		public void onListItemChosen(QuakeInfo q);
		public void onLongClickItemChosen(QuakeInfo q);
	} // end interface

	ArrayAdapter<QuakeInfo> adapter;
	ArrayList<QuakeInfo> earthquakes = new ArrayList<QuakeInfo>();

	private static final String TAG = "EARTHQUAKE";
	private Handler handler = new Handler();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				refreshEarthquakes();
			}
		});
		t.start();

		adapter = new ArrayAdapter<QuakeInfo>(getActivity(),
				android.R.layout.simple_list_item_1, earthquakes);
		setListAdapter(adapter);	
		
		
		ListView lv = getListView();
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v,
					int pos, long id) {
				onLongListItemClick(v,pos,id);
				return false;
			}
			
		});	

	}
	
	
	
	public void onLongListItemClick(View v,int pos, long id){
		QuakeInfo qi = earthquakes.get(pos);
		((Main)getActivity()).onLongClickItemChosen(qi);
		
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		QuakeInfo qi = earthquakes.get(position);
		((Main) getActivity()).onListItemChosen(qi);
	}

	private void addNewQuake(QuakeInfo q) {
		this.earthquakes.add(q);
		this.adapter.notifyDataSetChanged();
		// Add to DB here
		//check to see if in DB already
		boolean add = true;
		((Main)getActivity()).database.open();
		Cursor c = ((Main)getActivity()).database.getDates();
		
		while(c.moveToNext()){
			if(c.getString(0).equalsIgnoreCase(q.getDate().toString())){
				add = false;
			}
				
		}
		
		if(add){
			((Main) getActivity()).database.insertQuakeInfo(q);
			//Log.e("addNewQuake", "added to db");
		}else{
			//Log.e("addNewQuake" ,"didnt add to db");
		}
			
		
		((Main) getActivity()).database.close();

	} // end addNewQuake

	private void refreshEarthquakes() {
		URL url;
		try {
			String quakeFeed = "http://earthquake.usgs.gov/eqcenter/catalogs/1day-M2.5.xml";
			url = new URL(quakeFeed);

			URLConnection connection = url.openConnection();

			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {

				InputStream in = httpConnection.getInputStream();

				/*
				 * note instance of static factory instance method rather than
				 * constructor
				 */
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				Document dom = db.parse(in);
				Element docElem = dom.getDocumentElement();

				earthquakes.clear();

				NodeList nodeList = docElem.getElementsByTagName("entry");
				if (nodeList != null && nodeList.getLength() > 0) {
					for (int i = 0; i < nodeList.getLength(); i++) {
						Element entry = (Element) nodeList.item(i);

						Element title = (Element) entry.getElementsByTagName(
								"title").item(0);
						Element g = (Element) entry.getElementsByTagName(
								"georss:point").item(0);
						Element when = (Element) entry.getElementsByTagName(
								"updated").item(0);
						Element link = (Element) entry.getElementsByTagName(
								"link").item(0);

						String details = title.getFirstChild().getNodeValue();
						String hostName = "http://earthquake.usgs.gov";
						
						String linkString = hostName
								+ link.getAttribute("href");

						String point = g.getFirstChild().getNodeValue();
						String dt = when.getFirstChild().getNodeValue();
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd'T'hh:mm:ss'Z'");
						Date quakeDate = new GregorianCalendar(0, 0, 0)
								.getTime();
						try {
							quakeDate = sdf.parse(dt);
						} catch (ParseException e) {
							Log.d(TAG, "Date Parsing Exception." + e);
						} // end try-catch

						
						
						String[] location = point.split(" ");
						Location l = new Location("dummyGPS");
						l.setLatitude(Double.parseDouble(location[0]));
						l.setLongitude(Double.parseDouble(location[1]));

						/*
						 * why in the world are we taking a substring from 0 to
						 * the end of the string? why not just give it the
						 * string?
						 */
						String magnitudeString = details.split(" ")[1];
						int end = magnitudeString.length() - 1;
						double magnitude = Double.parseDouble(magnitudeString
								.substring(0, end));

						details = details.split(",")[1].trim();

						final QuakeInfo quakeInfo = new QuakeInfo(quakeDate,
								details, l, magnitude, linkString);

						handler.post(new Runnable() {

							@Override
							public void run() {
								addNewQuake(quakeInfo);
							} // end run

						}); // end post new Runnable

					} // end for
				} // end if

			} // end if

		} catch (MalformedURLException e) {
			Log.d(TAG, "MalformedURLException" + e);

		} catch (IOException e) {
			Log.d(TAG, "IOException" + e);

		} catch (SAXException e) {
			Log.d(TAG, "SAXException" + e);

		} catch (ParserConfigurationException e) {
			Log.d(TAG, "ParserConfigurationException" + e);
		} finally {

		} // end finally
	} // end refreshEarthquakes()

} // end class EarthquakeListFragment
