package com.ssttevee.pokemonandroid.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.helper.Pokemon;
import com.ssttevee.pokemonandroid.util.Helper;

public class HealthBar extends View {

	private Rect srcRect;
	private Bitmap green;
	private Bitmap yellow;
	private Bitmap red;
	private Paint nnPaint;

	private Pokemon pokemon;

	public float currentHP;

	public HealthBar(Context context, Pokemon pkmn) {
		super(context);
		setup();
		setPokemon(pkmn);
	}

	public HealthBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}

	private void setup() {
		green = BitmapFactory.decodeResource(getResources(), R.drawable.health_bar_green);
		yellow = BitmapFactory.decodeResource(getResources(), R.drawable.health_bar_yellow);
		red = BitmapFactory.decodeResource(getResources(), R.drawable.health_bar_red);
		srcRect = new Rect(0, 0, green.getWidth(), green.getHeight());

		nnPaint = new Paint();
		nnPaint.setAntiAlias(false);
		nnPaint.setFilterBitmap(false);
		nnPaint.setDither(false);
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
			width = Math.min(Helper.dpToPx(66), widthSize);
		} else {
			width = Helper.dpToPx(66);
		}

		//Measure Height
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			height = Math.min(Helper.dpToPx(7), heightSize);
		} else {
			height = Helper.dpToPx(7);
		}

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		double currentHp = pokemon == null ? 1 : currentHP/pokemon.getStat(Pokemon.IVStat.HP);

		NinePatchDrawable bg =  (NinePatchDrawable) getResources().getDrawable(R.drawable.health_bar_template);
		bg.setBounds(0, 0, getWidth(), getHeight());
		bg.draw(canvas);

		Bitmap bar;
		if(currentHp > .5D) bar = green;
		else if(currentHp > 0.125D) bar = yellow;
		else bar = red;

		canvas.drawBitmap(bar, srcRect, new RectF(Helper.dpToPx(15), Helper.dpToPx(2), (float) (Helper.dpToPx(15) + currentHp * (getWidth() - Helper.dpToPx(17))), getHeight() - Helper.dpToPx(2)), nnPaint);
	}

	public void setPokemon(Pokemon pkmn) {
		pokemon = pkmn;
		currentHP = pkmn.currentHp;
	}
}
