package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.IntStream;

import net.gnehzr.tnoodle.puzzle.NoInspectionThreeByThreeCubePuzzle;
import net.gnehzr.tnoodle.scrambles.Puzzle;

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
	int[][] parityEdges;
	
	static Cubie[][][] solvedArray;
	
	static {
		solvedArray = new Cubie[3][3][3];
		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				for(int k = 0; k < 3; k++) {
					if(i % 2 == 0 && j % 2 == 0 && k % 2 == 0) {
						solvedArray[i][j][k] = new Corner(new int[] {i,j,k});
					}
					else if(((i%2) + (j%2) + (k%2)) > 1)
						continue;
					else
						solvedArray[i][j][k] = new Edge(new int[] {i,j,k});
				}
			}
		}
		
		// U layer corners
        solvedArray[0][0][0].colors = new int[] {Sides.YEL, Sides.RED, Sides.GRE};
        solvedArray[0][0][2].colors = new int[] {Sides.YEL, Sides.RED, Sides.BLU};
        solvedArray[0][2][0].colors = new int[] {Sides.YEL, Sides.ORA, Sides.GRE};
        solvedArray[0][2][2].colors = new int[] {Sides.YEL, Sides.ORA, Sides.BLU};

        // D layer corners
        solvedArray[2][0][0].colors = new int[] {Sides.WHI, Sides.RED, Sides.GRE};
        solvedArray[2][0][2].colors = new int[] {Sides.WHI, Sides.RED, Sides.BLU};
        solvedArray[2][2][0].colors = new int[] {Sides.WHI, Sides.ORA, Sides.GRE};
        solvedArray[2][2][2].colors = new int[] {Sides.WHI, Sides.ORA, Sides.BLU};

        // U layer edges
        solvedArray[0][0][1].colors = new int[] {Sides.YEL, Sides.RED};
        solvedArray[0][1][0].colors = new int[] {Sides.YEL, Sides.GRE};
        solvedArray[0][1][2].colors = new int[] {Sides.YEL, Sides.BLU};
        solvedArray[0][2][1].colors = new int[] {Sides.YEL, Sides.ORA};

        // E layer edges
        solvedArray[1][0][0].colors = new int[] {Sides.RED, Sides.GRE};
        solvedArray[1][0][2].colors = new int[] {Sides.RED, Sides.BLU};
        solvedArray[1][2][0].colors = new int[] {Sides.ORA, Sides.GRE};
        solvedArray[1][2][2].colors = new int[] {Sides.ORA, Sides.BLU};

        // D layer edges
        solvedArray[2][0][1].colors = new int[] {Sides.WHI, Sides.RED};
        solvedArray[2][1][0].colors = new int[] {Sides.WHI, Sides.GRE};
        solvedArray[2][1][2].colors = new int[] {Sides.WHI, Sides.BLU};
        solvedArray[2][2][1].colors = new int[] {Sides.WHI, Sides.ORA};
	}
	
	Cubie[][][] cubieArray;
	HashSet<Cubie> tracedCorners;
	HashSet<Cubie> tracedEdges;
	HashSet<Corner> twists;
	HashSet<Edge> flips;
	boolean parity;
	ArrayList<String> rotationOrder = new ArrayList<String>();
	
	int[] rotations = {0,0,0};
	
	public Cube(int[] cornerBuffer, int[] edgeBuffer, int[]... parityEdges) {
		tracedCorners = new HashSet<Cubie>(8);
		tracedEdges = new HashSet<Cubie>(12);
		twists = new HashSet<Corner>(8);
		flips = new HashSet<Edge>(12);
		
		initCubeArray();
		initCubeColors();
		
		this.cornerBuffer = cornerBuffer;
		this.edgeBuffer = edgeBuffer;
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
	}
	
	public void scrambleCube(String scramble) {
		String[] moves = scramble.split(" ");
		for(String move : moves) {
			applyMove(move);
		}
		setParity(moves);
		rotateBack();
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
			case "Rw": //comment here
				rw();
				break;
			case "Rw'":
				rwi();
				break;
			case "Rw2":
				rw2();
				break;
			case "Lw":
				lw();
				break;
			case "Lw'":
				lwi();
				break;
			case "Lw2":
				lw2();
				break;
			case "Uw":
				uw();
				break;
			case "Uw'":
				uwi();
				break;
			case "Uw2":
				uw2();
				break;
			case "Dw":
				dw();
				break;
			case "Dw'":
				dwi();
				break;
			case "Dw2":
				dw2();
				break;
			case "Fw":
				fw();
				break;
			case "Fw'":
				fwi();
				break;
			case "Fw2":
				fw2();
				break;
			case "Bw":
				bw();
				break;
			case "Bw'":
				bwi();
				break;
			case "Bw2":
				bw2();
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
	}
	
	public void s2() {
		s();
		s();
	}
	
	public void x() {
		rotations[0] += 1;
		r();
		mi();
		li();
	}
	
	public void xi() {
		rotations[0] -= 1;
		ri();
		m();
		l();
	}
	
	public void x2() {
		x();
		x();
	}
	
	public void y() {
		rotations[1] += 1;
		u();
		ei();
		di();
	}
	
	public void yi() {
		rotations[1] -= 1;
		ui();
		e();
		d();
	}
	
	public void y2() {
		y();
		y();
	}
	
	public void z() {
		rotations[2] += 1;
		f();
		s();
		bi();
	}
	
	public void zi() {
		rotations[2] -= 1;
		fi();
		si();
		b();
	}
	
	public void z2() {
		z();
		z();
	}
	
	public void rw() {
		l();
		x();
	}
	
	public void rwi() {
		li();
		xi();
	}
	
	public void rw2() {
		rw();
		rw();
	}
	
	public void lw() {
		r();
		xi();
	}
	
	public void lwi() {
		ri();
		x();
	}
	
	public void lw2() {
		lw();
		lw();
	}
	
	public void uw() {
		d();
		y();
	}
	
	public void uwi() {
		di();
		yi();
	}
	
	public void uw2() {
		uw();
		uw();
	}
	
	public void dw() {
		u();
		yi();
	}
	
	public void dwi() {
		ui();
		y();
	}
	
	public void dw2() {
		dw();
		dw();
	}
	
	public void fw() {
		b();
		z();
	}
	
	public void fwi() {
		bi();
		zi();
	}
	
	public void fw2() {
		fw();
		fw();
	}
	
	public void bw() {
		f();
		zi();
	}
	
	public void bwi() {
		fi();
		z();
	}
	
	public void bw2() {
		bw();
		bw();
	}
	
	public void rotateBack() {
		
		int[] temp = rotations.clone();
		
		if(temp[1] != 0) {
			if(temp[1] < 0) {
				for(int i = 0; i > temp[1]; i--) y();
			} else {
				for(int i = 0; i < temp[1]; i++) {
					yi();
				}
			}
		}
		
		if(temp[0] != 0) {
			if(temp[0] < 0) {
				for(int i = 0; i > temp[0]; i--) x();
			} else {
				for(int i = 0; i < temp[0]; i++) {
					xi();
				}
			}
		}
		
		if(temp[2] != 0) {
			if(temp[2] < 0) {
				for(int i = 0; i > temp[2]; i--) z();
			} else {
				for(int i = 0; i < temp[2]; i++) zi();
			}
		}
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
	
	private int[] getCurrentPosition(int[] position) {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				for(int k = 0; k < 3; k++) {
					if(cubieArray[i][j][k] == null) continue;
					if(Arrays.equals(cubieArray[i][j][k].position, position)) {
						return new int[] {i,j,k};
					}
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
					else output += "              ";
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
							 //.filter(i -> !moves[i].contains("w"))
							 .filter(i -> !moves[i].contains("2"))
							 .count();
		parity = count % 2 != 0;
	}
	
	public int getAlgCount() {
		if(parity) {
			/*if(Arrays.equals(parityEdges[0], edgeBuffer))
				edgeBuffer = parityEdges[1];
			else if(Arrays.equals(parityEdges[1], edgeBuffer))
				edgeBuffer = parityEdges[0];
			Cubie temp = cubieArray[0][2][1];
			cubieArray[0][2][1] = cubieArray[0][1][2];
			cubieArray[0][1][2] = temp;
			*/
			int[] array1 = getCurrentPosition(parityEdges[0]);
			int[] array2 = getCurrentPosition(parityEdges[1]);
			
			Cubie temp = cubieArray[array1[0]][array1[1]][array1[2]];
			cubieArray[array1[0]][array1[1]][array1[2]] = cubieArray[array2[0]][array2[1]][array2[2]];
			cubieArray[array2[0]][array2[1]][array2[2]] = temp;
			
			//add orientation swapping later
		}
		findSolvedPieces();		
		checkNumTwists();
		int cornerTargets = trace(cornerBuffer, tracedCorners);
		int edgeTargets = trace(edgeBuffer, tracedEdges);
		
		//int cornerTargets = traceWithFloating(cornerBuffer, tracedCorners);
		//int edgeTargets = traceWithFloating(edgeBuffer, tracedEdges);
		
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
			if(getTwistDirection(corner) == 1)
				ccTwists.add(corner);
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
			if(isSolved(corner) && corner.isTwisted()) {
				if(Arrays.equals(corner.position, cornerBuffer)) continue;
				twists.add((Corner)corner);
			}
		}
		for(Cubie edge : tracedEdges) {
			if(isSolved(edge) && edge.isTwisted()) {
				if(parity) {
					if(Arrays.equals(edge.position, parityEdges[1])) //add another if condition for parity edges[0]
					//if(Arrays.equals(edgeBuffer, parityEdges[1]))
						continue;
					//if(Arrays.equals(edgeBuffer, parityEdges[0]))
					//	continue;
				} else {
					if(Arrays.equals(edge.position, parityEdges[0])) //possibly replace parityEdges[0] with buffer position
					//if(Arrays.equals(edge.position, edgeBuffer))
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
						if(isSolved(cubie))
							tracedCorners.add((Corner)cubie);
					}
					else if(cubie instanceof Edge) {
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
			Cubie temp = retrieveNextPiece(buffer);
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
	
	//TEMPORARY METHOD ONLY
	public int traceWithFloating(int[] passedBuffer, HashSet<Cubie> tracedPieces) {
		Cubie buffer = cubieArray[passedBuffer[0]][passedBuffer[1]][passedBuffer[2]];
		int[] bufferLocation = passedBuffer;
		Class<? extends Cubie> pieceType = buffer.getClass();
		int pieceCount = (buffer instanceof Corner) ? 8 : 12;
		int side = Sides.TOP;
		int counter = 0;
		
		int badCycles = 0;
		int cycleLength = 0;
		int startingColor = Sides.YEL; //temporary
		//if(isSolved(buffer) && buffer.isTwisted()) counter += 2;
		int oddCycles = 0;
		
		while(tracedPieces.size() < pieceCount) {
			System.out.print(buffer);

			Cubie temp = retrieveNextPiece(buffer);
			if(!tracedPieces.contains(temp)) {
				tracedPieces.add(temp);
				counter++;
				cycleLength++;
			}
			System.out.println(" " + counter);
			side = getNewSide(buffer, side);
			buffer = temp;
			
			if(Arrays.equals(temp.position, bufferLocation)) {
				tracedPieces.add(cubieArray[bufferLocation[0]][bufferLocation[1]][bufferLocation[2]]);
				System.out.println(Cubie.colorFromNum(buffer.colors[side]) + " vs " + Cubie.colorFromNum(startingColor));
				
				if(cycleLength % 2 != 0) oddCycles++;
				
				if(startingColor != buffer.colors[side]) {
					badCycles++;
					/*if(twists.size() == 0 && pieceCount == 8) {
						counter += 2;
					} else if(flips.size() % 2 == 0 && pieceCount == 12) {
						counter += 2;
					}*/
				}
				
				if(tracedPieces.size() < pieceCount) {
					
					
					
					cycleLength = 0;
					
					buffer = breakCycle(pieceType); //eh maybe won't work
					//System.out.println("new buffer " + buffer);
					bufferLocation = getIndex(buffer); //might not work idk lol
					
					side = Sides.TOP;
					//int scs = (cubieArray[array1[0]][array1[1]][array1[2]].isTwisted()) ? Sides.FRONT : Sides.TOP;
					startingColor = solvedArray[bufferLocation[0]][bufferLocation[1]][bufferLocation[2]].colors[side];
				}
			}
		}
		//Cubie ff = cubieArray[passedBuffer[0]][passedBuffer[1]][passedBuffer[2]];
		//if(ff.isTwisted() && isSolved(ff)) {
		//	counter += 2;
		//}
		if(badCycles % 2 != 0 && flips.size() % 2 == 0 && pieceCount == 12) {
			counter += 2;
		}
		else if(badCycles % 2 != 0 && twists.size() == 0 && pieceCount == 8) {
			counter += 2;
		}
		System.out.println(badCycles + " " + oddCycles);
		badCycles = Math.max(badCycles - (badCycles % 2) - (oddCycles - (oddCycles % 2)), 0);
		counter += badCycles - (badCycles % 2);
		counter += oddCycles - (oddCycles % 2);
		return counter;
	}
	
	public Cubie retrieveNextPiece(Cubie cubie) {
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
		//cube.scrambleCube("F2 L2 F L U' B' D L2 U F' L' U2 B2 D2 F R' B' U F2 D L D2 B R' D' B2 L B' L2 D2 Fw Uw2");
		//cube.scrambleCube("F2 L F2 U' R U2 B' U' R' B2 L B U2 F' L B D L D B' D B D F' R B' L F2 D B' Fw Uw");
		//cube.scrambleCube("R' B2 D L D2 F' L U B2 U' F' U' F D2 L' F D' R D R2 B U2 L2 B2 L2 F D2 L' D L Fw Uw2");
		cube.scrambleCube("B' D2 B U' R F' D B' D2 R2 F U B R' U F D2 B2 R2 D B' U' F' L2 F' R U' L2 U2 F Fw' Uw2");
		//cube.scrambleCube("R' B2 R D2 R B D' R2 D' F2 R' B D' L F D2 L D B2 R' B2 U' L2 D2 F2 L U R2 U B Rw Uw2");
		System.out.println(cube.getAlgCount());*/
	}
	
	public static void test() throws FileNotFoundException {
		double startTime = System.currentTimeMillis();

		HashMap<Integer, Integer> distro = new HashMap<>();
		distro.put(5, 0);
		distro.put(6, 0);
		distro.put(7, 0);
		distro.put(8, 0);
		distro.put(9, 0);
		distro.put(10, 0);
		distro.put(11, 0);
		distro.put(12, 0);
		distro.put(13, 0);
		distro.put(14, 0);
		distro.put(15, 0);
		
		int count = 0;
		int total = 0;
		int[] cornerBuffer = {0,2,2};
		int[] edgeBuffer = {0,2,1};
		int[] thing = {0,1,2};
		Cube cube = new Cube(cornerBuffer, edgeBuffer, edgeBuffer, thing);
		File file = new File("C:\\Users\\Jeff Park\\PycharmProjects\\Cubing\\Scrambles.txt");
		Scanner sc = new Scanner(file);
		//while(sc.hasNext()) {
		for(int i = 0; i < 100; i++) {
			//String scramble = sc.nextLine();
			String scramble = Scrambler.genScramble();
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
		System.out.println("Average: " + ((double)total)/count);
		System.out.println("Total time: " + (System.currentTimeMillis() - startTime) / 1000);
	}
}
