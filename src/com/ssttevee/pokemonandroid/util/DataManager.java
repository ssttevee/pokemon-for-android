package com.ssttevee.pokemonandroid.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.ssttevee.pokemonandroid.helper.Map;
import com.ssttevee.pokemonandroid.helper.Move;
import com.ssttevee.pokemonandroid.helper.Pokemon;

import java.util.ArrayList;
import java.util.BitSet;

public class DataManager {
	public static DataManager instance;

	public SQLiteDatabase pokemonDb;
	public SQLiteDatabase mapsDb;
	public SQLiteDatabase saveDb;

	private SparseArray<String> itemNames;
	private SparseArray<String> pkmnNames;
	private ArrayList<Pokemon> party;

	class PokemonDataSQLiteHelper extends SQLiteAssetHelper {
		public static final int VERSION = 1;

		public PokemonDataSQLiteHelper(Context context) {
			super(context, "pkmndata.db", null, VERSION);
			// setForcedUpgrade();
		}
	}
	class MapDataSQLHelper extends SQLiteAssetHelper {
		public static final int VERSION = 4;

		public MapDataSQLHelper(Context context) {
			super(context, "mapdata.db", null, VERSION);
			setForcedUpgrade();
		}
	}
	class GameDataSQLiteHelper extends SQLiteOpenHelper {
		public static final int VERSION = 1;

		public GameDataSQLiteHelper(Context context) {
			super(context, "save.db",  null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(
					"create table pokemon (" +
							"id integer not null," +
							"pokemon_id integer not null," +
							"original_trainer text not null," +
							"original_trainer_id integer not null," +
							"nickname text default null," +
							"individual_value blob not null," +
							"level integer default 1," +
							"experience integer default 0," +
							"hold_item integer default 0," +
							"ability integer not null," +
							"nature integer not null," +
							"current_hp integer not null," +
							"move_1 integer default null," +
							"move_1_max_pp integer default null," +
							"move_1_current_pp integer default null," +
							"move_2 integer default null," +
							"move_2_max_pp integer default null," +
							"move_2_current_pp integer default null," +
							"move_3 integer default null," +
							"move_3_max_pp integer default null," +
							"move_3_current_pp integer default null," +
							"move_4 integer default null," +
							"move_4_max_pp integer default null," +
							"move_4_current_pp integer default null," +
							"gender text default `male`," +
							"is_in_team integer default 0," +
							"team_order integer default null," +
							"caught_ball text not null," +
							"ailment integer default null," +
							"primary key(id)" +
							");"
			);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

	public DataManager(Context ctx) {
		pokemonDb = new PokemonDataSQLiteHelper(ctx).getReadableDatabase();
		mapsDb = new MapDataSQLHelper(ctx).getReadableDatabase();
		saveDb = new GameDataSQLiteHelper(ctx).getWritableDatabase();

		itemNames = new SparseArray<String>();
		pkmnNames = new SparseArray<String>();
		party = new ArrayList<Pokemon>();

		Cursor cursor = pokemonDb.query("item_names", new String[] {"item_id", "name"}, null, null, null, null, "item_id");
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			itemNames.append(cursor.getInt(0), cursor.getString(1));
			cursor.moveToNext();
		}
		cursor.close();

		cursor = saveDb.query("pokemon", new String[] {"*"}, "is_in_team=1", null, null, null, "team_order asc");
		cursor.moveToFirst();
		if(cursor.moveToFirst()) while(!cursor.isAfterLast()) {
			party.add(cursorToPokemon(cursor));
			cursor.moveToNext();
		}
		cursor.close();

		instance = this;
	}

	private Pokemon cursorToPokemon(Cursor cursor) {
		Pokemon p = new Pokemon();
		p.databaseId = cursor.getInt(cursor.getColumnIndex("id"));
		p.id = cursor.getInt(cursor.getColumnIndex("pokemon_id"));
		p.nickname = cursor.isNull(cursor.getColumnIndex("nickname")) ? getPokemonName(p.id) : cursor.getString(cursor.getColumnIndex("nickname"));
		p.individualValue = convertIV(cursor.getString(cursor.getColumnIndex("individual_value")));
		p.level = cursor.getInt(cursor.getColumnIndex("level"));
		p.experience = cursor.getInt(cursor.getColumnIndex("experience"));
		p.holdItem = cursor.getInt(cursor.getColumnIndex("hold_item"));
		p.abilityId = cursor.getInt(cursor.getColumnIndex("ability"));
		p.natureId = cursor.getInt(cursor.getColumnIndex("nature"));
		p.currentHp = cursor.getInt(cursor.getColumnIndex("current_hp"));
		p.gender = Pokemon.Gender.valueOf(cursor.getString(cursor.getColumnIndex("gender")));
		p.originalTrainer = cursor.getString(cursor.getColumnIndex("original_trainer"));
		p.otId = cursor.getInt(cursor.getColumnIndex("original_trainer_id"));
		p.moves = new ArrayList<Move>();
		p.ailment = cursor.getInt(cursor.getColumnIndex("ailment"));
		p.ballId = cursor.getInt(cursor.getColumnIndex("caught_ball"));
		p.isEnemy = false;
		p.isWild = false;

		for(int i = 1; i < 5; i++) {
			if(cursor.isNull(cursor.getColumnIndex("move_" + i))) continue;
			p.moves.add(
					new Move(
							cursor.getInt(cursor.getColumnIndex("move_" + i)),
							cursor.getInt(cursor.getColumnIndex("move_" + i + "_max_pp")),
							cursor.getInt(cursor.getColumnIndex("move_" + i + "_current_pp"))
					)
			);
		}

		return p;
	}

	public ArrayList<Map> getMapList() {
		ArrayList<Map> arr = new ArrayList<Map>();
		Cursor cursor = mapsDb.query(
				"maps",
				new String[] {
						"id",
						"name",
						"background"
				},
				null,
				null,
				null,
				null,
				"id");

		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			arr.add(
					new Map(
							cursor.getInt(0),
							cursor.getString(1),
							cursor.getInt(2)
					)
			);
			cursor.moveToNext();
		}
		cursor.close();

		return arr;
	}

	public ArrayList<String> getItemPocketsNames() {
		ArrayList<String> arr = new ArrayList<String>();
		Cursor cursor = pokemonDb.query(
				"item_pocket_names",
				new String[] {
						"name"
				},
				null,
				null,
				null,
				null,
				"item_pocket_id"
		);

		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			arr.add(cursor.getString(0));
			cursor.moveToNext();
		}
		cursor.close();

		return arr;
	}

	public SparseIntArray getItemsByPocket(int pocket_id) { // TODO get items from player data
		String item_categories = "";
		SparseIntArray items = new SparseIntArray();

		Cursor cursor = pokemonDb.query(
				"item_categories",
				new String[] {
						"id"
				},
				"pocket_id=" + pocket_id,
				null,
				null,
				null,
				"id"
		);

		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			item_categories += "category_id=" + cursor.getInt(0) + " or ";
			cursor.moveToNext();
		}
		cursor.close();

		item_categories = item_categories.substring(0, item_categories.length() - 4);

		cursor = pokemonDb.query(
				"items",
				new String[] {
						"id"
				},
				"(" + item_categories + ")",
				null,
				null,
				null,
				"category_id, id"
		);

		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			items.put(cursor.getInt(0), 98); // TODO change second param to number of that item in your bag
			cursor.moveToNext();
		}
		cursor.close();

		return items;
	}

	public ArrayList<Pokemon> getPokemonTeam() {
		return party;
	}

	public boolean addToPokemonTeam(Pokemon pkmn) {
		pkmn.isWild = false;
		pkmn.isEnemy = false;
		pkmn.originalTrainer = Helper.app.getTrainerName();
		pkmn.otId = Helper.app.getTrainerId();

		ContentValues values = new ContentValues();
		values.put("pokemon_id", pkmn.id);
		values.put("nickname", pkmn.nickname);
		values.put("individual_value", convertIV(pkmn.individualValue));
		values.put("level", pkmn.level);
		values.put("experience", pkmn.experience);
		values.put("hold_item", pkmn.holdItem);
		values.put("ability", pkmn.abilityId);
		values.put("nature", pkmn.natureId);
		values.put("current_hp", pkmn.getCurrentHP());
		values.put("gender", pkmn.gender.name());
		values.put("original_trainer", pkmn.originalTrainer);
		values.put("original_trainer_id", pkmn.otId);
		values.put("ailment", pkmn.ailment);
		values.put("caught_ball", pkmn.ballId);

		if(party.size() < 6) {
			values.put("is_in_team", 1);
			values.put("team_order", party.size());
		}

		for(int i = 0; i < pkmn.moves.size(); i++) {
			values.put("move_" + (i + 1), pkmn.moves.get(i).moveId);
			values.put("move_" + (i + 1) + "_max_pp", pkmn.moves.get(i).maxPP);
			values.put("move_" + (i + 1) + "_current_pp", pkmn.moves.get(i).currentPP);
		}

		saveDb.insert("pokemon", null, values);

		pkmn.databaseId = Helper.getIntegerFromDb(saveDb, "pokemon", "id", null, "id desc limit 1");

		if(party.size() < 6) {
			party.add(pkmn);
		} else {
			// TODO add to storage
			return false;
		}
		return true;
	}

	public String getAbilityName(int abilityId) {
		return Helper.getStringFromDb(pokemonDb, "ability_names", "name", "ability_id=" + abilityId, null);
	}

	public String getNatureName(int natureId) {
		return Helper.getStringFromDb(pokemonDb, "nature_names", "name", "nature_id=" + natureId, null);
	}

	public String getPokemonName(int pkmnId) {
		String name = pkmnNames.get(pkmnId);
		if(name == null) {
			pkmnNames.append(pkmnId, Helper.getStringFromDb(pokemonDb, "pokemon", "identifier", "id=" + pkmnId, null).toUpperCase());
			name = pkmnNames.get(pkmnId);
		}
		return name;
	}
	public String getPokeballIdentifier(int ballId) {
		return Helper.getStringFromDb(mapsDb, "pokeballs", "identifier", "id=" + ballId, null);
	}
	public String getItemName(int itemId) {
		return itemNames.get(itemId);
	}

	public boolean isItemCountable(int itemId) {
		long numRows = DatabaseUtils.longForQuery(pokemonDb, "SELECT COUNT(*) FROM item_flag_map WHERE item_id=" + itemId + " AND item_flag_id=1", null);
		return numRows > 0;
	}

	public static String convertIV(boolean[] bits) {
		String str = "";
		for(boolean bit : bits)
			str += bit ? "1" : "0";
		return str;
	}

	public static boolean[] convertIV(String iv) {
		boolean[] bits = new boolean[iv.length()];
		for(int i = 0; i < iv.length(); i++) {
			bits[i] = iv.charAt(i) == "1".charAt(0);
		}
		return bits;
	}
}
