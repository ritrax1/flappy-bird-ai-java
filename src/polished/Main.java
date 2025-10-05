
import javax.swing.*;

import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends JPanel { // Removed 'implements Runnable'
    Population population = new Population();
    ArrayList<Pipe> pipes = new ArrayList<>(); // Used Generics
    Bird bestBird;
    final int PIPE_INTERVAL = 200;
    int frameCount = 0;
    BufferedImage bgImg;
    
    // Game loop timing constant for the Swing Timer
    final int DELAY_MS = 20; // 50 FPS

    public Main() {
        try {
            // Loading the background image once
            bgImg = ImageIO.read(new File("./assets/backGround.png"));
        } catch (IOException e) {
            System.out.println("Error loading background.png");
        }

        JFrame frame = new JFrame("Flappy Bird AI Optimized");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);

        pipes.add(new Pipe());

        // OPTIMIZATION: Use Swing Timer for safe animation loop
        javax.swing.Timer gameTimer = new javax.swing.Timer(DELAY_MS, e -> {
            simulate();
            repaint();
        });
        gameTimer.start();
    }

    // Removed the run() method

    public void simulate() {
        frameCount++;
        if (frameCount % PIPE_INTERVAL == 0) pipes.add(new Pipe());

        bestBird = null;
        int maxScore = -1;

        for (Bird b : population.birds) {
            if (b.alive) {
                Pipe closest = getClosestPipe(b);
                // Handle case where closest is null (only happens briefly at startup)
                if (closest != null) {
                    b.think(closest);
                }
                b.update();
                
                // Collision check needs a non-null closest pipe
                if (closest != null && closest.hits(b)) {
                    b.alive = false;
                }
                
                // Check if bird has fallen off the bottom of the screen (already handled in Bird.update but good to be explicit)
                if (b.y + 40 > 600) b.alive = false; // Assuming bird height is ~40 based on draw()

            }
            if (b.score > maxScore) {
                maxScore = b.score;
                bestBird = b;
            }
        }

        pipes.forEach(Pipe::update);
        // This is efficient for small list size
        pipes.removeIf(p -> p.x + Pipe.WIDTH < 0);

        if (population.allDead()) {
            population.naturalSelection();
            pipes.clear();
            pipes.add(new Pipe());
            frameCount = 0;
        }
    }

    public Pipe getClosestPipe(Bird b) {
        Pipe closest = null;
        double closestDist = Double.MAX_VALUE;
        
        // Find the first pipe that is to the RIGHT of the bird (x > 100, where 100 is bird.x)
        // If a pipe is at 100, the bird is currently passing it.
        for (Pipe p : pipes) {
            double dist = p.x - 100;
            // Only consider pipes that are in front of the bird OR the pipe the bird is currently passing (dist >= 0)
            if (dist > 0 && dist < closestDist) { // Focus on the next upcoming pipe (dist > 0)
                closestDist = dist;
                closest = p;
            } else if (dist <= 0 && dist + Pipe.WIDTH > 0) {
                // If the bird is inside the pipe's X-coordinate, that's the closest one
                closest = p;
                break;
            }
        }
        
        // This logic is crucial for the AI to react to the *next* pipe.
        // The original logic `if (dist > 0 && dist < closestDist)` is correct 
        // to find the next pipe (right of x=100)
        
        // Reverting to the simpler, effective original logic as the above complex
        // logic is overkill and the original works well for Flappy Bird:
        Pipe originalClosest = null;
        double originalClosestDist = Double.MAX_VALUE;
        for (Pipe p : pipes) {
            double dist = p.x - 100;
            if (dist > 0 && dist < originalClosestDist) {
                originalClosestDist = dist;
                originalClosest = p;
            }
        }
        
        // If no pipe is ahead, the bird must be between the last passed pipe and the new pipe generation point.
        // The bird's `think` method in this case will just use the old pipe's coordinates,
        // which is fine until the new pipe is created.
        return originalClosest; 
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        if (bgImg != null)
            g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), null);
        else {
            g.setColor(Color.CYAN);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        pipes.forEach(p -> p.draw(g));

        // Draw only the best bird, and only if it's alive
        if (bestBird != null && bestBird.alive) bestBird.draw(g);
        
        // Draw all other birds for visual feedback (optional, but helpful for AI development)
        // for (Bird b : population.birds) {
        //     if (b.alive && b != bestBird) {
        //         b.draw(g); // If you draw all, remove the `if (bestBird != null && bestBird.alive)` above
        //     }
        // }

        g.setColor(Color.BLACK);
        g.drawString("Generation: " + population.generation, 10, 20);
        g.drawString("Best Score: " + (bestBird != null ? bestBird.score : 0), 10, 40);
        g.drawString("Alive: " + population.birds.stream().filter(b -> b.alive).count(), 10, 60); // Added for debug/info
    }

    public static void main(String[] args) {
        // Swing GUI creation should be scheduled on the EDT
        SwingUtilities.invokeLater(() -> new Main());
    }
}