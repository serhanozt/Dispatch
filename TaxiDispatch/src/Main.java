import java.util.*;

public class Main {

    static final int gridSize = 5;
    static final int numReq = 20;
    static final int window = 5;
    static long seed = 100;
    static Random rand = new Random(seed);

    /**
     * User priority: first come, first serve
     * User request: comes in every time unit
     * Match vehicle: Everytime user request comes, (flag=0/1)match the nearest vehicle, (flag=3) match the longest idle time vehicle
     * Idle time: (flag=0)vehicle doesn't move during idle time. (flag=1)vehicle move randomly
     */

    public static void alg1(int flag) {
        VehicleManager vehicleManager = new VehicleManager(gridSize, rand);
        vehicleManager.init();
        UserRequester userRequester = new UserRequester(gridSize, rand);
        PriorityQueue<User> queue = new PriorityQueue<>(1, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return Integer.compare(o1.getId(), o2.getId());
            }
        });

        int[] waitToMatch = new int[numReq];
        int[] waitToPickup = new int[numReq];
        User[] users = new User[numReq];

        int time = 0;
        while (time < numReq) {
            System.out.println("Time: " + time++);
            vehicleManager.updateBusyVehicleDistance();
            vehicleManager.log();
            User newUser = new User(userRequester);
            users[newUser.getId()] = newUser;
            queue.add(newUser);
            System.out.println("New user: " + newUser);
            List<User> pending = new ArrayList<>();
            while (!queue.isEmpty()) {
                User user = queue.poll();
                Vehicle vehicle = null;
                if (flag == 3) {
                    vehicle = alg2AssignVehicle(user, vehicleManager);
                } else {
                    vehicle = alg1AssignVehicle(user, vehicleManager);
                }
                if (vehicle == null) {
                    waitToMatch[user.getId()] += 1;
                    pending.add(user);
                } else {
                    waitToPickup[user.getId()] = Position.dist(vehicle.getPosition(), user.getSrc());
                }
            }
            if (flag == 1) {
                vehicleManager.move();
            }
            queue.addAll(pending);
            vehicleManager.updateIdleTime();
            System.out.println();
        }
        while (!queue.isEmpty() || vehicleManager.hasBusyVehicle()) {
            System.out.println("Time: " + time++);
            vehicleManager.updateBusyVehicleDistance();
            vehicleManager.log();
            List<User> pending = new ArrayList<>();
            while (!queue.isEmpty()) {
                User user = queue.poll();
                Vehicle vehicle = alg1AssignVehicle(user, vehicleManager);
                if (vehicle == null) {
                    waitToMatch[user.getId()] += 1;
                    pending.add(user);
                } else {
                    waitToPickup[user.getId()] = Position.dist(vehicle.getPosition(), user.getSrc());
                }
            }
            if (flag == 1) {
                vehicleManager.move();
            }
            queue.addAll(pending);
            vehicleManager.updateIdleTime();
            System.out.println();
        }

        int sumWaitToMatch = 0;
        int sumWaitToPickup = 0;
        int sumDeliverTime = 0;
        int sumWholeTime = 0;
        for (int i = 0; i < numReq; i++) {
            System.out.println("User " + i);
            System.out.println("Wait time to match: " + waitToMatch[i]);
            System.out.println("Wait time to pickup: " + waitToPickup[i]);
            int deliverTime = Position.dist(users[i].getSrc(), users[i].getDest());
            System.out.println("Deliver time: " + deliverTime);
            System.out.println("Whole time: " + (waitToMatch[i] + waitToPickup[i] + deliverTime));
            sumWaitToMatch += waitToMatch[i];
            sumWaitToPickup += waitToPickup[i];
            sumDeliverTime += deliverTime;
        }
        sumWholeTime = sumWaitToMatch + sumWaitToPickup + sumDeliverTime;
        System.out.println("All wait to match time: " + sumWaitToMatch);
        System.out.println("All wait to pickup time: " + sumWaitToPickup);
        System.out.println("All serving time: " + sumWholeTime);

        vehicleManager.logIdleTime();
        System.out.println();
    }

    private static Vehicle alg1AssignVehicle(User user, VehicleManager vehicleManager) {
        Vehicle vehicle = vehicleManager.assignNearestVehicle(user.getSrc());
        if (vehicle != null) {
            vehicle.updateDest(user.getDest());
            System.out.println(user);
            int totalDistance = Position.dist(vehicle.getPosition(), user.getSrc()) + Position.dist(user.getSrc(), user.getDest());
            vehicleManager.addBusyVehicle(vehicle, totalDistance);
            System.out.println("Vehicle chosen: " + vehicle + " Total Distance: " + totalDistance);
        }
        return vehicle;
    }

    /**
     * Assign the driver with longest idle time
     */
    private static Vehicle alg2AssignVehicle(User user, VehicleManager vehicleManager) {
        Vehicle vehicle = vehicleManager.assignLongestIdleTimeVehicle();
        if (vehicle != null) {
            vehicle.updateDest(user.getDest());
            System.out.println(user);
            int totalDistance = Position.dist(vehicle.getPosition(), user.getSrc()) + Position.dist(user.getSrc(), user.getDest());
            vehicleManager.addBusyVehicle(vehicle, totalDistance);
            System.out.println("Vehicle chosen: " + vehicle + " Total Distance: " + totalDistance);
        }
        return vehicle;
    }


    public static void main(String[] args) {
        System.out.println("Algorithm 1");
        alg1(1);

        User.refreshIdGenerator();
        Vehicle.refreshIdGenerator();
        System.out.println("Algorithm 2");
        alg1(2);
        System.out.println();

        User.refreshIdGenerator();
        Vehicle.refreshIdGenerator();
        System.out.println("Algorithm 3");
        alg1(3);
        System.out.println();


    }
}
