package Interface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Ginanjar on 12/3/2015.
 */
public class Lobby {
    private JPanel LobbyForm;
    private JComboBox RoomChoose;
    private JButton createButton;
    private JButton joinButton;
    private JButton refreshButton;
    private JTextField createdroomnamefield;
    private RoomWaiting roomWaiting;

    private Signal userInputSignal = new Signal();
    private String userInput = null;

    public String askUserInput() throws InterruptedException {
        userInputSignal.stopAndWait();
        return userInput;
    }

    //*khusus CREATE
    //*asumsi protokol benar dan dicomply oleh server dan langsung ada permintaan setelah klik create
    public String getCreatedRoomName(){
        return createdroomnamefield.getText();
    }

    JFrame frame;
    public Lobby() {
        frame = new JFrame("Lobby");
        frame.setContentPane(LobbyForm);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setSize(400,300);
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!getCreatedRoomName().isEmpty()){
                    userInput = "CREATE";
                    userInputSignal.tellToGo();
                }
            }
        });
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int IDX =getRoomChoose().getSelectedIndex();
                if (IDX >= 0){
                    userInput = "" + roomID[IDX];
                    userInputSignal.tellToGo();
                }
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userInput = "REFRESH";
                userInputSignal.tellToGo();
            }
        });
    }

    private int [] roomID = new int [0];
    public void updateRoomList(int [] roomID, String [] roomName, String [] roomStatus){
        this.roomID = roomID.clone();
        RoomChoose.removeAllItems();

        for (int i=0;i<roomID.length;i++){
            String text = "" + roomID[i] + " " + roomName[i] + roomStatus[i];
            RoomChoose.addItem(text);
        }
    }

    public void hide(){
        frame.setVisible(false);
    }

    public void show(){
        frame.setVisible(true);
    }

    public JPanel getLobbyForm() {
        return LobbyForm;
    }

    public JComboBox getRoomChoose() {
        return RoomChoose;
    }

    public JButton getCreateButton() {
        return createButton;
    }

    public JButton getJoinButton() {
        return joinButton;
    }

}
