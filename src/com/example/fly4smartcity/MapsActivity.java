package com.example.fly4smartcity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MapsActivity extends Activity implements  LocationListener{

	// Google Map
    private GoogleMap googleMap;
    MapView mapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.maps, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
