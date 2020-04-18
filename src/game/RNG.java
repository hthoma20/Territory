package game;

import java.util.Random;

public class RNG {
    private static Random random = new Random();

    /**
     * @return a double in the range [0,1)
     */
    public static double randDouble(){
        return random.nextDouble();
    }

    /**
     * @return a double in the range [lower,upper)
     */
    public static double randDouble(double lower, double upper){
        double range = upper-lower;

        return random.nextDouble()*(range) + lower;
    }

    /**
     * @return a double in the range [lower,upper)
     */
    public static double randDouble(double upper){
        return random.nextDouble()*upper;
    }

    /**
     * @param probability the probability of true
     * @return true with the given probability, false otherwise
     */
    public static boolean withProbability(double probability){
        return random.nextDouble() < probability;
    }
}
