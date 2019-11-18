package application;

public class SimpleTimer {
	
	private double startTime;
	
	public void start()
	{
		startTime = System.currentTimeMillis();
	}
	
	public double getTime()
	{
		return (System.currentTimeMillis() - startTime) / 1000;
	}
}
