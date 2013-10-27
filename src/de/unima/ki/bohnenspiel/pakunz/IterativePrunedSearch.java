package de.unima.ki.bohnenspiel.pakunz;

import java.util.Set;

public class IterativePrunedSearch extends Search {

	// the default depth as a fallback
	private final int standardDepth = 12;
	
	// the depth to start the search from
	private int startDepth;
	// the current global search depth
	private int searchDepth;
	
	// how long to wait for iterative deepening to finish
	private int timeout;
	
	// the currently stored best move of a running recursion
	private int bestMove;
	// the last result of a finished recursion
	private int foundNextMove;
	
	public IterativePrunedSearch(int timeout, int startDepth)
	{
		this.timeout = timeout;
		this.startDepth = startDepth;
	}
	
	// returns the best move
	@SuppressWarnings("deprecation")
	@Override
	public int getBestMove(final State s, final int player) 
	{		
		// no best move found
		foundNextMove = -1;

		// initialize search window and depth
		final int alpha = -Search.MAXVALUE;
		final int beta = Search.MAXVALUE;
		final int finalStartDepth = startDepth;
		
		Thread iterativeThread = new Thread(new Runnable() {
			public void run() {
				// counter for the loop
				searchDepth = finalStartDepth;
				while(searchDepth < 100) {
					// best move for this iteration
					bestMove = -1;
					// set the object's current search depth (for return condition)
					negamax(s, player, searchDepth, alpha, beta);
					// safe the found best move (overridden in next iteration)
					if(bestMove >= 0) {
						foundNextMove = bestMove;
						// print success message
						System.out.println("Search depth " + searchDepth + " completed, move: " + foundNextMove);
						searchDepth++;
					} else {
						System.out.println("Error at search depth " + searchDepth + ", no move found.");
					}
				}
			}
		});
		iterativeThread.start();
		
		try {
			Thread.sleep(timeout);
			iterativeThread.stop();
		} catch(Exception e) {
			// kill the iterative thread
			System.out.println("Error going to sleep, stop thread violently and compute for standard depth.");
			iterativeThread.stop();
			// do the search one time with secure depth
			negamax(s, player, standardDepth, alpha, beta);
			foundNextMove = bestMove;
		}
		
		return foundNextMove;
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
		String name = null;
		name = "Pokeball";
		//name = "tuesdaynightwash_v3.0Retro";
		return name;
	}

}
