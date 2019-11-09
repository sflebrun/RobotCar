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
public class CommandPipe extends Pipeline
{

	/**
	 * 
	 */
	public CommandPipe()
	{
		
	}

	/* (non-Javadoc)
	 * @see com.lebruns.steven.robotcar.pipeline.Pipeline#is_supported(com.lebruns.steven.robotcar.pipeline.MsgCapsule)
	 */
	@Override
	public boolean is_supported(MsgCapsule msg)
	{
		boolean supported = false;
		
		if ( msg instanceof CommandMsg )
		{
			supported = true;
		}
		return supported;
	}

}   // end of class CommandPipe
