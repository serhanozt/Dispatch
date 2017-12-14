import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Main {

    /**
     * User priority: first come, first serve
     * User request: comes in every time unit
     * Match vehicle: Everytime user request comes, (flag=0/1)match the nearest vehicle, (flag=2) match the longest idle time vehicle
     * Idle time: (flag=0)vehicle doesn't move during idle time. (flag=1)vehicle move randomly
     */
    public static List<User> dispatch(int flag, int gridSize, int numReq, int window, long seed, double lambda, VehicleManager vehicleManager) {
        vehicleManager.init();
        UserRequester userRequester = new UserRequester(gridSize, seed);
        PriorityQueue<User> queue = new PriorityQueue<>(1, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return Integer.compare(o1.getId(), o2.getId());
            }
        });

        User[] users = new User[numReq];

        int time = 0;
        int restUser = numReq;
        while (restUser > 0 || !queue.isEmpty() || vehicleManager.hasBusyVehicle()) {
            System.out.println("Time: " + time++);
            vehicleManager.updateBusyVehicleDistance();
//            vehicleManager.log();

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
                        assign = assignNearestVehicle(queue, vehicleManager);
                        break;
                    case 3:
                        assign = assignIdlestVehicle(queue, vehicleManager);
                        break;
                    case 4:
                        assign = assignMinDeliverTimeVehicle(queue, vehicleManager, (userDemand > 0), window);
                        break;
                    default:
                        break;

                }
            }
            vehicleManager.updateIdleTime();

            List<User> pending = new ArrayList<>();
            pending.addAll(queue);
            for (User user : pending) {
                user.incrementWaitToMatch();
            }
            if (flag == 2) {
                vehicleManager.move();
            }
            System.out.println();
        }
        return Arrays.asList(users);
    }

    private static void match(User user, Vehicle vehicle, VehicleManager vehicleManager) {
        vehicle.updateDest(user.getDest());
        System.out.println(user);
        int totalDistance = Position.dist(vehicle.getPosition(), user.getSrc()) + user.getTripTime();
        vehicleManager.addBusyVehicle(vehicle, totalDistance);
        System.out.println("Vehicle chosen: " + vehicle + " Total Distance: " + totalDistance);
        user.setWaitToPickup(Position.dist(vehicle.getPosition(), user.getSrc()));
    }

    private static boolean assignNearestVehicle(Queue<User> queue, VehicleManager vehicleManager) {
        if (queue.isEmpty()) {
            return false;
        }
        User user = queue.poll();
        Vehicle vehicle = vehicleManager.assignNearestVehicle(user.getSrc());
        if (vehicle != null) {
            match(user, vehicle, vehicleManager);
        } else {
            queue.add(user);
        }
        return vehicle != null;
    }

    private static boolean assignIdlestVehicle(Queue<User> queue, VehicleManager vehicleManager) {
        if (queue.isEmpty()) {
            return false;
        }
        User user = queue.poll();
        Vehicle vehicle = vehicleManager.assignLongestIdleTimeVehicle();
        if (vehicle != null) {
            match(user, vehicle, vehicleManager);
        } else {
            queue.add(user);
        }
        return vehicle != null;
    }

    private static boolean assignMinDeliverTimeVehicle(Queue<User> queue, VehicleManager vehicleManager, boolean hasNewUser, int window) {
        if (queue.isEmpty()) {
            return false;
        }
        if (queue.size() >= window || !hasNewUser) {
            List<User> users = new ArrayList<>(queue);
            Map.Entry<Vehicle, User> tuple = vehicleManager.assignMinDeliverTimeVehicle(users);
            if (tuple != null) {
                Vehicle vehicle = tuple.getKey();
                User user = tuple.getValue();
                match(user, vehicle, vehicleManager);
                queue.remove(user);
                return true;
            }
        }
        return false;
    }

    private static double max(List<Integer> list) {
        int max = Integer.MIN_VALUE;
        for (int ele : list) {
            max = Math.max(ele, max);
        }
        return max;
    }

    private static double min(List<Integer> list) {
        int min = Integer.MAX_VALUE;
        for (int ele : list) {
            min = Math.min(ele, min);
        }
        return min;
    }

    private static double avg(List<Integer> list) {
        int avg = 0;
        for (int ele : list) {
            avg += ele;
        }
        return avg / list.size();
    }

    private static double getVariance(List<Integer> list)
    {
        double mean = avg(list);
        double temp = 0;
        for(double a : list)
            temp += (a-mean)*(a-mean);
        return temp/(list.size()-1);
    }

    private static double getStdDev(List<Integer> list)
    {
        return Math.sqrt(getVariance(list));
    }

    public static List<Double> prepareData(List<User> users, VehicleManager vehicleManager) {
        List<Double> data = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        for (User user : users) {
            list.add(user.getWaitToMatch());
        }
        data.add(max(list));
        data.add(avg(list));
        data.add(min(list));
        data.add(getStdDev(list));
        list.clear();
        for (User user : users) {
            list.add(user.getWaitToPickup());
        }
        data.add(max(list));
        data.add(avg(list));
        data.add(min(list));
        data.add(getStdDev(list));
        list.clear();
        for (User user : users) {
            list.add(user.getServeTime());
        }
        data.add(max(list));
        data.add(avg(list));
        data.add(min(list));
        data.add(getStdDev(list));
        list.clear();
        Integer[] idleTimes = vehicleManager.getIdleTimes();
        for (int ele : idleTimes) {
            list.add(ele);
        }
        data.add(max(list));
        data.add(avg(list));
        data.add(min(list));
        data.add(getStdDev(list));
        return data;
    }


    /**
     *
     * @param gridSize
     * @param numReq
     * @param window
     * @param seed
     * @param lambda
     * @param numOfVehicles
     * @return {max, mean, min wait to match time; max, mean, min wait to pickup time; max, mean, min serve time; max, mean, min idle time}
     */
    public static List<List<Double>> experiment(int gridSize, int numReq, int window, long seed, double lambda, int numOfVehicles) {
        List<List<Double>> result = new ArrayList<>();

        VehicleManager vehicleManager = new VehicleManager(gridSize, seed, numOfVehicles);
        User.refreshIdGenerator();
        Vehicle.refreshIdGenerator();
        List<User> userResult = dispatch(1, gridSize, numReq, window, seed, lambda, vehicleManager);
        result.add(prepareData(userResult, vehicleManager));

        vehicleManager = new VehicleManager(gridSize, seed, numOfVehicles);
        User.refreshIdGenerator();
        Vehicle.refreshIdGenerator();
        userResult = dispatch(2, gridSize, numReq, window, seed, lambda, vehicleManager);
        result.add(prepareData(userResult, vehicleManager));

        vehicleManager = new VehicleManager(gridSize, seed, numOfVehicles);
        User.refreshIdGenerator();
        Vehicle.refreshIdGenerator();
        userResult = dispatch(3, gridSize, numReq, window, seed, lambda, vehicleManager);
        result.add(prepareData(userResult, vehicleManager));

        vehicleManager = new VehicleManager(gridSize, seed, numOfVehicles);
        User.refreshIdGenerator();
        Vehicle.refreshIdGenerator();
        userResult = dispatch(4, gridSize, numReq, window, seed, lambda, vehicleManager);
        result.add(prepareData(userResult, vehicleManager));

        return result;
    }

    private static String[] toStringArray(List<Double> list) {
        String[] strs = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            strs[i] = String.valueOf(list.get(i));
        }
        return strs;
    }

    public static void plot(String row, String[] column, List<List<Double>> result, PrintWriter pw) {
        pw.println(row);
        for (int i = 0; i < column.length; i++) {
            pw.print(column[i]);
            pw.print(",");
            pw.println(String.join(",", toStringArray(result.get(i))));
        }
        pw.println();
    }


    public static void eval(int gridSize, int numReq, int window, long seed, double lambda, int numOfVehicles, PrintWriter pw) {
        String row = "max Wait to match,avg Wait to match,min Wait to match,std Wait to match,max Wait to pickup,avg Wait to pickup,min Wait to pickup,std Wait to pickup,max Serve time,avg Serve time,min Serve time,std Serve time,max Vehicle idle time,avg Vehicle idle time,min Vehicle idle time,std Vehicle idle time";
        String[] column = {"Algorithm 1", "Algorithm 2", "Algorithm 3", "Algorithm 4"};
        if (gridSize == 0) {
            pw.println("Different GridSize");
            for (gridSize = 20; gridSize <= 70; gridSize += 50) {
                List<List<Double>> result = experiment(gridSize, numReq, window, seed, lambda, numOfVehicles);
                String xLabel = "GridSize:" + gridSize + " NumUser:" + numReq + " NumVeh:" + numOfVehicles + " Window:" + window + " lambda:" + lambda + ",";
                xLabel += row;
                plot(xLabel, column, result, pw);
            }
        } else if (numReq == 0) {
            pw.println("Different number of user requests");
            for (numReq = 40; numReq <= 120; numReq+=40) {
                List<List<Double>> result = experiment(gridSize, numReq, window, seed, lambda, numOfVehicles);
                String xLabel = "GridSize:" + gridSize + " NumUser:" + numReq + " NumVeh:" + numOfVehicles + " Window:" + window + " lambda:" + lambda + ",";
                xLabel += row;
                plot(xLabel, column, result, pw);
            }
        } else if (window == 0) {
            pw.println("Different window");
            for (window = 4; window <= 10; window += 2) {
                List<List<Double>> result = experiment(gridSize, numReq, window, seed, lambda, numOfVehicles);
                String xLabel = "GridSize:" + gridSize + " NumUser:" + numReq + " NumVeh:" + numOfVehicles + " Window:" + window + " lambda:" + lambda + ",";
                xLabel += row;
                plot(xLabel, column, result, pw);
            }
        } else if (lambda == 0) {
            pw.println("Different lambda");
            for (lambda = 4; lambda <= 10; lambda += 3) {
                List<List<Double>> result = experiment(gridSize, numReq, window, seed, lambda, numOfVehicles);
                String xLabel = "GridSize:" + gridSize + " NumUser:" + numReq + " NumVeh:" + numOfVehicles + " Window:" + window + " lambda:" + lambda + ",";
                xLabel += row;
                plot(xLabel, column, result, pw);
            }
        } else if (numOfVehicles == 0) {
            pw.println("Different number of vehicles");
            for (numOfVehicles = 50; numOfVehicles <= 80; numOfVehicles += 15) {
                List<List<Double>> result = experiment(gridSize, numReq, window, seed, lambda, numOfVehicles);
                String xLabel = "GridSize:" + gridSize + " NumUser:" + numReq + " NumVeh:" + numOfVehicles + " Window:" + window + " lambda:" + lambda + ",";
                xLabel += row;
                plot(xLabel, column, result, pw);
            }
        } else {
            List<List<Double>> result = experiment(gridSize, numReq, window, seed, lambda, numOfVehicles);
            String xLabel = "GridSize:" + gridSize + " NumUser:" + numReq + " NumVeh:" + numOfVehicles + " Window:" + window + " lambda:" + lambda + ",";
            xLabel += row;
            plot(xLabel, column, result, pw);
        }
    }

    public static void main(String[] args) throws FileNotFoundException{

        File f = new File("output.csv");
        PrintWriter pw = new PrintWriter(f);

        int gridSize = 20;
        int numReq = 100;
        int window = 3;
        int seed = 100;
        int lambda = 10;
        int numOfVehicles = 70;
        eval(gridSize, numReq, window, seed, lambda, numOfVehicles, pw);
        eval(0, numReq, window, seed, lambda, numOfVehicles, pw);
        eval(gridSize, 0, window, seed, lambda, numOfVehicles, pw);
        eval(gridSize, numReq, 0, seed, lambda, numOfVehicles, pw);
        eval(gridSize, numReq, window, seed, 0, numOfVehicles, pw);
        eval(gridSize, numReq, window, seed, lambda, 0, pw);
        pw.close();

    }
}
