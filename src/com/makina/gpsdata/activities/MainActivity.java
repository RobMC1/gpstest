package com.makina.gpsdata.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.makina.gpsdata.R;



public class MainActivity extends Activity {
	
	private String mDirName;
	private String mDirPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button launchGPS = (Button) findViewById(R.id.launch_gps);
		mDirName = "TestGeoloc";
		mDirPath = Environment.getExternalStorageDirectory().toString();
		launchGPS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), GPSActivity.class);
				i.putExtra("dirName", mDirName);
				i.putExtra("dirPath", mDirPath);
				i.putExtra("isRunning", false);
				startActivity(i);
			}
		});
		Button launchNetwork = (Button) findViewById(R.id.launch_network);
		launchNetwork.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), NetworkActivity.class);
				i.putExtra("dirName", mDirName);
				i.putExtra("dirPath", mDirPath);
				i.putExtra("isRunning", false);
				startActivity(i);
			}
		});
		Button launchGyro = (Button) findViewById(R.id.launch_gyro);
		launchGyro.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), GyroActivity.class);
				i.putExtra("dirName", mDirName);
				i.putExtra("dirPath", mDirPath);
				startActivity(i);
			}
		});
		Button launchAcc = (Button) findViewById(R.id.launch_acc);
		launchAcc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), AccelerometerActivity.class);
				i.putExtra("dirName", mDirName);
				i.putExtra("dirPath", mDirPath);
				startActivity(i);
			}
		});
		Button launchGravity = (Button) findViewById(R.id.launch_gravity);
		launchGravity.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), GravityActivity.class);
				i.putExtra("dirName", mDirName);
				i.putExtra("dirPath", mDirPath);
				startActivity(i);
			}
		});
		Button launchOrientation = (Button) findViewById(R.id.launch_orientation);
		launchOrientation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), OrientationActivity.class);
				i.putExtra("dirName", mDirName);
				i.putExtra("dirPath", mDirPath);
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		
		return super.onOptionsItemSelected(item);
	}
}
