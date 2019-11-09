/**
 * 
 */
package com.lebruns.steven.robotcar;

/**
 * @author Steven F. LeBrun
 *
 * Message Types for Arduino
 *   C -- Command,  sent to the Arduino
 *   R -- Response, sent by the Arduino
 *   E -- Error,    sent by the Arduino
 */
public enum ArduinoMsgType
{
	COMMAND("C"),
	RESPONSE("R"),
	ERROR("E");
	
	String msgType;
	
	ArduinoMsgType(String type)
	{
		msgType = type;
	}
	
	public String toString()
	{
		return msgType;
	}
}
