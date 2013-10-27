package de.unima.ki.bohnenspiel.pakunz.lightweight;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.Vector;

public class Main
{
	static boolean isOpener = false;
	static int gameID = 108;
	
	static String server = "http://drachten.informatik.uni-mannheim.de";
    static String name   = "/SET IN MAIN METHOD/";
    
    static int p1 = 0;
    static int p2 = 0;
   
    static Search ai = new SimpleSearch(10);
	
	public static int MOVE_STATE_CAN_BE_STARTED = 0;
	public static int MOVE_STATE_YOUR_TURN = -1;
	public static int MOVE_STATE_GAME_FINISHED = -2;
	public static int MOVE_STATE_NOT_YOUR_TURN = -3;
	public static int MOVE_STATE_PLAYER_MISSING = -4;
	
	public static int GAME_STATE_WAITING = 0;
	public static int GAME_STATE_RUNNING = 1;
	public static int GAME_STATE_FINISHED = 2;

    public static void main(String[] args) throws Exception
    {
    	Main.name = ai.getName();
    	
    	System.out.println(load(server));
    	if(isOpener) { 
    		createGame();
    	} else {
			openGames();
			joinGame(String.valueOf(gameID));
    	}
    }


    static void createGame() throws Exception
    {
        String url = server + "/api/creategame/" + name;
        String gameID = load(url);
        System.out.println("Spiel erstellt. ID: " + gameID);

        url = server + "/api/check/" + gameID + "/" + name;
        while(true) {
            Thread.sleep(3000);
            String state = load(url);
            System.out.print("." + " (" + state + ")");
            if(state.equals("0") || state.equals("-1")){
                break;
            } else if (state.equals("-2")){
                System.out.println("time out");
                return;
            }
        }
        play(gameID, 0);
    }


    static void openGames() throws Exception
    {
        String url = server + "/api/opengames";
        String[] opengames = load(url).split(";");
        for(int i = 0; i < opengames.length; i++) {
            System.out.println(opengames[i]);
        }
    }


    static void joinGame(String gameID) throws Exception
    {
        String url = server + "/api/joingame/" + gameID + "/" + name;
        String state = load(url);
        System.out.println("Join-Game-State: " + state);
        if(state.equals("1")) {
            play(gameID, 6);
        } else if (state.equals("0")) {
            System.out.println("error (join game)");
        }
    }


    static void play(String gameID, int offset) throws Exception
    {
        String checkURL = server + "/api/check/" + gameID + "/" + name;
        String statesMsgURL = server + "/api/statemsg/" + gameID;
        String stateIdURL = server + "/api/state/" + gameID;
        
        // initialize state
        int[] board = { 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6 }; // position 1-12
        int start, end;
        if(offset == 0) {
            start = 7;
            end = 12;
        } else {
            start = 1;
            end = 6;
        }
        
        // State s = new State(board, isOpener?p1:p2, isOpener?p2:p1);
        State s = new State(board, p1, p2);
        int player = isOpener ? 1 : -1;
        
        while(true) {
            
        	// poll once a second
        	Thread.sleep(100);
            
            // retrieve status from API
            int moveState = Integer.parseInt(load(checkURL));
            int gameState = Integer.parseInt(load(stateIdURL));
            
            // game not finished and either last move was yours or it is your turn
            if(gameState != GAME_STATE_FINISHED && ((start <= moveState && moveState <= end) || moveState == MOVE_STATE_YOUR_TURN)) {
            	
            	// not "your turn"
            	if (moveState != MOVE_STATE_YOUR_TURN) {
            		// translate from [1,12] to [0,11]
                    int opponentMove = moveState - 1;
                    Search.doMove(s,opponentMove);
                    System.out.println("Gegner waehlte: " + moveState + " /\t" + s.store1 + " - " + s.store2);
                    System.out.println(s.toString() + "\n");
                }
          
                int nextMove = ai.getBestMove(s, player);
                
                if(nextMove >= 0) {
                	Search.doMove(s,nextMove);
                    System.out.println("Waehle Feld: " + (nextMove+1) + " /\t" + s.store1 + " - " + s.store2);
                    System.out.println(s.toString() + "\n\n");
                    // call API with move
                    move(gameID, nextMove+1);
                } else {
                	// if no next move possible, do nothing
                	System.out.println("No move found.");
                }
                
            // game finished
            } else if(moveState == MOVE_STATE_GAME_FINISHED || gameState == GAME_STATE_FINISHED) {
                System.out.println("GAME Finished");
                checkURL = server + "/api/statemsg/" + gameID;
                System.out.println(load(checkURL));
                return;
                
            // game not finished, but nothing to do here (*flies away*)
            } else {
                System.out.println("- " + moveState + "\t\t" + load(statesMsgURL));
            }

        }
    }

    /*
    static int[] updateBoard(int[] board, int field)
    {
        int startField = field;
        
        int value = board[field];
        board[field] = 0;
        while(value > 0) {
            field = (++field) % 12;
            board[field]++;
            value--;
        }

        if(board[field] == 2 || board[field] == 4 || board[field] == 6) {
            do {                               
                if (startField <6){
                    p1 += board[field]; 
                } else {
                    p2 += board[field];
                }
                board[field] = 0;
                field = (field == 0) ? field = 11 : --field;
            } while(board[field] == 2 || board[field] == 4 || board[field] == 6);
        }
        return board;
    }


    static String printBoard(int[] board)
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
	*/

    static void move(String gameID, int fieldID) throws Exception
    {
        String url = server + "/api/move/" + gameID + "/" + name + "/" + fieldID;
        System.out.println(load(url));
    }


    static String load(String url) throws Exception
    {
        URI uri = new URI(url.replace(" ", ""));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uri.toURL().openConnection().getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedReader.close();
        return (sb.toString());
    }  	
    
}
