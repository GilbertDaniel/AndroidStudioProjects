/***
 * Excerpted from "Hello, Android! 3e",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband3 for more book information.
 ***/
package org.example.locationtest;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class LocationTest extends Activity { 
    
    private static final String TAG = "my Location Test";
    
	// Define human readable names
	private static final String[] ACCURACY = { "invalid", "n/a", "fine", "coarse" };
	private static final String[] POWER = { "invalid", "n/a", "low", "medium",
	"high" };
	private static final String[] STATUS = { "out of service",
		"temporarily unavailable", "available" };
	private static final String[] GPS_EVENTS = {"GPS event started", "GPS event stopped",
		"GPS event first fix", "GPS event satellite status"};

	private LocationManager mgr;
	private ScrollView scrollView;
	private TextView output;
	private String best;
	private GpsStatus gps;
	private PowerManager.WakeLock wakeLock;
	private ArrayList<SimpleLocationListener> mLocationListeners;
    private ToggleButton toggleButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mLocationListeners = new ArrayList<SimpleLocationListener>();
		
		mgr = (LocationManager) getSystemService(LOCATION_SERVICE); 

		output = (TextView) findViewById(R.id.output);
		scrollView = (ScrollView) findViewById(R.id.scroll_view_1);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);

		log("Location providers:");
		dumpProviders(); 

		Criteria criteria = new Criteria(); 
		best = mgr.getBestProvider(criteria, true);
		log("\nBest provider is:   " + best);
		
		
		log("\nLocations (starting with last known):");
		Location location 
		    = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
//		 Location location 
//		    = mgr.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER); 
		
//		 Location location 
//		        = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER); 
		
		dumpLocation(location);
	}

    public void toggleLocationProvider(View v) {
        if(toggleButton.isChecked()) {
            setListener(LocationManager.GPS_PROVIDER);
            showCurrentLocation(LocationManager.GPS_PROVIDER);
        }
        else {
            setListener(LocationManager.NETWORK_PROVIDER);
            showCurrentLocation(LocationManager.NETWORK_PROVIDER);
        }

    }


    private void showCurrentLocation(String locationProvider) {
        Location location
                = mgr.getLastKnownLocation(locationProvider);
        dumpLocation(location);
    }

    private void setListener(String locationProvider) {
        clearListOfListeners();
        SimpleLocationListener sll = new SimpleLocationListener();
        mLocationListeners.add(sll);
        mgr.requestLocationUpdates(locationProvider, 1000, 10, sll);
    }

    private void clearListOfListeners() {
        for(SimpleLocationListener sll : mLocationListeners)
            mgr.removeUpdates(sll);
    }


    @Override
	protected void onResume() {
		super.onResume();
		// GPS OR NETWORK????
		// mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 1, locationListener);
		// provider, update in milliseconds, update in location change, listener
		// mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 200, 10, locationListener);
		
	    //TO SEE NETWORK INFO AND STATUS
        // SimpleLocationListener sll = new SimpleLocationListener();
//        mLocationListeners.add(sll);
//        mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, sll);
//        
        // TO SEE GPS INFO AND STATUS
        // SimpleLocationListener sll = new SimpleLocationListener();
        // mLocationListeners.add(sll);
        // mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, sll);
////        mgr.addGpsStatusListener(gpsStatusListener);

        if(toggleButton.isChecked()) {
            setListener(LocationManager.GPS_PROVIDER);
        }
        else {
            setListener(LocationManager.NETWORK_PROVIDER);
        }


		// keep screen on!
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "LOCATION TEST");
		wakeLock.acquire();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Stop updates to save power while app paused
        clearListOfListeners();
		mgr.removeGpsStatusListener(gpsStatusListener);
		
		// let screen dim!
		wakeLock.release();
	}

	private class SimpleLocationListener implements LocationListener {
	    
		public void onLocationChanged(Location location) {
	        log("\n" + "onLocationChanged CALLED: ");
		    dumpLocation(location);
			Log.d("LocationTest", "Updated Location.");
		}

		public void onProviderDisabled(String provider) {
			log("\nProvider disabled: " + provider);
		}

		public void onProviderEnabled(String provider) {
			log("\nProvider enabled: " + provider);
		}

		public void onStatusChanged(String provider, int status,
				Bundle extras) {
			log("\nProvider status changed: " + provider + ", status="
					+ STATUS[status] + ", extras=" + extras);
		}
	}

	/** Write a string to the output window */
	private void log(String string) {
		output.append(string + "\n");
		int height = scrollView.getChildAt(0).getHeight();
		Log.d(TAG, "scroll view height: " + height);
		scrollView.scrollTo(0, height + 2000);
	}

	/** Write information from all location providers */
	private void dumpProviders() {
		List<String> providers = mgr.getAllProviders();
		for (String provider : providers) {
			dumpProvider(provider);
		}
	}

	/** Write information from a single location provider */
	private void dumpProvider(String provider) {
		LocationProvider info = mgr.getProvider(provider);
		StringBuilder builder = new StringBuilder();
		builder.append("LocationProvider:")
		.append(" name=")
		.append(info.getName())
		.append("\nenabled=")
		.append(mgr.isProviderEnabled(provider))
		.append("\ngetAccuracy=")
		.append(ACCURACY[info.getAccuracy() + 1])
		.append("\ngetPowerRequirement=")
		.append(POWER[info.getPowerRequirement() + 1])
		.append("\nhasMonetaryCost=")
		.append(info.hasMonetaryCost())
		.append("\nrequiresCell=")
		.append(info.requiresCell())
		.append("\nrequiresNetwork=")
		.append(info.requiresNetwork())
		.append("\nrequiresSatellite=")
		.append(info.requiresSatellite())
		.append("\nsupportsAltitude=")
		.append(info.supportsAltitude())
		.append("\nsupportsBearing=")
		.append(info.supportsBearing())
		.append("\nsupportsSpeed=")
		.append(info.supportsSpeed())
		.append("\n\n\n");
		log(builder.toString());
	}

	/** Describe the given location, which might be null */
	private void dumpLocation(Location location) {
		if (location == null)
			log(" ");
		else {
		    log("\n" + location.toString());
		}

	}

	GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			Log.d("Location Test", "gps status changed");
			log("\n-- GPS STATUS HAS CHANGED -- " + "\n" + GPS_EVENTS[event - 1]);
			gps = mgr.getGpsStatus(null);
			showSats(); 
		} 
	};

	private void showSats() {
		int satNum = 0;
		StringBuilder builder = new StringBuilder();
		for(GpsSatellite sat : gps.getSatellites()) {
			builder.append("Satellite Data: ");
			builder.append("\nnumber: ");
			builder.append(satNum);
			builder.append("\nAzimuth: ");
			builder.append(sat.getAzimuth());
			builder.append("\nElevation: ");
			builder.append(sat.getElevation());
			builder.append("\nSNR: ");
			builder.append(sat.getSnr());
			builder.append("\nUsed in fix?: ");
			builder.append(sat.usedInFix());
			log("\n\n" + builder.toString());
			builder.delete(0, builder.length());
			satNum++;
		}
	}

}
