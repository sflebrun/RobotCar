/**
 * 
 */
package com.lebruns.steven.robotcar;

import com.lebruns.steven.robotcar.menu.MenuItem;

/**
 * @author Steven F. LeBrun
 *
 */
public class MenuItemBlueDotNavigation extends MenuItem
{

	/**
	 * @param label
	 * @param goUp
	 * @param goDown
	 * @param goRight
	 * @param goLeft
	 * @param ioDevice
	 */
	public MenuItemBlueDotNavigation(String label, MenuItem goUp, MenuItem goDown, MenuItem goRight, MenuItem goLeft,
			LCDModule ioDevice)
	{
		super(label, goUp, goDown, goRight, goLeft, ioDevice);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.lebruns.steven.robotcar.menu.MenuItem#select()
	 */
	@Override
	public MenuItem select()
	{
		// TODO Auto-generated method stub
		return this;
	}
	
	/**
	 * @see com.lebruns.steven.robotcar.menu.MenuItem#isMenu()
	 */
	@Override
	public boolean isMenu()
	{
		return false;
	}

}   // end of class MenuItemBlueDotNavigator
