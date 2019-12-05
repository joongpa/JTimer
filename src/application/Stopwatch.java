package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;

public class Stopwatch {

	public DoubleProperty timeProperty;
	public StringProperty formattedTimeProperty;
	private Timeline timeline;
	//private boolean inProgress;
	public BooleanProperty inProgress;
	
	public Stopwatch()
	{
		timeProperty = new SimpleDoubleProperty(0);
		formattedTimeProperty = new SimpleStringProperty("0.00");
		//inProgress = false;
		inProgress = new SimpleBooleanProperty(false);
		
		timeline = new Timeline(new KeyFrame(Duration.millis(10), e ->  {
			timeProperty.set(timeProperty.doubleValue() + 0.01);
			formattedTimeProperty.setValue(formatTime(timeProperty.doubleValue()));
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
	}

	public void start()
	{
		timeline.play();
		//inProgress = true;
		inProgress.set(true);
	}
	
	public void pause()
	{
		timeline.stop();
	}
	
	public void stop()
	{
		//inProgress = false;
		inProgress.set(false);
	}
	
	public void reset()
	{
		timeProperty.set(0);
		formattedTimeProperty.set("0.00");
	}

	public double getTime()
	{
		return timeProperty.doubleValue();
	}
	
	public String getFormattedTime()
	{
		return formattedTimeProperty.get();
	}
	
	//public boolean inSolvingPhase()
	//{
	//	return inProgress;
	//}
	
	public static String formatTime(double time)
	{
		int hours = (int)(time / 3600);
		int minutes = (int)(time % 3600 / 60);
		double seconds = time % 60;
		
		if(hours > 0) return String.format("%d:%02d:%05.2f", hours, minutes, seconds);
		else if(minutes > 0) return String.format("%d:%05.2f", minutes, seconds);
		else return String.format("%.2f", seconds);
	}
}
