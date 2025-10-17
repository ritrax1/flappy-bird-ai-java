import javax.swing.*;

import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Main extends JPanel { 
    Population population = new Population();
    ArrayList<Pipe> pipes = new ArrayList<>(); 
    Bird bestBird;
    final int PIPE_INTERVAL = 200;
    int frameCount = 0;
    BufferedImage bgImg;
    
    final int DELAY_MS = 20; // 50 FPS

    public Main() {
       try {
            bgImg = ImageIO.read(new File("./assets/backGround.png"));
        } catch (IOException e) {
            System.out.println("Error loading background.png");
        }
        
        // --- NEW: Initialize Database ---
        DatabaseManager.initializeDatabase();

        JFrame frame = new JFrame("Flappy Bird AI");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);

        pipes.add(new Pipe());

        javax.swing.Timer gameTimer = new javax.swing.Timer(DELAY_MS, e -> {
            simulate();
            repaint();
        });
        gameTimer.start();
    }

    public void simulate() {
        frameCount++;
        if (frameCount % PIPE_INTERVAL == 0) pipes.add(new Pipe());

        bestBird = null;
        int maxScore = -1;

        for (Bird b : population.birds) {
            if (b.alive) {
                Pipe closest = getClosestPipe(b);
                if (closest != null) {
                    b.think(closest);
                }
                b.update();
                
                if (closest != null && closest.hits(b)) {
                    b.alive = false;
                }
                
                if (b.y + 40 > 600) b.alive = false; 

            }
            if (b.score > maxScore) {
                maxScore = b.score;
                bestBird = b;
            }
        }

        pipes.forEach(Pipe::update);
        pipes.removeIf(p -> p.x + Pipe.WIDTH < 0);

        if (population.allDead()) {
            
            // Save the best genome every 10 generations
            if (population.generation % 10 == 0) { 
                population.saveBestGenome("best_genome_gen_" + population.generation + ".bin");
            }
            
            population.naturalSelection();
            pipes.clear();
            pipes.add(new Pipe());
            frameCount = 0;
        }
    }

    public Pipe getClosestPipe(Bird b) {
        Pipe originalClosest = null;
        double originalClosestDist = Double.MAX_VALUE;
        for (Pipe p : pipes) {
            double dist = p.x - 100;
            if (dist > 0 && dist < originalClosestDist) {
                originalClosestDist = dist;
                originalClosest = p;
            }
        }
        return originalClosest; 
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bgImg != null)
            g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), null);
        else {
            g.setColor(Color.CYAN);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        pipes.forEach(p -> p.draw(g));

        if (bestBird != null && bestBird.alive) bestBird.draw(g);
        
        for (Bird b : population.birds) {
            if (b.alive && b != bestBird) {
                b.draw(g); 
            }
        }

        g.setColor(Color.BLACK);
        g.drawString("Generation: " + population.generation, 10, 20);
        g.drawString("Best Score: " + (bestBird != null ? bestBird.score : 0), 10, 40);
        g.drawString("Alive: " + population.birds.stream().filter(b -> b.alive).count(), 10, 60); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}