package com.makina.gpsdata.activities;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import com.makina.gpsdata.R;
import com.makina.gpsdata.utils.FileManager;

/**
 * This class returns the location given by the device's GPS and log it to a file
 * 
 * @author Guillaume Salmon
 *
 */
public class GPSActivity extends LocationActivity implements LocationListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.gps_layout);
		mLocCount = 0;
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = mLocationManager.getProviders(true);
		for (String provider : providers){
			if (provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
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
		mLocCount++;
		((TextView) findViewById(R.id.gps_status)).setText("Attempt n° : "+mLocCount);
		((TextView) findViewById(R.id.gps_lat)).setText("Latitude : "+location.getLatitude());
		((TextView) findViewById(R.id.gps_long)).setText("Longitude : "+location.getLongitude());
		((TextView) findViewById(R.id.gps_acc)).setText("Précision : "+location.getAccuracy());
		((TextView) findViewById(R.id.gps_alt)).setText("Altitude : "+location.getAltitude());
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
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
			((TextView) findViewById(R.id.gps_status)).setText("GPS on");
		}else{
			((TextView) findViewById(R.id.gps_status)).setText("GPS off");
		}
	}

	@Override
	protected int getType() {
		return FileManager.GPS_TYPE;
	}
}