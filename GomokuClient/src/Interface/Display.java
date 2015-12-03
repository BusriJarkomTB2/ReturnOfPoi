package Interface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * The Display class handles the graphics, draws the grid and pieces, and
 * updates after every move by getting input from the mouse and keyboard.
 */
public class Display extends JPanel implements MouseListener {

    public static void main(String [] args) throws InterruptedException {
        Display d = new Display();
        d._show();
        int [][] playerTable={
                {1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,-1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,-1,-1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,2,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,0,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,-1,-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}
        };
        d.updateTable(playerTable);
        d.updateTable(playerTable);

        for (int i=0;i<10;i++){
            int [] a = d.askMove();
            System.out.println(""+a[0]+" " +a[1]);
        }

        int [][] pieces = {
                {1, 1},
                {1, 2},
                {1, 3},
                {1, 4},
                {1, 5},
        };
        d.showWinDialog("wols",0,pieces);
    }

    /**
     * Constants for determining grid and piece size.
     */
    public static final int MARGIN = 24;
    public static final int GRID_ROWS = 20;
    public static final int PIECE_DIAMETER = 22;
    public static final int END_MARGIN = MARGIN * GRID_ROWS;

    private static final int WINDOW_SIZE = END_MARGIN + MARGIN; //untuk window

    /**
     * Instance variables that keep track of game play.
     */
    private Point newLocation;
    private Signal newLocationSignal = new Signal();
    private int [][] playerTable;
    private Color [] playerColor;
    private String [] playerName;
    private String [] playerStatus;

    public int[] askMove() throws InterruptedException {
        whoseMoveLabel = "YOUR MOVE";
        this.repaint();
        newLocationSignal.stopAndWait();
        whoseMoveLabel = "wait";
        return rowcol(newLocation);
    }

    private String whoseMoveLabel = "wait";

    public void drawWhoseMove(Graphics g){
        g.drawString(whoseMoveLabel,WINDOW_SIZE/2,MARGIN);
    }

    public void updatePlayer (String [] playerName, String [] playerStatus){
        this.playerName = playerName;
        this.playerStatus = playerStatus;

        //TODO tampilkan
    }

    public void updateTable(int [][] Table){
        this.playerTable = Table;
        this.repaint();
    }

    JFrame f;
    /**
     * Class constructor initalizes this to receive input from mouse click and
     * Enter key.
     */
    public Display() {
        this.addMouseListener(this);
        this.setFocusable(true);
        newLocation = new Point();
        playerTable = new int[20][20];
        playerColor = new Color[]{
                Color.BLACK, Color.WHITE, Color.RED, Color.BLUE, Color.CYAN, Color.YELLOW, Color.GREEN, Color.GRAY, Color.PINK
        };
        playerName = new String[9];
        playerStatus = new String[9];

        f = new JFrame();
        f.add(this);
        f.setSize(WINDOW_SIZE + 6, WINDOW_SIZE + 28);
        f.setResizable(false);
        f.setTitle("Gomoku");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void _hide(){
        f.setVisible(false);
    }

    public void _show(){
        f.setVisible(true);
    }

    /**
     * Update the display every turn or after receiving input.
     *
     * @param  g Graphics object to draw
     */
    public void paint(Graphics g) {
        super.paint(g);
        drawGrid(g);
        drawAllCircles(g);
        drawWhoseMove(g);
        drawWinningCircles(g);
    }

    /**
     * Draw a 20x20 black grid using class constants.
     *
     * @param  g Graphics object to draw lines
     */
    private void drawGrid(Graphics g) {
        g.setColor(Color.black);
        for (int space = 0; space < GRID_ROWS * MARGIN; space += MARGIN) {
            g.drawLine(MARGIN + space, MARGIN,
                       MARGIN + space, GRID_ROWS*MARGIN);
            g.drawLine(MARGIN, MARGIN + space,
                       GRID_ROWS*MARGIN, MARGIN + space);
        }
    }

    /**
     * Draw all pieces for all players.
     * 
     * Player 1 is white and moves first. Player 2 is black and moves second, ETC. (example)
     *
     * @param  g Graphics object to draw circles
     */
    private void drawAllCircles(Graphics g) {
        for (int i=0;i<playerTable.length;i++)
            for (int j=0;j<playerTable[i].length;j++)
                if (playerTable[i][j]!=-1)
                    drawCircle(g,point(i,j), playerColor[playerTable[i][j]]);
    }

    private static Point point(int row, int col){
        int Y = (row+1)*MARGIN;
        int X = (col+1)*MARGIN;
        return new Point(X,Y);
    }

    private static int[] rowcol(Point p){
        int [] retval = new int[2];
        retval[0]=(p.getY()/MARGIN)-1;
        retval[1]=(p.getX()/MARGIN)-1;
        return retval;
    }

    /**
     * Draw pieces at specified locations given by a list of Points.
     *
     * @param  g     Graphics object to draw circle
     * @param  piece Point where piece should be
     * @param  col   color of piece
     */
    private static void drawCircle(Graphics g, Point piece, Color col) {
        g.setColor(col);
        g.fillOval(piece.getX() - PIECE_DIAMETER/2,
                   piece.getY() - PIECE_DIAMETER/2,
                   PIECE_DIAMETER,
                   PIECE_DIAMETER);
        if (col != Color.BLACK) {
            g.setColor(Color.BLACK);  // black borders on white pieces
            g.drawOval(piece.getX() - PIECE_DIAMETER/2,
                       piece.getY()- PIECE_DIAMETER/2,
                       PIECE_DIAMETER,
                       PIECE_DIAMETER);
        }
    }


    /**
     * Set location from mouse click and make call to update display.
     * Freeze the display if the game is over.
     * 
     * @param  e mouse click input
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        newLocation = Point.convertFromPixel(e.getX(), e.getY());
        newLocationSignal.tellToGo();
    }

    /**
     * Display a dialog saying which player won the game and reset game after
     * clicking OK.
     */
    public void showWinDialog(String name, int playerID, int[][] pieces) {

        //gambar pieces
        winPieces = pieces;

        this.repaint();

        String winner = playerName[playerID];
        String notif;
        if (name.equals(winner))
            notif = "YOU WIN";
        else    notif = winner + "WINS";
            JOptionPane.showMessageDialog(null, notif, "Gomoku",
                    JOptionPane.PLAIN_MESSAGE);


    }

    private int [][] winPieces = null;

    private void drawWinningCircles(Graphics g){
        if (winPieces!=null){
            for (int i=0;i<winPieces.length;i++){
                Point piece = point(winPieces[i][0],winPieces[i][1]);

                int diameter = PIECE_DIAMETER*3/2;
                g.setColor(Color.BLACK);
                g.drawOval(piece.getX() - diameter/2,
                        piece.getY()- diameter/2,
                        diameter,
                        diameter);
            }
        }

        winPieces = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

}