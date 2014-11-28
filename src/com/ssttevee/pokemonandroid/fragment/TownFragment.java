package com.ssttevee.pokemonandroid.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.util.Helper;
import com.ssttevee.pokemonandroid.view.PokeHealer3000;
import com.ssttevee.pokemonandroid.view.PokeInfoView;

import javax.xml.validation.Validator;

public class TownFragment extends Fragment {
	private RelativeLayout rootView;

	private String[] buildingNames;
	private int[] buildingColors;
	private int currentBuilding = 0;
	private int newBuilding = 0;

	private TextView[] nameHolders = new TextView[3];
	private float holderWidth = 0;

	private ValueAnimator va = ValueAnimator.ofFloat(0, 1);

	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
		rootView = (RelativeLayout) inflater.inflate(R.layout.town, container, false);

		holderWidth = container.getWidth();
		buildingNames = getResources().getStringArray(R.array.townBuildings);
		buildingColors = getResources().getIntArray(R.array.townBuildingsColors);

		final RelativeLayout nameSlider = (RelativeLayout) rootView.findViewById(R.id.building_names);
		for(int i = 0; i < nameSlider.getChildCount(); i++)
			nameHolders[i] = (TextView) nameSlider.getChildAt(i);
		translateSlider(0);
		setNames();

		va.setDuration(400);
		va.setInterpolator(new DecelerateInterpolator());
		va.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				setCurrentBuilding(newBuilding);
				va.removeAllUpdateListeners();
			}
			@Override
			public void onAnimationCancel(Animator animation) {
				this.onAnimationEnd(animation);
			}
		});

		rootView.setOnTouchListener(new View.OnTouchListener() {
			private float origX = 0;
			private float clickX = 0;
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if(va.isRunning()) va.cancel();
				switch(event.getActionMasked()) {
					case MotionEvent.ACTION_DOWN:
						origX = clickX = event.getRawX();
						break;

					case MotionEvent.ACTION_MOVE:
						if(currentBuilding <= 0 && event.getRawX() > origX) origX = event.getRawX();
						if(currentBuilding >= buildingNames.length - 1 && event.getRawX() < origX) origX = event.getRawX();
						float trans = event.getRawX() - origX;
						translateSlider(trans);
						break;

					case MotionEvent.ACTION_UP:
						final float diff = event.getRawX() - origX;
						if(diff < -Helper.dpToPx(25) / 3) {
							newBuilding++;
							va.setDuration((long) ((holderWidth - Math.abs(diff)) / holderWidth * 300) + 100);
							va.setFloatValues(diff, -holderWidth);
							va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
								@Override
								public void onAnimationUpdate(ValueAnimator valueAnimator) {
									translateSlider((Float) valueAnimator.getAnimatedValue());
								}
							});
						} else if(diff > Helper.dpToPx(25) / 3) {
							newBuilding--;
							va.setDuration((long) ((holderWidth - Math.abs(diff)) / holderWidth * 300) + 100);
							va.setFloatValues(diff, holderWidth);
							va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
								@Override
								public void onAnimationUpdate(ValueAnimator valueAnimator) {
									translateSlider((Float) valueAnimator.getAnimatedValue());
								}
							});
						} else if(Math.abs(diff) > Helper.dpToPx(2)) {
							va.setDuration((long) (Math.abs(diff) / holderWidth * 300) + 100);
							va.setFloatValues(diff, 0);
							va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
								@Override
								public void onAnimationUpdate(ValueAnimator valueAnimator) {
									translateSlider((Float) valueAnimator.getAnimatedValue());
								}
							});
						} else if(event.getRawX() - clickX < Helper.dpToPx(2)) {
							enterBuilding(currentBuilding);
							return false;
						}
						va.start();
						break;
				}
				return true;
			}
		});

		return rootView;
	}

	private void enterBuilding(int building) {
		switch(building) {
			case 0: // Pokemon Center
				Dialog dialog = new Dialog(getActivity());
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(new PokeHealer3000(getActivity(), dialog));
				dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
				dialog.show();
				break;
			case 1: // PokeMart
				break;
			case 2: // Pokemon Renamer
				break;
		}
	}

	private void setCurrentBuilding(int building) {
		currentBuilding = building;
		translateSlider(0);
		setNames();
	}

	private void translateSlider(float x) {
		if(x < 0) rootView.setBackgroundColor(interpolateColor(buildingColors[currentBuilding], currentBuilding < buildingColors.length - 1 ? buildingColors[currentBuilding + 1] : buildingColors[currentBuilding], Math.abs(x) / holderWidth));
		else if(x > 0) rootView.setBackgroundColor(interpolateColor(buildingColors[currentBuilding], currentBuilding > 0 ? buildingColors[currentBuilding - 1] : buildingColors[currentBuilding], Math.abs(x) / holderWidth));
		else rootView.setBackgroundColor(buildingColors[currentBuilding]);
		nameHolders[0].setTranslationX(x - holderWidth);
		nameHolders[1].setTranslationX(x);
		nameHolders[2].setTranslationX(x + holderWidth);
	}

	private void setNames() {
		for(int i = 0; i < nameHolders.length; i++) {
			TextView tv = nameHolders[i];
			int num = i - 1 + currentBuilding;
			tv.setText((num < 0 || num >= buildingNames.length) ? "" : buildingNames[num]);
		}
	}

	private int interpolateColor(int a, int b, float proportion) {
		float[] rgb = new float[3];
		rgb[0] = interpolate(Color.red(a), Color.red(b), proportion);
		rgb[1] = interpolate(Color.green(a), Color.green(b), proportion);
		rgb[2] = interpolate(Color.blue(a), Color.blue(b), proportion);
		return Color.argb(255, (int) rgb[0], (int) rgb[1], (int) rgb[2]);
	}

	private float interpolate(float a, float b, float proportion) {
		return a + ((b - a) * proportion);
	}
}