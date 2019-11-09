/**
 * 
 */
package com.lebruns.steven.robotcar;

/**
 * @author Steven F. LeBrun
 *
 * PipeSink is an enumeration that identifies the consumer of each 
 * Pipeline.
 */
public enum PipeSink
{
	DISPLAY_SINK,
	MASTER_SINK,
	WATCHDOG_SINK,
	BLUEDOT_SINK,
	COMMAND_SINK,
	RESPONSE_SINK;
}
