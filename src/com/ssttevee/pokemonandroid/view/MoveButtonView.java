package com.ssttevee.pokemonandroid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.text.TextPaint;
import android.view.View;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.helper.Move;
import com.ssttevee.pokemonandroid.helper.PokemonType;
import com.ssttevee.pokemonandroid.util.Helper;

public class MoveButtonView extends View {
	private Rect mRuler = new Rect();
	private TextPaint mTextPaint;
	private GradientDrawable bg;

	public Move move;

	public MoveButtonView(Context context, Move move) {
		super(context);
		this.move = move;
		bg = (GradientDrawable) getResources().getDrawable(R.drawable.battle_button_shape);

		if(mTextPaint == null) {
			mTextPaint = new TextPaint();
			mTextPaint.setTypeface(Helper.Fonts.getPkmnFL());
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(isEnabled()) bg.setColor(PokemonType.getTypeColor(move.getType()));
		else bg.setColor(0xff6d6d6d);

		bg.setBounds(0, 0, getWidth(), getHeight());
		bg.draw(canvas);

		getTextWithShadowBounds(move.getName(), 16, mRuler);
		drawTextWithShadow(canvas, move.getName(), (getWidth() - mRuler.width()) / 2, (getHeight() + mRuler.height()) / 2, 16);

		getTextWithShadowBounds("PP " + move.currentPP + "/" + move.maxPP, 14, mRuler);
		drawTextWithShadow(canvas, "PP " + move.currentPP + "/" + move.maxPP, getWidth() - Helper.dpToPx(6) - mRuler.width(), getHeight() - Helper.dpToPx(6), 14);
	}

	private void drawTextWithShadow(Canvas canvas, String text, int x, int y, int size) {
		mTextPaint.setTextSize(Helper.dpToPx(size));

		mTextPaint.setColor(Color.parseColor("#404040"));
		canvas.drawText(text, x, y, mTextPaint);

		mTextPaint.setColor(Color.parseColor("#f8f0e0"));
		canvas.drawText(text, x - Helper.dpToPx(1), y - Helper.dpToPx(1), mTextPaint);
	}

	private void getTextWithShadowBounds(String text, int size, Rect r) {
		mTextPaint.setTextSize(Helper.dpToPx(size));
		mTextPaint.getTextBounds(text, 0, text.length(), r);
		r.right += Helper.dpToPx(1);
		r.bottom += Helper.dpToPx(1);
	}
}
