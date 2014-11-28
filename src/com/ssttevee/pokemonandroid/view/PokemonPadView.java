package com.ssttevee.pokemonandroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.helper.Pokemon;
import com.ssttevee.pokemonandroid.util.Helper;

import java.util.Random;

public class PokemonPadView extends RelativeLayout {
	private Pokemon pokemon;

	public PokemonPadView(Context context, AttributeSet attrs) {
		super(context, attrs);

		Random random = new Random();
//		pokemon = Pokemon.wild(random.nextInt(151) + 1, random.nextInt(100) + 1);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) Math.ceil(Helper.dpToPx(57))));

		((TextView) findViewById(R.id.healthNumbers)).setTypeface(Helper.Fonts.getPkmnFL());
		((TextView) findViewById(R.id.pokemonLevel)).setTypeface(Helper.Fonts.getPkmnFL());
		((TextView) findViewById(R.id.pokemonName)).setTypeface(Helper.Fonts.getPkmnFL());

		if(pokemon != null) setPokemon(pokemon);
	}

	public void setPokemon(Pokemon pkmn) {
		pokemon = pkmn;

		((TextView) findViewById(R.id.healthNumbers)).setText(pokemon.getCurrentHP() + "/" + pokemon.getStat(Pokemon.IVStat.HP));
		((TextView) findViewById(R.id.pokemonLevel)).setText("Lv " + pokemon.level);
		((TextView) findViewById(R.id.pokemonName)).setText(pokemon.nickname);

		((PokemonIconView) findViewById(R.id.pokemon)).setPokemon(pokemon.id);

		((HealthBar) findViewById(R.id.healthBar)).setPokemon(pokemon);

		invalidate();
	}

	public Pokemon getPokemon() {
		return pokemon;
	}
}
