/**
 * 
 */
package com.lebruns.steven.robotcar;

import com.lebruns.steven.robotcar.menu.MenuItem;

/**
 * @author Steven F. LeBrun
 *
 */
public class MenuItemLogger extends MenuItem
{

	/**
	 * @param label
	 * @param goUp
	 * @param goDown
	 * @param goRight
	 * @param goLeft
	 * @param ioDevice
	 */
	public MenuItemLogger(String label, MenuItem goUp, MenuItem goDown, MenuItem goRight, MenuItem goLeft,
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
		// Nothing to do here.  All the action takes place in the isMenu method.
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

}
