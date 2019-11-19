package application;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.IntStream;

import javafx.scene.control.TableView;

public class Average {
	
	private Solve[] solves;
	private int numSolves;
	private boolean isAverage;
	private int numUncountedSolves;
	private HashSet<Solve> uncountedSolves;
	
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
		this.isAverage = isAverage;
		setSolves(solve, timeList);
		
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

		Solve minValue = solves[0], maxValue = solves[0];
		double sum = 0;
		
		for(int i = 0; i < numUncountedSolves; i++)
		{
			for(int j = 1; j < solves.length; j++)
			{
				minValue = (solves[j].compareTo(minValue) < 0
							&& !uncountedSolves.contains(solves[j])) ? solves[j] : minValue;
				maxValue = (solves[j].compareTo(minValue) > 0
							&& !uncountedSolves.contains(solves[j])) ? solves[j] : maxValue;
			}
			uncountedSolves.add(minValue);
			uncountedSolves.add(maxValue);
		}
		if(Arrays.stream(solves).filter(s -> s.solveStateProperty.get() == SolveState.DNF).count() > numUncountedSolves) return -2;
		sum = Arrays.stream(solves).mapToDouble(Solve::getRealTime).sum();
		sum -= uncountedSolves.stream().mapToDouble(Solve::getRealTime).sum();
		double mean = sum / (numSolves - (2 * numUncountedSolves));
		return(mean);
	}
	
	public String getAverageString()
	{
		if(average == -1) return "-";
		if(average == -2) return "DNF";
		else return Stopwatch.formatTime(average);
	}
	
	public String toString()
	{
		String output = "";
		for(int i = 0; i < solves.length; i++)
		{
			output += (uncountedSolves.contains(solves[i])) ? "(" + solves[i].getDisplayedTime() + ")\n"
															: solves[i].getDisplayedTime();
		}
		return output;
	}
}
