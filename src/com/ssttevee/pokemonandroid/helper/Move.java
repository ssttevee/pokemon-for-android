package com.ssttevee.pokemonandroid.helper;

import android.os.Parcel;
import android.os.Parcelable;
import com.ssttevee.pokemonandroid.util.Helper;

public class Move implements Parcelable {
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Move createFromParcel(Parcel in) {
			return new Move(in);
		}
		public Move[] newArray(int size) {
			return new Move[size];
		}
	};

	public int moveId;
	public int maxPP;
	public int currentPP;

	private String name;
	private Integer type;
	private Integer power;
	private Integer pp;
	private Integer accuracy;
	private Integer priority;
	private Integer target;
	private Integer damageClass;
	private Integer ailment;
	private Integer ailmentChance;
	private Integer effect;
	private Integer effectChance;
	private Integer metaCategory;

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(moveId);
		parcel.writeInt(maxPP);
		parcel.writeInt(currentPP);
	}

	public Move(Parcel in) {
		this(in.readInt(), in.readInt(), in.readInt());
	}

	public Move(int moveId) {
		this.moveId = moveId;
		maxPP = getPP();
		currentPP = maxPP;
	}

	public Move(int moveId, int maxPP, int currentPP) {
		this.moveId = moveId;
		this.maxPP = maxPP;
		this.currentPP = currentPP;
	}

	public String getName() {
		return name == null ? name = Helper.getStringFromDb(Helper.dataMgr.pokemonDb, "move_names", "name", "move_id=" + moveId, null) : name;
	}
	public int getType() {
		return type == null ? type = getStatDB("type_id") : type;
	}
	public int getPower() {
		return power == null ? power = getStatDB("power") : power;
	}
	public int getPP() {
		return pp == null ? pp = getStatDB("pp") : pp;
	}
	public int getAccuracy() {
		return accuracy == null ? accuracy = getStatDB("accuracy") : accuracy;
	}
	public int getPriority() {
		return priority == null ? priority = getStatDB("priority") : priority;
	}
	public int getTarget() {
		return target == null ? target = getStatDB("target_id") : target;
	}
	public int getDamageClass() {
		return damageClass == null ? damageClass = getStatDB("damage_class_id") : damageClass;
	}
	public int getEffect() {
		return effect == null ? effect = getStatDB("effect_id") : effect;
	}
	public int getEffectChance() {
		return effectChance == null ? effectChance = getStatDB("effect_chance") : effectChance;
	}
	public int getAilment() {
		return ailment == null ? ailment = Helper.getIntegerFromDb(Helper.dataMgr.pokemonDb, "move_meta", "meta_ailment_id", "move_id=" + moveId, null) : ailment;
	}
	public int getAilmentChance() {
		return ailmentChance == null ? ailmentChance = Helper.getIntegerFromDb(Helper.dataMgr.pokemonDb, "move_meta", "ailment_chance", "move_id=" + moveId, null) : ailmentChance;
	}
	public int getMetaCategory() {
		return metaCategory == null ? metaCategory = Helper.getIntegerFromDb(Helper.dataMgr.pokemonDb, "move_meta", "meta_category_id", "move_id=" + moveId, null) : metaCategory;
	}
	private int getStatDB(String column) {
		return Helper.getIntegerFromDb(Helper.dataMgr.pokemonDb, "moves", column, "id=" + moveId, null);
	}
	public double getDamageFactor(int targetType) {
		return (double) Helper.getIntegerFromDb(Helper.dataMgr.pokemonDb, "type_efficacy", "damage_factor", "damage_type_id=" + type + " and target_type_id=" + targetType, null) / 100;
	}
}
