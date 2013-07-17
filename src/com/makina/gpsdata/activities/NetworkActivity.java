package com.makina.gpsdata.activities;

import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import com.makina.gpsdata.R;
import com.makina.gpsdata.utils.FileManager;

/**
 * This class returns the location given by the network and log it to a file
 * 
 * @author Guillaume Salmon
 *
 */
public class NetworkActivity extends LocationActivity implements LocationListener {	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		List<String> providers = mLocationManager.getProviders(true);
		for (String provider : providers){
			if (provider.equalsIgnoreCase(LocationManager.NETWORK_PROVIDER)) {
	            mIsOn = true;
	        }
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onLocationChanged(Location location) {
		mLocation = location;
		displayInfos();
		getInfo();
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	protected void stopUpdates() {
		super.stopUpdates();
		mLocationManager.removeUpdates(this);
	}
	
	@Override
	protected void startUpdates() {
		super.startUpdates();
		if (mIsOn){
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
			((TextView) findViewById(R.id.gps_status)).setText("Network on");
		}else{
			((TextView) findViewById(R.id.gps_status)).setText("Network off");
		}
	}
	
	@Override
	protected int getType() {
		return FileManager.NETWORK_TYPE;
	}
}
