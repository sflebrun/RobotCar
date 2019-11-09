/**
 * 
 */
package com.lebruns.steven.robotcar;

/**
 * @author Steven F. LeBrun
 *
 * The Motor Class provides static methods that allow for motor dependent
 * corrections.
 */
public class Motor
{
	static final int SLOWEST_SPEED = 0; //200;
	static final int FASTEST_SPEED = 255;
	
	static final double range = ((double) (FASTEST_SPEED - SLOWEST_SPEED)) / 
			                     (double)  FASTEST_SPEED;
	
	/**
	 * 
	 */
	public Motor()
	{
		return;
	}
	
	static public int convertSpeed( Wheels wheel, int rawSpeed )
	{
		int speed = 0;
		
		if ( rawSpeed > 0 )
		{
			speed = firstSpeedConvert(rawSpeed);
		}
		else if ( rawSpeed < 0 )
		{
			// Only convert positive or absolute speed.
			speed = -1*firstSpeedConvert( -1*rawSpeed );
		}
		return secondSpeedConvert(wheel, speed);
	}
	
	static private int firstSpeedConvert( int rawSpeed )
	{
		int speed = SLOWEST_SPEED + (int) (((double) rawSpeed) * range);
		
		return speed;
	}
	
	/**
	 * This method, when completed, will alter wheel speed so that if the
	 * speed of all four wheels start out the same (forward or backward
	 * motion), corrections will be made for each wheel speed so that the
	 * car will travel straight.
	 * 
	 * This is needed because at the same speed rate [0..255], each wheel
	 * may turn at a different rate.  This method compensates for that.
	 * @param wheel
	 * @param speed
	 * @return
	 */
	static private int secondSpeedConvert( Wheels wheel, int speed )
	{
		// Zero is stop.  All wheels have the same rate of spin; none.
		if ( speed == 0 )
		{
			return speed;
		}
		
		/**
		 * @todo Write code that will compensate for each wheel possibly
		 *       spinning at different rates for the same speed number.
		 */
		return speed;
	}   // end of secondSpeedConvert()
	
}   // end of class Motor
