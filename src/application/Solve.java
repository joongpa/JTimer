package application;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Solve {
	private SimpleIntegerProperty solveNumber;
	private SimpleStringProperty displayedTime;
	private SimpleDoubleProperty realTime;
	private SimpleStringProperty mo3;
	//private SimpleIntegerProperty ao5;
	//private SimpleIntegerProperty ao12;
	
	public Solve(Integer solveNumber, Double realTime, Double mo3) {
		super();
		this.solveNumber = new SimpleIntegerProperty(solveNumber);
		this.realTime = new SimpleDoubleProperty(realTime);
		this.displayedTime = new SimpleStringProperty(Stopwatch.formatTime(realTime));
		this.mo3 = new SimpleStringProperty(Stopwatch.formatTime(mo3));
		//this.mo3 = mo3;
		//this.ao5 = ao5;
		//this.ao12 = ao12;
	}
	
	public String getMo3() {
		return mo3.get();
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
}
