/**
 * 
 */
package com.lebruns.steven.robotcar.menu;

import com.lebruns.steven.robotcar.LCDModule;

/**
 * A Top Menu is usually just a Menu Item with a label or name.  It contains
 * more menu items below it and no menu items above it.
 * 
 * The Top Menu is either in the primary menu or the top of a sub-menu.
 * 
 * @author Steven F. LeBrun
 *
 */
public class MenuItemTopMenu extends MenuItem
{

    /**
     * @param goUp
     * @param goDown
     * @param goRight
     * @param goLeft
     * @param ioDevice
     */
    public MenuItemTopMenu( String    menuLabel,
                            MenuItem  goUp, 
                            MenuItem  goDown, 
                            MenuItem  goRight, 
                            MenuItem  goLeft, 
                            LCDModule ioDevice)
    {
        super(menuLabel, goUp, goDown, goRight, goLeft, ioDevice);

    }


    /* (non-Javadoc)
     * @see com.lebruns.steven.robotcar.MenuItem#select()
     */
    @Override
    public MenuItem select()
    {
        MenuItem item = haveDown() ? downItem : this ;
        return item;
    }

}
