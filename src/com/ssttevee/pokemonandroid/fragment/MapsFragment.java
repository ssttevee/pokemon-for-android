package com.ssttevee.pokemonandroid.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.ssttevee.pokemonandroid.PokeDroidApplication;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.activity.BattleActivity;
import com.ssttevee.pokemonandroid.adapter.MapsListAdapter;
import com.ssttevee.pokemonandroid.helper.Battle;
import com.ssttevee.pokemonandroid.helper.Pokemon;
import com.ssttevee.pokemonandroid.util.Helper;
import com.ssttevee.pokemonandroid.view.MapsButtonView;
import com.ssttevee.pokemonandroid.view.PokeOptionLayout;

import java.util.Random;

public class MapsFragment extends Fragment {
	private ListView mRootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = (ListView) inflater.inflate(R.layout.maps, container, false);

		mRootView.setAdapter(new MapsListAdapter(getActivity(), Helper.dataMgr.getMapList()));

		BitmapDrawable bg = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.maps_button_bg_1));
		bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		mRootView.setBackgroundDrawable(bg);

		mRootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Battle.Builder builder = new Battle.Builder(getActivity());
				builder.addOpponents(((MapsButtonView) view).getMap().getWildPokemon());
				builder.create().start();
			}
		});

		return mRootView;
	}

	@Override
	public void onStart() {
		super.onStart();
	}
}