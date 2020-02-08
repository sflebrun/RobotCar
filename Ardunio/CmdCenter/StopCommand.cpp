/**
 * StopCommand.cpp
 *
 * @date   August 8, 2017
 * @author Steven F. LeBrun
 */

/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */



#include <StopCommand.h>
#include <CarMotors.h>

namespace RobotCar
{

StopCommand::StopCommand( USBMessage::MsgType  mType,
        				  int                  id,
						  CmdType              cType,
						  const String**       tokens,
						  const int            nTokens) :
		USBBaseMessage(mType, id, cType, tokens, nTokens)
{
	// Nothing to do here.
}

StopCommand::~StopCommand()
{
	// Nothing to do.
}

void StopCommand::runCommand()
{
	Motors * motors = CarMotors::getInstance();

	motors->StopWheels();

	// Respond Back
	// Syntax:  R:#:SW;
	startResponse();
	endResponse();

	sendResponse();
}

} /* namespace RobotCar */
