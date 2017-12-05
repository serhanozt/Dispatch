import java.util.*;

public class Main {

    static final int gridSize = 3;

    public static void main(String[] args) {
        VehicleManager vehicleManager = new VehicleManager(gridSize);
        vehicleManager.init();
        UserRequester userRequester = new UserRequester(gridSize);
        Map<Position, Integer> busyVehicle = new HashMap<>();
        PriorityQueue<User> queue = new PriorityQueue<>(1, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return Integer.compare(o1.getId(), o2.getId());
            }
        });
        for (int i = 0; i < 10; i++) {
            for (Position v : busyVehicle.keySet()) {
                busyVehicle.put(v, busyVehicle.get(v) - 1);
                if (busyVehicle.get(v) < 1) {
                    vehicleManager.freeVehicle(v);
                }
            }
            vehicleManager.log();
            User newUser = new User(userRequester);
            queue.add(newUser);
            System.out.println("New user: " + newUser);
            List<User> pending = new ArrayList<>();
            while (!queue.isEmpty()) {
                User user = queue.poll();
                Position vehicle = vehicleManager.assignVehicle(user.getSrc());
                if (vehicle != null) {
                    System.out.println(user);
                    System.out.println("Vehicle chosen: " + vehicle.toString());
                    busyVehicle.put(vehicle, Position.dist(vehicle, user.getSrc()) + Position.dist(vehicle, user.getDest()));
                } else {
                    pending.add(user);
                }
            }
            queue.addAll(pending);
            vehicleManager.move();
            System.out.println();
        }
    }
}
