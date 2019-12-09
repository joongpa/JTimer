package application;

import java.util.Random;

public class Scrambler {
	static int length = 100; //19
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
	
	public static int getAlgCount(String scramble) {
		int[] cornerBuffer = {0,2,2};
		int[] edgeBuffer = {0,2,1};
		int[] thing = {0,1,2};
		Cube cube = new Cube(cornerBuffer, edgeBuffer, edgeBuffer, thing);
		cube.scrambleCube(scramble);
		return cube.getAlgCount();
	}
	
	public static String genScramble(int algCount) {
		int[] cornerBuffer = {0,2,2};
		int[] edgeBuffer = {0,2,1};
		int[] thing = {0,1,2};
		Cube cube = new Cube(cornerBuffer, edgeBuffer, edgeBuffer, thing);
		
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
			cube = new Cube(cornerBuffer, edgeBuffer, edgeBuffer, thing);
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