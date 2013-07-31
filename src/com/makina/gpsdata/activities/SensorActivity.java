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
import android.widget.TextView;

import com.makina.gpsdata.R;
import com.makina.gpsdata.utils.FileManager;
import com.makina.gpsdata.utils.Situation;

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
		mPrevSit = new Situation(mSituation);
		mSituation = new Situation();

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
		    
		    
		    //Apply the rotation matrix to the acceleration values
		    
		    mAccRefT[0] = mAccVal[0]*mR[0] + mAccVal[1]*mR[1] + mAccVal[2]*mR[2];
		    mAccRefT[1] = mAccVal[0]*mR[3] + mAccVal[1]*mR[4] + mAccVal[2]*mR[5];
		    mAccRefT[2] = mAccVal[0]*mR[6] + mAccVal[1]*mR[7] + mAccVal[2]*mR[8];
		    
		    mAccRefTvals.add(mAccRefT);
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
	
	private void computePosition (){
		double dist = 150000.0/6371000.0;
		double brng = Math.toRadians(90);
		double lat1 = Math.toRadians(26.88288045572338);
		double lon1 = Math.toRadians(75.78369140625);

		double lat2 = Math.asin( Math.sin(lat1)*Math.cos(dist) + Math.cos(lat1)*Math.sin(dist)*Math.cos(brng) );
		double a = Math.atan2(Math.sin(brng)*Math.sin(dist)*Math.cos(lat1), Math.cos(dist)-Math.sin(lat1)*Math.sin(lat2));
		System.out.println("a = " +  a);
		double lon2 = lon1 + a;

		lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;

		System.out.println("Latitude = "+Math.toDegrees(lat2)+"\nLongitude = "+Math.toDegrees(lon2));
	}
}
