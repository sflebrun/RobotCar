/**
 * 
 */
package com.lebruns.steven.robotcar.menu;

import java.io.IOException;

import com.lebruns.steven.robotcar.LCDColor;
import com.lebruns.steven.robotcar.LCDModule;

/**
 * @author steven
 *
 */
public class MenuItemRestart extends MenuItem 
{

	/**
	 * @param label
	 * @param goUp
	 * @param goDown
	 * @param goRight
	 * @param goLeft
	 * @param ioDevice
	 */
	public MenuItemRestart(String label, MenuItem goUp, MenuItem goDown, MenuItem goRight, MenuItem goLeft,
			LCDModule ioDevice) {
		super(label, goUp, goDown, goRight, goLeft, ioDevice);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.lebruns.steven.robotcar.MenuItem#select()
	 */
	@Override
	public MenuItem select() 
	{
		String[] env = {"PATH=/bin:/usr/bin"};
		String   cmd = "/home/pi/RobotCar/RobotCar start";
		boolean  flag = false;
		
		try
		{
			Runtime.getRuntime().exec(cmd, env);
			flag = true;
			
		}
		catch ( IOException exc )
		{
	        lcd.clear();
	        lcd.home();
	        
	        lcd.setBackground(LCDColor.RED);
	        
	        lcd.write("Error:");
	        lcd.setCursorPosition(1, 0);
	        lcd.write("Unable to restart");
	 
		}
		
		if ( flag )
		{
			// New process started - terminate this one
			System.exit(0);
		}
		
		return this;
	}    // end of select()

}   // end of class MenuItemRestart
