package com.makina.gpsdata.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

/**
 * This class works fine
 * Handles all the data formatting and writing to files.
 * 
 * @author Guillaume Salmon
 * 
 */
public class FileManager {

	public static final int GPS_TYPE = 0;
	public static final int NETWORK_TYPE = 1;
	public static final int GYRO_TYPE = 2;
	public static final int ACC_TYPE = 3;
	public static final int GRAVITY_TYPE = 4;
	public static final int SENSOR_TYPE = 5;
	public static final int ORIENTATION_TYPE = 6;

	private File mGPSout;
	private File mNetworkOut;
	private File mGyroOut;
	private File mAccOut;
	private File mGravityOut;
	private File mSensorOut;
	private File mOrientationOut;
	private String mDirPath;
	private String mDirName;

	public FileManager(String dirPath, String dirName) throws Exception {
		mDirName = dirName;
		mDirPath = dirPath;
		createFiles();
	}

	public String getPath(int type) {
		switch (type) {
		case GPS_TYPE:
			return mGPSout.getAbsolutePath();
		case NETWORK_TYPE:
			return mNetworkOut.getAbsolutePath();
		case GYRO_TYPE:
			return mGyroOut.getAbsolutePath();
		case ACC_TYPE:
			return mAccOut.getAbsolutePath();
		case GRAVITY_TYPE:
			return mGravityOut.getAbsolutePath();
		case SENSOR_TYPE:
			return mSensorOut.getAbsolutePath();
		case ORIENTATION_TYPE:
			return mOrientationOut.getAbsolutePath();
		default:
			return null;
		}
	}

	/**
	 * Creates the files for all types of data if they don't already exist.
	 * 
	 * @throws Exception
	 */
	private void createFiles() throws Exception {

		File dir = new File(mDirPath, mDirName);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				RuntimeException e = new RuntimeException(
						"Cannot create directory: " + mDirName);
				throw e;
			}
		} else {
			if (!dir.isDirectory()) {
				RuntimeException e = new RuntimeException(mDirName
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
		mSensorOut = new File(mDirPath, "SensorOutput.txt");
		mOrientationOut = new File(mDirPath, "OrientationOutput.txt");

		if (!mGPSout.exists()) {
			mGPSout.createNewFile();
		}
		if (!mNetworkOut.exists()) {
			mNetworkOut.createNewFile();
		}
		if (!mGyroOut.exists()) {
			mGyroOut.createNewFile();
		}
		if (!mAccOut.exists()) {
			mAccOut.createNewFile();
		}
		if (!mGravityOut.exists()) {
			mGravityOut.createNewFile();
		}
		if (!mSensorOut.exists()) {
			mSensorOut.createNewFile();
		}
		if (!mOrientationOut.exists()) {
			mOrientationOut.createNewFile();
		}
	}

	/**
	 * Writes location data to file
	 * 
	 * @param type is the type of activity that returns the values
	 * @param loc is the current location information
	 * @param elapsedTime is the time elapsed since the last location was acquired
	 * @return true if everything ran normally, false otherwise
	 * @throws IOException
	 */
	public boolean writeDataToFile(int type, Situation loc, Long elpasedTime)
			throws IOException {
		FileWriter fileWritter;
		switch (type) {
		case GPS_TYPE:
			fileWritter = new FileWriter(mGPSout.getAbsolutePath(), true);
			break;
		case NETWORK_TYPE:
			fileWritter = new FileWriter(mNetworkOut.getAbsolutePath(), true);
			break;
		case SENSOR_TYPE:
			fileWritter = new FileWriter(mSensorOut.getAbsolutePath(), true);
			break;
		default:
			return false;
		}
		String text = "\n\n";
		text = text + Calendar.getInstance().getTime().toString() + "\n"
				+ formatData(loc) + "\n Temps écoulé : " + elpasedTime + " ms";
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(text);
		bufferWritter.close();
		return true;
	}

	/**
	 * Writes sensors data to file
	 * 
	 * @param type
	 * @param valeurs
	 * @return true if everything ran normally, false otherwise
	 * @throws IOException
	 */
	public boolean writeDataToFile(int type, float[] valeurs)
			throws IOException {
		FileWriter fileWritter;
		switch (type) {
		case GYRO_TYPE:
			fileWritter = new FileWriter(mGyroOut.getAbsolutePath(), true);
			break;
		case ACC_TYPE:
			fileWritter = new FileWriter(mAccOut.getAbsolutePath(), true);
			break;
		case GRAVITY_TYPE:
			fileWritter = new FileWriter(mGravityOut.getAbsolutePath(), true);
			break;
		case ORIENTATION_TYPE:
			fileWritter = new FileWriter(mOrientationOut.getAbsolutePath(),
					true);
			break;
		default:
			return false;
		}
		String text = "\n\n";
		text = text + Calendar.getInstance().getTime().toString() + "\n"
				+ formatData(valeurs);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(text);
		bufferWritter.close();
		return true;
	}

	/**
	 * Writes general data at the beginning of every set of data
	 * 
	 * @param type
	 * @param valeurs
	 * @param brightness
	 * @param batPerc
	 * @return true if everything ran normally, false otherwise
	 * @throws IOException
	 */
	public boolean writeDataToFile(int type, boolean valeurs[], int brightness,
			float batPerc) throws IOException {
		FileWriter fileWritter;
		String text = "\n\n\n*********************";
		switch (type) {
		case GYRO_TYPE:
			text = text + "\nGyroscope : ";
			fileWritter = new FileWriter(mGyroOut.getAbsolutePath(), true);
			break;
		case ACC_TYPE:
			text = text + "\nAccéléromètre : ";
			fileWritter = new FileWriter(mAccOut.getAbsolutePath(), true);
			break;
		case GRAVITY_TYPE:
			text = text + "\nGravité : ";
			fileWritter = new FileWriter(mGravityOut.getAbsolutePath(), true);
			break;
		case GPS_TYPE:
			text = text + "\nGPS : ";
			fileWritter = new FileWriter(mGPSout.getAbsolutePath(), true);
			break;
		case NETWORK_TYPE:
			text = text + "\nNetwork : ";
			fileWritter = new FileWriter(mNetworkOut.getAbsolutePath(), true);
			break;
		case SENSOR_TYPE:
			text = text + "\nSensors location : ";
			fileWritter = new FileWriter(mSensorOut.getAbsolutePath(), true);
			break;
		case ORIENTATION_TYPE:
			text = text + "\nOrientation : ";
			fileWritter = new FileWriter(mOrientationOut.getAbsolutePath(),
					true);
			break;
		default:
			return false;
		}
		text = text + Calendar.getInstance().getTime().toString() + "\n"
				+ formatData(valeurs) + "\nLuminosité de l'écran : "
				+ brightness + "\nPourcentage Batterie : " + (batPerc * 100)
				+ "%";
		text = text + "\n*********************";
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(text);
		bufferWritter.close();
		return true;
	}

	/**
	 * Writes mean location data to file
	 * 
	 * @param type is the type of activity that returns the values
	 * @param meanLat is the current mean latitude
	 * @param meanLong is the current mean longitude
	 * @param meanAcc is the current mean accuracy
	 * @param isValid is true if the last location acquired is valid, false otherwise
	 * @return true if everything ran normally, false otherwise
	 * @throws IOException
	 */
	public boolean writeDataToFile(int type, double meanLat, double meanLong,
			float meanAcc, boolean isValid) throws IOException {
		FileWriter fileWritter;
		String text = "\n\n";
		switch (type) {
		case GPS_TYPE:
			fileWritter = new FileWriter(mGPSout.getAbsolutePath(), true);
			break;
		case NETWORK_TYPE:
			fileWritter = new FileWriter(mNetworkOut.getAbsolutePath(), true);
			break;
		case SENSOR_TYPE:
			fileWritter = new FileWriter(mSensorOut.getAbsolutePath(), true);
			break;
		case ORIENTATION_TYPE:
			fileWritter = new FileWriter(mOrientationOut.getAbsolutePath(),
					true);
			break;
		default:
			return false;
		}
		text = text + Calendar.getInstance().getTime().toString()
				+ "\nLatitude moyenne : " + meanLat + "\nLongitude moyenne : "
				+ meanLong + "\nPrécision moyenne : " + meanAcc
				+ "\nValeur suivante valide : " + isValid;
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(text);
		bufferWritter.close();
		return true;
	}

	/**
	 * Writes final data at the end of every set of data
	 * 
	 * @param type
	 * @param batPerc
	 * @return true if everything ran normally, false otherwise
	 * @throws IOException
	 */
	public boolean writeDataToFile(int type, float batPerc) throws IOException {
		FileWriter fileWritter;
		String text = "\n\n*********************\n";
		switch (type) {
		case GYRO_TYPE:
			fileWritter = new FileWriter(mGyroOut.getAbsolutePath(), true);
			break;
		case ACC_TYPE:
			fileWritter = new FileWriter(mAccOut.getAbsolutePath(), true);
			break;
		case GRAVITY_TYPE:
			fileWritter = new FileWriter(mGravityOut.getAbsolutePath(), true);
			break;
		case GPS_TYPE:
			fileWritter = new FileWriter(mGPSout.getAbsolutePath(), true);
			break;
		case NETWORK_TYPE:
			fileWritter = new FileWriter(mNetworkOut.getAbsolutePath(), true);
			break;
		case SENSOR_TYPE:
			fileWritter = new FileWriter(mSensorOut.getAbsolutePath(), true);
			break;
		case ORIENTATION_TYPE:
			fileWritter = new FileWriter(mOrientationOut.getAbsolutePath(),
					true);
			break;
		default:
			return false;
		}
		text = text + Calendar.getInstance().getTime().toString()
				+ "\nPourcentage Batterie : " + (batPerc * 100) + "%";
		text = text + "\n*********************\n*********************\n";
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(text);
		bufferWritter.close();
		return true;
	}

	/**
	 * Makes location data understandable before to write it on the file
	 * 
	 * @param loc
	 * @return formatted data in a String
	 */
	private String formatData(Situation loc) {
		String data = "";
		if (loc != null) {
			data = "Latitude : " + loc.getLatitude() + "\nLongitude : "
					+ loc.getLongitude() + "\nPrécision : " + loc.getAccuracy();
		}
		return data;
	}

	/**
	 * Makes sensors data understandable before to write it on the file
	 * 
	 * @param valeurs
	 * @return formatted data in a String
	 */
	private String formatData(float[] valeurs) {
		String data = "";
		if (valeurs != null) {
			data = "x : " + valeurs[0] + "\ny : " + valeurs[1] + "\nz : "
					+ valeurs[2] + "\nEchantillon de " + valeurs[3]
					+ " valeurs\nConsommation : " + valeurs[4] + "mA";
		}
		return data;
	}

	/**
	 * Makes general data understandable before to write it on the file
	 * 
	 * @param valeurs
	 * @return formatted data in a String
	 */
	private String formatData(boolean[] valeurs) {
		String data = "";
		if (valeurs != null) {
			data = "Wifi activé : " + valeurs[0] + "\nData activée : "
					+ valeurs[1] + "\nGSM activé : " + valeurs[2];
		}
		return data;
	}

	/**
	 * Delete the file containing the data for the specified type
	 * 
	 * @param type
	 */
	public void deleteFiles(int type) {
		switch (type) {
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
		case SENSOR_TYPE:
			mSensorOut.delete();
			break;
		case ORIENTATION_TYPE:
			mOrientationOut.delete();
			break;
		}
	}
}
