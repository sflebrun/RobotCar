/**
 * 
 */
package com.lebruns.steven.robotcar;

/**
 * @author Steven F. LeBrun
 *
 * The Log Level enumeration defines the different levels of log messages.
 * The higher the priority level, the more important the message is.
 */
public enum LogLevel
{	VERBOSE(1),
	DEBUG(3),
	INFORMATION(5),
	WARNING(7),
	ERROR(10);
	
	private int level;
	
	private LogLevel( int priority )
	{
		level = priority;
	}
	
	public int getPriority()
	{
		return level;
	}
}   // end of enum LogLevel
