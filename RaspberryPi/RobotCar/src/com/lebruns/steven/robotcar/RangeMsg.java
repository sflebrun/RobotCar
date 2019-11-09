/**
 * 
 */
package com.lebruns.steven.robotcar;

/**
 * @author Steven F. LeBrun
 *
 */
public class RangeMsg extends CommandMsg
{
	private final int MAX_RANGE = 400;
	private final int MAX_ATTEMPTS = 8;
	
	private final int DEFAULT_RANGE    = MAX_RANGE;
	private final int DEFAULT_ANGLE    = 0;
	private final int DEFAULT_ATTEMPTS = 4;
	
	private int maxRange;
	private int direction;
	private int attempts;
	
	public RangeMsg()
	{
		setup( DEFAULT_RANGE, DEFAULT_ANGLE, DEFAULT_ATTEMPTS);
	}
	
	public RangeMsg( int range )
	{
		setup( range, DEFAULT_ANGLE, DEFAULT_ATTEMPTS );
	}
	
	public RangeMsg( int range, int angle )
	{
		setup( range, angle, DEFAULT_ATTEMPTS );
	}

	/**
	 * Constructor for Range Finding Command.
	 * 
	 * @param range The maximum distance to search for an object, in cm.
	 *              Zero range means infinite max range.
	 *              Maximum range is 4 meters or 400 centimeters.
	 *              Legal Range: [0..400] 
	 * @param angle Direction to look in degrees.  Zero means straight ahead.
	 *              Positive angles mean look to right.
	 *              Negative angles mean look to the left.
	 *              Legal Range: [-90 to 90]
	 * @param nAttempts Number of range findings to average together for the
	 *                  final results.
	 *                  Legal Range: [1..8]
	 */
	public RangeMsg(int range, int angle, int nAttempts)
	{
		setup( range, angle, nAttempts);
	}
	
	public int getRange()
	{
		return maxRange;
	}
	
	public int getAngle()
	{
		return direction;
	}
	
	public int getAttempts()
	{
		return attempts;
	}
	
	/**
	 * 
	 * @param range     The maximum distance to search for an object, in cm.
	 *                  Zero range means infinite max range.
	 *                  Maximum range is 4 meters or 400 centimeters.
	 *                  Legal Range: [0..400] 
	 * @param angle     Direction to look in degrees.  Zero means straight ahead.
	 *                  Positive angles mean look to right.
	 *                  Negative angles mean look to the left.
	 *                  Legal Range: [-90 to 90]
	 * @param nAttempts Number of range findings to average together for the
	 *                  final results.
	 *                  Legal Range: [1..8]
	 */
	private void setup( int range, int angle, int nAttempts )
	{
		// Set maximum range : [0 .. MAX_RANGE] where zero is infinity.
		if ( range > MAX_RANGE )
		{
			maxRange = MAX_RANGE;
		}
		else if ( range < 0 )
		{
			maxRange = 0;
		}
		else
		{
			maxRange = range;
		}
		
		// Set Direction : -90 to 90 degrees
		if ( angle < -90 )
		{
			direction = -90;
		}
		else if ( angle > 90 )
		{
			direction = 90;
		}
		else
		{
			direction = angle;
		}
		
		// Set number of attempts to average
		if ( nAttempts > MAX_ATTEMPTS )
		{
			attempts = MAX_ATTEMPTS;
		}
		else if ( nAttempts < 1 )
		{
			attempts = 1;
		}
		else
		{
			attempts = nAttempts;
		}
		
		return;
		
	}   // end of setup()
	
	
	/**
	 * Syntax of Arduino Find Range Command is:
	 *   C:<MsgID>:FR:<angle>:<attempts>:<maxRange>;
	 */
	public String getMessage()
	{
		String message = COMMAND                + SEPARATOR +
				Integer.toString(getMsgId())    + SEPARATOR +
				this.RANGE                      + SEPARATOR + 
				Integer.toString(getAngle() )   + SEPARATOR +
				Integer.toString(getAttempts()) + SEPARATOR +
				Integer.toString(getRange())    + TERMINATOR;
		
		return message;
	}


}   // end of class RangeMsg
