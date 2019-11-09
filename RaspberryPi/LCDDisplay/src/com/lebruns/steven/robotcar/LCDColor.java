/**
 * 
 */
package com.lebruns.steven.robotcar;

/**
 * The LCD Module Background can be switched between eight different
 * colors, including Black and White.  This Enum provides a member for
 * each of these colors.
 * 
 * The internal structure of LCDColor is used to determine which LEDs
 * are turned on and off to achieve each color.
 * 
 * The background light is comprised of three LEDs; red, green and blue.
 * Each color is achieved by turning different combinations of these
 * three colors.  The LEDs are on/off only so no shades of colors is
 * possible.  Potentially, it may be possible to simulate PWM with
 * software.  This would allow control of the intensity of each LED.
 * 
 * @author Steven F. LeBrun
 */
public enum LCDColor
{
    // Background Colors for the LCD Module.
    WHITE(  true,  true,  true),
    RED(    true,  false, false),
    YELLOW( true,  true,  false), 
    GREEN(  false, true,  false),
    TEAL(   false, true,  true), 
    BLUE(   false, false, true),
    VIOLET( true,  false, true),
    BLACK(  false, false, false);
    
    private boolean red;
    private boolean green;
    private boolean blue;
    
    /**
     * Private Constructor for LCDColor.
     * 
     * @param useRed    Boolean.  True for use Red LED, False to not use Red LED.
     * @param useGreen
     * @param useBlue
     */
    private LCDColor(boolean useRed, boolean useGreen, boolean useBlue)
    {
        red    = useRed;
        green  = useGreen;
        blue   = useBlue;
    }
    
    /**
     * Determines whether to turn the Red LED On or Off.
     * 
     * @return  Returns True if the Red LED should be turned on for this color.
     *          Returns False if the Red LED is not used for this color.
     */
    public boolean useRed()
    {
        return red;
    }
    
    /**
     * Determines whether to turn the Green LED On or Off.
     * 
     * @return  Returns True if the Green LED should be turned on for this color.
     *          Returns False if the Green LED is not used for this color.
     */
    public boolean useGreen()
    {
        return green;
    }
    
    /**
     * Determines whether to turn the Blue LED On or Off.
     * 
     * @return  Returns True if the Blue LED should be turned on for this color.
     *          Returns False if the Blue LED is not used for this color.
     */
    public boolean useBlue()
    {
        return blue;
    }
    

}   // end of enum LCDColor
