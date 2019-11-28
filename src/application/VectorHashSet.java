package application;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class VectorHashSet extends HashSet<int[]> {
	
	//idk what this is but eclipse made me put it here
	private static final long serialVersionUID = 1L;

	public VectorHashSet() {
		super();
	}
	
	public VectorHashSet(int initialSize) {
		super(initialSize);
	}
	
	public VectorHashSet(List list) {
		super(list);
	}
	
	@Override 
	public boolean contains(Object object) {
		int[] vector = (int[])object;
		
		for(int[] i : this) {
			if(Arrays.equals(vector, i)) return true;
		}
		return false;
	}
}