/**
 * USBBaseMessage.h
 *
 * @date  July 30, 2017
 * @author Steven F. LeBrun
 */

/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */



#ifndef USBBASEMESSAGE_H_
#define USBBASEMESSAGE_H_

#include  <Arduino.h>

#include  <USBMessage.h>

namespace RobotCar
{

/**
 * Abstract class that defines all the different Command Messages.
 *
 * @ref USBMessage The class that holds the serialized version of a message.
 */
class USBBaseMessage
{
	public:
		/**
		 * @enum CmdType
		 *
		 * An enumeration type that specifies what type of command the message is.
		 */
		enum CmdType
		{
			/**
			 * Command to use the Ultrasonic Sensor to determine the distance
			 * of any object that is in front of the robot car.
			 */
			FIND_RANGE,

			/**
			 * Command to cause the wheels to start turning, stop turning or
			 * to change the current speed.
			 */
			TURN_WHEELS,

			/**
			 * Command to stop the wheels from turning.  This is a special
			 * case of the TURN_WHEELS command with some extra meanings.
			 */
			STOP_WHEELS,

			/**
			 * Command to get a status report from the Arduino.
			 *
			 * @todo Implement the Status Report Command.
			 */
			STATUS_REPORT,

			/**
			 * The object is of an unknown command.  This is also used if
			 * the message object is incomplete and/or non-functional.
			 */
			UNKNOWN
		};

		/**
		 * String for the STOP_WHEELS command.
		 */
		static const String STOP_WHEELS_TOKEN;

		/**
		 * String for the TURN_WHEELS command.
		 */
		static const String TURN_WHEELS_TOKEN;

		/**
		 * String for the FIND_RANGE command.
		 */
		static const String FIND_RANGE_TOKEN;

		/**
		 * String for the STATUS_REPORT command.
		 */
		static const String STATUS_REPORT_TOKEN;

		/**
		 * String for the Response Message Type.
		 */
		static const String RESPONSE_TOKEN;

		/**
		 * String for the Error Message Type.
		 */
		static const String ERROR_TOKEN;

		/**
		 * Creation Function that takes an array of tokens, usually generated
		 * by from a serialized message, and turns in into a Message Object
		 * of the appropriate type based on the Cmd Type string in tokens.
		 *
		 * @param tokens  An Array of String [pointers].  Each element contains
		 *                a token from the serialized message.
		 * @param nTokens Number of elements in the Tokens array.
		 *
		 * @return Returns the binary Message object for the Command Type
		 *         specified in the tokens array.  If the Command Type does
		 *         not match any known commands, a @b null value is returned.
		 */
		static USBBaseMessage * createInstance( const String** tokens, const int nTokens );

		/**
		 * Virtual method that each derived Command Message class must implement.  This
		 * is the method that performs the task of the command on the Arduino board.
		 */
		virtual void runCommand() = 0;

		/**
		 * Initializes a serialize message string that contains the Response to
		 * executing this command.  The response string starts with the Response
		 * Message Type followed by the UID and Command Type from the Command
		 * message that generated this response.
		 */
		void startResponse();

		/**
		 * Initializes a serialize message string that contains the Error
		 * Message caused by executing this command.  The error string starts
		 * with the Error Message Type followed by the UID, Command Type from the Command
		 * message that generated this response, and the Error Code.
		 *
		 * @param errorCode A numeric Error code.
		 *
		 * @todo Add a Error Message field to the error message that is
		 *       16 characters or less so it can be displayed on the LCD
		 *       Display on the Raspberry Pi.
		 */
		void startErrorResponse( int errorCode );

		/**
		 * Adds a token to the Response Message or Error Message being built
		 * using the delimiter string defined in USBMessage.h as a separator
		 * between tokens.
		 */
		void addResponse( const String value );

		/**
		 * Terminates a Response or Error Message by appending the terminator
		 * string, as defined in USBMessage.h.
		 */
		void endResponse();

		/**
		 * Writes the Response or Error Message to the Serial Port which
		 * is the USB connector.
		 */
		void sendResponse();

	protected:
		/**
		 * Message Type enum value for the incoming message.
		 * The string value of this enum is the first token in the
		 * serialized message.
		 */
	    USBMessage::MsgType  msgType;

	    /**
	     * Command Type enum value for the incoming message.
	     * The string value of this enum is the third token in the
	     * serialized message.
	     */
	    CmdType              cmdType;

	    /**
	     * The Unique ID, /<UID/>, if the incoming message.
	     * The string value of this integer is the second token in the
	     * serialized message.
	     */
	    int                  msgId;

	    /**
	     * The Command Type token string.
	     */
	    String               cmdToken;

	    /**
	     * An array of tokens from the serialized message starting with
	     * the first token after the Command Type token.  If there are
	     * no arguments after the Command Type token, this array will
	     * be set to @b null.
	     */
		String **            arguments;

		/**
		 * The number of tokens in the arguments array.  If there are
		 * no arguments, this variable is set to @b null.
		 */
		int                  nargs;

		/**
		 * Buffer used to build the serialized Response or Error Message.
		 */
		String               response;

		/**
		 * Flag that denotes if the Response or Error Message is still
		 * under construction (@b true) or has been terminated (@b false).
		 */
		bool                 openResponse;

		/**
		 * Local holder for the delimitor string defined in the USBMessage.h
		 * file.
		 */
		static const String  Separator;

		/**
		 * Local holder for the terminator string defined in the USBMessage.h
		 * file.
		 */
		static const String  Terminator;

		/**
		 * Root Constructor for the base class.
		 *
		 * @note This constructor is not public.  This requires that the
		 *       Creation of a USBMessage Derived type go through the
		 *       Creation Factory method.
		 *
		 * @param  mtype   The Message Type associated with the incoming message.
		 * @param  id      The Unique ID assigned to the incoming message.
		 * @param  cType   The Command Type associated with the incoming message.
		 * @param  tokens  The array of tokens from the serialized incoming message.
		 * @param  nTokens The number of tokens in the tokens array.
		 */
		USBBaseMessage( USBMessage::MsgType  mType,
				        int                  id,
						CmdType              cType,
						const String**       tokens,
						const int            nTokens );

	public:
		/**
		 * The virtual destructor for the base class.
		 */
		virtual ~USBBaseMessage();

	private:
		/**
		 * Determines what the Command Type enum value is for the incoming message.
		 *
		 * @return  Returns the CmdType enum value for the message command type.
		 */
		CmdType whatCommand();

		/**
		 * Converts a Command Type token (string) into a CmdType enum value.
		 *
		 * @param  token The Command Type Token String.
		 *
		 * @return Returns the appropriate CmdType enum value for the the command
		 *         type token string.  If the token string does not match any of
		 *         the known commands, the value CmdType::UNKNOWN is returned.
		 */
		static CmdType whatCommand( const String token);

};   // end of class USBBaseMessage


} /* namespace RobotCar */

#endif /* USBBASEMESSAGE_H_ */
