package application;

import java.util.Comparator;

public class AverageComparator implements Comparator<Average>{

	@Override
	public int compare(Average o1, Average o2) {
		return o1.compareTo(o2);
	}
	
}
