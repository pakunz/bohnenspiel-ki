package de.unima.ki.bohnenspiel.pakunz;

public class RandomSearch extends Search {

	int offset = 0;
	
	public String getName()
	{
		return "Random AI";
	}
	
	@Override
	public int getBestMove(State s, int player) {
		
		int offset = 0;
		if(player == -1) {
			offset = 6;
		}
		
		int[] board = s.getBoard();
		
		// calculate fieldID
        int selectField;
        // System.out.println("Finde Zahl: ");
        do {
            selectField = (int) (Math.random() * 6) + offset;
            // System.out.println("\t-> " + selectField );
        } while(board[selectField] == 0);
        
        return selectField;
	}

}
