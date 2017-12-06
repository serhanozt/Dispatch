import java.util.Random;

public class PoissonRandom {
    public static int getPoissonRandomPoints(double lambda) {
        Random r = new Random();
        double L = Math.exp(-lambda);
        int k = 0;
        double p1 = 1.0;
        do {
            p1 = p1 * r.nextDouble();
            k++;
        } while (p1 > L);
        return k - 1;
    }
}
