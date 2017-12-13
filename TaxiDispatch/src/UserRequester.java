import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserRequester {
    private int gridSize;
    Random rand;

    public UserRequester(int gridSize, Random rand) {
        this.gridSize = gridSize;
        this.rand = rand;
    }

    public Position[] generatePosition() {
//        Random rand = new Random(System.currentTimeMillis());
        Position src = new Position(rand.nextInt(gridSize), rand.nextInt(gridSize));
        Position dest = new Position(rand.nextInt(gridSize), rand.nextInt(gridSize));
        while (src.equals(dest)) {
            dest = new Position(rand.nextInt(gridSize), rand.nextInt(gridSize));
        }
        Position[] req = {src, dest};
        return req;
    }

    public int getUserDemand(double lambda) {
        return PoissonRandom.getPoissonRandomPoints(lambda);
    }

    public int getUserDemand() {
        return 1;
    }
}
