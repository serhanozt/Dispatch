import javafx.geometry.Pos;

import java.util.*;

public class VehicleManager {
    static final int ratio = 2;

    private int gridSize = 0;
    private Set<Vehicle> availableVehicles;
    private Set<Vehicle> unAvailableVehicles;
    private Map<Vehicle, Integer> busyVehicle;
    private Random rand;

    public VehicleManager(int gridSize, Random rand) {
        this.gridSize = gridSize;
        this.availableVehicles = new HashSet<>();
        this.unAvailableVehicles = new HashSet<>();
        this.busyVehicle = new HashMap<>();
        this.rand = rand;
    }

    public void init() {
        while (this.availableVehicles.isEmpty()) {
//            Random rand = new Random(System.currentTimeMillis());
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    if (rand.nextInt(ratio) == 0) {
                        this.availableVehicles.add(new Vehicle(new Position(i, j)));
                    }
                }
            }
        }
    }

    public void move() {
        for (Vehicle v : this.availableVehicles) {
            Random rand = new Random(System.currentTimeMillis());
            // 0: top, 1: right, 2: bottom, 3: left
            int direction = rand.nextInt(4);
            boolean move = true;
            Position pos = v.getPosition();
            for (int i = direction; i < direction + 4; i++) {
                switch (i % 4) {
                    case 0:
                        if (pos.row > 0) {
                            pos.update(pos.row - 1, pos.col);
                            break;
                        }
                    case 1:
                        if (pos.col < gridSize - 1) {
                            pos.update(pos.row, pos.col + 1);
                            break;
                        }
                    case 2:
                        if (pos.row < gridSize - 1) {
                            pos.update(pos.row + 1, pos.col);
                            break;
                        }
                    case 3:
                        if (pos.col > 0) {
                            pos.update(pos.row, pos.col - 1);
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

    private Vehicle getNearestVehicle(Position userPos) {
        int minDist = Integer.MAX_VALUE;
        Vehicle vehicleChosen = null;
        for (Vehicle v : this.availableVehicles) {
            int tmpDist = Position.dist(v.getPosition(), userPos);
            if (tmpDist < minDist) {
                minDist = tmpDist;
                vehicleChosen = v;
            }
        }
        return vehicleChosen;
    }

    public Vehicle assignNearestVehicle(Position userPos) {
        Vehicle vehicleChosen = getNearestVehicle(userPos);
        if (vehicleChosen != null) {
            this.availableVehicles.remove(vehicleChosen);
            this.unAvailableVehicles.add(vehicleChosen);
        }
        return vehicleChosen;
    }

    private Vehicle getLongestIdleTimeVehicle() {
        int maxIdleTime = -1;
        Vehicle vehicleChosen = null;
        for (Vehicle v : this.availableVehicles) {
            if(v.getIdleTime() > maxIdleTime) {
                vehicleChosen = v;
                maxIdleTime = v.getIdleTime();
            }
        }
        return vehicleChosen;
    }

    public Vehicle assignLongestIdleTimeVehicle() {
        Vehicle vehicleChosen = getLongestIdleTimeVehicle();
        if (vehicleChosen != null) {
            this.availableVehicles.remove(vehicleChosen);
            this.unAvailableVehicles.add(vehicleChosen);
        }
        return vehicleChosen;
    }

    public void freeVehicle(Vehicle vehicle) {
        this.unAvailableVehicles.remove(vehicle);
        this.availableVehicles.add(vehicle);
    }

    public boolean hasBusyVehicle() {
        return !this.unAvailableVehicles.isEmpty();
    }

    public void updateBusyVehicleDistance() {
        for (Vehicle v : this.busyVehicle.keySet()) {
            this.busyVehicle.put(v, busyVehicle.get(v) - 1);
            if (busyVehicle.get(v) == 0) {
                v.updatePosition(v.getDest());
                this.freeVehicle(v);
            }
        }
    }

    public void addBusyVehicle(Vehicle vehicle, int distance) {
        this.busyVehicle.put(vehicle, distance);
    }


    public void updateIdleTime() {
        for (Vehicle vehicle: this.availableVehicles) {
            vehicle.incrementIdleTime();
        }
    }

    public void logIdleTime() {
        int sumIdleTime = 0;
        for (Vehicle vehicle: this.availableVehicles) {
            System.out.println("Vehicle " + vehicle.getId() + "'s idle time: " + vehicle.getIdleTime());
            sumIdleTime += vehicle.getIdleTime();
        }
        System.out.println("All vehicles' idle time is " + sumIdleTime);
    }

    public void log() {
        System.out.println("Available:");
        for (Vehicle v : this.availableVehicles) {
            System.out.println(v);
        }
        System.out.println("UnAvailable:");
        for (Vehicle v: this.unAvailableVehicles) {
            System.out.println(v);
        }
    }

}
