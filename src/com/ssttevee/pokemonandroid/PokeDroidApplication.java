package com.ssttevee.pokemonandroid;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.preference.PreferenceManager;import com.ssttevee.pokemonandroid.util.*;

import java.util.Random;

public class PokeDroidApplication extends Application {
	private SharedPreferences mPrefs;

	private StepDetector mStepDetector;
	private int mActionPoints = 0;

	public PokeDroidApplication() {
		Helper.app = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Helper.dataMgr = Helper.dataMgr == null ? new DataManager(getApplicationContext()) : Helper.dataMgr;
		mPrefs = getSharedPreferences("pokesave", MODE_PRIVATE);
		mActionPoints = mPrefs.getInt("actionPoints", 0);

		new Helper(getResources());
	}

	public void addActionPoint() {
		mActionPoints++;
		saveActionPoints();
	}
	public void useActionPoints(int amount) {
		mActionPoints -= amount;
		saveActionPoints();
	}
	public int getActionPoints() {
		return mActionPoints;
	}
	private void saveActionPoints() {
		SharedPreferences.Editor edit = mPrefs.edit();
		edit.putInt("actionPoints", mActionPoints);
		edit.apply();
	}

	public void setTrainerName(String name) {
		SharedPreferences.Editor edit = mPrefs.edit();
		edit.putString("trainer_name", name);
		edit.putInt("trainer_id", Math.abs(new Random().nextInt()));
		edit.apply();
	}

	public String getTrainerName() {
		return mPrefs.getString("trainer_name", null);
	}

	public int getTrainerId() {
		return mPrefs.getInt("trainer_id", 0);
	}

	public StepDetector getStepDetector() {
		if(mStepDetector == null) return mStepDetector = new StepDetector();
		return mStepDetector;
	}

	public int getTextSpeed() {
		return PreferenceManager.getDefaultSharedPreferences(this).getInt("optTextSpeed", getResources().getInteger(R.integer.optTextSpeedDefault)) + 1;
	}
}
