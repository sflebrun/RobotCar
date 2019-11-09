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
public abstract class MenuItem
{
    protected  LCDModule lcd       = null;
    protected  MenuItem  upItem    = null;
    protected  MenuItem  downItem  = null;
    protected  MenuItem  rightItem = null;
    protected  MenuItem  leftItem  = null;
    protected  String    name      = null;
    protected  String    nextLine  = null;
    
    public MenuItem( String     label,
                     MenuItem   goUp, 
                     MenuItem   goDown,
                     MenuItem   goRight,
                     MenuItem   goLeft,
                     LCDModule  ioDevice )
    {
        upItem     =  goUp;
        downItem   =  goDown;
        rightItem  =  goRight;
        leftItem   =  goLeft;
        
        lcd        =  ioDevice;
        name       =  label;
        
        buildNextLine();
    }
    
    public abstract MenuItem select();
    
    public void display()
    {
        lcd.clear();
        
        setDisplayColor();
        
        displayFirstLine();
        displaySecondLine();
    }
    
    protected void setDisplayColor()
    {
        lcd.setBackground(LCDColor.BLUE);
    }
    protected void displayFirstLine()
    {
        lcd.setCursorPosition(0, 0);
        lcd.write(name);
    }
    
    protected void displaySecondLine()
    {
        lcd.setCursorPosition(1, 0);
        lcd.write(nextLine);
    }
    
    public MenuItem up()
    {
        // Expected results
        if ( haveUp() )
        {
            return upItem;
        }
        
        // Default results
        return this;
    }
    
    public MenuItem down()
    {
        // Expected Results
        if ( haveDown() )
        {
            return downItem;
        }
        
        // Default Results
        return null;
    }
    
    public MenuItem right()
    {
        // Expected Results
        if ( haveRight() )
        {
            return rightItem;
        }
        
        // Default Results
        return null;    
    }
    
    public MenuItem left()
    {
        if ( haveLeft() )
        {
            return leftItem;
        }
        
        if ( haveUp() )
        {
            return upItem.left();
        }
        
        return null;
    }
    
    public boolean haveUp()
    {
        return ( null != upItem );
    }
    
    public boolean haveDown()
    {
        return ( null != downItem );
    }
    
    public boolean haveRight()
    {
        return ( null != rightItem );
    }
    
    public boolean haveLeft()
    {
        return ( null != leftItem );
    }
    
    /**
     * This method determines if the code should remain in menu mode.
     * If the code should exit Menu Mode, this method needs to be overridden
     * in a child class.
     * 
     * @pre This method assumes that the select() method has just been called.
     * 
     * @return Returns @b true if code should stay in Menu Mode.  
     *         Returns @b false if code should exit Menu Mode.
     */
    public boolean isMenu()
    {
    	return true;
    }
    
    /**
     * This method determines whether the code should redisplay the current
     * menu item, which may have changed during the execution of select().
     * 
     * If a menu item displays something to the LCD Module, that child class
     * should override this method in order to prevent what it has displayed
     * from being immediately overwritten.
     * 
     * @return Returns @b true to display the current menu item immediately
     *         after running select().  Returns @b false if the display 
     *         should not be rewritten immediately after running select().
     */
    public boolean displayMenuItem()
    {
    	return true;
    }
    
    public MenuItem setUp( MenuItem newItem )
    {
        MenuItem oldItem = upItem;
        
        upItem = newItem;
        
        return oldItem;
    }
    
    public MenuItem setDown( MenuItem newItem )
    {
        MenuItem oldItem = downItem;
        
        downItem = newItem;
        
        return oldItem;
    }
    
    public MenuItem setRight( MenuItem newItem )
    {
        MenuItem oldItem = rightItem;
        
        rightItem = newItem;
        
        return oldItem;
    }
    
    public MenuItem setLeft( MenuItem newItem )
    {
        MenuItem oldItem = leftItem;
        
        leftItem = newItem;
        
        return oldItem;
    }
    

    public void buildNextLine()
    {
        if ( haveLeft() && !haveDown() && !haveRight() )
        {
            nextLine = "<               ";
        }
        else if ( haveLeft() && haveDown() && !haveRight() )
        { 
            nextLine = "<      V        ";
        }
        else if ( haveLeft() && !haveDown() && haveRight() )
        {
            nextLine = "<              >";
        }
        else if ( haveLeft() && haveDown() && haveRight() )
        {
            nextLine = "<      V       >";
        }
        else if ( !haveLeft() && haveDown() && haveRight() )
        {
            nextLine = "       V       >";
        }
        else if ( !haveLeft() && !haveDown() && haveRight() )
        {
            nextLine = "               >";
        }
        else if ( !haveLeft() && haveDown() && !haveRight() )
        {
            nextLine = "       V        ";
        }
        else if ( !haveLeft() && !haveDown() && haveRight() )
        {
            nextLine = "               >";
        }
        else
        {
            nextLine = " ";
        }
   }


}   // end of abstract class MenuItem
