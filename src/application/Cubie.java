package application;

import java.util.Arrays;
import java.util.HashMap;

abstract class Cubie {
	
	public int[] position;
	public int[] colors;
	public char[] letters;
	public HashMap<Integer, Integer> colorMap;
	
	protected Cubie(int[] position, int[] realPos) {
		this.position = position;
		initColorMap();
	}
	
	protected Cubie(int[] position) {
		this.position = position;
		initColorMap();
	}
	
	abstract void initColorMap();
	
	abstract public int getSide(int color);
	abstract public boolean isTwisted();
	
	@Override
	public String toString() {
		HashMap<Integer, String> printMap = new HashMap<Integer, String>(6);
		printMap.put(0, "YEL");
		printMap.put(1, "GRE");
		printMap.put(2, "ORA");
		printMap.put(3, "BLU");
		printMap.put(4, "RED");
		printMap.put(5, "WHI");
		
		String output = "";
		for(int i : colors) {
			output += printMap.get(i) + "/";
		}
		return output;
	}
}

class Corner extends Cubie{
	
	public Corner(int[] position, int[] realPos) {
		super(position, realPos);
	}
	
	public Corner(int[] position) {
		super(position);
	}
	
	@Override
	public void initColorMap() {
		colorMap = new HashMap<Integer, Integer>(6);
		colorMap.put(Sides.YEL, 0);
		colorMap.put(Sides.WHI, 0);
		colorMap.put(Sides.ORA, 1);
		colorMap.put(Sides.RED, 1);
		colorMap.put(Sides.GRE, 2);
		colorMap.put(Sides.BLU, 2);
	}

	@Override
	public int getSide(int color) {
		return colorMap.get(color);
	}

	@Override
	public boolean isTwisted() {
		return !(colors[Sides.TOP] == Sides.YEL || colors[Sides.TOP] == Sides.WHI);
	}
}

class Edge extends Cubie{
	
	static final int[][] FLIPS = {{0,0,1},
			   					  {0,2,1},
			   					  {2,2,1},
			   					  {2,0,1}};
	static final VectorHashSet FLIPLOCATIONS = new VectorHashSet(Arrays.asList(FLIPS));
	
	public Edge(int[] position, int[] realPos) {
		super(position, realPos);
	}
	
	public Edge(int[] position) {
		super(position);
	}
	
	@Override
	public void initColorMap() {
		colorMap = new HashMap<Integer, Integer>(6);
		colorMap.put(Sides.YEL, 0);
		colorMap.put(Sides.WHI, 0);
		colorMap.put(Sides.ORA, 0);
		colorMap.put(Sides.RED, 0);
		colorMap.put(Sides.GRE, 1);
		colorMap.put(Sides.BLU, 1);
	}

	@Override
	public int getSide(int color) {
		return colorMap.get(color);
	}

	@Override
	public boolean isTwisted() {
		
		if(FLIPLOCATIONS.contains(position)) {
			return (colors[Sides.TOP] == Sides.YEL || colors[Sides.TOP] == Sides.WHI)
					? false
					: true;
		} else {
			return (colors[Sides.FRONT] == Sides.GRE || colors[Sides.FRONT] == Sides.BLU)
					? false
					: true;
		}
	}
}

