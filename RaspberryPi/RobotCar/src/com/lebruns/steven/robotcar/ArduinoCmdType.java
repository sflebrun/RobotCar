/**
 * 
 */
package com.lebruns.steven.robotcar;

/**
 * @author steven
 *
 *  <ArduinoCmdType> == "SW" for Stop Wheels
 *               "TW" for Turn Wheels
 *               "FR" for Find Range
 *               "SR" for Status Report {Not Implemented}

 */
public enum ArduinoCmdType
{
	StopWheels("SW"),
	TurnWheels("TW"),
	FindRange("FR"),
	StatusReport("SR"),
	UNKNOWN("Unknown");
	
	String cmdType;
	
	ArduinoCmdType( String value )
	{
		this.cmdType = value;
	}
	
	public String toString()
	{
		return this.cmdType;
	}
	

}
