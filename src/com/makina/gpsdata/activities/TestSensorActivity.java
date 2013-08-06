package com.makina.gpsdata.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;

public abstract class TestSensorActivity extends TestActivity {
	
	private Timer mTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
