package com.makina.gpsdata.activities;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.makina.gpsdata.R;
import com.makina.gpsdata.application.GPSData;
import com.makina.gpsdata.utils.Situation;
import com.makina.gpsdata.utils.Speed;

public abstract class LocationActivity extends TestActivity {

	private final int TAB_LENGTH = 5;

	protected LocationManager mLocationManager;
	protected boolean mIsOn;
	protected Speed mSpeed;
	protected Situation mSituation;
	protected Situation mPrevSit;

	private Position mLocations[];
	private int mLocCount;
	private long mStartTime;
	private long mElapsedTime;
	private int mIndice;
	private int mTurn = 0;

	private class Position {
		public double latitude;
		public double longitude;
		public float accuracy;
		public boolean isRelevant;

		public Position(double latitude, double longitude, float accuracy,
				boolean relevant) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.accuracy = accuracy;
			this.isRelevant = relevant;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i = getIntent();
		
		mIsUpdating = i.getBooleanExtra("isRunning", true);
				
		setContentView(R.layout.gps_layout);
		mLocCount = 0;
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		Log.e(getClass().getName(), "updating = "+mIsUpdating);
	}

	@Override
	protected void getInfo() {
		updateElapsedTime();
		getMean();
		displayInfos();
		try {
			mFileManager.writeDataToFile(getType(), mSituation, mElapsedTime);
		} catch (IOException e) {
			Toast.makeText(this, "Failed to write on file", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}
	}
	
	
	protected void computeSpeed(){
		double dist = traveledDist();
		double x = computeDist(mPrevSit.getLatitude(), mPrevSit.getLongitude(), mPrevSit.getLatitude(), mSituation.getLongitude());
		double y = computeDist(mPrevSit.getLatitude(), mPrevSit.getLongitude(), mSituation.getLatitude(), mPrevSit.getLongitude());
		
		//absolute speed is in m/ms
		double absSpeed = dist/(mSituation.getTime()-mPrevSit.getTime());
		
		//speed in m/s on x and y axis
		Speed s = new Speed(1000*absSpeed*x/dist, 1000*absSpeed*y/dist, 0);
		mSituation.setSpeed(s);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.gps_service:
			i = new Intent(getApplicationContext(), GPSActivity.class);
			i.putExtra("dirName", mDirName);
			i.putExtra("dirPath", mDirPath);
			i.putExtra("isRunning", mIsUpdating);
			startActivity(i);
			break;
		case R.id.network_service:
			i = new Intent(getApplicationContext(), NetworkActivity.class);
			i.putExtra("dirName", mDirName);
			i.putExtra("dirPath", mDirPath);
			i.putExtra("isRunning", mIsUpdating);
			startActivity(i);
			break;
		case R.id.sensor_service:
			i = new Intent(getApplicationContext(), SensorActivity.class);
			i.putExtra("dirName", mDirName);
			i.putExtra("dirPath", mDirPath);
			i.putExtra("isRunning", mIsUpdating);
			startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void stopUpdates() {
		GPSData.getInstance().currentSituation = new Situation(mSituation);
		super.stopUpdates();
	}

	@Override
	protected void onResume() {
		if (mIsUpdating){
			startUpdates();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void startUpdates() {
		mLocations = new Position[TAB_LENGTH];
		mLocCount = 0;
		mIndice = 0;
		mTurn = 0;
		mStartTime = SystemClock.uptimeMillis();
		mPrevSit = new Situation(GPSData.getInstance().currentSituation);
		mIsUpdating = true;
		super.startUpdates();
	}

	/**
	 * Displays informations on screen about current values
	 */
	private void displayInfos() {
		mLocCount++;
		((TextView) findViewById(R.id.gps_status)).setText("Essai n° : "
				+ mLocCount + ", " + mElapsedTime + "ms écoulées");
		((TextView) findViewById(R.id.gps_lat)).setText("Latitude : "
				+ mSituation.getLatitude());
		((TextView) findViewById(R.id.gps_long)).setText("Longitude : "
				+ mSituation.getLongitude());
		((TextView) findViewById(R.id.gps_acc)).setText("Précision : "
				+ mSituation.getAccuracy());
	}

	/**
	 * Computes elapsed time between fixes
	 */
	protected void updateElapsedTime() {
		long time = SystemClock.uptimeMillis();
		mElapsedTime = time - mStartTime;
		mStartTime = time;
	}

	/**
	 * 
	 */
	private void getMean() {
		double meanLat = 0;
		double meanLong = 0;
		float meanAcc = 0;
		int cpt = 0;
		if (mLocCount != 0) {
			if (mTurn == 0) {
				for (int i = 0; i < mIndice; i++) {
					if (mLocations[i].isRelevant) {
						meanLat = meanLat + mLocations[i].latitude;
						meanLong = meanLong + mLocations[i].longitude;
						meanAcc = meanAcc + mLocations[i].accuracy;
						cpt++;
					}
				}
				meanLat = meanLat / cpt;
				meanLong = meanLong / cpt;
				meanAcc = meanAcc / cpt;
			} else {
				for (int i = 0; i < TAB_LENGTH; i++) {
					if (mLocations[i].isRelevant) {
						meanLat = meanLat + mLocations[i].latitude;
						meanLong = meanLong + mLocations[i].longitude;
						meanAcc = meanAcc + mLocations[i].accuracy;
						cpt++;
					}
				}
				meanLat = meanLat / TAB_LENGTH;
				meanLong = meanLong / TAB_LENGTH;
				meanAcc = meanAcc / TAB_LENGTH;
			}
			if (mIndice == TAB_LENGTH) {
				mIndice = 0;
				mTurn++;
			}
		}
		mLocations[mIndice] = new Position(mSituation.getLatitude(),
				mSituation.getLongitude(), mSituation.getAccuracy(),
				isValidValue(meanLat, meanLong, meanAcc));
		mIndice++;

		try {
			mFileManager.writeDataToFile(getType(), meanLat, meanLong, meanAcc,
					isValidValue(meanLat, meanLong, meanAcc));
		} catch (IOException e) {
			Toast.makeText(this, "Failed to write on file", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}
	}

	private boolean isValidValue(double meanLat, double meanLong, float meanAcc) {
		if (mLocCount == 0){
			return true;
		}
		return (mSituation.getAccuracy() <= meanAcc);
	}
	
	private double traveledDist() {
	    return computeDist(mPrevSit.getLatitude(), mPrevSit.getLongitude(), mSituation.getLatitude(), mSituation.getLongitude());
	}
	
	private double computeDist(double lat1, double lng1, double lat2, double lng2){
		double earthRadius = 6371000;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);
	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;
	    return dist;
	}
}
