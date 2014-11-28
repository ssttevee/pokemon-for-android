package com.ssttevee.pokemonandroid.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.util.Log;
import android.util.SparseArray;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.View;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.helper.Move;
import com.ssttevee.pokemonandroid.helper.Pokemon;
import com.ssttevee.pokemonandroid.util.Helper;

import java.io.IOException;
import java.util.ArrayList;

public class PokeHealer3000 extends View {
	private Dialog container;

	private NinePatchDrawable screen;
	private NinePatchDrawable bezel;
	private Paint bmPaint;
	private Paint paint;
	private TextPaint textPaint;
	private Rect ruler;

	private boolean initAnimStarted = false;
	private float[] initAnims;
	private ValueAnimator[] initVA;

	private boolean isDone = false;
	private boolean isWaiting = true;
	private int flashingAlpha = 0;
	private ValueAnimator waitingFlash;
	private ValueAnimator healer;

	private int width = Helper.dpToPx(200);
	private int height = Helper.dpToPx(200);

	private SparseArray<Bitmap> assets = new SparseArray<Bitmap>();

	public PokeHealer3000(Context context, Dialog dialog) {
		this(context);
		container = dialog;
	}

	public PokeHealer3000(Context context) {
		super(context);

		screen = (NinePatchDrawable) getResources().getDrawable(R.drawable.poke_healer_screen);
		bezel = (NinePatchDrawable) getResources().getDrawable(R.drawable.poke_healer_bezel);

		ruler = new Rect();
		paint = new Paint();
		bmPaint = new Paint();
		bmPaint.setAntiAlias(false);
		bmPaint.setFilterBitmap(false);
		bmPaint.setDither(false);
		textPaint = new TextPaint();
		textPaint.setTypeface(Helper.Fonts.getPkmnFL());

		initAnims = new float[Helper.dataMgr.getPokemonTeam().size()];
		for(int i = 0; i < initAnims.length; i++) initAnims[i] = 0;
		initVA = new ValueAnimator[Helper.dataMgr.getPokemonTeam().size()];
		for(int i = 0; i < initVA.length; i++) {
			final int j = i;
			ValueAnimator va = ValueAnimator.ofFloat(0, 1);
			va.setDuration(400);
			va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				private boolean nextStarted = false;
				private int i = j;
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					initAnims[i] = (Float) valueAnimator.getAnimatedValue();
					invalidate();
					if(initAnims[i] > 0.2 && !nextStarted) {
						startNextAnimator();
						nextStarted = true;
					}
				}
			});
			initVA[i] = va;
		}

		waitingFlash = ValueAnimator.ofInt(0, 255);
		waitingFlash.setDuration(500);
		waitingFlash.setRepeatCount(ValueAnimator.INFINITE);
		waitingFlash.setRepeatMode(ValueAnimator.REVERSE);
		waitingFlash.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				flashingAlpha = (Integer) valueAnimator.getAnimatedValue();
				invalidate();
				if(!isWaiting && !isDone && flashingAlpha == 0) valueAnimator.cancel();
			}
		});
		waitingFlash.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationCancel(Animator animation) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						healer.start();
					}
				});
			}
		});
		waitingFlash.start();

		healer = ValueAnimator.ofFloat(0, 1);
		healer.setDuration(ValueAnimator.getFrameDelay()*500);
		healer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				isDone = doHeal();
				if(isDone) {
					valueAnimator.cancel();
					waitingFlash.start();
					for(Pokemon p : Helper.dataMgr.getPokemonTeam())
						p.save();
				}
			}
		});

		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if(isWaiting) isWaiting = false;
				if(isDone && container != null) container.dismiss();
				return false;
			}
		});
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		screen.setBounds(0, 0, getWidth(), getHeight());
		screen.draw(canvas);

		paint.setColor(0xffffe1da);
		canvas.drawRect(Helper.dpToPx(5), Helper.dpToPx(4), getWidth() - Helper.dpToPx(5), getHeight() - Helper.dpToPx(4), paint);

		String str = "POKéHEAL-O-MATIC";
		textPaint.setColor(0xffd03d34);
		textPaint.setTextSize(Helper.dpToPx(15));
		textPaint.setAlpha(255);
		textPaint.getTextBounds(str, 0, str.length(), ruler);

		paint.setColor(0xfff49389);
		canvas.drawRect(Helper.dpToPx(5), Helper.dpToPx(4), getWidth() - Helper.dpToPx(5), Helper.dpToPx(9) + ruler.height(), paint);
		paint.setColor(0xffffc6bd);
		canvas.drawRect(Helper.dpToPx(5), Helper.dpToPx(4), getWidth() - Helper.dpToPx(5), Helper.dpToPx(8) + ruler.height(), paint);

		canvas.drawText(str, (getWidth() - ruler.width()) / 2, Helper.dpToPx(6) + ruler.height(), textPaint);

		ruler.left = Helper.dpToPx(9);
		ruler.right = getWidth() - ruler.left;
		int capsuleLeft = ruler.left;
		int capsuleWidth = ruler.width();
		int capsuleTop = Helper.dpToPx(13) + ruler.height();
		ArrayList<Pokemon> party = Helper.dataMgr.getPokemonTeam();

		for(int i = 0; i < party.size(); i++)
			drawPokemon(canvas, capsuleLeft, capsuleTop + Helper.dpToPx(44 * i), capsuleWidth, party.get(i), initAnims[i]);

		bezel.setBounds(0, 0, getWidth(), getHeight());
		bezel.draw(canvas);

		if(waitingFlash.isRunning()) {
			str = isDone ? "Healing Complete" : "Tap to Begin";
			textPaint.setColor(0xffffffff);
			textPaint.setTextSize(Helper.dpToPx(20));
			textPaint.setAlpha(flashingAlpha);
			textPaint.getTextBounds(str, 0, str.length(), ruler);
			canvas.drawText(str, (getWidth() - ruler.width()) / 2, (getHeight() + ruler.height()) / 2, textPaint);
		}

		if(!initAnimStarted) {
			startNextAnimator();
			initAnimStarted = true;
		}

		if(getHeight() != capsuleTop + Helper.dpToPx(44 * party.size() + 5)) {
			height = capsuleTop + Helper.dpToPx(44 * party.size() + 5);
			requestLayout();
		}
	}

	private void drawPokemon(Canvas canvas, int x, int y, int w, Pokemon p, float animPercent) {
		int xOff, yOff;

		paint.setColor(0xfff47d75);
		canvas.drawRect(x + Helper.dpToPx(20), y, x + (w - Helper.dpToPx(20)) * animPercent, y + Helper.dpToPx(40), paint);
		canvas.drawArc(new RectF(x + (w - Helper.dpToPx(40)) * animPercent, y, x + w * animPercent, y + Helper.dpToPx(40)), -90, 180, true, paint);
		paint.setColor(0xffffa39b);
		canvas.drawRect(x + Helper.dpToPx(22), y + Helper.dpToPx(2), x + (w - Helper.dpToPx(20)) * animPercent, y + Helper.dpToPx(38), paint);
		canvas.drawArc(new RectF(x + (w - Helper.dpToPx(38)) * animPercent, y + Helper.dpToPx(2), x + (w - Helper.dpToPx(2)) * animPercent, y + Helper.dpToPx(38)), -90, 180, true, paint);

		Bitmap ic = getPokemonIcon(p);
		xOff = (int) ((w + Helper.dpToPx(2) - Helper.dpToPx(ic.getWidth())) * animPercent);
		yOff = (Helper.dpToPx(40) - Helper.dpToPx(ic.getHeight())) / 2;
		ruler.set(x + xOff, y + yOff, x + xOff + Helper.dpToPx(ic.getWidth()), y + yOff + Helper.dpToPx(ic.getHeight()));
		canvas.drawBitmap(ic, new Rect(0, 0, ic.getWidth(), ic.getHeight()), ruler, bmPaint);

		paint.setColor(0xff505050);
		canvas.drawArc(new RectF(x, y, x + Helper.dpToPx(40), y + Helper.dpToPx(40)), 0, 360, true, paint);
		paint.setColor(0xff707070);
		canvas.drawArc(new RectF(x + Helper.dpToPx(1), y + Helper.dpToPx(1), x + Helper.dpToPx(39), y + Helper.dpToPx(39)), 0, 360, true, paint);

		paint.setColor(0xff58d080);
		canvas.drawArc(new RectF(x, y, x + Helper.dpToPx(40), y + Helper.dpToPx(40)), -90F, 360F * p.getPercentHP(), true, paint);
		paint.setColor(0xff70f8a8);
		canvas.drawArc(new RectF(x + Helper.dpToPx(1), y + Helper.dpToPx(1), x + Helper.dpToPx(39), y + Helper.dpToPx(39)), -90F, 360F * p.getPercentHP(), true, paint);

		Bitmap pb = getPokeball(p);
		xOff = -Helper.dpToPx(12);
		yOff = -Helper.dpToPx(12);
		ruler.set(x + xOff, y + yOff, x + xOff + Helper.dpToPx(pb.getWidth() * 2), y + yOff + Helper.dpToPx(pb.getHeight() * 2));
		canvas.drawBitmap(pb, new Rect(0, 0, pb.getWidth(), pb.getHeight()), ruler, bmPaint);
	}

	private Bitmap getPokeball(Pokemon pokemon) {
		if(assets.get(10000 + pokemon.ballId) == null)
			try {
				assets.append(10000 + pokemon.ballId, BitmapFactory.decodeStream(getResources().getAssets().open("sprites/items/" + Helper.dataMgr.getPokeballIdentifier(pokemon.ballId) + ".png")));
			} catch(IOException e) {
				e.printStackTrace();
			}
		return assets.get(10000 + pokemon.ballId);
	}

	private Bitmap getPokemonIcon(Pokemon pokemon) {
		if(assets.get(pokemon.id) == null)
			try {
				assets.append(pokemon.id, BitmapFactory.decodeStream(getResources().getAssets().open("sprites/icons/_" + pokemon.id + ".png")));
			} catch(IOException e) {
				e.printStackTrace();
			}
		return assets.get(pokemon.id);
	}

	private void startNextAnimator() {
		for(ValueAnimator va : initVA) {
			if(va.isRunning() || ((Float) va.getAnimatedValue()) == 1F) continue;
			va.start();
			return;
		}
	}

	private boolean doHeal() {
		boolean allHealed = true;
		for(Pokemon p : Helper.dataMgr.getPokemonTeam()) {
			int maxHP = p.getStat(Pokemon.IVStat.HP);
			if(p.currentHp == maxHP) continue;
			float op = maxHP / 100F;
			p.currentHp += op;
			if(p.currentHp >= maxHP) {
				p.currentHp = maxHP;
				p.ailment = 0;
				for(Move m : p.moves)
					m.currentPP = m.maxPP;
			}
			else allHealed = false;
		}
		invalidate();
		return allHealed;
	}
}
