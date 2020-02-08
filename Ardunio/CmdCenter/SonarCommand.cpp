/**
 * SonarCommand.cpp
 *
 * @date    July 30, 2017
 * @author  Steven F. LeBrun
 */

/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */



#include "SonarCommand.h"

#define __PIC32MX__ active

#include <AFMotor.h>

#include <Sonar.h>

#define   DEBUG_OFF
#include <Debug.h>

namespace RobotCar
{

SonarCommand::SonarCommand(  USBMessage::MsgType      type,
	     					 int                      msgId,
							 USBBaseMessage::CmdType  cmdType,
							 const String **          tokens,
							 const int                nTokens ) :
	USBBaseMessage(type, msgId, cmdType, tokens, nTokens)
{

}

SonarCommand::~SonarCommand()
{
	// TODO Auto-generated destructor stub
}

void SonarCommand::runCommand()
{
#ifdef  DEBUG
	Serial.write("SonarCommand - run\n");
#endif

	int angle       =  0;
	int repeat      =  1;
	int maxDistance = -1;

	Sonar sonar;

	// Parse Arguments
	// Get Angle to point Sensor
	if ( nargs > 0 )
	{
		angle = arguments[0]->toInt();
	}

	// Get number of sensor readings to merge together.
	if ( nargs > 1 )
	{
		repeat = arguments[1]->toInt();
	}

	// Get new Maximum Range or Distance for sensor to search.
	if ( nargs > 2 )
	{
		maxDistance = arguments[2]->toInt();
		sonar.changeMaxRange(maxDistance);
	}

	// Do Range Finding.
	int distanceCM = sonar.findRange(angle, repeat);

#ifdef   DEBUG
	Serial.write("Distance = ");
	writeInt(distanceCM);
	Serial.write(" cm\n");
#endif

	startResponse();
	addResponse(String(distanceCM));
	addResponse(String(angle));
	endResponse();

	sendResponse();

	return;
}

} /* namespace RobotCar */
