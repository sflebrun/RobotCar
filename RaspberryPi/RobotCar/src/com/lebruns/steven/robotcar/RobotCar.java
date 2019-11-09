/**
 * 
 */
package com.lebruns.steven.robotcar;


/**
 * @author Steven F. LeBrun
 *
 */
public class RobotCar
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		RobotCar car = new RobotCar();
		
		boolean keepGoing = true;
		
		car.init();
		
		while ( keepGoing )
		{
			try
			{
				Thread.sleep(1000);
			}
			catch ( Exception exc )
			{
				
			}
		}

	}
	
	public RobotCar()
	{
		//init();
	}
	
	public void init()
	{
		System.out.println("Starting Robot Car Init.");
		// Initialize Inter-Thread Communications
		PipeWork.getInstance();

		// Initialize Thread Holders
		ControlPanel.getInstance();
	}
	


}   // end of class RobotCar
