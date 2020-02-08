/*
 * Debug.cpp
 *
 *  Created on: Aug 7, 2017
 *      Author: steven
 */

#include <Arduino.h>
#include <Debug.h>


namespace RobotCar
{


// *** Begin Debug ***
/**
 * Write an Integer Value to debug output as a string.
 *
 * @param value Integer value to output.
 */
void writeInt(int value)
{
	String number = String(value);

	int nChars    = number.length() + 1;
	char buffer[sizeof(char)*nChars];

	number.toCharArray(buffer, nChars);

	Serial.write(buffer);
}

/**
 * Write a Long Value to debug output as a string.
 *
 * @param value Long value to output.
 */
void writeLong(long value)
{
	String number = String(value);

	int nChars    = number.length() + 1;
	char buffer[sizeof(char)*nChars];

	number.toCharArray(buffer, nChars);

	Serial.write(buffer);
}


/**
 * Write a String value to the Debug Output.
 *
 * This is the method that actually does the writing.
 *
 * @param value String to be written to Debug Output.
 */
void writeString(String value)
{
	int nChars    = value.length() + 1;
	char buffer[sizeof(char)*nChars];

	value.toCharArray(buffer, nChars);

	Serial.write(buffer);
}


/**
 * Convert a USBMessage::MsgType enum value into a string so it
 * can be written to the Debug Output.
 */
void writeMsgType(USBMessage::MsgType msgType)
{
	const char * buffer;

	switch (msgType)
	{
	case USBMessage::MsgType::COMMAND:
		buffer = "Command";
		break;

	case USBMessage::MsgType::ERROR:
		buffer = "Error";
		break;

	case USBMessage::MsgType::INCOMPLETE:
		buffer = "Incomplete";
		break;

	case USBMessage::MsgType::OVERFLOW:
		buffer = "Overflow";
		break;

	case USBMessage::MsgType::RESPONSE:
		buffer = "Response";
		break;

	case USBMessage::MsgType::UNKNOWN:
		buffer = "Unknown";
		break;

	default:
		buffer = "Illegal";
		break;
	}

	Serial.write(buffer);
}

// *** End   Debug ***


} /* namespace RobotCar */
