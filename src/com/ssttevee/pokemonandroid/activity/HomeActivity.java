package com.ssttevee.pokemonandroid.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.adapter.DrawerMenuAdapter;
import com.ssttevee.pokemonandroid.fragment.DashboardFragment;
import com.ssttevee.pokemonandroid.fragment.MapsFragment;
import com.ssttevee.pokemonandroid.fragment.OptionsFragment;
import com.ssttevee.pokemonandroid.fragment.TownFragment;
import com.ssttevee.pokemonandroid.helper.Pokemon;
import com.ssttevee.pokemonandroid.service.PokePedometerService;
import com.ssttevee.pokemonandroid.util.DataManager;
import com.ssttevee.pokemonandroid.util.Helper;
import com.ssttevee.pokemonandroid.view.PokeHealer3000;
import com.ssttevee.pokemonandroid.view.PokeInfoView;
import com.ssttevee.pokemonandroid.view.TrainerCardView;


public class HomeActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ImageView mHomeIcon;
	private DrawerMenuAdapter mDrawerAdapter;

	private int mCurrentFragment = 0;
	private Integer mSelectedStarter;
	private ValueAnimator mCurrentVA;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Helper.dataMgr = Helper.dataMgr == null ? new DataManager(getApplicationContext()) : Helper.dataMgr;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if(Helper.dataMgr.getPokemonTeam().size() > 0) {
			setContentView(R.layout.drawer_dashboard);

			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
			mDrawerList = (ListView) findViewById(R.id.menuDrawer);
			mHomeIcon = (ImageView) findViewById(R.id.homeIcon);

			mDrawerList.setAdapter(mDrawerAdapter = new DrawerMenuAdapter(this, getResources().getStringArray(R.array.drawerItems)));
			mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
					mDrawerAdapter.setSelectedItem(position);
					mDrawerAdapter.notifyDataSetChanged();

					mCurrentFragment = position;
					changePage(mCurrentFragment);
				}
			});
			startService(new Intent(getBaseContext(), PokePedometerService.class));

			final TrainerCardView tcv = (TrainerCardView) findViewById(R.id.trainerCard);
			tcv.setOnTouchListener(new View.OnTouchListener() {
				float origY = 0;

				@Override
				public boolean onTouch(View view, MotionEvent event) {
					View left = view.findViewById(R.id.tcLeft);
					switch(event.getActionMasked()) {
						case MotionEvent.ACTION_DOWN:
							Rect rect = new Rect(left.getLeft(), left.getTop(), view.findViewById(R.id.tcRight).getRight(), left.getBottom());
							if(!rect.contains((int) event.getX(), (int) event.getY())) return false;

							origY = event.getRawY();
							break;

						case MotionEvent.ACTION_MOVE:
							float trans = tcv.slideHeight + (event.getRawY() - origY);
							tcv.setTranslationY((trans > tcv.slideHeight ? tcv.slideHeight : trans));
							break;

						case MotionEvent.ACTION_UP:
							if(event.getRawY() - origY < -Helper.dpToPx(25)) {
								tcv.hide();
							} else {
								tcv.show();
							}
							break;
					}
					return true;
				}
			});
			findViewById(R.id.pseudoActionBar).setOnTouchListener(new View.OnTouchListener() {
				float origY = 0;

				@Override
				public boolean onTouch(View view, MotionEvent event) {
					View left = tcv.findViewById(R.id.tcLeft);
					if(tcv.isShowing) return false;
					switch(event.getActionMasked()) {
						case MotionEvent.ACTION_DOWN:
							Rect rect = new Rect(left.getLeft(), left.getTop(), tcv.findViewById(R.id.tcRight).getRight(), left.getBottom());
							if(!rect.contains((int) event.getX(), (int) event.getY())) return false;
							tcv.setVisibility(View.VISIBLE);

							origY = event.getRawY();
							break;

						case MotionEvent.ACTION_MOVE:
							float trans = event.getRawY() - origY;
							tcv.setTranslationY((trans > tcv.slideHeight ? tcv.slideHeight : trans));
							break;

						case MotionEvent.ACTION_UP:
							if(event.getRawY() - origY > Helper.dpToPx(25)) {
								tcv.show();
							} else {
								tcv.hide();
							}
							break;
					}
					return true;
				}
			});

			mDrawerLayout.setDrawerListener(new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

				/**
				 * Called when a drawer has settled in a completely closed state.
				 */
				public void onDrawerClosed(View view) {
					super.onDrawerClosed(view);
					mHomeIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("drawable/logo_" + mCurrentFragment, null, getPackageName())));
					tcv.hide();
					invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
				}

				/**
				 * Called when a drawer has settled in a completely open state.
				 */
				public void onDrawerOpened(View drawerView) {
					super.onDrawerOpened(drawerView);
					mHomeIcon.setImageDrawable(getResources().getDrawable(R.drawable.logo_main));
					invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
				}
			});

			mHomeIcon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("drawable/logo_" + mCurrentFragment, null, getPackageName())));

			changePage(mCurrentFragment);
		} else {
			setContentView(R.layout.first_time);
			checkForTrainer();
		}
	}

	private void checkForTrainer() {
		String name = Helper.app.getTrainerName();
		if(name == null || name.equals("")) {
			AlertDialog.Builder d = new AlertDialog.Builder(this);
			final EditText et = new EditText(this);
			d.setTitle("What is your name?");
			d.setView(et);
			d.setCancelable(false);
			d.setNeutralButton("Done", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					Helper.app.setTrainerName(et.getText().toString());
					checkForTrainer();
					dialogInterface.dismiss();
				}
			});
			d.create().show();
		}
	}

	private void changePage(int position) {
		// Create a new fragment and specify the planet to show based on position
		Fragment fragment;
		switch(position) {
			case 1:
				fragment = new MapsFragment();
				break;
			case 2:
				fragment = new TownFragment();
				break;
			case 8:
				fragment = new OptionsFragment();
				break;
			default:
				fragment = new DashboardFragment();
				break;
		}

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.contentFrame, fragment).commit();

		// close the drawer
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	public void pick(final View v) {
		int starterId = Integer.parseInt((String) v.getTag());
		if(mCurrentVA != null && mCurrentVA.isRunning()) {
			if(starterId == mSelectedStarter) return;
			mCurrentVA.reverse();
		}
		if(mSelectedStarter != null && starterId == mSelectedStarter) {
			Pokemon starter = Pokemon.wild(starterId, 5);
			starter.ballId = 1;
			Helper.dataMgr.addToPokemonTeam(starter);
			this.recreate();
			return;
		}

		if(mCurrentVA != null) {
			mCurrentVA.reverse();
			mCurrentVA.removeAllListeners();
		}

		mCurrentVA = new ValueAnimator();
		mCurrentVA.setDuration(500);
		mCurrentVA.setFloatValues(0.1F, 0.5F);
		mCurrentVA.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				setWeight(v, (Float) valueAnimator.getAnimatedValue());
			}
		});
		mCurrentVA.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animator) {
				Toast.makeText(HomeActivity.this, "Tap " + Helper.dataMgr.getPokemonName(mSelectedStarter) + " again to confirm", Toast.LENGTH_SHORT).show();
			}
		});
		mCurrentVA.start();

		mSelectedStarter = starterId;
	}

	private void setWeight(View v, float weight) {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();
		lp.weight = weight;
		v.setLayoutParams(lp);
	}

}