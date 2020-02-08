/*
 * SonarCommand.h
 *
 *  Created on: Jul 30, 2017
 *      Author: steven
 */

/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */



#ifndef SONARCOMMAND_H_
#define SONARCOMMAND_H_

#include "USBBaseMessage.h"

namespace RobotCar
{

/**
 * SonarCommand, derived from USBBaseMessage is sent to request
 * a range or distance finding from an Ultrasonic Sensor.
 *
 * Serialized Command Format:
 *
 * <table>
 * <tr><th colspan="2">&nbsp;C:\<UID\>:FR:\<angle\>:\<repeat\>[:\<maxDistanceCM\>];</th></tr>
 * <tr><td colspan="2">&nbsp;</td></tr>
 * <tr><th>C</th><td>Message Type - Command</td></tr>
 * <tr><th>\<UID\></th><td>Unique ID</td></tr>
 * <tr><th>FR</th><td>Command Type - Find Range</td></tr>
 * <tr><th>\<angle\></th><td>Direction to point Sensor in degrees.<br />
 * Range -90 to +90.  <br /> 0 Degrees
 * points straight ahead from the prospective of the robot car.</td></tr>
 * <tr><th>\<repeat\></th><td>Number of measurements to take and average together.<br />
 * Values less than 1 are treated as take one measurement.</td></tr>
 * <tr><th>\<maxDistanceCM\></th><td>[optional] Maximum distance in centimeters to search.</td></tr>
 * </table>
 *
 */
class SonarCommand: public USBBaseMessage
{
public:
	/**
	 * Constructor for this class.  The parameters passed are usually obtained
	 * decoding a serialized message.
	 *
	 * @param type    The MsgType enum value for the message.  If we are
	 *                calling this constructor, the value should be COMMAND.
	 * @param msgId   The unique ID assigned to the message by the sender.
	 * @param cmdType The CmdType enum value for the message.  If we are
	 *                calling this constructor, the value should be FIND_RANGE.
	 * @param tokens  The parsed serialized message.  The first token should
	 *                be the MsgType string.
	 * @param nTokens The number of elements in the @b tokens array.
	 */
	SonarCommand( USBMessage::MsgType      type,
			      int                      msgId,
				  USBBaseMessage::CmdType  cmdType,
				  const String **          tokens,
				  const int                nTokens );

	/**
	 * The Virtual Destructor for this class.
	 */
	virtual ~SonarCommand();

	/**
	 * The command to be run when this command message is received by the Arduino.
	 *
	 * This command turns the sensor and runs one or more range finding
	 * measurements and returns a Response Message back to the sender with the
	 * results.
	 *
	 * Serialized Response Message:
	 *
	 * <table>
	 * <tr><th colspan="2">R:\<UID\>:FR:\<distanceCM\>:\<angle\>;</th></tr>
	 * <tr><td colspan="2">&nbsp;</td></tr>
	 * <tr><th>R</th><td>Message Type - Response</td></tr>
	 * <tr><th>\<UID\></th><td>Unique ID.<br />  This value is taken from the
	 * Command Message that is being answered.</td></tr>
	 * <tr><th>FR</th><td>Response to Command Type - Find Range</td></tr>
	 * <tr><th>\<angle\></th><td>Direction sensor was point in degrees.<br />
	 * 0 degrees is straight ahead from the robot car perspective.</td></tr>
	 * <tr><th>\<distanceCM\></th><td>Distance to an object detected in
	 * centimeters.<br />If no object is detected, this value is set to zero.</td></tr>
	 * </table>
	 */
	void runCommand();

};   // end of class SonarCommand

} /* namespace RobotCar */

#endif /* SONARCOMMAND_H_ */
