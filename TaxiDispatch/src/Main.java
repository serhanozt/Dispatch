import java.util.*;

public class Main {

    static final int gridSize = 3;

    public static void main(String[] args) {
        VehicleManager vehicleManager = new VehicleManager(gridSize);
        vehicleManager.init();
        UserRequester userRequester = new UserRequester(gridSize);
        Map<Position, Integer> busyVehicle = new HashMap<>();
        PriorityQueue<User> queue = new PriorityQueue<>(0, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return Integer.compare(o1.getId(), o2.getId());
            }
        });
        for (int i = 0; i < 3; i++) {
            vehicleManager.log();
            for (Position v : busyVehicle.keySet()) {
                if (busyVehicle.get(v) <= 1) {
                    vehicleManager.freeVehicle(v);
                }
            }
            User user = new User(userRequester);
            queue.add(user);
            System.out.println(user);
            Position vehicle = vehicleManager.assignVehicle(user.getSrc());
            System.out.println("Vehicle chosen: " + vehicle.toString());
            busyVehicle.put(vehicle, Position.dist(vehicle, user.getSrc()) + Position.dist(vehicle, user.getDest()));
            vehicleManager.move();
        }
    }
}
