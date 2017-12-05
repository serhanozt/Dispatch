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

		HashMap<Integer,Integer> waitingMap = new HashMap<>();  //user waiting map
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
			waitingMap.put(newUser.getId(), 0); //start the timer for the user
			System.out.println("New user: " + newUser);
			List<User> pending = new ArrayList<>();
			while (!queue.isEmpty()) {
				User user = queue.poll();
				Position vehicle = vehicleManager.assignVehicle(user.getSrc());
				if (vehicle != null) {
					System.out.println(user);
					System.out.println("Vehicle chosen: " + vehicle.toString());
					int totalDistance = Position.dist(vehicle, user.getSrc()) + Position.dist(vehicle, user.getDest());
					busyVehicle.put(vehicle, totalDistance);
					waitingMap.put(user.getId(), totalDistance); // set user serving time
				} else {
					waitingMap.put(user.getId(), waitingMap.get(user.getId()) + 1); // increment user waiting time
					pending.add(user);
				}
			}

			queue.addAll(pending);
			vehicleManager.move();
			System.out.println();
		}
		Evaluation eval = new Evaluation();
		System.out.println("total serve + waiting time for all users:" + eval.evaluate(waitingMap));
	}
}
