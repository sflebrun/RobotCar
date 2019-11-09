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
public class DisplayPipe extends Pipeline
{

	/**
	 * 
	 */
	public DisplayPipe()
	{
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.lebruns.steven.robotcar.pipeline.Pipeline#is_supported(com.lebruns.steven.robotcar.pipeline.MsgCapsule)
	 */
	@Override
	public boolean is_supported(MsgCapsule msg)
	{
		boolean supported = false;
		
		if ( msg instanceof DisplayMsg )
		{
			supported = true;
		}
		
		return supported;
		
	}   // is_supported()

}
