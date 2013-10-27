package de.unima.ki.bohnenspiel.pakunz;

public abstract class Search {

	public static final int MAXVALUE = 72 + 1; // 101
	public static final int PLAYER_ONE = 1;
	public static final int PLAYER_TWO = -1;
	
	public abstract int getBestMove(State s, int player);
	
	public abstract String getName();
	
}
