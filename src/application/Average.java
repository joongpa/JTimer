package application;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.IntStream;

import javafx.scene.control.TableView;

public class Average implements Time, Comparable<Solve> {
	
	private Solve[] solves;
	private int numSolves;
	private int numUncountedSolves;
	private HashSet<Solve> uncountedSolves;
	private boolean isAverage;
	private boolean dnfMatters;
	
	private double average;
	
	public Average(Solve[] solves, boolean isAverage, boolean dnfMatters)
	{
		uncountedSolves = new HashSet<Solve>();
		this.solves = solves;
		this.numSolves = solves.length;
		this.isAverage = isAverage;
		this.dnfMatters = dnfMatters;
		
		if(isAverage)
		{
			numUncountedSolves = (int)((numSolves < 10) ? 1 : Math.round(numSolves * 0.05));
		} else numUncountedSolves = 0;
		
		average = getAverage();
	}
	
	public Average(Solve solve, TableView<Solve> timeList, int numSolves, boolean isAverage, boolean dnfMatters)
	{
		uncountedSolves = new HashSet<Solve>();
		solves = new Solve[numSolves];
		this.numSolves = numSolves;
		setSolves(solve, timeList);
		this.isAverage = isAverage;
		this.dnfMatters = dnfMatters;
		
		if(isAverage)
		{
			numUncountedSolves = (int)((numSolves < 10) ? 1 : Math.round(numSolves * 0.05));
		} else numUncountedSolves = 0;
		
		average = getAverage();
	}
	
	private void setSolves(Solve solve, TableView<Solve> timeList) {
		try {
			int startIndex = findIndex(solve, timeList) - numSolves + 1;
			for(int i = 0; i < solves.length; i++) solves[i] = timeList.getItems().get(i + startIndex);
		} catch (IndexOutOfBoundsException e) {
			solves = null;
		}
	}
	
	public static int findIndex(Solve solve, TableView<Solve> timeList)
	{
		int index = IntStream.range(0, timeList.getItems().size())
				.filter(i -> solve == timeList.getItems().get(i))
				.findFirst()
				.orElse(-1);
		
		return index;
	}
	
	public double getAverage() //fix this
	{
		if(solves == null) return -1;

		
		Solve[] countedSolves = new Solve[solves.length];
		for(int i = 0; i < solves.length; i++) countedSolves[i] = solves[i];
		Arrays.sort(countedSolves);
		double mean = 0;
		
		for(int i = 0; i < countedSolves.length; i++)
		{
			if(i < numUncountedSolves || i >= countedSolves.length - numUncountedSolves) {
				uncountedSolves.add(countedSolves[i]);
				continue;
			}
			else if(countedSolves[i].solveStateProperty.get() == SolveState.DNF && !uncountedSolves.contains(countedSolves[i])) {
				return -2;
			}
			mean += countedSolves[i].getRealTime();
		}
		mean /= solves.length - (2 * numUncountedSolves);
		return mean;
	}
	
	@Override
	public String getDisplayedTime()
	{
		if(average == -1) return "-";
		if(average == -2) return "DNF";
		else return Stopwatch.formatTime(average);
	}
	
	@Override
	public String toString()
	{
		if(solves == null) return "";
		String output = (isAverage) ? "Average" : "Mean";
		output += " of " + numSolves + ":   " + getDisplayedTime() + "\n";

		for(int i = 0; i < solves.length; i++)
		{
			output += i + 1 + ".\t";
			String better = solves[i].toString();
			
			if(uncountedSolves.contains(solves[i]))
				output += "(" + better + ")   " + solves[i].getScramble();
			else 
				output += better + "   " + solves[i].getScramble();
			output += "\n";
		}
		return output;
	}

	@Override
	public int compareTo(Solve o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
