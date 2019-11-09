/**
 * 
 */
package com.lebruns.steven.robotcar;

/**
 * @author steven
 *
 *  <CmdType> == "SW" for Stop Wheels
 *               "TW" for Turn Wheels
 *               "FR" for Find Range
 *               "SR" for Status Report {Not Implemented}

 */
public enum ResponseType
{
	StopWheels("SW"),
	TurnWheels("TW"),
	FindRange("FR"),
	StatusReport("SR"),
	UNKNOWN("Unknown");
	
	String type;
	
	ResponseType( String value )
	{
		this.type = value;
	}
	
	public String toString()
	{
		return this.type;
	}
	

}
