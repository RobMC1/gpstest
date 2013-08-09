package com.makina.gpsdata.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.makina.gpsdata.R;
import com.makina.gpsdata.utils.FileManager;
import com.makina.gpsdata.utils.Situation;
import com.makina.gpsdata.utils.Speed;


/**
 * 
 * ***** WARNING *****
 * * NOT WORKING YET *
 * *******************
 * 
 * This activity allows to locate the phone using a start position and a start speed given by either the GPSActivity or the NetworkActivity, and from then only uses the phone accelerometer, magnetic field sensor and the gravity sensor to find the current position.
 * 
 * Approximations : No difference is made between magnetic north and geographic north and the earth is considered as perfectly round.
 * 
 * @author guillaume
 *
 */
public class SensorActivity extends LocationActivity implements SensorEventListener {
	
	private Sensor mSensor;
	private Sensor mMagSensor;
	private Sensor mAccSensor;
	private SensorManager mSensorManager;
	
	private float [] mMagVal = new float [3];
	private float [] mGravVal = new float [3];
	private float [] mAccVal = new float [3];
	
	private float[] mR = new float[9];
    private float[] mI = new float[9];
	
    private float [] mAccRefT = new float [3];
	private List<float []> mAccRefTvals = new ArrayList<float[]>();
	private float [] mAcceleration = new float [3];
    
	private boolean mIsReady;
	
	private Timer mTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		for (int i = 0; i<3; i++){
			mAccRefT[i] = 0;
		}
		setContentView(R.layout.sensor_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		mMagSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
	protected void stopUpdates() {
		mTimer.cancel();
		mSensorManager.unregisterListener(this, mSensor);
		mSensorManager.unregisterListener(this, mMagSensor);
		mSensorManager.unregisterListener(this, mAccSensor);
		super.stopUpdates();
	}
	
	@Override
	protected void startUpdates() {
		mTimer = new Timer();
		TimerTask updateInfo = new InfoUpdater();
		mTimer.scheduleAtFixedRate(updateInfo, 1000, 1000);
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, mMagSensor, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, mAccSensor, SensorManager.SENSOR_DELAY_UI);
		super.startUpdates();
	}
	
	@Override
	protected int getType() {
		return FileManager.NETWORK_TYPE;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
	
	@Override
	protected void getInfo() {
		
		// TODO We should retrieve the last know location using
		// GPSData.getInstance().currentSituation then save
		// the new one in case we switch to another activity later
				
		// mPrevSit = new Situation(GPSData.getInstance().currentSituation);

		mSituation = new Situation();
		computeMeanAcc();
		double dist = getDist();
		double bearing = getBearing();
		computePosition(bearing, dist);
		
		// GPSData.getInstance().currentSituation = new Situation(mSituation);
		
		super.getInfo();
	}
	
	/**
	 * 
	 * @return bearing from magnetic north direction
	 */
	private double getBearing() {
		return Math.atan(mAcceleration[0]/mAcceleration[1])-(Math.PI/2);
	}
	
	/**
	 * 
	 * @return absolute distance between the previous recorded location and the current location
	 */
	private double getDist() {
		Speed s = new Speed(mPrevSit.getSpeed().getX()+mAcceleration[0], mPrevSit.getSpeed().getY()+mAcceleration[1], 0);
		double dist = Math.sqrt(Math.pow(s.getX(), 2)+Math.pow(s.getY(), 2));
		return dist;
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
	    case Sensor.TYPE_MAGNETIC_FIELD:
	        mMagVal = event.values.clone();
	        mIsReady = true;
	        break;
	    case Sensor.TYPE_GRAVITY:
	        mGravVal = event.values.clone();
	        break;
	    case Sensor.TYPE_ACCELEROMETER:
	    	mAccVal = event.values.clone();
		}   
		
	    if (mMagVal != null && mGravVal != null && mAccVal != null && mIsReady) {
	    	 
	    	
	    	//Compute the rotation matrix using the magnetic field sensor and the gravity sensor
	    	
	        mIsReady = false;

	        SensorManager.getRotationMatrix(mR, mI, mGravVal, this.mMagVal);
		    
		    for (int i=0; i<3; i++){
		    	mAccRefT[i]=0;
		    }
		    
		    
		    //Ignore the acceleration due to gravity
		    
		    mAccVal[0] = mAccVal[0]-mGravVal[0];
		    mAccVal[1] = mAccVal[1]-mGravVal[1];
		    mAccVal[2] = mAccVal[2]-mGravVal[2];
		    
		    
		    //Apply the rotation matrix to the acceleration values
		    
		    mAccRefT[0] = mAccVal[0]*mR[0] + mAccVal[1]*mR[1] + mAccVal[2]*mR[2];
		    mAccRefT[1] = mAccVal[0]*mR[3] + mAccVal[1]*mR[4] + mAccVal[2]*mR[5];
		    mAccRefT[2] = mAccVal[0]*mR[6] + mAccVal[1]*mR[7] + mAccVal[2]*mR[8];
		    
		    mAccRefTvals.add(mAccRefT);
		    

			displayInfos();
	    }
	}
	
	/**
	 * Is called by the timer regularly and makes the data being wrote to file.
	 * 
	 * @author Guillaume Salmon
	 *
	 */
	private class InfoUpdater extends TimerTask {
		public void run() {
			getInfo();
		}
	}
	
	/**
	 * Computes the mean acceleration during the last second on the x, y and z axis
	 */
	private void computeMeanAcc(){
		mAcceleration[0] = 0;
		mAcceleration[1] = 1;
		mAcceleration[2] = 2;
		for (float [] acc : mAccRefTvals){
			for (int i =0; i<3; i++){
				mAcceleration[i] = mAcceleration[i] + acc[i];
			}
		}
		for (int i = 0; i<3; i++){
			mAcceleration[i] = mAcceleration[i]/mAccRefTvals.size();
		}
		mAccRefTvals.clear();
	}
	
	/**
	 * 
	 * @param brng : bearing from the magnetic north direction
	 * @param d : absolute distance from the last recorded location 
	 */
	private void computePosition (double brng, double d){
		double dist = d/6371000.0;
		double lat1 = Math.toRadians(mPrevSit.getLatitude());
		double lon1 = Math.toRadians(mPrevSit.getLongitude());

		mSituation.setLatitude(Math.asin( Math.sin(lat1)*Math.cos(dist) + Math.cos(lat1)*Math.sin(dist)*Math.cos(brng) ));
		double a = Math.atan2(Math.sin(brng)*Math.sin(dist)*Math.cos(lat1), Math.cos(dist)-Math.sin(lat1)*Math.sin(mSituation.getLatitude()));
		System.out.println("a = " +  a);
		double lon2 = lon1 + a;

		mSituation.setLongitude((lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI);

		System.out.println("Latitude = "+Math.toDegrees(mSituation.getLatitude())+"\nLongitude = "+Math.toDegrees(mSituation.getLongitude()));
	}
}
