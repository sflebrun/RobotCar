/**
 * 
 */
package com.lebruns.steven.robotcar;

import com.lebruns.steven.robotcar.pipeline.MsgCapsule;

/**
 * @author Steven F. LeBrun
 *
 * Base class for message capsules sent to the Command Controller.  There
 * will be derived classes for specific commands.
 */
public abstract class CommandMsg extends MsgCapsule
{
	protected final String COMMAND  = "C";
	protected final String RESPONSE = "R";
	protected final String ERROR    = "E";

	protected final String STOP     = "SW";
	protected final String MOVE     = "TW";
	protected final String RANGE    = "FR";
	protected final String STATUS   = "SR";
	
	protected final String SEPARATOR  = ":";
	protected final String TERMINATOR = ";";
	/**
	 * 
	 */
	public CommandMsg()
	{
		
	}
	
	public abstract String getMessage();

}
