/**
 * 
 */
package com.lebruns.steven.robotcar;

import com.lebruns.steven.robotcar.menu.MenuItem;
import com.lebruns.steven.robotcar.pipeline.MsgCapsule;
import com.lebruns.steven.robotcar.pipeline.Pipeline;

/**
 * @author Steven F. LeBrun
 *
 */
public class DisplayController extends Controller
{
	final private int  LCD_BUS     = 1;
	final private int  LCD_ADDRESS = 0x20;
	
	private LCDModule  lcd  = null;
	
	private MenuItem   topMenuItem     = null;
	private MenuItem   currentMenuItem = null;
	/**
	 * Display Mode determines whether the LCD Module is being used
	 * for Menu or Log display.  The functions of the buttons depend
	 * on the mode.
	 */
	private DisplayMode mode = DisplayMode.MENU;
	
	private Pipeline    logPipe = PipeWork.getInstance().getPipeline(this.getType());


	/**
	 * @param type
	 */
	public DisplayController()
	{
		super(PipeSink.DISPLAY_SINK);
		
		init();
	}

	/**
	 * @param type
	 * @param arg0
	 */
	public DisplayController(Runnable arg0)
	{
		super(PipeSink.DISPLAY_SINK, arg0);
		
		init();
	}

	/**
	 * @param type
	 * @param arg0
	 */
	public DisplayController(String arg0)
	{
		super(PipeSink.DISPLAY_SINK, arg0);
		
		init();
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public DisplayController(ThreadGroup arg0, Runnable arg1)
	{
		super(PipeSink.DISPLAY_SINK, arg0, arg1);
		
		init();
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public DisplayController(ThreadGroup arg0, String arg1)
	{
		super(PipeSink.DISPLAY_SINK, arg0, arg1);
		
		init();
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 */
	public DisplayController(Runnable arg0, String arg1)
	{
		super(PipeSink.DISPLAY_SINK, arg0, arg1);
		
		init();
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public DisplayController(ThreadGroup arg0, Runnable arg1, String arg2)
	{
		super(PipeSink.DISPLAY_SINK, arg0, arg1, arg2);
		
		init();
	}

	/**
	 * @param type
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public DisplayController(ThreadGroup arg0, Runnable arg1, String arg2, long arg3)
	{
		super(PipeSink.DISPLAY_SINK, arg0, arg1, arg2, arg3);
		
		init();
	}
	

	
	private void init()
	{
		try
		{
			lcd = new LCDModule(LCD_BUS, LCD_ADDRESS);
	            
			//MenuController  myMenu = new MenuController( lcd );
		}
		catch (Exception exc)
		{
			System.out.println("Unable to create LCD Module");
			System.out.println(exc.getMessage());
			exc.printStackTrace(System.out);
		}
		
		lcd.clear();
		
		topMenuItem = RobotCarMenuBuilder.buildMenu(lcd);

		return;
	}


	/* (non-Javadoc)
	 * @see com.lebruns.steven.robotcar.Controller#run()
	 */
	@Override
	public void run()
	{
		// Starts out in Menu Mode
		mode = DisplayMode.MENU;
		currentMenuItem = topMenuItem;
		topMenuItem.display();
		
		// Perform looping action
		loop();

	}
	
	private void loop()
	{
		byte buttons = 0x00;
		int  bits    =    0;
		
		MsgCapsule  message = null;
		
		// Infinite Loop for running thread
		for ( ; ; )
		{
			buttons = 0x00;
			bits    =    0;
			
			// -- First,  Check if any buttons have been pushed --
			try
			{
				buttons = getDisplay().getButtonStates();
				bits    = bitCount(buttons);
			}
			catch (Exception exc)
			{
				/** @todo Error Handling goes here. */
				break;
			}
			
			if ( bits > 1 )
			{
				processButtons();
				continue;
			}
			
			if ( bits == 1 )
			{
				processButton(buttons);
				continue;
			}
			
			// No Buttons were pushed.
			
			// -- Second, Check for incoming messages --
			message = logPipe.getNextCommand();
			
			if ( message != null )
			{
				processLogMessage(message);
				continue;
			}
			
			// Nothing to do this loop, so wait 0.1 second
			// and try again.
			try
			{
				Thread.sleep(100);
			}
			catch ( Exception exc )
			{
				// Ignore this exception and start next iteration
				// of the infinite loop.
			}
			
		}   // end of infinite loop
		
		return;
		
	}   // end of loop()
	
	private void processButtons()
	{
		// Multiple Buttons were pushed.
		// Go back to Menu Mode and go to the beginning of the menu tree.
		currentMenuItem = topMenuItem;
		
		// manually change mode to Logger so that the code will see a 
		// mode state change and display the current menu item.
		mode = DisplayMode.LOGGER;
		changeMode(DisplayMode.MENU);
				
		return;
	}
	
	private void processButton(byte buttons)
	{
		if ( mode == DisplayMode.MENU )
		{
			processMenuButton(buttons);
		}
		else if ( mode == DisplayMode.LOGGER )
		{
			processLoggerButton(buttons);
		}
		else
		{
			// Unknown/unexpected Display Mode
			/** @todo Error Handling goes here. */
		}
		
		return;
		
	}   // end of processButton()
	
	private void processLogMessage( MsgCapsule msg )
	{
		
	}
	
	private void processMenuButton( byte buttons )
	{
		boolean  displayItem = true;
		
		MenuItem nextItem = currentMenuItem;
		
        // One Button was pushed
        if ( (buttons & lcd.select_button) != 0)
        {
            nextItem = currentMenuItem.select();
            
            displayItem = currentMenuItem.displayMenuItem();
            
            if ( !currentMenuItem.isMenu() )
            {
            	changeMode( DisplayMode.LOGGER );
            }
        }
        else if ( (buttons & lcd.up_button) != 0)
        {
            nextItem = currentMenuItem.up();
        }
        else if ( (buttons & lcd.down_button) != 0)
        {
            nextItem = currentMenuItem.down();
        }
        else if ( (buttons & lcd.left_button) != 0)
        {
            nextItem = currentMenuItem.left();
        }
        else if ( (buttons & lcd.right_button) != 0)
        {
            nextItem = currentMenuItem.right();
        } 
        
        if ( nextItem != null )
        {
        	currentMenuItem = nextItem;
        }
        
        // If the previous Menu Item was selected then there is a possibility
        // that the select action results in switching out of Menu Mode and 
        // into Menu Mode.
        //
        // This is primarily done by a Navigation Menu Item
        if ( displayItem && mode == DisplayMode.MENU )
        {
        	currentMenuItem.display();
        }
        

	}
	
	private void processLoggerButton( byte buttons )
	{
		if ( (buttons & lcd.select_button) != 0 )
		{
			changeMode( DisplayMode.MENU);
		}
	}
	
	private void changeMode( DisplayMode newMode )
	{
		if ( newMode == mode )
		{
			// nothing changed.  Ignore
			return;
		}

		mode = newMode;
		
		if ( newMode == DisplayMode.MENU )
		{
			currentMenuItem.display();
		}
		else if ( newMode == DisplayMode.LOGGER )
		{
			/** @todo Display current log item here */
		}
		

	}
	
	private LCDModule getDisplay()
	{
		return lcd;
	}
	   
    static private int bitCount( byte data )
    {
        int count = 0;
        
        while ( data != 0 )
        {
            if ( (data & 0x01) != 0 )
            {
                ++count;
            }
            
            data >>= 1;
        }
        
        return count;
    }
  

}   // end of class DisplayController
