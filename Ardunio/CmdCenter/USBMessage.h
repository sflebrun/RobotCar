/**
 * USBMessage.h
 *
 * Header file that declares the USBMessage Class
 *
 *  @date   July 30, 2017
 *  @author Steven F. LeBrun
 */

/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */

#ifndef USBMESSAGE_H_
#define USBMESSAGE_H_

#include <Arduino.h>

namespace RobotCar
{

#ifndef NUL
#define NUL  '\0'
#endif

#ifndef NULL
#define  NULL ((void *) 0)
#endif

/**
 * The maximum length of a Message sent over an USB connection.
 */
#define BufferMax  64

/**
 * @class USBMessage USBMessage.h
 *
 * @brief The class that represents a message as a string of characters used
 *        to send and receive a message using serialized I/O
 *
 * Messages are encoded and decoded as a string of UTF-8 characters so
 * that they can be sent and received over a Serial connection which on
 * the Arduino is through the USB connector.
 *
 * <h2>Serialized Message Syntax:</h2>
 *
 * <table>
 *  <tr><th colspan="2">\<MsgType\>:\<UID\>:\<CmdType\>[:\<argN\>]\*;</th></tr>
 *  <tr><td colspan="2">&nbsp;</td></tr>
 *
 *  <tr><th rowspan="3">\<MsgType\></th><td>"C" for Command</td></tr>
 *  <tr><td>"R" for Response</td></tr>
 *  <tr><td>"E" for Error</td></tr>
 *  <tr><td colspan="2">&nbsp;</td></tr>
 *
 *  <tr><th>\<UID\></th><td>Integer, Unique ID.  Each Command Message is assigned a UID.</td></tr>
 *  <tr><td colspan="2">&nbsp;</td></tr>
 *
 *  <tr><th rowspan="4">\<CmdType\></th><td>"SW" for Stop Wheels</td></tr>
 *  <tr><td>"TW" for Turn Wheels</td></tr>
 *  <tr><td>"FR" for Find Range</td></tr>
 *  <tr><td>"SR" for Status Report {Not Implemented}</td></tr>
 *  <tr><td colspan="2">&nbsp;</td></tr>
 *
 *  <tr><th>\<argN\></th><td>Argument, CmdType specific.  Zero or more arguments based on CmdType.</td></tr>
 *
 *  <tr><td colspan="2">&nbsp;</td></tr>
 *  <tr><th>:</th><td>Token Separator.</td></tr>
 *  <tr><th>;</th><td>Serial Message Terminator</td></tr>
 *
 *  </table>
 *
 */
class USBMessage
{
	public:
		/**
		 * @enum MsgType
		 *
		 * The enumeration defines the Message Type and/or the state
		 * of the message.
		 */
		enum MsgType
		{
			/**
			 * The message is a Command.
			 * The message state is complete.
			 */
			COMMAND,

			/**
			 * The message is a Response to a Command.
			 * The message state is complete.
			 */
			RESPONSE,

			/**
			 * The message is an error message which may or may not be
			 * in response to a Command.
			 * The message state is complete.
			 */
			ERROR,

			/**
			 * State of a message where there are still more characters to
			 * be read or written before the message is complete.
			 */
			INCOMPLETE,

			/**
			 * State of a message where the number of characters exceeds the
			 * Maximum buffer size defined by BufferMax.
			 */
			OVERFLOW,

			/**
			 * State of a message.  The message string is not in a known state.
			 */
			UNKNOWN
		};

		/**
		 * The string that denotes the Message Type of Command.
		 */
		static const String  COMMAND_TOKEN;

		/**
		 * The string that denotes the Message Type of Response.
		 */
		static const String  RESPONSE_TOKEN;

		/**
		 * The string that denotes the Message Type of Error Message.
		 */
		static const String  ERROR_TOKEN;

		/**
		 * The string that separates fields in the message string.
		 * Set to a colon.  ":"
		 */
		static const  char delimiter;

		/**
		 * The string that terminates a message string.
		 * Set to a semi-colon.  ";"
		 */
		static const  char terminator;

	protected:
		/**
		 * Buffer to hold incoming messages string.
		 */
		char   buffer[BufferMax];

		/**
		 * The number of characters held in the buffer.
		 */
		int    bufferStop;

		/**
		 * Flag that determines if the buffer contains a complete (@b true)
		 * or incomplete (@b false) message string.
		 */
		bool   bufferFull;

		/**
		 * Internal data member used for parsing a serialized message into
		 * a list or array of tokens.
		 */
		String ** tokens;

		/**
		 * The length of the tokens array.
		 */
		int       nTokens;

	public:
		/**
		 * Constructor for USBMessage class
		 */
		USBMessage();

		/**
		 * Virtual Destructor.
		 *
		 * Releases all resources held by this object,
		 * specifically, returning dynamically allocated memory.
		 */
		virtual ~USBMessage();

		/**
		 * Reset this object back to its Constructor state.
		 */
		void reset();

		/**
		 * Add a character to the Buffer.
		 *
		 * @param value The character to be added.
		 *
		 * @return An Type enum that represents the state of the buffer.
		 *         If terminator is detected (buffer full), the type of
		 *         message is returned (Type::COMMAND, Type::RESPONSE or
		 *         Type::ERROR).  If an error occurs such as buffer
		 *         overflow (no terminator found), MsgType::OVERFLOW is returned.
		 *         If the buffer is not full (terminator not reached),
		 *         Type::INCOMPLETE is returned.
		 */
		MsgType put(char value);

		/**
		 * Exposes Token array if the message is complete.
		 *
		 * @return Returns a pointer to the tokens array.  If message is not
		 *         complete, a NULL pointer is returned.  Also, the TokenCnt
		 *         integer is set to the number of tokens in the array.
		 */
		String ** getTokens( int * TokenCnt );

		/**
		 * Determines if the Message Terminator has been detected and
		 * therefore, the USB Message is full or complete.
		 *
		 * @return Returns TRUE if the USB Message is complete.
		 *         Returns FALSE if the USB Message is incomplete.
		 */
		bool full();

		/**
		 * Determines what state the buffer is in or what type of message
		 * the buffer contains.  The buffer is considered full when the
		 * terminator character has been detected.  Before that state is
		 * reached, the buffer is considered to be incomplete.
		 *
		 * @return Returns the MsgType associated with the message if the
		 *         buffer is full.  If the buffer is not full,
		 *         MsgType::INCOMPLETE is returned.  If the MsgType Token does
		 *         not match any of the know message types, MsgType::UNKNOWN
		 *         is returned.
		 */
		MsgType whatType();

		/**
		 * Converts a string into a Message MsgType Enum value.
		 *
		 * @return Returns the appropriate Message MsgType Enum based on the
		 *         type string supplied.  If the string does not match any
		 *         Message MsgType tokens, MsgType::UNKNOWN is returned.
		 */
		static MsgType whatType( const String type );


	private:
		/**
		 * Returns the USB Message object to its original state where there
		 * are no characters in the buffer.
		 */
		void clearBuffer();


		/**
		 * Parses the USB Message buffer into an array of tokens.
		 *
		 * The USB Message, once complete, consists of a string of tokens
		 * separated by colons and terminated with a semi-colon.
		 */
		void parseMessage();

		/**
		 * Determines how many tokens exist in the USB Message buffer.
		 *
		 * @returns Number of token separators and message terminators
		 *          contained in the buffer.
		 */
		int  countTokens();


};   // end of class USBMessage



} /* namespace RobotCar */

#endif /* USBMESSAGE_H_ */
