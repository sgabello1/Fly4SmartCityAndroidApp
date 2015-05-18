package com.example.fly4smartcity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class GetRequest extends AsyncTask implements LocationListener{

	String httpAddress;
	String numtel;
	String gps;
	Context context;
	String retSrc = "none for now";
	boolean getOrPost;
	public GetRequest(String http, String num, String g, Context c, boolean gop) {
		// TODO Auto-generated constructor stub
		this.httpAddress = http;
		this.numtel = num;
		this.gps = g;
		this.context = c;
		this.getOrPost = gop; //if it s true get otherwise post request
		
	}

	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub
		
		 try {
				GoGetIt();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return null;
	}

	@Override
	protected void onPostExecute(Object result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
        Toast.makeText(context, retSrc,Toast.LENGTH_LONG).show(); 
		
	}
	
	private void GoGetIt() throws URISyntaxException, ClientProtocolException, IOException, JSONException {
		// TODO Auto-generated method stub

		if(getOrPost){
		HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet();
//        request.setURI(new URI(httpAddress));
//        request.setURI(new URI("http://163.162.42.113/ffsc/request/?gps=45.1121957,7.671746300000001&cell=3389575351"));
        
        //faccio il furbo
        // http://163.162.42.113/ffsc/request/?gps=
        // lat +, long & cell= + numtel
        
        String urlo = httpAddress + gps + "&cell=" + numtel;
        request.setURI(new URI(urlo));
        
        HttpResponse res= client.execute(request);
        //qui gli faccio la richiesta ma non so ancora che parametri ci metto e se è GET o POST
         
        HttpEntity entity = res.getEntity();
        
        
        if (entity != null) {
	            retSrc = EntityUtils.toString(entity); 
	          //parsing JSON
	           
	           JSONObject result = new JSONObject(retSrc); //Convert String to JSON Object
	           
	           	// writing response to log
	 			Log.d("Http Response:", retSrc);
	 			
	        }
		}else{
			
			// Creating HTTP client
	        HttpClient httpClient = new DefaultHttpClient();
	        // Creating HTTP Post
	        HttpPost httpPost = new HttpPost("http://163.162.42.113/ffsc/request/");
	 
	        // Building post parameters
	        // key and value pair
	        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
	        
	        nameValuePair.add(new BasicNameValuePair("gps", "45.1121957,7.671746300000001"));
	        nameValuePair.add(new BasicNameValuePair("cell", "3389575351"));
	 
	        // Url Encoding the POST parameters
	        try {
	        	
	            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
	            
	        } catch (UnsupportedEncodingException e) {
	            // writing error to Log
	            e.printStackTrace();
	        }
	        
	        // Making HTTP Request
	        try {
	            HttpResponse response = httpClient.execute(httpPost);
	 
	            HttpEntity entity2 = response.getEntity();
	            
	            if(entity2 != null)
	            	{
	            	
//	             retSrc = "here"; 
	            	retSrc = EntityUtils.toString(entity2); 
	            	JSONObject result = new JSONObject(retSrc); //Convert String to JSON Object
	            	}
	            // writing response to log
	            Log.d("Http Response:", response.toString());
	        } catch (ClientProtocolException e) {
	            // writing exception to log
	            e.printStackTrace();
	        } catch (IOException e) {
	            // writing exception to log
	            e.printStackTrace();
	        }
	        
	     // writing response to log
 			Log.d("Http Response:", retSrc);
	        
		}//else
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	

}
