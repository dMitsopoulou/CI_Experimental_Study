
import org.apache.commons.math3.distribution.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import static java.lang.Math.abs;


public class NovelMethod {
    static ArrayList<Double> randomised;
    private static ArrayList<Double> bounds;
    private static Random r;
    static ArrayList<ArrayList<Double>> population;
    static ArrayList<Double> fitness;
    static ArrayList<ArrayList<Double>> parents;
    static ArrayList<ArrayList<Double>> children;
    static ArrayList<ArrayList<Double>> candidates;
    static ArrayList<Double> candidateFitness;
    static ArrayList<ArrayList<Double>> survivors;
    static DemandPrediction train_problem;
    static DemandPrediction test_problem;
    private static final double STANDARD_DEVIATION = 0.1;
    static int generations = 0;

    public NovelMethod() throws IOException {
        bounds = new ArrayList<>(DemandPrediction.bounds());
        r = new Random();

        train_problem = new DemandPrediction("train");
        //test_problem = new DemandPrediction("test");

        evoAlgorithm();

    }

    public static ArrayList<Double> random_parameters(){  //element of random search
        randomised = new ArrayList<>();
        for (int j = 0; j < 14; j++) {      //14 parameters
            //randomised.add(bounds.get(0) + r.nextDouble() * (bounds.get(1) - bounds.get(0)));
            randomised.add(r.nextDouble(-100.0, 100.0));
        }
        return randomised;
    }

    /**
     * Creates Population out of random tours
     * @param size size of population
     */
    public static void initializePopulation(int size){
        population = new ArrayList<>();
        for (int i=0; i<size;i++){ population.add(random_parameters()); } //ATTENTION
    }


    /**
     * Runs fitness function on all tours of a list and puts it in a fitness list
     * @param forEval list of candidates to be evaluated
     * @param fitArray list to put the results into
     */
    public static void evaluation(ArrayList<ArrayList<Double>> forEval, ArrayList<Double> fitArray){
        for (ArrayList<Double> candidate: forEval){ fitArray.add(train_problem.evaluate(candidate)); } //ATTENTION
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
        ArrayList<Double> bestCandidate;

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
        ArrayList<Double> child1 = new ArrayList<>();
        ArrayList<Double> child2 = new ArrayList<>();
        Random rand = new Random();

        //gets two random parents
        ArrayList<Double> parentOne = parents.get(rand.nextInt(parents.size()-1));
        ArrayList<Double> parentTwo = parents.get(rand.nextInt(parents.size()-1));

        int cut = rand.nextInt(parentOne.size()-2);

        //first part of child one
        for (int i = 0; i<=cut; i++) { child1.add(parentOne.get(i));}

        //first part of child 2
        for (int i = 0; i<=cut; i++) { child2.add(parentTwo.get(i)); }

        //second part of child one
        for (int i = cut+1; i < parentTwo.size(); i++){ child1.add(parentTwo.get(i)); }

        //second part of child 2
        for (int i = cut+1; i < parentOne.size(); i++){ child2.add(parentOne.get(i)); }

        children.add(child1);
        children.add(child2);
    }

    /**
     * Mutates selected tour by swapping part of it
     * @param params sequence of cities
     * @deprecated 11-12-22
     */
    public static void swapMutation(ArrayList<Double> params){
        Random rand = new Random();
        int i = rand.nextInt(params.size()-2);
        int j = rand.nextInt(i+1, params.size()-1);

        //replace existing child with mutated child
        children.set(children.indexOf(params), twoOptSwap(params,i,j));
    }

    /**
     * Used by swapMutation. Does the swapping part
     * @param tour the sequence to mutate
     * @param i start index. the swapped part starts on the next of that one
     * @param j stop index. is included in the swapped part
     * @return the swapped sequence
     * @deprecated 11-12-22
     */
    public static ArrayList<Double> twoOptSwap(ArrayList<Double> tour, int i, int j){
        ArrayList<Double> swapped = new ArrayList<>();
        ArrayList<Double> finalTour = new ArrayList<>();

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
     * Uses the normal distribution 'Gaussian' method to get a number which is used to mutate every component
     * of the solution.
     * @param params the solution to mutate
     */
    public static void gaussianMutation(ArrayList<Double> params){
        int randnum = r.nextInt(13);
        double mutatedVal;
        //for(int i=0; i<=params.size()-1;i++){
            //set up distribution and get sample.
            //the distribution sets the existing number as mean and a constant standard deviation
            NormalDistribution distribution =  new NormalDistribution(params.get(randnum), STANDARD_DEVIATION  /*+ abs(params.get(i))*/);
            mutatedVal = params.get(randnum) + distribution.sample();

            //puts the number within bounds, if it is outside
            if(mutatedVal > 100.0) mutatedVal = (mutatedVal/2) - (abs(mutatedVal - params.get(randnum)));
            else if (mutatedVal < -100.0) mutatedVal = (mutatedVal/2) + (abs(mutatedVal - params.get(randnum)));

            //replace into params
            params.set(randnum, mutatedVal);




        //children.set(children.indexOf(params), );
    }

    /**
     * Selects survivors that make it to the next generation
     * @param survivor tour to be evaluated
     */
    public static void survivorSelection(ArrayList<Double> survivor){
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

    /**
     * For the purpose of collecting data, returns the generation number and the best fitness
     */
    public static void collectStats(){
        double bestFit;
        bestFit = fitness.get(0);
        for(Double fit: fitness){
            if(fit < bestFit) bestFit = fit;
        }
        System.out.println("Gen:" + generations + "fitness:" + bestFit);

    }

    /**
     * The main body of the algorithm. Calls all of the above methods in a sequence
     */
    public static void evoAlgorithm(){
        children = new ArrayList<>();
        parents = new ArrayList<>();
        fitness = new ArrayList<>();
        survivors = new ArrayList<>();

        initializePopulation(100);
        evaluation(population, fitness);

        //use time as termination condition
        long start = System.currentTimeMillis(); //current system time
        long end = start + 10 * 1000L;           //seconds - 2

        //while (System.currentTimeMillis() < end) {
        while (generations <= 20){
            generations++;
            // picks parents
            tournamentSelection(2,100);

            int pairsOfChildren = 50; //input half the number of children required 50*2=100 children
            for(int i =1; i<=pairsOfChildren;i++){
                //produces two children
                recombination();
            }

            //mutate all offspring created
            for(int i =0; i<children.size();i++){
                gaussianMutation(children.get(i));
                survivorSelection(children.get(i)); //all children survive
            }

            nextGenSetup();
        }

        for(ArrayList<Double> candidate: population) System.out.println(candidate);

    }
    //evolutionary alg method
    //initialise population with random candidate solutions
    //Evaluate each candidate with cost function
    //repeat until termination cond is satisfied
    //select parents
    //recombine pairs of parents
    //mutate the resulting offspring
    //evaluate individuals
    //select individuals for next generation
    //End\
    //End
}
