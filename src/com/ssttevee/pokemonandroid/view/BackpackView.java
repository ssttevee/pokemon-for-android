package com.ssttevee.pokemonandroid.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.activity.BattleActivity;
import com.ssttevee.pokemonandroid.adapter.BackpackAdapter;
import com.ssttevee.pokemonandroid.helper.Battle;
import com.ssttevee.pokemonandroid.util.Helper;

import java.util.ArrayList;

public class BackpackView extends RelativeLayout {
	private int selectedTab;
	private BackpackAdapter itemsAdapter;
	private LinearLayout tabContainer;

	private int item;

	public BackpackView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		// Create and add the tabs to the view
		addTabs(Helper.dataMgr.getItemPocketsNames());

		// Initialize list view
		ListView listView = (ListView) findViewById(R.id.backpackItems);
		listView.setAdapter(itemsAdapter = new BackpackAdapter(getContext()));
		final PopupMenu.OnMenuItemClickListener menuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				switch(menuItem.getItemId()) {
					case R.id.use:
						useItem(item);
						return true;
					case R.id.give:
						giveItem(item);
						return true;
					case R.id.discard:
						discardItem(item);
						return true;
				}
				return false;
			}
		};
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				item = (int) l;
				PopupMenu popupMenu = new PopupMenu(getContext(), view);
				popupMenu.getMenuInflater().inflate(R.menu.backpack_items, popupMenu.getMenu());
				popupMenu.setOnMenuItemClickListener(menuItemClickListener);
				popupMenu.show();
			}
		});

		// Select the first tab
		select(0);
	}

	private void useItem(int itemId) {
		if(isBattle()) {
			BattleActivity ba = (BattleActivity) getContext();
			switch(itemId) {
				case 1: // Master ball
					ba.throwBall(4);
					break;
				case 2: // Ultra ball
					ba.throwBall(3);
					break;
				case 3: // Great ball
					ba.throwBall(2);
					break;
				case 4: // Poke ball
					ba.throwBall(1);
					break;
			}
		}
	}

	private void giveItem(int itemId) {

	}

	private void discardItem(int itemId) {

	}

	private boolean isBattle() {
		return getContext() instanceof BattleActivity;
	}

	private void addTabs(ArrayList<String> tabs) {
		tabContainer = (LinearLayout) findViewById(R.id.backpackTabs);
		for(int i = 0; i < tabs.size(); i++) {
			if(i == 5) continue;

			// Create and setup a new button
			TextView tab = new TextView(getContext());
			tab.setBackgroundDrawable(getResources().getDrawable(R.drawable.backpack_tab));
			tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			tab.setText(tabs.get(i));
			tab.setTypeface(Helper.Fonts.getPkmnFL());
			tab.setTag(i + 1);
			tab.setMinHeight(Helper.dpToPx(48));
			tab.setGravity(Gravity.CENTER);

			// Add a bottom margin to all tabs except the last
			if(i < tabs.size() - 1) {
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				layoutParams.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.backpackTabBottomMargin));
				tab.setLayoutParams(layoutParams);
			}

			// Set the selected tab when clicked
			tab.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(tabContainer.indexOfChild(v) == selectedTab) return;
					select(tabContainer.indexOfChild(v));
				}
			});

			// Finally add the button to the view
			tabContainer.addView(tab);
		}
	}

	private void select(int position) {
		// Update the background of the new and old tab
		tabContainer.getChildAt(selectedTab).setBackgroundDrawable(getResources().getDrawable(R.drawable.backpack_tab));
		tabContainer.getChildAt(position).setBackgroundDrawable(getResources().getDrawable(R.drawable.backpack_tab_selected));

		// Populate list view with items from the new tab
		itemsAdapter.setItems(Helper.dataMgr.getItemsByPocket((Integer) tabContainer.getChildAt(position).getTag()));

		// Globally update the selected tab
		selectedTab = position;
	}
}
