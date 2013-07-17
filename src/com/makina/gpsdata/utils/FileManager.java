package com.makina.gpsdata.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import android.location.Location;


public class FileManager {
	
	public static final int GPS_TYPE = 0;
	public static final int NETWORK_TYPE = 1;
	public static final int GYRO_TYPE = 2;
	public static final int ACC_TYPE = 3;
	public static final int GRAVITY_TYPE = 4;
	
	private File mGPSout;
	private File mNetworkOut;
	private File mGyroOut;
	private File mAccOut;
	private File mGravityOut;
	private String mDirPath;
	private String mDirName;

	public FileManager(String dirPath, String dirName) throws Exception {
		mDirName = dirName;
		mDirPath = dirPath;
		createFiles();
	}

	private void createFiles () throws Exception{
		
		File dir = new File(mDirPath, mDirName);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                RuntimeException e =
                        new RuntimeException("Cannot create directory: "
                                + mDirName);
                throw e;
            }
        } else {
            if (!dir.isDirectory()) {
                RuntimeException e =
                        new RuntimeException(mDirName
                                + " exists, but is not a directory");
                throw e;
            }
        }
        mDirPath = dir.getAbsolutePath();
		
		mGPSout = new File(mDirPath, "GPSOutput.txt");
		mNetworkOut = new File(mDirPath, "NetworkOutput.txt");
		mGyroOut = new File(mDirPath, "GyroOutput.txt");
		mAccOut = new File(mDirPath, "AccOutput.txt");
		mGravityOut = new File(mDirPath, "GravityOutput.txt");
		
		
		if(!mGPSout.exists()){
			mGPSout.createNewFile();
		}
		if(!mNetworkOut.exists()){
			mNetworkOut.createNewFile();
		}
		if(!mGyroOut.exists()){
			mGyroOut.createNewFile();
		}
		if(!mAccOut.exists()){
			mAccOut.createNewFile();
		}
		if(!mGravityOut.exists()){
			mGravityOut.createNewFile();
		}
	}
	
	public boolean writeDataToFile (int type, Location loc) throws IOException{
		FileWriter fileWritter;
		switch (type){
		case GPS_TYPE:
			fileWritter = new FileWriter(mGPSout.getAbsolutePath(),true);
			break;
		case NETWORK_TYPE:
			fileWritter = new FileWriter(mNetworkOut.getAbsolutePath(),true);
			break;
		default:
			return false;
		}
		String text = "\n\n";
		text = text+Calendar.getInstance().getTime().toString()+"\n"+formatData(loc);
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        bufferWritter.write(text);
        bufferWritter.close();
        return true;
	}
	
	public boolean writeDataToFile (int type, float [] valeurs) throws IOException{
		FileWriter fileWritter;
		switch (type){
		case GYRO_TYPE:
			fileWritter = new FileWriter(mGyroOut.getAbsolutePath(),true);
			break;
		case ACC_TYPE:
			fileWritter = new FileWriter(mAccOut.getAbsolutePath(),true);
			break;
		case GRAVITY_TYPE:
			fileWritter = new FileWriter(mGravityOut.getAbsolutePath(),true);
			break;
		default:
			return false;
		}
		String text = "\n\n";
		text = text+Calendar.getInstance().getTime().toString()+"\n"+formatData(valeurs);
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        bufferWritter.write(text);
        bufferWritter.close();
        return true;
	}
	
	public boolean writeDataToFile (int type, boolean valeurs[], int brightness, float batPerc) throws IOException{
		FileWriter fileWritter;
		String text = "\n\n\n*********************";
		switch (type){
		case GYRO_TYPE:
			text = text+"\nGyroscope : ";
			fileWritter = new FileWriter(mGyroOut.getAbsolutePath(),true);
			break;
		case ACC_TYPE:
			text = text+"\nAccéléromètre : ";
			fileWritter = new FileWriter(mAccOut.getAbsolutePath(),true);
			break;
		case GRAVITY_TYPE:
			text = text+"\nGravité : ";
			fileWritter = new FileWriter(mGravityOut.getAbsolutePath(),true);
			break;
		case GPS_TYPE:
			text = text+"\nGPS : ";
			fileWritter = new FileWriter(mGPSout.getAbsolutePath(),true);
			break;
		case NETWORK_TYPE:
			text = text+"\nNetwork : ";
			fileWritter = new FileWriter(mNetworkOut.getAbsolutePath(),true);
			break;
		default:
			return false;
		}
		text = text+Calendar.getInstance().getTime().toString()+"\n"+formatData(valeurs)+"\nLuminosité de l'écran : "+brightness+"\nPourcentage Batterie : "+(batPerc*100)+"%";
		text = text+"\n*********************";
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        bufferWritter.write(text);
        bufferWritter.close();
        return true;
	}
	
	public boolean writeDataToFile (int type, float batPerc) throws IOException{
		FileWriter fileWritter;
		String text = "\n\n*********************\n";
		switch (type){
		case GYRO_TYPE:
			fileWritter = new FileWriter(mGyroOut.getAbsolutePath(),true);
			break;
		case ACC_TYPE:
			fileWritter = new FileWriter(mAccOut.getAbsolutePath(),true);
			break;
		case GRAVITY_TYPE:
			fileWritter = new FileWriter(mGravityOut.getAbsolutePath(),true);
			break;
		case GPS_TYPE:
			fileWritter = new FileWriter(mGPSout.getAbsolutePath(),true);
			break;
		case NETWORK_TYPE:
			fileWritter = new FileWriter(mNetworkOut.getAbsolutePath(),true);
			break;
		default:
			return false;
		}
		text = text+Calendar.getInstance().getTime().toString()+"\nPourcentage Batterie : "+(batPerc*100)+"%";
		text = text+"\n*********************\n*********************\n";
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        bufferWritter.write(text);
        bufferWritter.close();
        return true;
	}
	
	private String formatData(Location loc){
		String data = "";
		if (loc!=null){
			data = "Latitude : "+loc.getLatitude()+"\nLongitude : "+loc.getLongitude()+"\nPrécision : "+loc.getAccuracy()+"\nAltitude : "+loc.getAltitude();
		}
		return data;
	}
	
	private String formatData(float [] valeurs){
		String data = "";
		if (valeurs!=null){
			data = "x : "+valeurs[0]+"\ny : "+valeurs[1]+"\nz : "+valeurs[2]+"\nEchantillon de "+valeurs[3]+" valeurs\nConsommation : "+valeurs[4]+"mA";
		}
		return data;
	}
	
	private String formatData(boolean [] valeurs){
		String data = "";
		if (valeurs!=null){
			data = "Wifi activé : "+valeurs[0]+"\nData activée : "+valeurs[1]+"\nGSM activé : "+valeurs[2];
		}
		return data;
	}
	
	public void deleteFiles(int type){
		switch (type){
		case GPS_TYPE:
			mGPSout.delete();
			break;
		case NETWORK_TYPE:
			mNetworkOut.delete();
			break;
		case GYRO_TYPE:
			mGyroOut.delete();
			break;
		case ACC_TYPE:
			mAccOut.delete();
			break;
		case GRAVITY_TYPE:
			mGravityOut.delete();
			break;
		}
	}
}
