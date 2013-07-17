package com.makina.gpsdata.activities;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
 * This abstract class implements the methods which get the test values for the providers.
 * 
 * @author Guillaume Salmon
 * 
 */
public abstract class TestActivity extends Activity {
	protected FileManager mFileManager;
	private Timer mTimer;
	private boolean mIsUpdating;
	protected String mDirName;
	protected String mDirPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		mDirName = (String) i.getCharSequenceExtra("dirName");
		mDirPath = (String) i.getCharSequenceExtra("dirPath");
		mIsUpdating = false;

		try {
			mFileManager = new FileManager(mDirPath, mDirName);
		} catch (Exception e) {
			Toast.makeText(this, "Failed to create files", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.loc_menu, menu);
		if (!mIsUpdating){
			menu.findItem(R.id.stop_service).setIcon(android.R.drawable.ic_media_play);
		}else{
			menu.findItem(R.id.stop_service).setIcon(android.R.drawable.ic_media_pause);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			break;
		case R.id.clear_files:
			mFileManager.deleteFiles(FileManager.NETWORK_TYPE);
			break;
		case R.id.stop_service:
			if (mIsUpdating){
				stopUpdates();
				item.setIcon(android.R.drawable.ic_media_play);
			}else{
				startUpdates();
				item.setIcon(android.R.drawable.ic_media_pause);
			}
			mIsUpdating = !mIsUpdating;
		}
		return super.onOptionsItemSelected(item);
	}

	protected float getBatLvl() {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = getApplicationContext().registerReceiver(null,
				ifilter);

		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float batteryPct = level / (float) scale;
		return batteryPct;
	}
	
	private class InfoUpdater extends TimerTask {
		public void run() {
			getInfo();
		}
	}
	
	@Override
	protected void onResume() {
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
	
	protected void stopUpdates(){
		mTimer.cancel();
		try {
			mFileManager.writeDataToFile (getType(), getBatLvl());
		} catch (IOException e) {
			Toast.makeText(this, "Failed write data on file", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	protected void startUpdates(){
		mTimer = new Timer();
		TimerTask updateInfo = new InfoUpdater();
		mTimer.scheduleAtFixedRate(updateInfo, 1000, 1000);
		
		// Find general informations and add it to file : 
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo typeWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		ContentResolver cr = getContentResolver();
		boolean wifi = typeWifi.isAvailable();
		boolean gsm = false; 
		boolean data = false;
		try {
			int value = Secure.getInt(cr, "preferred_network_mode");
			switch (value){
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
			screenBrightness = android.provider.Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			screenBrightness = -1;
		}
		boolean valeurs [] = {wifi, data, gsm};
		try {
			mFileManager.writeDataToFile(getType(), valeurs, screenBrightness, getBatLvl());
		} catch (IOException e) {
			Toast.makeText(this, "Failed write data on file", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	protected abstract void getInfo();
	
	protected abstract int getType();
}
