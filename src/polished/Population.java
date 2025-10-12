import java.util.ArrayList;
import java.io.*;

public class Population {
    public ArrayList<Bird> birds;
    private int popSize = 1000;
    public int generation = 1;

    public Population() {
        birds = new ArrayList<>();
        for (int i = 0; i < popSize; i++) birds.add(new Bird());
    }

    public void naturalSelection() {
        // Calculate metrics from the just-finished generation
        double fitnessSum = 0;
        int maxScore = 0;
        
        for (Bird b : birds) {
            fitnessSum += b.score;
            if (b.score > maxScore) maxScore = b.score;
        }
        double avgFitness = fitnessSum / popSize;

        // --- NEW: Log metrics to Database before selection ---
        DatabaseManager.logGenerationMetrics(generation, maxScore, avgFitness);
        // ----------------------------------------------------

        ArrayList<Bird> newBirds = new ArrayList<>();
        // fitnessSum is already calculated above
        
        for (int i = 0; i < popSize; i++) {
            Bird parentA = selectParent(fitnessSum);
            Bird parentB = selectParent(fitnessSum);
            Bird child = crossover(parentA, parentB);
            child.brain.mutate(0.1);
            newBirds.add(child);
        }
        birds = newBirds;
        generation++;
    }

    private Bird selectParent(double fitnessSum) {
        double rand = Math.random() * fitnessSum;
        double runningSum = 0;
        for (Bird b : birds) {
            runningSum += b.score;
            if (runningSum > rand) return b;
        }
        return birds.get(0);
    }

    private Bird crossover(Bird a, Bird b) {
        Bird child = new Bird();
        child.brain = a.brain.crossover(b.brain);
        return child;
    }

    public boolean allDead() {
        for (Bird b : birds) if (b.alive) return false;
        return true;
    }
    
    // Helper to find the best bird from the *just finished* generation
    public Bird getBestBird() {
        Bird best = null;
        int maxScore = -1;
        for (Bird b : birds) {
            if (b.score > maxScore) { 
                maxScore = b.score;
                best = b;
            }
        }
        return best;
    }

    // Method to save the best bird's neural network (genome)
    public void saveBestGenome(String filename) {
        Bird bestBird = getBestBird();
        if (bestBird == null) return;

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(bestBird.brain);
            System.out.println("Saved best genome (Gen " + generation + ") to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving best genome: " + e.getMessage());
        }
    }
}