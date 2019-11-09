/**
 * 
 */
package com.lebruns.steven.robotcar;



/**
 * @author Steven F. LeBrun
 *
 */
public class LCDDisplay
{

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
           
           lcd.write("Hello Steven");
           
           //lcd.DumpRegistersRepeat();
           
           boolean keepgoing = true;
           byte    buttons;
           
           while ( keepgoing )
           {
               buttons = lcd.getButtonStates();
               if ( buttons != 0 )
               {
                   // Change of state detected.
                   if ( (buttons & lcd.select_button) != 0)
                   {
                       System.out.println("Select Button pushed");
                   }
                   
                   if ( (buttons & lcd.up_button) != 0)
                   {
                       System.out.println("Up Button pushed");
                   }
 
                   if ( (buttons & lcd.down_button) != 0)
                   {
                       System.out.println("Down Button pushed");
                   }
                   
                   if ( (buttons & lcd.left_button) != 0)
                   {
                       System.out.println("left Button pushed");
                   }
                   if ( (buttons & lcd.right_button) != 0)
                   {
                       System.out.println("Right Button pushed");
                   } 
                   
                   Thread.sleep(1000);
               }
               
           }
           
           for ( LCDColor color : LCDColor.values() )
           {
               lcd.setBackground(color);
           }
           
           lcd.setBackground(LCDColor.BLACK);
            
        }
        catch ( Exception ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        
        return;

    }

}
