package com.ssttevee.pokemonandroid.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ssttevee.pokemonandroid.PokeDroidApplication;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.helper.Map;
import com.ssttevee.pokemonandroid.util.Helper;

public class MapsButtonView extends RelativeLayout {
	private Map map;

	public MapsButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setMap(Map map) {
		this.map = map;
		((TextView) findViewById(R.id.middleMiddle)).setText(map.name);
		setStyle(map.backgroundId);
	}

	public Map getMap() {
		return this.map;
	}

	private void setStyle(int styleId) {
		String[] pieces = getResources().getStringArray(R.array.tilePieces);

		BitmapDrawable bg = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("drawable/maps_button_bg_" + styleId, null, getContext().getPackageName())));
		bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		setBackgroundDrawable(bg);

		for(String piece : pieces) {
			String[] split = piece.split(",,");

			View v = findViewById(getResources().getIdentifier("id/" + split[0], null, getContext().getPackageName()));

			BitmapDrawable bm = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("drawable/maps_button_" + styleId + "_" + split[1], null, getContext().getPackageName())));

			bm.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

			if(split[1].equals("mm")) {
				v.setBackgroundDrawable(bm);
				((TextView) v).setTypeface(Helper.Fonts.getPkmnFL());
			} else {
				((ImageView) v).setImageDrawable(bm);
			}
		}
	}
}
