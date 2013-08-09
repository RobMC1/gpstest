package com.makina.gpsdata.activities;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.makina.gpsdata.R;
import com.makina.gpsdata.utils.Situation;
import com.makina.gpsdata.utils.Speed;

/**
 * \/!\ Contains a working base but also a bunch of not working features
 * LocationActivity does all the processing of location data returned by the
 * geolocation based activities
 * 
 * @author Guillaume Salmon
 * 
 */
public abstract class LocationActivity extends TestActivity {

	// Actually useful
	private final int TAB_LENGTH = 5;

	protected boolean mIsOn;
	protected Situation mSituation;

	private long mStartTime;
	private long mElapsedTime;
	private int mLocCount;
	private TextView mStatusView;
	private TextView mLatView;
	private TextView mLongView;
	private TextView mAccView;

	//
	// Used for incoming features or test purpose
	//
	protected Situation mPrevSit; // Is supposed to hold the previous position
									// in order to compute the current speed of
									// the device

	private Position mLocations[]; // Is supposed to hold previous positions in
									// order to know if the current one is valid
									// or absurd. The new values regularly
									// overwrite the old ones.
	private int mIndice; // Is supposed to hold the index of the current
							// location in mLocations
	private int mTurn = 0; // Is supposed to hold the amount how many times
							// mLoction has been totally overwrote with new
							// locations.
	private boolean mShow = false; // Is true when data is wrote to file and
									// indicates that the displayed data should
									// be updated

	// This class class should be replaced by either Location or Situation (see
	// com.makina.gpsdata.utils.Situation;)
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

		setContentView(R.layout.gps_layout);
		mStatusView = (TextView) findViewById(R.id.gps_status);
		mLatView = (TextView) findViewById(R.id.gps_lat);
		mLongView = (TextView) findViewById(R.id.gps_long);
		mAccView = (TextView) findViewById(R.id.gps_acc);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mLocCount = 0;
	}

	@Override
	protected void getInfo() {
		updateElapsedTime();
		mShow = true;
		try {
			mFileManager.writeDataToFile(getType(), mSituation, mElapsedTime);
		} catch (IOException e) {
			Toast.makeText(this, "Failed to write on file", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}
	}

	/**
	 * \/!\ Not working Computes the average speed of the device based on
	 * previous location and actual location.
	 */
	protected void computeSpeed() {
		double dist = traveledDist();
		double x = computeDist(mPrevSit.getLatitude(), mPrevSit.getLongitude(),
				mPrevSit.getLatitude(), mSituation.getLongitude());
		double y = computeDist(mPrevSit.getLatitude(), mPrevSit.getLongitude(),
				mSituation.getLatitude(), mPrevSit.getLongitude());

		// absolute speed in m/ms
		double absSpeed = dist / (mSituation.getTime() - mPrevSit.getTime());

		// speed in m/s on x and y axis
		Speed s = new Speed(1000 * absSpeed * x / dist, 1000 * absSpeed * y
				/ dist, 0);
		mSituation.setSpeed(s);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		// TODO When switching between providers, we should use intents to
		// propagate whether the provider is currently running or not. This way
		// we would'nt have to start manually the new provider.
		switch (item.getItemId()) {
		case R.id.gps_service:
			i = new Intent(getApplicationContext(), GPSActivity.class);
			i.putExtra("dirName", mDirName);
			i.putExtra("dirPath", mDirPath);
			// i.putExtra("isRunning", mIsUpdating);
			startActivity(i);
			break;
		case R.id.network_service:
			i = new Intent(getApplicationContext(), NetworkActivity.class);
			i.putExtra("dirName", mDirName);
			i.putExtra("dirPath", mDirPath);
			// i.putExtra("isRunning", mIsUpdating);
			startActivity(i);
			break;
		case R.id.sensor_service:
			i = new Intent(getApplicationContext(), SensorActivity.class);
			i.putExtra("dirName", mDirName);
			i.putExtra("dirPath", mDirPath);
			// i.putExtra("isRunning", mIsUpdating);
			startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void startUpdates() {
		mLocations = new Position[TAB_LENGTH];
		mLocCount = 0;
		mIndice = 0;
		mTurn = 0;
		mStartTime = SystemClock.uptimeMillis();

		// TODO Check this, it is supposed to hold the last location update even if it was saved by a previous activity
		//mPrevSit = new Situation(GPSData.getInstance().currentSituation);

		mIsUpdating = true;
		super.startUpdates();
	}

	/**
	 * Displays informations on screen about current values
	 */
	protected void displayInfos() {
		if (mShow) {
			mLocCount++;
			mStatusView.setText("Essai n° : " + mLocCount + ", " + mElapsedTime
					+ "ms écoulées");
			mLatView.setText("Latitude : " + mSituation.getLatitude());
			mLongView.setText("Longitude : " + mSituation.getLongitude());
			mAccView.setText("Précision : " + mSituation.getAccuracy());
			mShow = false;
		}
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
	 * 
	 */
	protected void getMean() {
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

	/**
	 * \/!\ To be improved. 
	 * Returns a boolean that indicates if the value is
	 * accurate enough
	 * 
	 * @param meanLat
	 * @param meanLong
	 * @param meanAcc
	 * @return
	 */
	private boolean isValidValue(double meanLat, double meanLong, float meanAcc) {
		if (mLocCount == 0) {
			return true;
		}
		return (mSituation.getAccuracy() <= meanAcc);
	}

	/**
	 * \/!\ Not working Computes the total traveled distance between the
	 * previous location and the current one
	 * 
	 * @return the total traveled distance since the last known location
	 */
	private double traveledDist() {
		return computeDist(mPrevSit.getLatitude(), mPrevSit.getLongitude(),
				mSituation.getLatitude(), mSituation.getLongitude());
	}

	/**
	 * \/!\ Not working Computes the total distance between two geographic
	 * locations
	 * 
	 * @param lat1
	 *            : First location latitude
	 * @param lng1
	 *            : First location longitude
	 * @param lat2
	 *            : Second location latitude
	 * @param lng2
	 *            : Second location longitude
	 * @return total distance between the two locations
	 */
	private double computeDist(double lat1, double lng1, double lat2,
			double lng2) {
		double earthRadius = 6371000;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
				* Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;
		return dist;
	}
}
