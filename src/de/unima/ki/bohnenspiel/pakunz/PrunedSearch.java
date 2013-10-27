package de.unima.ki.bohnenspiel.pakunz;

import java.util.Set;

public class PrunedSearch extends Search {

	private int searchDepth = 10;
	private int bestMove;
	
	public PrunedSearch(int searchDepth)
	{
		this.searchDepth = searchDepth;
	}
	
	// returns the best move
	@Override
	public int getBestMove(State s, int player) 
	{		
		// no best move found
		bestMove = -1;
		
		// initialize search window
		int alpha = -Search.MAXVALUE;
		int beta = Search.MAXVALUE;
		
		// recursively find best move
		this.negamax(s, player, searchDepth, alpha, beta);
		
		return bestMove;
	}
	
	
	// returns an eval value!
	private int negamax(State s, int player, int level, int alpha, int beta) {

		// cutoff test
		if(s.cutoffTest(level,player)) {
			return player*s.eval(player);
		}
		
		// initialize best value found (i.e. worst value for current player)
		//int bestValue = (-player)*Search.MAXVALUE;
		
		int bestValue = alpha;

		// throw-away variable so "undoMove" is not necessary
		State currentState = null;
		Set<Integer> moves = s.getMoves(player);
		moveLoop: for (int move : moves) {
			// copy state
			currentState = new State(s);
			currentState.doMove(move);
			
			// call negamax for other player, negate value
			int value = (-1)*this.negamax(currentState, -player, level-1, -beta, -bestValue);
			
			// save max{-negamax(s) | s Nachfolger von n }, strict comparison!
			if(value > bestValue) {
				bestValue = value;
				if(bestValue >= beta) {
					break moveLoop;
				}
				if(level == searchDepth) {
					bestMove = move;
				}
			}
		}
		
		return bestValue;
	}
	
	@Override
	public String getName() {
		return "prunedNegaMax";
	}

}
