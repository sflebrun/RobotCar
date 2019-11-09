/**
 * @file OutOfBandMessage.java
 *
 *
 */
/**
 * @copyright Statement goes here.
 */
package com.lebruns.steven.robotcar.pipeline;

/**
 * @author Steven F. LeBrun
 *
 */
public interface OutOfBandMessage
{
	/**
	 * Determines whether the out of bound message should result
	 * in the Pipeline being flushed of its regular messages without
	 * processing them.
	 * 
	 * @return Returns @b true if the receiving thread should flush the
	 *         Pipeline of incoming messages.  These messages will not be
	 *         processed.  The effect is the same as if the flushed messages
	 *         were never sent.  Returns @b false if the receiving thread
	 *         should continue processing the incoming messages after
	 *         processing this out of band message.
	 */
	public abstract boolean flushPipeline( ); 

}   // end of interface OutOfBandMessage
