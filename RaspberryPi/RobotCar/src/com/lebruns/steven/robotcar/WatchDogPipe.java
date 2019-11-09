/**
 * 
 */
package com.lebruns.steven.robotcar;

import com.lebruns.steven.robotcar.pipeline.MsgCapsule;
import com.lebruns.steven.robotcar.pipeline.Pipeline;

/**
 * @author Steven F. LeBrun
 *
 */
public class WatchDogPipe extends Pipeline
{

	/**
	 * 
	 */
	public WatchDogPipe()
	{
		return;
	}

	/* (non-Javadoc)
	 * @see com.lebruns.steven.robotcar.pipeline.Pipeline#is_supported(com.lebruns.steven.robotcar.pipeline.MsgCapsule)
	 */
	@Override
	public boolean is_supported(MsgCapsule msg)
	{
		boolean supported = ( msg instanceof WatchDogMsg ) ? true : false ;
		
		return supported;
	}

}   // end of class WatchDogPipe
