package com.makina.gpsdata.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.makina.gpsdata.R;
import com.makina.gpsdata.utils.FileManager;


/**
 * Display the acceleration applied to the phone in the terrestrial reference
 * 
 * @author guillaume
 *
 */
public class OrientationActivity extends TestSensorActivity implements SensorEventListener {

	private Sensor mSensor;
	private Sensor mMagSensor;
	private Sensor mAccSensor;
	private SensorManager mSensorManager;
	
	private float [] mAccRefT = new float [3];
	private List<float []> mAccRefTvals = new ArrayList<float[]>();
	
	private float [] mMagVal = new float [3];
	private float [] mGravVal = new float [3];
	private float [] mAccVal = new float [3];
	
	private float[] mR = new float[9];
    private float[] mI = new float[9];
	
	private boolean mIsReady;
	private boolean mDisplay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDisplay = false;
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
	protected void getInfo() {
		mDisplay = true;
		
		
		// Compute the mean of the values acquired during the last second
		
		float x = 0;
		float y = 0;
		float z = 0;
		
		for (float [] val : mAccRefTvals){
			x = x + val[0];
			y = y + val[1];
			z = z + val[2];
		}
		
		x = x/mAccRefTvals.size();
		y = y/mAccRefTvals.size();
		z = z/mAccRefTvals.size();
		
		
		// Write the values on the file
		
		float [] val = {x, y, z, mAccRefTvals.size(), mSensor.getPower()};
		
    	try {
			mFileManager.writeDataToFile(getType(), val);
		} catch (IOException e) {
			Toast.makeText(this, "Failed to write on file", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
    	
    	mAccRefTvals.clear();
	}

	@Override
	protected int getType() {
		return FileManager.ORIENTATION_TYPE;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
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
		    

		    // Display values each second 
	        if (mDisplay){
	        	((TextView)findViewById(R.id.sensor_x)).setText("x = "+mAccRefT[0]);
			    ((TextView)findViewById(R.id.sensor_y)).setText("y = "+mAccRefT[1]);
			    ((TextView)findViewById(R.id.sensor_z)).setText("z = "+mAccRefT[2]);
			    ((TextView)findViewById(R.id.sensor_pwr_usage)).setText("Consommation = "+(mAccSensor.getPower()+mMagSensor.getPower()+mSensor.getPower())+"mA");
	        	mDisplay = false;
	        }
	    }
	}
	
	@Override
	protected void stopUpdates() {
		super.stopUpdates();
		mSensorManager.unregisterListener(this, mSensor);
		mSensorManager.unregisterListener(this, mMagSensor);
		mSensorManager.unregisterListener(this, mAccSensor);
	}

	@Override
	protected void startUpdates() {
		super.startUpdates();
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, mMagSensor, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, mAccSensor, SensorManager.SENSOR_DELAY_UI);
	}

}
