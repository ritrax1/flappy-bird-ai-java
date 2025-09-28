import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Bird");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(boardWidth, boardHeight);
        frame.setResizable(false);
        frame.setVisible(true);

        FlappyBird game = new FlappyBird();
        frame.add(game);
        frame.pack();
        game.requestFocus();
        frame.setVisible(true);
    }
}
