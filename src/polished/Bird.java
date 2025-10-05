import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

public class Bird {
    public double y, velocity;
    public boolean alive;
    public int score;
    public NeuralNetwork brain;

    private static final double GRAVITY = 0.6;
    private static final double LIFT = -8;
    
    // OPTIMIZATION: Use static field for image to load it only once
    private static BufferedImage BIRD_IMAGE;
    
    // Static block to load image only once
    static {
        
        try {
            BIRD_IMAGE = ImageIO.read(new File("./assets/bird.png"));
        } catch (IOException e) {
            System.out.println("Error loading bird.png");
        }
    }

    public Bird() {
        y = 300;
        velocity = 0;
        alive = true;
        score = 0;
        brain = new NeuralNetwork(5, 8, 1); 
    }

    public void update() {
        velocity += GRAVITY;
        y += velocity;

        // NEW: Check for ceiling collision and set alive = false
        if (y <= 0) {
            y = 0; // Keep the bird visible at the top edge for one frame
            alive = false;
        }
        
        // Check for ground collision (assuming 600 is the bottom edge)
        if (y + 40 > 600) alive = false; // Assuming bird height is ~40
        
        score++;
    }

    public void flap() {
        velocity = LIFT;
    }

    public void think(Pipe pipe) {
        double[] inputs = new double[5];
        inputs[0] = y / 600.0;
        
        // Velocity clamping for more robust normalization
        double max_vel = 15.0; // A reasonable max absolute velocity
        inputs[1] = Math.min(Math.max(velocity, -max_vel), max_vel) / max_vel;
        
        inputs[2] = pipe.x / 800.0;
        inputs[3] = pipe.top / 600.0;
        inputs[4] = pipe.bottom / 600.0;

        double output = brain.feedForward(inputs)[0];
        if (output > 0.5) flap();
    }

    public void draw(Graphics g) {
        // Use the static image field
        if (BIRD_IMAGE != null)
            g.drawImage(BIRD_IMAGE, 100, (int)y, 40, 40, null); // Bird size is 40x40
        else {
            g.setColor(Color.RED);
            g.fillOval(100, (int)y, 20, 20);
        }
    }
}