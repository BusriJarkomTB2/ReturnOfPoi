/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomokuserver;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nim_13512501
 */
public class Player {
    private String name;
    private Socket socket;
    private boolean connected;
    public Player(String name, Socket socket, boolean connected){
        this.name = name;
        this.socket = socket;
        this.connected = connected;
    }
    
    public String getName(){
        return name;
    }
    
    public Socket getSocket(){
        return socket;
    }
    
    public boolean isConnected(){
        return connected;
    }
    
    public void setConnected(boolean connected){
        this.connected = connected;
    }
    
    public int[] getMove()
    {
        int[] playMove = new int[2];
        try {
            Scanner myScan = new Scanner(getSocket().getInputStream());
            PrintStream ps = new PrintStream(getSocket().getOutputStream());
            
            ps.println("MOVE");
            
            boolean gotNumber = false;
            do{
                    playMove[0] = myScan.nextInt();
                    System.out.println("test " + playMove[0]);
                    playMove[1] = myScan.nextInt();
                    System.out.println("test " + playMove[1]);
                    gotNumber = true;
            }while(!gotNumber);
            
            return playMove;
        }
        catch(InputMismatchException ex){
            playMove=null;
        }catch(NumberFormatException ex){
            playMove=null;
        }catch(NoSuchElementException ex){
            connected = false;
            playMove=null;
        }catch (IOException ex) {
            setConnected(false);
            playMove=null;
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        return playMove;
    }
    
    public void reconnect(Socket s){
        if (s.isConnected() && !s.isClosed()){
            this.socket = s;
            this.setConnected(true);
        }
    }
}
