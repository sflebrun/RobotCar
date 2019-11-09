/**
 * 
 */
package com.lebruns.steven.robotcar;

import com.lebruns.steven.robotcar.pipeline.MsgCapsule;

/**
 * @author Steven F. LeBrun
 *
 */
public class WatchDogMsg extends MsgCapsule
{
	private boolean  isMoving = false;
	
	/**
	 * 
	 */
	public WatchDogMsg(boolean carIsMoving)
	{
		isMoving = carIsMoving;
	}
	
	public boolean isCarMoving()
	{
		return isMoving;
	}

}   // end of class WatchDogMsg
