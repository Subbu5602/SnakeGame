import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;


public class GamePanel extends JPanel implements ActionListener {
    // Constants defining the game screen size and unit size for snake and apple.
    static final int SCREEN_WIDTH = 1300;
    static final int SCREEN_HEIGHT = 750;
    static final int UNIT_SIZE = 50;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 175; // Delay in milliseconds for the timer.

    // Arrays to hold the x and y coordinates of the snake body parts.
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];

    int bodyParts = 6; // Initial size of the snake.
    int applesEaten; // Counter for the number of apples eaten by the snake.
    int appleX; // X-coordinate of the apple.
    int appleY; // Y-coordinate of the apple.
    char direction = 'R'; // Initial direction of the snake (right).
    boolean running = false; // Flag to indicate if the game is running.
    Timer timer; // Timer to control the game loop.
    Random random; // Random object to generate random apple positions.

    // Constructor to set up the game panel.
    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK); // Sets the background color of the game panel.
        this.setFocusable(true); //focusable so it can receive keyboard functions
        this.addKeyListener(new MyKeyAdapter()); // Adds a key listener for user input.
        this.requestFocusInWindow(); // Requests focus for key events.
        startGame(); // Initializes the game.
    }

    // Method to start the game.
    public void startGame() {
        newApple(); // Places the first apple on the screen.
        running = true; // Sets the game status to running.
        timer = new Timer(DELAY, this); // Creates a timer with the specified delay of 175ms, thus game starts after 175 ms
        timer.start(); // Starts the timer.
    }

    // Paint method to draw the components on the game panel.
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Calls the superclass's paintComponent method.
        draw(g); // Calls the draw method to render the game elements.
    }

    // Method to draw the game elements (snake and apple).
    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.red); // Sets the color for the apple.
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE); // Draws the apple.

            // Loop to draw the snake's body.
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) { // Draw the snake's head.
                    g.setColor(new Color(0,255,0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE); // Draws a filled square at the position (x[i], y[i]) with width and height equal to UNIT_SIZE
                } else { // Draw the rest of the snake's body.
                    g.setColor(new Color(105, 180, 0)); // Slightly different green color for body.
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            // Display the score at the top of the screen.
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g); // If not running, display game over screen.
        }
    }

    // Method to generate a new apple at a random position.
    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    // Method to move the snake by shifting each body part to the position of the preceding part.
    public void move() {
        //illusion of movement by moving say last portion of snake to position of second last portion of snake
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1]; // Shift x-coordinate.
            y[i] = y[i - 1]; // Shift y-coordinate.
        }

        // Update the head position based on the current direction.
        switch (direction) {
            case 'U': // Up
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D': // Down
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L': // Left
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R': // Right
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    // Method to check if the snake's head has eaten an apple.
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++; // Increase the size of the snake.
            applesEaten++; // Increment the apple count.
            newApple(); // Generate a new apple.
        }
    }

    // Method to check for collisions with itself or the game borders.
    public void checkCollisions() {
        // Check for collisions with the snake's body.
        for (int i = bodyParts; i > 0; i--) {
            // head component touches any body component of the snake
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false; // End the game if the head collides with its body.
            }
        }

        // Check for collisions with the borders.
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false; // End the game if the head goes out of bounds.
        }

        if (!running) {
            timer.stop(); // Stop the timer when the game ends.
        }
    }

    // Method to display the game-over screen and score.
    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());

        // Display "Game Over" text.
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    // Method that is called by the timer at regular intervals.
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move(); // Move the snake.
            checkApple(); // Check if the snake eats an apple.
            checkCollisions(); // Check for collisions.
        }
        repaint(); // Redraw the game panel for any updation in appearance, eg snake moves, snake gets bigger, apple moves etc
    }

    // Inner class to handle key events for controlling the snake.
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') { // Prevent the snake from moving directly into itself, so like moving right, pressing left means going into itself, we prevent that
                        direction = 'L'; // Change direction to left.
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') { // Prevent the snake from moving directly into itself.
                        direction = 'R'; // Change direction to right.
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') { // Prevent the snake from moving directly into itself.
                        direction = 'U'; // Change direction to up.
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') { // Prevent the snake from moving directly into itself.
                        direction = 'D'; // Change direction to down.
                    }
                    break;
            }
        }
    }
}
