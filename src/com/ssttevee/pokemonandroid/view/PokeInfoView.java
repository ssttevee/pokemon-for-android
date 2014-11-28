package com.ssttevee.pokemonandroid.view;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.helper.Move;
import com.ssttevee.pokemonandroid.helper.Pokemon;
import com.ssttevee.pokemonandroid.helper.PokemonType;
import com.ssttevee.pokemonandroid.util.Helper;

import java.io.IOException;
import java.io.InputStream;

public class PokeInfoView extends View {
	private Context context;

	private Paint mShapePaint;
	private TextPaint mLabelPaint;
	private TextPaint mTextPaint;

	private Rect mRuler = new Rect();

	private NinePatchDrawable mLabelBg;
	private NinePatchDrawable mViewBorder;
	private BitmapDrawable mPokemonViewBg;
	private BitmapDrawable mMaleSymbol;
	private BitmapDrawable mFemaleSymbol;
	private BitmapDrawable mStatBorder;

	private Pokemon pokemon;

	public PokeInfoView(Context context, Pokemon pkmn) {
		super(context);
		this.context = context;
		this.pokemon = pkmn;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(pokemon.moves.size() <= 2)
			setMeasuredDimension(Helper.dpToPx(302), Helper.dpToPx(215));
		else
			setMeasuredDimension(Helper.dpToPx(302), Helper.dpToPx(245));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(mShapePaint == null) {
			mShapePaint = new Paint();
			mShapePaint.setAntiAlias(false);
			mShapePaint.setFilterBitmap(false);
			mShapePaint.setDither(false);
		}
		if(mViewBorder == null) {
			mViewBorder = (NinePatchDrawable) getResources().getDrawable(R.drawable.stat_view_border);
		}
		if(mStatBorder == null) {
			mStatBorder = (BitmapDrawable) getResources().getDrawable(R.drawable.bg_stats);
			mStatBorder.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		}

		mViewBorder.setBounds(0, 0, getWidth(), getHeight());
		mViewBorder.draw(canvas);

		mStatBorder.setBounds(Helper.dpToPx(2), Helper.dpToPx(2), getWidth() - Helper.dpToPx(2), getHeight() - Helper.dpToPx(2));
		mStatBorder.draw(canvas);

		drawPokemonView(canvas, pokemon, Helper.dpToPx(4), Helper.dpToPx(4), getWidth()/2 - Helper.dpToPx(8));

		String[][] stats = new String[][] {
				{"HIT POINTS", pokemon.getCurrentHP() + "/" + pokemon.getStat(Pokemon.IVStat.HP)},
				{"ATTACK", pokemon.getStat(Pokemon.IVStat.ATTACK) + ""},
				{"DEFENCE", pokemon.getStat(Pokemon.IVStat.DEFENSE) + ""},
				{"SP ATTACK", pokemon.getStat(Pokemon.IVStat.SP_ATK) + ""},
				{"SP DEFENCE", pokemon.getStat(Pokemon.IVStat.SP_DEF) + ""},
				{"SPEED", pokemon.getStat(Pokemon.IVStat.SPEED) + ""},
				{"EXPERIENCE", pokemon.experience + ""},
				{"NEXT LEVEL", pokemon.getExperienceToNextLevel() + ""},
				{"NUMBER", pokemon.id + ""},
				{"NAME", pokemon.nickname},
				{"ORIG TRAINER", pokemon.originalTrainer},
				{"ABILITY", Helper.dataMgr.getAbilityName(pokemon.abilityId)},
				{"OT ID", pokemon.otId + ""},
				{"NATURE", Helper.dataMgr.getNatureName(pokemon.natureId)},
				{"TYPE", TextUtils.join("\u00A7", pokemon.getTypes())},
				{"ITEM", "NONE"},
		};

		int remainingWidth = getWidth() - Helper.dpToPx(152);
		for(int i = 0; i < stats.length; i++) {
			int mod = 0;
			if(i >= 8) {
				drawStat(canvas, stats[i][0], stats[i][1], ((i - 8)%2) * getWidth()/2 + Helper.dpToPx(4),
						Helper.dpToPx((int) (5 + (8 + Math.floor((i - 8)/2)) * 15)),
						getWidth()/2 - Helper.dpToPx(8)
				);
			} else {
				drawStat(canvas, stats[i][0], stats[i][1], getWidth()/2 + Helper.dpToPx(4), Helper.dpToPx(5 + i * 15), getWidth()/2 - Helper.dpToPx(8));
			}
		}

		int i = 0;
		for(Move m : pokemon.moves) {
			drawMove(canvas, m, (i%2) * getWidth()/2 + Helper.dpToPx(4), (int) (Helper.dpToPx(185) + Helper.dpToPx(30)*Math.floor(i/2)), getWidth()/2 - Helper.dpToPx(8));
			i++;
		}
	}

	private void drawPokemonView(Canvas canvas, Pokemon pkmn, int x, int y, int w) { // height should be ~117dp
		if(w < Helper.dpToPx(104)) w = Helper.dpToPx(104);
		if(mPokemonViewBg == null) {
			mPokemonViewBg = (BitmapDrawable) getResources().getDrawable(R.drawable.stat_pokemon_view_bg);
			mPokemonViewBg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		}
		if(mMaleSymbol == null)
			mMaleSymbol = (BitmapDrawable) getResources().getDrawable(R.drawable.symbol_male_big);
		if(mFemaleSymbol == null)
			mFemaleSymbol = (BitmapDrawable) getResources().getDrawable(R.drawable.symbol_female_big);

		int textHeight = Helper.dpToPx(11);
		getTextWithShadowBounds(pkmn.nickname, 12, mRuler);
		if(textHeight < mRuler.height()) textHeight = mRuler.height();
		getTextWithShadowBounds("Lv" + pkmn.level, 12, mRuler);
		if(textHeight < mRuler.height()) textHeight = mRuler.height();

		mShapePaint.setColor(Color.parseColor("#606878"));
		mRuler.set(x, y, x + w, y + textHeight + Helper.dpToPx(96) + Helper.dpToPx(10));
		canvas.drawRect(mRuler, mShapePaint);
		mShapePaint.setColor(Color.parseColor("#c8a8e8"));
		mRuler.set(x + Helper.dpToPx(2), y + Helper.dpToPx(2), x + w - Helper.dpToPx(2), y + textHeight + Helper.dpToPx(96) + Helper.dpToPx(8));
		canvas.drawRect(mRuler, mShapePaint);
		mShapePaint.setColor(Color.parseColor("#b088d0"));
		mRuler.set(x + Helper.dpToPx(3), y + textHeight + Helper.dpToPx(4), x + w - Helper.dpToPx(3), y + textHeight + Helper.dpToPx(96) + Helper.dpToPx(7));
		canvas.drawRect(mRuler, mShapePaint);

		drawTextWithShadow(canvas, "Lv" + pkmn.level, x + Helper.dpToPx(5), y + textHeight + Helper.dpToPx(3), 12);
		getTextWithShadowBounds(pkmn.nickname, 12, mRuler);
		drawTextWithShadow(canvas, pkmn.nickname, x + (w - mRuler.width()) / 2, y + textHeight + Helper.dpToPx(3), 12);

		mRuler.set(x + w - Helper.dpToPx(10), y + Helper.dpToPx(3), x + w - Helper.dpToPx(4), y + textHeight + Helper.dpToPx(3));
		if(pkmn.gender == Pokemon.Gender.MALE) {
			mMaleSymbol.setBounds(mRuler);
			mMaleSymbol.draw(canvas);
		} else if(pkmn.gender == Pokemon.Gender.FEMALE) {
			mFemaleSymbol.setBounds(mRuler);
			mFemaleSymbol.draw(canvas);
		}

		mRuler.set(x + Helper.dpToPx(4), y + textHeight + Helper.dpToPx(5), x + w - Helper.dpToPx(4), y + textHeight + Helper.dpToPx(96) + Helper.dpToPx(6));
		mPokemonViewBg.setBounds(mRuler);
		mPokemonViewBg.draw(canvas);

		try {
			Bitmap pkmnBitmap = BitmapFactory.decodeStream(context.getAssets().open("sprites/front/_" + pkmn.id + ".png"));
			mRuler.left += (w - Helper.dpToPx(96))/2;
			mRuler.right = mRuler.left + Helper.dpToPx(96);
			canvas.drawBitmap(pkmnBitmap, new Rect(0, 0, pkmnBitmap.getWidth(), pkmnBitmap.getHeight()), mRuler, mShapePaint);

			if(pkmn.ballId > 0) {
				Bitmap ballBitmap = BitmapFactory.decodeStream(context.getAssets().open("sprites/balls/_" + pkmn.ballId + ".png"));
				mRuler.left = x + w - Helper.dpToPx(18);
				mRuler.top = y + textHeight + Helper.dpToPx(96) - Helper.dpToPx(8);
				mRuler.right = mRuler.left + Helper.dpToPx(12);
				mRuler.bottom = mRuler.top + Helper.dpToPx(12);
				canvas.drawBitmap(ballBitmap, new Rect(0, 0, ballBitmap.getWidth(), ballBitmap.getHeight()), mRuler, mShapePaint);
			}
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	private void drawMove(Canvas canvas, Move move, int x, int y, int w) {
		drawBox(canvas, x + Helper.dpToPx(5), y + Helper.dpToPx(1), w - Helper.dpToPx(5), Helper.dpToPx(25));
		drawTypeTag(canvas, PokemonType.get(move), x, y);

		drawTextWithShadowDark(canvas, move.getName(), x + Helper.dpToPx(42), y + Helper.dpToPx(13), 15);

		getTextWithShadowBounds(move.currentPP + "/" + move.maxPP, 13, mRuler);
		drawTextWithShadowDark(canvas, move.currentPP + "/" + move.maxPP, x + w - mRuler.width() - Helper.dpToPx(2), y + Helper.dpToPx(25), 13);

		drawTextWithShadowDark(canvas, "PP", x + w - Helper.dpToPx(46), y + Helper.dpToPx(25), 12);
	}

	private void drawStat(Canvas canvas, String statName, String statValue, int x, int y, int w) {
		drawBox(canvas, x + Helper.dpToPx(62), y, w - Helper.dpToPx(62), Helper.dpToPx(11));

		if(statName.equalsIgnoreCase("type")) {
			String[] types = statValue.split("\u00A7");
			for(int i = 0; i < types.length; i++)
				drawTypeTag(canvas, PokemonType.get(Integer.parseInt(types[i])), x + Helper.dpToPx(64 + i * 39), y);
		} else {
			getTextWithShadowBounds(statValue, 12, mRuler);
			if(statName.equalsIgnoreCase("number") || statName.equalsIgnoreCase("name") || statName.equalsIgnoreCase("type") || statName.equalsIgnoreCase("type") || statName.equalsIgnoreCase("ability") || statName.equalsIgnoreCase("nature") || statName.equalsIgnoreCase("item") || statName.equalsIgnoreCase("ot id") || statName.equalsIgnoreCase("orig trainer")) mRuler.right = mRuler.left + w - Helper.dpToPx(66);
			drawTextWithShadowDark(canvas, statValue.toUpperCase(), x + w - mRuler.width() - Helper.dpToPx(1), y + Helper.dpToPx(11), 12);
		}

		drawLabel(canvas, statName, x, y + Helper.dpToPx(1), 60);
	}

	private void drawBox(Canvas canvas, int x, int y, int w, int h) {
		mViewBorder.setBounds(x, y, x + w, y + h);
		mViewBorder.draw(canvas);
	}

	private void drawTypeTag(Canvas canvas, PokemonType type, int x, int y) {
		int w = Helper.dpToPx(38);
		mShapePaint.setColor(type.color);
		mRuler.set(x, y + Helper.dpToPx(1), x + w, y + Helper.dpToPx(10));
		canvas.drawRect(mRuler, mShapePaint);
		mRuler.set(x + Helper.dpToPx(1), y, x + w - Helper.dpToPx(1), y + Helper.dpToPx(11));
		canvas.drawRect(mRuler, mShapePaint);

		getTextWithShadowBounds(type.tag, 12, mRuler);
		drawTextWithShadow(canvas, type.tag, x + (w - mRuler.width())/2 + Helper.dpToPx(1), y + Helper.dpToPx(11) - (Helper.dpToPx(11) - mRuler.height())/2, 12);
	}

	private void drawLabel(Canvas canvas, String text, int x, int y, int w) {
		if(mLabelPaint == null) {
			mLabelPaint = new TextPaint();
			mLabelPaint.setTextSize(Helper.dpToPx(10));
			mLabelPaint.setTypeface(Helper.Fonts.getPkmnFL());
		}
		if(mLabelBg == null)
			mLabelBg = (NinePatchDrawable) getResources().getDrawable(R.drawable.stat_label_bg);

		int pxWidth = Helper.dpToPx(w);
		float textWidth = mLabelPaint.measureText(text);

		// resize 9patch according to w
		mLabelBg.setBounds(x, y + Helper.dpToPx(2), x + pxWidth, y + Helper.dpToPx(8));
		mLabelBg.draw(canvas);

		// custom
		mLabelPaint.setStyle(Paint.Style.STROKE);
		mLabelPaint.setColor(Color.parseColor("#788090"));
		mLabelPaint.setStrokeWidth(Helper.dpToPx(2));
		canvas.drawText(text, x + (pxWidth - textWidth) / 2, y + Helper.dpToPx(8), mLabelPaint);

		// custom
		mLabelPaint.setStyle(Paint.Style.FILL);
		mLabelPaint.setColor(Color.parseColor("#ffffff"));
		mLabelPaint.setStrokeWidth(0);
		canvas.drawText(text, x + (pxWidth - textWidth) / 2, y + Helper.dpToPx(8), mLabelPaint);
	}

	private void drawTextWithShadow(Canvas canvas, String text, int x, int y, int size) {
		if(mTextPaint == null) {
			mTextPaint = new TextPaint();
			mTextPaint.setTypeface(Helper.Fonts.getPkmnFL());
		}
		mTextPaint.setTextSize(Helper.dpToPx(size));

		mTextPaint.setColor(Color.parseColor("#606060"));
		canvas.drawText(text, x, y, mTextPaint);

		mTextPaint.setColor(Color.parseColor("#f8f8f8"));
		canvas.drawText(text, x - Helper.dpToPx(1), y - Helper.dpToPx(1), mTextPaint);
	}

	private void drawTextWithShadowDark(Canvas canvas, String text, int x, int y, int size) {
		if(mTextPaint == null) {
			mTextPaint = new TextPaint();
			mTextPaint.setTypeface(Helper.Fonts.getPkmnFL());
		}
		mTextPaint.setTextSize(Helper.dpToPx(size));

		mTextPaint.setColor(Color.parseColor("#d8d8c0"));
		canvas.drawText(text, x, y, mTextPaint);

		mTextPaint.setColor(Color.parseColor("#404040"));
		canvas.drawText(text, x - Helper.dpToPx(1), y - Helper.dpToPx(1), mTextPaint);
	}

	private void getTextWithShadowBounds(String text, int size, Rect r) {
		if(mTextPaint == null) {
			mTextPaint = new TextPaint();
			mTextPaint.setTypeface(Helper.Fonts.getPkmnFL());
		}
		mTextPaint.setTextSize(Helper.dpToPx(size));
		mTextPaint.getTextBounds(text, 0, text.length(), r);
		r.right += Helper.dpToPx(1);
		r.bottom += Helper.dpToPx(1);
	}
}