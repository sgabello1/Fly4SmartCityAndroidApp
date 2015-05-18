package com.example.fly4smartcity;

import java.sql.Date;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class GPSTracker extends Service implements LocationListener {

	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude
	double accuracy;
	double time;
	
	boolean internet = false;  
	
	//GPS provider
	private String provider;
	
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 20000; // 1 msec

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPSTracker(Context context) {
		this.mContext = context;
		
	}

	public Location getLocation() {
		try {
			
			
//			turn GPS On 
			
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);
			
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
		    criteria.setPowerRequirement(Criteria. POWER_HIGH);
		    
			
//		     Criteria criteria = new Criteria();
//		        //Use FINE or COARSE (or NO_REQUIREMENT) here
//		        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//		        criteria.setPowerRequirement(Criteria.POWER_HIGH);
//		        criteria.setAltitudeRequired(true);
//		        criteria.setSpeedRequired(true);
//		        criteria.setCostAllowed(true);
//		        criteria.setBearingRequired(true);
//
//		        //API level 9 and up
//		        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
//		        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
//		        criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
//		        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
		    
		    provider = locationManager.getBestProvider(criteria, false);
		    location = locationManager.getLastKnownLocation(provider);
		    		
			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
				//no Wifi
			} else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {
					
					//wifi
					
					internet = true;
					
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						
						location = locationManager
								.getLastKnownLocation(provider);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					
					//GPS
					
//					internet = false;
					
					if (location == null) {
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							
							location = locationManager
									.getLastKnownLocation(provider);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}
	
	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * */
	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(GPSTracker.this);
		}		
	}
	
	/**
	 * Function to get latitude
	 * */
	public double getLatitude(){
		if(location != null){
			latitude = location.getLatitude();
		}
		
		// return latitude
		return latitude;
	}
	
	public double getAccuracy(){
		if(location != null){
			accuracy = location.getAccuracy();
		}
		
		// return latitude
		return accuracy;
	}
	
	@SuppressLint("SimpleDateFormat")
	public String getFrameTime(){
		
		String textTime = null;
		
		if(location != null){
			time = location.getTime();
			Date date = new Date((long) time);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    textTime = sdf.format(date);
		}
		
		// return latitude
		return textTime;
	}
	
	/**
	 * Function to get longitude
	 * */
	public double getLongitude(){
		if(location != null){
			longitude = location.getLongitude();
		}
		
		// return longitude
		return longitude;
	}
	
	
	public boolean getInfo(){
		
		// return internet or GPS boolean
		return internet;
	}
	
	/**
	 * Function to check GPS/wifi enabled
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}
	
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
   	 
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
 
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
 
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
//            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            	mContext.startActivity(intent);
            	startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            

            	
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
	}

	@Override
	public void onProviderDisabled(String provider) {
		
		/* bring up the GPS settings */
		Intent intent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		/* bring up the GPS settings */

		Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void remove(MainActivity mainActivity) {
		// TODO Auto-generated method stub
		
		
	}

}
