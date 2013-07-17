package com.makina.gpsdata.utils;

import android.location.Location;

/**
 * A class to store a position and a speed
 * 
 * @author Guillaume Salmon
 *
 */

public class Situation {
	private Location mLocation;
	private Speed mSpeed;
	
	public Situation (Location l, float x, float y, float z){
		mLocation = new Location(l);
		mSpeed = new Speed(x, y, z);
	}
	
	public Situation (){
		mLocation = null;
		mSpeed = new Speed();
	}
	
	public Situation (Location l){
		mLocation = new Location(l);
		mSpeed = new Speed();
	}

	public Location getLocation() {
		return mLocation;
	}

	public void setLocation(Location location) {
		this.mLocation = location;
	}

	public Speed getSpeed() {
		return mSpeed;
	}

	public void setSpeed(Speed speed) {
		this.mSpeed = speed;
	}
}
