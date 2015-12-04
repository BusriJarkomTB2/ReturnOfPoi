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


public class Display extends JPanel implements MouseListener {
//Program Display Game Gomoku
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

// Konstanta untuk mendefinisikan grid dan size
    
    public static final int MARGIN = 24;
    public static final int GRID_ROWS = 20;
    public static final int PIECE_DIAMETER = 22;
    public static final int END_MARGIN = MARGIN * GRID_ROWS;

    private static final int WINDOW_SIZE = END_MARGIN + MARGIN; //untuk window
    private static final int PLAYERLIST_WIDTH = 200;

   
// Inisialisasi variable
     
    private Point newLocation;
    private Signal newLocationSignal = new Signal();
    private int [][] playerTable;
    private Color [] playerColor;
    private String [] playerName;
    private String [] playerStatus;

//fungsi untuk memberitahu agar user bermain pada gilirannya
    public int[] askMove() throws InterruptedException {
        whoseMoveLabel = "YOUR MOVE";
        this.repaint();
        newLocationSignal.stopAndWait();
        whoseMoveLabel = "wait";
        return rowcol(newLocation);
    }

// Membuat Display antara "Move" atau "Wait"
    private String whoseMoveLabel = "wait";
    public void drawWhoseMove(Graphics g){
        g.drawString(whoseMoveLabel,WINDOW_SIZE/2,MARGIN);
    }

// Membuat Display List Player yang ada tersedia
    public void drawPlayerList(Graphics g){
        final int ROWHEIGHT = MARGIN;
        for (int i=0;i<playerName.length;i++){
            g.drawString(playerName[i]+" "+playerStatus[i],WINDOW_SIZE+6+ROWHEIGHT, (int) (ROWHEIGHT*(i+1.5)));
            g.setColor(playerColor[i]);
            g.fillOval(WINDOW_SIZE + 6, ROWHEIGHT * (i + 1), ROWHEIGHT, ROWHEIGHT);
            g.setColor(Color.BLACK);
            g.drawOval(WINDOW_SIZE + 6, ROWHEIGHT * (i + 1), ROWHEIGHT, ROWHEIGHT);
        }
    }

// Display untuk meng-update player
    public void updatePlayer (String [] playerName, String [] playerStatus){
        this.playerName = playerName;
        this.playerStatus = playerStatus;

        this.repaint();
    }

// Display untuk meng-update Table Gomoku
    public void updateTable(int [][] Table){
        this.playerTable = Table;
        this.repaint();
    }

    JFrame f;
// Konstruktor untuk membuat laman awal dan memulai permainan
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
        f.setSize(WINDOW_SIZE + 6 + PLAYERLIST_WIDTH, WINDOW_SIZE + 28);
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
     *  Update display setiap turn atau setelah menerima input
     *
     * @param  g Graphics object to draw
     */
    public void paint(Graphics g) {
        super.paint(g);
        drawGrid(g);
        drawAllCircles(g);
        drawWhoseMove(g);
        drawWinningCircles(g);
        drawPlayerList(g);
    }

    /**
     * Menggambar grid 20x20
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

// Menggambar cicle pada player yang menang
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