package com.ssttevee.pokemonandroid.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.helper.Battle;
import com.ssttevee.pokemonandroid.helper.Pokemon;
import com.ssttevee.pokemonandroid.helper.QueuedMove;
import com.ssttevee.pokemonandroid.util.BattleAnimator;
import com.ssttevee.pokemonandroid.util.Helper;
import com.ssttevee.pokemonandroid.view.BackpackView;
import com.ssttevee.pokemonandroid.view.BattleHealthView;
import com.ssttevee.pokemonandroid.view.MoveButtonView;
import com.ssttevee.pokemonandroid.view.PokemonView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class BattleActivity extends Activity {
	private Random rnd = new Random();
 
	public Battle battle;

	private int currentAlly = 0;
	public PokemonView allyPV;
	public BattleHealthView allyBHV;

	private int currentEnemy = 0;
	public PokemonView enemyPV;
	public BattleHealthView enemyBHV;

	private boolean movesShowing = false;
	public String[] capFailMsgs = new String[] {"Oh no! The pokemon broke free!", "Aww! It appeared to be caught!", "Argh! Almost had it!", "Shootit was so close too!"};

	private ArrayList<QueuedMove> movesQueue = new ArrayList<QueuedMove>();
	private BattleAnimator animator;
	private	View.OnClickListener moveButtonsListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			disableMoves();
 
			movesQueue.add(new QueuedMove(getAlly(), ((MoveButtonView) view).move, getEnemy()));
			movesQueue.add(new QueuedMove(getEnemy(), getEnemy().moves.get(rnd.nextInt(getEnemy().moves.size())), getAlly()));
 
			runTurn();
		}
 
	};
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView (R.layout.battle);
 
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		battle = extras.getParcelable("battle");
 
		animator = BattleAnimator.getInstance();
		animator.setMessageView((TextView) findViewById(R.id.battleDialog));
		animator.attach(this);
 
		setup();
	}
 
	private void setup() {
		changeButton(R.id.btnBag, 0xffe09828);
		changeButton(R.id.btnFight, 0xffe83838);
		changeButton(R.id.btnRun, 0xff2890c8);
		changeButton(R.id.btnPokemon, 0xff58b028);
		((TextView) findViewById(R.id.battleDialog)).setTypeface(Helper.Fonts.getPkmnFL());
 
		findViewById(R.id.btnBag).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Dialog dialog = new Dialog(BattleActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.comp_backpack);
				dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
				((BackpackView) dialog.findViewById(R.id.backpack)).dialog = dialog;
				dialog.show();
			}
		});
 
		findViewById(R.id.btnFight).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showMoves();
			}
		});
		findViewById(R.id.btnRun).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptEscape();
			}
		});
		// TODO implement pokemon shifting
		// findViewById(R.id.btnPokemon).setOnClickListener(listener);
 
		((LinearLayout) findViewById(R.id.healthBoardContainerTop)).addView(enemyBHV = new BattleHealthView(this, getEnemy()));
		((LinearLayout) findViewById(R.id.pokemonContainerTop)).addView(enemyPV = new PokemonView(this, getEnemy()));
		changePokemon(battle.opponents, currentEnemy);
		animator.addAction(BattleAnimator.Action.SEND_POKEMON, getEnemy());
		animator.addAction(BattleAnimator.Action.MESSAGE, "A wild " + getEnemy().nickname + " appeared!", true);
 
		((LinearLayout) findViewById(R.id.healthBoardContainerBottom)).addView(allyBHV = new BattleHealthView(this, getAlly()));
		((LinearLayout) findViewById(R.id.pokemonContainerBottom)).addView(allyPV = new PokemonView(this, getAlly()));
		changePokemon(battle.allies, currentAlly);
		animator.addAction(BattleAnimator.Action.SEND_POKEMON, getAlly());
		animator.addAction(BattleAnimator.Action.MESSAGE, "Go " + getAlly().nickname + "!", true);
 
		animator.addAction(BattleAnimator.Action.MESSAGE, "What will " + getAlly().nickname + " do?");
		animator.start();
 
		disableMoves();
	}
 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
 
	@Override
	public void onBackPressed() {
		if(movesShowing) {
			hideMoves();
			movesQueue.clear();
		} else {
			attemptEscape() ;
		}
	}
 
	@Override
	protected void onDestroy() {
		super.onDestroy();
		animator.cancel();
	}
 
	private void showMoves() {
		for(int i = 0; i < getAlly().moves.size(); i++) {
			if(getAlly().moves.get(i).currentPP < 1) continue;
			if(((ViewGroup) ((RelativeLayout) findViewById(R.id.moveButtonsContainer)).getChildAt(i + 1)).getChildCount() < 1) continue;
			((ViewGroup) ((RelativeLayout) findViewById(R.id.moveButtonsContainer)).getChildAt(i + 1)).getChildAt(0).setEnabled(true);
		}
		findViewById(R.id.battleButtons).setVisibility(View.INVISIBLE);
		movesShowing = true;
	}
	private void hideMoves() {
		findViewById(R.id.battleButtons).setVisibility(View.VISIBLE);
		movesShowing = false;
	}
 
	private void disableMoves() {
		for(int i = 0; i < getAlly().moves.size(); i++) {
			((ViewGroup) ((RelativeLayout) findViewById(R.id.moveButtonsContainer)).getChildAt(i + 1)).getChildAt(0).setEnabled(false);
		}
	}
 
	private void runTurn() {
		ArrayList<QueuedMove> queue = new ArrayList<QueuedMove>();
		while(movesQueue.size() > 0) {
			QueuedMove first = movesQueue.get(0);
			movesQueue.remove(0);
			for(QueuedMove qm : movesQueue) {
				if(qm.move.getPriority() > first.move.getPriority()) {
					movesQueue.add(first);
					movesQueue.remove(qm);
					first = qm;
				} else if(qm.move.getPriority() == first.move.getPriority() && qm.attacker.getStat(Pokemon.IVStat.SPEED) > first.attacker.getStat(Pokemon.IVStat.SPEED)) {
					movesQueue.add(first);
					movesQueue.remove(qm);
					first = qm;
				}
			}
			queue.add(first);
		}
 
		for(final QueuedMove qm : queue) {
			animator.addAction(BattleAnimator.Action.MESSAGE, qm.attacker.nickname + " used " + qm.move.getName(), false);
			animator.addAction(BattleAnimator.Action.DELTA_HP, qm.target, true);
			qm.use();
			if(qm.target.currentHp < 1) {
				if(qm.attacker.isEnemy) {
					animator.addAction(BattleAnimator.Action.MESSAGE, "You whited out...", true);
					animator.addAction(BattleAnimator.Action.MESSAGE, "You lost 0¥...", true);
					animator.start(new Runnable() {
						@Override
						public void run() {
							finish();
							overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
						}
					});
				} else {
					animator.addAction(BattleAnimator.Action.MESSAGE, qm.attacker.nickname + " gained " + qm.target.getExperienceYield(qm.attacker) + " experience", false);
					if(qm.attacker.gainExperience(qm.target.getExperienceYield(qm.attacker))) {
						animator.addAction(BattleAnimator.Action.CALLBACK, new Runnable() {
							@Override
							public void run() {
								if(qm.attacker.getLevelProgress() > 1) animateLevelUp(qm.attacker);
							}
						}, false);
					} else {
						animator.addAction(BattleAnimator.Action.GAIN_EXP, qm.attacker, true);
					}
					animator.start(new Runnable() {
						@Override
						public void run() {
							if(hasMorePokemon(battle.opponents)) {
								changePokemon(battle.opponents, nextPokemon(battle.opponents));
								animator.addAction(BattleAnimator.Action.SEND_POKEMON, getEnemy());
								animator.addAction(BattleAnimator.Action.MESSAGE, "Enemy sent out " + getEnemy().nickname + "!", true);
								animator.addAction(BattleAnimator.Action.MESSAGE, "What will " + getAlly().nickname + " do?", false);
								animator.start(new Runnable() {
									@Override
									public void run() {
										hideMoves();
									}
								});
							} else {
								finish();
								overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
							}
						}
					});
					return;
				}
			}
		}

		animator.addAction(BattleAnimator.Action.MESSAGE, "What will " + getAlly().nickname + " do?", false);
		animator.start(new Runnable() {
			@Override
			public void run() {
				hideMoves();
			}
		});
	}
 
	private void animateLevelUp(final Pokemon pokemon) {
		animator.injectAction(BattleAnimator.Action.CALLBACK, new Runnable() {
			@Override
			public void run() {
				if(pokemon.getLevelProgress() > 1) animateLevelUp(pokemon);
				else animator.injectAction(BattleAnimator.Action.GAIN_EXP, pokemon, true);
			}
		}, false);
		animator.injectAction(BattleAnimator.Action.MESSAGE, pokemon.nickname + " grew to Level "  + (pokemon.level + 1) + 	"!", true);
		animator.injectAction(BattleAnimator.Action.LEVEL_UP, pokemon, false);
		animator.injectAction(BattleAnimator.Action.GAIN_EXP, pokemon, false);
	}
 
	private void attemptEscape() {
		int a = getAlly().getStagedStat(Pokemon.IVStat.SPEED);
		int b = getEnemy().getStagedStat(Pokemon.IVStat.SPEED);
		int f = ((a * 128 / (b < 1 ? 1 : b)) + 30 * battle.escapeAttempts++) % 256;
		if(rnd.nextInt(256) < f) {
			animator.addAction(BattleAnimator.Action.MESSAGE, "Got away safely!", true);
			animator.start(new Runnable() {
				@Override
				public void run() {
					finish();
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			});
		} else {
			animator.addAction(BattleAnimator.Action.MESSAGE, "Can't escape!", true);
			movesQueue.add(new QueuedMove(getEnemy(), getEnemy().moves.get(rnd.nextInt(getEnemy().moves.size())), getAlly()));
			runTurn();
		}
	}
 
	public void throwBall(final int ballId) {
		int catchRate = Helper.getIntegerFromDb(Helper.dataMgr.pokemonDb, "pokemon_species	", "capture_rate", "id=" + getEnemy().id, null);
		double ballBonus = Helper.getDoubleFromDb(Helper.dataMgr.mapsDb, "pokeballs", "	multiplier", "id=" + ballId, null);
		double statusBonus = getEnemy().ailment == 2 || getEnemy().ailment == 3 ? 2 : getEnemy().ailment == 1 || getEnemy().ailment == 4 || getEnemy().ailment == 5 ? 1.5 : 1;
		double a = (3*getEnemy().getStat(Pokemon.IVStat.HP) - 2*getEnemy().currentHp) * catchRate * ballBonus * statusBonus / (3*getEnemy().getStat(Pokemon.IVStat.HP));
		double b = (Math.pow(2, 16) - 1) * Math.pow(a / (Math.pow(2, 8) - 1), 0.25);
 
		float initX = findViewById(R.id.pokemonContainerBottom).getX() + findViewById(R.id.pokemonContainerBottom).getWidth()/2;
		float initY = findViewById(R.id.battle_bottom).getY() + findViewById(R.id.battle_bottom).getHeight()/2;
		float destX = findViewById(R.id.pokemonContainerTop).getX() + findViewById(R.id.pokemonContainerTop).getWidth()/2;
		float destY = findViewById(R.id.battle_top).getY() + findViewById(R.id.battle_top).getHeight()/2;
		float fallY = findViewById(R.id.battle_top).getY() + findViewById(R.id.battle_top).getHeight()*4/5;

		try {
			Bitmap bm = BitmapFactory.decodeStream(getResources().getAssets().open("sprites/balls/_" + ballId + ".png"));
			BitmapDrawable ball = new BitmapDrawable(getResources(), bm);
			ball.setAntiAlias(false);
			ball.setDither(false);

			((ImageView) findViewById(R.id.pokeball)).setImageDrawable(ball);
			findViewById(R.id.pokeball).setMinimumWidth(Helper.dpToPx(bm.getWidth()));
			findViewById(R.id.pokeball).setMinimumHeight(Helper.dpToPx(bm.getHeight()));
		} catch(IOException e) {
			e.printStackTrace();
		}
		animator.addAction(BattleAnimator.Action.MESSAGE, "You threw a " + Helper.getStringFromDb(Helper.dataMgr.mapsDb, "pokeballs", "name", "id=" + ballId, null) + " at " + getEnemy().nickname, false);

		findViewById(R.id.pokeball).setVisibility(View.VISIBLE);
		findViewById(R.id.pokeball).setTranslationX(initX);
		findViewById(R.id.pokeball).setTranslationX(initY);
		animator.addAction(BattleAnimator.Action.THROW_BALL, new Float[]{initX, initY, destX, destY});
		animator.addAction(BattleAnimator.Action.CALLBACK, new Runnable() {
			@Override
			public void run() {
				try {
					Bitmap bm = BitmapFactory.decodeStream(getResources().getAssets().open("sprites/balls-open/_" + ballId + ".png"));
					BitmapDrawable ball = new BitmapDrawable(getResources(), bm);

					((ImageView) findViewById(R.id.pokeball)).setImageDrawable(ball);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
		animator.addAction(BattleAnimator.Action.SUCK_POKEMON, getEnemy());
		animator.addAction(BattleAnimator.Action.CALLBACK, new Runnable() {
			@Override
			public void run() {
				try {
					Bitmap bm = BitmapFactory.decodeStream(getResources().getAssets().open("sprites/balls/_" + ballId + ".png"));
					BitmapDrawable ball = new BitmapDrawable(getResources(), bm);

					((ImageView) findViewById(R.id.pokeball)).setImageDrawable(ball);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
		animator.addAction(BattleAnimator.Action.DROP_BALL, new Float[] {destY, fallY});
		animator.addAction(BattleAnimator.Action.WAIT, 300);

		for(int i = 0; i < 4; i++) {
			if(rnd.nextInt((int) Math.pow(2, 16)) < b) {
				// Shake Pokeball
				if(i == 3) break;
				animator.addAction(BattleAnimator.Action.SHAKE_BALL, null);
				animator.addAction(BattleAnimator.Action.WAIT, 700);
			} else {
				// Pokemon escaped
				animator.addAction(BattleAnimator.Action.CALLBACK, new Runnable() {
					@Override
					public void run() {
						try {
							Bitmap bm = BitmapFactory.decodeStream(getResources().getAssets().open("sprites/balls-open/_" + ballId + ".png"));
							BitmapDrawable ball = new BitmapDrawable(getResources(), bm);

							((ImageView) findViewById(R.id.pokeball)).setImageDrawable(ball);
						} catch(IOException e) {
							e.printStackTrace();
						}
					}
				});
				animator.addAction(BattleAnimator.Action.SEND_POKEMON, getEnemy());
				animator.addAction(BattleAnimator.Action.CALLBACK, new Runnable() {
					@Override
					public void run() {
						findViewById(R.id.pokeball).setVisibility(View.INVISIBLE);
					}
				});
				animator.addAction(BattleAnimator.Action.MESSAGE, capFailMsgs[i], true);

				movesQueue.add(new QueuedMove(getEnemy(), getEnemy().moves.get(rnd.nextInt(getEnemy().moves.size())), getAlly()));

				runTurn();
				return;
			}
		}

		try {
			Bitmap bm = BitmapFactory.decodeStream(getResources().getAssets().open("effects/star.png"));
			ImageView[] stars = new ImageView[6];
			for(int i = 0; i < stars.length; i++) {
				stars[i] = new ImageView(this);
				stars[i].setScaleType(ImageView.ScaleType.FIT_XY);
				stars[i].setMinimumWidth(Helper.dpToPx(7));
				stars[i].setMinimumHeight(Helper.dpToPx(7));
				stars[i].setTag(rnd.nextInt(360));
				stars[i].setImageBitmap(bm);
			}
			animator.addAction(BattleAnimator.Action.LOCK_BALL, stars);
		} catch(IOException e) {
			e.printStackTrace();
		}

		animator.addAction(BattleAnimator.Action.MESSAGE, "Gotcha! " + getEnemy().nickname + " was caught!", true);
		animator.start(new Runnable() {
			@Override
			public void run() {
				getEnemy().ballId = ballId;
				Helper.dataMgr.addToPokemonTeam(getEnemy());
				finish();
			}
		});
	}
 
 
 
	public boolean hasMorePokemon(Pokemon[]	team) {
		for(Pokemon pkmn : team) if(pkmn.currentHp > 0) return true;
		return false;
	}
 
	public int nextPokemon(Pokemon[] team) {
		int newpos = rnd.nextInt(team.length);
		while(team[newpos].currentHp < 1) {
			newpos = rnd.nextInt(team.length);
		}
		return newpos;
	}
 
	public Pokemon getEnemy() {
		return battle.opponents[currentEnemy];
	}
 
	public Pokemon getAlly() {
		return battle.allies[currentAlly];
	}
 
 
	private void changeButton(int resId, int color) {
		((TextView) findViewById(resId)).setTypeface(Helper.Fonts.getPkmnFL());
		((GradientDrawable) findViewById(resId).getBackground()).setColor(color);
	}
 
	private void changePokemon(Pokemon[] team, int newPos) {
		boolean isEnemy = team[newPos].isEnemy;
 
		if(isEnemy) {
			currentEnemy = newPos;
		} else {
			currentAlly = newPos;
 
			// Change move buttons
			for(int i = 0; i < ((RelativeLayout) findViewById(R.id.moveButtonsContainer)).getChildCount(); i++) {
				View v = ((RelativeLayout) findViewById(R.id.moveButtonsContainer)).getChildAt(i);
				if(v instanceof ViewGroup) ((ViewGroup) v).removeAllViews();
			}
 
			for(int i = 0; i < team[newPos].moves.size(); i++) {
				MoveButtonView mbv = new MoveButtonView(this, team[newPos].moves.get(i));
				mbv.setOnClickListener(moveButtonsListener);
				((ViewGroup) ((RelativeLayout) findViewById(R.id.moveButtonsContainer)).getChildAt(i + 1)).addView(mbv);
			}
		}
 
		(isEnemy ? enemyBHV : allyBHV).setPokemon(team[newPos]);
		(isEnemy ? enemyPV : allyPV).setPokemon(team[newPos]);
 
		(isEnemy ? enemyPV : allyPV).setTranslationY(0);
		(isEnemy ? enemyPV : allyPV).setScaleX(0);
		(isEnemy ? enemyPV : allyPV).setScaleY(0);
 
	}
 
}