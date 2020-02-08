/*
 * TurnCommand.h
 *
 *  Created on: Aug 8, 2017
 *      Author: Steven F. LeBrun
 */

/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */



#ifndef TURNCOMMAND_H_
#define TURNCOMMAND_H_

#include <USBBaseMessage.h>
#include <CarMotors.h>
#include <Motors.h>

namespace RobotCar
{

/**
 * TurnCommand, derived from USBBaseMessage, to send a request
 * to the Arduino to make each of the wheels turn or stop.
 *
 *
 * Serialized Command Format:
 *
 * <table>
 * <tr><th colspan="2">C:\<UID\>:TW:\<opCode\>:\<Speed1\>[:\<Speed2\>[:\<Speed3\>:\<Speed4\>]];</th></tr>
 * <tr><td colspan="2">&nbsp;</td></tr>
 * <tr><th>C</th><td>Message Type - Command</td></tr>
 * <tr><th>\<UID\></th><td>Unique ID</td></tr>
 * <tr><th>TW</th><td>Command Type - Turn Wheels</td></tr>
 * <tr><th>\<opCode\></th><td>
 *    <table>
 *    <tr><th>opCode></th><th>Meaning</th></tr>
 *    <tr><th>1</th><td>Set all four wheels to the same speed as defined in \<Speed1\></td></tr>
 *    <tr><th>2</th><td>Set both left wheels to \<Speed1\> and both right wheels to \<Speed2\></td></tr>
 *    <tr><th>4</th><td>Set each wheel individually to the following speeds:<br />
 *                      Front Left&nbsp;&nbsp;to \<Speed1\><br />
 *                      Front Right to \<Speed2\><br />
 *                      Rear&nbsp;&nbsp;Left&nbsp;&nbsp;to \<Speed3\><br />
 *                      Rear&nbsp;&nbsp;Right to \<Speed4\></td></tr>
 *    </table>
 *    </td></tr>
 *
 * </table>
 *
 *    For opCode 1, Speed2, Speed3, Speed4 not used and do not need to be
 *    provided.
 *
 *    For opCode 2, Speed3 and Speed4 not used and do not need to be provided.
 */
class TurnCommand: public virtual USBBaseMessage
{
protected:
	int   speeds[5];
	int   directions[5];

	int     error_id;
	String  error_msg;

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

	TurnCommand(USBMessage::MsgType  mType,
			    int                  msgId,
			    CmdType              cType,
			    const String**       tokens,
			    const int            nTokens);

	/**
	 * The Virtual Destructor for this class.
	 */
	virtual ~TurnCommand();

	/**
	 * The command to be run when this command message is received by the Arduino.
	 *
	 * This command causes the Arduino to send commands to the motor controller
	 * shield that results in the four wheel DC motors to run.  A Response Message
	 * back to the sender with the results.
	 *
	 * Serialized Response Message:
	 *
	 * <table>
	 * <tr><th colspan="2">R:\<UID\>:TW:\<FLSpeed\>:\<FRSpeed\>:\<RLSpeed\>:\<RRSpeed\>;</th></tr>
	 * <tr><td colspan="2">&nbsp;</td></tr>
	 *
	 * <tr><th>\<FLSpeed\></th><td>Speed of Front Left Wheel</td></tr>
	 * <tr><th>\<FRSpeed\></th><td>Speed of Front Right Wheel</td></tr>
	 * <tr><th>\<RLSpeed\></th><td>Speed of Rear Left Wheel</td></tr>
	 * <tr><th>\<RRSpeed\></th><td>Speed of Rear Right Wheel</td></tr>
	 * </table>
	 *
	 */
	void runCommand();

protected:
	bool parseArguments();

	bool parseAllWheels();

	bool parseSideWheels();

	bool parseEachWheel();

	void setWheel(int wheel, int speed);
};

} /* namespace RobotCar */

#endif /* TURNCOMMAND_H_ */
