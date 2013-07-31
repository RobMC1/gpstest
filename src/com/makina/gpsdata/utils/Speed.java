package com.makina.gpsdata.utils;

/**
 * A simple class to gather data about speed. Unit is m/s.
 * 
 * @author Guillaume Salmon
 *
 */
public class Speed {
	private double x;
	private double y;
	private double z;
	public Speed (){
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	public Speed (double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public void setSpeed (double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getZ() {
		return z;
	}
}
