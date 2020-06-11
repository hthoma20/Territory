package territory.game;

import java.util.Collection;
import java.util.List;
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
     * @return an int in the range [0, upper)
     */
    public static int randInt(int upper){
        return random.nextInt(upper);
    }

    /**
     * @return an int in the range [lower, upper)
     */
    public static int randInt(int lower, int upper){
        return random.nextInt(upper-lower) + lower;
    }

    /**
     * @param probability the probability of true
     * @return true with the given probability, false otherwise
     */
    public static boolean withProbability(double probability){
        return random.nextDouble() < probability;
    }

    /**
     * Pick a random choice with uniform probability
     * @param choices the choices to pick from
     * @return a random choice
     */
    public static <T> T pick(List<T> choices){
        return choices.get(random.nextInt(choices.size()));
    }
}
