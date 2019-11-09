/**
 * 
 */
package com.lebruns.steven.robotcar.menu;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.lebruns.steven.robotcar.LCDModule;

/**
 * @author Steven F. LeBrun
 *
 */
public class MenuItemIPAddress extends MenuItem
{
    private String  ipAddress = null;

    /**
     * @param goUp
     * @param goDown
     * @param goRight
     * @param goLeft
     * @param ioDevice
     */
    public MenuItemIPAddress( String interfaceName,
                              MenuItem goUp, 
                              MenuItem goDown, 
                              MenuItem goRight, 
                              MenuItem goLeft, 
                              LCDModule ioDevice)
        throws Exception
    {
        super("Display Address", goUp, goDown, goRight, goLeft, ioDevice);
        
        // Obtain IP (Inet4) Address for this Interface.
 
        NetworkInterface network   = NetworkInterface.getByName(interfaceName);
        
        if ( network != null )
        {        
        	Enumeration<InetAddress>      addresses = network.getInetAddresses();
        
        	name = interfaceName;
        	ipAddress = "<unknown>";
        
        	while (addresses.hasMoreElements())
        	{
        		InetAddress address = addresses.nextElement();
        		if ( address instanceof Inet4Address )
        		{
        			name      = network.getDisplayName();
        			ipAddress = address.getHostAddress();
        			break;
        		}
        	}
        }
        else
        {
        	ipAddress = "<not available>";
        	name      = interfaceName;
        }
        
    }

    /* (non-Javadoc)
     * @see com.lebruns.steven.robotcar.MenuItem#displayFirstLine()
     */
    @Override
    public void displayFirstLine()
    {
        String text = "Display " + name;
        
        lcd.write(text);
    }

    /* (non-Javadoc)
     * @see com.lebruns.steven.robotcar.MenuItem#select()
     */
    @Override
    public MenuItem select()
    {
        lcd.clear();
        lcd.home();
        lcd.write(name);
        lcd.setCursorPosition(1, 0);
        lcd.write(ipAddress);
        
        return this;
    }
    
    /**
     * @see com.lebruns.steven.robotcar.menu.MenuItem#displayMenuItem()
     */
    @Override
    public boolean displayMenuItem()
    {
    	return false;
    }

}
