package com.example.fly4smartcity;

import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String HTTP = "http address";
	private static final String KEY  = "http";
	protected static final String TAG = null;
	String	httpAddress;
	String response ="";
	String numtel = "<no number>";
	String gps = "<no GPS>";
	String total_history;
	
	ImageView help;
	
	SharedPreferences settings;
	
	//GPS variables
	double lat;
	double lng;
	double accuracy;
	// GPSTracker class
	GPSTracker Gps;
	TextView gpsresult;
	//get context

	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		//GPS stuff
		Gps = new GPSTracker(this);
   	 	Gps.getLocation();
   	 	
   	 	//get phone number
   		Context mAppContext = context;
   		TelephonyManager tMgr =(TelephonyManager)mAppContext .getSystemService(Context.TELEPHONY_SERVICE);
   		numtel = tMgr.getDeviceId();   
   		   
   		//get the saved http address
   		
   		settings = getSharedPreferences(HTTP, Context.MODE_PRIVATE);
   		
   		httpAddress = settings.getString(KEY, "http://163.162.42.113/ffsc/request/?gps=");
		
		//Button for debugging
		
		Button map = (Button) findViewById(R.id.map);
		Button gpsbutton = (Button) findViewById(R.id.gps);
		Button history = (Button) findViewById(R.id.History);
		help = (ImageView) findViewById(R.id.alertbutton);
		
	    gpsresult = (TextView) findViewById(R.id.gpsresult);
		gpsresult.setText("No gps coordinates");
		
		
		//yeah I know that this is the worst way to use button listeners...don't care ;)
		history.setOnClickListener(mGlobal_OnClickListener);
		
		map.setOnClickListener(mGlobal_OnClickListener);
		gpsbutton.setOnClickListener(mGlobal_OnClickListener);
		//help button
		help.setOnClickListener(mGlobal_OnClickListener);
		
		Button httpChange = (Button)findViewById(R.id.http);
		httpChange.setOnClickListener(mGlobal_OnClickListener);
	}
	
	
	//Global On click listener for all views
    final OnClickListener mGlobal_OnClickListener = new OnClickListener() {
        public void onClick(final View v) {
            switch(v.getId()) {
                case R.id.http:
                	HttpChange();
                break;
                case R.id.History:
                	seeHistory();               
                break;
                case R.id.map:
                	visualizeMap();
                    break;
                case R.id.gps:
                	GpsRequest();
                    break;
                case R.id.alertbutton:
                	SendRequest(httpAddress);
                    break;
            }
        }
    };

	void SendRequest(String httpAd){
	// TODO Auto-generated method stub
		
		//when pressed change status button
		help.setImageResource(R.drawable.alert_button_pressed);
		
		 // check if GPS enabled		
        if(Gps.canGetLocation()){
        	
        	 Gps.getLocation();
        	 lat =  Gps.getLatitude();
        	 lng =  Gps.getLongitude();
        	 accuracy = Gps.getAccuracy();
        	 
        	 gps = Double.toString(lat) + "," +Double.toString(lng);
        	 
        	// \n is for new line
        	Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + lat + "\nLong: " + lng + " accuracy " + accuracy + "\n Sending request to server...", Toast.LENGTH_LONG).show();
        	
        	TextView gpsresult = (TextView) findViewById(R.id.gpsresult);
        	
        	gpsresult.setText("LAT " + lat + " LON " + lng + " accuracy " + accuracy);
        	

    		GetRequest  get = new GetRequest(httpAd, numtel, gps, getApplicationContext(), true);
    		
    		get.execute();
    		
    		Toast.makeText(getApplicationContext(), "Help request sent" + gps + "" + numtel,Toast.LENGTH_SHORT).show();
        	
        }else{
        	// can't get location
        	// GPS or Network is not enabled
        	// Ask user to enable GPS/network in settings
        	Gps.showSettingsAlert();
        } 
		
		
	}
    void seeHistory()
    {
    	
    	if(total_history != null){
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);
	 
				// set title
				alertDialogBuilder.setTitle("History");
	 
				// set dialog message
				alertDialogBuilder
					.setMessage(total_history)
					.setCancelable(false)
					.setNegativeButton("Ok",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							// if this button is clicked, just close
							// the dialog box and do nothing
							dialog.cancel();
						}
					});
	 
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it
					alertDialog.show();
			
			
			
		}else
		{
			
        	Toast.makeText(getApplicationContext(), " NO history ! ! ! ", Toast.LENGTH_LONG).show();

		}
	}

    	
    
    
	void visualizeMap()
	{
  
		if(lat != 0 && lng != 0){
			double latitude = lat;
			double longitude = lng;
			String label = "ABC Label";
			String uriBegin = "geo:" + latitude + "," + longitude;
			String query = latitude + "," + longitude + "(" + label + ")";
			String encodedQuery = Uri.encode(query);
			String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
			Uri uri = Uri.parse(uriString);
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
			startActivity(intent);
			}else
			{
				
	        	Toast.makeText(getApplicationContext(), " NO GPS ! ! ! ", Toast.LENGTH_LONG).show();

			}
	}
	
	
	void GpsRequest()
	{
		
		if(Gps.canGetLocation()){
        	
	       	 Gps.getLocation();
	       	 lat =  Gps.getLatitude();
	       	 lng =  Gps.getLongitude();
	       	 accuracy = Gps.getAccuracy();
	       	 boolean internet = Gps.getInfo();
	       	 String time = Gps.getFrameTime();
	       	 
	       	 gps = Double.toString(lat) + "," +Double.toString(lng);
	       	 
//	       	// \n is for new line
	       	Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + lat + "\nLong: " + lng + "\n ", Toast.LENGTH_LONG).show();
	       	
	       	gpsresult = (TextView) findViewById(R.id.gpsresult);
	       	
	       	gpsresult.setText(" LAT " + lat + " LON " + lng + " accuracy " + accuracy + " WIFI = " + internet + " T = " + time);
	       	
	       	if(total_history != null)
	       	total_history = total_history + " LAT " + lat + " LON " + lng + " accuracy " + accuracy + " WIFI = " + internet + "\n";
	       	else
	       		total_history =  " LAT " + lat + " LON " + lng + " accuracy " + accuracy + " WIFI = " + internet + "\n";
	       }else{
	       	// can't get location
	       	// GPS or Network is not enabled
	       	// Ask user to enable GPS/network in settings
	       	Gps.showSettingsAlert();
	       }   
		
		
	}
		
	
	
	
	
	
	void HttpChange(){
		
		// TODO Auto-generated method stub
		
		//alert window to change http index
		//When you click the button, Alert dialog will be showed
        
	       /* Alert Dialog Code Start*/     
	            AlertDialog.Builder alert = new AlertDialog.Builder(context);
	            alert.setTitle("Change http address"); //Set Alert dialog title here
	            
	 
	            // Set an EditText view to get user input 
	            final EditText input = new EditText(context);
	            input.setHint(httpAddress);
	            alert.setView(input);
	 
	            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	             //You will get as string input data in this variable.
	             // here we convert the input to a string and show in a toast.
	            	
	             String str = input.getEditableText().toString();
	            	
	             //here you are
	             httpAddress = str;  
	            	
	           //get the saved http address
	             
	            SharedPreferences.Editor editor = settings.edit();
	            
	     		editor.putString(KEY, httpAddress);
	      		
//	     		httpshow.setText(httpAddress);
	     		
	     		editor.commit();
	     		
	                            
	            } // End of onClick(DialogInterface dialog, int whichButton)
	        }); //End of alert.setPositiveButton
	            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
	              public void onClick(DialogInterface dialog, int whichButton) {
	                // Canceled.
	                  dialog.cancel();
	              }
	        }); //End of alert.setNegativeButton
	            AlertDialog alertDialog = alert.create();
	            alertDialog.show();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
				
		return true;
	}

	@Override
	protected void onResume() {
		/*
		 * onResume is is always called after onStart, even if the app hasn't been
		 * paused
		 *
		 * add location listener and request updates every 1000ms or 10m
		 */
		Gps.getLocation();
		super.onResume();
	}

	@Override
	protected void onPause() {
		/* GPS, as it turns out, consumes battery like crazy */
		Gps.remove(this);
		super.onResume();
	}

}
