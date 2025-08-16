import javax.swing.JPanel;
import javax.swing.Timer; 
import java.awt.*; 
import java.awt.event.*; 

public class Gameplay extends JPanel implements KeyListener, ActionListener { 
    private boolean play = false; 
    private int score = 0; 
    private int totalBricks = 21;   
    private Timer timer; 
    private int delay = 8; 
    private int playerX = 310; 
    private int ballPosX = 120; 
    private int ballPosY = 350; 


    // decide the speed and direction of the ball’s
    // movement on the screen (up,down,left and right)

    private int ballXDir = - 1; // 
    private int ballYDir = - 2; 

    private MapGenerator map; // instance to manage bricks

    public Gameplay() {
        map = new MapGenerator(3, 7); // 
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    // using paintComponent for smooth drawing // method for drawing; it prevents flickering.
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // for background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        // for drawing bricks
        map.draw((Graphics2D) g);

        // for drawing paddle
        g.setColor(Color.green);
        g.fillRect(playerX, 550, 100, 8);

        // balls
        g.setColor(Color.yellow);
        g.fillOval(ballPosX, ballPosY, 20, 20);

        // for score display
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("Score: " + score, 570, 30);

        // for borders
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(691, 0, 3, 592);

        // for Game Over
        if (ballPosY > 570) {
            play = false;
            ballXDir = ballYDir = 0;
            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over, Score: " + score, 200, 300);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
        }

        // for winning condition
        if (totalBricks <= 0) {
            play = false;
            ballXDir = ballYDir = 0;
            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You Won! Score: " + score, 200, 300);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 350);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {
            // for paddle collision
            if (new Rectangle(ballPosX, ballPosY, 20, 20)
                    .intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballYDir = -ballYDir;
            }

            A:
            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int bw = map.brickWidth;
                        int bh = map.brickHeight;

                        Rectangle brickRect = new Rectangle(brickX, brickY, bw, bh);
                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);

                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 10;

                            if (ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + bw) {
                                ballXDir = -ballXDir;
                            } else {
                                ballYDir = -ballYDir;
                            }
                            break A; // break the loops or wont return.
                        }
                    }
                }
            }

            ballPosX += ballXDir;
            ballPosY += ballYDir;

            // for wall collisions
            if (ballPosX < 0) ballXDir = -ballXDir;
            if (ballPosY < 0) ballYDir = -ballYDir;
            if (ballPosX > 670) ballXDir = -ballXDir;
        }
        repaint();
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) playerX = 600;
            else moveRight();
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX <= 10) playerX = 10;
            else moveLeft();
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER && !play) {
            if (ballPosY > 570 || totalBricks <= 0) { // restart only if game ended
                resetGame();
            }
        }
    }

    private void moveRight() {
        play = true;
        playerX += 20;
    }

    private void moveLeft() {
        play = true;
        playerX -= 20;
    }

    private void resetGame() {
        play = true;
        ballPosX = 120;
        ballPosY = 350;
        ballXDir = -1;
        ballYDir = -2;
        playerX = 310;
        score = 0;
        totalBricks = 21;
        map = new MapGenerator(3, 7);
        repaint();
    }
}
