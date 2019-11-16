package application;

import java.text.DecimalFormat;

public class Stopwatch {

	private double startTime;
	private double time;
	
	public boolean isStopped;
	
	private DecimalFormat df = new DecimalFormat("#0.00");
	
	public Stopwatch()
	{
		startTime = 0;
		time = 0;
		isStopped = true;
	}
	
	public void start()
	{
		startTime = System.currentTimeMillis();
		isStopped = false;
	}
	
	public void stop()
	{
		time = getTime();
		isStopped = true;
	}
	
	public double getTime()
	{
		return isStopped ? time / 1000 : (System.currentTimeMillis() - startTime) / 1000;
	}
	
	public String getTimeAsString()
	{
		double rawTime = getTime();
		double rawHours = rawTime / 3600;
		double rawMinutes = (rawHours % 1) * 60;
		double seconds = (rawMinutes % 1) * 60;
		
		int hours = (int)rawHours;
		int minutes = (int)rawMinutes;
		
		if(hours > 0) return hours + ":" + minutes + ":" + df.format(seconds);
		else if(minutes > 0) return minutes + ":" + df.format(seconds);
		return df.format(seconds);
	}
}
