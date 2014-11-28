package com.ssttevee.pokemonandroid.helper;

public class QueuedMove {
	public Pokemon attacker;
	public Move move;
	public Pokemon target;

	public QueuedMove(Pokemon attacker, Move move, Pokemon target) {
		this.attacker = attacker;
		this.move = move;
		this.target = target;
	}

	public void use() {
		attacker.attack(target, move);
		target.save();
	}
}
