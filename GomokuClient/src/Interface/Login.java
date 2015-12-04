package Interface;

import sun.misc.Regexp;

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
            public boolean nameValid(String txt){
                //validasi: tidak boleh ada whitespace. word character aja kali ya dan tidak boleh kosong
                return txt.matches("\\w+") && !txt.isEmpty();
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameValid(getUsernameField().getText()))
                    buttonPressed.tellToGo();
                else
                JOptionPane.showMessageDialog(null, "Invalid input: only alphanumeric characters and '_' are allowed", "Gomoku",
                        JOptionPane.PLAIN_MESSAGE);

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
