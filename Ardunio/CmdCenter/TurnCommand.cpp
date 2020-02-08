/*
 * TurnCommand.cpp
 *
 *  Created on: Aug 8, 2017
 *      Author: steven
 */

/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */



#include <TurnCommand.h>
#include <Motors.h>

namespace RobotCar
{

TurnCommand::TurnCommand(USBMessage::MsgType  mType,
		  	  	  	     int                  id,
						 CmdType              cType,
						 const String**       tokens,
						 const int            nTokens) :
			USBBaseMessage( mType, id, cType, tokens, nTokens),
			error_id(0)
{
	int i;
	for ( i = 0 ; i <= Motors::LAST_MOTOR ; ++i )
	{
		speeds[i] = 0;
		directions[i] = RELEASE;
	}

}

TurnCommand::~TurnCommand()
{
	// TODO Auto-generated destructor stub
}

void TurnCommand::runCommand()
{
	Motors * motors = CarMotors::getInstance();

	if ( parseArguments() )
	{
		motors->SpinWheels(speeds, directions);

		startResponse();
		int i;
		for ( i = Motors::FIRST_MOTOR ; i <= Motors::LAST_MOTOR ; ++i )
		{
			addResponse( String(speeds[i]) );
		}
	}
	else
	{
		startErrorResponse(error_id);
		addResponse(error_msg);
	}

	endResponse();

	sendResponse();

}

bool TurnCommand::parseArguments()
{
	if ( nargs < 2 )
	{
		// Send Error Message
		error_id  = 0x0101;
		error_msg = "Syntax Error - Number of Arguments = " + String(nargs);
		return false;
	}

	bool flag = false;

	int opCode = arguments[0]->toInt();

	switch (opCode)
	{
	case 1:
		flag = parseAllWheels();
		break;

	case 2:
		flag = parseSideWheels();
		break;

	case 4:
		flag = parseEachWheel();
		break;

	default:
		error_id  = 0x0102;
		error_msg = "Unknown OpCode[" + String(opCode) + "]";
		break;
	}

	return flag;

}   // end of parseArguments()

void TurnCommand::setWheel(int wheel, int speed)
{
	int abs_speed;
	int direction;

	if ( speed == 0 )
	{
		abs_speed = 0;
		direction = BRAKE;
	}
	if ( speed < 0 )
	{
		abs_speed = - speed;
		direction = BACKWARD;
	}
	else
	{
		abs_speed = speed;
		direction = FORWARD;
	}

	if ( abs_speed > 255 )
	{
		abs_speed = 255;
	}

	speeds[wheel]     = abs_speed;
	directions[wheel] = direction;

	return;

}   // end of setWheels()

bool TurnCommand::parseAllWheels()
{
	int speed = arguments[1]->toInt();

	setWheel(FLWheel, speed);
	setWheel(FRWheel, speed);
	setWheel(RLWheel, speed);
	setWheel(RRWheel, speed);

	return true;
}

bool TurnCommand::parseSideWheels()
{
	if ( nargs < 3 )
	{
		error_id   = 0x0103;
		error_msg  = "OpCode 2, # Arguments = " + String(nargs) + " instead of 3";
		return false;
	}

	int leftSpeed   = arguments[1]->toInt();
	int rightSpeed  = arguments[2]->toInt();

	setWheel(FLWheel, leftSpeed);
	setWheel(FRWheel, rightSpeed);
	setWheel(RLWheel, leftSpeed);
	setWheel(RRWheel, rightSpeed);

	return true;

}

bool TurnCommand::parseEachWheel()
{
	if ( nargs < 5 )
	{
		error_id   = 0x0104;
		error_msg  = "OpCode 4, # Arguments = " + String(nargs) + " instead of 5";
		return false;
	}

	int FLspeed = arguments[1]->toInt();
	int FRspeed = arguments[2]->toInt();
	int RLspeed = arguments[3]->toInt();
	int RRspeed = arguments[4]->toInt();

	setWheel(FLWheel, FLspeed);
	setWheel(FRWheel, FRspeed);
	setWheel(RLWheel, RLspeed);
	setWheel(RRWheel, RRspeed);

	return true;
}



} /* namespace RobotCar */
