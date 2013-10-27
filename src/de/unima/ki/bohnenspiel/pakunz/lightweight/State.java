package de.unima.ki.bohnenspiel.pakunz.lightweight;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class State {
	
	public int[] board;
	
	public int store1 = 0;
	public int store2 = 0;
	
	public State(int[] board, int store1, int store2) 
	{
		this.board = board;
		this.store1 = store1;
		this.store2 = store2;
	} 
	
	public State(State state)
	{
		this.board = Arrays.copyOf(state.board, 12);
		this.store1 = state.store1;
		this.store2 = state.store2;
	}
	
}
