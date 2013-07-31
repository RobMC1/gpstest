package com.makina.gpsdata.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;

public class TestSensorActivity extends TestActivity {
	
	private Timer mTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIsUpdating = false;
	}

	@Override
	protected void stopUpdates() {
		mTimer.cancel();
		super.stopUpdates();
	}

	@Override
	protected void startUpdates() {
		mTimer = new Timer();
		TimerTask updateInfo = new InfoUpdater();
		mTimer.scheduleAtFixedRate(updateInfo, 1000, 1000);
		
		super.startUpdates();
	}

	@Override
	protected void getInfo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getType() {
		// TODO Auto-generated method stub
		return 0;
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

}
