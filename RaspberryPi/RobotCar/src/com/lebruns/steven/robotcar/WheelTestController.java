/**
 * Wheel Test -- Based on Blue Dot Navigator Controller.
 * 
 * This class test wheel speed by increasing raw speed each time the 
 * Blue Dot is pressed above the center and reduces the speed each time 
 * the Blue Dot is pressed below the center.
 * 
 * Wheels turn for only one second.
 */
package com.lebruns.steven.robotcar;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import com.lebruns.steven.robotcar.pipeline.MsgCapsule;
import com.lebruns.steven.robotcar.pipeline.PipelineIllegalParameterException;

/**
 * @author Steven F. LeBrun
 * 
 * Wheel Test Controller.
 * 
 * Used to determine at what speed the wheels start to turn based on the
 * voltage applied and the speed value sent.
 */
public class WheelTestController extends Controller
{
	/**
	 * UUID = 24c94f24-82fa-4c79-81c1-e641a5d9c688
	 * 
	 * The UUID is used to uniquely identify the Bluetooth service that we are
	 * creating and using to communicate with the BlueDot application running
	 * on an Android phone.
	 */
	private static UUID  uuid = new UUID(0x24c94f2482fa4c79L, 0x81c1e641a5d9c688L);
	
	public static final String BLUETOOTH_SCHEME = "btspp:";
			
	private LocalDevice               blueDevice = null;
	private StreamConnectionNotifier  blueEar    = null;
	
	private static final int  BLUEDOT_RELEASE    = 0;
	private static final int  BLUEDOT_PRESS      = 1;
	private static final int  BLUEDOT_MOVE       = 2;
	
	private       int  speed     = 0;
	private final int deltaSpeed = 10;

	/**
	 * @param type
	 */
	public WheelTestController()
	{
		super(PipeSink.BLUEDOT_SINK);
		
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param type
	 * @param arg0
	 */
	public WheelTestController(Runnable arg0)
	{
		super(PipeSink.BLUEDOT_SINK, arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param type
	 * @param arg0
	 */
	public WheelTestController(String arg0)
	{
		super(PipeSink.BLUEDOT_SINK, arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public WheelTestController(ThreadGroup arg0, Runnable arg1)
	{
		super(PipeSink.BLUEDOT_SINK, arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public WheelTestController(ThreadGroup arg0, String arg1)
	{
		super(PipeSink.BLUEDOT_SINK, arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public WheelTestController(Runnable arg0, String arg1)
	{
		super(PipeSink.BLUEDOT_SINK, arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public WheelTestController(ThreadGroup arg0, Runnable arg1, String arg2)
	{
		super(PipeSink.BLUEDOT_SINK, arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public WheelTestController(ThreadGroup arg0, Runnable arg1, String arg2, long arg3)
	{
		super(PipeSink.BLUEDOT_SINK, arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.lebruns.steven.robotcar.Controller#run()
	 */
	@Override
	public void run()
	{
		StreamConnection  client = null;
		
		// Create and register the Bluetooth Service
		if ( !init() )
		{
			/** @todo Error Handling goes here. */
			return;
		}
		
		try
		{
			// Outer Infinite Loop
			for ( ; ; )
			{
				// What for next client to connect to our service
				client = blueEar.acceptAndOpen();
				
				// We can only handle a single client at a time.  Therefore,
				// instead of spawning a thread to handle the client, we will
				// handle the client in this thread before waiting for the 
				// next client.
				processClient( client );
			
			
			}  // end of outer Infinite Loop
		}
		catch ( Exception Exc )
		{
			
		}
	}
	
	/**
	 * Initializes the Bluetooth Server.
	 * 
	 * The URL for the Bluetooth Server uses the scheme "btspp" because we are using RFCOMM
	 * communications.  If we wanted to use L2CAP communications, we would use the schema
	 * "btl2cap".
	 * 
	 * @return
	 */
	private boolean init()
	{
		try
		{
			// Make sure that the device running our Bluetooth Service is discoverable 
			blueDevice   = LocalDevice.getLocalDevice();
			blueDevice.setDiscoverable(DiscoveryAgent.GIAC);
			
			// Register our Bluetooth Service with the SDDB [Service Discovery Database].
			// This is done by opening a connector to the URL that defines the service,
			//
			String url = BLUETOOTH_SCHEME + "//localhost:" + 
			              uuid.toString().replaceAll("-", "") + 
			              ";name=RobotCar";
			
			blueEar = (StreamConnectionNotifier) Connector.open(url);
		}
		catch ( Exception exc )
		{
			System.out.println("Unable to initialize Bluetooth Service.");
			System.out.println(exc.getMessage());
			exc.printStackTrace(System.out);
			return false;
		}
		
		return true;
		
	}   // end of init()
	
	private void processClient( StreamConnection client )
	{
		BufferedReader    input = null;
		String            line  = null;
		
		try
		{
			// The client sends a line of text that terminates with an END OF LINE
			// sequence.  In order to easily read a line at a time, we will use
			// a BufferedReader object that already knows how to properly handle
			// line data.  Therefore, we will convert the StreamConnection into
			// a BufferedReader, which is a three step process.
		
			DataInputStream   incoming = client.openDataInputStream();
			InputStreamReader reader   = new InputStreamReader(incoming);
			
			input    = new BufferedReader( reader );
		}
		catch ( IOException exc )
		{
			/** @todo Error Handling goes here. */
		}
		
		// Inner Infinite loop
		for ( ; ; )
		{
			try
			{
				line = input.readLine();
				System.out.println(line);
			}
			catch ( IOException exc )
			{
				/** @todo Error Handling goes here. */
			}
			
			if ( !processLine(line) ) 
			{
				// End of Client Detected, exit inner loop;
				break;
			}
		}   // end of inner loop
		
		return;
		
	}   // end of processClient()
	
	/**
	 * Line Syntax:
	 *    <Operation>,<X Pos>,<Y Pos>\n
	 *    
	 *    <Operation>:
	 *      0 = Release
	 *      1 = Pressed
	 *      2 = Moved without releasing
	 */
	private boolean processLine( String line )
	{
		final int  OP_CODE   = 0;
		final int  X_POS     = 1;
		final int  Y_POS     = 2;
		final int  MAX_PARTS = 3;
		
		// Split up line into its three components
		String[]  parts = line.split(",");
		
		if ( parts.length != MAX_PARTS )
		{
			/** @todo Error Handling goes here */
		}
		
		int     operation = Integer.parseInt(parts[OP_CODE]);
		double  xpos      = Float.parseFloat(parts[X_POS]);
		double  ypos      = Float.parseFloat(parts[Y_POS]);
		
		
		switch (operation)
		{
		case BLUEDOT_RELEASE:
			stopCar();
			break;
			
		case BLUEDOT_PRESS:
		case BLUEDOT_MOVE:
			moveCar(xpos, ypos);
			break;
			
			default:
				/** @todo Error Handling goes here. */
		}
		return true;
	}   // end of processLine()

	private void stopCar()
	{
		MsgCapsule message = new MotorMsg(0);
		
		try
		{
			PipeWork.getInstance().getPipeline(PipeSink.COMMAND_SINK).put(message);
			
		} catch (PipelineIllegalParameterException exc)
		{
			// TODO Auto-generated catch block
			
		}
	}   // end of stopCar()
	
	private void moveCar( double xpos, double ypos )
	{
		if ( ypos > 0.0 )
		{
			speed += deltaSpeed;
		}
		else
		{
			speed -= deltaSpeed;
		}
		
		if ( speed > 255 )
		{
			speed = 255;
		}
		else if ( speed < -255 )
		{
			speed = -255;
		}
		
		System.out.println(String.format("Wheel Speed @ %d", speed));
		
		MsgCapsule message = new MotorMsg( speed );
		MsgCapsule stopMsg = new MotorMsg(0);

		
		try
		{
			PipeWork.getInstance().getPipeline(PipeSink.COMMAND_SINK).put(message);
			TimeUnit.SECONDS.sleep(1);	
			PipeWork.getInstance().getPipeline(PipeSink.COMMAND_SINK).put(stopMsg);
		} 
		catch ( Exception exc )
		{
			System.out.println("Exception detected when trying to move and stop car");
			System.out.println(exc.getMessage());
			exc.printStackTrace(System.out);
		}
//		catch (PipelineIllegalParameterException exc)
//		{
//			// TODO Auto-generated catch block
//			
//		}

	}   // end of moveCar()
	
}   // end of class BlueDotNavigator
