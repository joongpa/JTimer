package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

import javafx.util.Pair;

class Sides {
	static final int YEL = 0;
	static final int GRE = 1;
	static final int ORA = 2;
	static final int BLU = 3;
	static final int RED = 4;
	static final int WHI = 5;
			
	static final int TOP = 0;
	static final int FRONT = 1;
	static final int SIDE = 2;
}
		
abstract class Cubie {
	
	public int[] position;
	public int[] realPos;
	public int[] colors;
	public int[] orgColors;
	public HashMap<Integer, Integer> colorMap;
	public VectorHashSet twistLocations;
	
	protected Cubie(int[] position, int[] realPos) {
		this.position = position;
		this.realPos = realPos;
		initColorMap();
		initTwistLocations();
	}
	
	protected Cubie(int[] position) {
		this.position = position;
		this.realPos = position.clone();
		initColorMap();
		initTwistLocations();
	}
	
	public boolean isSolved() {
		return Arrays.equals(realPos, position);
	}
	
	public void initOrgColors() {
		orgColors = colors.clone();
	}
	
	abstract void initColorMap();
	abstract void initTwistLocations();
	
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
	
	public int twistDirection;
	
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
	public void initTwistLocations() {
		twistLocations = new VectorHashSet(4);
		twistLocations.add(new int[] {0,0,0});
		twistLocations.add(new int[] {0,2,2});
		twistLocations.add(new int[] {2,2,0});
		twistLocations.add(new int[] {2,0,2});
	}

	@Override
	public int getSide(int color) {
		return colorMap.get(color);
	}

	@Override
	public boolean isTwisted() {
		return !(colors[Sides.TOP] == Sides.YEL || colors[Sides.TOP] == Sides.WHI);
	}

	public void setTwistDirection() {
		int topColor = IntStream.range(0, colors.length)
			.filter(i -> orgColors[Sides.TOP] == colors[i])
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
	}
}

class Edge extends Cubie{
	
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
	public void initTwistLocations() {
		twistLocations = new VectorHashSet(4);
		twistLocations.add(new int[] {0,0,1});
		twistLocations.add(new int[] {0,2,1});
		twistLocations.add(new int[] {2,2,1});
		twistLocations.add(new int[] {2,0,1});
	}

	@Override
	public int getSide(int color) {
		return colorMap.get(color);
	}

	@Override
	public boolean isTwisted() {
		
		if(twistLocations.contains(position)) {
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

class VectorHashSet extends HashSet<int[]> {
	
	//idk what this is but eclipse made me put it here
	private static final long serialVersionUID = 1L;

	public VectorHashSet() {
		super();
	}
	
	public VectorHashSet(int initialSize) {
		super(initialSize);
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


public class Cube {
	
	int[] cornerBuffer;
	int[] edgeBuffer;
	int[][] parityEdges;
	
	Cubie[][][] cubieArray;
	HashSet<Cubie> tracedCorners;
	HashSet<Cubie> tracedEdges;
	HashSet<Corner> twists;
	HashSet<Edge> flips;
	boolean parity;
	
	public Cube(int[] cornerBuffer, int[] edgeBuffer, int[]... parityEdges) {
		tracedCorners = new HashSet<Cubie>(8);
		tracedEdges = new HashSet<Cubie>(12);
		twists = new HashSet<Corner>(8);
		flips = new HashSet<Edge>(12);
		
		this.cornerBuffer = cornerBuffer;
		this.edgeBuffer = edgeBuffer;
		this.parityEdges = parityEdges;
		initCubeArray();
		initCubeColors();
	}
	
	private void initCubeArray() {
		cubieArray = new Cubie[3][3][3];
		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				for(int k = 0; k < 3; k++) {
					if(i % 2 == 0 && j % 2 == 0 && k % 2 == 0) {
						cubieArray[i][j][k] = new Corner(new int[] {i,j,k});
					}
					else if(((i%2) + (j%2) + (k%2)) > 1)
						continue;
					else
						cubieArray[i][j][k] = new Edge(new int[] {i,j,k});
				}
			}
		}
	}
	
	private void initCubeColors() {
		// U layer corners
        cubieArray[0][0][0].colors = new int[] {Sides.YEL, Sides.RED, Sides.GRE};
        cubieArray[0][0][2].colors = new int[] {Sides.YEL, Sides.RED, Sides.BLU};
        cubieArray[0][2][0].colors = new int[] {Sides.YEL, Sides.ORA, Sides.GRE};
        cubieArray[0][2][2].colors = new int[] {Sides.YEL, Sides.ORA, Sides.BLU};

        // D layer corners
        cubieArray[2][0][0].colors = new int[] {Sides.WHI, Sides.RED, Sides.GRE};
        cubieArray[2][0][2].colors = new int[] {Sides.WHI, Sides.RED, Sides.BLU};
        cubieArray[2][2][0].colors = new int[] {Sides.WHI, Sides.ORA, Sides.GRE};
        cubieArray[2][2][2].colors = new int[] {Sides.WHI, Sides.ORA, Sides.BLU};

        // U layer edges
        cubieArray[0][0][1].colors = new int[] {Sides.YEL, Sides.RED};
        cubieArray[0][1][0].colors = new int[] {Sides.YEL, Sides.GRE};
        cubieArray[0][1][2].colors = new int[] {Sides.YEL, Sides.BLU};
        cubieArray[0][2][1].colors = new int[] {Sides.YEL, Sides.ORA};

        // E layer edges
        cubieArray[1][0][0].colors = new int[] {Sides.RED, Sides.GRE};
        cubieArray[1][0][2].colors = new int[] {Sides.RED, Sides.BLU};
        cubieArray[1][2][0].colors = new int[] {Sides.ORA, Sides.GRE};
        cubieArray[1][2][2].colors = new int[] {Sides.ORA, Sides.BLU};

        // D layer edges
        cubieArray[2][0][1].colors = new int[] {Sides.WHI, Sides.RED};
        cubieArray[2][1][0].colors = new int[] {Sides.WHI, Sides.GRE};
        cubieArray[2][1][2].colors = new int[] {Sides.WHI, Sides.BLU};
        cubieArray[2][2][1].colors = new int[] {Sides.WHI, Sides.ORA};
        
        for(Cubie[][] layer : cubieArray) {
        	for(Cubie[] column : layer) {
        		for(Cubie cubie : column) {
        			if(cubie != null)
        				cubie.initOrgColors();
        		}
        	}
        }
	}
	
	private void updatePosition() {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				for(int k = 0; k < 3; k++) {
					if(cubieArray[i][j][k] != null)
						cubieArray[i][j][k].realPos = new int[] {i,j,k};
				}
			}
		}
	}
	
	public void scrambleCube(String scramble) {
		String[] moves = scramble.split(" ");
		for(String move : moves) {
			applyMove(move);
		}
	}
	
	public void applyMove(String move) {
		switch(move) {
			case "U": 
				u();
				break;
			case "U'":
				ui();
				break;
			case "U2":
				u2();
				break;
			case "D":
				d();
				break;
			case "D'":
				di();
				break;
			case "D2":
				d2();
				break;
			case "L":
				l();
				break;
			case "L'":
				li();
				break;
			case "L2":
				l2();
				break;
			case "R":
				r();
				break;
			case "R'":
				ri();
				break;
			case "R2":
				r2();
				break;
			case "F":
				f();
				break;
			case "F'":
				fi();
				break;
			case "F2":
				f2();
				break;
			case "B":
				b();
				break;
			case "B'":
				bi();
				break;
			case "B2":
				b2();
				break;
			case "M":
				m();
				break;
			case "M'":
				mi();
				break;
			case "E":
				e();
				break;
			case "E'":
				ei();
				break;
			case "S":
				s();
				break;
			case "S'":
				si();
				break;
			default:
		}
	}
	
	private void u() {
		Cubie topleft = cubieArray[0][0][0];
		Cubie topright = cubieArray[0][0][2];
		Cubie bottomright = cubieArray[0][2][2];
		Cubie bottomleft = cubieArray[0][2][0];

        topleft.colors = new int[] {topleft.colors[Sides.TOP], topleft.colors[Sides.SIDE], topleft.colors[Sides.FRONT]};
        topright.colors = new int[] {topright.colors[Sides.TOP], topright.colors[Sides.SIDE], topright.colors[Sides.FRONT]};
        bottomright.colors = new int[] {bottomright.colors[Sides.TOP], bottomright.colors[Sides.SIDE], bottomright.colors[Sides.FRONT]};
		bottomleft.colors = new int[] {bottomleft.colors[Sides.TOP], bottomleft.colors[Sides.SIDE], bottomleft.colors[Sides.FRONT]};

	    Cubie temp = cubieArray[0][0][0];
		cubieArray[0][0][0] = cubieArray[0][2][0];
		cubieArray[0][2][0] = cubieArray[0][2][2];
		cubieArray[0][2][2] = cubieArray[0][0][2];
		cubieArray[0][0][2] = temp;

		temp = cubieArray[0][0][1];
		cubieArray[0][0][1] = cubieArray[0][1][0];
		cubieArray[0][1][0] = cubieArray[0][2][1];
		cubieArray[0][2][1] = cubieArray[0][1][2];
		cubieArray[0][1][2] = temp;

		updatePosition();
	}
	
	private void ui() {
		Cubie topleft = cubieArray[0][0][0];
		Cubie topright = cubieArray[0][0][2];
		Cubie bottomright = cubieArray[0][2][2];
		Cubie bottomleft = cubieArray[0][2][0];

        topleft.colors = new int[] {topleft.colors[Sides.TOP], topleft.colors[Sides.SIDE], topleft.colors[Sides.FRONT]};
        topright.colors = new int[] {topright.colors[Sides.TOP], topright.colors[Sides.SIDE], topright.colors[Sides.FRONT]};
        bottomright.colors = new int[] {bottomright.colors[Sides.TOP], bottomright.colors[Sides.SIDE], bottomright.colors[Sides.FRONT]};
		bottomleft.colors = new int[] {bottomleft.colors[Sides.TOP], bottomleft.colors[Sides.SIDE], bottomleft.colors[Sides.FRONT]};

	    Cubie temp = cubieArray[0][0][0];
		cubieArray[0][0][0] = cubieArray[0][0][2];
		cubieArray[0][0][2] = cubieArray[0][2][2];
		cubieArray[0][2][2] = cubieArray[0][2][0];
		cubieArray[0][2][0] = temp;

		temp = cubieArray[0][0][1];
		cubieArray[0][0][1] = cubieArray[0][1][2];
		cubieArray[0][1][2] = cubieArray[0][2][1];
		cubieArray[0][2][1] = cubieArray[0][1][0];
		cubieArray[0][1][0] = temp;

		updatePosition();
	}
	
	private void u2() {

	    Cubie temp = cubieArray[0][0][0];
		cubieArray[0][0][0] = cubieArray[0][2][2];
		cubieArray[0][2][2] = temp;
		
		temp = cubieArray[0][2][0];
		cubieArray[0][2][0] = cubieArray[0][0][2];
		cubieArray[0][0][2] = temp;
		
		temp = cubieArray[0][0][1];
		cubieArray[0][0][1] = cubieArray[0][2][1];
		cubieArray[0][2][1] = temp;
		
		temp = cubieArray[0][1][0];
		cubieArray[0][1][0] = cubieArray[0][1][2];
		cubieArray[0][1][2] = temp;
		
		updatePosition();
	}
	
	private void d() {
		Cubie topleft = cubieArray[2][0][0];
		Cubie topright = cubieArray[2][0][2];
		Cubie bottomright = cubieArray[2][2][2];
		Cubie bottomleft = cubieArray[2][2][0];

        topleft.colors = new int[] {topleft.colors[Sides.TOP], topleft.colors[Sides.SIDE], topleft.colors[Sides.FRONT]};
        topright.colors = new int[] {topright.colors[Sides.TOP], topright.colors[Sides.SIDE], topright.colors[Sides.FRONT]};
        bottomright.colors = new int[] {bottomright.colors[Sides.TOP], bottomright.colors[Sides.SIDE], bottomright.colors[Sides.FRONT]};
		bottomleft.colors = new int[] {bottomleft.colors[Sides.TOP], bottomleft.colors[Sides.SIDE], bottomleft.colors[Sides.FRONT]};

	    Cubie temp = cubieArray[2][0][0];
		cubieArray[2][0][0] = cubieArray[2][0][2];
		cubieArray[2][0][2] = cubieArray[2][2][2];
		cubieArray[2][2][2] = cubieArray[2][2][0];
		cubieArray[2][2][0] = temp;

		temp = cubieArray[2][0][1];
		cubieArray[2][0][1] = cubieArray[2][1][2];
		cubieArray[2][1][2] = cubieArray[2][2][1];
		cubieArray[2][2][1] = cubieArray[2][1][0];
		cubieArray[2][1][0] = temp;

		updatePosition();
	}
	
	private void di() {
		Cubie topleft = cubieArray[2][0][0];
		Cubie topright = cubieArray[2][0][2];
		Cubie bottomright = cubieArray[2][2][2];
		Cubie bottomleft = cubieArray[2][2][0];

        topleft.colors = new int[] {topleft.colors[Sides.TOP], topleft.colors[Sides.SIDE], topleft.colors[Sides.FRONT]};
        topright.colors = new int[] {topright.colors[Sides.TOP], topright.colors[Sides.SIDE], topright.colors[Sides.FRONT]};
        bottomright.colors = new int[] {bottomright.colors[Sides.TOP], bottomright.colors[Sides.SIDE], bottomright.colors[Sides.FRONT]};
		bottomleft.colors = new int[] {bottomleft.colors[Sides.TOP], bottomleft.colors[Sides.SIDE], bottomleft.colors[Sides.FRONT]};

	    Cubie temp = cubieArray[2][0][0];
		cubieArray[2][0][0] = cubieArray[2][2][0];
		cubieArray[2][2][0] = cubieArray[2][2][2];
		cubieArray[2][2][2] = cubieArray[2][0][2];
		cubieArray[2][0][2] = temp;

		temp = cubieArray[2][0][1];
		cubieArray[2][0][1] = cubieArray[2][1][0];
		cubieArray[2][1][0] = cubieArray[2][2][1];
		cubieArray[2][2][1] = cubieArray[2][1][2];
		cubieArray[2][1][2] = temp;

		updatePosition();
	}
	
	private void d2() {

	    Cubie temp = cubieArray[2][0][0];
		cubieArray[2][0][0] = cubieArray[2][2][2];
		cubieArray[2][2][2] = temp;
		
		temp = cubieArray[2][2][0];
		cubieArray[2][2][0] = cubieArray[2][0][2];
		cubieArray[2][0][2] = temp;
		
		temp = cubieArray[2][0][1];
		cubieArray[2][0][1] = cubieArray[2][2][1];
		cubieArray[2][2][1] = temp;
		
		temp = cubieArray[2][1][0];
		cubieArray[2][1][0] = cubieArray[2][1][2];
		cubieArray[2][1][2] = temp;
		
		updatePosition();
	}
	
	private void l() {
		Cubie topleft = cubieArray[0][0][0];
		Cubie topright = cubieArray[0][2][0];
		Cubie bottomright = cubieArray[2][2][0];
		Cubie bottomleft = cubieArray[2][0][0];

        topleft.colors = new int[] {topleft.colors[Sides.FRONT], topleft.colors[Sides.TOP], topleft.colors[Sides.SIDE]};
        topright.colors = new int[] {topright.colors[Sides.FRONT], topright.colors[Sides.TOP], topright.colors[Sides.SIDE]};
        bottomright.colors = new int[] {bottomright.colors[Sides.FRONT], bottomright.colors[Sides.TOP], bottomright.colors[Sides.SIDE]};
		bottomleft.colors = new int[] {bottomleft.colors[Sides.FRONT], bottomleft.colors[Sides.TOP], bottomleft.colors[Sides.SIDE]};

	    Cubie temp = cubieArray[0][0][0];
		cubieArray[0][0][0] = cubieArray[2][0][0];
		cubieArray[2][0][0] = cubieArray[2][2][0];
		cubieArray[2][2][0] = cubieArray[0][2][0];
		cubieArray[0][2][0] = temp;

		temp = cubieArray[0][1][0];
		cubieArray[0][1][0] = cubieArray[1][0][0];
		cubieArray[1][0][0] = cubieArray[2][1][0];
		cubieArray[2][1][0] = cubieArray[1][2][0];
		cubieArray[1][2][0] = temp;

		updatePosition();
	}
	
	private void li() {
		Cubie topleft = cubieArray[0][0][0];
		Cubie topright = cubieArray[0][2][0];
		Cubie bottomright = cubieArray[2][2][0];
		Cubie bottomleft = cubieArray[2][0][0];

        topleft.colors = new int[] {topleft.colors[Sides.FRONT], topleft.colors[Sides.TOP], topleft.colors[Sides.SIDE]};
        topright.colors = new int[] {topright.colors[Sides.FRONT], topright.colors[Sides.TOP], topright.colors[Sides.SIDE]};
        bottomright.colors = new int[] {bottomright.colors[Sides.FRONT], bottomright.colors[Sides.TOP], bottomright.colors[Sides.SIDE]};
		bottomleft.colors = new int[] {bottomleft.colors[Sides.FRONT], bottomleft.colors[Sides.TOP], bottomleft.colors[Sides.SIDE]};

	    Cubie temp = cubieArray[0][0][0];
		cubieArray[0][0][0] = cubieArray[0][2][0];
		cubieArray[0][2][0] = cubieArray[2][2][0];
		cubieArray[2][2][0] = cubieArray[2][0][0];
		cubieArray[2][0][0] = temp;

		temp = cubieArray[0][1][0];
		cubieArray[0][1][0] = cubieArray[1][2][0];
		cubieArray[1][2][0] = cubieArray[2][1][0];
		cubieArray[2][1][0] = cubieArray[1][0][0];
		cubieArray[1][0][0] = temp;

		updatePosition();
	}
	
	private void l2() {

	    Cubie temp = cubieArray[0][0][0];
		cubieArray[0][0][0] = cubieArray[2][2][0];
		cubieArray[2][2][0] = temp;
		
		temp = cubieArray[2][0][0];
		cubieArray[2][0][0] = cubieArray[0][2][0];
		cubieArray[0][2][0] = temp;
		
		temp = cubieArray[0][1][0];
		cubieArray[0][1][0] = cubieArray[2][1][0];
		cubieArray[2][1][0] = temp;
		
		temp = cubieArray[1][0][0];
		cubieArray[1][0][0] = cubieArray[1][2][0];
		cubieArray[1][2][0] = temp;
		
		updatePosition();
	}
	
	private void r() {
		Cubie topleft = cubieArray[0][2][2];
		Cubie topright = cubieArray[0][0][2];
		Cubie bottomright = cubieArray[2][0][2];
		Cubie bottomleft = cubieArray[2][2][2];

        topleft.colors = new int[] {topleft.colors[Sides.FRONT], topleft.colors[Sides.TOP], topleft.colors[Sides.SIDE]};
        topright.colors = new int[] {topright.colors[Sides.FRONT], topright.colors[Sides.TOP], topright.colors[Sides.SIDE]};
        bottomright.colors = new int[] {bottomright.colors[Sides.FRONT], bottomright.colors[Sides.TOP], bottomright.colors[Sides.SIDE]};
		bottomleft.colors = new int[] {bottomleft.colors[Sides.FRONT], bottomleft.colors[Sides.TOP], bottomleft.colors[Sides.SIDE]};

	    Cubie temp = cubieArray[0][0][2];
		cubieArray[0][0][2] = cubieArray[0][2][2];
		cubieArray[0][2][2] = cubieArray[2][2][2];
		cubieArray[2][2][2] = cubieArray[2][0][2];
		cubieArray[2][0][2] = temp;

		temp = cubieArray[0][1][2];
		cubieArray[0][1][2] = cubieArray[1][2][2];
		cubieArray[1][2][2] = cubieArray[2][1][2];
		cubieArray[2][1][2] = cubieArray[1][0][2];
		cubieArray[1][0][2] = temp;

		updatePosition();
	}
	
	private void ri() {
		Cubie topleft = cubieArray[0][2][2];
		Cubie topright = cubieArray[0][0][2];
		Cubie bottomright = cubieArray[2][0][2];
		Cubie bottomleft = cubieArray[2][2][2];

        topleft.colors = new int[] {topleft.colors[Sides.FRONT], topleft.colors[Sides.TOP], topleft.colors[Sides.SIDE]};
        topright.colors = new int[] {topright.colors[Sides.FRONT], topright.colors[Sides.TOP], topright.colors[Sides.SIDE]};
        bottomright.colors = new int[] {bottomright.colors[Sides.FRONT], bottomright.colors[Sides.TOP], bottomright.colors[Sides.SIDE]};
		bottomleft.colors = new int[] {bottomleft.colors[Sides.FRONT], bottomleft.colors[Sides.TOP], bottomleft.colors[Sides.SIDE]};

	    Cubie temp = cubieArray[0][0][2];
		cubieArray[0][0][2] = cubieArray[2][0][2];
		cubieArray[2][0][2] = cubieArray[2][2][2];
		cubieArray[2][2][2] = cubieArray[0][2][2];
		cubieArray[0][2][2] = temp;

		temp = cubieArray[0][1][2];
		cubieArray[0][1][2] = cubieArray[1][0][2];
		cubieArray[1][0][2] = cubieArray[2][1][2];
		cubieArray[2][1][2] = cubieArray[1][2][2];
		cubieArray[1][2][2] = temp;

		updatePosition();
	}
	
	private void r2() {

	    Cubie temp = cubieArray[0][0][2];
		cubieArray[0][0][2] = cubieArray[2][2][2];
		cubieArray[2][2][2] = temp;
		
		temp = cubieArray[2][0][2];
		cubieArray[2][0][2] = cubieArray[0][2][2];
		cubieArray[0][2][2] = temp;
		
		temp = cubieArray[0][1][2];
		cubieArray[0][1][2] = cubieArray[2][1][2];
		cubieArray[2][1][2] = temp;
		
		temp = cubieArray[1][0][2];
		cubieArray[1][0][2] = cubieArray[1][2][2];
		cubieArray[1][2][2] = temp;
		
		updatePosition();
	}
	
	private void f() {
		Cubie topleft = cubieArray[0][2][0];
		Cubie topright = cubieArray[0][2][2];
		Cubie bottomright = cubieArray[2][2][2];
		Cubie bottomleft = cubieArray[2][2][0];

        topleft.colors = new int[] {topleft.colors[Sides.SIDE], topleft.colors[Sides.FRONT], topleft.colors[Sides.TOP]};
        topright.colors = new int[] {topright.colors[Sides.SIDE], topright.colors[Sides.FRONT], topright.colors[Sides.TOP]};
        bottomright.colors = new int[] {bottomright.colors[Sides.SIDE], bottomright.colors[Sides.FRONT], bottomright.colors[Sides.TOP]};
		bottomleft.colors = new int[] {bottomleft.colors[Sides.SIDE], bottomleft.colors[Sides.FRONT], bottomleft.colors[Sides.TOP]};

	    Cubie temp = cubieArray[0][2][0];
		cubieArray[0][2][0] = cubieArray[2][2][0];
		cubieArray[2][2][0] = cubieArray[2][2][2];
		cubieArray[2][2][2] = cubieArray[0][2][2];
		cubieArray[0][2][2] = temp;

		Cubie top = cubieArray[0][2][1];
		Cubie right = cubieArray[1][2][2];
		Cubie bottom = cubieArray[2][2][1];
		Cubie left = cubieArray[1][2][0];
		
		top.colors = new int[] {top.colors[Sides.FRONT], top.colors[Sides.TOP]};
		right.colors = new int[] {right.colors[Sides.FRONT], right.colors[Sides.TOP]};
		left.colors = new int[] {left.colors[Sides.FRONT], left.colors[Sides.TOP]};
		bottom.colors = new int[] {bottom.colors[Sides.FRONT], bottom.colors[Sides.TOP]};
		
		temp = cubieArray[0][2][1];
		cubieArray[0][2][1] = cubieArray[1][2][0];
		cubieArray[1][2][0] = cubieArray[2][2][1];
		cubieArray[2][2][1] = cubieArray[1][2][2];
		cubieArray[1][2][2] = temp;

		updatePosition();
	}
	
	private void fi() {
		Cubie topleft = cubieArray[0][2][0];
		Cubie topright = cubieArray[0][2][2];
		Cubie bottomright = cubieArray[2][2][2];
		Cubie bottomleft = cubieArray[2][2][0];

        topleft.colors = new int[] {topleft.colors[Sides.SIDE], topleft.colors[Sides.FRONT], topleft.colors[Sides.TOP]};
        topright.colors = new int[] {topright.colors[Sides.SIDE], topright.colors[Sides.FRONT], topright.colors[Sides.TOP]};
        bottomright.colors = new int[] {bottomright.colors[Sides.SIDE], bottomright.colors[Sides.FRONT], bottomright.colors[Sides.TOP]};
		bottomleft.colors = new int[] {bottomleft.colors[Sides.SIDE], bottomleft.colors[Sides.FRONT], bottomleft.colors[Sides.TOP]};

	    Cubie temp = cubieArray[0][2][0];
		cubieArray[0][2][0] = cubieArray[0][2][2];
		cubieArray[0][2][2] = cubieArray[2][2][2];
		cubieArray[2][2][2] = cubieArray[2][2][0];
		cubieArray[2][2][0] = temp;

		Cubie top = cubieArray[0][2][1];
		Cubie right = cubieArray[1][2][2];
		Cubie bottom = cubieArray[2][2][1];
		Cubie left = cubieArray[1][2][0];
		
		top.colors = new int[] {top.colors[Sides.FRONT], top.colors[Sides.TOP]};
		right.colors = new int[] {right.colors[Sides.FRONT], right.colors[Sides.TOP]};
		left.colors = new int[] {left.colors[Sides.FRONT], left.colors[Sides.TOP]};
		bottom.colors = new int[] {bottom.colors[Sides.FRONT], bottom.colors[Sides.TOP]};
		
		temp = cubieArray[0][2][1];
		cubieArray[0][2][1] = cubieArray[1][2][2];
		cubieArray[1][2][2] = cubieArray[2][2][1];
		cubieArray[2][2][1] = cubieArray[1][2][0];
		cubieArray[1][2][0] = temp;

		updatePosition();
	}
	
	private void f2() {

	    Cubie temp = cubieArray[0][2][0];
		cubieArray[0][2][0] = cubieArray[2][2][2];
		cubieArray[2][2][2] = temp;
		
		temp = cubieArray[0][2][2];
		cubieArray[0][2][2] = cubieArray[2][2][0];
		cubieArray[2][2][0] = temp;
		
		temp = cubieArray[0][2][1];
		cubieArray[0][2][1] = cubieArray[2][2][1];
		cubieArray[2][2][1] = temp;
		
		temp = cubieArray[1][2][0];
		cubieArray[1][2][0] = cubieArray[1][2][2];
		cubieArray[1][2][2] = temp;
		
		updatePosition();
	}
	
	private void b() {
		Cubie topleft = cubieArray[0][0][0];
		Cubie topright = cubieArray[0][0][2];
		Cubie bottomright = cubieArray[2][0][2];
		Cubie bottomleft = cubieArray[2][0][0];

        topleft.colors = new int[] {topleft.colors[Sides.SIDE], topleft.colors[Sides.FRONT], topleft.colors[Sides.TOP]};
        topright.colors = new int[] {topright.colors[Sides.SIDE], topright.colors[Sides.FRONT], topright.colors[Sides.TOP]};
        bottomright.colors = new int[] {bottomright.colors[Sides.SIDE], bottomright.colors[Sides.FRONT], bottomright.colors[Sides.TOP]};
		bottomleft.colors = new int[] {bottomleft.colors[Sides.SIDE], bottomleft.colors[Sides.FRONT], bottomleft.colors[Sides.TOP]};

	    Cubie temp = cubieArray[0][0][0];
		cubieArray[0][0][0] = cubieArray[0][0][2];
		cubieArray[0][0][2] = cubieArray[2][0][2];
		cubieArray[2][0][2] = cubieArray[2][0][0];
		cubieArray[2][0][0] = temp;

		Cubie top = cubieArray[0][0][1];
		Cubie right = cubieArray[1][0][2];
		Cubie bottom = cubieArray[2][0][1];
		Cubie left = cubieArray[1][0][0];
		
		top.colors = new int[] {top.colors[Sides.FRONT], top.colors[Sides.TOP]};
		right.colors = new int[] {right.colors[Sides.FRONT], right.colors[Sides.TOP]};
		left.colors = new int[] {left.colors[Sides.FRONT], left.colors[Sides.TOP]};
		bottom.colors = new int[] {bottom.colors[Sides.FRONT], bottom.colors[Sides.TOP]};
		
		temp = cubieArray[0][0][1];
		cubieArray[0][0][1] = cubieArray[1][0][2];
		cubieArray[1][0][2] = cubieArray[2][0][1];
		cubieArray[2][0][1] = cubieArray[1][0][0];
		cubieArray[1][0][0] = temp;

		updatePosition();
	}
	
	private void bi() {
		Cubie topleft = cubieArray[0][0][0];
		Cubie topright = cubieArray[0][0][2];
		Cubie bottomright = cubieArray[2][0][2];
		Cubie bottomleft = cubieArray[2][0][0];

        topleft.colors = new int[] {topleft.colors[Sides.SIDE], topleft.colors[Sides.FRONT], topleft.colors[Sides.TOP]};
        topright.colors = new int[] {topright.colors[Sides.SIDE], topright.colors[Sides.FRONT], topright.colors[Sides.TOP]};
        bottomright.colors = new int[] {bottomright.colors[Sides.SIDE], bottomright.colors[Sides.FRONT], bottomright.colors[Sides.TOP]};
		bottomleft.colors = new int[] {bottomleft.colors[Sides.SIDE], bottomleft.colors[Sides.FRONT], bottomleft.colors[Sides.TOP]};

	    Cubie temp = cubieArray[0][0][0];
		cubieArray[0][0][0] = cubieArray[2][0][0];
		cubieArray[2][0][0] = cubieArray[2][0][2];
		cubieArray[2][0][2] = cubieArray[0][0][2];
		cubieArray[0][0][2] = temp;

		Cubie top = cubieArray[0][0][1];
		Cubie right = cubieArray[1][0][2];
		Cubie bottom = cubieArray[2][0][1];
		Cubie left = cubieArray[1][0][0];
		
		top.colors = new int[] {top.colors[Sides.FRONT], top.colors[Sides.TOP]};
		right.colors = new int[] {right.colors[Sides.FRONT], right.colors[Sides.TOP]};
		left.colors = new int[] {left.colors[Sides.FRONT], left.colors[Sides.TOP]};
		bottom.colors = new int[] {bottom.colors[Sides.FRONT], bottom.colors[Sides.TOP]};
		
		temp = cubieArray[0][0][1];
		cubieArray[0][0][1] = cubieArray[1][0][0];
		cubieArray[1][0][0] = cubieArray[2][0][1];
		cubieArray[2][0][1] = cubieArray[1][0][2];
		cubieArray[1][0][2] = temp;

		updatePosition();
	}
	
	private void b2() {

	    Cubie temp = cubieArray[0][0][0];
		cubieArray[0][0][0] = cubieArray[2][0][2];
		cubieArray[2][0][2] = temp;
		
		temp = cubieArray[0][0][2];
		cubieArray[0][0][2] = cubieArray[2][0][0];
		cubieArray[2][0][0] = temp;
		
		temp = cubieArray[0][0][1];
		cubieArray[0][0][1] = cubieArray[2][0][1];
		cubieArray[2][0][1] = temp;
		
		temp = cubieArray[1][0][0];
		cubieArray[1][0][0] = cubieArray[1][0][2];
		cubieArray[1][0][2] = temp;
		
		updatePosition();
	}
	
	public void m() {
		Cubie top = cubieArray[0][0][1];
		Cubie right = cubieArray[0][2][1];
		Cubie bottom = cubieArray[2][2][1];
		Cubie left = cubieArray[2][0][1];
		
		top.colors = new int[] {top.colors[Sides.FRONT], top.colors[Sides.TOP]};
		right.colors = new int[] {right.colors[Sides.FRONT], right.colors[Sides.TOP]};
		left.colors = new int[] {left.colors[Sides.FRONT], left.colors[Sides.TOP]};
		bottom.colors = new int[] {bottom.colors[Sides.FRONT], bottom.colors[Sides.TOP]};
		
		Cubie temp = cubieArray[0][0][1];
		cubieArray[0][0][1] = cubieArray[2][0][1];
		cubieArray[2][0][1] = cubieArray[2][2][1];
		cubieArray[2][2][1] = cubieArray[0][2][1];
		cubieArray[0][2][1] = temp;

		updatePosition();
	}
	
	public void mi() {
		Cubie top = cubieArray[0][0][1];
		Cubie right = cubieArray[0][2][1];
		Cubie bottom = cubieArray[2][2][1];
		Cubie left = cubieArray[2][0][1];
		
		top.colors = new int[] {top.colors[Sides.FRONT], top.colors[Sides.TOP]};
		right.colors = new int[] {right.colors[Sides.FRONT], right.colors[Sides.TOP]};
		left.colors = new int[] {left.colors[Sides.FRONT], left.colors[Sides.TOP]};
		bottom.colors = new int[] {bottom.colors[Sides.FRONT], bottom.colors[Sides.TOP]};
		
		Cubie temp = cubieArray[0][0][1];
		cubieArray[0][0][1] = cubieArray[0][2][1];
		cubieArray[0][2][1] = cubieArray[2][2][1];
		cubieArray[2][2][1] = cubieArray[2][0][1];
		cubieArray[2][0][1] = temp;

		updatePosition();
	}
	
	public void e() {
		Cubie topLeft = cubieArray[1][0][0];
		Cubie topRight = cubieArray[1][0][2];
		Cubie bottomLeft = cubieArray[1][2][0];
		Cubie bottomRight = cubieArray[1][2][2];
		
		topLeft.colors = new int[] {topLeft.colors[Sides.FRONT], topLeft.colors[Sides.TOP]};
		topRight.colors = new int[] {topRight.colors[Sides.FRONT], topRight.colors[Sides.TOP]};
		bottomLeft.colors = new int[] {bottomLeft.colors[Sides.FRONT], bottomLeft.colors[Sides.TOP]};
		bottomRight.colors = new int[] {bottomRight.colors[Sides.FRONT], bottomRight.colors[Sides.TOP]};
		
		Cubie temp = cubieArray[1][0][0];
		cubieArray[1][0][0] = cubieArray[1][0][2];
		cubieArray[1][0][2] = cubieArray[1][2][2];
		cubieArray[1][2][2] = cubieArray[1][2][0];
		cubieArray[1][2][0] = temp;

		updatePosition();
	}
	
	public void ei() {
		Cubie topLeft = cubieArray[1][0][0];
		Cubie topRight = cubieArray[1][0][2];
		Cubie bottomLeft = cubieArray[1][2][0];
		Cubie bottomRight = cubieArray[1][2][2];
		
		topLeft.colors = new int[] {topLeft.colors[Sides.FRONT], topLeft.colors[Sides.TOP]};
		topRight.colors = new int[] {topRight.colors[Sides.FRONT], topRight.colors[Sides.TOP]};
		bottomLeft.colors = new int[] {bottomLeft.colors[Sides.FRONT], bottomLeft.colors[Sides.TOP]};
		bottomRight.colors = new int[] {bottomRight.colors[Sides.FRONT], bottomRight.colors[Sides.TOP]};
		
		Cubie temp = cubieArray[1][0][0];
		cubieArray[1][0][0] = cubieArray[1][2][0];
		cubieArray[1][2][0] = cubieArray[1][2][2];
		cubieArray[1][2][2] = cubieArray[1][0][2];
		cubieArray[1][0][2] = temp;

		updatePosition();
	}
	
	private void s() {
		Cubie topLeft = cubieArray[0][1][0];
		Cubie topRight = cubieArray[0][1][2];
		Cubie bottomLeft = cubieArray[2][1][2];
		Cubie bottomRight = cubieArray[2][1][0];
		
		topLeft.colors = new int[] {topLeft.colors[Sides.FRONT], topLeft.colors[Sides.TOP]};
		topRight.colors = new int[] {topRight.colors[Sides.FRONT], topRight.colors[Sides.TOP]};
		bottomLeft.colors = new int[] {bottomLeft.colors[Sides.FRONT], bottomLeft.colors[Sides.TOP]};
		bottomRight.colors = new int[] {bottomRight.colors[Sides.FRONT], bottomRight.colors[Sides.TOP]};
		
		Cubie temp = cubieArray[0][1][0];
		cubieArray[0][1][0] = cubieArray[2][1][0];
		cubieArray[2][1][0] = cubieArray[2][1][2];
		cubieArray[2][1][2] = cubieArray[0][1][2];
		cubieArray[0][1][2] = temp;

		updatePosition();
	}
	
	private void si() {
		Cubie topLeft = cubieArray[0][1][0];
		Cubie topRight = cubieArray[0][1][2];
		Cubie bottomLeft = cubieArray[2][1][2];
		Cubie bottomRight = cubieArray[2][1][0];
		
		topLeft.colors = new int[] {topLeft.colors[Sides.FRONT], topLeft.colors[Sides.TOP]};
		topRight.colors = new int[] {topRight.colors[Sides.FRONT], topRight.colors[Sides.TOP]};
		bottomLeft.colors = new int[] {bottomLeft.colors[Sides.FRONT], bottomLeft.colors[Sides.TOP]};
		bottomRight.colors = new int[] {bottomRight.colors[Sides.FRONT], bottomRight.colors[Sides.TOP]};
		
		Cubie temp = cubieArray[0][1][0];
		cubieArray[0][1][0] = cubieArray[0][1][2];
		cubieArray[0][1][2] = cubieArray[2][1][2];
		cubieArray[2][1][2] = cubieArray[2][1][0];
		cubieArray[2][1][0] = temp;

		updatePosition();
	}
	
	@Override
	public String toString() {
		String output = "";
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				for(int k = 0; k < 3; k++) {
					if(cubieArray[i][j][k] != null)
						output += cubieArray[i][j][k].toString() + ", ";
					else output += "X ";
				}
				output += "\n";
			}
			output += "\n";
		}
		return output;
	}
	
	public int getAlgCount() {
		findSolvedPieces();		
		checkNumTwists();
		int cornerTargets = trace(cornerBuffer, tracedCorners);
		parity = cornerTargets % 2 != 0;
		int[] edgeBuffer = (parity) ? parityEdges[1] : parityEdges[0];
		
		if(parity) {
			Cubie temp = cubieArray[0][2][1];
			cubieArray[0][2][1] = cubieArray[0][1][2];
			cubieArray[0][1][2] = temp;
			flips.clear();
			updatePosition();
			findSolvedPieces();
			checkNumTwists();
		}
		
		int edgeTargets = trace(edgeBuffer, tracedEdges);
		int edgeAlgs = (int)Math.ceil((double)edgeTargets / 2);
		int cornerAlgs = (int)(Math.ceil((double)cornerTargets / 2));
		int algs = edgeAlgs + cornerAlgs + numTwistAlgs() + numFlipAlgs();
	/*	System.out.println("twists: " + twists.size());
		System.out.println("flips: " + flips.size());
		System.out.println("corner targets: " + cornerTargets);
		System.out.println("edge targets: " + edgeTargets);
		System.out.println("Parity: " + parity);
		System.out.println("total algs: " + algs);*/
		return algs;
	}
	
	public int numTwistAlgs() {
		HashSet<Corner> ccTwists = new HashSet<Corner>(8);
		HashSet<Corner> cTwists = new HashSet<Corner>(8);
		
		for(Corner corner : twists) {
			if(corner.twistDirection == 1)
				ccTwists.add(corner);
			else if(corner.twistDirection == 0)
				cTwists.add(corner);
		}
		int otherTwists = Math.abs(ccTwists.size() - cTwists.size());
		int twoTwists = Math.min(cTwists.size(), ccTwists.size());
		return twoTwists + otherTwists;
	}
	
	public int numFlipAlgs() {
		return (int)Math.ceil((double)flips.size() / 2);
	}
	
	//Allow for ECCE users later after first working version
	public void checkNumTwists() {
		for(Cubie corner : tracedCorners) {
			if(corner.isSolved() && corner.isTwisted()) {
				if(Arrays.equals(corner.position, cornerBuffer)) continue; //potentially wrong
				((Corner)corner).setTwistDirection();
				twists.add((Corner)corner);
			}
		}
		for(Cubie edge : tracedEdges) {
			if(edge.isSolved() && edge.isTwisted()) {
				if(parity) {
					if(Arrays.equals(edge.position, parityEdges[1]))
						continue;
				} else {
					if(Arrays.equals(edge.position, parityEdges[0]))
						continue;
				}
				flips.add((Edge)edge);
			}
		}
	}
	
	public void findSolvedPieces() {
		for(Cubie[][] layer : cubieArray) {
			for(Cubie[] column : layer) {
				for(Cubie cubie : column) {
					if(cubie instanceof Corner) {
						if(cubie.isSolved() /*&& !tracedCorners.contains(cubie)*/)
							tracedCorners.add((Corner)cubie);
					}
					else if(cubie instanceof Edge) {
						if(cubie.isSolved()/* && !tracedEdges.contains(cubie)*/)
							tracedEdges.add((Edge)cubie);
					}
				}
			}
		}
	}
	
	public int trace(int[] passedBuffer, HashSet<Cubie> tracedPieces) {
		
		Cubie buffer = cubieArray[passedBuffer[0]][passedBuffer[1]][passedBuffer[2]];
		int[] bufferLocation = passedBuffer;
		Class<? extends Cubie> pieceType = buffer.getClass();
		int pieceCount = (buffer instanceof Corner) ? 8 : 12;
		int side = Sides.TOP;
		int counter = 0;
		
		while(tracedPieces.size() < pieceCount) {
			Cubie temp = retrieveNextPiece(buffer, side);
			if(!tracedPieces.contains(temp)) {
				tracedPieces.add(temp);
				counter++;
			}
			buffer = temp;
			side = getNewSide(buffer, side);
			
			if(Arrays.equals(temp.position, bufferLocation)) {
				tracedPieces.add(cubieArray[bufferLocation[0]][bufferLocation[1]][bufferLocation[2]]);
				if(tracedPieces.size() < pieceCount) {
					buffer = breakCycle(pieceType); //eh maybe won't work
					bufferLocation = buffer.realPos;
					side = Sides.TOP;
					counter += 2;
				} else break;
			}
		}
		return counter;
	}
	
	public Cubie retrieveNextPiece(Cubie cubie, int sticker) {
		int[] position = cubie.position;
		Cubie nextCubie = cubieArray[position[0]][position[1]][position[2]];
		return nextCubie;
	}
	
	public int getNewSide(Cubie cubie, int sticker) {
		return cubie.getSide(cubie.colors[sticker]);
	}
	
	public Cubie breakCycle(Class<? extends Cubie> pieceType) {
		for(Cubie[][] layer : cubieArray) {
			for(Cubie[] column : layer) {
				for(Cubie cubie : column) {
					if(cubie != null) {
						if(pieceType == cubie.getClass()) {
							if(cubie instanceof Corner && !tracedCorners.contains(cubie))
								return cubie;
							else if(cubie instanceof Edge && !tracedEdges.contains(cubie))
								return cubie;
						}
					}
				}
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		double start = System.currentTimeMillis();
		HashMap<Integer, Integer> distribution = new HashMap<Integer, Integer>();
		distribution.put(5, 0);
		distribution.put(6, 0);
		distribution.put(7, 0);
		distribution.put(8, 0);
		distribution.put(9, 0);
		distribution.put(10, 0);
		distribution.put(11, 0);
		distribution.put(12, 0);
		distribution.put(13, 0);
		distribution.put(14, 0);
		
		int[] cornerBuffer = {0,2,2};
		int[] edgeBuffer = {0,2,1};
		int[] otherThing = {0,1,2};
		
		Cube cube = new Cube(cornerBuffer, edgeBuffer, edgeBuffer, otherThing);
		//cube.scrambleCube("B2 U B2 D' B2 R2 F R' U2 B2 D2 L' F2 U2 L D2 R ");
		//System.out.println(cube.getAlgCount());
		
		
		int total = 0;
		int counter = 0;
		File file = new File("C:\\Users\\Jeff Park\\PycharmProjects\\Cubing\\Scrambles.txt");
		
		try {
	        Scanner sc = new Scanner(file);

	        while (sc.hasNextLine()) {
	            String scramble = sc.nextLine();
	            scramble = scramble.replaceAll("(\\d+\\))", "");
	            scramble = scramble.replaceAll("(.w.*)", "");
	            cube.scrambleCube(scramble);
	            int algs = cube.getAlgCount();
	           // if(!cube.parity && cube.flips.size() == 0) {
		            distribution.put(algs, distribution.get(algs) + 1);
		            total += algs;
		            counter++;
	            //}
		        //if(algs >= 13)
		        //	System.out.println(scramble);
	            cube = new Cube(cornerBuffer, edgeBuffer, edgeBuffer, otherThing);
	        }
	        sc.close();
	    } 
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
		for(Map.Entry<Integer, Integer> mapElement : distribution.entrySet()) {
			System.out.println(mapElement.getKey() + ": " + mapElement.getValue());
		}
		System.out.println("total scrambles: " + counter);
		System.out.println("Average alg count: " + (double)total/(double)counter);
		
		double time = (System.currentTimeMillis() - start) / 1000;
		System.out.println(time);
	}
}

class Scrambler {
	static int length = 15;
	static String[][] moves = {{"U", "U'", "U2", "D", "D'", "D2"},
	                        {"R", "R'", "R2", "L", "L'", "L2"},
	                        {"F", "F'", "F2", "B", "B'", "B2"}};
	
	public static String genScramble() {
		String scramble = "";
		int lastFirstIndex = -1;
		int firstIndex;
		int secondIndex;
		
		for(int i = 0; i < length; i++) {
			firstIndex = getRandomWithExclusion(0, 2, lastFirstIndex);
			secondIndex = (int)(Math.random() * 6);
			lastFirstIndex = firstIndex;
			scramble += moves[firstIndex][secondIndex] + " ";
		}
		return scramble;
	}
	
	public static int getRandomWithExclusion(int start, int end, int... exclude) {
		Random rnd = new Random();
	    int random = start + rnd.nextInt(end - start + 1 - exclude.length);
	    for (int ex : exclude) {
	        if (random < ex) {
	            break;
	        }
	        random++;
	    }
	    return random;
	}
}
