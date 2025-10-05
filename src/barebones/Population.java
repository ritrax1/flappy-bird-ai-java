import java.util.ArrayList;

public class Population {
    public ArrayList<Bird> birds;
    private int popSize = 1000;
    public int generation = 1;

    public Population() {
        birds = new ArrayList<>();
        for (int i = 0; i < popSize; i++) birds.add(new Bird());
    }

    public void naturalSelection() {
        ArrayList<Bird> newBirds = new ArrayList<>();
        double fitnessSum = 0;
        for (Bird b : birds) fitnessSum += b.score;

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
}
