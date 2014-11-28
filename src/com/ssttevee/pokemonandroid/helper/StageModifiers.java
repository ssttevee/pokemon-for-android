package com.ssttevee.pokemonandroid.helper;

import android.util.SparseIntArray;

import java.util.Random;

public class StageModifiers {
	public static final int HP = 1;
	public static final int ATTACK = 2;
	public static final int DEFENSE = 3;
	public static final int SP_ATK = 4;
	public static final int SP_DEF = 5;
	public static final int SPEED = 6;
	public static final int ACCURACY = 7;
	public static final int EVASION = 8;
	public static final int CRITICAL = 9;

	private SparseIntArray stages;

	public StageModifiers() {
		stages = new SparseIntArray();
	}

	public boolean boost(int stat, int number) {
		int stage = getStage(stat);
		if(stage > 6) return false;
		if(stage + number > 6) stages.append(stat, 6);
		else stages.append(stat, stage + number);
		return true;
	}

	public boolean reduce(int stat, int number) {
		int stage = getStage(stat);
		if(stage < -6) return false;
		if(stage - number < -6) stages.append(stat, -6);
		else stages.append(stat, stage - number);
		return true;
	}

	public int getStage(int stat) {
		return stages.get(stat, 0);
	}

	public double getMod(int stat) {
		double stage = getStage(stat);
		if(stat < 7) {
			if(stage < 0) return 2 / (2 + stage);
			if(stage > 0) return (2 + stage) / 2;
			else return 1;
		} else if(stat < 9) {
			if(stage < 0) return 3 / (3 + stage);
			if(stage > 0) return (3 + stage) / 3;
			else return 1;
		} else {
			if(stage == 1) return 1/8;
			if(stage == 2) return 1/4;
			if(stage == 3) return 1/3;
			if(stage == 4) return 1/2;
			return 1/16;
		}
	}

	public boolean isCrit() {
		return new Random().nextDouble() < getMod(CRITICAL);
	}

	public void reset() {
		stages = new SparseIntArray();
	}
}
