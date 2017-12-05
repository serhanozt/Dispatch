import java.util.*;

public class VehicleManager {
    static final int ratio = 5;

    private int gridSize = 0;
    private Set<Position> availableVehicles;
    private Set<Position> unAvailableVehicles;

    public VehicleManager(int gridSize) {
        this.gridSize = gridSize;
        this.availableVehicles = new HashSet<>();
        this.unAvailableVehicles = new HashSet<>();
    }

    public void init() {
        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if(rand.nextInt(ratio) == 0) {
                    this.availableVehicles.add(new Position(i, j));
                }
            }
        }
    }

    public void move() {
        for (Position v : this.availableVehicles) {
            Random rand = new Random(System.currentTimeMillis());
            // 0: top, 1: right, 2: bottom, 3: left
            int direction = rand.nextInt(4);
            boolean move = true;
            for (int i = direction; i < direction + 4; i++) {
                switch (i % 4) {
                    case 0:
                        if (v.row > 0) {
                            v.update(v.row - 1, v.col);
                            break;
                        }
                    case 1:
                        if (v.col < gridSize - 1) {
                            v.update(v.row, v.col + 1);
                            break;
                        }
                    case 2:
                        if (v.row < gridSize - 1) {
                            v.update(v.row + 1, v.col);
                            break;
                        }
                    case 3:
                        if (v.col > 0) {
                            v.update(v.row, v.col - 1);
                            break;
                        }
                    default:
                        move = false;
                        break;
                }
                if (move) {
                    break;
                }
            }
        }
    }

    private Position getNearestVehicle(Position userPos) {
        int minDist = Integer.MAX_VALUE;
        Position vehicleChosen = null;
        for (Position vPos : this.availableVehicles) {
            int tmpDist = Position.dist(vPos, userPos);
            if (tmpDist < minDist) {
                minDist = tmpDist;
                vehicleChosen = vPos;
            }
        }

        return vehicleChosen;
    }

    public Position assignVehicle(Position userPos) {
        Position vehicleChosen = getNearestVehicle(userPos);
        if (vehicleChosen != null) {
            this.availableVehicles.remove(vehicleChosen);
            this.unAvailableVehicles.add(vehicleChosen);
        }
        return vehicleChosen;
    }

    public void freeVehicle(Position pos) {
        unAvailableVehicles.remove(pos);
        availableVehicles.add(pos);
    }

    public void log() {
        System.out.println("Available:");
        for (Position v : availableVehicles) {
            System.out.println(v.toString());
        }
        System.out.println("UnAvailable:");
        for (Position v: unAvailableVehicles) {
            System.out.println(v.toString());
        }
    }

}
