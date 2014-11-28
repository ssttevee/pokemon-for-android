package com.ssttevee.pokemonandroid.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.view.DrawerItemEntry;

public class DrawerMenuAdapter extends BaseAdapter {

	private final String[] menuItems;
	private final Context context;

	private int mSelectedItem = 0;

	public DrawerMenuAdapter(Context context, String[] menuItems) {
		this.context = context;
		this.menuItems = menuItems;
	}
	@Override
	public int getCount() {
		return menuItems.length;
	}

	@Override
	public Object getItem(int position) {
		return menuItems[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		DrawerItemEntry die;
		if(convertView == null) die = (DrawerItemEntry) View.inflate(context, R.layout.lv_drawer, null);
		else die = (DrawerItemEntry) convertView;

		((TextView) die.findViewById(android.R.id.text1)).setText(menuItems[position]);

		if (position == mSelectedItem) {
			die.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border_4));
		} else {
			die.setBackgroundDrawable(null);
		}

		final float scale = context.getResources().getDisplayMetrics().density;

		die.setPadding((int) (20 * scale + 0.5f), (int) (10 * scale + 0.5f), (int) (20 * scale + 0.5f), (int) (10 * scale + 0.5f));

		return die;
	}

	public int getSelectedItem() {
		return mSelectedItem;
	}

	public void setSelectedItem(int selectedItem) {
		this.mSelectedItem = selectedItem;
	}
}
