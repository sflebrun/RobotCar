/**
 * 
 */
package com.lebruns.steven.robotcar;

import com.lebruns.steven.robotcar.menu.MenuItem;
import com.lebruns.steven.robotcar.menu.MenuItemDisplayOff;
import com.lebruns.steven.robotcar.menu.MenuItemIPAddress;
import com.lebruns.steven.robotcar.menu.MenuItemMenu;
import com.lebruns.steven.robotcar.menu.MenuItemRestart;
import com.lebruns.steven.robotcar.menu.MenuItemTopMenu;

/**
 * @author Steven F. LeBrun
 *
 */
public class RobotCarMenuBuilder
{
	LCDModule  lcd = null;
	
	public static MenuItem buildMenu(LCDModule ioDevice)
	{
		RobotCarMenuBuilder menuBuilder = new RobotCarMenuBuilder(ioDevice);
		
		return menuBuilder.createMenuTree();
	}

	/**
	 * 
	 */
	private RobotCarMenuBuilder( LCDModule ioDevice )
	{
		lcd = ioDevice;
	}
	
	private MenuItem createMenuTree()
	{
		MenuItem First  = buildGeneralMenu();
		MenuItem Second = buildNavigationMenu();
		MenuItem Third  = buildStatusMenu();
		
		First.setRight(Second);
		First.buildNextLine();
		
		Second.setLeft(First);
		Second.setRight(Third);
		Second.buildNextLine();
		
		Third.setLeft(Second);
		Third.buildNextLine();
		
		return First;
	}
	
	private MenuItem buildGeneralMenu()
	{
		MenuItem  Top  = new MenuItemTopMenu("General",
				null, null, null, null, lcd);
		
        MenuItem  stopMotor  = new MenuItemMenu("STOP Wheels!", 
                Top, null, null, null, 
                lcd );
        MenuItem  Hide = new MenuItemDisplayOff("Turn Display Off", 
        		stopMotor, null, null, null, 
        		lcd );
        MenuItem  Restart = new MenuItemRestart("Restart Software",
        		Hide, null, null, null,
        		lcd );
        MenuItem  Stop = new MenuItemTopMenu("Quit", 
        		Restart, null, null, null, 
        		lcd );

        Top.setDown(stopMotor);
        Top.buildNextLine();
        
        stopMotor.setDown(Hide);
        stopMotor.buildNextLine();

        Hide.setDown(Restart);
        Hide.buildNextLine();

        Restart.setDown(Stop);
        Restart.buildNextLine();

        return Top;
		
	}   // end of buildGeneralMenu()
	
	private MenuItem buildNavigationMenu()
	{
		MenuItem blueDot = new MenuItemBlueDotNavigation(
				"BlueDot Navigation",
				null, null, null, null, lcd);
		
		MenuItem Top = new MenuItemTopMenu("Navigation",
				null, blueDot, null, null,
				lcd);
		
		blueDot.setUp(Top);
		blueDot.buildNextLine();
		
		return Top;
	}
	
	private MenuItem buildStatusMenu()
	{
        MenuItem  IPAddress = buildIPAddressMenu();

        
        MenuItem  Top  = new MenuItemTopMenu( "Status",
                                               null,
                                               IPAddress,
                                               null,
                                               null,
                                               lcd );
        
        IPAddress.setUp(Top);
        IPAddress.buildNextLine();
       
        return Top;

	}   // end of buildStatusMenu()

    /**
     * Builds Menu with sub-menu:
     * 
     *     "IP Addresses"  -->  "Display eth0"
     *                                |
     *                                V
     *                          "Display wlan0"
     * @return
     * @throws Exception
     */
    private MenuItem buildIPAddressMenu()
    {
        MenuItem  IPAddress = new MenuItemMenu("IP Addresses",
                                                null, null, null, null,
                                                lcd);
 
        try
        {
        	MenuItem  Wlan = new MenuItemIPAddress( "wlan0",
        			null,
        			null,
        			null,
        			null,
        			lcd );

        	MenuItem  Eth  = new MenuItemIPAddress( "eth0",
        			null,
        			Wlan,
        			null,
        			IPAddress,
        			lcd);

        	Wlan.setUp(Eth);
        	Wlan.buildNextLine();
        
        	IPAddress.setRight(Eth);
        	IPAddress.buildNextLine();
        }
        catch (Exception exc)
        {
        	/** @todo Error Handling goes here. */
        }
        
        return IPAddress;
        
    }   // end of buildIPAddressMenu()

}   // end of class RobotCarMenuBuilder
