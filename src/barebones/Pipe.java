import java.awt.*;
import java.util.Random;

public class Pipe {
    public double x;
    public double top, bottom;
    public static final int WIDTH = 50;
    public static final double SPEED = 3;
    private static final double GAP = 150;

    public Pipe() {
        x = 800;
        Random rand = new Random();
        top = rand.nextInt(300) + 50;
        bottom = top + GAP;
    }

    public void update() {
        x -= SPEED;
    }

    public boolean hits(Bird b) {
        if (b.y < top || b.y > bottom) {
            if (x < 120 && x + WIDTH > 100) return true;
        }
        return false;
    }

    public void draw(Graphics g) {
        g.setColor(Color.gray);
        g.fillRect((int)x, 0, WIDTH, (int)top);
        g.fillRect((int)x, (int)bottom, WIDTH, 600 - (int)bottom);
    }
}
