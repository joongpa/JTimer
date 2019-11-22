package application;

public class TimeRow {
	public Solve solve;
	public Average mean3;
	public Average avg5;
	public Average avg12;
	
	public TimeRow(Solve solve)
	{
		this.solve = solve;
		this.mean3 = null;
		this.avg5 = null;
		this.avg12 = null;
	}
	
	public Solve getSolve() {
		return solve;
	}
	
	public Average getMean3() {
		return mean3;
	}
	
	public Average getAvg5() {
		return avg5;
	}
	
	public Average getAvg12() {
		return avg12;
	}
}
