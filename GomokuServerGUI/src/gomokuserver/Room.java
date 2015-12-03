/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomokuserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nim_13512501
 */
public class Room extends Thread{
    //config
    private static final int MIN_PLAYER_TO_START = 3;
    private static final int HEIGHT = 20;
    private static final int WIDTH = 20;
    
    //attrs
    private static final AtomicInteger count = new AtomicInteger(0); 
    private final int ID;
    private final String name;
    private final Lobby parentLobby;
    private boolean gameStarted;
    
    public Room(String name, Lobby parentLobby){
        players = Collections.synchronizedList(new ArrayList<Player>());
        ID = count.incrementAndGet();
        this.name = name;
        gameStarted = false;
        this.parentLobby = parentLobby;
    }
    
    public String getRoomName(){
        return name;
    }
    
    public int getRoomID(){
        return ID;
    }

    private List<Player> players;
    
    public Player getPlayerWithName(String name){
        for (Player pl : players) {
            if (pl.getName().equals(name))
                return pl;
        }
        return null;
    }
    
    public void enterPlayer(Player pl) throws GameStartedException, IOException{
        if (gameStarted) throw new GameStartedException("Game Started");
        PrintStream ps = new PrintStream(pl.getSocket().getOutputStream());
        ps.println("ROOM");
        ps.println(this.getRoomName());
        players.add(pl);
    }
    
    public int numPlayerConnected(){
        int sum = 0;
        sum = players.stream().filter((p) -> (p.isConnected())).map((_item) -> 1).reduce(sum, Integer::sum);
        return sum;
    }
    
    private void waitforStart(){
       while (!interrupted() && !gameStarted  && numPlayerConnected() > 0){
           boolean noAction = true;//flag untuk sleep. jika noAction, sleep dulu
           Iterator<Player> i = players.iterator();
           while (i.hasNext()){
               Player p = i.next();
               if (p.isConnected()){
                try {
                    InputStream is = p.getSocket().getInputStream();
                    PrintStream ps = new PrintStream(p.getSocket().getOutputStream());
                    if(is.available()>0){
                         noAction = false;
                         BufferedReader in = new BufferedReader(new InputStreamReader(is));
                         String inputLine =in.readLine();
                         if (inputLine.equals("LEAVE")){
                             i.remove();
                             parentLobby.enterPlayer(p);
                         }else if (inputLine.equals("START")){
                             if (numPlayerConnected() >= MIN_PLAYER_TO_START ){
                                 gameStarted=true;
                                 broadcastln("STARTING");
                             }else{
                                 ps.println("NOTENOUGHPLAYER");
                             }
                             break;
                         }else{
                             ps.println("INVALIDINPUT");
                         }
                    }
                } catch (IOException ex) {
                    p.setConnected(false);
                    Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
                }
               }else{
                   players.remove(p);
               }
               if (interrupted())
                   break;
           }
           if (noAction) try {
               sleep(1000);
           } catch (InterruptedException ex) {
           }
           broadcastPlayers();
       }
    }
        
    public void playGame(){
        boolean play = true;
        Table gameTable = new Table(WIDTH,HEIGHT);
        int playerInTurn = 0;
        while (play && numPlayerConnected()>0){
            Player curplayer = players.get(playerInTurn);
            if (curplayer.isConnected()){
                gameTable.printTable();
                broadcastPlayers();
                broadcastTable(gameTable);
                boolean validMove;
                int [] move;
                do {
                        move = curplayer.getMove();
                        if (move==null) break;
                        validMove = gameTable.makeMove(playerInTurn, move[0], move[1]);
                } while (!validMove);

                // check win
                if (move!=null){
                    int win = gameTable.checkWin();
                    if (win >=0) {
                            gameTable.printTable();
                            broadcastPlayers();
                            broadcastTable(gameTable);
                            broadcastWin(win,gameTable.getWinPieces());
                            play=false;
                    }
                }
            }
            playerInTurn=(playerInTurn + 1) % players.size();//next player
        }
        gameStarted=false;
    }

    @Override
    public void run() {
        broadcastPlayers();
        while (numPlayerConnected() > 0){
            broadcastln("ROOM");
            waitforStart();
            if (gameStarted)
                playGame();
        }
        parentLobby.deleteRoom(this);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    //mengirimkan msg + newline
    private void broadcastln(String msg){
        for (Player p: players){
            if (p.isConnected()){
                PrintStream ps;
                try {
                    ps = new PrintStream (p.getSocket().getOutputStream());
                    ps.println(msg);
                } catch (IOException ex) {
                    p.setConnected(false);
                    Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private void broadcastPlayers(){
        String broadcastMessage = "PLAYER";
        broadcastMessage += "\n" + players.size();
        for (int i=0;i<players.size();i++){
            broadcastMessage+='\n';
            Player p = players.get(i);
            broadcastMessage+=i;
            broadcastMessage+=' ';
            broadcastMessage+=p.getName();
            broadcastMessage+=' ';
            broadcastMessage+=p.isConnected()?"CONNECTED":"DISCONNECTED";
        }
        broadcastln(broadcastMessage);
    }
    
    private void broadcastTable(Table t){
        String broadcastMessage = "TABLE";
        int[][] tab = t.getStore();
        for (int i=0;i<tab.length;i++){
            broadcastMessage+='\n';
            for (int j=0;j<tab[i].length;j++){
                broadcastMessage+=' ';
                broadcastMessage+=tab[i][j];
            }
        }
        broadcastln(broadcastMessage);
    }
    
    private void broadcastWin(int player, int[][] winPieces){
        String broadcastMessage = "WIN";
        broadcastMessage+= '\n';
        broadcastMessage+= player;
        for (int i=0;i<winPieces.length;i++){
            broadcastMessage+='\n';
            for (int j=0;j<winPieces[i].length;j++){
                broadcastMessage+=' ';
                broadcastMessage+=winPieces[i][j];
            }
        }
        broadcastln(broadcastMessage);
    }
    
    public boolean gameStarted(){
        return gameStarted;
    }
    
}
