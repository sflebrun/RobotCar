/**
 * 
 */
package com.lebruns.steven.robotcar;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.lebruns.steven.robotcar.pipeline.MsgCapsule;

/**
 * @author Steven F. LeBrun
 *
 */
public class ResponseController extends Controller
{
	final private String ARDUINO_SEPARATOR  = new String(":");
	final private String ARDUINO_TERMINATOR = new String(";");
	
	// Message Header
	final private int MSGTYPE = 0;
	final private int ID      = 1;
	final private int CMDTYPE = 2;
	
	// Stop Response
	// -- No Arguments --
//	final private int STOP_ARGS = 3;
	
	// Turn Wheels Response
//	final private int LEFT_FRONT  = 3;
//	final private int RIGHT_FRONT = 4;
//	final private int LEFT_REAR   = 5;
//	final private int RIGHT_REAR  = 6;
//	final private int TURN_WHEEL_ARGS = 7;
	
	// Find Range Response
	final private int DISTANCE = 3;
	final private int ANGLE    = 4;
	final private int FIND_RANGE_ARGS = 5;
	
	// Find Range constants
	final private int MIN_RANGE  = 10;  // centimeters

	
	private MailBag   bag     = null;
	private USBDevice arduino = null;


	/**
	 * @param type
	 */
	public ResponseController(USBDevice device, MailBag mailBag)
	{
		super(PipeSink.RESPONSE_SINK);
		
		setup(device, mailBag);
	}

	/**
	 * @param type
	 * @param arg0
	 */
	public ResponseController(USBDevice device, MailBag mailBag, Runnable arg0)
	{
		super(PipeSink.RESPONSE_SINK, arg0);

		setup(device, mailBag);

	}

	/**
	 * @param type
	 * @param arg0
	 */
	public ResponseController(USBDevice device, MailBag mailBag, String arg0)
	{
		super(PipeSink.RESPONSE_SINK, arg0);

		setup(device, mailBag);

	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public ResponseController(USBDevice device, MailBag mailBag, ThreadGroup arg0, Runnable arg1)
	{
		super(PipeSink.RESPONSE_SINK, arg0, arg1);

		setup(device, mailBag);

	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public ResponseController(USBDevice device, MailBag mailBag, ThreadGroup arg0, String arg1)
	{
		super(PipeSink.RESPONSE_SINK, arg0, arg1);

		setup(device, mailBag);

	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public ResponseController(USBDevice device, MailBag mailBag, Runnable arg0, String arg1)
	{
		super(PipeSink.RESPONSE_SINK, arg0, arg1);

		setup(device, mailBag);

	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public ResponseController(USBDevice device, MailBag mailBag, ThreadGroup arg0, Runnable arg1, String arg2)
	{
		super(PipeSink.RESPONSE_SINK, arg0, arg1, arg2);

		setup(device, mailBag);

	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public ResponseController(USBDevice device, MailBag mailBag, ThreadGroup arg0, Runnable arg1, String arg2, long arg3)
	{
		super(PipeSink.RESPONSE_SINK, arg0, arg1, arg2, arg3);

		setup(device, mailBag);

	}

	/* (non-Javadoc)
	 * @see com.lebruns.steven.robotcar.Controller#run()
	 */
	@Override
	public void run()
	{
		String message = null;
		
		// Infinite Loop
		for ( ; ; )
		{
			// Read bytes from Arduino until a full message has arrived
			message = receiveMessage();
			
			processMessage(message);
			
		}   // end of infinite loop.

	}
	
	/**
	 * Initializes communications with Arduino and prepares Command Controller
	 * to run in its infinite loop.
	 *
	 * Used by the constructors.
	 */
	private void setup(USBDevice device, MailBag mailBag)
	{
		arduino  = device;
		bag      = mailBag;
		
		bag.size();
		
		return;
	}   // end of setup()
	
	private String receiveMessage()
	{
		StringBuilder  buffer = new StringBuilder();
		
		String         letter = null;
		
		byte[] incoming = new byte[2];
		int    cnt      = 0;
		
		// Loop for reading in a full message from the Arduino.
		for ( ; ; )
		{
			// Get next character.
	        try
			{
				cnt = arduino.read(incoming, 1);
			} 
	        catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        if ( cnt == 1 )
	        {
	        	// we got a new character
	        	try
	    		{
	        		letter = new String(incoming, "UTF-8");
	    		}
	        	catch (UnsupportedEncodingException e)
	        	{
	        		// TODO Auto-generated catch block
	        		e.printStackTrace();
	    		}

	        	buffer.append(letter);
    		
	        	if ( letter.equals(ARDUINO_TERMINATOR) ) 
	        	{
	        		break;
	        	}
	        }   // end of append letter to buffer

		}   //  end of loop that reads a message

		
		return buffer.toString();
	}   // end of receiveMessage()
	
	private void processMessage( String message )
	{		
		String[] parts = message.split(ARDUINO_SEPARATOR);
		
		if ( parts.length < 3 )
		{
			/** @todo Error Handling goes here. */
		}
		
		int  msgId = Integer.parseInt(parts[ID]);
		
		if ( parts[MSGTYPE].equals(ArduinoMsgType.RESPONSE.toString()) )
		{
			processResponse(msgId, parts);
		}
		else if ( parts[MSGTYPE].equals(ArduinoMsgType.ERROR.toString()) )
		{
			processError(msgId, parts);
		}
		else
		{
			// Unexpected response
			/** @todo Error Handling Here */
		}
		
		return;
	}   // end of processMessage()
	
	private void processResponse(int msgId, String[] parts )
	{
		String cmdType = parts[CMDTYPE];
		
		MsgCapsule message = null;
		
		if ( bag.contains(msgId) )
		{
			message = bag.fetch(msgId);
		}
		
		if ( cmdType.equals(ArduinoCmdType.FindRange.toString()) )
		{
			processFindRange(msgId, parts, message);
		}
		else if ( cmdType.equals(ArduinoCmdType.StatusReport.toString()) )
		{
		}
		else if ( cmdType.equals(ArduinoCmdType.TurnWheels.toString()) )
		{
		}
		else if ( cmdType.equals(ArduinoCmdType.StatusReport.toString()) )
		{
			
		}
		else
		{
			// Unknown or unexpected response
			/** @todo Error Handling goes here. */
		}
	}   // end of processResponse()
	
	private void processFindRange(int id, String[] parts, MsgCapsule message)
	{
		if ( parts.length != FIND_RANGE_ARGS ) 
		{	
			// Wrong number of arguments.
			/** @todo Error Handling goes here. */
			return;
		}
		
		int range = Integer.parseInt(parts[DISTANCE]);
		int angle = Integer.parseInt(parts[ANGLE]);
		
		if ( range <= MIN_RANGE )
		{
			// Emergency - Stop Car
			stopCar(range, angle);
		}
		
		logRange(range, angle);
		
	}   // end of processFindRange()
	
	private void processError( int msgId, String[] parts )
	{
	
	}
	
	private void stopCar(int range, int angle)
	{
		MsgCapsule stopcar = new EmergencyStopMsg();
		
		try
		{
			PipeWork.getInstance().getPipeline(PipeSink.COMMAND_SINK).put(stopcar);
			
			/** 
			 * @todo Add code to tell Navigator Controllers to also stop or
			 *       that an obstacle has been reached.
			 */
		}
		catch (Exception exc)
		{
			/** @todo Error Handling goes here. */
		}
	}
	
	private void logRange( int range, int angle )
	{
		
	}

}   // end of class ResponseController
