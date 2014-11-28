package com.ssttevee.pokemonandroid.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.ssttevee.pokemonandroid.util.DataManager;

import java.util.ArrayList;
import java.util.Random;

public class Map {
	public int id;
	public String name;
	public int backgroundId;

	public Map(int id, String name, int backgroundId) {
		this.id = id;
		this.name = name;
		this.backgroundId = backgroundId;
	}

	private int[] getPokemonPool() {
		Cursor cursor = DataManager.instance.mapsDb.query("map_pokemon", new String[] {"pokemon_id"}, "map_id="+id, null, null, null, null);

		int[] pool = new int[cursor.getCount()];

		cursor.moveToFirst();
		for(int i = 0; i < cursor.getCount(); i++) {
			pool[i] = cursor.getInt(0);
			cursor.moveToNext();
		}
		cursor.close();

		return pool;
	}

	private int getPokemonLevel() {
		Cursor cursor = DataManager.instance.mapsDb.query("maps", new String[] {"min_level", "max_level"}, "id="+id, null, null, null, null);

		int level = 100;
		if(cursor.moveToFirst()) {
			int min = cursor.getInt(cursor.getColumnIndex("min_level"));
			int max = cursor.getInt(cursor.getColumnIndex("max_level"));
			level = new Random().nextInt(max - min + 1) + min;
		}
		cursor.close();

		return level;
	}

	public Pokemon getWildPokemon() {
		int[] pool = getPokemonPool();
		Random rand = new Random();
		return Pokemon.wild(pool[rand.nextInt(pool.length)], getPokemonLevel());
	}
}
