/**
 * 
 */
package com.lebruns.steven.robotcar;

import com.lebruns.steven.robotcar.pipeline.MsgCapsule;
import com.lebruns.steven.robotcar.pipeline.Pipeline;

/**
 * @author Steven F. LeBrun
 *
 */
public class WatchDogController extends Controller
{
	/**
	 * The amount of time to wait between range finding when
	 * the car is in motion.
	 */
	static private final int deltaWait = 1000; // 250;
	
	static private final int ANGLE    = 0;   // Straight Ahead
	static private final int ATTEMPTS = 4;   // Average 4 readings
	static private final int RANGE    = 400; // 400 centimeters
	
	private boolean  isMoving = false;
	
	private Pipeline pipe  = null;
	private Pipeline sonar = null;

	/**
	 * @param type
	 */
	public WatchDogController()
	{
		super(PipeSink.WATCHDOG_SINK);
		
		setup();
	}

	/**
	 * @param type
	 * @param arg0
	 */
	public WatchDogController(Runnable arg0)
	{
		super(PipeSink.WATCHDOG_SINK, arg0);
		
		setup();
	}

	/**
	 * @param type
	 * @param arg0
	 */
	public WatchDogController(String arg0)
	{
		super(PipeSink.WATCHDOG_SINK, arg0);
		
		setup();
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public WatchDogController(ThreadGroup arg0, Runnable arg1)
	{
		super(PipeSink.WATCHDOG_SINK, arg0, arg1);
		
		setup();
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public WatchDogController(ThreadGroup arg0, String arg1)
	{
		super(PipeSink.WATCHDOG_SINK, arg0, arg1);

		setup();
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public WatchDogController(Runnable arg0, String arg1)
	{
		super(PipeSink.WATCHDOG_SINK, arg0, arg1);

		setup();
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public WatchDogController(ThreadGroup arg0, Runnable arg1, String arg2)
	{
		super(PipeSink.WATCHDOG_SINK, arg0, arg1, arg2);

		setup();
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public WatchDogController(ThreadGroup arg0, Runnable arg1, String arg2, long arg3)
	{
		super(PipeSink.WATCHDOG_SINK, arg0, arg1, arg2, arg3);

		setup();
	}

	private void setup()
	{
		PipeWork pipework = PipeWork.getInstance();
		
		pipe  = pipework.getPipeline(PipeSink.WATCHDOG_SINK);
		sonar = pipework.getPipeline(PipeSink.COMMAND_SINK);
		
		// Wake Up Sonar Sensor
		MsgCapsule left   = (MsgCapsule) new RangeMsg(RANGE, -45);
		MsgCapsule right  = (MsgCapsule) new RangeMsg(RANGE,  45);
		MsgCapsule ahead  = (MsgCapsule) new RangeMsg(RANGE,   0);
		
		try
		{
			sonar.put(left);
			sonar.put(right);
			sonar.put(ahead);
		}
		catch (Exception exc)
		{
			/** @todo Error Handling goes here. */
		}
		
		return;
		
	}   // end of setup()
	
	/* (non-Javadoc)
	 * @see com.lebruns.steven.robotcar.Controller#run()
	 */
	@Override
	public void run()
	{
		loop();
	}
	
	private void loop()
	{
		// Infinite Loop
		for ( ; ; )
		{
			if ( isMoving )
			{
				rangeFind();
				
				try
				{
					Thread.sleep(deltaWait);
				}
				catch (Exception exc)
				{
					// ignore exception
				}
				
				if ( ! pipe.isEmpty() )
				{
					// should not wait since Pipe is not Empty.
					getNextMessage();
				}
			}
			else
			{
				// May wait if there is not a message already in the Pipe.
				getNextMessage();
			}
		}
	}   // end of loop()
	
	/**
	 * Obtains and processes the next message in the Pipe.
	 * 
	 * If the Pipe is empty, this method will block and wait
	 * for the next message.
	 */
	private void getNextMessage()
	{
		MsgCapsule  message = null;

		message = pipe.waitForNextCommand();
		
		if ( message != null )
		{
			processMessage( message );
		}
		else
		{
			try
			{
				Thread.sleep(deltaWait);
			}
			catch (Exception exc)
			{
				// ignore exception
			}
		}	
	}   // end of getNextMessage()
	
	private void rangeFind()
	{
		// Build MsgCapsule to trigger Range Finding
		MsgCapsule ping = (MsgCapsule) new RangeMsg(RANGE, ANGLE, ATTEMPTS);
		
		try
		{
			sonar.put(ping);
		}
		catch ( Exception exc )
		{
			/** @todo Error handling goes here. */
		}
		
		return;
	}
	
	private void processMessage(MsgCapsule msg)
	{
		WatchDogMsg message = (WatchDogMsg) msg;
		
		isMoving = message.isCarMoving();
		
		return;
	}

}   // end of WatchDogController
