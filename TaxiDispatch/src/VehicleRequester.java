import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VehicleRequester {

	/**
	 * @param numReq
	 * @param mean
	 * @return
	 */
	public List<Vehicle> generateVehicles(int numReq, double mean){
		List<Integer> poissonDist = new ArrayList<Integer>();
		List<Vehicle> result = new ArrayList<Vehicle>();
		Random random = new Random();		
		// generate numReq random points
		for(int i = 0; i < numReq; i ++){
			getPoissonRandomPoints(poissonDist, mean);
		}

		for(int i = poissonDist.size() -1; i >= 0; i --){
			int index = random.nextInt(i + 1);
			result.add(new Vehicle(new Position(poissonDist.get(index),poissonDist.get(i))));
		}
		return result;
	}

	/**
	 * @param list
	 * @param lambda
	 */
	public void getPoissonRandomPoints(List<Integer> list, double lambda) {
		Random r = new Random();
		double L = Math.exp(-lambda);
		int k = 0;
		double p1 = 1.0;
		do {
			p1 = p1 * r.nextDouble();
			k++;
		} while (p1 > L);
		list.add(k -1);
	}
}
