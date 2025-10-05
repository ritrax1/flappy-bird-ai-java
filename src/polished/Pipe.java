import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Pipe {
    public double x;
    public double top, bottom;
    public static final int WIDTH = 50;
    public static final double SPEED = 3;
    private static final double GAP = 150;

    // OPTIMIZATION: Use static fields for images to load them only once
    private static BufferedImage TOP_IMAGE;
    private static BufferedImage BOTTOM_IMAGE;
    
    // Static block to load images only once when the class is loaded
    static {
        try {
            TOP_IMAGE = ImageIO.read(new File("assets/topPipe.png"));
            BOTTOM_IMAGE = ImageIO.read(new File("assets/bottomPipe.png"));
        } catch (IOException e) {
            System.out.println("Error loading pipe images");
        }
    }

    public Pipe() {
        x = 800;
        Random rand = new Random();
        top = rand.nextInt(300) + 50;
        bottom = top + GAP;
        
        // Removed image loading from constructor
    }

    public void update() {
        x -= SPEED;
    }

    public boolean hits(Bird b) {
        // Bird collision logic (assuming bird is at x=100 and has a height/width of 40 based on draw())
        
        // 1. Check if the pipe is horizontally aligned with the bird
        // Bird X range: 100 to 140 (100 + 40)
        // Pipe X range: x to x + WIDTH (50)
        boolean x_overlap = x < 140 && x + WIDTH > 100;
        
        if (x_overlap) {
            // 2. Check vertical collision with top or bottom pipe segments
            // Bird Y range: b.y to b.y + 40
            
            // Collision with top pipe: bird bottom is above top pipe's bottom edge (b.y + 40 > top) 
            // AND bird top is above the pipe's top (b.y < top, which is always true since pipe top is at y=0)
            boolean top_collision = b.y < top; 
            
            // Collision with bottom pipe: bird top is below bottom pipe's top edge (b.y < bottom)
            // AND bird bottom is below the pipe's bottom edge (b.y + 40 > bottom, which is always true since pipe bottom is at y=600)
            boolean bottom_collision = b.y + 40 > bottom;

            return top_collision || bottom_collision;
        }
        
        // The original logic was too simple and likely inaccurate:
        // if (b.y < top || b.y > bottom) {
        //     if (x < 120 && x + WIDTH > 100) return true;
        // }
        
        // Returning a more accurate collision detection for a 40x40 bird at x=100
        return false;
    }

    public void draw(Graphics g) {
        if (TOP_IMAGE != null)
            g.drawImage(TOP_IMAGE, (int)x, 0, WIDTH, (int)top, null);
        else {
            g.setColor(Color.GREEN);
            g.fillRect((int)x, 0, WIDTH, (int)top);
        }

        if (BOTTOM_IMAGE != null)
            g.drawImage(BOTTOM_IMAGE, (int)x, (int)bottom, WIDTH, 600 - (int)bottom, null);
        else {
            g.setColor(Color.GREEN);
            g.fillRect((int)x, (int)bottom, WIDTH, 600 - (int)bottom);
        }
    }
}