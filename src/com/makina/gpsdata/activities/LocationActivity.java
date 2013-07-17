package com.makina.gpsdata.activities;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.makina.gpsdata.R;
import com.makina.gpsdata.application.GPSData;
import com.makina.gpsdata.utils.Speed;

public abstract class LocationActivity extends TestActivity{

	protected LocationManager mLocationManager;
	protected boolean mIsOn;
	protected int mLocCount;
	protected Location mLocation;
	protected Speed mSpeed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.gps_layout);
		mLocCount = 0;
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	protected void getInfo() {
		try {
			mFileManager.writeDataToFile(getType(), mLocation);
		} catch (IOException e) {
			Toast.makeText(this, "Failed to write on file", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.gps_service:
			i = new Intent(getApplicationContext(), GPSActivity.class);
			i.putExtra("dirName", mDirName);
			i.putExtra("dirPath", mDirPath);
			startActivity(i);
			break;
		case R.id.network_service:
			i = new Intent(getApplicationContext(), NetworkActivity.class);
			i.putExtra("dirName", mDirName);
			i.putExtra("dirPath", mDirPath);
			startActivity(i);
			break;
		case R.id.sensor_service:
			//TODO
			/*i = new Intent(getApplicationContext(), GPSActivity.class);
			i.putExtra("dirName", mDirName);
			i.putExtra("dirPath", mDirPath);
			startActivity(i);*/
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void displayInfos(){
		mLocCount++;
		((TextView) findViewById(R.id.gps_status)).setText("Attempt n° : "+mLocCount);
		((TextView) findViewById(R.id.gps_lat)).setText("Latitude : "+mLocation.getLatitude());
		((TextView) findViewById(R.id.gps_long)).setText("Longitude : "+mLocation.getLongitude());
		((TextView) findViewById(R.id.gps_acc)).setText("Précision : "+mLocation.getAccuracy());
		((TextView) findViewById(R.id.gps_alt)).setText("Altitude : "+mLocation.getAltitude());
	}

	@Override
	protected void stopUpdates() {
		GPSData.getInstance().currentSituation.setLocation(mLocation);
		GPSData.getInstance().currentSituation.setSpeed(mSpeed);
		super.stopUpdates();
	}

	@Override
	protected void startUpdates() {
		mLocation = GPSData.getInstance().currentSituation.getLocation();
		mSpeed = GPSData.getInstance().currentSituation.getSpeed();
		super.startUpdates();
	}
}
