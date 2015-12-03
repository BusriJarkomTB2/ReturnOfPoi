package Interface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Ginanjar on 12/3/2015.
 */
public class RoomWaiting {
    private JPanel RoomWaitingForm;
    private JList listPlayer;
    private JButton startButton;
    private JButton leaveButton;
    private JTextPane notificationTextPane;
    private Lobby lobby;
    private Gomoku gomoku;
    private JFrame frame;

    String userInput;
    Signal userInputSignal = new Signal();

    public String askUserInput() throws InterruptedException {
        userInputSignal.stopAndWait();
        return userInput;
    }

    public RoomWaiting() {
        frame = new JFrame("RoomWaiting");
        frame.setContentPane(RoomWaitingForm);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userInput="LEAVE";
                userInputSignal.tellToGo();
            }
        });
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userInput="START";
                userInputSignal.tellToGo();
            }
        });
    }


    public void hide(){
        frame.setVisible(false);
    }

    public void show(){
        frame.setVisible(true);
    }

    public JPanel getRoomWaitingForm() {
        return RoomWaitingForm;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getLeaveButton() {
        return leaveButton;
    }

    public void updatePlayerList(String [] player){
        String text = "";
        for (int i=0;i<player.length;i++){
            text+=player[i];
        }
        notificationTextPane.setText(text);
    }

    public void giveAlert(String text){
        notificationTextPane.setText(text);
    }
}
