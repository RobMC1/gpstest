package com.makina.gpsdata.application;

import android.app.Application;

import com.makina.gpsdata.utils.Situation;

public class GPSData extends Application {
	
	private static GPSData singleton = null;
	public Situation currentSituation;
	public boolean currentlyRunning;

    public static GPSData getInstance() {
        return singleton;
    }
	
	@Override
	public void onCreate() {
		currentSituation = new Situation();
		currentlyRunning = false;
		singleton = this;
		//PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		super.onCreate();
	}
	
}
