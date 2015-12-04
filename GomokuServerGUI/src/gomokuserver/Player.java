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
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nim_13512501
 */
public class Player implements Runnable{
    private String name;
    private Socket socket;
    private boolean connected;
    Thread plThread;
    public Player(String name, Socket socket, boolean connected){
        try {
            this.ps = new PrintStream(socket.getOutputStream());
        } catch (IOException ex) {
            connected = false;
        }
        this.name = name;
        this.socket = socket;
        this.connected = connected;
        plThread = (new Thread(this));
        plThread.start();
    }
    
    public String getPlayerName(){
        return name;
    }
    
    private Socket getSocket(){
        return socket;
    }
    
    public boolean isConnected(){
        return connected;
    }
    
    public void setConnected(boolean connected){
        this.connected = connected;
    }
    
    //pembacaan dan penulisan
    BlockingQueue<String> messages = new LinkedBlockingQueue<>();
    
    @Override
    public void run(){
        messages.clear();
        Scanner sc = null;
        try {
            sc = new Scanner(socket.getInputStream());
            while (sc.hasNext() && connected){
                messages.add(sc.next());
            }
        } catch (IOException ex) {
            connected = false;
        }
        connected = false;
        messages.add(" __@@!!FAILURE!!@@__ ");
    }
    
    public String nextMessage() throws InterruptedException{
        String msg = messages.take();
        if (msg.equals(" __@@!!FAILURE!!@@__ "))
            return null;
        else return msg;
    }
    
    /**
     * 
     * @return true if a message can be acquired without blocking
     */
    public boolean messageAvailable(){
        return !messages.isEmpty();
    }
    
    private PrintStream ps;
    
    public void println(Object o){
        ps.println(o);
        ps.flush();
    }
    
    public void print(Object o){
        ps.print(o);
        ps.flush();
    }
    
    public int[] getMove() throws InterruptedException
    {
        int[] playMove = new int[2];
        try {
            
            this.println("MOVE");
            
            boolean gotNumber = false;
            do{
                    String txt = nextMessage();
                    if (txt==null) return null;
                    playMove[0] = Integer.parseInt(txt);
                    txt = nextMessage();
                    if (txt==null) return null;
                    playMove[1] = Integer.parseInt(txt);
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
        }
        return playMove;
    }
    
    public void reconnect(Socket s){
        if (s.isConnected() && !s.isClosed()){
            try {
                this.socket = s;
                ps = new PrintStream(socket.getOutputStream());
                this.setConnected(true);
                if (!plThread.isAlive())
                    plThread = (new Thread(this));
                    plThread.start();
            } catch (IOException ex) {
                connected = false;
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
