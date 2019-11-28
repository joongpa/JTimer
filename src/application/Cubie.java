package application;

import java.util.Arrays;
import java.util.HashMap;

abstract class Cubie {
	
	public int[] position;
	//public int[] realPos;
	public int[] colors;
	//public int[] orgColors;
	public HashMap<Integer, Integer> colorMap;
	
	protected Cubie(int[] position, int[] realPos) {
		this.position = position;
		//this.realPos = realPos;
		initColorMap();
		//initTwistLocations();
	}
	
	protected Cubie(int[] position) {
		this.position = position;
		//this.realPos = position.clone();
		initColorMap();
		//initTwistLocations();
	}
	
	//public boolean isSolved() {
	//	return Arrays.equals(realPos, position);
	//}
	
	//public void initOrgColors() {
	//	orgColors = colors.clone();
	//}
	
	abstract void initColorMap();
	//abstract void initTwistLocations();
	
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
	
	//public int twistDirection;
	
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
	
	/*@Override
	public void initTwistLocations() {
		twistLocations = new VectorHashSet(4);
		twistLocations.add(new int[] {0,0,0});
		twistLocations.add(new int[] {0,2,2});
		twistLocations.add(new int[] {2,2,0});
		twistLocations.add(new int[] {2,0,2});
	}*/

	@Override
	public int getSide(int color) {
		return colorMap.get(color);
	}

	@Override
	public boolean isTwisted() {
		return !(colors[Sides.TOP] == Sides.YEL || colors[Sides.TOP] == Sides.WHI);
	}

	/*public void setTwistDirection() {
		int topColor = IntStream.range(0, colors.length)
				.filter(i -> colors[i] == Sides.WHI || colors[i] == Sides.YEL)
				.findFirst()
				.orElse(-1);
		if(isTwisted()) {
			if(topColor == Sides.FRONT) {
				twistDirection = (twistLocations.contains(realPos))
								? 0
								: 1;
			}
			else if(topColor == Sides.SIDE) {
				twistDirection = (twistLocations.contains(realPos))
								? 1
								: 0;
			}
		} else twistDirection = -1;
	}*/
}

class Edge extends Cubie{
	
	static final int[][] FLIPS = {{0,0,1},
			   					  {0,2,1},
			   					  {2,2,1},
			   					  {2,0,1}};
	static final VectorHashSet FLIPLOCATIONS = new VectorHashSet(Arrays.asList(FLIPS));
	
	public Edge(int[] position, int[] realPos) {
		super(position, realPos);
		//initTwistLocations();
	}
	
	public Edge(int[] position) {
		super(position);
		//initTwistLocations();
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
	
	/*@Override
	public void initTwistLocations() {
		twistLocations = new VectorHashSet(4);
		twistLocations.add(new int[] {0,0,1});
		twistLocations.add(new int[] {0,2,1});
		twistLocations.add(new int[] {2,2,1});
		twistLocations.add(new int[] {2,0,1});
	}*/
	
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

