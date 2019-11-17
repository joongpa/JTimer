package application;

public class Stopwatch {

	private double startTime;
	public boolean inProgress;
	
	public Stopwatch()
	{
		inProgress = false;
	}
	
	public void start()
	{
		startTime = System.currentTimeMillis();
		inProgress = true;
	}
	
	public double getTime()
	{
		return (System.currentTimeMillis() - startTime) / 1000;
	}
	
	public String getTimeAsString()
	{
		return formatTime((System.currentTimeMillis() - startTime) / 1000);
	}
	
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
