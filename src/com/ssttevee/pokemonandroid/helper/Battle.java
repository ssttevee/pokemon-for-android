package com.ssttevee.pokemonandroid.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.ssttevee.pokemonandroid.R;
import com.ssttevee.pokemonandroid.activity.BattleActivity;
import com.ssttevee.pokemonandroid.util.Helper;

import java.util.ArrayList;
import java.util.Collections;

public class Battle implements Parcelable {
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Battle createFromParcel(Parcel in) {
			return new Battle(in);
		}
		public Battle[] newArray(int size) {
			return new Battle[size];
		}
	};

	private Context context;

	public Pokemon[] opponents;
	public Pokemon[] allies;

	public int stage = 1;

	public int trainerClass;
	public String trainerName;

	public int escapeAttempts = 1;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int i) {
		out.writeTypedArray(opponents, 0);
		out.writeInt(stage);
		out.writeInt(trainerClass);
		out.writeString(trainerName);
	}

	public Battle(Parcel in) {
		opponents = (Pokemon[]) in.createTypedArray(Pokemon.CREATOR);
		stage = in.readInt();
		trainerClass = in.readInt();
		trainerName = in.readString();

		allies = new Pokemon[Helper.dataMgr.getPokemonTeam().size()];
		Helper.dataMgr.getPokemonTeam().toArray(allies);
	}

	public Battle(Context context) {
		this.context = context;
	}

	public static class Builder {
		private Battle battle;

		private ArrayList<Pokemon> opponents;

		public Builder(Context context) {
			opponents = new ArrayList<Pokemon>();
			battle = new Battle(context);
		}
		public Battle create() {
			while(opponents.size() > 6) opponents.remove(opponents.size() - 1);

			battle.opponents = new Pokemon[opponents.size()];
			battle.allies = new Pokemon[Helper.dataMgr.getPokemonTeam().size()];

			opponents.toArray(battle.opponents);
			Helper.dataMgr.getPokemonTeam().toArray(battle.allies);
			return battle;
		}

		public void addOpponents(Pokemon... pokemon) {
			Collections.addAll(opponents, pokemon);
		}
		public void setStage(int stageId) {
			battle.stage = stageId;
		}
	}

	public void start() {
		if(context != null) {
			Intent battleintent = new Intent(context, BattleActivity.class);
			battleintent.putExtra("battle", this);
			context.startActivity(battleintent);
			((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
	}


}