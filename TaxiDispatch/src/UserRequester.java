import java.util.Random;

public class UserRequester {
    private int gridSize;

    public UserRequester(int gridSize) {
        this.gridSize = gridSize;
    }

    public Position[] generateRequest() {
        Random rand = new Random(System.currentTimeMillis());
        // TODO: if src and dest are the same
        Position[] req = {new Position(rand.nextInt(gridSize), rand.nextInt(gridSize)), new Position(rand.nextInt(gridSize), rand.nextInt(gridSize))};
        return req;
    }
}
