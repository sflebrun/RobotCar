/**
 * 
 */
package com.lebruns.steven.robotcar;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author steven
 *
 */
public class USBFilter implements FilenameFilter
{
	/**
	 * Directory - Name of the Device Director
	 */
	public static  String  Directory = new String("/dev");
	
	/**
	 * BaseName - Beginning part of file name for an USB file
	 */
	private static String BaseName = new String("ttyUSB");
	
	/**
	 * BaseEnd - Offset of last character in BaseName.
	 */
	int  BaseEnd = 0;

	/**
	 * Determines if File Name matches "/dev/ttyUSB*"
	 * 
	 * @return Returns true if File Name is for a USB device file.
	 */
	public USBFilter()
	{
		BaseEnd = BaseName.length() - 1;
	}

	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(File arg0, String arg1)
	{
//		String  name = arg0.getName();
		String  path = arg0.getPath();
		
		if ( arg1.length() <= BaseEnd )
		{
			return false;
		}
		
		String  base = arg1.substring(0, BaseEnd + 1);
		
//		System.out.println("Path: " + path + ", Name: " + name + ", Base: = " + base);
		
		if ( Directory.equals(path) && BaseName.equals(base) )
		{
			return true;
		}
		
		return false;
	}

}
