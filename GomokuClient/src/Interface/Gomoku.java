package Interface;

import javax.swing.JFrame;

public class Gomoku {
    private static final int WINDOW_SIZE = Display.END_MARGIN + Display.MARGIN;

    public Gomoku() {
        JFrame f = new JFrame();
        Display d = new Display();

        f.add(d);
        f.setSize(WINDOW_SIZE + 6, WINDOW_SIZE + 28);
        f.setResizable(false);
        f.setTitle("Gomoku");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}