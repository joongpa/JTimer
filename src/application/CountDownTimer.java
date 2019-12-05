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

public class CountDownTimer {
	private int startTime;
	public int time;
	public StringProperty formattedTimeProperty;
	private Timeline timeline;
	
	public CountDownTimer(int startTime)
	{
		this.startTime = startTime;
		time = startTime;
		formattedTimeProperty = new SimpleStringProperty(String.valueOf(startTime));
		
		timeline = new Timeline(new KeyFrame(Duration.millis(1000), e ->  {
			time -= 1;
			formattedTimeProperty.setValue(getFormattedTime());
			
			if(time <= 0) timeline.stop();
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
	}

	public void start()
	{
		timeline.play();
	}
	
	public void pause()
	{
		timeline.stop();
	}
	
	public void reset()
	{
		time = startTime;
		formattedTimeProperty.set(String.valueOf(time));
	}

	public int getTime()
	{
		return time;
	}
	
	public String getFormattedTime()
	{
		return String.valueOf(time);
	}
}
