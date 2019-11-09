/**
 * 
 */
package com.lebruns.steven.robotcar.menu;

import com.lebruns.steven.robotcar.LCDColor;
import com.lebruns.steven.robotcar.LCDModule;

/**
 * @author Steven F. LeBrun
 *
 */
public class MenuItemHappyBirthday extends MenuItem
{

    /**
     * @param label
     * @param goUp
     * @param goDown
     * @param goRight
     * @param goLeft
     * @param ioDevice
     */
    public MenuItemHappyBirthday(String label, MenuItem goUp, MenuItem goDown, MenuItem goRight, MenuItem goLeft,
            LCDModule ioDevice)
    {
        super(label, goUp, goDown, goRight, goLeft, ioDevice);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.lebruns.steven.robotcar.MenuItem#select()
     */
    @Override
    public MenuItem select()
    {
        lcd.clear();
        lcd.home();
        lcd.setBackground(LCDColor.YELLOW);
        
        lcd.write("Happy Birthday");
        lcd.setCursorPosition(1, 0);
        lcd.write(name);
        lcd.write("!");
        return this;
    }

}
