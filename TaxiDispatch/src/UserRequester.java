import java.util.Random;

public class UserRequester {
    private int gridSize;
    Random rand;

    public UserRequester(int gridSize, Random rand) {
        this.gridSize = gridSize;
        this.rand = rand;
    }

    public Position[] generateRequest() {
//        Random rand = new Random(System.currentTimeMillis());
        Position src = new Position(rand.nextInt(gridSize), rand.nextInt(gridSize));
        Position dest = new Position(rand.nextInt(gridSize), rand.nextInt(gridSize));
        while (src.equals(dest)) {
            dest = new Position(rand.nextInt(gridSize), rand.nextInt(gridSize));
        }
        Position[] req = {src, dest};
        return req;
    }
}
