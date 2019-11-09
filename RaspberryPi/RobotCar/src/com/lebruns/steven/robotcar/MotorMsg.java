/**
 * 
 */
package com.lebruns.steven.robotcar;

/**
 * @author Steven F. LeBrun
 *
 */
public class MotorMsg extends CommandMsg
{
	private MotorCmd  cmdType;
	
	private int leftSpeed;
	private int rightSpeed;

	/**
	 * Constructor for STOP, FORWARD and BACKWARD motor messages.
	 * 
	 * @param speed The speed to run the motors at.  Negative speeds
	 *              result in backwards or reverse motion.
	 *              Legal range: [-255 to 255]
	 */
	public MotorMsg( int speed )
	{
		leftSpeed = rightSpeed = speed;
		
		if ( speed == 0 )
		{
			cmdType = MotorCmd.STOP;
		}
		else if ( speed > 0 )
		{
			cmdType = MotorCmd.FORWARD;
		}
		else
		{
			cmdType = MotorCmd.BACKWARD;
		}	
		
		speedCheck();
	}
	
	/**
	 * Constructor for a TURN motor message.
	 * 
	 * @param rightTurn Boolean flag.  Set to @b true to turn to the right.
	 *                  Set to @b false to turn to the left.
	 * @param speed     The speed to run the fastest wheels on the opposite 
	 *                  side of the turn.  Legal range: [-255 to 255]
	 * @param rate      The speed to run the slowest wheels on the side of
	 *                  the turn.  Legal range: [-255 to 255]
	 */
	public MotorMsg( boolean rightTurn, int speed, int rate )
	{
		cmdType = (rightTurn) ? MotorCmd.TURN_RIGHT : MotorCmd.TURN_LEFT ;
		
		int turnSpeed = speed - rate;
		
		if ( rightTurn )
		{
			leftSpeed  = speed;
			rightSpeed = turnSpeed; //rate;
		}
		else
		{
			leftSpeed  = turnSpeed; //rate;
			rightSpeed = speed;
		}
		
		speedCheck();
		
		System.out.println(String.format("Left Wheels @ %d, Right Wheels @ %d", leftSpeed, rightSpeed));
	}
	
	public MotorCmd  getCmdType()
	{
		return cmdType;
	}
	
	public int getRightSpeed()
	{
		return rightSpeed;
	}
	
	public int getLeftSpeed()
	{
		return leftSpeed;
	}
	
	private void speedCheck()
	{
		if ( rightSpeed > 255 )
		{
			rightSpeed = 255;
		}
		else if ( rightSpeed < -255 )
		{
			rightSpeed = -255;
		}
		
		if ( leftSpeed > 255 )
		{
			leftSpeed = 255;
		}
		else if (leftSpeed < -255 )
		{
			leftSpeed = -255;
		}
	}
	
	/**
	 * Syntax:
	 *    C:<MsgID>:SW;
	 *    C:<MsgID>:TW:1:<Speed>;
	 *    C:<MsgID>:TW:2:<LeftSpeed>:<RightSpeed>;
	 *    C:<MsgID>:TW:4:<LeftFront>:<RightFront>:LeftRear>:<RightRear>;
	 */
	public String getMessage()
	{
		 String message = COMMAND           + SEPARATOR +
			Integer.toString(getMsgId())    + SEPARATOR;
		 
		 switch ( cmdType )
		 {
		 case STOP:
			 message += getStopMessage();
			 break;
			 
		 case FORWARD:
		 case BACKWARD:
		 case TURN_LEFT:
		 case TURN_RIGHT:
			 message += getMoveMessage();
			 break;
			 
			 default:
				 /**
				  * @todo Add Error Handling here.
				  */
		 }
		 
		 
		 return message;

	}   // end of getMessage()
	
	private String getStopMessage()
	{
		String args = STOP + TERMINATOR;
		
		return args;
	}
	
//	private String getStraightMessage()
//	{
//		String args = MOVE + SEPARATOR + "1"          + SEPARATOR + 
//				Integer.toString(this.getLeftSpeed()) + TERMINATOR;
//		
//		return args;
//	}
//	
//	private String getTurnMessage()
//	{
////		String args = MOVE + SEPARATOR + "2"      + SEPARATOR +
////				Integer.toString(getLeftSpeed())  + SEPARATOR +
////				Integer.toString(getRightSpeed()) + TERMINATOR;
//		
//		String leftWheel  = Integer.toString(getLeftSpeed());
//		String rightWheel = Integer.toString(getRightSpeed());
//		
//		String args = MOVE + SEPARATOR + "4"        + SEPARATOR +
//				leftWheel  + SEPARATOR + rightWheel + SEPARATOR +
//				leftWheel  + SEPARATOR + rightWheel + TERMINATOR;
//		
//		return args;
//	}

	
	private String getMoveMessage()
	{
		int left_front   = Motor.convertSpeed(Wheels.LEFT_FRONT, getLeftSpeed());
		int left_rear    = Motor.convertSpeed(Wheels.LEFT_REAR,  getLeftSpeed());
		int right_front  = Motor.convertSpeed(Wheels.RIGHT_FRONT, getRightSpeed());
		int right_rear   = Motor.convertSpeed(Wheels.RIGHT_REAR,  getRightSpeed());
		
		String leftFront  = Integer.toString(left_front);
		String leftRear   = Integer.toString(left_rear);
		String rightFront = Integer.toString(right_front);
		String rightRear  = Integer.toString(right_rear);
		
		String args = MOVE  + SEPARATOR + "4"        + SEPARATOR +
				leftFront   + SEPARATOR + rightFront + SEPARATOR +
				leftRear    + SEPARATOR + rightRear  + TERMINATOR;
		
		return args;
	}
	
	public boolean isStopped()
	{
		if ( cmdType == MotorCmd.STOP ||
				(leftSpeed == 0 && rightSpeed == 0))
		{
			return true;
		}
		
		return false;
	}
}   // end of class MotorMsg
