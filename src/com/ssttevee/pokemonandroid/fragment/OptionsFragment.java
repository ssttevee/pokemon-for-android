package com.ssttevee.pokemonandroid.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.view.PokeOptionLayout;

public class OptionsFragment extends Fragment {
	private ViewGroup mRootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = (ViewGroup) inflater.inflate(R.layout.options, container, false);

		for(String str : getResources().getStringArray(R.array.allOptions)) {
			PokeOptionLayout pol = (PokeOptionLayout) inflater.inflate(R.layout.comp_option_entry, null);
			pol.setData(str);

			((ViewGroup) mRootView.findViewById(R.id.optionsContainer)).addView(pol);
		}

		return mRootView;
	}

	@Override
	public void onStart() {
		super.onStart();
	}
}