package com.ssttevee.pokemonandroid.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.helper.Map;
import com.ssttevee.pokemonandroid.util.Helper;
import com.ssttevee.pokemonandroid.view.MapsButtonView;

import java.util.ArrayList;
import java.util.Collections;

public class MapsListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Map> mMapsList;

	public MapsListAdapter(Context ctx, ArrayList<Map> maps) {
		context = ctx;
		mMapsList = maps;
		Collections.reverse(mMapsList);
	}

	@Override
	public int getCount() {
		return mMapsList.size();
	}

	@Override
	public Object getItem(int position) {
		return mMapsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		MapsButtonView mbv;

		if(convertView == null) mbv = (MapsButtonView) View.inflate(context, R.layout.comp_maps_button, null);
		else mbv = (MapsButtonView) convertView;

		mbv.setMap(mMapsList.get(position));

		return mbv;
	}
}
