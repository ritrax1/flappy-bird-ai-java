import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Main extends JPanel implements ActionListener {
    Population population = new Population();
    ArrayList<Pipe> pipes = new ArrayList<>();
    Timer timer = new Timer(20, this);

    public Main() {
        JFrame frame = new JFrame("Flappy Bird AI");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);

        pipes.add(new Pipe());
        timer.start();
    }

    public void actionPerformed(ActionEvent e) {
        simulate();
        repaint();
    }

    public void simulate() {
        for (Bird b : population.birds) {
            if (b.alive) {
                Pipe closest = getClosestPipe(b);
                b.think(closest);
                b.update();
                if (closest.hits(b)) b.alive = false;
            }
        }

        pipes.forEach(Pipe::update);
        if (pipes.get(pipes.size()-1).x < 500) pipes.add(new Pipe());
        pipes.removeIf(p -> p.x + Pipe.WIDTH < 0);

        if (population.allDead()) {
            population.naturalSelection();
            pipes.clear();
            pipes.add(new Pipe());
        }
    }

    public Pipe getClosestPipe(Bird b) {
        Pipe closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Pipe p : pipes) {
            double dist = p.x - 100;
            if (dist > 0 && dist < closestDist) {
                closestDist = dist;
                closest = p;
            }
        }
        return closest;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        pipes.forEach(p -> p.draw(g));
        population.birds.forEach(b -> {
            if (b.alive) b.draw(g);
        });

        g.setColor(Color.white);
        g.drawString("Generation: " + population.generation, 10, 20);
        g.drawString("Alive: " + population.birds.stream().filter(b -> b.alive).count(), 10, 40); // Added for debug/info
    }

    public static void main(String[] args) {
        new Main();
    }
}
