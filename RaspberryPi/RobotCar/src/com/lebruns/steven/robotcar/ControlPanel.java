/**
 * 
 */
package com.lebruns.steven.robotcar;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Steven F. LeBrun
 *
 * The Control Panel is a singleton class used to hold references
 * to all the Controller Threads.  Each Controller Thread uses
 * a ControllerType as its key.
 * 
 * The assumption is that there can only be one Controller Thread
 * for each Controller Type.
 */
public class ControlPanel
{
	static private ControlPanel  controlPanel = null;
	static private Object        lock         = new Object();
	
	private Map<ControllerType, Controller>  controllers = null;
	
	/**
	 * The default and only constructor for this class is private
	 * in following the Singleton Pattern.
	 *
	 */
	private ControlPanel()
	{
		controllers = new HashMap<ControllerType, Controller>();
		
		init();
	}
	
	/**
	 * Provides access to the singleton Control Panel instance.
	 * 
	 * The first call to this method will result in the creation of the
	 * Control Panel instance.  If multiple calls come before the Control 
	 * Panel instance is created, then the first one to obtain the valve lock
	 * will create the instance.
	 * 
	 * @return Returns a reference to the Control Panel Instance.
	 */
	static ControlPanel getInstance()
	{
		if ( controlPanel == null )
		{
			// Obtain valve lock in order to prevent multiple calls to
			// this method from creating multiple PipeWork objects.
			try
			{
			synchronized (lock)
			{
				if ( controlPanel == null )
				{
					controlPanel = new ControlPanel();
				}
			}  // end of lock
			}
			catch ( Exception exc )
			{
				System.out.println("Creating new Control Panel");
				System.out.println(exc.getMessage());
				exc.printStackTrace(System.out);
			}
		}
		
		return controlPanel;
		
	}   // end of getInstance()
	
	public synchronized boolean containsController(ControllerType key)
	{
		return controllers.containsKey(key);
	}
	
	public synchronized Controller getController(ControllerType key)
	{
		if ( ! containsController(key) )
		{
			return null;
		}
		
		return controllers.get(key);
	}
	
	/**
	 * Create and initialize each Controller used by the Robot Car.
	 * Once all the controllers have been created, start their threads.
	 */
	private void init()
	{
		// Create LCD Display Controller
		initDisplayController();
		
		// Create the Arduino Input and Output Controllers
		initArduinoControllers();
		
		// Create one or more Navigator Controller
		initNavigatorControllers();
		
		// Create the Watch Dog Controller
		initWatchDogController();
		
		// Start all the Controller Threads
		start();
		
		return;
		
	}   // end of init()
	
	/**
	 * The two Arduino Controllers, Command and Response, share resources.
	 * One is the Arduino device.  One controller writes to the Arduino and
	 * the other one reads from it.  There is also a mail bag that they share
	 * so that the writer can pass the Message Capsules, used to generate
	 * the Arduino commands, to the reader in case they are needed when
	 * handling the responses.
	 */
	private void initArduinoControllers()
	{
		// These two controllers share the USBDevice object that
		// performs the actual communications with the Arduino and
		// the Mail Bag for passing processed messages from the
		// Command Sender to the Response Handler
		MailBag   bag     = new MailBag();
		USBDevice arduino = new USBDevice();
		
		arduino.open();
		
		if ( ! arduino.isOpen() )
		{
			/** @todo Error Handling goes here. */
		}
		
		Controller arduino_sender =   new CommandController( arduino, bag, "ArduinoWriter");
		Controller arduino_receiver = new ResponseController( arduino, bag, "ArduinoReader" );
		
		// Initialize and Setup of Arduino Controllers goes here.
		
		// Store reference to controllers for future use.
		controllers.put(ControllerType.COMMAND, arduino_sender);
		controllers.put(ControllerType.RESPONSE, arduino_receiver);
		
		return;

	}   // end of initArduinoControllers()
	
	private void initDisplayController()
	{
		Controller LCD_display = new DisplayController("LCD Panel");
		
		controllers.put(ControllerType.DISPLAY, LCD_display);
		
		return;
		
	}   // end of initDisplayController()
	
	/**
	 * This method initiates all the Controllers that perform Navigation for
	 * the Robot Car.  These controllers send commands to the motors via the
	 * Command Controller which transforms the message into the Arduino 
	 * protocol and sends the messages to the Arduino.
	 * 
	 * At this time, we have only one Navigator and that is the one that
	 * uses the BlueDot app on an Android phone and is human controlled.
	 */
	private void initNavigatorControllers()
	{
		Controller bluedot_navigator = new BlueDotNavigatorController("BlueDotNavigator");
		
		// Replace BlueDotNavigatorController with WheelTestController when testing
		// range of power that will control the wheels.
//		Controller bluedot_navigator = new WheelTestController("WheelTester");
		
		controllers.put(ControllerType.BLUEDOT_NAVIGATOR, bluedot_navigator);
		
		return;
		
	}   // end of initNavigatorControllers()
	
	/**
	 * The WatchDog Controller issues Range Finder commands to the Arduino
	 * while the car is in motion.  The results of the Range Finder commands
	 * will result in the car stopping.
	 */
	private void initWatchDogController()
	{
		Controller watchdog = new WatchDogController("WatchDog");
		
		controllers.put(ControllerType.WATCHDOG, watchdog);

		
		return;
		
	}   // end of initWatchDogController()

	/**
	 * Start all the controllers at the same time.
	 * 
	 * Actually, the controllers are started one at a time, though they are
	 * all started in this method.
	 */
	private void start()
	{
		for ( Map.Entry<ControllerType, Controller> element : controllers.entrySet() )
		{
			element.getValue().start();
		}
		
		return;
	}

}   // end of class ControlPanel
