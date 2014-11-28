package com.ssttevee.pokemonandroid.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

public class PokemonIconView extends View {
	private Context context;
	private int currentPokemon = 1;
	private int prevPokemon = 0;

	private Paint nnPaint;
	private Bitmap pokemonIcon;

	public PokemonIconView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		nnPaint = new Paint();
		nnPaint.setAntiAlias(false);
		nnPaint.setFilterBitmap(false);
		nnPaint.setDither(false);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if(!isInEditMode()) {
			if(currentPokemon != prevPokemon) try {
				InputStream is = context.getAssets().open("sprites/icons/_" + currentPokemon + ".png");
				pokemonIcon = BitmapFactory.decodeStream(is);
				prevPokemon = currentPokemon;
			} catch(IOException e) {
				System.out.println(e.getMessage());
			}
			canvas.drawBitmap(pokemonIcon, new Rect(0, 0, pokemonIcon.getWidth(), pokemonIcon.getHeight()), new Rect(0, 0, getWidth(), getHeight()), nnPaint);
		}
	}

	public void setPokemon(int pkmnId) {
		currentPokemon = pkmnId;

		invalidate();
	}
}
