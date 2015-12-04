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
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Room baik waiting maupun bermain
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
    
    /**
     * konstruktor
     * @param name
     * @param parentLobby 
     */
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
        synchronized(players){
            for (Player pl : players) {
                if (pl.getPlayerName().equals(name))
                    return pl;
            }
        }
        return null;
    }
    
    /**
     * memasukkan player ke room
     * @param pl
     * @throws GameStartedException bila permainan sudah dimulai dan pl tidak dapat dimasukkan
     * @throws IOException 
     */
    public void enterPlayer(Player pl) throws GameStartedException, IOException{
        if (gameStarted) throw new GameStartedException("Game Started");
        pl.println("ROOM");
        pl.println(this.getRoomName());
        synchronized(players){
            players.add(pl);
        }
    }
    
    /**
     * @return banyaknya player berstatus connected
     */
    public int numPlayerConnected(){
        int sum = 0;
        synchronized(players){
            sum = players.stream().filter((p) -> (p.isConnected())).map((_item) -> 1).reduce(sum, Integer::sum);
        }
        return sum;
    }
    
    /**
     * menunggu start
     * cycle through all players
     * refer to protocol definition for further info
     */
    private void waitforStart(){
       while (!interrupted() && !gameStarted  && numPlayerConnected() > 0){
           boolean noAction = true;//flag untuk sleep. jika noAction, sleep dulu
           synchronized(players){
           Iterator<Player> i = players.iterator();
           while (i.hasNext()){
               Player p = i.next();
               if (p.isConnected()){
                try {
                    if(p.messageAvailable()){
                         noAction = false;
                         String inputLine =p.nextMessage();
                         if (inputLine!=null)
                         if (inputLine.equals("LEAVE")){
                             i.remove();
                             parentLobby.enterPlayer(p);
                         }else if (inputLine.equals("START")){
                             if (numPlayerConnected() >= MIN_PLAYER_TO_START ){
                                 gameStarted=true;
                                 broadcastln("STARTING");
                             }else{
                                 p.println("NOTENOUGHPLAYER");
                             }
                             break;
                         }else{
                             p.println("INVALIDINPUT");
                         }
                    }
                }  catch (InterruptedException ex) {
                       Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
                   }
               }else{
                   try{
                       i.remove();
                   }catch(ConcurrentModificationException e){
                       e.printStackTrace();
                   }
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
    }
        /**
         * bermain
         * @throws InterruptedException 
         */
    public void playGame() throws InterruptedException{
        boolean play = true;
        Table gameTable = new Table(WIDTH,HEIGHT);
        int playerInTurn = 0;
        synchronized(players){
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
        }
        gameStarted=false;
    }

    /**
     * switch between waitforStart and playGame
     */
    @Override
    public void run() {
        broadcastPlayers();
        while (numPlayerConnected() > 0){
            try {
                broadcastln("ROOM");
                waitforStart();
                if (gameStarted)
                    playGame();
            } catch (InterruptedException ex) {
                Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        parentLobby.deleteRoom(this);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    /**
     * mengirimkan msg + newline ke semua player
     * */
    private void broadcastln(String msg){
        for (Player p: players){
            if (p.isConnected()){
                PrintStream ps;
                p.println(msg);
            }
        }
    }
    
    /**
     * broadcast list of players
     */
    private void broadcastPlayers(){
        String broadcastMessage = "PLAYER";
        broadcastMessage += "\n" + players.size();
        for (int i=0;i<players.size();i++){
            broadcastMessage+='\n';
            Player p = players.get(i);
            broadcastMessage+=i;
            broadcastMessage+=' ';
            broadcastMessage+=p.getPlayerName();
            broadcastMessage+=' ';
            broadcastMessage+=p.isConnected()?"CONNECTED":"DISCONNECTED";
        }
        broadcastln(broadcastMessage);
    }
    
    /**
     * broadcast seluruh tabel
     * @param t 
     */
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
    
    /**
     * broadcast status WIN ke semua player
     * @param player
     * @param winPieces 
     */
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
