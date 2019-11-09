/**
 * The LCD Module that this code is for is one that uses an MCP23017 chip
 * I2C Serial Interface with the Raspberry Pi and an HD44780U LCD Chip for
 * the actual display.  The MCP23017 chip communicates with the LCD Chip 
 * through its Port A GPIO pins.  The MCP23017 is also connected to the 
 * push buttons on the board through its Port B GPIO pins.
 * 
 * For information on this module see the following URLS:
 * 1) For the entire Board:  http://wiki.52pi.com/index.php/RGB_LCD1602_SKU:EP-0058
 * 2) For the MCP23017 Chip: http://www.microchip.com/wwwproducts/en/MCP23017
 *    - See MCP23017/MCP23S17 Data Sheet listed under Documentation
 *      http://ww1.microchip.com/downloads/en/DeviceDoc/20001952C.pdf
 * 3) For the HD44780U Chip: https://www.sparkfun.com/datasheets/LCD/HD44780.pdf
 */
package com.lebruns.steven.robotcar;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.component.lcd.LCD;
import com.pi4j.component.lcd.LCDBase;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;


/**
 * This class encapsulates the LCD Module which communicates with the
 * Raspberry Pi using I2C.  The I2C communications goes through an
 * MCP23017 chip, which is encapsulated by its own class; MCP23017.
 * 
 * @author Steven F. LeBrun
 */
@SuppressWarnings("unused")
public class LCDModule extends LCDBase implements LCD
{
    // Bit Mask that represent the GPIO Pin that each push button
    // is connected to.  Why the button numbers do not match the
    // Port B GPIO pins is a mystery to me though that is how the
    // Schematic shows and testing confirms.
    final public   int  down_button    = 0x02;  // Switch #3
    final public   int  left_button    = 0x04;  // Switch #5
    final public   int  right_button   = 0x08;  // Switch #2
    final public   int  select_button  = 0x01;  // Switch #1
    final public   int  up_button      = 0x10;  // Switch #4
    
    /**
     *  The I2C Bus number varies with each Raspberry Pi board.  The 
     *  new boards such as the Raspberry Pi 3 uses I2C 1.  Older 
     *  Raspberry Pi boards use I2C 0.  Check your documentation to 
     *  determine what is the correct number to use.  You can also
     *  try running the following commands:
     *     "i2cdetect -y 0" and "i2cdetect -y 1"
     *  The correct one for your board will result in a matrix of
     *  addresses, most of them 0x00.
     */
    final static private int bus_number    = 1;
    
    /**
     * The Slave Address of the LCD Display Hat.  This is hardwired on 
     * the module.  Some modules allow for the three LSB bits to be
     * controlled manually (by the board) to vary the address.
     * The LCD Module that I am using uses the slave address of 0x20.
     * 
     * You can use the "i2cdetect -y 1" command to determine which
     * address is being used.
     */
    final static private int slave_address = 0x20;
    
    /**
     * This LCD Display has 2 rows.
     */
    final static private int nRows         = 2;
    
    /**
     * This LCD Display has 16 characters per row.
     */
    final static private int nColumns      = 16;
    
    /**
     * Bit Mask for the RS Bit on Port A GPIO pins.  This pin is used to
     * determine if the data being sent is a command or text for the display.
     */
    final static private int RS_Bit        = 0x01;
    
    /**
     * Bit Mask for the RW Bit on Port A GPIO pins.  This pin is used to
     * determine if the data is to written or read.
     */
    final static private int RW_Bit        = 0x02;
    
    /**
     * Bit Mask for the E Bit on the Port A GPIO pins.  This pin is also 
     * known as the Strobe pin.  It is used to cause the LCD Chip to 
     * process the bits on the MCP23017 chip Port A GPIO pins.
     */
    final static private int E_Bit         = 0x04;
    
    // The MCP23017 and HD44780U chips communicate using 4 data bits and
    // three control bits (RS, RW and E).  These are the bit masks for
    // the location of the data pins used on the GPIO bus.  These masks
    // are not actually used.  Instead each nybble of data bits is shifted
    // by three bits to line up to the same positions.
    //
    // NOTE: The bit position is determined by the MCP23017 chip and are
    //       the pins of the GPIO bus used to write to the HD44780U chip.
    //
    // Other LCD Modules may be wired differently.  In which case, these
    // bit masks should be used for setting and clearing each bit as
    // necessary.
    final static private int D7_Bit        = 0x40;
    final static private int D6_Bit        = 0x20;
    final static private int D5_Bit        = 0x10;
    final static private int D4_Bit        = 0x08;
    
    /**
     * The bit mask for the BackLight bit.  There is none for this LCD Module
     * so the mask contains no set bits.
     */
    final static private int BL_Bit        = 0x00;
    
    // Primary Command values for the HD44780U Chip.  The command is determined
    // by the first MSB bit that is set.  The lower bits can be set to activate
    // subfunctions of each command.
    final  private byte LCD_CMD_CLEAR    = 0x01;
    final  private byte LCD_CMD_HOME     = 0x02;
    final  private byte LCD_CMD_ENTRY    = 0x04;
    final  private byte LCD_CMD_DISPLAY  = 0x08;
    final  private byte LCD_CMD_CURSOR   = 0x10;
    final  private byte LCD_CMD_FUNC     = 0x20;
    final  private byte LCD_CMD_CGRAM    = 0x40;
    final  private byte LCD_CMD_DDRAM    = (byte) 0x80;
    
    /**
     * Special Command for the HD44780U.  Send this command three times and 
     * the chip resets into its 8 bit mode.  Follow this command with the
     * LCD_CMD_RESET4 to switch to 4 bit mode.
     */
    final static private byte LCD_CMD_RESET8   = 0x33;
    
    /**
     * Special Command for the HD44780U.  Send this command once after sending
     * the LCD_CMD_RESET8 three times to reset the HD44780U chip into 4 bit mode.
     */
    final static private byte LCD_CMD_RESET4   = 0x32;
    
    // Bit masks for subfunctions of the LCD_CMD_CURSOR command
    final static private byte LCD_CURSOR_DISPLAY_SHIFT = 0x08;
    final static private byte LCD_CURSOR_RIGHT_SHIFT   = 0x04;
    
    // Bit masks for the subfunctions of the LCD_CMD_DISPLAY command.
    final static private byte LCD_DISPLAY_ON           = 0x04;
    final static private byte LCD_DISPLAY_CURSOR_ON    = 0x02;
    final static private byte LCD_DISPLAY_BLINK_ON     = 0x01;
    
    // Bit masks for the subfunctions of the LCD_CMD_ENTRY command.
    final static private byte LCD_ENTRY_INCREMENT      = 0x02;
    final static private byte LCD_ENTRY_SHIFT_ON       = 0x01;
    
    // Bit masks for the subfunctions fo the LCD_CMD_FUNC command.
    final static private byte LCD_FUNC_8_BIT_MODE      = 0x10;
    final static private byte LCD_FUNC_TWO_LINES       = 0x08;
    final static private byte LCD_FUNC_5_10_CHAR       = 0x04;
    
    /**
     * The Addresses of different lines on the Display.  Four lines are listed
     * even though this LCD Module only has two lines.  Some other LCD Modules
     * can support up to four lines.
     * 
     * Note that the MSB bit is set and corresponds to the LCD_CMD_DDRAM command.
     */
    final static protected int[] LCD_LINE_ADDRESS = {0x80, 0xC0, 0x94, 0xD4 };
    
    /**
     * Flag that represents the state of the RS bit (set) for Characters
     * or text to be displayed.
     */
    final static protected boolean LCD_CHR  = true;
    
    /**
     * Flag that represents the state of the the RS bit (clear) for
     * Commands.
     */
    final static protected boolean LCD_CMD  = false;

    
    /**
     * Flag that determines if the RS bit should be set (true) or cleared (false).
     * RS == Text or Command
     */
    protected boolean rsFlag               = false;

    /**
     * Flag that determines if the RW bit should be set (true) or cleared (false).
     * RW == Read or Write
     */
    protected boolean rwFlag               = false;

    /**
     * Flag that determines if the E bit should be set (true) or cleared (false).
     * E == Strobe == If set causes the HD4780U to read the data bits and process
     * the command.
     */
    protected boolean eFlag                = false;
    
    
    /**
     * The I2C Device used to communicate with the LCD Module.
     * @todo is this data member needed in this module or should it only exist
     * in the MCP23017 class?
     */
    protected I2CDevice iodevice           = null;
    
    /**
     * The object that represents the front end I2C serial interface chip.  All
     * reads and writes to the LCD Module goes through this chip.
     */
    protected MCP23017  mcp_chip           = null;
    
    /**
     * The object that allows the software to access the GPIO bus on the 
     * Raspberry Pi.
     */
    protected GpioController  gpio         = null;

    /**
     * The Raspberry Pi GPIO pin connected to the Red LED.  The pin
     * number scheme being used if defined by the PI4J and WiringPi libraries.
     */
    protected Pin       GPIO_RED_PIN       = RaspiPin.GPIO_00;  //  11 -- GPIO 0

    /**
     * The Raspberry Pi GPIO pin connected to the Green LED.  The pin
     * number scheme being used if defined by the PI4J and WiringPi libraries.
     */
    protected Pin       GPIO_GREEN_PIN     = RaspiPin.GPIO_02;  //  13 -- GPIO 2

    /**
     * The Raspberry Pi GPIO pin connected to the Blue LED.  The pin
     * number scheme being used if defined by the PI4J and WiringPi libraries.
     */
    protected Pin       GPIO_BLUE_PIN      = RaspiPin.GPIO_03;  //  15 -- GPIO 3
    
    /**
     * The PI4J object that is used to set and clear the GPIO pin that is 
     * connected to the Red LED.
     */
    protected GpioPinDigitalOutput LCD_RED    = null;

    /**
     * The PI4J object that is used to set and clear the GPIO pin that is 
     * connected to the Green LED.
     */
    protected GpioPinDigitalOutput LCD_GREEN  = null;

    /**
     * The PI4J object that is used to set and clear the GPIO pin that is 
     * connected to the Blue LED.
     */
    protected GpioPinDigitalOutput LCD_BLUE   = null;
    
    public LCDModule( int i2cBus, int i2cAddress )
        throws Exception
    {
        // Initialize I2C Bus 
        I2CBus    bus  = I2CFactory.getInstance(bus_number);
        iodevice       = bus.getDevice(slave_address);
        
        // Initialize MCP23017 [16-bit I/O Expander with Serial Interface] chip
        mcp_chip       = new MCP23017(iodevice, false);
        
        this.init();
        
        return; 
    }
    
    @Override
    public void clear()
    {
        try
        {
            lcd_byte(LCD_CMD_CLEAR, LCD_CMD);
        }
        catch (Exception ex)
        {
            Logger.getLogger(LCDModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void home()
    {
        try
        {
            lcd_byte(LCD_CMD_HOME, LCD_CMD);
            
            // Needs up to 1.52 ms to process Home Command
            Thread.sleep(2);  
        }
        catch (Exception ex)
        {
            Logger.getLogger(LCDModule.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
    
    public void entry(boolean increment, boolean shift)
        throws Exception
    {
        byte data = LCD_CMD_ENTRY;
        
        if ( increment )
        {
            data |= LCD_ENTRY_INCREMENT;
        }
        
        if ( shift )
        {
            data |= LCD_ENTRY_SHIFT_ON;
        }
        
        try
        {
            lcd_byte(data, LCD_CMD);
            
            // Needs up to 37 microseconds to process Home Command
            TimeUnit.MICROSECONDS.sleep(50); 
        }
        catch (Exception ex)
        {
            Logger.getLogger(LCDModule.class.getName()).log(Level.SEVERE, null, ex);
        }       
        
    }
    
    public void display(boolean displayOn, boolean cursorOn, boolean blinkOn)
            throws Exception
    {
        byte data = LCD_CMD_DISPLAY;
        
        if ( displayOn )
        {
            data |= LCD_DISPLAY_ON;
        }

        if (     cursorOn )
        {   
            data |= LCD_DISPLAY_CURSOR_ON;
        }

        if ( blinkOn )
        {
            data |= LCD_DISPLAY_BLINK_ON;
        }

        try
        {
            lcd_byte(data, LCD_CMD);

            // Needs up to 37 microseconds to process Home Command
            TimeUnit.MICROSECONDS.sleep(50);
        }
        catch (Exception ex)
        {
            Logger.getLogger(LCDModule.class.getName()).log(Level.SEVERE, null, ex);
        }       

    }   // end of display()

    
    public void cursor(boolean displayShift, boolean shiftRight)
        throws Exception
    {
        byte data = LCD_CMD_CURSOR;
        
        if ( displayShift )
        {
            data |= LCD_CURSOR_DISPLAY_SHIFT;
        }
        
        if ( shiftRight )
        {
            data |= LCD_CURSOR_RIGHT_SHIFT;
        }
        
        try
        {
            lcd_byte(data, LCD_CMD);
            
            // Needs up to 37 microseconds to process Home Command
            TimeUnit.MICROSECONDS.sleep(50);  
        }
        catch (Exception ex)
        {
            Logger.getLogger(LCDModule.class.getName()).log(Level.SEVERE, null, ex);
        }       
        
    }  // end of cursor()
    
    public void mode(boolean eightBitMode, boolean twoLines, boolean largeFont)
            throws Exception
    {
        byte data = LCD_CMD_FUNC;
        
        if ( eightBitMode )
        {
            data |= LCD_FUNC_8_BIT_MODE;
        }

        if ( twoLines )
        {   
            data |= LCD_FUNC_TWO_LINES;
        }

        if ( largeFont )
        {
            data |= LCD_FUNC_5_10_CHAR;
        }

        try
        {
            lcd_byte(data, LCD_CMD);

            // Needs up to 37 microseconds to process Home Command
            TimeUnit.MICROSECONDS.sleep(50);
        }
        catch (Exception ex)
        {
            Logger.getLogger(LCDModule.class.getName()).log(Level.SEVERE, null, ex);
        }       

    }   // end of mode()


    @Override
    public int getColumnCount()
    {
        return nColumns;
    }
    
    @Override 
    public int getRowCount()
    {
        return nRows;
    }
    

    @Override
    public void setCursorPosition(int row, int column)
    {
        if ( row < 0 )
        {
            row = 0;
        }
        else if (row >= nRows)
        {
            row = (nRows - 1);
        }
        
        if ( column < 0 )
        {
            column = 0;
        }
        else if ( column >= nColumns )
        {
            column = nColumns - 1;
        }
        
        int  position = LCD_LINE_ADDRESS[row] + column;
        byte data     = (byte) position;
        
        try
        {
            lcd_byte(data, LCD_CMD);

            // Needs up to 37 microseconds to process Home Command
            TimeUnit.MICROSECONDS.sleep(50);
        }
        catch (Exception ex)
        {
            Logger.getLogger(LCDModule.class.getName()).log(Level.SEVERE, null, ex);
        }       

        
    }
    

    
    @Override
    public void write(byte data)
    {
        try
        {
            // INFINITE LOOP lcd_byte() calls write(byte)
            lcd_byte(data, LCD_CHR);
        }
        catch (Exception ex)
        {
            Logger.getLogger(LCDModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void write(String data) 
    {
        for (int i = 0; i < data.length(); i++) 
        {
            try 
            {
                lcd_byte(data.charAt(i), LCD_CHR);
            } 
            catch (Exception ex) 
            {
                System.out.println("Problems writing data");
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
  
    /**
     * Prepares and sends data to the LCD Chip via the MCP23017 chip using I2C.
     * 
     * @param value   The Byte of data to be sent.  Only the 8 LSB are used.  The
     *                rest of the bits are ignored.
     * @param type    Boolean.  True for sending Text to be displayed.
     *                False for sending a command to the LCD Chip.
     *              
     * The MCP23017 chip uses one of its 8 bit ports to communicate with the 
     * LCD chip.  Four bits contain a nybble of the original data value.
     * Three bits used for the control bits [RS, RW, and E].  The high nybble
     * is sent first, followed with pulsing the E [Strobe] bit and then the 
     * lower nybble followed with pulsing the E [Strobe] bit.
     * 
     * @throws Exception
     */
    public void lcd_byte(int value, boolean type) 
                throws Exception 
    {
        byte   data;
        
        // Set the Control Bit flags for Write/[Cmd|Chr]/No-Strobe
        setRS(type);
        setRW(false);
        setE(false);
        
        // High Nybble
        data = (byte) ((value >> 4) & 0x0F);
        send(data);
        pulse_en(type, data);    // cmd or display data
        
        // Low Nybble
        data = (byte) (value & 0x0F);
        send(data);
        pulse_en(type, data);
    }
    
    /**
     * Toggle the E [Strobe] bit on the LCD Chip causing the chip to
     * read the data written to via the MCP23017 Chip.
     * 
     * @param type  Boolean.  True for Character Mode (write to display)
     *              False for Command Mode (write to LCD Chip register)
     * @param data  The data to be sent.  This byte only contains four
     *              bits of the actual data since the MCP to LCD interface
     *              is in 4 bit mode.  The three LSBs are will contain
     *              the Control bits (RS, RW, and E).
     *              
     * @throws Exception
     */
    private void pulse_en(boolean type, byte data) 
            throws Exception
    {
        setE(true);
        send(data);
        
        if ( type == LCD_CMD )
        {
            // wait a minimum of 50 microseconds for command to be processed
           TimeUnit.MICROSECONDS.sleep(55);
        }
        
        setE(false);
        send(data);
    }
    
    /**
     * Turns an individual bit on or off based on the flag.
     * 
     * @param value The data to be altered.
     * @param flag  Boolean. True to set the bit.  False to clear the bit
     * @param bit   Bit Mask.  Only one bit should be set.
     * 
     * @return Returns the original value with the specified bit set or cleared.
     */
    private int setBit(int value, boolean flag, int bit)
    {
        if ( flag )
        {
            value |= bit;
        }
        else
        {
            value &= ~bit;
        }
        return value;
    }
    
    /**
     * Sends a byte of data to the MCP23017 Chip.  The data is passed on to
     * the LCD Chip.
     * 
     * This method only sends the data to the MCP23017/LCD Chips.  Multiple
     * send()'s are needed to get the LCD Chip to process the data with the
     * Strobe [E] bit off, then on, then off.  The strobing of the E bit is
     * handled by the caller of this method.
     * 
     * @param data  The data to be sent.  Data should already be prepared for
     *              sending one nybble where four bits are data and three
     *              bits are control bits.  The 8th bit is ignored.
     *              
     * @throws Exception
     */
    private void send(byte data)
        throws Exception
    {
        int word = data << 3;
        
        word = setBit(word, rsFlag, RS_Bit);
        word = setBit(word, rwFlag, RW_Bit);
        word = setBit(word, eFlag,  E_Bit);
        
        byte newData = (byte) (word & 0xFF);
        
        mcp_chip.write(newData);
        
        return;
    }
    
    private void setRS(boolean flag)
    {
        rsFlag = flag;
    }
    
    private void setRW(boolean flag)
    {
        rwFlag = flag;
    }
    
    private void setE(boolean flag)
    {
        eFlag = flag;
    }

    private void init()
        throws Exception
    {
        // Initialize Background LEDs
        gpio         = GpioFactory.getInstance();
        
        LCD_RED      = gpio.provisionDigitalOutputPin(GPIO_RED_PIN);
        LCD_GREEN    = gpio.provisionDigitalOutputPin(GPIO_GREEN_PIN);
        LCD_BLUE     = gpio.provisionDigitalOutputPin(GPIO_BLUE_PIN);
        
        setBackground(LCDColor.YELLOW);
        
        // Reset LCD Chip and leave it in 4-bit mode
        reset4Bits();
        
        // Set to 4 bit mode, 2 lines and 5x10 font
        mode(false, true, true);
        
        // display Off, hide cursor, no blinking
        this.display(false, false, false);
        
        // Do not shift display, move cursor right
        this.cursor(false, true);
        
        clear();
        home();
        
        this.display(true, false, false);
    }

    private void  reset4Bits()
        throws Exception
    {
        // Must send the 8 bit reset three times followed by
        // the 4 bit reset command once.
        lcd_byte(LCD_CMD_RESET8, LCD_CMD);
        lcd_byte(LCD_CMD_RESET8, LCD_CMD);
        lcd_byte(LCD_CMD_RESET8, LCD_CMD);
        lcd_byte(LCD_CMD_RESET4, LCD_CMD);
     }
    
    private void setPin( GpioPinDigitalOutput pin, boolean high )
    {
        if ( high )
        {
            pin.high();
        }
        else
        {
            pin.low();
        }
    }
    
    public void setBackground(LCDColor  color)
    {
        setPin(LCD_BLUE,  color.useBlue());
        setPin(LCD_GREEN, color.useGreen());
        setPin(LCD_RED,   color.useRed());
        
        return;
    }
    
    public byte getButtonStates()
        throws Exception
    {
        return mcp_chip.getButtonStates();
    }

    public void DumpRegisters()
        throws Exception
    {
        mcp_chip.dumpRegisters();
    }
    
    public void DumpRegistersRepeat()
        throws Exception
    {
        mcp_chip.dumpRegistersRepeat();
    }

}
