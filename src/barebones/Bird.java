import java.awt.*;

public class Bird {
    public double y, velocity;
    public boolean alive;
    public int score;
    public NeuralNetwork brain;

    private static final double GRAVITY = 0.6;
    private static final double LIFT = -10;

    public Bird() {
        y = 300;
        velocity = 0;
        alive = true;
        score = 0;
        brain = new NeuralNetwork(5, 8, 1); // 5 inputs, 8 hidden, 1 output
    }

    public void update() {
        velocity += GRAVITY;
        y += velocity;

        if (y > 600) alive = false;
        if (y < 0) y = 0;

        score++;
    }

    public void flap() {
        velocity = LIFT;
    }

    public void think(Pipe pipe) {
        double[] inputs = new double[5];
        inputs[0] = y / 600.0;
        inputs[1] = velocity / 10.0;
        inputs[2] = pipe.x / 800.0;
        inputs[3] = pipe.top / 600.0;
        inputs[4] = pipe.bottom / 600.0;

        double output = brain.feedForward(inputs)[0];
        if (output > 0.5) flap();
    }

    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.fillOval(100, (int)y, 20, 20);
    }
}
