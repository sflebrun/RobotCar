/**
 * Encapsulate building the Menu Tree in a single class.
 */
package com.lebruns.steven.robotcar.menu;

import com.lebruns.steven.robotcar.LCDModule;

/**
 * The Menu Tree is used by the Robot Car to make commands available to
 * a human user through the LCD Module using both the LCD Display and the
 * five buttons referred to as Up, Down, Left, Right and Select.
 * 
 * This code has been pulled out of the MenuController class.  This code
 * is used only during the startup phase of the Robotic Car Application.
 * By isolating this code in its own class, the source code and memory 
 * used can be processed by Java garbage collection after startup.
 * @author Steven F. LeBrun
 *
 */
public class MenuBuilder
{
    LCDModule  lcd = null;
    
    public static MenuBuilder getBuilder( LCDModule ioDevice )
    {
        MenuBuilder  builder = new MenuBuilder( ioDevice );
        
        return builder;
    }

    /**
     * 
     */
    private MenuBuilder( LCDModule ioDevice )
    {
        lcd = ioDevice;
    }
    
    public MenuItem  buildMenuTree()
            throws Exception
    {   
        MenuItem  First  = buildFirstMenu();
        MenuItem  Status = buildStatusMenu();
        
        First.setRight(Status);
        First.buildNextLine();
        
        Status.setLeft(First);
        Status.buildNextLine();
            
        return First;
    }
    
    protected MenuItem buildFirstMenu()
    {
        /**
         * @todo Create Menu Item that issues a real command.
         */
        MenuItem  Top  = new MenuItemTopMenu("STOP Wheels!", 
                                             null, null, null, null, 
                                             lcd );
        MenuItem  Hide = new MenuItemDisplayOff("Turn Display Off", 
                                                Top, null, null, null, 
                                                lcd );
        MenuItem  Restart = new MenuItemRestart("Restart Software",
        										Hide, null, null, null,
        										lcd );
        MenuItem  Stop = new MenuItemTopMenu("Quit", 
                                              Restart, null, null, null, 
                                              lcd );
        
        Top.setDown(Hide);
        Top.buildNextLine();
        
        Hide.setDown(Restart);
        Hide.buildNextLine();
        
        Restart.setDown(Stop);
        Restart.buildNextLine();
        
        return Top;
    }
    
    protected MenuItem buildStatusMenu()
        throws Exception
    {
        MenuItem  IPAddress = buildIPAddressMenu();
        MenuItem  BDay      = buildBirthdayMenu();
        
        MenuItem  Top  = new MenuItemTopMenu( "Status",
                                               null,
                                               IPAddress,
                                               null,
                                               null,
                                               lcd );
        
        IPAddress.setUp(Top);
        IPAddress.setDown(BDay);
        IPAddress.buildNextLine();
        
        BDay.setUp(IPAddress);
        BDay.buildNextLine();
        
        return Top;
    }
    
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
        throws Exception
    {
        MenuItem  IPAddress = new MenuItemMenu("IP Addresses",
                                                null, null, null, null,
                                                lcd);
        
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
        
        return IPAddress;
    }
    
    private MenuItem buildBirthdayMenu()
    {
        MenuItem Birthdays = new MenuItemMenu( "Happy Birthdays",
                                                null, null, null, null,
                                                lcd );
        
        MenuItem DylanBennett    = new MenuItemHappyBirthday( "Dylan Bennett",
        													null, null, null, Birthdays, lcd);

        MenuItem ChristinaKernes = new MenuItemHappyBirthday( "Christina Kernes",
        													DylanBennett, null, null, null, lcd);
        
        MenuItem BrittanyMcMann  = new MenuItemHappyBirthday( "Brittany McMann",
        													ChristinaKernes, null, null, null, lcd);
        
        MenuItem LynnBogovich    = new MenuItemHappyBirthday( "Lynn Bogovich",
        													BrittanyMcMann, null, null, null, lcd);
        
        MenuItem RobPalmason     = new MenuItemHappyBirthday( "Rob Palmason",
        													LynnBogovich, null, null, null, lcd);
        
        MenuItem MissySmith      = new MenuItemHappyBirthday( "Missy Smith",
        													RobPalmason, null, null, null, lcd);
        
        MenuItem TracyTaylor     = new MenuItemHappyBirthday( "Tracy Taylor",
        													MissySmith, null, null, null, lcd);
        
        MenuItem TimJenkins      = new MenuItemHappyBirthday( "Tim Jenkins",
        													TracyTaylor, null, null, null, lcd);
        
        MenuItem MarilynConverse = new MenuItemHappyBirthday( "Marilyn Converse",
        													TimJenkins, null, null, null, lcd);
        
   

        Birthdays.setRight(DylanBennett);
        
        DylanBennett.setDown(ChristinaKernes);
        ChristinaKernes.setDown(BrittanyMcMann);
        BrittanyMcMann.setDown(LynnBogovich);
        LynnBogovich.setDown(RobPalmason);
        RobPalmason.setDown(MissySmith);
        MissySmith.setDown(TracyTaylor);
        TracyTaylor.setDown(TimJenkins);
        TimJenkins.setDown(MarilynConverse);

 
        Birthdays.buildNextLine();
        DylanBennett.buildNextLine();
        ChristinaKernes.buildNextLine();
        BrittanyMcMann.buildNextLine();
        LynnBogovich.buildNextLine();
        RobPalmason.buildNextLine();
        MissySmith.buildNextLine();
        TracyTaylor.buildNextLine();
        TimJenkins.buildNextLine();
        
        return Birthdays;
    }



}   // End of class MenuBuilder
