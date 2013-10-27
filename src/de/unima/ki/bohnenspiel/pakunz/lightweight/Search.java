package de.unima.ki.bohnenspiel.pakunz.lightweight;

import java.util.HashSet;
import java.util.Set;

public abstract class Search {

	public static final int MAXVALUE = 999999999;
	public static final int PLAYER_ONE = 1;
	public static final int PLAYER_TWO = -1;
	
	public abstract int getBestMove(State s, int player);
	
	public abstract String getName();

	/* static methods */
	
	public static Set<Integer> getMoves(State s, int player) {
		int start = 0;
		if(player == Search.PLAYER_ONE) {
			start = 0;
		} else {
			start = 6;
		}
		int end = start + 5;
		
		Set<Integer> moves = new HashSet<Integer>();
		for (int i = start; i <= end; i++) {
			if (s.board[i] != 0) {
				moves.add(new Integer(i));
			}
		}
		
		return moves;	
	}
	
	public static void doMove(State s, int field)
	{
		int startField = field;

        int beansLeft = s.board[field];
        s.board[field] = 0;
        
        // afterwards, field contains the last field reached
        while(beansLeft > 0) {
            field = (++field) % 12;
            s.board[field]++;
            beansLeft--;
        }

        // steal beans in last n connected fields where condition holds
        while(s.board[field] == 2 || s.board[field] == 4 || s.board[field] == 6) {
        	// player one moved
            if(startField < 6) {
            	s.store1 += s.board[field];
            } else {
            	s.store2 += s.board[field];
            }
            s.board[field] = 0;
            // (-1) % 12 = 11, not working correctly
            field = (field == 0) ? field = 11 : --field;
        }
	}
	
	public static String toString(State s)
	{
		String str = "";
        for(int i = 11; i >= 6; i--) {
            if(i != 6) {
            	str += s.board[i] + "; ";
            } else {
            	str += s.board[i];
            }
        }

        str += "\n";
        for(int i = 0; i <= 5; i++) {
            if(i != 5) {
            	str += s.board[i] + "; ";
            } else {
            	str += s.board[i];
            }
        }

        return str;
	}
	
	public static boolean cutoffTest(State s, int depthLeft)
	{
		if(depthLeft == 0 || Search.terminalTest(s)) {
			return true;
		}
		return false;
	}
	
	public static boolean terminalTest(State s)
	{
		return false;
	}
	
	public static int getWinner(State s)
	{
		if(s.store1 > 36) {
			return Search.PLAYER_ONE;
		} else if(s.store2 > 36) {
			return Search.PLAYER_TWO;
		} else {
			return 0;
		}
	}
	
	public static int eval(State s)
	{
		int winner = Search.getWinner(s);
		if(winner != 0) {
			return winner*Search.MAXVALUE;
		}
		
		float storeFactor = 1;
		float boardFactor = 1.5f;
		
		float rating = 0;
		
		rating += storeFactor*s.store1;
		rating -= storeFactor*s.store2;
		
		for(int i=0; i<12; i++) {
			if(i<6) {
				rating += boardFactor*s.board[i];
			} else {
				rating -= boardFactor*s.board[i];
			}
		}
		
		return (int)rating;
	}
	
}
