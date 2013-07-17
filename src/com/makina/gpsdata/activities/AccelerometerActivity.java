package com.makina.gpsdata.activities;

import java.io.IOException;

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
 * This class returns the values given by the device's accelerometer and log them to a file
 * 
 * @author Guillaume Salmon
 *
 */
public class AccelerometerActivity extends TestActivity implements SensorEventListener{
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private int mCount;
	private float x;
	private float y;
	private float z;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mCount = 0;
		
		setContentView(R.layout.sensor_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			mCount++;
		    x = x+event.values[0];
		    y = y+event.values[1];
		    z = z+event.values[2];
		    ((TextView)findViewById(R.id.sensor_x)).setText("x = "+event.values[0]);
		    ((TextView)findViewById(R.id.sensor_y)).setText("y = "+event.values[1]);
		    ((TextView)findViewById(R.id.sensor_z)).setText("z = "+event.values[2]);
		    ((TextView)findViewById(R.id.sensor_pwr_usage)).setText("Consommation = "+mSensor.getPower()+"mA");
		}
	}

	@Override
	protected void getInfo() {
		x = x/mCount;
		y = y/mCount;
		z = z/mCount;
		
		float [] val = {x, y, z, mCount, mSensor.getPower()};
    	try {
			mFileManager.writeDataToFile(getType(), val);
		} catch (IOException e) {
			Toast.makeText(this, "Failed to write on file", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
    	
    	mCount = 0;
	}

	@Override
	protected void stopUpdates() {
		super.stopUpdates();
		mSensorManager.unregisterListener(this, mSensor);
	}

	@Override
	protected void startUpdates() {
		super.startUpdates();
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected int getType() {
		return FileManager.ACC_TYPE;
	}
}
