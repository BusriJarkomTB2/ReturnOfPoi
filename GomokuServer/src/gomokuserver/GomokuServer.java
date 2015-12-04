/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomokuserver;

import java.io.IOException;
import static java.lang.Thread.interrupted;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nim_13512501
 */
public class GomokuServer {

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        if (args.length!=1){
            System.out.println("Run with listen portno as argument");
            return;
        }
        int portno = Integer.parseInt(args[0]);
        ServerSocket sc;
        try{
            System.out.print("Creating socket bound to " + portno + " ... ");
            sc = new ServerSocket(portno);
            
            System.out.println("DONE");
        }catch(IOException ex){
            System.out.println(ex.getMessage());
            return;
        }
        FrontOffice fo = new FrontOffice();
        Lobby lb = new Lobby();
        fo.setReferalLobby(lb);
        fo.start();
        lb.start();
        while (!interrupted()){
            try {
                Socket playersocket = sc.accept();
                playersocket.setKeepAlive(true);
                System.out.println("Received client from " + playersocket.getInetAddress());
                fo.handle(playersocket);
            } catch (IOException ex) {
                Logger.getLogger(GomokuServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        fo.interrupt();
        lb.interrupt();
        fo.join();
        lb.join();
    }

    GomokuServer(String text) throws InterruptedException {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String[] argstext = new String[1];
        argstext[0] = text;
        main(argstext);
    }
    
}
