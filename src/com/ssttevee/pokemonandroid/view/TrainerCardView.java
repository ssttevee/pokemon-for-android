package com.ssttevee.pokemonandroid.view;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ssttevee.pokemonandroid.PokeDroidApplication;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.util.Helper;
import com.ssttevee.pokemonandroid.util.StepListener;

import java.util.HashMap;

public class TrainerCardView extends RelativeLayout {
	private PokeDroidApplication mApp;
	private Context context;

	public boolean isShowing = false;
	public float slideHeight = 0;

	public HashMap<TrainerStats, TextView> statTextViews;
	public enum TrainerStats {EXPERIENCE, ACTION_POINTS, POKEDEX, BATTLES, CATCHES, FAV_POKEMON};

	private ValueAnimator va = new ValueAnimator();

	private StepListener mStepListener = new StepListener() {
		@Override
		public void onStep() {
			updateStatBullet(TrainerStats.ACTION_POINTS, "AP:  " + mApp.getActionPoints());
		}

		@Override
		public void passValue() {}
	};

	public TrainerCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		if(!isInEditMode()) {
			mApp = (PokeDroidApplication) ((Activity) context).getApplication();
		}
		slideHeight = Helper.dpToPx(146);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				setTranslationY((Float) valueAnimator.getAnimatedValue());
			}
		});
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		((TextView) findViewById(R.id.tcName)).setTypeface(Helper.Fonts.getPkmnFL());

		int i = 0;
		statTextViews = new HashMap<TrainerStats, TextView>();
		for (TrainerStats ts : TrainerStats.values()) {
			ViewGroup stat = (ViewGroup) makeStatBullet(ts.name());
			TextView tv = (TextView) stat.findViewById(android.R.id.text1);

			((LinearLayout) ((LinearLayout) findViewById(R.id.tcStats)).getChildAt(i % 2)).addView(stat);
			statTextViews.put(ts, tv);
			i++;
		}

		((TextView) findViewById(R.id.tcName)).setText(Helper.app.getTrainerName());

		updateStatBullet(TrainerStats.EXPERIENCE, "XP:  123, 456");
		updateStatBullet(TrainerStats.ACTION_POINTS, "AP:  0");
		updateStatBullet(TrainerStats.POKEDEX, "POKEDEX: 123");
		updateStatBullet(TrainerStats.BATTLES, "BATTLES: 1, 234");
		updateStatBullet(TrainerStats.CATCHES, "CAUGHT: 135");
		updateStatBullet(TrainerStats.FAV_POKEMON, "FAV: Bulbasaur");

		mStepListener.onStep();
	}

	private View makeStatBullet(String text) {
		LinearLayout stat = (LinearLayout) View.inflate(context, R.layout.comp_tc_stat, null);

		((TextView) stat.findViewById(android.R.id.text1)).setTypeface(Helper.Fonts.getPkmnFL());
		((TextView) stat.findViewById(android.R.id.text1)).setText(text);

		stat.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 1f));

		return stat;
	}

	private void updateStatBullet(TrainerStats ts, String text) {
		statTextViews.get(ts).setText(text);
	}

	public void show() {
		va.setFloatValues(getTranslationY(), slideHeight);
		va.setDuration((long) Math.abs((1 - getTranslationY() / slideHeight) * 400));
		va.start();

		// Update AP number on the trainer card
		mStepListener.onStep();

		// Reregister the step listener
		mApp.getStepDetector().addStepListener(mStepListener);

		isShowing = true;
	}

	public void hide() {
		va.setFloatValues(getTranslationY(), 0);
		va.setDuration((long) Math.abs(getTranslationY() / slideHeight * 400));
		va.start();

		// Unregister the step listener
		mApp.getStepDetector().removeStepListener(mStepListener);

		isShowing = false;
	}
}
