/**
 * 
 */
package com.lebruns.steven.robotcar;

import com.lebruns.steven.robotcar.pipeline.OutOfBandMessage;

/**
 * @author Steven F. LeBrun
 *
 * The Emergency Stop Message is meant to be sent when it is necessary to 
 * bring the Robot Car to an immediate halt.  Primarily, this is so that the
 * software can stop movement of the Robot Car before it runs into something.
 * 
 * An Emergency Stop Message is sent in the out of band pipe so that it can
 * bypass any message capsules already in the pipeline.
 */
public class EmergencyStopMsg extends CommandMsg implements OutOfBandMessage
{

	/**
	 * 
	 */
	public EmergencyStopMsg()
	{
	}

	/**
	 * An Emergency Stop Message means to stop everything.  That includes the
	 * flushing of any commands still in the pipeline without processing those
	 * messages.
	 * 
	 * @return Always returns @b true so that the command pipeline gets flushed,
	 * 
	 * @see com.lebruns.steven.robotcar.pipeline.OutOfBandMessage#flushPipeline()
	 */
	@Override
	public boolean flushPipeline()
	{
		return true;
	}

	/**
	 * Build a command message that tells the Arduino to stop the wheels from
	 * turning.
	 * 
	 * @see com.lebruns.steven.robotcar.CommandMsg#getMessage()
	 */
	@Override
	public String getMessage()
	{
		String args = 
				COMMAND                         + SEPARATOR +
				Integer.toString(getMsgId())    + SEPARATOR +
				STOP                            + TERMINATOR;
		
		return args;
	}

}
