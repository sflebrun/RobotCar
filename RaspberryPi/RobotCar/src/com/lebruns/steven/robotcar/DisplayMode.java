/**
 * 
 */
package com.lebruns.steven.robotcar;

/**
 * @author Steven F. LeBrun
 * 
 * The LCD Module operates in one of two modes.  Menu Mode where the LCD
 * Display shows menu items and the buttons allow you move around in the
 * menu and to select a menu item.  Logger Mode where the LCD Display shows
 * log messages, such as range finder result, and the four cursor buttons
 * allow for moving around in the log (limited) with the select button
 * switching back to Menu Mode.
 */
public enum DisplayMode
{
	MENU,
	LOGGER;
}
