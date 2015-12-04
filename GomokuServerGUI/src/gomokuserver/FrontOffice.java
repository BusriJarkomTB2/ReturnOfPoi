/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomokuserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Kelas ini untuk menerima login pengguna
 * @author nim_13512501
 */
public class FrontOffice extends Thread{
    
    private BlockingQueue<Socket> queue = new LinkedBlockingQueue<Socket>();//antrian pemain untuk diproses
    private Lobby referalLobby = null; //lobby tempat melempar pemain
    public void setReferalLobby(Lobby lb){
        referalLobby = lb;
    }
    public Lobby getReferalLobby(){
        return referalLobby;
    }
    
    /**
     * meminta FrontOffice untuk menangani koneksi socket sc
     * @param sc 
     */        
    public void handle(Socket sc){
        System.out.println("asking for name...");
        PrintStream out;
        try {
            out = new PrintStream(sc.getOutputStream());
            out.println("NAME");
            out.flush();
            queue.add(sc);
        } catch (IOException ex) {
            Logger.getLogger(FrontOffice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * run looping berulang-ulang menangani dari antrian:
     * Apabila belum ada jawaban, ulang loop.
     * Apabila sudah ada jawaban, cek apakah nama sudah terhubung atau belum.
     * Jika sudah terhubung, coba reconnect. Jika belum, buat objek Player baru
     * dan lempar ke lobby
     */
    @Override
    public void run() {
        while (!interrupted()){
            try {
                boolean noAction = true;//flag untuk sleep. jika noAction, sleep dulu
                Socket sc = queue.take();
                InputStream scis = sc.getInputStream();
                OutputStream scos = sc.getOutputStream();
                if (scis.available()>0){
                    
                    noAction = false;
                    
                    System.out.println ("processing new player...");

                    PrintStream out = new PrintStream(scos);

                    String playerName;                

                    BufferedReader in = new BufferedReader(new InputStreamReader(scis));
                    playerName =in.readLine();

                    Player p = referalLobby.getPlayerWithName(playerName);//cari player di lobby
                    System.out.println("player name: "+playerName);

                    if (p==null)
                        referalLobby.enterPlayer(new Player(playerName,sc,true)); //melempar player ke lobby
                    else if (!p.isConnected()){
                        p.reconnect(sc);
                        out.println(referalLobby.playerLocation(playerName));
                    }else
                        handle(sc);
                }else queue.add(sc);
                
                if (noAction) sleep(10);
                
            } catch (InterruptedException ex) {
                Logger.getLogger(FrontOffice.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FrontOffice.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
