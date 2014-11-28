package com.ssttevee.pokemonandroid.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ssttevee.pokemonandroid.PokeDroidApplication;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.util.Helper;
import com.ssttevee.pokemonandroid.util.OptionListener;

import java.util.ArrayList;

public class PokeOptionLayout extends RelativeLayout {
	private static final int OPTION_ID = 0x0;
	private static final int OPTION_POS = 0x1;

	private String mIdentifier;
	private String mName;
	private String[] mOptions;
	private SparseArray<TextView> mOptionViews;
	private int mSelected;

	private ArrayList<OptionListener> mListeners;
	private SharedPreferences mSharedPrefs;

	public PokeOptionLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mOptionViews = new SparseArray<TextView>();
		mListeners = new ArrayList<OptionListener>();
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	public void setData(String name) {
		LinearLayout optionsView = (LinearLayout) findViewById(R.id.options);

		mIdentifier = name;
		mName = getResources().getString(getResources().getIdentifier("string/opt" + mIdentifier + "Label", null, getContext().getPackageName()));
		mOptions = getResources().getStringArray(getResources().getIdentifier("array/opt" + mIdentifier, null, getContext().getPackageName()));
		mSelected = mSharedPrefs.getInt("opt" + mIdentifier, getResources().getInteger(getResources().getIdentifier("integer/opt" + mIdentifier + "Default", null, getContext().getPackageName())));

		TextView optionTitle = (TextView) findViewById(R.id.optionTitle);
		optionTitle.setTypeface(Helper.Fonts.getPkmnFL());
		optionTitle.setText(mName);


		if(mOptions.length < 4) for(int i = 0; i < mOptions.length; i++) {
			TextView tv = new TextView(getContext());
			tv.setText(mOptions[i]);
			tv.setTypeface(Helper.Fonts.getPkmnFL());
			tv.setTag(i);
			if(i == mSelected) {
				tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_1));
			} else {
				tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_0));
			}

			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mOptionViews.get(mSelected).setBackgroundDrawable(getResources().getDrawable(R.drawable.border_0));
					v.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_1));

					SharedPreferences.Editor edit = mSharedPrefs.edit();
					edit.putInt("opt" + mIdentifier, (Integer) v.getTag());
					edit.apply();

					mSelected = (Integer) v.getTag();

					for(OptionListener ol : mListeners) ol.onChange(mSelected);
				}
			});

			mOptionViews.append(i, tv);
			optionsView.addView(tv);
		} else {
			TextView left = new TextView(getContext());
			TextView tv = new TextView(getContext());
			TextView right = new TextView(getContext());
			left.setText("<");
			tv.setText(mOptions[mSelected]);
			right.setText(">");
			left.setTypeface(Helper.Fonts.getPkmnFL());
			tv.setTypeface(Helper.Fonts.getPkmnFL());
			right.setTypeface(Helper.Fonts.getPkmnFL());

			left.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_0));
			tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_1));
			right.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_0));

			left.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mSelected--;
					if(mSelected < 0) mSelected = mOptions.length - 1;

					mOptionViews.get(0).setText(mOptions[mSelected]);

					SharedPreferences.Editor edit = mSharedPrefs.edit();
					edit.putInt("opt" + mIdentifier, mSelected);
					edit.apply();

					for(OptionListener ol : mListeners) ol.onChange(mSelected);
				}
			});

			right.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mSelected++;
					if(mSelected >= mOptions.length) mSelected = 0;

					mOptionViews.get(0).setText(mOptions[mSelected]);

					SharedPreferences.Editor edit = mSharedPrefs.edit();
					edit.putInt("opt" + mIdentifier, mSelected);
					edit.apply();

					for(OptionListener ol : mListeners) ol.onChange(mSelected);
				}
			});

			mOptionViews.append(0, tv);

			optionsView.addView(left);
			optionsView.addView(tv);
			optionsView.addView(right);
		}
	}

	public void addOptionListener(OptionListener ol) {
		mListeners.add(ol);
	}

	public void removeOptionListener(OptionListener ol) {
		mListeners.remove(ol);
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String[] getOptions() {
		return mOptions;
	}

	public void setOptions(String[] mOptions) {
		this.mOptions = mOptions;
	}

	public int getSelected() {
		return mSelected;
	}

	public void setSelected(int mSelected) {
		this.mSelected = mSelected;
	}

}
