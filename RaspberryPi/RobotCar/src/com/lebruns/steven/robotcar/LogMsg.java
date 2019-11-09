package com.lebruns.steven.robotcar;

import java.util.Date;

public class LogMsg extends DisplayMsg
{
	/**
	 * Log Level or Log Priority.  Defines the importance of the message.
	 */
	private LogLevel level;
	
	/**
	 * Log Message that can be displayed on the LCD Display.  This message
	 * is limited to one line on the display, which, in turn, is limited 
	 * to a maximum of 16 characters.
	 */
	private String   displayMsg;
	
	/**
	 * Full Log Message that can be written to a log file.
	 */
	private String   fullMsg;
	
	/**
	 * Timestamp of when the Log Message was generated.
	 */
	private Date   timestamp = null;
	
	/**
	 * Boolean flag that denotes whether the Display Message should be
	 * displayed on the LCD Display.  @b True means to display on the
	 * LCD Display.  @b False means not to display on the LCD Display.
	 */
	private boolean  displayFlag;
	
	/**
	 * Boolean flag that denotes whether the Full Message should be written
	 * to the log file.  @b True means to write the Full Log Message to the
	 * Log File.  @b False means to not write the message to the Log File.
	 */
	private boolean  logFlag;
	
	

	public LogMsg(LogLevel priority, 
				  String   displayText, 
				  String   fileText)
	{
		init( priority, displayText, fileText, true, true);
	}
	

	public LogMsg(LogLevel priority, 
				  String   displayText, 
				  String   fileText,
				  boolean  display)
	{
		init( priority, displayText, fileText, display, true);
	}
	

	public LogMsg(LogLevel priority, 
				  String   displayText, 
				  String   fileText,
				  boolean  display,
				  boolean  store)
	{
		init( priority, displayText, fileText, display, store);
	}
	
	private void init( LogLevel priority,
					   String   displayText,
					   String   fileText,
					   boolean  display,
					   boolean  store )
	{
		level       = priority;
		displayMsg  = displayText.substring(0, 15);
		fullMsg     = fileText;
		displayFlag = display;
		logFlag     = store;
		
		timestamp   = new Date();
	}
	
	public LogLevel getPriority()
	{
		return level;
	}
	
	public String getDisplayMsg()
	{
		return displayMsg;
	}
	
	public String getLogMsg()
	{
		return fullMsg;
	}
	
	public boolean displayMsg()
	{
		return displayFlag;
	}
	
	public boolean logMsg()
	{
		return logFlag;
	}
	
	public Date getTimestamp()
	{
		return timestamp;
	}

}
