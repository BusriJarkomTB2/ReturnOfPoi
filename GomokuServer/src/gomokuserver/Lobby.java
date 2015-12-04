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
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nim_13512501
 */
public class Lobby extends Thread{
    private List<Player> players = Collections.synchronizedList(new LinkedList<Player>());
    private List<Room> rooms = Collections.synchronizedList(new LinkedList<Room>());
    
    public boolean playerConnected(String name){
        Player pl = getPlayerWithName(name);
        if (pl!=null)
            return pl.isConnected();
        else return false;
    }
    
    public String playerLocation(String name){
        for (int i=0; i< players.size();i++){
            Player pl = players.get(i);
            if (pl.getName().equals(name))
                return "LOBBY";
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
        
    public Player getPlayerWithName(String name){
        for (int i=0; i< players.size();i++){
            Player pl = players.get(i);
            if (pl.getName().equals(name))
                return pl;
        }
        for (int i=0;i<rooms.size();i++){
            Player pl = rooms.get(i).getPlayerWithName(name);
            if (pl != null) return pl;            
        }
        return null;
    }
    
    public void enterPlayer(Player pl){
        players.add(pl);
        sendLobbyInfo(pl);
    }
    
    public void deleteRoom(Room r){
        rooms.remove(r);
    }
    
    public Room getRoomFromID(int ID){
        for (Room r : rooms){
            if (r.getRoomID()==ID)
                return r;
        }
        
        return null;
    }
    
    public void sendLobbyInfo(Player pl){
        OutputStream os;
        try {
            os = pl.getSocket().getOutputStream();
            PrintStream ps = new PrintStream(os);
            ps.println("LOBBY");
            ps.println("ROOMLIST");
            ps.println(rooms.size());
            for (Room r : rooms){
                ps.println(r.getRoomID() + " " + r.getRoomName()
                        + " " + (r.gameStarted()?"STARTED":"WAITING"));
            }
        } catch (IOException ex) {
            Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @Override
    public void run() {
       while (!interrupted()){
           boolean noAction = true;//flag untuk sleep. jika noAction, sleep dulu
           synchronized(players){
           Iterator<Player> i = players.iterator();
           while (i.hasNext()){
               Player p =i.next();
               if (!p.isConnected()){
                   try{
                    i.remove();
                   }catch(ConcurrentModificationException e){
                       e.printStackTrace();
                   }
               }else try {
                   InputStream is = p.getSocket().getInputStream();
                   PrintStream ps = new PrintStream(p.getSocket().getOutputStream());
                   if(is.available()>0){
                        noAction = false;
                        BufferedReader in = new BufferedReader(new InputStreamReader(is));
                        String inputLine =in.readLine();
                        if (inputLine.equals("REFRESH"))
                            sendLobbyInfo(p);
                        else if (inputLine.equals("CREATE")){
                            ps.println("NAME");
                            ps.flush();
                            String name = in.readLine();
                            Room r = new Room(name, this);
                            try {
                                r.enterPlayer(p);
                            } catch (GameStartedException ex) {
                                Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            i.remove();
                            rooms.add(r);
                            r.start();
                        }else if (inputLine.equals("LEAVE")){
                            i.remove();
                            p.setConnected(false);
                            p.getSocket().close();
                        }else try{
                            int roomID = Integer.parseInt(inputLine);
                            Room r = getRoomFromID(roomID);
                            if (r==null){
                                ps.println("NOTFOUND");
                                sendLobbyInfo(p);
                            }else{
                                r.enterPlayer(p);
                                i.remove();
                            }
                        }catch(NumberFormatException e){
                            ps.println("INVALIDINPUT");
                            sendLobbyInfo(p);
                        } catch (GameStartedException ex) {
                            ps.println("ALREADYSTARTED");
                            sendLobbyInfo(p);
                        }
                   }
               } catch (IOException ex) {
                   p.setConnected(false);
                   Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, null, ex);
               }
               if (interrupted())
                   break;
           }
           if (noAction) try {
               sleep(10);
           } catch (InterruptedException ex) {
           }
           }
       }
       
    }
}
