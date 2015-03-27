package com.cookbook.SMSresponser;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SMSResponser extends Activity {

	private static final String serviceName = "com.cookbook.SMSresponser.ResponserService";
	private static final String TAG = "SMS App";
	
	private TextView tv1;
	private EditText ed1;
	private SharedPreferences myPrefs;
	private String reply;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG,"In onCreate method");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		myPrefs 
		    = PreferenceManager.getDefaultSharedPreferences(this);
		tv1 = (TextView) this.findViewById(R.id.display);
		ed1 = (EditText) this.findViewById(R.id.editText);      
	}

	private void startMyService() {
		Log.v(TAG,"In startMyService method");
		boolean running = isMyServiceRunning();
		Log.d(TAG, "running: " + running);
		if(!running) {
			try {
				// start Service
				Intent svc = new Intent(this, ResponserService.class);
				startService(svc);	
			}	
			catch (Exception e) {
				Log.e("onCreate", "service creation problem", e);
			}    	
		}
	}

	public void onResume() {
		super.onResume();
		Log.v(TAG, "in onResume");
		reply = myPrefs.getString("reply", "Thank you " +
				"for your message. " +
				"I am busy now. I will call you later");
		tv1.setText(reply);
		ed1.setHint(reply);
		// startMyService();
	}
	
	public void changeAutoReplyText(View v) {
		Log.v(TAG, "in change respond text");
		reply = ed1.getText().toString();
		if(reply.length() == 0)
			reply = "Thank you for your message. " +
					"I am busy now. " + 
					"I will call you later.";
		tv1.setText(reply);
        SharedPreferences.Editor updater = myPrefs.edit();
		updater.putString("reply", reply);
        updater.apply();
	}

	public void startAutoRespond(View v) {
		Log.v(TAG, "in start auto respond ");
		startMyService();
	}

	public void stopAutoRespond(View v) {
		Log.d(TAG, "in stop auto respond");
		Intent svc = new Intent(this, ResponserService.class);
		boolean stopResult = stopService(svc);	
		Log.d(TAG, "stop service result: " + stopResult);
	}

	private boolean isMyServiceRunning() {
		Log.v(TAG, "checking if service is running");
	    ActivityManager manager 
	    	= (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    boolean result = false;
	    // log all services to demo in class
	    for (RunningServiceInfo service 
	    			: manager.getRunningServices(Integer.MAX_VALUE)) {
	        Log.v(TAG, "Running service: " + service.service.getClassName());
	    	if (serviceName.equals(service.service.getClassName())) {
	            result = true; 
	        }
	    }
	    return result;
	}
}