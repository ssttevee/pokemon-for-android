package com.ssttevee.pokemonandroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ssttevee.pokemonandroid.util.Helper;

public class DrawerItemEntry extends LinearLayout {

	public DrawerItemEntry(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		((TextView) findViewById(android.R.id.text1)).setTypeface(Helper.Fonts.getPkmnFL());
	}
}
