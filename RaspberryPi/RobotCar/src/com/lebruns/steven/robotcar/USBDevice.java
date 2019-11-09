/**
 * USBDevice is a class used to communicate with an Arduino over an USB
 * cable.  This is a serial device.
 */
package com.lebruns.steven.robotcar;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import com.pi4j.io.serial.*;

/**
 * @author Steven F. LeBrun
 *
 */
public class USBDevice 
{
	/**
	 * The number of milliseconds to wait for the Arduino to respond
	 * after the serial connection is first made.
	 */
	static final int ARDIUNO_WAIT = 250;
	
	/**
	 * The actual Serial object used for reading and writing
	 */
	private Serial  Arduino = null;
	
	/**
	 * State of Arduino (Serial device via USB)
	 */
	private boolean   is_open = false;
	
	/**
	 * File Name for USB Port
	 */
	private String   portName = "";
	
	/**
	 * Handshake
	 */
	private String  Handshake = new String("Ready");
	private int     HandSize  = Handshake.length() + 2;  // 2 == "\n" at beginning and end of string.
	

	/**
	 * Constructor for Arduino object.
	 * 
	 * Currently, has nothing to do.  Real construction occurs during Open Command.
	 */
	public USBDevice() 
	{
		
	}
	
	/**
	 * Creates an open connection with the Arduino board.
	 * 
	 * When opening the Serial device, this method will search the existing
	 * USB ports on the Raspberry Pi and test each one to see if the Arduino
	 * with the Robot Car software is connected to it.  If the port is not
	 * physically connected to that board, the connection will be closed and 
	 * the next USB port will be checked.  The first Arduino board that is 
	 * running the Robot Car software will be used as the connection between
	 * the Raspberry Pi and the Arduino which controls the sensors and the
	 * motors.
	 * 
	 * NOTE.  If the connection between the Arduino and the Raspberry Pi is
	 * already open, this method does nothing.  In order to create a new
	 * connection, the close() method needs to be called first.
	 * 
	 * @return  Returns true if a connection was made or already exists.
	 *          Returns false if unable to make a connection.
	 */
	public boolean open()
	{
		if ( is_open )
		{
			return true;
		}
		
		boolean flag = FindArduino();
		
		if ( flag )
		{
			is_open = true;
		}
		
		return flag;
	}
	
	/**
	 * Closes the Arduino USB Connection and destroys the Serial object
	 * used for communication.
	 * 
	 * @throws IOException
	 */
	public void close()
		throws IOException
	{
		if ( Arduino != null && Arduino.isOpen() )
		{
			try
			{
				Arduino.close();
				is_open = false;
				Arduino = null;
			}
			catch ( IOException ex )
			{
				// Do Nothing for now.
			}
		}

	}
	
	public boolean isOpen()
	{
		return is_open;
	}
	
	public String port()
	{
		return portName;
	}
	
	/**
	 * Write a series of bytes to Arduino.
	 * 
	 * @param buffer  The array of bytes to be sent.
	 * 
	 * @throws IOException An IOException is thrown if there is no connection
	 *         setup and open between the Raspberry Pi and the Arduino or if
	 *         there is an error occurs when writing to the Arduino.
	 */
	public void write(byte[] buffer)
		throws IOException
	{
		if ( !is_open )
		{
			throw new IOException("No Arduino Open");
		}
		
		OutputStream device = Arduino.getOutputStream();
		
		device.write(buffer);
		device.flush();
	}
	
	/**
	 * Writes a String to the Arduino after converting the string to an
	 * array of bytes.
	 * 
	 * @param data  The string to be sent.
	 * 
	 * @throws IOException  IOException An IOException is thrown if there
	 * 		                is no connection setup and open between the 
	 *                      Raspberry Pi and the Arduino or if there is an 
	 *                      error occurs when writing to the Arduino.
	 */
	public void write(String data )
		throws IOException
	{
		byte[] buffer = data.getBytes("UTF-8");
		
		write(buffer);
	}
	
	/**
	 * Checks to see if there are any bytes available for reading on the
	 * Arduino device.
	 * 
	 * @return Returns the number of bytes that can be read from the Arduino.
	 * 
	 * @throws IOException  IOException An IOException is thrown if there
	 * 		                is no connection setup and open between the 
	 *                      Raspberry Pi and the Arduino.
	 */
	public int available()
		throws IOException
	{
		if ( !is_open )
		{
			throw new IOException("No Arduino Open");
		}
		
		InputStream device = Arduino.getInputStream();
		
		return device.available();
		
	}
	
	/**
	 * Reads bytes from Arduino device.
	 * 
	 * @param buffer  An array of bytes to hold the read bytes.  Must be
	 *                long enough to hold at least nBytes.
	 * @param nBytes  The number of bytes to be read.
	 * 
	 * @return        Returns the number of bytes read.  If the connection
	 *                has been closed and there are no bytes to be read, a
	 *                -1 is returned to mark the end of file.
	 *                
	 * @throws IOException  IOException An IOException is thrown if there
	 * 		                is no connection setup and open between the 
	 *                      Raspberry Pi and the Arduino or if there is an 
	 *                      error occurs when reading from the Arduino.
	 */
	public int read(byte[] buffer, int nBytes)
		throws IOException
	{
		if ( !is_open )
		{
			throw new IOException("No Arduino Open");
		}
		
		InputStream device = Arduino.getInputStream();
		
		int count = 0;
		
		count = device.read(buffer, 0, nBytes);
		
		return count;
	}
	
	/**
	 * FindArduino()
	 * 
	 * Searches all USB devices until it fines the one connected to the
	 * Arduino.  This is the USB port that can be opened and returns the
	 * string "Ready" when opened.
	 */
	private boolean FindArduino()
	{
		FilenameFilter  nameFilter = new USBFilter();
		
		File            deviceDir = null;
		
		String[]        devices = null;
		
		// Open Directory for reading list of filenames
		try
		{
			deviceDir = new File(USBFilter.Directory);
		}
		catch (NullPointerException ex)
		{
			// Do Something to report Error
			deviceDir = null;
		}
		
		if ( deviceDir == null || !deviceDir.isDirectory() )
		{
			return false;
		}
		
		// Get list of filenames for USB Devices only
		try
		{
			devices = deviceDir.list(nameFilter);
		}
		catch (SecurityException ex)
		{
			// Do something to report Error
			devices = null;
		}
		
		if ( devices == null || devices.length == 0 )
		{
			return false;
		}
		
		// Find which USB Device is attached to the Arduino
		boolean  found = false;
		for ( String  device : devices )
		{
			found = CheckDevice(device);
			if ( found )
			{
				return true;
			}
		}
		
		return false;
		
	}   // end of FindArduino()
	
	/**
	 * Determines if Device is the USB connection to the Arduino
	 * @param device Name of USB Device
	 * @return
	 */
	private boolean CheckDevice( String device )
	{
		final int  BufferMax = 32;
		
		// data is a buffer that needs to hold "Ready" plus NULL terminator
		// plus whitespaces that will be trimmed.  This is the first thing
		// that the Arduino will send/
		byte[]  data = new byte[BufferMax];  
		
		String  path = new String( "/dev/" + device);
		
		// Try to open the USB Device
		try
		{
			Serial  usb = SerialFactory.createInstance();

			usb.open(path, 9600);
			
			if ( !usb.isOpen() )
			{
				usb.close();
				return false;
			}
			
			InputStream  reader = usb.getInputStream();
			
			int tries  = 5;
			int nBytes = 0;
			int nChar  =  0;
			
			for ( int i = 0 ; i < tries ; ++i )
			{
				try
				{	
					// Got to give the Arduino a chance to respond.
					Thread.sleep(ARDIUNO_WAIT);
					
					nBytes = reader.available();
				
					if ( nBytes >= HandSize )
					{
						nChar = reader.read(data, 0, HandSize);
						
						if ( nChar >= 0 )
						{
							String handshake = new String(data, "UTF-8").trim();
							
							if ( handshake.equals(Handshake) ) 
							{
								Arduino  = usb;
								is_open  = true;
								portName = path;
								
								return true;
							}
						}
						
						usb.close();
					}
				}
				catch ( Exception ex )
				{
					usb.close();
					continue;
				}
			}
		}
		catch ( IOException ex )
		{
			// Do something to report error

			return false;
		}

		return false;
	}   // end of USBDevice::CheckDevice()
	

}   // end of class USBDevice
