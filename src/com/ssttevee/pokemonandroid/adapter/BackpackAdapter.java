package com.ssttevee.pokemonandroid.adapter;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.util.Helper;

public class BackpackAdapter extends BaseAdapter {
	private Context context;
	private SparseIntArray items;

	public BackpackAdapter(Context context) {
		this(context, new SparseIntArray());
	}

	public BackpackAdapter(Context context, SparseIntArray items) {
		this.context = context;
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.keyAt(position);
	}

	@Override
	public long getItemId(int position) {
		return items.keyAt(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Create or recycle view
		RelativeLayout item;
		if(convertView == null) {
			item = (RelativeLayout) View.inflate(context, R.layout.lv_backpack, null);
			for(int i = 0; i < item.getChildCount(); i++)
				((TextView) item.getChildAt(i)).setTypeface(Helper.Fonts.getPkmnFL());
		} else {
			item = (RelativeLayout) convertView;
		}

		((TextView) item.findViewById(android.R.id.summary)).setText(Helper.dataMgr.getItemName(items.keyAt(position)));

		if(Helper.dataMgr.isItemCountable(items.keyAt(position)))
			((TextView) item.findViewById(android.R.id.text1)).setText(items.valueAt(position) + "");
		else
			((TextView) item.findViewById(android.R.id.text1)).setText("1");



		return item;
	}

	public void setItems(SparseIntArray items) {
		this.items = items;
		notifyDataSetChanged();
	}
}
