package examples;

public class att implements Comparable<att>{

	private int threat;
	
	
	public att(int t){
		threat = t;
	}
	
	@Override
	public int compareTo(att o) {
		if (o.threat == threat){
			return 0;
		}else if (o.threat<threat){
			return 1;
		}else
			return -1;
	} 
 
}
