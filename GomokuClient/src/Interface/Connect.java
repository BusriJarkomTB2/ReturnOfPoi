package Interface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.*;
/**
 * Created by Ginanjar on 12/2/2015.
 */
public class Connect {
    private JTextField serverAddress;
    private JTextField serverHost;
    private JButton connectButton;
    private JTextField serverPort;
    private JPanel ConnectForm;

    private Signal buttonPressed = new Signal();
    /**
     * menunggu user menekan connect
     */
    public String[] askConnectArgs() throws InterruptedException {
        String [] retval = new String[2];
        buttonPressed.stopAndWait();
        try{
            retval[0]=getserverAddress();
            retval[1]=getserverPort();
            return retval;
        }catch(NumberFormatException e){
            return null;
        }
    }



    public String getserverAddress()
    {
        return serverAddress.getText();
    }

    public String getserverHost()
    {
        return serverHost.getText();
    }

    public String getserverPort()
    {
        return serverPort.getText();
    }


    public void hide(){
        frame.setVisible(false);
    }

    public void show(){
        frame.setVisible(true);
    }

    private JFrame frame;
    public Connect() {
        frame = new JFrame("Connect");
        frame.setContentPane(ConnectForm);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(400,300);
        frame.setResizable(false);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonPressed.tellToGo();
            }
        });
    }
}
