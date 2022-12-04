import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import demandPrediction;

/**
 * For the baseline method, I will be using the evolutionary algorithm I created for the labs
 */
public class BaselineMethod {
    static ArrayList<String> locations; //representation rename!
    static ArrayList<ArrayList<String>> population;
    static ArrayList<Double> fitness;
    static ArrayList<ArrayList<String>> parents;
    static ArrayList<ArrayList<String>> children;
    static ArrayList<ArrayList<String>> candidates;
    static ArrayList<Double> candidateFitness;
    static ArrayList<ArrayList<String>> survivors;
    static DemandPrediction train_problem;

    public BaselineMethod() throws IOException {
        survivors = new ArrayList<>();
        train_problem = new DemandPrediction("train");

    }

    /**
     * Creates Population out of random tours
     * @param size size of population
     */
    public static void initializePopulation(int size){
        population = new ArrayList<ArrayList<String>>();

        for (int i=0; i<size;i++){ population.add(randomRoutes()); } //ATTENTION

        //return population;
    }


    /**
     * Runs fitness function on all tours of a list and puts it in a fitness list
     * @param forEval list of candidates to be evaluated
     * @param fitArray list to put the results into
     */
    public static void evaluation(ArrayList<ArrayList<String>> forEval, ArrayList<Double> fitArray){
        for (ArrayList<String> candidate: forEval){ fitArray.add(train_problem.evaluate(candidate)); } //ATTENTION
    }


    /**
     * Picks out random tours and chooses the best one to be potential parent
     * Result is in parents ArrayList
     * @param tournamentSize the size of each tournament
     * @param parentSize the final number of parents
     */
    public static void tournamentSelection(int tournamentSize, int parentSize){
        candidates = new ArrayList<>(); //candidates that will compete
        candidateFitness = new ArrayList<>();
        Random rand = new Random();
        int randNum;
        double bestFitness;
        ArrayList<String> bestCandidate;

        for(int j=0; j<parentSize;j++) {

            //fill up tournament candidate arrayList
            for (int i = 0; i < tournamentSize; i++) {
                randNum = rand.nextInt(population.size());
                candidates.add(population.get(randNum));
                candidateFitness.add(fitness.get(randNum)); //ATTENTION
            }

            // select parent out of that arrayList
            bestCandidate = candidates.get(1);
            bestFitness = candidateFitness.get(1);

            for (int i = 0; i < candidateFitness.size(); i++) {
                if (candidateFitness.get(i) < bestFitness) {
                    bestFitness = candidateFitness.get(i);
                    bestCandidate = candidates.get(i);
                }
            }
            parents.add(bestCandidate);

            //delete all entries in arrayLists
            candidates.clear();
            candidateFitness.clear();
        }
    }

    /**
     * Produces two children by Crossing over two parents
     * Adds the children into a global children array
     */
    public static void recombination(){
        ArrayList<ArrayList<String>> crossedOver = new ArrayList<>();
        ArrayList<String> child1 = new ArrayList<>();
        ArrayList<String> child2 = new ArrayList<>();
        Random rand = new Random();

        //gets two random parents
        ArrayList<String> parentOne = parents.get(rand.nextInt(parents.size()-1));
        ArrayList<String> parentTwo = parents.get(rand.nextInt(parents.size()-1));

        int cut = rand.nextInt(parentOne.size()-2);

        //first part of child one
        for (int i = 0; i<=cut; i++) {
            child1.add(parentOne.get(i));
        }

        //first part of child 2
        for (int i = 0; i<=cut; i++) {
            child2.add(parentTwo.get(i));
        }

        //second part of child one
        for (int i = cut+1; i < parentTwo.size(); i++){
            child1.add(parentTwo.get(i));
        }

        //second part of child 2
        for (int i = cut+1; i < parentOne.size(); i++){
            child2.add(parentOne.get(i));
        }

        children.add(child1);
        children.add(child2);

        //return crossedOver;
    }

    /**
     * Mutates selected tour by swapping part of it
     * @param tour sequence of cities
     */
    public static void swapMutation(ArrayList<String> tour){
        Random rand = new Random();
        int i = rand.nextInt(locations.size()-2);
        int j = rand.nextInt(i+1, locations.size()-1);

        //replace existing child with mutated child
        children.set(children.indexOf(tour), twoOptSwap(tour,i,j));
    }
    public static ArrayList<String> twoOptSwap(ArrayList<String> tour, int i, int j){
        ArrayList<String> swapped = new ArrayList<>();
        ArrayList<String> finalTour = new ArrayList<>();

        for (int x=i; x<=j;x++) {swapped.add(tour.get(x));}
        Collections.reverse(swapped);

        for (int c=0;c < i;c++){ //add every element before the swapped part
            finalTour.add(tour.get(c));
        }

        finalTour.addAll(swapped); //add swapped part

        for (int c=j+1;c < tour.size();c++){ //add every element after the swapped part
            finalTour.add(tour.get(c));
        }

        return finalTour;
    }

    /**
     * Selects survivors that make it to the next generation
     * @param survivor tour to be evaluated
     */
    public static void survivorSelection(ArrayList<String> survivor){
        //children survive, replace the parents
        if (children.contains(survivor)){ survivors.add(survivor); }
    }

    /**
     * Sets up new generation
     */
    public static void nextGenSetup(){
        //survivors are the new population
        population.clear();
        population.addAll(survivors);

        fitness.clear();
        evaluation(survivors, fitness); //evaluates new population

        //empty arrays
        parents.clear();
        children.clear();
        survivors.clear();

    }
    public static void evoAlgorithm(){
        children = new ArrayList<>();
        parents = new ArrayList<>();
        fitness = new ArrayList<>();
        survivors = new ArrayList<>();

        initializePopulation(10);
        evaluation(population, fitness);

        long start = System.currentTimeMillis(); //current system time
        long end = start + 2 * 1000L;           //seconds - 2

        while (System.currentTimeMillis() < end) {
            // picks parents
            tournamentSelection(2,100);

            int pairsOfChildren = 10; //input half the number of children required
            for(int i =1; i<=pairsOfChildren;i++){
                //produces two children
                recombination();
            }

            //mutate all offspring created
            for(int i =0; i<children.size();i++){
                swapMutation(children.get(i));
                survivorSelection(children.get(i)); //all children survive
            }

            nextGenSetup();
        }

    }
    //evolutionary alg method
    //initialise population with random candidate solutions
    //Evaluate each candidate with cost function
    //repeat until termination cond is satisfied
    //select parents
    //recombine pairs of parents
    //mutate the resulting offspring
    //evaluate new candidates
    //select individuals for next generation
    //End
    //END



}
