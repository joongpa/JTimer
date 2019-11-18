package application;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Solve implements Comparable<Solve> {
	private SimpleIntegerProperty solveNumber;
	private SimpleStringProperty displayedTime;
	public SimpleDoubleProperty realTime;
	
	public ObjectProperty<SolveState> solveStateProperty;
	
	public Solve(Integer solveNumber, Double realTime) {
		super();
		solveStateProperty = new SimpleObjectProperty<SolveState>(SolveState.OK);
		
		this.solveNumber = new SimpleIntegerProperty(solveNumber);
		this.realTime = new SimpleDoubleProperty(realTime);
		this.displayedTime = new SimpleStringProperty();
		resetDisplay();
		
		solveStateProperty.addListener((o, oldval, newVal) -> {
			switch(solveStateProperty.get())
			{
				case OK:
					resetDisplay();
					break;
				case PLUS2:
					displayedTime.set(Stopwatch.formatTime(realTime.doubleValue() + 2) + "+");
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

	public String getDisplayedTime() {
		return displayedTime.get();
	}
	
	public void resetDisplay()
	{
		displayedTime.set(Stopwatch.formatTime(realTime.doubleValue()));
	}
	
	public boolean equals(Solve other)
	{
		if(realTime == other.realTime && solveNumber == other.solveNumber) return true;
		else return false;
	}

	@Override
	public int compareTo(Solve o) {
		if(solveStateProperty.get() == SolveState.DNF && o.solveStateProperty.get() == SolveState.DNF) return 0;
		if(solveStateProperty.get() == SolveState.DNF) return Integer.MAX_VALUE;
		if(o.solveStateProperty.get() == SolveState.DNF) return Integer.MIN_VALUE;
		
		int penalty = solveStateProperty.get() == SolveState.PLUS2 ? 2 : 0;
		int otherPenalty = o.solveStateProperty.get() == SolveState.PLUS2 ? 2 : 0;
		return (int)(100 * (getRealTime() + penalty - (o.getRealTime() + otherPenalty)));
	}
	
	public String toString()
	{
		return displayedTime.get();
	}
}
