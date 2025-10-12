import java.io.Serializable;
import java.util.Random;
import java.io.*;

// Make the class implement Serializable
public class NeuralNetwork implements Serializable {
    // Required for serialization
    private static final long serialVersionUID = 1L; 
    
    private int inputNodes, hiddenNodes, outputNodes;
    private double[][] weights_ih, weights_ho;
    private double[] bias_h, bias_o;
    // NOTE: Random object cannot be static if it is to be serialized, 
    // but we can mark it as transient since we don't need to save its state.
    private transient Random rand = new Random(); 
    
    public NeuralNetwork(int in, int hidden, int out) {
        inputNodes = in;
        hiddenNodes = hidden;
        outputNodes = out;

        weights_ih = new double[hidden][in];
        weights_ho = new double[out][hidden];
        bias_h = new double[hidden];
        bias_o = new double[out];

        for (int i = 0; i < hidden; i++) {
            bias_h[i] = rand.nextDouble() * 2 - 1;
            for (int j = 0; j < in; j++) weights_ih[i][j] = rand.nextDouble() * 2 - 1;
        }
        for (int i = 0; i < out; i++) {
            bias_o[i] = rand.nextDouble() * 2 - 1;
            for (int j = 0; j < hidden; j++) weights_ho[i][j] = rand.nextDouble() * 2 - 1;
        }
    }

    // Since 'rand' is transient, we must re-initialize it after deserialization
    private void readObject(java.io.ObjectInputStream in) 
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        rand = new Random();
    }

    public double[] feedForward(double[] input) {
        double[] hidden = new double[hiddenNodes];
        for (int i = 0; i < hiddenNodes; i++) {
            double sum = 0;
            for (int j = 0; j < inputNodes; j++) sum += weights_ih[i][j] * input[j];
            hidden[i] = sigmoid(sum + bias_h[i]);
        }

        double[] output = new double[outputNodes];
        for (int i = 0; i < outputNodes; i++) {
            double sum = 0;
            for (int j = 0; j < hiddenNodes; j++) sum += weights_ho[i][j] * hidden[j];
            output[i] = sigmoid(sum + bias_o[i]);
        }
        return output;
    }

    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public void mutate(double rate) {
        // Ensure rand is initialized even if the object was deserialized without calling readObject explicitly
        if (rand == null) rand = new Random(); 
        
        for (int i = 0; i < hiddenNodes; i++) {
            if (rand.nextDouble() < rate) bias_h[i] += rand.nextGaussian() * 0.1;
            for (int j = 0; j < inputNodes; j++)
                if (rand.nextDouble() < rate) weights_ih[i][j] += rand.nextGaussian() * 0.1;
        }
        for (int i = 0; i < outputNodes; i++) {
            if (rand.nextDouble() < rate) bias_o[i] += rand.nextGaussian() * 0.1;
            for (int j = 0; j < hiddenNodes; j++)
                if (rand.nextDouble() < rate) weights_ho[i][j] += rand.nextGaussian() * 0.1;
        }
    }

    public NeuralNetwork crossover(NeuralNetwork partner) {
        NeuralNetwork child = new NeuralNetwork(inputNodes, hiddenNodes, outputNodes);
        
        // Ensure child's rand is initialized
        if (child.rand == null) child.rand = new Random();
        
        for (int i = 0; i < hiddenNodes; i++) {
            child.bias_h[i] = child.rand.nextBoolean() ? bias_h[i] : partner.bias_h[i];
            for (int j = 0; j < inputNodes; j++)
                child.weights_ih[i][j] = child.rand.nextBoolean() ? weights_ih[i][j] : partner.weights_ih[i][j];
        }
        for (int i = 0; i < outputNodes; i++) {
            child.bias_o[i] = child.rand.nextBoolean() ? bias_o[i] : partner.bias_o[i];
            for (int j = 0; j < hiddenNodes; j++)
                child.weights_ho[i][j] = child.rand.nextBoolean() ? weights_ho[i][j] : partner.weights_ho[i][j];
        }
        return child;
    }
}