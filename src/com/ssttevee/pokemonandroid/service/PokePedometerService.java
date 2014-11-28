package com.ssttevee.pokemonandroid.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import com.ssttevee.pokemonandroid.PokeDroid;
import com.ssttevee.pokemonandroid.PokeDroidApplication;
import com.ssttevee.pokemonandroid.util.StepDetector;
import com.ssttevee.pokemonandroid.util.StepListener;

import java.util.logging.Logger;

/**
 * Created by Steve on 11/6/2014.
 */
public class PokePedometerService extends Service {

	private static final Logger logger = Logger.getLogger(PokePedometerService.class.getSimpleName());

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private StepDetector mStepDetector;

	private SharedPreferences mSettings;
	private PokeDroidApplication mApp;

	private int mStep = 0;

	@Override
	public void onCreate() {
		mApp = (PokeDroidApplication) getApplication();

		mStepDetector = mApp.getStepDetector();
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		registerDetector();

		mSettings = PreferenceManager.getDefaultSharedPreferences(this);

		mStepDetector.setSensitivity(
				Float.valueOf(mSettings.getString("sensitivity", "10"))
		);

		mStepDetector.addStepListener(new StepListener() {
			@Override
			public void onStep() {
				mStep++;
				if(mStep >= PokeDroid.STEPS_PER_ACTION_POINT) {
					mStep -= PokeDroid.STEPS_PER_ACTION_POINT;
					mApp.addActionPoint();
				}
			}

			@Override
			public void passValue() {

			}
		});
	}

	@Override
	public void onDestroy() {

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void registerDetector() {
		mSensor = mSensorManager.getDefaultSensor(
				Sensor.TYPE_ACCELEROMETER /*|
            Sensor.TYPE_MAGNETIC_FIELD |
            Sensor.TYPE_ORIENTATION*/);
		mSensorManager.registerListener(mStepDetector, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
	}

	private void unregisterDetector() {
		mSensorManager.unregisterListener(mStepDetector);
	}
}
