/**
 * 
 */
package com.lebruns.steven.robotcar;


import java.util.HashMap;
import java.util.Map;

import com.pi4j.io.i2c.I2CDevice;

/**
 * The MCP23017 class provides access to the MCP23017 chip which is
 * the 16-Bit I/O Expander with Serial Interface chip on the LCD Module.
 * This chip comm44780U (LCD Matrix Display) chip on the LCD Module.
 * 
 * @author Steven F. LeBrun
 *
 */
@SuppressWarnings("unused")
public class MCP23017
{
    
    //========================================================================
    // Constants
    //========================================================================
    
    //========================================================================
    // Private Data Members
    //========================================================================

    private I2CDevice  iodevice;
    
    private MCP_REGISTERS registers;
    
    private MCP_IOCON     iocon;
    
    private MCP_BANK      bank;
    
    private byte          Buttons  = 0x00;
    
    
    //========================================================================
    // Define Enum Types
    //========================================================================

    /**
     * Enum type to represents the different BANK settings in the MCP23017.
     * 
     * There are two Banks available: Bank 1 and Bank 0
     * 
     * @author Steven F. LeBrun
     *
     */
    public enum MCP_BANK { 
        ONE(1), 
        ZERO(0);
        
        private int BANK_BIT = 0x80;
        
        private int value;
        
        private  MCP_BANK( int value )
        {
            this.value = value;
        }
        
        public int get_bit()
        {
            // if Bank Zero, return 0.  Otherwise return BANK_BIT
            return BANK_BIT * value;
        }
        
        public int get_value()
        {
            return value;
        }
        
    }  // end of enum MCP_BANK
    

    /**
     * Enum type to represent the different I/O ports on the MCP23017.
     * 
     * There are two I/O ports, Port A and Port B.  Each port has 8 bits of
     * data that can be read or written.
     * 
     * @author Steven F. LeBrun
     *
     */
    public enum MCP_PORT 
    { 
        PORT_A("A"),
        PORT_B("B");
        
        private String short_name;
        
        private MCP_PORT(String letter)
        {
            short_name = letter;
            
        }
        /**
         * @return Returns the number of members in enum
         */
        public int  size()
        {
            // Make sure that the enum member used is the last one defined.
            return (PORT_B.ordinal() + 1);
        }
        
        public String getShortName()
        {
            return short_name;
        }
    }   // end of enum MCP_PORT
 
    
    /**
     * Enum Type that represents the different registers available for
     * each Port on the MCP23017.
     * 
     * Note that the register IOCON is actually the same register used 
     * by both Port A and Port B even though it has a different address
     * for each port.
     * 
     * @author Steven F. LeBrun
     *
     */
    // MCP23017 Register Names
    // These names are independent on PORTA and PORTB.
    public enum MCP_REGISTER
    {
        IODIR,   // I/O Direction Register
        IPOL,    // Input Polarity Port Register
        GPINTEN, // Interrupt-OnChange Control Register
        DEFVAL,  // Default Value Register
        INTCON,  // Interrupt Control Register
        IOCON,   // I/O Expander Configuration Register
        GPPU,    // GPIO Pull-Up Resistor Register
        INTF,    // Interrupt Flag Register
        INTCAP,  // Interrupt Capture Value for Port Register
        GPIO,    // General Purpose I/O Port Register
        OLAT;    // Output Latch Register
        
        /**
         * @return Returns the number of members in enum
         */
        public int  size()
        {
            // Make sure that the enum member used is the last one defined.
            return (OLAT.ordinal() + 1);
        }

    }   // end of enum MCP_REGISTER

    
    //========================================================================
    // Constructors
    //========================================================================
    
    public MCP23017(I2CDevice device, boolean use_bank_1)
        throws Exception
    {
        iodevice = device;
        
        bank =  ( use_bank_1 ) ? MCP_BANK.ONE : MCP_BANK.ZERO;
        
        // Create Initial IOCON register value to be used when intializing
        // the MCP23017 chip.
        iocon = new MCP_IOCON(use_bank_1);
        
        iocon.enable_separate_interrupts();
        iocon.enable_byte_mode();
        
        // Initialize Register Addresses
        MCP_BANK bank = ( use_bank_1 ? MCP_BANK.ONE : MCP_BANK.ZERO );
        registers = new MCP_REGISTERS(bank);
        
        init();
       
        return;
    }
    

    
    //========================================================================
    // Nested Classes
    //========================================================================
    
    private class MCP_IOCON
    {
        // Bit Masks for the IOCON register
        private int BANK_BIT   =  0x80;
        private int MIRROR_BIT =  0x40;
        private int SEQOP_BIT  =  0x20;
        private int DISSLW_BIT =  0x10;
        private int HAEN_BIT   =  0x08;
        private int ORD_BIT    =  0x04;
        private int INTPOL_BIT =  0x02;
        private int UNUSED_BIT =  0x01;
        
        private byte register  =  0x00;
        
        public MCP_IOCON(boolean use_bank_1)
        {
            // Clear all bits.
            register = 0x00;
            
            if ( use_bank_1 )
            {
                enable_bank_1();
            }
            else
            {
                enable_bank_0();
            }
            
            return;
        }   // end of MCP_IOCON Constructor
        
        public byte get()
        {
            return register;
        }
        
        public void enable_bank_1()
        {
            register |= BANK_BIT;
        }
        
        public void enable_bank_0()
        {
            register &= ~BANK_BIT;
        }
        
        public void enable_single_interrupt()
        {
            register  |= MIRROR_BIT;
        }
        
        public void enable_separate_interrupts()
        {
            register &= ~MIRROR_BIT;
        }
        
        public void enable_sequential_mode()
        {
            register &= ~SEQOP_BIT;
        }
        
        public void enable_byte_mode()
        {
            register |= SEQOP_BIT;
        }
        
        // Enable methods for DISSLW, HAEN, ODR, and INTPOL are
        // not implemented at this time.  If we find a need for
        // any of them being set to their non-zero values, the 
        // methods will be implemented at that time.
        
    }   // end of class MCP23017.MCP_IOCON
    
    public class MCP_KEY
    {
        private  MCP_REGISTER   register;
        private  MCP_PORT       port;
        
        public MCP_KEY( MCP_REGISTER reg, MCP_PORT  gpio)
        {
            register = reg;
            port     = gpio;
        }
        
        public MCP_KEY getKey( MCP_REGISTER reg, MCP_PORT gpio )
        {
            MCP_KEY newKey = new MCP_KEY(reg, gpio);
            
            return newKey;
        }
        
        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
            {
                // Same object. Its a match
                return true;
            }
            
            if ( !(obj instanceof MCP_KEY) )
            {
                // Wrong type of object.  Not a match
                return false;
            }
            
            MCP_KEY other = (MCP_KEY) obj;
            
            boolean match = ( (other.register == this.register) &&
                              (other.port     == this.port));
            
            return match;
            
        }   // end of equals()
        
        @Override
        public int hashCode()
        {
            int result = register.ordinal() + 
                    (port.ordinal() * register.size());
            
            return result;
        }
        
    }   // end of class MCP_KEY
    
    /**
     * Class to manage the Addresses of the MCP23017 Registers
     * 
     * @author Steven F. LeBrun
     *
     */
    public class MCP_REGISTERS
    {
        /**
         * Defines a two dimensional Map that contains the addresses of
         * the MCP23017 Registers.  The actual value of the addresses
         * is based on which Bank is being used.  Changing the Bank
         * results in the addresses changing.
         * 
         * Note: MCP_KEY is comprised of an MCP_REGISTER and MCP_PORT
         *       value.  Thus making this a two dimensional map.
         */
        private Map<MCP_KEY, Integer> addresses = new HashMap<MCP_KEY, Integer>();
        
        /**
         * Boolean used to determine when it is necessary to update
         * the map of register addresses.  TRUE means it needs to be updated.
         */
        private  boolean  update_addresses = true;
        
        /**
         * Denotes which BANK is being used.
         */
        private  MCP_BANK   bank;
        
        /**
         * Constructor for MCP_REGISTERS class.
         * 
         * @param newBank  The BANK to initially use.
         */
        public MCP_REGISTERS( MCP_BANK  newBank )
            throws Exception
        {
            setBank(newBank);
        }
        
        /**
         * Setter for the Bank.
         * 
         * @param newBank  The BANK used to determine how the MCP23017
         *                 Registers will be addressed.
         *                 
         * Calling this method will result in the register addresses
         * being recalibrated, even if the actual BANK remains the same.
         */
        public void setBank( MCP_BANK newBank )
        {
            bank = newBank;
            
            update_addresses = true;
            
            return;
        }
        
        /**
         * Getter for the Bank.
         * 
         * @return Returns the current BANK value being used.
         */
        public MCP_BANK getBank()
        {
            return bank;
        }
        
        /**
         * Obtains the current address for the specified register.
         * 
         * @param register  The register of interest.
         * @param port      The register for this Port.
         * 
         * This method returns the current address assigned to the
         * specified register in the specified port.  If the map
         * of addresses are out of date, due to a change in BANK mode,
         * this method automatically causes the addresses to be 
         * recalculated before returning the result.
         * 
         * @return  Returns the address of the specified register.
         */
        public int getAddress( MCP_REGISTER register, MCP_PORT port)
        {
            if ( update_addresses )
            {
                setAddresses();
            }
            
            MCP_KEY  key = new MCP_KEY(register, port);
            
            return addresses.get(key).intValue();
        }
        
        /**
         * Master method for setting the register addresses.
         * This method uses the current BANK value to determine
         * which actual method to use for setting addresses.
         */
        private void setAddresses()
        {
            switch (bank)
            {
            case  ONE:
                setBank1Addresses();
                break;
                
            case ZERO:
                setBank0Addresses();
                break;
                
            default:
                System.out.printf("Unknown MCP BANK [%s].\n", bank.name());
                break;
            }
            
            update_addresses = false;
            
            return;
        }
        
        /**
         * Sets register addresses when in BANK 0 mode.
         * 
         * For BANK 0, the equivalent registers for Port A and Port B
         * have consecutive addresses.
         */
        private void setBank0Addresses()
        {
            int  address  = 0x00;
            
            //*** Debug Start
//            System.out.printf("Setting Registry Addresses for BANK %s\n", 
//                              bank.name());
            //*** Debug End
            
            for ( MCP_REGISTER rKey : MCP_REGISTER.values() )
            {
                MCP_KEY  aKey = new MCP_KEY(rKey, MCP_PORT.PORT_A);
                MCP_KEY  bKey = new MCP_KEY(rKey, MCP_PORT.PORT_B);
                
                addresses.put(aKey, address);
                ++address;
                addresses.put(bKey, address);
                ++address;
                
                //*** Debug Start
//                System.out.printf("Register<%-7s,%s> @ 0x%02X\n", 
//                        rKey.name(), MCP_PORT.PORT_A.name(), 
//                        addresses.get(aKey));
//                System.out.printf("Register<%-7s,%s> @ 0x%02X\n", 
//                        rKey.name(), MCP_PORT.PORT_B.name(), 
//                        addresses.get(bKey));
                //*** Debug End
            }
            
            return;            
        }
        
        /**
         * Sets register addresses when in BANK 1 mode.
         * 
         * In BANK 1 Mode, the registers for Port A are in one bank of
         * addresses while the Port B registers are in a different bank.
         */
        private void setBank1Addresses()
        {
            int  address    = 0x00;
            int  port_delta = 0x10;
            
            //*** Debug Start
//            System.out.printf("Setting Registry Addresses for BANK %s\n", 
//                              bank.name());
            //*** Debug End
            
            for ( MCP_REGISTER rKey : MCP_REGISTER.values() )
            {
                MCP_KEY  aKey = new MCP_KEY(rKey, MCP_PORT.PORT_A);
                MCP_KEY  bKey = new MCP_KEY(rKey, MCP_PORT.PORT_B);
                
                addresses.put(aKey, address);
                addresses.put(bKey, (address + port_delta));
                ++address;
                
                //*** Debug Start
//                System.out.printf("Register<%7s,%s> @ 0x%02X\n", 
//                        rKey.name(), MCP_PORT.PORT_A.name(), 
//                        addresses.get(aKey));
//                System.out.printf("Register<%7s,%s> @ 0x%02X\n", 
//                        rKey.name(), MCP_PORT.PORT_B.name(), 
//                        addresses.get(bKey));
                //*** Debug End
            }
            
            return;            
        }
        
    }   // end of class MCP_REGISTER
    
    /**
     * Initializes the MCP23017 [I/O Expander with Serial Interface] Chip.
     * 
     * - Sets BANK mode to 1.  
     * - Initializes PORT A for output to the LCD Chip.
     * - Initializes PORT B for input from buttons.
     * 
     * 

     *
     */
    private  void  init()
        throws Exception
    {
        //*** Debug Start
//        System.out.println("Initializing MCP23017 Chip");
//        dumpRegisters();
        //*** Debug End
        
        int   register;
        byte  data;
        
        // Prepare IOCON Register value
        iocon.enable_byte_mode();
        iocon.enable_separate_interrupts();
        
        // First set the IOCON Register
        //
        // NOTE: Even though there are two IOCON registers, one for Port A and
        //       one for Port B, they are actually the same register.
        register = registers.getAddress(MCP_REGISTER.IOCON, MCP_PORT.PORT_A);
        iodevice.write(register, iocon.get());

        //*** Debug Start
//        System.out.printf("IOCON-A at 0x%02X to 0x%02X\n", register, iocon.get());
//        dumpRegisters();
        //*** Debug End
        
        register = registers.getAddress(MCP_REGISTER.IOCON, MCP_PORT.PORT_B);
        iodevice.write(register, iocon.get());
        
        //*** Debug Start
//        System.out.printf("IOCON-B at 0x%02X to 0x%02X\n", register, iocon.get());
//        dumpRegisters();
        //*** Debug End
        
        
        // Set IODIR for Port A -- Output on all pins
        register = registers.getAddress(MCP_REGISTER.IODIR, MCP_PORT.PORT_A);
        data     = 0x00;  // setting all pins to output mode
        iodevice.write(register, data);

        //*** Debug Start
//        System.out.printf("IODIR-A at 0x%02X to 0x%02X\n", register, data);
//        dumpRegisters();
        //*** Debug End

        // Set IODIR for Port B -- Input for first 5 pins
        register = registers.getAddress(MCP_REGISTER.IODIR, MCP_PORT.PORT_B);
        data     = 0x1F;  // Setting 5 LSBs for Input.
        iodevice.write(register, data);
        
        //*** Debug Start
//        System.out.printf("IODIR-B at 0x%02X to 0x%02X\n", register, data);
//        dumpRegisters();
        //*** Debug End
        
        // Set IPOL so that GPIO port bits match the logic of the actual pins.
        // In other words, both are high at the same time and low at the same 
        // time.
        data = 0x00; // register bits reflect the same logic state of the 
                     // input pins.  (0xFF for opposite logic state)
        register = registers.getAddress(MCP_REGISTER.IPOL, MCP_PORT.PORT_A);
        iodevice.write(register, data);
        
        //*** Debug Start
//        System.out.printf("IPOL-A at 0x%02X to 0x%02X\n", register, data);
//        dumpRegisters();
        //*** Debug End
        
        register = registers.getAddress(MCP_REGISTER.IPOL, MCP_PORT.PORT_B);
        iodevice.write(register, data);
        
        //*** Debug Start
//        System.out.printf("IPOL-B at 0x%02X to 0x%02X\n", register, data);
//        dumpRegisters();
        //*** Debug End

        // Set GPINTEN Register
        data = 0x00;
        register = registers.getAddress(MCP_REGISTER.GPINTEN, MCP_PORT.PORT_A);
        iodevice.write(register, data);
        
        //*** Debug Start
//        System.out.printf("GPINTEN-A at 0x%02X to 0x%02X\n", register, data);
//        dumpRegisters();
        //*** Debug End
                
        data = 0x1F;
        register = registers.getAddress(MCP_REGISTER.GPINTEN, MCP_PORT.PORT_B);
        iodevice.write(register, data);
        
        //*** Debug Start
//        System.out.printf("GPINTEN-B at 0x%02X to 0x%02X\n", register, data);
//        dumpRegisters();
        //*** Debug End
        
        // Set the DEFVAL Register
        data = 0x00;
        register = registers.getAddress(MCP_REGISTER.DEFVAL, MCP_PORT.PORT_A);
        iodevice.write(register, data);
        
        //*** Debug Start
//        System.out.printf("DEFVAL-A at 0x%02X to 0x%02X\n", register, data);
//        dumpRegisters();
        //*** Debug End
        
        data = 0x1F;
        register = registers.getAddress(MCP_REGISTER.DEFVAL, MCP_PORT.PORT_B);
        iodevice.write(register, data);
        
        //*** Debug Start
//        System.out.printf("DEFVAL-B at 0x%02X to 0x%02X\n", register, data);
//        dumpRegisters();
        //*** Debug End

        // Set INTCON Register
        data = 0x00;
        register = registers.getAddress(MCP_REGISTER.INTCON, MCP_PORT.PORT_A);
        iodevice.write(register, data);
        
        //*** Debug Start
//        System.out.printf("INTCON-A at 0x%02X to 0x%02X\n", register, data);
//        dumpRegisters();
        //*** Debug End
        
        data = 0x00; // 0x1F;
        register = registers.getAddress(MCP_REGISTER.INTCON, MCP_PORT.PORT_B);
        iodevice.write(register, data);
        
        //*** Debug Start
//        System.out.printf("INTCON-B at 0x%02X to 0x%02X\n", register, data);
//        dumpRegisters();
        //*** Debug End

        // Set GPPU for Port B -- Only needed for Input Pins.
        data = 0x1F;
        register = registers.getAddress(MCP_REGISTER.GPPU, MCP_PORT.PORT_B);
        iodevice.write(register, data);
        
        //*** Debug Start
//        System.out.printf("GPPU-B at 0x%02X to 0x%02X\n", register, data);
//        dumpRegisters();
        //*** Debug End
        
        return;
    }   
    // end of init()
    
    public void write(byte data)
        throws Exception
    {
        int register = registers.getAddress(MCP_REGISTER.GPIO, MCP_PORT.PORT_A);
        iodevice.write(register, data);
    }
    
    public void write(MCP_REGISTER register, MCP_PORT port, byte data)
            throws Exception
        {
            int address = registers.getAddress(MCP_REGISTER.GPIO, MCP_PORT.PORT_A);
            iodevice.write(address, data);
        }
    
    public byte read()
        throws Exception
    {
        int register = registers.getAddress(MCP_REGISTER.GPIO, MCP_PORT.PORT_B);
        byte data = (byte) iodevice.read(register);
        
        return data;
    }
    
    public byte read(MCP_REGISTER register, MCP_PORT port)
        throws Exception
    {
        int address = registers.getAddress(register, port);
        byte data   = (byte) iodevice.read(address);
        
        return data;                
    }
    
    /**
     * Determines if any of the buttons have been pushed that were not
     * already pushed the last time this method was called.
     * 
     * @return Returns a Byte that has a bit set for each button that has
     *         been pushed.
     *         
     * @throws Exception
     */
    public byte getButtonStates()
        throws Exception
    {
        byte buttonBits = 0x00;
        byte data;
        byte interrupt;
        int  changes;
        byte changedBits;
        
        interrupt = this.read(MCP_REGISTER.INTF, MCP_PORT.PORT_B);
        
        if ( interrupt != 0x00 )
        {
            // Change of state detected.  Now we need to determine which
            // pins have changed and how they have changed.
            data = this.read(MCP_REGISTER.GPIO, MCP_PORT.PORT_B);
            
            // Determine which bits have changed.
            changes = (Buttons & ~data) | (~Buttons & data);
            changedBits = (byte) changes;
            
            for ( int bitMask = 0x01 ; bitMask < 0x0100 ; bitMask <<= 1 )
            {
                if ( (bitMask & changedBits) != 0 )
                {
                    // The bit has changed.  Now we need to determine if the
                    // new bit is set or cleared.
                    if ( (bitMask & data) != 0 )
                    {
                        buttonBits |= bitMask;
                    }
                }
            }
            
            // Update the Old Button Values
            Buttons = data;
        } 
        
        return buttonBits;
    }   // end of getButtonStates()

    
    //***  Debug Start
    public void dumpRegisters()
        throws Exception
    {
        int           register;
        byte          data;

        System.out.println("\nMCP23017 Register Dump");
            
        for ( MCP_PORT port : MCP_PORT.values() )
        {
            for (MCP_REGISTER rKey : MCP_REGISTER.values())
            {                
                data     = read(rKey, port);
                System.out.printf("Register<%7s, %s> = 0x%02X\n",
                        rKey.name(), port.name(), data);
            }
                
                
        }  // end of port loop
    }
    
    public void dumpRegistersRepeat()
        throws Exception
    {
        boolean keepGoing = true;
        int     maxIt     = 100;
        
        for (int i = 0 ; keepGoing && i < maxIt ; ++i)
        {
            dumpRegisters();
        }
        
    }
    //***  Debug End
    

}   // End of class MCP23017
