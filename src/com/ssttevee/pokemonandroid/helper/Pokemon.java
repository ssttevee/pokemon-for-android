package com.ssttevee.pokemonandroid.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.os.Parcel;
import android.os.Parcelable;
import com.ssttevee.pokemonandroid.util.BattleAnimator;
import com.ssttevee.pokemonandroid.util.DataManager;
import com.ssttevee.pokemonandroid.util.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Pokemon implements Parcelable {
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Pokemon createFromParcel(Parcel in) {
			Pokemon pokemon = new Pokemon();
			pokemon.databaseId = in.readInt();
			pokemon.id = in.readInt();
			pokemon.nickname = in.readString();
			pokemon.individualValue = new boolean[30];
			for(int i = 0; i < pokemon.individualValue.length; i++)
				pokemon.individualValue[i] = in.readByte() == 1;
			pokemon.level = in.readInt();
			pokemon.experience = in.readInt();
			pokemon.holdItem = in.readInt();
			pokemon.abilityId = in.readInt();
			pokemon.natureId = in.readInt();
			pokemon.currentHp = in.readInt();
			pokemon.gender = Gender.values()[in.readInt()];
			pokemon.originalTrainer = in.readString();
			pokemon.otId = in.readInt();
			pokemon.isWild = in.readByte() == 1;
			pokemon.isEnemy = in.readByte() == 1;
			in.readTypedList(pokemon.moves = new ArrayList<Move>(), Move.CREATOR);
			pokemon.ailment = in.readInt();
			pokemon.ballId = in.readInt();

			return pokemon;
		}
		public Pokemon[] newArray(int size) {
			return new Pokemon[size];
		}
	};

	public int databaseId = -1;
	public int id;
	public String nickname;
	public boolean[] individualValue;
	public int level;
	public int experience;
	public int holdItem;
	public int abilityId;
	public int natureId;
	public float currentHp;
	public Gender gender;
	public String originalTrainer;
	public int otId;
	public boolean isWild = true;
	public boolean isEnemy = true;
	public int ballId = 0;

	public int ailment = 0;

	private int growthRate = 0;
	private SQLiteStatement sqlstatement;

	public ArrayList<Move> moves;
	public StageModifiers stageMods = new StageModifiers();

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(databaseId);
		parcel.writeInt(id);
		parcel.writeString(nickname);
		for(boolean bool : individualValue)
			parcel.writeByte((byte) (bool ? 1 : 0));
		parcel.writeInt(level);
		parcel.writeInt(experience);
		parcel.writeInt(holdItem);
		parcel.writeInt(abilityId);
		parcel.writeInt(natureId);
		parcel.writeInt((int) currentHp);
		parcel.writeInt(gender.ordinal());
		parcel.writeString(originalTrainer);
		parcel.writeInt(otId);
		parcel.writeByte((byte) (isWild ? 1 : 0));
		parcel.writeByte((byte) (isEnemy ? 1 : 0));
		parcel.writeTypedList(moves);
		parcel.writeInt(ailment);
		parcel.writeInt(ballId);
	}

	public static enum Gender {MALE, FEMALE, GENDERLESS}
	public static class IVStat {
		public static final int HP = 1;
		public static final int ATTACK = 2;
		public static final int DEFENSE = 3;
		public static final int SP_ATK = 4;
		public static final int SP_DEF = 5;
		public static final int SPEED = 6;
	}

	public static Pokemon wild(int pkmnId, int level) {
		Pokemon pokemon = new Pokemon();
		Random random = new Random();
		DataManager dm = Helper.dataMgr;

		pokemon.id = pkmnId;
		pokemon.nickname = pokemon.getSpeciesName();
		pokemon.individualValue = new boolean[30];
		for(int i = 0; i < 30; i++)
			pokemon.individualValue[i] = random.nextBoolean();
		pokemon.level = level;
		int growth_rate_id = Helper.getIntegerFromDb(dm.pokemonDb, "pokemon_species", "growth_rate_id", "id=" + pkmnId, null);
		pokemon.experience = Helper.getIntegerFromDb(dm.pokemonDb, "experience", "experience", "growth_rate_id=" + growth_rate_id + " and level=" + level, null);
		pokemon.holdItem = getRandomHoldItem(pkmnId);
		pokemon.abilityId = Helper.getIntegerFromDb(dm.pokemonDb, "pokemon_abilities", "ability_id", "pokemon_id=" + pkmnId + " and is_hidden=0", "random() limit 1");
		pokemon.natureId = Helper.getIntegerFromDb(dm.pokemonDb, "natures", "id", null, "random() limit 1");
		pokemon.currentHp = pokemon.getStat(IVStat.HP);
		int genderRate = Helper.getIntegerFromDb(dm.pokemonDb, "pokemon_species", "gender_rate", "id=" + pkmnId, null);
		pokemon.gender = genderRate < 0 ? Gender.GENDERLESS : random.nextInt(8) < genderRate ? Gender.FEMALE : Gender.MALE;
		pokemon.isWild = true;
		pokemon.originalTrainer = "N/A";
		pokemon.otId = -1;

		pokemon.generateMoves();

		return pokemon;
	}

	private static int getRandomHoldItem(int pkmnId) {
		int item = 0;
		int random = new Random().nextInt(100);

		Cursor cursor = Helper.dataMgr.pokemonDb.query("pokemon_items", new String[] {"item_id", "rarity"}, "pokemon_id=" + pkmnId, null, null, null, "rarity" );

		cursor.moveToFirst();

		while(!cursor.isAfterLast()) {
			int chance = cursor.getInt(cursor.getColumnIndex("rarity"));

			if(random < chance) {
				item = cursor.getInt(cursor.getColumnIndex("item_id"));
				random = 0;
			} else {
				random -= chance;
			}

			cursor.moveToNext();
		}
		cursor.close();

		return item;
	}

	public void generateMoves() {
		if(moves != null) return;
		moves = new ArrayList<Move>();
		Cursor cursor = Helper.dataMgr.pokemonDb.query("pokemon_moves", new String[] {"move_id"}, "pokemon_id=" + id + " and pokemon_move_method_id=1 and level <=" + level, null, null, null, "level desc, `order` asc", "4");

		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			moves.add(new Move(cursor.getInt(0)));
			cursor.moveToNext();
		}

		cursor.close();
	}

	public int getStagedStat(int statId) {
		return (int) (getStat(statId) * stageMods.getMod(statId));
	}

	public int getStat(int statId) {
		int base = Helper.getIntegerFromDb(Helper.dataMgr.pokemonDb, "pokemon_stats", "base_stat", "stat_id=" + statId + " and pokemon_id=" + id, null);
		int stat;
		switch(statId) {
			case IVStat.HP:
				stat = (((getStatIV(IVStat.HP) + (2 * base) + 121) * level) / 100) + 10;
				break;

			default:
				stat =(((getStatIV(IVStat.HP) + (2 * base) + 21) * level) / 100) + 5;
				stat *= Nature.getStatModifier(natureId, statId);
		}
		return stat;
	}

	public int getStatIV(int statId) {
		boolean[] bits = Arrays.copyOfRange(individualValue, (statId - 1) * 5, (statId) * 5);
		long value = 0L;
		for (int i = 0; i < bits.length; ++i) {
			value += bits[i] ? (1L << i) : 0L;
		}
		return (int) value;
	}

	public int getCurrentHP() {
		return Math.round(currentHp);
	}

	public float getPercentHP() {
		return (float) currentHp / (float) getStat(IVStat.HP);
	}

	public String getSpeciesName() {
		return Helper.dataMgr.getPokemonName(id);
	}

	public Integer[] getTypes() {
		Cursor cursor = DataManager.instance.pokemonDb.query("pokemon_types", new String[] {"type_id"}, "pokemon_id=" + id, null, null, null, "slot");
		Integer[] types = new Integer[cursor.getCount()];

		int i = 0;
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			types[i] = cursor.getInt(0);
			cursor.moveToNext();
			i++;
		}
		cursor.close();
		return types;
	}

	public void levelUp() {
		int oldhp = getStat(IVStat.HP);
		level++;
		currentHp += getStat(IVStat.HP) - oldhp;
		save();
	}

	public int getGrowthRate() {
		return growthRate == 0 ? growthRate = Helper.getIntegerFromDb(Helper.dataMgr.pokemonDb, "pokemon_species", "growth_rate_id", "id=" + id, null) : growthRate;
	}

	public int getExperienceToNextLevel() {
		if(level == 100) return 0;
		return getExperienceAtLevel(level + 1, getGrowthRate()) - experience;
	}

	public int getExperienceAtLevel(int level, int growthRate) {
		switch(growthRate) {
			case 1:
				return (int) (5*Math.pow(level, 3)/4);
			case 2:
				return (int) Math.pow(level, 3);
			case 3:
				return (int) (4*Math.pow(level, 3)/5);
			case 4:
				return (int) ((6*Math.pow(level, 3)/5) - (15*Math.pow(level, 2)) + (100 * level) - 140);
			case 5:
				if(level <= 15)
					return (int) (Math.pow(level, 3) * (((level + 1)/3 + 24)/50));
				else if(level <= 36)
					return (int) (Math.pow(level, 3) * ((level + 14)/50));
				else
					return (int) (Math.pow(level, 3) * ((level/2 + 32)/50));
			case 6:
				if(level <= 50)
					return (int) ((Math.pow(level, 3) * (100 - level))/50);
				else if(level <= 68)
					return (int) ((Math.pow(level, 3) * (150 - level))/100);
				else if(level <= 98)
					return (int) ((Math.pow(level, 3) * ((1911 - 10 * level)/3))/500);
				else
					return (int) (Math.pow(level, 3) * (160 - level)/100);
		}
		save();
		return 0;
	}

	public float getLevelProgress() {
		if(level == 100) return 0;
		float lastLevel = getExperienceAtLevel(level, getGrowthRate());
		float nextLevel = getExperienceAtLevel(level + 1, getGrowthRate());
		return (experience - lastLevel) / (nextLevel - lastLevel);
	}

	public int getExperienceYield(Pokemon victor) {
		return (int) (((isWild?1:1.5) * (victor.isOT()?1:1.5) * getBaseExperienceYield() * (victor.holdItem==208?1.5:1) * level) / (7 /** EXP SHARE EQUATION HERE **/));
	}

	public boolean isOT() {
		return true;
	}

	private int getBaseExperienceYield() {
		return Helper.getIntegerFromDb(Helper.dataMgr.pokemonDb, "pokemon", "base_experience", "id=" + id, null);
	}

	public int getDamage(Move move, Pokemon target) {
		int damageClass = move.getDamageClass();
		double a = (2 * level + 10) / 250D;
		double b;
		if(damageClass == 2) b = getStat(IVStat.ATTACK) / (double) getStat(IVStat.DEFENSE);
		else if(damageClass == 3) b = getStat(IVStat.SP_ATK) / (double) getStat(IVStat.SP_DEF);
		else return 0;
		double c = move.getPower();

		double mod = (new Random().nextInt(16) + 85D) / 100D;

		for(int type : getTypes()) if(type == move.getType()) mod *= 1.5D;
		for(int type : target.getTypes()) mod *= move.getDamageFactor(type);

		return (int) Math.floor((a * b * c + 2) * mod);
	}

	public boolean gainExperience(int exp) {
		experience += exp;
		save();
		return getLevelProgress() > 1;
	}

	public void attack(Pokemon target, Move move) {
		int metaCat = move.getMetaCategory();
		move.currentPP--;
		if(metaCat == 0 || metaCat == 4 || metaCat == 6 || metaCat == 7 || metaCat == 8) {
			int damage = getDamage(move, target);
			if(stageMods.isCrit()) {
				damage *= 2;
				sendMessageToAnimator("Critical Hit!", true);
			}
			target.currentHp -= damage;

			if(target.currentHp < 1) {
				target.currentHp = 0;
				BattleAnimator.getInstance().addAction(BattleAnimator.Action.FAINT_POKEMON, target);
				sendMessageToAnimator(target.nickname + " fainted", true);
			}

			for(int type : target.getTypes()) {
				double factor = move.getDamageFactor(type);
				if(factor > 1) {
					sendMessageToAnimator("It was super effective!", true);
					break;
				} else if(factor == 0) {
					sendMessageToAnimator("It doesn't affect the Pokémon...", true);
					break;
				} else if(factor < 1) {
					sendMessageToAnimator("It was not very effective...", true);
					break;
				}
			}
		}
		if(metaCat == 9) {
			target.currentHp = 0;
			sendMessageToAnimator("It's a one-hit KO!", true);

			if(target.currentHp < 1) sendMessageToAnimator(target.nickname + " fainted", true);
		}
		if(metaCat == 1 || metaCat == 4 || metaCat == 5) {
			if(move.getAilmentChance() == 100 || move.getAilmentChance() == 0) {

			}
		}
	}

	private void sendMessageToAnimator(String message, boolean requireAck) {
		if(BattleAnimator.instance != null) BattleAnimator.getInstance().addAction(BattleAnimator.Action.MESSAGE, message, requireAck);
	}

	public void save() {
		if(databaseId < 0) return;

		SQLiteStatement stmt = getSQLStatement();
		stmt.clearBindings();

		Helper.dataMgr.saveDb.beginTransaction();

		int i = 1;
		stmt.bindLong(i++, level);
		stmt.bindLong(i++, experience);
		stmt.bindLong(i++, holdItem);
		stmt.bindLong(i++, (int) currentHp);
		stmt.bindLong(i++, ailment);
		for(int j = 0; j < moves.size(); j++) {
			stmt.bindLong(i++, moves.get(j).moveId);
			stmt.bindLong(i++, moves.get(j).maxPP);
			stmt.bindLong(i++, moves.get(j).currentPP);
		}
		stmt.executeUpdateDelete();

		Helper.dataMgr.saveDb.setTransactionSuccessful();
		Helper.dataMgr.saveDb.endTransaction();
	}

	private SQLiteStatement getSQLStatement() {
		if(sqlstatement == null) {
			String sql = "UPDATE pokemon SET level=?,experience=?,hold_item=?,current_hp=?,ailment=?";
			for(int i = 0; i < moves.size(); i++) sql += ",move_" + (i + 1) + "=?,move_" + (i + 1) + "_max_pp=?,move_" + (i + 1) + "_current_pp=?";
			return sqlstatement = Helper.dataMgr.saveDb.compileStatement(sql + " WHERE id=" + databaseId);
		} else {
			return sqlstatement;
		}
	}

}