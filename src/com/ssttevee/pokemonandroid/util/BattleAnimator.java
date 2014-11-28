package com.ssttevee.pokemonandroid.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.activity.BattleActivity;
import com.ssttevee.pokemonandroid.helper.Pokemon;
import com.ssttevee.pokemonandroid.view.BattleHealthView;
import com.ssttevee.pokemonandroid.view.PokemonView;

import java.util.ArrayList;

public class BattleAnimator {
	private ArrayList<Action> actions = new ArrayList<Action>();
	private ArrayList<Object> data = new ArrayList<Object>();
	private ArrayList<Boolean> ackRequest = new ArrayList<Boolean>();

	public static BattleAnimator instance;

	private static TextView messageView;
	private boolean acked = true;

	private ValueAnimator valueAnimator;
	private BattleActivity battleActivity;

	public static enum Action {
		MESSAGE, SEND_POKEMON, RETRIEVE_POKEMON, FAINT_POKEMON, GAIN_EXP, LEVEL_UP, DELTA_HP, CALLBACK, THROW_BALL
	}

	public BattleAnimator() {
		instance = this;

		valueAnimator = new ValueAnimator();
		valueAnimator.setInterpolator(new LinearInterpolator());
		valueAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				switch(actions.get(0)) {
					case FAINT_POKEMON:
						getBHV((Pokemon) data.get(0)).setVisibility(View.INVISIBLE);
						break;
					case SEND_POKEMON:
						getBHV((Pokemon) data.get(0)).setVisibility(View.VISIBLE);
						break;
				}

				acked = !ackRequest.get(0);

				actions.remove(0);
				data.remove(0);
				ackRequest.remove(0);

				if(actions.size() < 1)
					battleActivity.findViewById(R.id.ackListener).setVisibility(View.GONE);

				valueAnimator.removeAllUpdateListeners();
				valueAnimator.setDuration(400);

				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						animateNext();
					}
				});
			}
		});
	}

	public void attach(BattleActivity ba) {
		battleActivity = ba;
		battleActivity.findViewById(R.id.ackListener).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!acked) {
					acked = true;
					animateNext();
				}
			}
		});
	}

	public void detach() {
		battleActivity = null;
	}

	public void start() {
		battleActivity.findViewById(R.id.ackListener).setVisibility(View.VISIBLE);
		animateNext();
	}

	public void start(Runnable callback) {
		addAction(Action.CALLBACK, callback);
		start();
	}

	private void animateNext() {
		valueAnimator.setDuration(400);
		if(actions.size() > 0 && acked) {
//			isRunning = true;
			switch(actions.get(0)) {
				case MESSAGE:
					final String message = data.get(0) + " ";
					valueAnimator.setFloatValues(0, message.length());
					valueAnimator.setDuration(message.length()*ValueAnimator.getFrameDelay()*Helper.app.getTextSpeed());
					valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator valueAnimator) {
							messageView.setText(message.substring(0, Math.round((Float) valueAnimator.getAnimatedValue())));
						}
					});
					break;
				case DELTA_HP:
					valueAnimator.setFloatValues(getBHV((Pokemon) data.get(0)).getCurrentHP(), ((Pokemon) data.get(0)).currentHp);
					valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator valueAnimator) {
							BattleHealthView bhv = getBHV((Pokemon) data.get(0));
							bhv.setCurrentHP((Float) valueAnimator.getAnimatedValue());
							bhv.invalidate();
						}
					});
					break;
				case GAIN_EXP:
					valueAnimator.setFloatValues(getBHV((Pokemon) data.get(0)).getCurrentExpProgress(), ((Pokemon) data.get(0)).getLevelProgress() > 1 ? 1 : ((Pokemon) data.get(0)).getLevelProgress());
					valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator valueAnimator) {
							BattleHealthView bhv = getBHV((Pokemon) data.get(0));
							bhv.setCurrentExpProgress((Float) valueAnimator.getAnimatedValue());
							bhv.invalidate();
						}
					});
					break;
				case LEVEL_UP:
					valueAnimator.setFloatValues(0, 1);
					valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator valueAnimator) {
							BattleHealthView bhv = getBHV((Pokemon) data.get(0));
							bhv.setLevelUpProgress((Float) valueAnimator.getAnimatedValue());
							bhv.invalidate();
						}
					});
					break;
				case FAINT_POKEMON:
					valueAnimator.setFloatValues(0, 1);
					valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator valueAnimator) {
							PokemonView pv = getPV((Pokemon) data.get(0));
							pv.setTranslationY(pv.getHeight() * (Float) valueAnimator.getAnimatedValue());
						}
					});
					break;
				case RETRIEVE_POKEMON:
					getBHV((Pokemon) data.get(0)).setVisibility(View.INVISIBLE);
					valueAnimator.setFloatValues(1, 0);
					valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator valueAnimator) {
							getPV((Pokemon) data.get(0)).setScaleX((Float) valueAnimator.getAnimatedValue());
							getPV((Pokemon) data.get(0)).setScaleY((Float) valueAnimator.getAnimatedValue());
						}
					});
					break;
				case SEND_POKEMON:
					valueAnimator.setFloatValues(0, 1);
					valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator valueAnimator) {
							getPV((Pokemon) data.get(0)).setScaleX((Float) valueAnimator.getAnimatedValue());
							getPV((Pokemon) data.get(0)).setScaleY((Float) valueAnimator.getAnimatedValue());
						}
					});
					break;
				case THROW_BALL:
					valueAnimator.setFloatValues(0, 1);
					valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator valueAnimator) {
							Float[] positions = (Float[]) data.get(0);
							Float val = (Float) valueAnimator.getAnimatedValue();
							View pokeball = battleActivity.findViewById(R.id.pokeball);
							pokeball.setTranslationX(positions[0] + val * positions[2]);
							pokeball.setTranslationY((float) (positions[1] + positions[3] * (-Math.pow(1.6 * val - 1.1, 2) + 1.25)));
						}
					});
					break;
				case CALLBACK:
					((Runnable) data.get(0)).run();
					break;
			}

			valueAnimator.start();
		}
	}

	public void pause() {
//		valueAnimator.pause();
	}

	public void resume() {
//		valueAnimator.resume();
	}

	public void cancel() {
		valueAnimator.cancel();
		actions.clear();
		data.clear();
		ackRequest.clear();
	}

	private BattleHealthView getBHV(Pokemon pkmn) {
		return (pkmn.isEnemy ? battleActivity.enemyBHV : battleActivity.allyBHV);
	}

	private PokemonView getPV(Pokemon pkmn) {
		return (pkmn.isEnemy ? battleActivity.enemyPV : battleActivity.allyPV);
	}

	public void setMessageView(TextView view) {
		messageView = view;
	}

	public void addAction(Action a, Object datum) {
		addAction(a, datum, false);
	}

	public void addAction(Action a, Object datum, boolean acknowledge) {
		actions.add(a);
		data.add(datum);
		ackRequest.add(acknowledge);
	}

	public void injectAction(Action a, Object datum, boolean acknowledge) {
		actions.add(1, a);
		data.add(1, datum);
		ackRequest.add(1, acknowledge);
	}

	public static BattleAnimator getInstance() {
		return instance == null ? instance = new BattleAnimator() : instance;
	}
}
