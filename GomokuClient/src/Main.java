import Interface.*;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * Created by Ginanjar on 12/2/2015.
 */
public class Main {
    private static Connect connect;
    private static Login login;
    private static Lobby lobby;
    private static RoomWaiting roomWaiting;
    private static Display display;

    private static Socket socketToServer;
    private static InputStream is;
    private static Scanner sc;
    private static OutputStream os;
    private static PrintStream ps;

    private static int state;

    private static final int STATE_CONNECT = 0;
    private static final int STATE_LOGIN = 1;
    private static final int STATE_LOBBY = 2;
    private static final int STATE_ROOMWAITING = 3;
    private static final int STATE_GAME = 4;
    private static final int STATE_QUIT = -1;

    public static void main(String args[]) throws InterruptedException, IOException {
        connect = new Connect();

        String [] connectArgs;

        socketToServer = null;
        boolean successConnect = false;
        do{
            connectArgs = connect.askConnectArgs();
            try {
                socketToServer = new Socket(connectArgs[0],Integer.parseInt(connectArgs[1]));
                socketToServer.setKeepAlive(true);
                successConnect = true;
                is = socketToServer.getInputStream();
                os = socketToServer.getOutputStream();
                ps = new PrintStream(os);
                sc = new Scanner(is);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
        }while (!successConnect);

        connect.hide();

        login = new Login();
        lobby = new Lobby();
        roomWaiting = new RoomWaiting();
        display = new Display();
        login.hide();
        lobby.hide();
        roomWaiting.hide();
        display._hide();

        String line = sc.nextLine();
        stateChangeMessageToStateInt(line);

        while (state!=STATE_QUIT){
            switch(state){
                case STATE_LOGIN: handleLoginComm(); break;
                case STATE_LOBBY: handleLobbyComm(); break;
                case STATE_ROOMWAITING: handleRoomWaitingComm(); break;
                case STATE_GAME: handleGameComm(); break;
                default: exit(0);
            }
        }

    }
    private static String name;

    public static void handleLoginComm() throws InterruptedException, IOException {
        login.show();
        while (state== STATE_LOGIN){
            do{
                name=login.askName();
            }while(name==null || name.isEmpty());
            ps.println(name);
            ps.flush();
            String line = sc.nextLine();
            stateChangeMessageToStateInt(line);
        }
        login.hide();
    }

    public static void handleLobbyComm() throws IOException, InterruptedException {
        lobby.show();
        while (state==STATE_LOBBY){
            String line = sc.nextLine();

            switch(line){
                case "INVALIDINPUT": break; //ignore
                case "ROOMLIST":
                    int roomnum = Integer.parseInt(sc.nextLine());
                    int [] roomID = new int[roomnum];
                    String [] roomName = new String[roomnum];
                    String [] roomStatus = new String[roomnum];
                    for (int i=0;i<roomnum;i++){
                        roomID[i]=Integer.parseInt(sc.next());
                        roomName[i]=sc.next();
                        roomStatus[i]=sc.next();
                    }
                    lobby.updateRoomList(roomID, roomName, roomStatus);
                    String curReply=lobby.askUserInput();
                    ps.println(curReply);
                    ps.flush();
                    System.out.println(curReply);
                    if (curReply=="CREATE") {
                        if (sc.nextLine().equals("NAME")) {
                            System.out.println("check");
                            ps.println(lobby.getCreatedRoomName());
                            ps.flush();
                        }
                    }

                    break;
                default: stateChangeMessageToStateInt(line);

            }

        }

        lobby.hide();

    }

    public static class WaitUserStartOrLeave extends Thread{
        private RoomWaiting roomWaiting;
        private PrintStream ps;
        public WaitUserStartOrLeave(RoomWaiting roomWaiting, PrintStream ps){
            this.roomWaiting = roomWaiting;
            this.ps = ps;
        }

        @Override
        public void run(){
            boolean cont = true;
            while (!interrupted()){
                try{
                    ps.println(roomWaiting.askUserInput());
                    ps.flush();
                }catch(InterruptedException e){
                    cont = false;
                    //do nothing. exit thread.
                }
            }
        }

    }
    public static void handleRoomWaitingComm() throws InterruptedException {
        roomWaiting.show();
        WaitUserStartOrLeave waitUserStartOrLeave = new WaitUserStartOrLeave(roomWaiting, ps);
        waitUserStartOrLeave.start();
        while (state==STATE_ROOMWAITING){
            String line = sc.nextLine();
            System.out.println("roomwait received message: " + line);
            switch(line){
                case "PLAYER":
                    int numPlayer = Integer.parseInt(sc.nextLine());
                    String [] player = new String[numPlayer]; //ini nanti diganti kalau perlu
                    for (int i=0;i<numPlayer;i++){
                        player[i]=sc.nextLine();
                    }
                    roomWaiting.updatePlayerList(player);
                    break;
                case "NOTENOUGHPLAYER":
                    roomWaiting.giveAlert(line);
                    break;
                default:stateChangeMessageToStateInt(line); break;
            }
        }
        waitUserStartOrLeave.interrupt();
        System.out.println("starting...");
        roomWaiting.hide();
    }

    public static void handleGameComm() throws InterruptedException {
        display._show();
        while (state==STATE_GAME){
            String line = sc.nextLine();
            switch(line){
                case "PLAYER":
                    int numPlayer = Integer.parseInt(sc.nextLine());
                    String [] playerName = new String[numPlayer]; //ini nanti diganti kalau perlu
                    String [] playerStatus = new String[numPlayer];
                    for (int i=0;i<numPlayer;i++){
                        int id = Integer.parseInt(sc.next());
                        playerName[id] = sc.next();
                        playerStatus[id] = sc.next();
                    }
                    display.updatePlayer(playerName, playerStatus);
                    break;
                case "TABLE":
                    int [][] Tab = new int[20][20];
                    for (int i=0;i<20;i++)
                        for (int j=0;j<20;j++){
                            Tab[i][j] = Integer.parseInt(sc.next());
                            System.out.println(Tab[i][j]);
                        }
                    display.updateTable(Tab);
                    break;
                case "MOVE":
                    int [] mv = display.askMove();
                    ps.println(""+mv[0]+" "+mv[1]);
                    break;
                case "WIN":
                    int winning = Integer.parseInt(sc.nextLine());
                    int [][] pieces = new int [5][2];
                    for (int i=0;i<5;i++){
                        pieces[i][0]=Integer.parseInt(sc.next());
                        pieces[i][1]=Integer.parseInt(sc.next());
                    }
                    display.showWinDialog(name,winning,pieces);
                    break;
                default:
                    stateChangeMessageToStateInt(line);

            }
        }
        display._hide();

    }

    public static void stateChangeMessageToStateInt(String line){
        switch(line){
            case "LOBBY": state=STATE_LOBBY;break;
            case "NAME": state=STATE_LOGIN;break;
            case "ROOM": state=STATE_ROOMWAITING;break;
            case "STARTING": state=STATE_GAME;break;
            default: break;
        }
    }
}
