package com.makina.gpsdata.utils;

import android.location.Location;
import android.os.SystemClock;

/**
 * Gives the latitude, longitude and speed at a given time.
 * 
 * @author Guillaume Salmon
 *
 */
//TODO Using Location instead of this could be a great idea, but without a provider, I cannot instantiate it

public class Situation {
	private double mLatitude;
	private double mLongitude;
	private Long mTime;
	private Speed mSpeed;
	private float mAccuracy;
	
	public Situation (Location l, Speed s){
		mLatitude = l.getLatitude();
		mLongitude = l.getLongitude();
		mAccuracy = l.getAccuracy();
		mSpeed = s;
		setTime();
	}
	
	public Situation (){
		mLatitude = 0;
		mLongitude = 0;
		mAccuracy = 0;
		mSpeed = new Speed();
		setTime();
	}
	
	public Situation (Situation s){
		mLatitude = s.getLatitude();
		mLongitude = s.getLongitude();
		mAccuracy = s.getAccuracy();
		mSpeed = s.getSpeed();
		mTime = s.getTime();
	}

	public Situation (Location l){
		mLatitude = l.getLatitude();
		mLongitude = l.getLongitude();
		mAccuracy = l.getAccuracy();
		mSpeed = new Speed();
		setTime();
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double latitude) {
		this.mLatitude = latitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double longitude) {
		this.mLongitude = longitude;
	}

	public Long getTime() {
		return mTime;
	}

	private void setTime() {
		this.mTime = SystemClock.elapsedRealtime();
	}

	public Speed getSpeed() {
		return mSpeed;
	}

	public void setSpeed(Speed speed) {
		this.mSpeed = speed;
	}
	
	public float getAccuracy() {
		return mAccuracy;
	}
	
	public void setAccuracy(float acc) {
		this.mAccuracy = acc;
	}
}
