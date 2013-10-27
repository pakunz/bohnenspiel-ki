package de.unima.ki.bohnenspiel.pakunz;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StupidState {

	public static final int MAXVALUE = 999999999;
	public static final int PLAYER_ONE = 1;
	public static final int PLAYER_TWO = -1;
	
	private int[] board;
	
	private int store1 = 0;
	private int store2 = 0;
	
	public StupidState(int[] board, int store1, int store2) 
	{
		this.board = board;
		this.store1 = store1;
		this.store2 = store2;
	} 
	
	public StupidState(StupidState state)
	{
		this.board = Arrays.copyOf(state.getBoard(), 12);
		this.store1 = state.getStore1();
		this.store2 = state.getStore2();
	}
	
	/* getters and setters */
	public void setBoard(int[] board)
	{
		this.board = board;
	}
	
	public int[] getBoard()
	{
		return board;
	}
	
	public void setStore1(int store1)
	{
		this.store1 = store1;
	}
	
	public int getStore1()
	{
		return store1;
	}
	
	public void setStore2(int store2)
	{
		this.store2 = store2;
	}
	
	public int getStore2()
	{
		return store2;
	}
	
	public Set<Integer> getMoves(int player) {
		int start = 0;
		if(player == 1) {
			start = 0;
		} else {
			start = 6;
		}
		int end = start + 5;
		
		HashSet<Integer> moves = new HashSet<Integer>();
		for (int i = start; i <= end; i++) {
			if (board[i] != 0) {
				moves.add(new Integer(i));
			}
		}
		
		return moves;	
	}
	
	public void doMove(int field)
	{
		int startField = field;

        int beansLeft = board[field];
        board[field] = 0;
        
        // afterwards, field contains the last field reached
        while(beansLeft > 0) {
            field = (++field) % 12;
            board[field]++;
            beansLeft--;
        }

        // steal beans in last n connected fields where condition holds
        while(board[field] == 2 || board[field] == 4 || board[field] == 6) {
        	// player one moved
            if(startField < 6) {
            	store1 += board[field];
            } else {
            	store2 += board[field];
            }
            board[field] = 0;
            field = (field == 0) ? field = 11 : --field;
        }
	}
	
	public String toString()
	{
		String s = "";
        for(int i = 11; i >= 6; i--) {
            if(i != 6) {
                s += board[i] + "; ";
            } else {
                s += board[i];
            }
        }

        s += "\n";
        for(int i = 0; i <= 5; i++) {
            if(i != 5) {
                s += board[i] + "; ";
            } else {
                s += board[i];
            }
        }

        return s;
	}
	
	public boolean cutoffTest(int depthLeft)
	{
		if(depthLeft == 0 || terminalTest()) {
			return true;
		}
		return false;
	}
	
	public boolean terminalTest()
	{
		return false;
	}
	
	public int getWinner()
	{
		if(store1 > 36) {
			return PLAYER_ONE;
		} else if(store2 > 36) {
			return PLAYER_TWO;
		} else {
			return 0;
		}
	}
	
	public int eval()
	{
		int winner = getWinner();
		if(winner != 0) {
			return winner*MAXVALUE;
		}
		
		float storeFactor = 1;
		float boardFactor = 1.5f;
		
		float rating = 0;
		
		rating += storeFactor*store1;
		rating -= storeFactor*store2;
		
		for(int i=0; i<12; i++) {
			if(i<6) {
				rating += boardFactor*board[i];
			} else {
				rating -= boardFactor*board[i];
			}
		}
		
		return (int)rating;
	}
	
}
