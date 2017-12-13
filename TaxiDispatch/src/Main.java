import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.*;

public class Main {

    static final int gridSize = 3;
    static final int numReq = 5;
    static final int window = 5;
    static long seed = 100;
    static final double lambda = 2;

    /**
     * User priority: first come, first serve
     * User request: comes in every time unit
     * Match vehicle: Everytime user request comes, (flag=0/1)match the nearest vehicle, (flag=2) match the longest idle time vehicle
     * Idle time: (flag=0)vehicle doesn't move during idle time. (flag=1)vehicle move randomly
     */
    public static void dispatch(int flag) {
        Random rand = new Random(seed);
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
        int restUser = numReq;
        while (restUser > 0 || !queue.isEmpty() || vehicleManager.hasBusyVehicle()) {
            System.out.println("Time: " + time++);
            vehicleManager.updateBusyVehicleDistance();
            vehicleManager.log();

            int userDemand = userRequester.getUserDemand(lambda);
            if (userDemand > restUser) {
                userDemand = restUser;
                restUser = 0;
            } else {
                restUser -= userDemand;
            }
            for (int i = 0; i < userDemand; i++) {
                User newUser = new User(userRequester);
                users[newUser.getId()] = newUser;
                queue.add(newUser);
                System.out.println("New user: " + newUser);
            }
            boolean assign = true;
            while (assign) {
                switch (flag) {
                    case 1:
                    case 2:
                        assign = assignNearestVehicle(queue, vehicleManager, waitToPickup);
                        break;
                    case 3:
                        assign = assignIdlestVehicle(queue, vehicleManager, waitToPickup);
                        break;
                    case 4:
                        assign = assignMinDeliverTimeVehicle(queue, vehicleManager, waitToPickup, (userDemand > 0));
                        break;
                    default:
                        break;

                }
            }
            vehicleManager.updateIdleTime();

            List<User> pending = new ArrayList<>();
            pending.addAll(queue);
            for (User user : pending) {
                waitToMatch[user.getId()] += 1;
            }
            if (flag == 2) {
                vehicleManager.move();
            }
            System.out.println();
        }

        eval(waitToMatch, waitToPickup, users, vehicleManager);
    }

    private static void match(User user, Vehicle vehicle, VehicleManager vehicleManager, int[] waitToPickup) {
        vehicle.updateDest(user.getDest());
        System.out.println(user);
        int totalDistance = Position.dist(vehicle.getPosition(), user.getSrc()) + Position.dist(user.getSrc(), user.getDest());
        vehicleManager.addBusyVehicle(vehicle, totalDistance);
        System.out.println("Vehicle chosen: " + vehicle + " Total Distance: " + totalDistance);
        waitToPickup[user.getId()] = Position.dist(vehicle.getPosition(), user.getSrc());
    }

    private static boolean assignNearestVehicle(Queue<User> queue, VehicleManager vehicleManager, int[] waitToPickup) {
        if (queue.isEmpty()) {
            return false;
        }
        User user = queue.poll();
        Vehicle vehicle = vehicleManager.assignNearestVehicle(user.getSrc());
        if (vehicle != null) {
            match(user, vehicle, vehicleManager, waitToPickup);
        } else {
            queue.add(user);
        }
        return vehicle != null;
    }

    private static boolean assignIdlestVehicle(Queue<User> queue, VehicleManager vehicleManager, int[] waitToPickup) {
        if (queue.isEmpty()) {
            return false;
        }
        User user = queue.poll();
        Vehicle vehicle = vehicleManager.assignLongestIdleTimeVehicle();
        if (vehicle != null) {
            match(user, vehicle, vehicleManager, waitToPickup);
        } else {
            queue.add(user);
        }
        return vehicle != null;
    }

    private static boolean assignMinDeliverTimeVehicle(Queue<User> queue, VehicleManager vehicleManager, int[] waitToPickup, boolean hasNewUser) {
        if (queue.isEmpty()) {
            return false;
        }
        if (queue.size() >= window || !hasNewUser) {
            List<User> users = new ArrayList<>(queue);
            Map.Entry<Vehicle, User> tuple = vehicleManager.assignMinDeliverTimeVehicle(users);
            if (tuple != null) {
                Vehicle vehicle = tuple.getKey();
                User user = tuple.getValue();
                match(user, vehicle, vehicleManager, waitToPickup);
                queue.remove(user);
                return true;
            }
        }
        return false;
    }

    public static void eval(int[] waitToMatch, int[] waitToPickup, User[] users, VehicleManager vehicleManager) {
        int sumWaitToMatch = 0;
        int sumWaitToPickup = 0;
        int sumDeliverTime = 0;
        int sumWholeTime = 0;
        for (int i = 0; i < numReq; i++) {
            System.out.println("User " + i);
            System.out.println(users[i]);
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
        System.out.println("All deliver time: " + sumDeliverTime);
        System.out.println("All serving time: " + sumWholeTime);

        vehicleManager.logIdleTime();
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("GridSize: " + gridSize);
        System.out.println("Num of user request: " + numReq);
        System.out.println("Number of vehicles / Grid: " + VehicleManager.ratio);
        System.out.println();

        User.refreshIdGenerator();
        Vehicle.refreshIdGenerator();
        System.out.println("Algorithm 1");
        dispatch(1);

        User.refreshIdGenerator();
        Vehicle.refreshIdGenerator();
        System.out.println("Algorithm 2");
        dispatch(2);
        System.out.println();

        User.refreshIdGenerator();
        Vehicle.refreshIdGenerator();
        System.out.println("Algorithm 3");
        dispatch(3);
        System.out.println();

        User.refreshIdGenerator();
        Vehicle.refreshIdGenerator();
        System.out.println("Algorithm 4");
        dispatch(4);
        System.out.println();

    }
}
