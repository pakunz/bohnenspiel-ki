package de.unima.ki.bohnenspiel.pakunz;

import java.util.Set;

public class SimpleSearch extends Search {

	private int searchDepth = 10;
	
	public SimpleSearch(int searchDepth)
	{
		this.searchDepth = searchDepth;
	}
	
	// returns the best move
	@Override
	public int getBestMove(State s, int player) 
	{		
		// cutoff test
		if(s.terminalTest(player)) {
			return -1;
		}
		
		// initialize best value found (i.e. worst value for current player)
		//int bestValue = (-player)*Search.MAXVALUE;
		int bestValue = -Search.MAXVALUE;
		// no best move found so far
		int bestMove = -1;
		
		// throw-away variable so "undoMove" is not necessary
		State currentState = null;
		Set<Integer> moves = s.getMoves(player);
		for (int move : moves) {
			// copy state
			currentState = new State(s);
			currentState.doMove(move);
			// call negamax for other player, negate value
			int value = (-1)*this.negamax(currentState, -player, searchDepth-1);	
			// save max{-negamax(s) | s Nachfolger von n }
			if(value >= bestValue) {
				bestValue = value;
				bestMove = move;
			}
		}
		
		return bestMove;
	}
	
	
	// returns an eval value!
	private int negamax(State s, int player, int level) {

		// cutoff test
		if(s.cutoffTest(level,player)) {
			return player*s.eval(player);
		}
		
		// initialize best value found (i.e. worst value for current player)
		//int bestValue = (-player)*Search.MAXVALUE;
		int bestValue = -Search.MAXVALUE;
		
		// throw-away variable so "undoMove" is not necessary
		State currentState = null;
		Set<Integer> moves = s.getMoves(player);
		for (int move : moves) {
			// copy state
			currentState = new State(s);
			currentState.doMove(move);
			// call negamax for other player, negate value
			int value = (-1)*this.negamax(currentState, -player, level-1);
			// save max{-negamax(s) | s Nachfolger von n }
			if(value >= bestValue) {
				bestValue = value;
			}
		}
		
		return bestValue;
	}
	
	@Override
	public String getName() {
		return "simpleNegaMax";
	}

}
