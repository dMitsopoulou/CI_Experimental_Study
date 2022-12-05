import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/* A simple example of how the DemandPrediction class could be used as part
 * of a random search. It is not expected that you use this code as part of
 * your solution - it is just a demonstration of how the class's methods can be
 * called and how we can use two versions of the problem (here train and
 * test) to, respectively, obtain a promising set of parameters (using
 * train) and then to measure their performance (using test).
 */
public class RandomSearch {
    static Random r;
    static ArrayList<Double> bounds;

    public RandomSearch() throws IOException {
        var training_problem = new DemandPrediction("train");
        bounds = new ArrayList<>(DemandPrediction.bounds());
        r = new Random();

        /* Generate N_TRIES random parameters and measure their MSE on the test
         * problem, saving the best parameters.
         */
        int N_TRIES = 100;
        ArrayList<Double> best_parameters = random_parameters();
        double best_training_error = training_problem.evaluate(best_parameters);
        for (int i = 0; i < N_TRIES - 1; i++) {
            ArrayList<Double> parameters = random_parameters();
            var training_error = training_problem.evaluate(parameters);
            if(training_error < best_training_error){
                best_training_error = training_error;
                best_parameters = parameters;
            }
        }
        System.out.printf("Best training error after %d " +
                "iterations: %f%n", N_TRIES, best_training_error);

        // Check the MSE of the best parameters on the test problem.
        var test_problem  = new DemandPrediction("test");
        var test_error = test_problem.evaluate(best_parameters);
        System.out.printf("Test error of best solution " +
                "found while training: %f%n", test_error);
    }

    public static ArrayList<Double> random_parameters(){  //element of random search
        ArrayList<Double> parameters = new ArrayList<>();
        for (int j = 0; j < 14; j++) {      //14 parameters
            //parameters[j] = bounds[j][0] + r.nextDouble() * (bounds[j][1] - bounds[j][0]);
            parameters.add(bounds.get(0) + r.nextDouble() * (bounds.get(1) - bounds.get(0)));
        }
        return parameters;
    }
}
