/**
 * The Menu Control Project maintains communications between the
 * LCD Module and the Raspberry Pi 3 computer.  The buttons on the 
 * LCD Module are used to move about the menu and to select menu items.
 * The LCD Display shows the current menu item or the results of 
 * selecting it.
 */
package com.lebruns.steven.robotcar.menu;

import com.lebruns.steven.robotcar.LCDModule;

/**
 * The Menu Controller class is the primary class that maintains
 * menus as linked lists of menu items with movement up, down,
 * left, and right through the menu tree.  Pressing multiple
 * buttons will return the menu to the top-most and left-most
 * menu item.  In other words, pressing two or more buttons at
 * the same time will return you to the beginning of the menu.
 * 
 * The MenuController.loop() method is designed to run in its
 * own thread so that other processing can occur while waiting
 * for a human to press the buttons.
 * 
 * @author Steven F. LeBrun
 *
 */
public class MenuController
{
    /**
     * The object that represents the LCD Module.  All communication
     * with the LCD Module goes through this object.
     */
    protected LCDModule  lcd = null;
    
    /**
     * The Top-most/Left-most menu item. A.K.A. The beginning of the
     * Menu.
     */
    protected MenuItem   menu        = null;
    
    /**
     * The current Menu Item.  As the user moves around the menu tree,
     * this object is always set to the menu item being displayed.
     */
    protected MenuItem   currentMenu = null;
    
    /**
     * Constructor for the MenuController class.
     * 
     * @param ioDevice An LCDModule object.  This object must be created
     *                 before a MenuController object is created.  If this
     *                 parameter is not set, an Exception is thrown.
     *                 
     * @throws Exception An exception is thrown if the ioDevice parameter is
     *                   null.  Exceptions can also be thrown during the 
     *                   initialization of this object.
     */
    public MenuController( LCDModule ioDevice )
        throws Exception
    {
        if ( ioDevice == null )
        {
            throw new Exception("LCD Module not found.");
        }
        lcd = ioDevice;
        
        init();
        
        currentMenu = menu;
        currentMenu.display();
    }
    
    public void loop()
        throws Exception
    {
        MenuItem oldItem  = currentMenu;
        MenuItem nextItem = null;
        
        byte     buttons  = 0x00;
        int      bits     = 0;
        
        /**
         * Boolean flag that can be changed by the debug to end an
         * otherwise infinite loop.
         */
        boolean  keepGoing = true;
        
        while (keepGoing)
        {
            buttons = getLCD().getButtonStates();
            
            bits     =  bitCount(buttons);
            nextItem = null;
            
            if ( bits > 1 )
            {
                // Multiple Buttons were pushed.
                // GO TO HOME Menu Item
                nextItem = menu;
            }
            else if ( bits == 1 )
            {
                // One Button was pushed
                if ( (buttons & lcd.select_button) != 0)
                {
                    nextItem = currentMenu.select();
                }
                
                if ( (buttons & lcd.up_button) != 0)
                {
                    nextItem = currentMenu.up();
                }

                if ( (buttons & lcd.down_button) != 0)
                {
                    nextItem = currentMenu.down();
                }
                
                if ( (buttons & lcd.left_button) != 0)
                {
                    nextItem = currentMenu.left();
                }
                if ( (buttons & lcd.right_button) != 0)
                {
                    nextItem = currentMenu.right();
                } 

            }
            else
            {
                // No Buttons were pushed.  Do nothing.
                // This block is here for debugging purposes.
                nextItem = null;
            }
            
            if ( nextItem != null && ( nextItem != oldItem) )
            {
                // Change of Menu Item has occurred.
                
                currentMenu = oldItem = nextItem;
                
                currentMenu.display();
            }
            
            // Sleep for 1/2 second before checking for the next
            // button state.
            // Adjust as needed for speedy response time versus
            // amount of CPU used.
            
            Thread.sleep(50);
            
        }   // end of infinite loop
        
    }   // end of loop()
    
    
    protected LCDModule getLCD()
    {
        return lcd;
    }
    
    private int bitCount( byte data )
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
    
    private void  init()
        throws Exception
    {
       MenuBuilder  builder = MenuBuilder.getBuilder( getLCD() );
       
       menu = builder.buildMenuTree();
    }
    
}   // end of class MenuController
