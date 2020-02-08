/*
 * StopCommand.h
 *
 *  Created on: Aug 8, 2017
 *      Author: steven
 */

/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */



#ifndef STOPCOMMAND_H_
#define STOPCOMMAND_H_

#include <USBBaseMessage.h>

namespace RobotCar
{
/**
 * The StopCommand class is derived from the USBBaseMessage class.
 * It is the command to immediately stop the robot car from moving.
 * If the car is already stopped, this command does nothing.
 *
 * Serialized Command Format:
 *
 * <table>
 * <tr><th colspan="2">C:\<UID\>:SW;</th></tr>
 * <tr><td colspan="2">&nbsp;</td></tr>
 * <tr><th>C</th><td>Message Type - Command</td></tr>
 * <tr><th>\<UID\></th><td>Unique ID</td></tr>
 * <tr><th>SW</th><td>Command Type - Stop Wheels</td></tr>
 * <table>
 */
class StopCommand: public USBBaseMessage
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
	 *                calling this constructor, the value should be STOP_WHEELS.
	 * @param tokens  The parsed serialized message.  The first token should
	 *                be the MsgType string.
	 * @param nTokens The number of elements in the @b tokens array.
	 */
	StopCommand( USBMessage::MsgType  mType,
	             int                  id,
				 CmdType              cType,
				 const String**       tokens,
				 const int            nTokens);

	/**
	 * The Virtual Destructor for this class.
	 */
	virtual ~StopCommand();

	/**
	 * The command to run when this command message is received by the Arduino.
	 *
	 * This command stops all the wheels from turning.
	 *
	 * Serialized Response Message Format:
	 *
	 * <table>
	 * <tr><th colspan="2">R:\<UID\>:SW;</th></tr>
	 * <tr><td colspan="2">&nbsp;</td></tr>
	 * <tr><th>R</th><td>Message Type - Response</td></tr>
	 * <tr><th>\<UID\></th><td>Unique ID</td></tr>
	 * <tr><th>SW</th><td>Command Type - Stop Wheels</td></tr>
	 * <table>
	 *
	 */
	void runCommand();
};

} /* namespace RobotCar */

#endif /* STOPCOMMAND_H_ */
