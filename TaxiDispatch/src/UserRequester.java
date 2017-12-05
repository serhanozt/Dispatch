import java.util.Random;

public class UserRequester {
    private int gridSize;
    Random rand;

    public UserRequester(int gridSize) {
        this.gridSize = gridSize;
        rand = new Random(System.currentTimeMillis());
    }

    public Position[] generateRequest() {
        // TODO: if src and dest are the same
        Position[] req = {new Position(rand.nextInt(gridSize), rand.nextInt(gridSize)), new Position(rand.nextInt(gridSize), rand.nextInt(gridSize))};
        return req;
    }
}
