package com.ssttevee.pokemonandroid.helper;

import android.util.SparseArray;

public class PokemonType {
	private static SparseArray<PokemonType> types;

	public String name;
	public String tag;
	public int color;

	static {
		types = new SparseArray<PokemonType>();
		types.append(1, new PokemonType("Normal", "NORMAL", 0xffa8a878));
		types.append(2, new PokemonType("Fighting", "FIGHT", 0xffc03028));
		types.append(3, new PokemonType("Flying", "FLYING", 0xffa890f0));
		types.append(4, new PokemonType("Poison", "POISON", 0xffa040a0));
		types.append(5, new PokemonType("Ground", "GROUND", 0xffe0c068));
		types.append(6, new PokemonType("Rock", "ROCK", 0xffb8a038));
		types.append(7, new PokemonType("Bug", "BUG", 0xffa8b820));
		types.append(8, new PokemonType("Ghost", "GHOST", 0xff705898));
		types.append(9, new PokemonType("Steel", "STEEL", 0xffb8b8d0));
		types.append(10, new PokemonType("Fire", "FIRE", 0xfff08030));
		types.append(11, new PokemonType("Water", "WATER", 0xff6890f0));
		types.append(12, new PokemonType("Grass", "GRASS", 0xff78c850));
		types.append(13, new PokemonType("Electric", "ELECTR", 0xfff8d030));
		types.append(14, new PokemonType("Psychic", "PSYCHC", 0xfff85888));
		types.append(15, new PokemonType("Ice", "ICE", 0xff98d8d8));
		types.append(16, new PokemonType("Dragon", "DRAGON", 0xff7038f8));
		types.append(17, new PokemonType("Dark", "DARK", 0xff705848));
		types.append(18, new PokemonType("Fairy", "FAIRY", 0xffff65d5));
	}

	public PokemonType(String name, String code, int color) {
		this.name = name;
		this.tag = code;
		this.color = color;
	}

	public static PokemonType get(int typeId) {
		return types.get(typeId);
	}

	public static PokemonType get(Move move) {
		return types.get(move.getType());
	}

	public static String getTypeName(int typeId) {
		return types.get(typeId).name;
	}

	public static String getTypeTag(int typeId) {
		return types.get(typeId).tag;
	}

	public static int getTypeColor(int typeId) {
		return types.get(typeId).color;
	}

}
