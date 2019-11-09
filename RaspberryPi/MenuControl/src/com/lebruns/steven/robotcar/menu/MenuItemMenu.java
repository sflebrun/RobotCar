/**
 * 
 */
package com.lebruns.steven.robotcar.menu;

import com.lebruns.steven.robotcar.LCDModule;

/**
 * MenuItemMenu is a MenuItem subclass that contains a submenu.
 * The Submenu is on the Right of this menu item.
 * 
 * @author Steven F. LeBrun
 *
 */
public class MenuItemMenu extends MenuItem
{
    public MenuItemMenu( String    menuLabel,
                         MenuItem  goUp,
                         MenuItem  goDown,
                         MenuItem  goRight,
                         MenuItem  goLeft,
                         LCDModule ioDevice )
    {
        super(menuLabel, goUp, goDown, goRight, goLeft, ioDevice);        
    }

 
    /* (non-Javadoc)
     * @see com.lebruns.steven.robotcar.MenuItem#select()
     */
    @Override
    public MenuItem select()
    {
        // Go to submenu
        return right();
    }
}   // end of class MenuItemMenu
