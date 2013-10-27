package de.unima.ki.bohnenspiel.pakunz;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class State {
	
	private int[] board;
	
	private int store1 = 0;
	private int store2 = 0;
	
	public State(int[] board, int store1, int store2) 
	{
		this.board = board;
		this.store1 = store1;
		this.store2 = store2;
	} 
	
	public State(State state)
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
		if(player == Search.PLAYER_ONE) {
			start = 0;
		} else {
			start = 6;
		}
		
		HashSet<Integer> moves = new HashSet<Integer>();
		for (int i = start; i < start+6; i++) {
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
            field = (field == 0) ? 11 : (field-1);
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
	
	public boolean cutoffTest(int depthLeft, int player)
	{
		if(depthLeft == 0 || terminalTest(player)) {
			return true;
		}
		return false;
	}
	
	public boolean terminalTest(int player)
	{
		int offset = 0;
		if(player != Search.PLAYER_ONE) {
			offset = 6;
		}
		
		int leftBeans = 0;
		for(int i=offset; i<offset+6; i++) {
			leftBeans += board[i];
		};
		
		// no beans left for current player
		if(leftBeans == 0) {
			return true;
		}
		
		return false;
	}
	
	/*
	public int getWinner()
	{
		if(store1 > 36) {
			return Search.PLAYER_ONE;
		} else if(store2 > 36) {
			return Search.PLAYER_TWO;
		} else {
			return 0;
		}
	}
	*/
	
	// rating range: [-72,+72]
	public int eval(int player)
	{
		/*
		int winner = getWinner();
		if(winner != 1) {
			return winner*Search.MAXVALUE;
		}
		*/
		
		// feature 1: beans left on player's board side
		//final float DANGER_FACTOR = 0.5f;
		//final float NO_DANGER_FACTOR = 0.8f;
		
		// beans found for a player
		int beans1 = 0;
		int beans2 = 0;
		// sum of beans found for a player
		int leftBeans1 = 0;
		int leftBeans2 = 0;
		// weighted sum of bean position
		//float leftBeansValue1 = 0.0f;
		//float leftBeansValue2 = 0.0f;
		
		for(int i=0; i<6; i++) {
			//beans1 = board[i];
			//beans2 = board[i+6];
			leftBeans1 += board[i];
			leftBeans2 += board[i+6];
			//leftBeansValue1 += (beans1<6 && beans1%2==1) ? (beans1*DANGER_FACTOR) : (beans1*NO_DANGER_FACTOR);
			//leftBeansValue2 += (beans2<6 && beans2%2==1) ? (beans2*DANGER_FACTOR) : (beans2*NO_DANGER_FACTOR);
			//leftBeansValue1 += (beans1<6 && beans1%2==1) ? DANGER_FACTOR : NO_DANGER_FACTOR;
			//leftBeansValue2 += (beans2<6 && beans2%2==1) ? DANGER_FACTOR : NO_DANGER_FACTOR;
		}
		
		int totalLeftBeans = leftBeans1 + leftBeans2;
		
		// no beans left for a player
		if(leftBeans1 == 0 && player == Search.PLAYER_ONE || leftBeans2 == 0 && player == Search.PLAYER_TWO || totalLeftBeans == 0)
		{ 
			return (store1 + leftBeans1) - (store2 + leftBeans2);
		}
		
		/*
		// feature 2: store of a player
		int storeValue1 = store1;
		int storeValue2 = store2;
		
		// weighed for protection against hunger strategy
		if(player == Search.PLAYER_ONE) {
			if(leftBeans1 > totalLeftBeans*0.33) {
				storeValue2 = (int)(store2*0.40f);
			} else if(leftBeans1 > totalLeftBeans*0.2) {
				storeValue2 = (int)(store2*0.70f);
			} else {
				storeValue2 = store2;
			}
		} else {
			if(leftBeans2 > totalLeftBeans*0.33) {
				storeValue1 = (int)(store1*0.40f);
			} else if(leftBeans2 > totalLeftBeans*0.2) {
				storeValue1 = (int)(store1*0.70f);
			} else {
				storeValue1 = store1;
			}
		}
		*/
		
		// combination
		int rating;
		rating = store1 - store2;
		//rating = storeValue1 - storeValue2;
		//int rating = (store1 + (int)leftBeansValue1) - (store2 + (int)leftBeansValue2);
		
		return rating;
		
		/*
		
		float leftBeanShare1 = leftBeans1/totalLeftBeans; // [0,1]

		// feature 2: beans in player's store
		// number of beans in store becomes more important when share of left beans drops
		float storeWeight = 0.50f;
		
		if(leftBeanShare1 <= 0.20f) {
			storeWeight = 0.7f;
		} else if(leftBeanShare1 <= 0.33f) {
			storeWeight = 0.6f;
		} else if(leftBeanShare1 > 0.33f && leftBeanShare1 < 0.67f) {
			storeWeight = 0.5f;
		} else if(leftBeanShare1 <= 0.80f) {
			storeWeight = 0.4f;
		// if(leftBeanShare1 > 0.80f)
		} else {
			storeWeight = 0.3f;
		}
		
		// feature combination: 
		// leftBeans + store <= 72
		// bei Maximalgewichtung 72*1,4 = 100,8 < 101 (MAXVALUE!)
		int rating1 = (int) (leftBeans1 * (1-storeWeight) + store1 * storeWeight);

		return rating1;
		
		*/
	}
	
}
