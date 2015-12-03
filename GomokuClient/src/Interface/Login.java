package Interface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Ginanjar on 12/2/2015.
 */



public class Login {
    private JPanel LoginForm;
    private JTextField usernameField;
    private JButton LogInBtn;
    private Lobby lobby;
    private JFrame frame;
    public Login() {
        frame = new JFrame("Login");
        frame.setContentPane(LoginForm);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(400,150);
        frame.setResizable(false);
        LogInBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonPressed.tellToGo();
            }
        });
    }

    private Signal buttonPressed = new Signal();
    /**
     * menunggu user menekan connect
     */
    public String askName() throws InterruptedException {
        buttonPressed.stopAndWait();
        return getUsernameField().getText();
    }

    public void hide(){
        frame.setVisible(false);
    }

    public void show(){
        frame.setVisible(true);
    }

    public JPanel getLoginForm() {
        return LoginForm;
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JButton getLogInBtn() {
        return LogInBtn;
    }
}
