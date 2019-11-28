package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.IntStream;

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
	
	static final int[][] TWISTS = {{0,0,0},
			   					   {0,2,2},
			   					   {2,2,0},
			   					   {2,0,2}};
	static final VectorHashSet TWISTLOCATIONS = new VectorHashSet(Arrays.asList(TWISTS));
}
		
public class Cube {
	
	int[] cornerBuffer;
	int[] edgeBuffer;
	//Corner cornerBuffer;
	//Edge edgeBuffer;
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
		
		initCubeArray();
		initCubeColors();
		
		this.cornerBuffer = cornerBuffer;
		this.edgeBuffer = edgeBuffer;
		//this.cornerBuffer = (Corner)cubieArray[cornerBuffer[0]][cornerBuffer[1]][cornerBuffer[2]];
		//this.edgeBuffer = (Edge)cubieArray[edgeBuffer[0]][edgeBuffer[1]][edgeBuffer[2]];
		this.parityEdges = parityEdges;
		
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
        
        /*for(Cubie[][] layer : cubieArray) {
        	for(Cubie[] column : layer) {
        		for(Cubie cubie : column) {
        			if(cubie != null)
        				cubie.initOrgColors();
        		}
        	}
        }*/
	}
	
	/*private void updatePosition() {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				for(int k = 0; k < 3; k++) {
					//if(cubieArray[i][j][k] != null)
						//cubieArray[i][j][k].realPos = new int[] {i,j,k};
				}
			}
		}
	}*/
	
	public void scrambleCube(String scramble) {
		String[] moves = scramble.split(" ");
		for(String move : moves) {
			applyMove(move);
		}
		setParity(moves);
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
			case "M2":
				m2();
				break;
			case "E":
				e();
				break;
			case "E'":
				ei();
				break;
			case "E2":
				e2();
				break;
			case "S":
				s();
				break;
			case "S'":
				si();
				break;
			case "S2":
				s2();
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

		//updatePosition();
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

		//updatePosition();
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
		
		//updatePosition();
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

		//updatePosition();
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

		//updatePosition();
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
		
		//updatePosition();
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

		//updatePosition();
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

		//updatePosition();
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
		
		//updatePosition();
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

		//updatePosition();
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

		//updatePosition();
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
		
		//updatePosition();
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

		//updatePosition();
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

		//updatePosition();
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
		
		//updatePosition();
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

		//updatePosition();
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

		//updatePosition();
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
		
		//updatePosition();
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

		//updatePosition();
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

		//updatePosition();
	}
	
	public void m2() {
		m();
		m();
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

		//updatePosition();
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

		//updatePosition();
	}
	
	public void e2() {
		e();
		e();
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

		//updatePosition();
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

		//updatePosition();
	}
	
	public void s2() {
		s();
		s();
	}
	
	private boolean isSolved(Cubie cubie) {
		
		return cubie == cubieArray[cubie.position[0]][cubie.position[1]][cubie.position[2]];
	}
	
	private int getTwistDirection(Corner corner) {
		int[] indices = getIndex(corner);
		int topColor = IntStream.range(0, corner.colors.length)
				.filter(c -> corner.colors[c] == Sides.WHI || corner.colors[c] == Sides.YEL)
				.findFirst()
				.orElse(-1);
		if(corner.isTwisted()) {
			if(topColor == Sides.FRONT) {
				return (Sides.TWISTLOCATIONS.contains(indices))
								? 0
								: 1;
			}
			else if(topColor == Sides.SIDE) {
				return (Sides.TWISTLOCATIONS.contains(indices))
								? 1
								: 0;
			}
		}
		return -1;
	}
	
	private int[] getIndex(Cubie cubie) {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				for(int k = 0; k < 3; k++) {
					if(cubieArray[i][j][k] == cubie) return new int[] {i,j,k};
				}
			}
		}
		return null;
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
	
	public void setParity(String[] moves) {
		int count = (int)IntStream.range(0, moves.length)
							 .filter(i -> !moves[i].contains(")"))
							 .filter(i -> !moves[i].contains("w"))
							 .filter(i -> !moves[i].contains("2"))
							 .count();
		//parity = !((count % 2 != 0) && parity);
		parity = count % 2 != 0;
	}
	
	public int getAlgCount() {
		//int[] edgeBuffer;
		
		if(parity) {
			if(Arrays.equals(parityEdges[0], edgeBuffer))
				edgeBuffer = parityEdges[1];
			else if(Arrays.equals(parityEdges[1], edgeBuffer))
				edgeBuffer = parityEdges[0];
			Cubie temp = cubieArray[0][2][1];
			cubieArray[0][2][1] = cubieArray[0][1][2];
			cubieArray[0][1][2] = temp;
		}
		findSolvedPieces();		
		checkNumTwists();
		int cornerTargets = trace(cornerBuffer, tracedCorners);
		int edgeTargets = trace(edgeBuffer, tracedEdges);
		int edgeAlgs = (int)Math.ceil((double)edgeTargets / 2);
		int cornerAlgs = (int)(Math.ceil((double)cornerTargets / 2));
		int algs = edgeAlgs + cornerAlgs + numTwistAlgs() + numFlipAlgs();
		/*System.out.println("twists: " + twists.size());
		System.out.println("flips: " + flips.size());
		System.out.println("corner targets: " + cornerTargets);
		System.out.println("edge targets: " + edgeTargets);
		System.out.println("Parity: " + parity);
		System.out.println("total algs: " + algs);*/
		//unswap();
		return algs;
	}
	
	public int numTwistAlgs() {
		HashSet<Corner> ccTwists = new HashSet<Corner>(8);
		HashSet<Corner> cTwists = new HashSet<Corner>(8);
		
		for(Corner corner : twists) {
			//if(corner.twistDirection == 1)
			if(getTwistDirection(corner) == 1)
				ccTwists.add(corner);
			//else if(corner.twistDirection == 0)
			else if(getTwistDirection(corner) == 0)
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
			//if(corner.isSolved() && corner.isTwisted()) {
			if(isSolved(corner) && corner.isTwisted()) {
				if(Arrays.equals(corner.position, cornerBuffer)) continue; //potentially wrong
				//((Corner)corner).setTwistDirection();
				twists.add((Corner)corner);
			}
		}
		for(Cubie edge : tracedEdges) {
			//if(edge.isSolved() && edge.isTwisted()) {
			if(isSolved(edge) && edge.isTwisted()) {
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
						//if(cubie.isSolved())
						if(isSolved(cubie))
							tracedCorners.add((Corner)cubie);
					}
					else if(cubie instanceof Edge) {
						//if(cubie.isSolved())
						if(isSolved(cubie))
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
			//System.out.println(buffer);
			Cubie temp = retrieveNextPiece(buffer, side);
			if(!tracedPieces.contains(temp)) {
				tracedPieces.add(temp);
				counter++;
			}
			side = getNewSide(buffer, side);
			buffer = temp;
			
			if(Arrays.equals(temp.position, bufferLocation)) {
				tracedPieces.add(cubieArray[bufferLocation[0]][bufferLocation[1]][bufferLocation[2]]);
				if(tracedPieces.size() < pieceCount) {
					buffer = breakCycle(pieceType); //eh maybe won't work
					//System.out.println("new buffer " + buffer);
					bufferLocation = getIndex(buffer); //might not work idk lol
					side = Sides.TOP;
					counter += 2;
				} else break;
			}
		}
		return counter;
	}
	
	public Cubie retrieveNextPiece(Cubie cubie, int sticker) {
		
		/*if(parity) {
			if(Arrays.equals(cubie.position, parityEdges[0])) {
				//int[] position = parityEdges[1]; //cubieArray[parityEdges[1][0]][parityEdges[1][1]][parityEdges[1][2]].position;
				Cubie nextCubie = cubieArray[parityEdges[1][0]][parityEdges[1][1]][parityEdges[1][2]];
				return nextCubie;
			}
			else if(Arrays.equals(cubie.position, parityEdges[1])) {
				//int[] position = parityEdges[0]; //cubieArray[parityEdges[0][0]][parityEdges[0][1]][parityEdges[0][2]].position;
				Cubie nextCubie = cubieArray[parityEdges[0][0]][parityEdges[0][1]][parityEdges[0][2]];
				return nextCubie;
			}
		}*/
		
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
	
	public static void main(String[] args) throws FileNotFoundException {
		test();
		/*int[] cornerBuffer = {0,2,2};
		int[] edgeBuffer = {0,2,1};
		int[] thing = {0,1,2};
		Cube cube = new Cube(cornerBuffer, edgeBuffer, edgeBuffer, thing);
		cube.scrambleCube("B2 L' D R U2 R' F L' B R2 B2 R2 L2 D2 R F2 L' D2 B2 Rw Uw");
		System.out.println(cube.getAlgCount());*/
	}
	
	public static void test() throws FileNotFoundException {
		double startTime = System.currentTimeMillis();

		HashMap<Integer, Integer> distro = new HashMap<>();
		distro.put(6, 0);
		distro.put(7, 0);
		distro.put(8, 0);
		distro.put(9, 0);
		distro.put(10, 0);
		distro.put(11, 0);
		distro.put(12, 0);
		distro.put(13, 0);
		distro.put(14, 0);
		
		int count = 0;
		int total = 0;
		int[] cornerBuffer = {0,2,2};
		int[] edgeBuffer = {0,2,1};
		int[] thing = {0,1,2};
		Cube cube = new Cube(cornerBuffer, edgeBuffer, edgeBuffer, thing);
		File file = new File("C:\\Users\\Jeff Park\\PycharmProjects\\Cubing\\Scrambles.txt");
		Scanner sc = new Scanner(file);
		while(sc.hasNext()) {
			String scramble = sc.nextLine();
			cube.scrambleCube(scramble);
			
			int algs = cube.getAlgCount();
			distro.put(algs, distro.get(algs) + 1);
			total += algs;
			cube = new Cube(cornerBuffer, edgeBuffer, edgeBuffer.clone(), thing);
			count++;
		}
		sc.close();
		
		for(Map.Entry<Integer, Integer> entry : distro.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		System.out.println(((double)total)/count);
		System.out.println((System.currentTimeMillis() - startTime) / 1000);
	}
}
