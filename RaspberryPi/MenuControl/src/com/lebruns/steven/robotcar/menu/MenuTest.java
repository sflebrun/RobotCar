/**
 * 
 */
package com.lebruns.steven.robotcar.menu;

import com.lebruns.steven.robotcar.LCDModule;

/**
 * @author Steven F. LeBrun
 *
 */
public class MenuTest
{

    /**
     * 
     */
    public MenuTest()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        int  bus = 1;
        int  slave = 0x20;
        
        try
        {
            LCDModule  lcd = new LCDModule(bus, slave);
            
            MenuController  myMenu = new MenuController( lcd );
            
            myMenu.loop();
        }
        catch (Exception exc)
        {
            System.out.println(exc.getMessage());
            exc.printStackTrace(System.out);
        }

    }

}   // end of class MenuTest
