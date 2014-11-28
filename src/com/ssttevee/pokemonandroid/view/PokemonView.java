package com.ssttevee.pokemonandroid.view;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import com.ssttevee.pokemonandroid.helper.Pokemon;
import com.ssttevee.pokemonandroid.util.Helper;

import java.io.InputStream;

public class PokemonView extends View {
	private Paint nnPaint;
	private Bitmap pkmnBitmap;

	private Rect bmRect;
	private Rect viewRect;

	public PokemonView(Context context, Pokemon pkmn) {
		super(context);

		nnPaint = new Paint();
		nnPaint.setAntiAlias(false);
		nnPaint.setFilterBitmap(false);
		nnPaint.setDither(false);

		bmRect = new Rect();
		viewRect = new Rect();

		setPokemon(pkmn);
		setScaleX(0);
		setScaleY(0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			width = Math.min(Helper.dpToPx(160), widthSize);
		} else {
			width = Helper.dpToPx(160);
		}

		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			height = Math.min(width, heightSize);
		} else {
			height = width;
		}

		setMeasuredDimension(width, height);
		viewRect.set(0, 0, width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(pkmnBitmap, bmRect, viewRect, nnPaint);
	}

	public void setPokemon(Pokemon pkmn) {
		try {
			InputStream is = getResources().getAssets().open("sprites/" + (pkmn.isEnemy ? "front" : "back") + "/_" + pkmn.id + ".png");
			pkmnBitmap = BitmapFactory.decodeStream(is);

			bmRect.set(0, 0, pkmnBitmap.getWidth(), pkmnBitmap.getHeight());
		} catch(Exception e) {
			e.printStackTrace();
		}

		invalidate();
	}
}
