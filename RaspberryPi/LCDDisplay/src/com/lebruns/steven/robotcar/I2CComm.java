/**
 * 
 */
package com.lebruns.steven.robotcar;



import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * @author Steven F. LeBrun
 *
 */
//@SuppressWarnings("unused")
public class I2CComm
{
    /**
     * The I2C Bus number.  This should be either 1 or 0 depending on the
     * age of the Raspberry Pi board.  Newer boards use bus 1.
     */
    private  int I2CBus;
    
    /**
     * The I2C Address of the LCD Module.  This is the slave address that
     * the module uses when connecting to the I2C drivers.  For the LCD
     * board I am using, this address is 0x20.
     */
    private  int I2CAddress;
    
    /**
     * The I2C Bus as obtained from the I2CFactory for the bus I2CBus.
     */
    private I2CBus     iobus;
    
    /**
     * The I2C Device as obtained from iobus for the address I2CAddress.
     */
    private I2CDevice  iodevice;
    
    /**
     * Constructor for the I2CComm class.
     * @param bus       Number for the I2C Bus.  For newer Raspberry Pi 
     *                  boards, this should be 1.  For older Raspberry Pi 
     *                  boards, this should be 0.  To check, run the following
     *                  command from 'i2cdetect -y N' where N is either
     *                  1 or 0.  The correct bus number will return a table 
     *                  of addresses.
     * @param address   The address of LCD Board on the I2C Bus.  For the
     *                  board that I am using, this is 0x20.  The address
     *                  can be obtained from the schematics or by running
     *                  the 'i2cdetect -y 1' command.
     *                  
     * The constructor obtains an object that allows access to the I2C Device
     * through the I2C Bus.  Reads and Writes to the the LCD Module are 
     * done with this object.
     * 
     * @throws Exception  An exception is thrown if either the I2C Bus or
     *                    address are invalid.
     */
    public I2CComm(int bus, int address)
        throws Exception
    {
        I2CBus      = bus;
        I2CAddress  = address;
        
        // Obtain I2C access
        iobus    = I2CFactory.getInstance(I2CBus);
        iodevice = iobus.getDevice(I2CAddress);
        
        return;
    }
    
    /**
     * Getter for exposing the I2C Device for I/O operations.
     * 
     * @return  Returns the I2CDevice.
     */
    public I2CDevice getDevice()
    {
        return iodevice;
    }
    

}   // End of class I2CComm
