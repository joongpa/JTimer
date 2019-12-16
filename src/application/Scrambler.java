package application;

import java.util.Random;

public class Scrambler {
	static int length = 19; //19
	static String[][] moves = {{"U", "U'", "U2", "D", "D'", "D2"},
	                        {"R", "R'", "R2", "L", "L'", "L2"},
	                        {"F", "F'", "F2", "B", "B'", "B2"}};

	static String[] orientations = {"", "Uw", "Uw2", "Uw'", "Fw2", "Fw2 Uw", "Fw2 Uw2", "Fw2 Uw'", "Rw", "Rw Uw", "Rw Uw2", "Rw Uw'",
			 						"Rw'", "Rw' Uw", "Rw' Uw2", "Rw' Uw'", "Fw'", "Fw' Uw", "Fw' Uw2", "Fw' Uw'", "Fw", "Fw Uw", "Fw Uw2", "Fw Uw'"};
	
	/* YEL: ORA BLU RED GRE
	 * WHI: ORA BLU RED GRE
	 * BLU: ORA WHI RED YEL
	 * GRE: ORA WHI RED YEL
	 * ORA: BLU YEL GRE WHI
	 * RED: BLU YEL GRE WHI
	 */
	
	public int[] cornerBuffer;
	public int[] edgeBuffer;
	public int[] parityEdge1;
	public int[] parityEdge2;
	
	public Scrambler(int[] cornerBuffer, int[] edgeBuffer, int[] parityEdge1, int[] parityEdge2) {
		this.cornerBuffer = cornerBuffer;
		this.edgeBuffer = edgeBuffer;
		this.parityEdge1 = parityEdge1;
		this.parityEdge2 = parityEdge2;
	}
	
	public int getAlgCount(String scramble) {
		Cube cube = new Cube(cornerBuffer, edgeBuffer, parityEdge1, parityEdge2);
		cube.scrambleCube(scramble);
		return cube.getAlgCount();
	}
	
	public String genScramble(int algCount) {;
		Cube cube = new Cube(cornerBuffer, edgeBuffer, parityEdge1, parityEdge2);
		
		SimpleTimer st = new SimpleTimer();
		st.start();
		boolean timedOut = false;
		while(!timedOut) {
			String scramble = Scrambler.genScramble();
			cube.scrambleCube(scramble);
			int algs = cube.getAlgCount();
			if(algs == algCount) {
				return scramble;
			}
			if(st.getTime() > 10.0) break;
			cube = new Cube(cornerBuffer, edgeBuffer, parityEdge1, parityEdge2);
		}
		return null;
	}
	
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
		
		scramble += orientations[(int)(Math.random() * 24)];
		
		return scramble;
	}
	
	private static int getRandomWithExclusion(int start, int end, int... exclude) {
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