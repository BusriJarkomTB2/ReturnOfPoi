/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomokuserver;

import java.io.IOException;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Player yang dilempar ke sini dapat melihat ada apa di lobby,
 * join, create, dan refresh
 * @author nim_13512501
 */
public class Lobby extends Thread{
    private BlockingQueue<Player> players = new LinkedBlockingQueue<Player>();
    private List<Room> rooms = Collections.synchronizedList(new LinkedList<Room>());
    
    /**
     * 
     * @param name
     * @return true bila player dengan nama name masih terhubung di Lobby atau di suatu Room
     * 
     */
    public boolean playerConnected(String name){
        Player pl = getPlayerWithName(name);
        if (pl!=null)
            return pl.isConnected();
        else return false;
    }
    /**
     * 
     * @param name
     * @return "LOBBY" bila player dengan nama name ada di lobby, "STARTING" bila ada di permainan, "ROOM" bila menunggu di room
     */
    public String playerLocation(String name){
        synchronized(players){
            if (pOnProcess!=null)
                if (pOnProcess.getPlayerName().equals(name))
                    return "LOBBY";
                Iterator<Player> it = players.iterator();
                while(it.hasNext()){
                    Player pl = it.next();
                    if (pl.getPlayerName().equals(name))
                        return "LOBBY";
                }
        }
        for (int i=0;i<rooms.size();i++){
            Player pl = rooms.get(i).getPlayerWithName(name);
            if (pl != null){
                if (rooms.get(i).gameStarted()){return "STARTING";}
                else return "ROOM";
            }
        }
        return null;
    }
    
    Player pOnProcess = null;
   /**
    * 
    * @param name
    * @return objek player dengan nama name bila ada di lobby atau suatu room, null bila tidak
    */     
    public Player getPlayerWithName(String name){
        synchronized(players){
            if (pOnProcess!=null)
                if (pOnProcess.getPlayerName().equals(name))
                    return pOnProcess;
            Iterator<Player> it = players.iterator();
            while(it.hasNext()){
                Player pl = it.next();
                if (pl.getPlayerName().equals(name))
                    return pl;
            }
        }
        for (int i=0;i<rooms.size();i++){
            Player pl = rooms.get(i).getPlayerWithName(name);
            if (pl != null) return pl;            
        }
        return null;
    }
    
    /**
     * menaruh pl di dalam lobby
     * @param pl
     * @throws InterruptedException 
     */
    public void enterPlayer(Player pl) throws InterruptedException{
        players.put(pl);
        sendLobbyInfo(pl);
    }
    
    /**
     * menghapus room
     * @param r 
     */
    public void deleteRoom(Room r){
        rooms.remove(r);
    }
    
    /**
     * 
     * @param ID
     * @return objek Room dengan RoomID ID
     */
    public Room getRoomFromID(int ID){
        for (Room r : rooms){
            if (r.getRoomID()==ID)
                return r;
        }
        
        return null;
    }
    
    /**
     * mengirimkan informasi lobby kepada player pl
     * @param pl 
     */
    public void sendLobbyInfo(Player pl){
        pl.println("LOBBY");
        pl.println("ROOMLIST");
        pl.println(rooms.size());
        for (Room r : rooms){
            pl.println(r.getRoomID() + " " + r.getRoomName()
                    + " " + (r.gameStarted()?"STARTED":"WAITING"));
        }
        
    }
    
    /**
     * loop memproses message dari player
     */
    @Override
    public void run() {
        int countNoAction = 0; //untuk sleep bila tidak ada message agar tidak busy wait
       while (!interrupted()){
            try {
                pOnProcess =players.take();
                if (!pOnProcess.isConnected()){
                    players.put(pOnProcess);
                }else
                    try {
                        boolean putPlayerAgain = true;
                    if(pOnProcess.messageAvailable()){
                        countNoAction++;
                        String inputLine =pOnProcess.nextMessage();
                        if (inputLine!=null)
                            if (inputLine.equals("REFRESH"))
                                sendLobbyInfo(pOnProcess);
                            else if (inputLine.equals("CREATE")){
                                pOnProcess.println("NAME");
                                String name = pOnProcess.nextMessage();
                                if (name!=null){
                                    Room r = new Room(name, this);
                                    try {
                                        r.enterPlayer(pOnProcess);
                                    } catch (GameStartedException ex) {
                                        Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    putPlayerAgain = false;
                                    rooms.add(r);
                                    r.start();
                                }
                            }else if (inputLine.equals("LEAVE")){
                                putPlayerAgain = false;
                                pOnProcess.setConnected(false);
                            }else try{
                                int roomID = Integer.parseInt(inputLine);
                                Room r = getRoomFromID(roomID);
                                if (r==null){
                                    pOnProcess.println("NOTFOUND");
                                    sendLobbyInfo(pOnProcess);
                                }else{
                                    r.enterPlayer(pOnProcess);
                                    putPlayerAgain=false;
                                }
                            }catch(NumberFormatException e){
                                pOnProcess.println("INVALIDINPUT");
                                sendLobbyInfo(pOnProcess);
                            } catch (GameStartedException ex) {
                                pOnProcess.println("ALREADYSTARTED");
                                sendLobbyInfo(pOnProcess);
                            }
                    }
                    if (putPlayerAgain) players.put(pOnProcess);
                } catch (IOException ex) {
                    pOnProcess.setConnected(false);
                    Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (interrupted())
                    break;
                
                if (countNoAction>=players.size()) try {
                    countNoAction = 0;
                    sleep(10);
                } catch (InterruptedException ex) {
                }} catch (InterruptedException ex) {
                Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
            }
           
       }
       
    }
}
