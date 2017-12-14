import javafx.geometry.Pos;

import java.util.*;

public class VehicleManager {

    private int gridSize = 0;
    private int numOfVehicles = 0;
    private Set<Vehicle> availableVehicles;
    private Set<Vehicle> unAvailableVehicles;
    private Map<Vehicle, Integer> busyVehicle;
    private Random rand;

    public VehicleManager(int gridSize, long seed, int numOfVehicles) {
        this.gridSize = gridSize;
        this.numOfVehicles = numOfVehicles;
        this.rand = new Random(seed);
        this.availableVehicles = new HashSet<>();
        this.unAvailableVehicles = new HashSet<>();
        this.busyVehicle = new HashMap<>();
    }

    public void init() {
        int ratio = gridSize * gridSize / numOfVehicles;
        ratio = ratio == 0 ? 1 : ratio;
        int restNum = numOfVehicles;
        int count = gridSize * gridSize;
        while (this.availableVehicles.isEmpty()) {
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    if (restNum > 0 && (rand.nextInt(ratio) == 0 || count <= restNum)) {
                        this.availableVehicles.add(new Vehicle(new Position(i, j)));
                        restNum -= 1;
                    }
                    count -= 1;
                }
            }
        }
    }

    public int getNumOfVehicles() {
        return this.numOfVehicles;
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

    // deliver time: pick up time + drop off time
    private Vehicle getMinDeliverTimeVehicle(User user) {
        int minDeliverTime = Integer.MAX_VALUE;
        Vehicle vehicleChosen = null;
        for (Vehicle v : this.availableVehicles) {
            int tmp = Position.dist(v.getPosition(), user.getSrc()) + Position.dist(user.getSrc(), user.getDest());
            if (tmp < minDeliverTime) {
                minDeliverTime = tmp;
                vehicleChosen = v;
            }
        }
        return vehicleChosen;
    }

    private Map.Entry<Vehicle, User> getMinDeliverTimeVehicle(List<User> users) {
        int minDeliverTime = Integer.MAX_VALUE;
        Vehicle vehicleChosen = null;
        User userChosen = null;
        for (User user : users) {
            Vehicle v = this.getMinDeliverTimeVehicle(user);
            if (v == null) {
                return null;
            }
            int tmp = Position.dist(v.getPosition(), user.getSrc()) + Position.dist(user.getSrc(), user.getDest());
            if (tmp < minDeliverTime) {
                minDeliverTime = tmp;
                vehicleChosen = v;
                userChosen = user;
            }
        }
        return new AbstractMap.SimpleEntry<Vehicle, User>(vehicleChosen, userChosen);
    }

    public Map.Entry<Vehicle, User> assignMinDeliverTimeVehicle(List<User> users) {
        Map.Entry<Vehicle, User> tuple = getMinDeliverTimeVehicle(users);
        if (tuple != null) {
            this.availableVehicles.remove(tuple.getKey());
            this.unAvailableVehicles.add(tuple.getKey());
        }
        return tuple;
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

    public Integer[] getIdleTimes() {
        Integer[] idleTimes = new Integer[numOfVehicles];
        for (Vehicle vehicle: this.availableVehicles) {
            idleTimes[vehicle.getId()] = vehicle.getIdleTime();
        }
        return idleTimes;
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
