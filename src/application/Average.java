package application;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.IntStream;

import javafx.scene.control.TableView;

public class Average implements Time {
	
	private Solve[] solves;
	private int numSolves;
	private int numUncountedSolves;
	private HashSet<Solve> uncountedSolves;
	private boolean isAverage;
	
	private double average;
	
	public Average(Solve[] solves, int numSolves, boolean isAverage)
	{
		uncountedSolves = new HashSet<Solve>();
		this.solves = solves;
		this.numSolves = numSolves;
		this.isAverage = isAverage;
		
		if(isAverage)
		{
			numUncountedSolves = (int)((numSolves < 10) ? 1 : Math.round(numSolves * 0.05));
		} else numUncountedSolves = 0;
		
		average = getAverage();
	}
	
	public Average(Solve solve, TableView<Solve> timeList, int numSolves, boolean isAverage)
	{
		uncountedSolves = new HashSet<Solve>();
		solves = new Solve[numSolves];
		this.numSolves = numSolves;
		setSolves(solve, timeList);
		this.isAverage = isAverage;
		
		if(isAverage)
		{
			numUncountedSolves = (int)((numSolves < 10) ? 1 : Math.round(numSolves * 0.05));
		} else numUncountedSolves = 0;
		
		average = getAverage();
	}
	
	private void setSolves(Solve solve, TableView<Solve> timeList) {
		try {
			int startIndex = IntStream.range(0, timeList.getItems().size())
					.filter(i -> solve == timeList.getItems().get(i))
					.findFirst()
					.orElse(-1)
					- numSolves + 1;
			for(int i = 0; i < solves.length; i++) solves[i] = timeList.getItems().get(i + startIndex);
		} catch (IndexOutOfBoundsException e) {
			solves = null;
		}
	}
	
	public double getAverage()
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
			String temp = Stopwatch.formatTime(solves[i].getRealTime());
			String tempDNF = "DNF[" + temp + "]";
			
			if(uncountedSolves.contains(solves[i]))
				output += "(" + (solves[i].solveStateProperty.get() == SolveState.DNF ? tempDNF : temp) + ")";
			else 
				output += (solves[i].solveStateProperty.get() == SolveState.DNF ? tempDNF : temp);
			output += "\n";
		}
		return output;
	}
}
