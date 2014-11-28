package com.ssttevee.pokemonandroid.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.ssttevee.pokemonandroid.PokeDroidApplication;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.helper.Pokemon;
import com.ssttevee.pokemonandroid.util.Helper;
import com.ssttevee.pokemonandroid.view.PokeInfoView;
import com.ssttevee.pokemonandroid.view.PokemonPadView;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {
	private PokeDroidApplication mApp;
	private Handler mHandler;
	private ViewGroup mRootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Get the Application
		mApp = (PokeDroidApplication) getActivity().getApplication();

		mHandler = new Handler();

		// Setup the root view
		mRootView = (ViewGroup) inflater.inflate(R.layout.home, container, false);

		System.out.println("nothing");

		return mRootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		ArrayList<Pokemon> pokemonTeam = Helper.dataMgr.getPokemonTeam();
		for (int i = 0; i < pokemonTeam.size(); i++) {
			final PokemonPadView ppv = (PokemonPadView) View.inflate(getActivity(), R.layout.comp_pkmn_pad, null);
			ppv.setPokemon(pokemonTeam.get(i));
			((ViewGroup) ((ViewGroup) mRootView.findViewById(R.id.pokemonTeam)).getChildAt(i % 2)).addView(ppv);

			ppv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Dialog dialog = new Dialog(getActivity());
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(new PokeInfoView(getActivity(), ppv.getPokemon()));
					dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
					dialog.show();
				}
			});
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		((ViewGroup) ((ViewGroup) mRootView.findViewById(R.id.pokemonTeam)).getChildAt(0)).removeAllViews();
		((ViewGroup) ((ViewGroup) mRootView.findViewById(R.id.pokemonTeam)).getChildAt(1)).removeAllViews();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
}