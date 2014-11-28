package com.ssttevee.pokemonandroid.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;
import com.ssttevee.pokemonandroid.util.Helper;

public class Nature {
	public static final int HARDY = 1;
	public static final int BOLD = 2;
	public static final int MODEST = 3;
	public static final int CALM = 4;
	public static final int TIMID = 5;
	public static final int LONELY = 6;
	public static final int DOCILE = 7;
	public static final int MILD = 8;
	public static final int GENTLE = 9;
	public static final int HASTY = 10;
	public static final int ADAMANT = 11;
	public static final int IMPISH = 12;
	public static final int BASHFUL = 13;
	public static final int CAREFUL = 14;
	public static final int RASH = 15;
	public static final int JOLLY = 16;
	public static final int NAUGHTY = 17;
	public static final int LAX = 18;
	public static final int QUIRKY = 19;
	public static final int NAIVE = 20;
	public static final int BRAVE = 21;
	public static final int RELAXED = 22;
	public static final int QUIET = 23;
	public static final int SASSY = 24;
	public static final int SERIOUS = 25;

	private static final SparseArray<int[]> statMods = new SparseArray<int[]>();

	public static double getStatModifier(int natureId, int statId) {
		SQLiteDatabase db = Helper.dataMgr.pokemonDb;

		int decreaseId = 0;
		int increaseId = 0;
		int[] mods = statMods.get(natureId, null);

		if(mods == null) {
			Cursor c = db.query("natures", new String[]{"decreased_stat_id", "increased_stat_id"}, "id=" + natureId, null, null, null, null);
			if(!c.moveToFirst()) return 1;
			decreaseId = c.getInt(0);
			increaseId = c.getInt(1);
			statMods.append(natureId, new int[] {decreaseId, increaseId});
			c.close();
		} else {
			decreaseId = mods[0];
			increaseId = mods[1];
		}

		if(increaseId == decreaseId) return 1;
		else if(increaseId == statId) return 1.1;
		else if(decreaseId == statId) return 0.9;
		else return 1;
	}
}