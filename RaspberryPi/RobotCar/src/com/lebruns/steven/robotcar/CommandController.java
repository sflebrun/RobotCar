/**
 * 
 */
package com.lebruns.steven.robotcar;

import java.io.IOException;

import com.lebruns.steven.robotcar.pipeline.MsgCapsule;
import com.lebruns.steven.robotcar.pipeline.OutOfBandMessage;
import com.lebruns.steven.robotcar.pipeline.Pipeline;

/**
 * @author Steven F. LeBrun
 *
 */
public class CommandController extends Controller
{
	private MailBag   bag     = null;
	private USBDevice arduino = null;
	
	private boolean   keepRunning = true;
	
	private Pipeline  commands    = null;
	private Pipeline  watchDog    = null;
	
	private boolean   movingState = false;
	

	/**
	 * @param type
	 */
	public CommandController(USBDevice device, MailBag mailBag)
	{
		super(PipeSink.COMMAND_SINK);
		
		setup(device, mailBag);
	}

	/**
	 * @param type
	 * @param arg0
	 */
	public CommandController(USBDevice device, MailBag mailBag, Runnable arg0)
	{
		super(PipeSink.COMMAND_SINK, arg0);
		
		setup(device, mailBag);
	}

	/**
	 * @param type
	 * @param arg0
	 */
	public CommandController(USBDevice device, MailBag mailBag, String arg0)
	{
		super(PipeSink.COMMAND_SINK, arg0);
		
		setup(device, mailBag);
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public CommandController(USBDevice device, MailBag mailBag, ThreadGroup arg0, Runnable arg1)
	{
		super(PipeSink.COMMAND_SINK, arg0, arg1);
		
		setup(device, mailBag);
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public CommandController(USBDevice device, MailBag mailBag, ThreadGroup arg0, String arg1)
	{
		super(PipeSink.COMMAND_SINK, arg0, arg1);
		
		setup(device, mailBag);
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public CommandController(USBDevice device, MailBag mailBag, Runnable arg0, String arg1)
	{
		super(PipeSink.COMMAND_SINK, arg0, arg1);
		
		setup(device, mailBag);
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public CommandController(USBDevice device, MailBag mailBag, ThreadGroup arg0, Runnable arg1, String arg2)
	{
		super(PipeSink.COMMAND_SINK, arg0, arg1, arg2);
		
		setup(device, mailBag);
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public CommandController(USBDevice device, MailBag mailBag, ThreadGroup arg0, Runnable arg1, String arg2, long arg3)
	{
		super(PipeSink.COMMAND_SINK, arg0, arg1, arg2, arg3);
		
		setup(device, mailBag);
	}

	/* (non-Javadoc)
	 * @see com.lebruns.steven.robotcar.Controller#run()
	 */
	@Override
	public void run()
	{
		loop();
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
		
		PipeWork pipes = PipeWork.getInstance();
		
		commands = pipes.getPipeline(PipeSink.COMMAND_SINK);
		watchDog = pipes.getPipeline(PipeSink.WATCHDOG_SINK);
		
		return;
	}   // end of setup()
	
	
	/**
	 * Main Thread executable.  This is an infinite loop that waits
	 * for a new incoming command message and sends it to the Arduino
	 */
	public void loop()
	{
		MsgCapsule message = null;
		
		while ( keepRunning )
		{
			message = commands.waitForNextCommand();
			
			if ( message != null )
			{
				if ( message instanceof MotorMsg )
				{
					processMessage( message );
				}
				sendCommand(message);
			}
		}
		
		return;
	}
	
	/** 
	 * Set the termination flag to false so that on the next iteration
	 * of the run loop, the thread will stop executing.
	 */
	public void terminate()
	{
		keepRunning = false;
		
		PipeWork.getInstance().getPipeline(PipeSink.COMMAND_SINK).notifyAll();
		this.notifyAll();
	}
	
	/**
	 * Send a Command Message to the Arduino.
	 * 
	 * @param command The Command object to be sent.
	 */
	private void sendCommand(MsgCapsule cmd)
	{
		CommandMsg command = (CommandMsg) cmd;
		
		String message = command.getMessage();
		
		System.out.println(String.format("Command Msg: %s", message));
		
		try
		{
			arduino.write(message);
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// If this is an out of band message, check to see if we need
		// to flush the pipeline.
		if ( command instanceof OutOfBandMessage )
		{
			if ( ((OutOfBandMessage) command).flushPipeline() )
			{
				PipeWork pipes = PipeWork.getInstance();
				
				Pipeline commands = pipes.getPipeline(PipeSink.COMMAND_SINK);
				
				commands.flush();
			}
		}
		
		// Mark Command as having been sent and pass it along to
		// the Arduino Reader.
		command.hasBeenProcessed();;
		
		bag.insert(command);
		
		return;
		
	}   // end of sendCommand()
	
	
	/**
	 * Process Message checks the message to see whether it will cause the car
	 * to move or to stop.  If there is a state change, a message is sent to 
	 * the WatchDog Controller to start or stop checking ranges so the car
	 * does not bump into anything while moving.
	 * @param msg
	 */
	private void processMessage( MsgCapsule msg )
	{
		boolean  stopFlag = false;
		boolean  moveFlag = false;
		
		if ( msg instanceof MotorMsg )
		{
			MotorMsg message = (MotorMsg) msg;
			
			stopFlag = message.isStopped();
			moveFlag = true;
		}
		
		// Only need to send a message when there is a state change.
		if ( moveFlag )
		{
			if ( (stopFlag && movingState) || (!stopFlag && !movingState))
			{
				WatchDogMsg  monitorMsg = new WatchDogMsg(!stopFlag);
				
				movingState = !stopFlag;
				
				System.out.println("Moving State: " + (movingState ? "moving" : "stopped"));
				
				try
				{
					watchDog.put(monitorMsg);
				}
				catch ( Exception exc )
				{
					System.out.println("Attempt to signal WatchDog failed.");
					System.out.println(exc.getMessage());
					exc.printStackTrace(System.out);
				}
			}
		}
		
	}   // end of processMessage()

}   // end of class CommandController
