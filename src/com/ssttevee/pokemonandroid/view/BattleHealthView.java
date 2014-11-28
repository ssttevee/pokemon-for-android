package com.ssttevee.pokemonandroid.view;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextPaint;
import android.view.View;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.helper.Pokemon;
import com.ssttevee.pokemonandroid.util.Helper;

public class BattleHealthView extends View {
	private Pokemon pokemon;
	private HealthBar healthBar;

	private NinePatchDrawable healthBoard;
	private Bitmap whiteOverlay;
	private BitmapDrawable genderSymbol;
	private BitmapDrawable expBar;
	private TextPaint mTextPaint;
	private Paint mShapePaint;
	private Paint mOverlayPaint;
	private PorterDuffColorFilter levelUpOverlayFilter;
	private Rect mRuler = new Rect();
	
	private float currentExpProgress;
	private boolean leveledUp = false;

	public BattleHealthView(Context context, Pokemon pkmn) {
		super(context);

		expBar = (BitmapDrawable) getResources().getDrawable(R.drawable.exp_bar_bg);
		expBar.setTileModeX(Shader.TileMode.REPEAT);

		mTextPaint = new TextPaint();
		mTextPaint.setTypeface(Helper.Fonts.getPkmnFL());

		mShapePaint = new Paint();
		mShapePaint.setColor(0xff40c8f8);

		levelUpOverlayFilter = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
		mOverlayPaint = new Paint();
		mOverlayPaint.setColorFilter(levelUpOverlayFilter);
		mOverlayPaint.setAlpha(0);

		setPokemon(pkmn);
		setVisibility(INVISIBLE);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		//Measure Width
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			width = (int) Math.min(widthSize * 0.4D, widthSize);
		} else {
			width = (int) (widthSize * 0.4D * (pokemon.isEnemy ? 100D : 104D) / 104D);
		}

		//Measure Height
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			if(pokemon.isEnemy)
				height = (int) Math.min(29D / 100D * width, heightSize);
			else
				height = (int) Math.min(37D / 104D * width, heightSize);
		} else {
			height = (int) (37D / 104D * width);
		}

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		healthBoard.setBounds(0, 0, getWidth(), getHeight());
		healthBoard.draw(canvas);

		if(whiteOverlay == null) {
			whiteOverlay = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(whiteOverlay);
			healthBoard.draw(c);
		}

		if(pokemon.isEnemy) {
			healthBar.layout(0,0, (int) (66D/100D*getWidth()), Helper.dpToPx(7));
			canvas.save();
			canvas.translate((23F / 100F * getWidth()), (17F / 29F * getHeight()));
			healthBar.draw(canvas);
			canvas.restore();

			getTextWithShadowBounds("Lv" + pokemon.level, 14, mRuler);
			drawTextWithShadow(canvas, "Lv" + pokemon.level, getWidth() - Helper.dpToPx(14) - mRuler.width(), (int) (14F / 29F * getHeight()), 14);

			getTextWithShadowBounds(pokemon.nickname, 14, mRuler);
			drawTextWithShadow(canvas, pokemon.nickname, (int) (6F / 100F * getWidth()), (int) (14F / 29F * getHeight()), 14);

			int x = (int) (6F / 100F * getWidth() + mRuler.width());
			int y = (int) (14F / 29F * getHeight());
			int h = mRuler.height() + Helper.dpToPx(1);
			int w = (int) (5D / 8D * h);
			genderSymbol.setBounds(x, y - h, x + w, y);
			genderSymbol.draw(canvas);
		} else {
			healthBar.layout(0,0, (int) (66D/104D*getWidth()), Helper.dpToPx(7));
			canvas.save();
			canvas.translate((32F / 104F * getWidth()), (17F / 37F * getHeight()));
			healthBar.draw(canvas);
			canvas.restore();

			String hp = pokemon.getCurrentHP() + "/" + pokemon.getStat(Pokemon.IVStat.HP);
			getTextWithShadowBounds(hp, 12, mRuler);
			drawTextWithShadow(canvas, hp, getWidth() - Helper.dpToPx(9) - mRuler.width(), getHeight() - Helper.dpToPx(8), 12);

			getTextWithShadowBounds("Lv" + pokemon.level, 14, mRuler);
			drawTextWithShadow(canvas, "Lv" + pokemon.level, getWidth() - Helper.dpToPx(9) - mRuler.width(), (int) (14F / 37F * getHeight()), 14);

			getTextWithShadowBounds(pokemon.nickname, 14, mRuler);
			drawTextWithShadow(canvas, pokemon.nickname, (int) (15F / 104F * getWidth()), (int) (14F / 37F * getHeight()), 14);

			int x = (int) (15F / 104F * getWidth() + mRuler.width());
			int y = (int) (14F / 37F * getHeight());
			int h = mRuler.height() + Helper.dpToPx(1);
			int w = (int) (5D / 8D * h);
			genderSymbol.setBounds(x, y - h, x + w, y);
			genderSymbol.draw(canvas);

			expBar.setBounds((int) (14D/104D*getWidth()), (int) (34D/37D*getHeight()), (int) (96D/104D*getWidth()), (int) (36D/37D*getHeight()));
			expBar.draw(canvas);
			canvas.drawRect(14F/104F*getWidth(), 34F/37F*getHeight(), 14F/104F*getWidth() + currentExpProgress*82F/104F*getWidth(), 36F/37F*getHeight(), mShapePaint);
		}

		if(mOverlayPaint.getAlpha() > 0) {
			canvas.drawBitmap(whiteOverlay, 0, 0, mOverlayPaint);
		}
	}

	private void drawTextWithShadow(Canvas canvas, String text, int x, int y, int size) {
		mTextPaint.setTextSize(Helper.dpToPx(size));

		mTextPaint.setColor(Color.parseColor("#d8d0b0"));
		canvas.drawText(text, x, y, mTextPaint);

		mTextPaint.setColor(Color.parseColor("#404040"));
		canvas.drawText(text, x - Helper.dpToPx(1), y - Helper.dpToPx(1), mTextPaint);
	}

	private void getTextWithShadowBounds(String text, int size, Rect r) {
		mTextPaint.setTextSize(Helper.dpToPx(size));
		mTextPaint.getTextBounds(text, 0, text.length(), r);
		r.right += Helper.dpToPx(1);
		r.bottom += Helper.dpToPx(1);
	}

	public void setPokemon(Pokemon pkmn) {
		pokemon = pkmn;
		currentExpProgress = pokemon.getLevelProgress();
		
		if(healthBar == null) healthBar = new HealthBar(getContext(), pkmn);
		healthBar.setPokemon(pokemon);

		healthBoard = (NinePatchDrawable) getResources().getDrawable(pkmn.isEnemy ? R.drawable.battle_health_board_enemy : R.drawable.battle_health_board);

		genderSymbol = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), pkmn.gender == Pokemon.Gender.MALE ? R.drawable.symbol_male_big : R.drawable.symbol_female_big));

		invalidate();
	}

	public float getCurrentHP() {
		return healthBar.currentHP;
	}

	public void setCurrentHP(float newHP) {
		healthBar.currentHP = newHP;
	}
	public float getCurrentExpProgress() {
		return currentExpProgress;
	}
	public void setCurrentExpProgress(float currentExpProgress) {
	this.currentExpProgress = currentExpProgress;
}

	public void setLevelUpProgress(float progress) {
		int alpha = (int) (((-4) * Math.pow(progress-0.5, 2) + 1) * 255);
		mOverlayPaint.setAlpha(alpha);
		if(progress >= 0.5 && !leveledUp) {
			leveledUp = true;
			pokemon.levelUp();
			currentExpProgress = 0;
			healthBar.currentHP = pokemon.currentHp;
		}
		if(progress == 1) leveledUp = false;
	}
}
