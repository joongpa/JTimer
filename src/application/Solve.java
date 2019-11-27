package application;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Solve implements Comparable<Solve>, Time {
	private SimpleIntegerProperty solveNumber;
	private SimpleStringProperty displayedTime;
	private String scramble;
	public SimpleDoubleProperty realTime;
	public Average mo3;
	public Average ao5;
	public Average ao12;
	public int numAlgs;
	
	public ObjectProperty<SolveState> solveStateProperty;
	
	public Solve(Integer solveNumber, Double realTime, String scramble, int numAlgs) {
		super();
		solveStateProperty = new SimpleObjectProperty<SolveState>(SolveState.OK);
		
		this.solveNumber = new SimpleIntegerProperty(solveNumber);
		this.realTime = new SimpleDoubleProperty(realTime);
		this.displayedTime = new SimpleStringProperty();
		this.scramble = scramble;
		this.numAlgs = numAlgs;
		resetDisplay();
		
		solveStateProperty.addListener((o, oldval, newVal) -> {
			if(oldval == SolveState.PLUS2) {
				this.realTime.set(this.realTime.get() - 2);
			}

			switch(newVal)
			{
				case OK:
					resetDisplay();
					break;
				case PLUS2:
					this.realTime.set(this.realTime.get() + 2);
					displayedTime.set(Stopwatch.formatTime(this.realTime.get()) + "+");
					break;
				case DNF:
					displayedTime.set("DNF");
			}
		});
	}
	
	public Solve getThis() {
		return this;
	}

	public Integer getSolveNumber() {
		return solveNumber.get();
	}
	
	public Double getRealTime() {
		return realTime.get();
	}
	
	public void setSolveNumber(Integer x) {
		solveNumber.setValue(x);
	}

	@Override
	public String getDisplayedTime() {
		return displayedTime.get();
	}
	
	public void resetDisplay()
	{
		displayedTime.set(Stopwatch.formatTime(realTime.doubleValue()));
	}

	@Override
	public int compareTo(Solve o) {
		if(solveStateProperty.get() == SolveState.DNF) {
			if(o.solveStateProperty.get() == SolveState.DNF)
				return 0;
			else return Integer.MAX_VALUE;
		} else if(o.solveStateProperty.get() == SolveState.DNF) return Integer.MIN_VALUE;
		
		return (int)(1000 * (getRealTime() - (o.getRealTime())));
	}
	
	public String toString()
	{
		if(solveStateProperty.get() == SolveState.DNF) return "DNF[" + Stopwatch.formatTime(getRealTime()) + "]";
		else return displayedTime.get();
	}
	
	public String getScramble()
	{
		return scramble;
	}
}
