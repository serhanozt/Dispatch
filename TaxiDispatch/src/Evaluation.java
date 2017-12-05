import java.util.HashMap;

public class Evaluation {

	public int evaluate(HashMap<Integer, Integer> map){
		int totalSum = 0;
		for (Integer value : map.values()) {
			totalSum += value;
		}
		return totalSum;
	}
	
}
