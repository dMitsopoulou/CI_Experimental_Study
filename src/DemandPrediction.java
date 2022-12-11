import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/** Car price prediction problem */
public class DemandPrediction {
    private List<ArrayList<Double>> X;
    private List<Double> y;
    public static final int N_DEMAND_INDICATORS = 13;
    // Parameters consist of a bias (intercept) for the sum and one weight for
    // each demand indicator.
    public static final int N_PARAMETERS = N_DEMAND_INDICATORS + 1;

    /**
     * Construct a car price prediction problem instance.
     * @param dataset Specifies which dataset to use. Valid values are "train",
     *                "validation" or "test".
     */
    public DemandPrediction(String dataset) throws IOException {
        if(Objects.equals(dataset, "train")) load_dataset("data/train.csv");
        else if(Objects.equals(dataset, "test")) load_dataset("data/test.csv");
        else throw new IllegalArgumentException("Only permitted arguments for " +
                    "CarPricePrediction::CarPricePrediction are train and test.");
    }
    /**
     * Rectangular bounds on the search space.
     * @return Vector b such that b[i][0] is the minimum permissible value of the
     * ith solution component and b[i][1] is the maximum.
     */

    /*
    public static double[][] bounds() {
        double[][] bnds = new double[N_PARAMETERS][2];
        double[] dim_bnd = {-100.0,100.0};
        for(int i = 0;i<N_PARAMETERS;++i)
            bnds[i] = dim_bnd;
        return bnds;
    }

     */

    /**
     * Sets a list of lower and upper bounds to dictate the problem's feasible region
     * @return arrayList of 2 bounds
     */
    public static ArrayList<Double> bounds(){
        return new ArrayList<>(Arrays.asList(-100.0,100.0));
    }

    /**
     * Check whether the function parameters (weights) lie within the
     * problem's feasible region.
     * There should be the correct number of weights for the predictor function.
     * Each weight should lie within the range specified by the bounds.
     * @param parameters a list of the problem parameters
     */
    public boolean is_valid(ArrayList<Double> parameters) {
        if(parameters.size() != N_PARAMETERS) return false;
        //All weights lie within the bounds.
        ArrayList<Double> b = bounds();
        for(int i = 0; i<N_PARAMETERS; i++)
            if(parameters.get(i) < b.get(0) || parameters.get(i) > b.get(1)) return false;
        return true;
    }

    /**
     * Evaluate a set of ANN parameters on the dataset used by the class
     * instance (train/validation/test).
     * @param parameters An array of size the bias/intercept and the weights
     *                   to be used to predict demand.
     * @return The mean absolute error of the predictions on the selected
     * dataset.
     */
    public double evaluate(ArrayList<Double> parameters) {
        double absolute_error = 0.0;
        for(int i = 0; i < X.size(); i++){
            double y_pred = predict(X.get(i),parameters);
            absolute_error += Math.abs(y.get(i)-y_pred);
        }
        absolute_error /= X.size();
        return absolute_error;
    }

    private void load_dataset(String file) throws IOException {
        ArrayList<Double> x = new ArrayList<>();
        X = new ArrayList<>();
        y = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] line_data = line.split(",");
            if(line_data.length != N_DEMAND_INDICATORS + 1) {
                throw new RuntimeException("in CarpPricePrediction::load_dataset, " +
                        "a line in the dataset contained the wrong number of" +
                        "entries.");
            }

            y.add(Double.parseDouble(line_data[0]));
            for(int i = 1; i <= N_DEMAND_INDICATORS; i++){
                x.add(Double.parseDouble(line_data[i]));
            }
            X.add(x);

        }
    }

    /*
     * Predicts demand based on a weighted sum of demand indicators. You may
     * replace this with something more complex, but will likely have to change
     * the form of the parameters array as well.
     */
    private static double predict(ArrayList<Double> demand_indicators, ArrayList<Double> parameters){
        double prediction = parameters.get(0);

        for (int i = 1; i < N_PARAMETERS ; i++) { prediction += demand_indicators.get(i-1) * parameters.get(i);}

        return prediction;
    }
}
