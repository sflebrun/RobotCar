/**
 * 
 */
package com.lebruns.steven.robotcar;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

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
 * The Blue Dot Navigator Controller is a Bluetooth Server whose client is the
 * Blue Dot App on an Android phone.  The client sends text lines to the service
 * specifying where a person has pressed, released, or moved their finger on
 * the Blue Dot circle.
 * 
 * The text line contains three fields, separated by commas and terminated with
 * a new line character.  The first field is an integer which determines if the
 * person pressed (1) the Blue Dot, removed their finger (0) from the Blue Dot, 
 * or moved their finger (2) on the Blue Dot.  The second field is a real
 * number denoting the X position in the circle.  The third field is a real
 * number denoting the Y position in the circle.
 * 
 * The range of the X and Y positions should be -1.0 to +1.0.  The app does 
 * send X and Y positions outside of the circle so this controller checks for
 * that and ignores any outside circle coordinates.
 * 
 * The Y axis is the vertical axis.  The X axis is the horizontal axis.
 *
 */
public class BlueDotNavigatorController extends Controller
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

	/**
	 * @param type
	 */
	public BlueDotNavigatorController()
	{
		super(PipeSink.BLUEDOT_SINK);
		
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param type
	 * @param arg0
	 */
	public BlueDotNavigatorController(Runnable arg0)
	{
		super(PipeSink.BLUEDOT_SINK, arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param type
	 * @param arg0
	 */
	public BlueDotNavigatorController(String arg0)
	{
		super(PipeSink.BLUEDOT_SINK, arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public BlueDotNavigatorController(ThreadGroup arg0, Runnable arg1)
	{
		super(PipeSink.BLUEDOT_SINK, arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public BlueDotNavigatorController(ThreadGroup arg0, String arg1)
	{
		super(PipeSink.BLUEDOT_SINK, arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public BlueDotNavigatorController(Runnable arg0, String arg1)
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
	public BlueDotNavigatorController(ThreadGroup arg0, Runnable arg1, String arg2)
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
	public BlueDotNavigatorController(ThreadGroup arg0, Runnable arg1, String arg2, long arg3)
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
			System.out.println("Bluetooth Service failed to start.  Is this program running as Root?");
			return;
		}
		
		try
		{
			// Outer Infinite Loop
			for ( ; ; )
			{
				// What for next client to connect to our service
				client = blueEar.acceptAndOpen();
				
				System.out.println("Connected to new Client");
				
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
		String url = "undefined";
		
		try
		{
			// Make sure that the device running our Bluetooth Service is discoverable 
			blueDevice   = LocalDevice.getLocalDevice();
			blueDevice.setDiscoverable(DiscoveryAgent.GIAC);
			
			// Register our Bluetooth Service with the SDDB [Service Discovery Database].
			// This is done by opening a connector to the URL that defines the service,
			//
			url = BLUETOOTH_SCHEME + "//localhost:" + 
			              uuid.toString().replaceAll("-", "") + 
			              ";name=RobotCar";
			
			blueEar = (StreamConnectionNotifier) Connector.open(url);
		}
		catch ( Exception exc )
		{
			System.out.println("Opening Bluetooth URL: " + url + " Failed.");
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
			System.out.println("Failed to connect to new Client");
			System.out.println(exc.getMessage());
			exc.printStackTrace(System.out);
		}
		
		try
		{
			line = input.readLine();
			System.out.println("First Line: " + line);
		}
		catch ( IOException exc )
		{
			System.out.println("Failed to read first line from Client");
			System.out.println(exc.getMessage());
			exc.printStackTrace(System.out);
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
				System.out.println("Failed to read command from Client");
				System.out.println(exc.getMessage());
				exc.printStackTrace(System.out);
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
			System.out.println("Error: parts length = " + parts.length + " too long.  Max: " + MAX_PARTS);
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
				System.out.println("Error: Unknown Command: " + line);
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
		final   double  delta = 0.1;

		// radius always zero or positive
		double  radius    = Math.sqrt( xpos*xpos + ypos*ypos );

		// are we still in the Blue Dot [circle]
		if ( radius > 1.0 )
		{
			// we have moved outside of the circle.
			// Set Radius to maximum value of 1.0
			radius = 1.0;
		}
		
		// Forward Speed is always posiive, range [0.0 .. 255.0]
		// The actual speed is the forward speed times the direction.
		double  forwardSpeed = (255.0*radius);
		
//		// It is possible for Blue Dot to return coordinates outside of the
//		// blue dot (circle) which will have a radius greater than 1.0.
//		if ( forwardSpeed > 255.0 )
//		{
//			forwardSpeed = 255.0;
//		}
		
		// Direction ==  +1.0 for forward, -1.0 for backward
		double  direction = (ypos >= 0 ) ? 1.0 : -1.0;
		
		int speed = (int) ( direction * forwardSpeed );
		
		System.out.println(String.format("Speed = %d", speed));
		
		MsgCapsule message = null;

		
		double pXPos = Math.abs(xpos);
		
		if ( pXPos > radius)
		{
			// xpos/pXPos is either 1.0 or -1.0
			xpos = (xpos/pXPos)*radius;
			
			pXPos = Math.abs(xpos);
		}
		
		if ( pXPos < delta )
		{
			// Go Straight (forward or backwards)
			message = new MotorMsg( speed );
		}
		else
		{
			// Turn (left or right)
			boolean  turnRight = (xpos > 0 );
			
			// The percentage that the slower wheel turn is based on the 
			// cosine of the angle made by the X and Y lengths.  We want to
			// use sin(a), which is equal to ypos/radius, but do not want to
			// use ypos because it may not be in the circle.  It is the xpos
			// that drives the rate of turn, hence using the cos(a) to 
			// determine the effective sin(a).
			//
			// Note rate of turn, sinTurn ranges from 1.0 to 0.0
			//
			// cos(a) = Adjacent/Hypotenuse
			// sin(a) = Opposite/Hypontenuse = 1 - cos(a)
			//
			// sinTurn range = [0.0 .. 1.0]
			double sinTurn = /* 1.0 - */ ( ((double) pXPos) / radius);
			
			double turnRate = speed * sinTurn;
			
			System.out.println(String.format("Turn Radius = %f", turnRate));
			
			message = new MotorMsg( turnRight, speed, (int) turnRate );
		}
		
		try
		{
			PipeWork.getInstance().getPipeline(PipeSink.COMMAND_SINK).put(message);
			
		} catch (PipelineIllegalParameterException exc)
		{
			// TODO Auto-generated catch block
			
		}

	}   // end of moveCar()
	
}   // end of class BlueDotNavigator
