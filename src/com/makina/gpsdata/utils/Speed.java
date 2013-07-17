package com.makina.gpsdata.utils;

/**
 * A simple class to gather data about speed
 * 
 * @author Guillaume Salmon
 *
 */
public class Speed {
	private float x;
	private float y;
	private float z;
	public Speed (){
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	public Speed (float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public void setSpeed (float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public float getZ() {
		return z;
	}
}
