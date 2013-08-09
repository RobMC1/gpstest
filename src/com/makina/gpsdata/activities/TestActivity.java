package com.makina.gpsdata.activities;

import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.makina.gpsdata.R;
import com.makina.gpsdata.utils.FileManager;

/**
 * TestActivity creates files to save data and gets global values like battery
 * level or screen brightness.
 * 
 * @author Guillaume Salmon
 * 
 */
public abstract class TestActivity extends Activity {
	protected FileManager mFileManager;
	protected String mDirName;
	protected String mDirPath;
	protected boolean mIsUpdating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		mDirName = (String) i.getCharSequenceExtra("dirName");
		mDirPath = (String) i.getCharSequenceExtra("dirPath");

		try {
			mFileManager = new FileManager(mDirPath, mDirName);
		} catch (Exception e) {
			Toast.makeText(this, "Failed to create files", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}
		mIsUpdating = false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.loc_menu, menu);
		if (!mIsUpdating) {
			menu.findItem(R.id.stop_service).setIcon(
					android.R.drawable.ic_media_play);
		} else {
			menu.findItem(R.id.stop_service).setIcon(
					android.R.drawable.ic_media_pause);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			
			// Stop updates for the activity currently running and go back to main menu
			stopUpdates();
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			break;
			
		case R.id.clear_files:
			
			// Delete the file used by the activity currently running
			mFileManager.deleteFiles(getType());
			break;
			
		case R.id.view_files:
			
			// Open a text editor to view the file used by the activity currently running
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setDataAndType(
					Uri.parse("file://" + mFileManager.getPath(getType())),
					"text/plain");
			try {
				startActivity(intent);
			} catch (Exception e) {
				Toast.makeText(this, "Unable to open file\nCheck if you have a text editor installed", Toast.LENGTH_LONG)
						.show();
			}
			break;
			
		case R.id.stop_service:
			
			// Stops the activity if it's running, starts it otherwise
			if (mIsUpdating) {
				stopUpdates();
				item.setIcon(android.R.drawable.ic_media_play);
				mIsUpdating = false;
			} else {
				startUpdates();
				item.setIcon(android.R.drawable.ic_media_pause);
				mIsUpdating = true;
			}
			break;
			
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * This function gets the current battery level
	 * 
	 * @return The current battery level between 0 and 1
	 */
	protected float getBatLvl() {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = getApplicationContext().registerReceiver(null,
				ifilter);

		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float batteryPct = level / (float) scale;
		return batteryPct;
	}

	@Override
	protected void onResume() {
		//Keeps the activity running when changing screen orientation
		if (mIsUpdating) {
			startUpdates();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (mIsUpdating) {
			stopUpdates();
		}
		super.onPause();
	}

	/**
	 * Stop the timer and stop the data from being wrote to the file. Write
	 * final infos on file.
	 */
	protected void stopUpdates() {
		try {
			mFileManager.writeDataToFile(getType(), getBatLvl());
		} catch (IOException e) {
			Toast.makeText(this, "Failed write data on file",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	/**
	 * Start the timer and schedule the data updates. Write general informations
	 * on file.
	 */
	protected void startUpdates() {

		// Find general informations and add it to file :
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo typeWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		ContentResolver cr = getContentResolver();
		boolean wifi = typeWifi.isAvailable();
		boolean gsm = false;
		boolean data = false;
		try {
			int value = Secure.getInt(cr, "preferred_network_mode");
			switch (value) {
			case 0:
			case 3:
				gsm = true;
				data = true;
				break;
			case 1:
				gsm = true;
				data = false;
				break;
			case 2:
				gsm = false;
				data = true;
				break;
			}
		} catch (SettingNotFoundException e1) {
			gsm = false;
			data = false;
		}
		int screenBrightness;
		try {
			screenBrightness = android.provider.Settings.System.getInt(
					getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			screenBrightness = -1;
		}
		boolean values[] = { wifi, data, gsm };
		try {
			mFileManager.writeDataToFile(getType(), values, screenBrightness,
					getBatLvl());
		} catch (IOException e) {
			Toast.makeText(this, "Failed write data on file",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	/**
	 * Collect info and write it to file
	 */
	protected abstract void getInfo();

	/**
	 * Gets the type of the provider or sensor currently in use
	 * 
	 * @return The type of the sensor or provider as defined in FileManager.
	 */
	protected abstract int getType();
}
