/*
 * USBBaseMessage.cpp
 *
 * Message Syntax:
 *    <MsgType>:<UID>:<CmdType>[:<argN>]*;
 *
 *  <MsgType> == "C" for Command
 *               "R" for Response
 *               "E" for Error
 *
 *  <UID>     == Integer, Unique ID.  Each Command Message is assigned a UID
 *
 *  <CmdType> == "SW" for Stop Wheels
 *               "TW" for Turn Wheels
 *               "FR" for Find Range
 *               "SR" for Status Report {Not Implemented}
 *
 *  <argN>    == Argument, CmdType specific.  Zero or more arguments
 *
 *  Created on: Jul 30, 2017
 *      Author: Steven F. LeBrun
 */


/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */



#include <Arduino.h>

#include  <USBMessage.h>
#include  <USBBaseMessage.h>

#include  <SonarCommand.h>
#include  <StopCommand.h>
#include  <TurnCommand.h>

namespace RobotCar
{

const String USBBaseMessage::STOP_WHEELS_TOKEN   = "SW";
const String USBBaseMessage::TURN_WHEELS_TOKEN   = "TW";
const String USBBaseMessage::FIND_RANGE_TOKEN    = "FR";
const String USBBaseMessage::STATUS_REPORT_TOKEN = "SR";

const String USBBaseMessage::RESPONSE_TOKEN      = "R";
const String USBBaseMessage::ERROR_TOKEN         = "E";

const String USBBaseMessage::Separator    = String(USBMessage::delimiter);
const String USBBaseMessage::Terminator   = String(USBMessage::terminator);


USBBaseMessage::USBBaseMessage( USBMessage::MsgType  mType,
		                        int                  mid,
								CmdType              cType,
								const String**       tokens,
								const int            nTokens) :
				msgType(mType),
				cmdType(cType),
				msgId(mid),
				arguments(NULL),
				nargs(0),
				response(""),
				openResponse(false)
{
	if ( nTokens <= 3 )
	{
		// Nothing to do.
	}

	cmdToken = String(*tokens[2]);

	nargs = nTokens - 3;

	if ( nargs > 0 )
	{
		arguments = new String*[nargs];

		int i,j;

		for ( i = 0, j = 3 ; j < nTokens ; ++i, ++j )
		{
			if ( tokens[j] != NULL )
			{
				arguments[i] = new String(*tokens[j]);
			}
		}
	}

}   // end of Constructor


USBBaseMessage::~USBBaseMessage()
{
	if ( arguments != NULL )
	{
		for ( int i = 0 ; i < nargs ; ++i )
		{
			if ( arguments[i] != NULL )
			{
				delete arguments[i];
				arguments[i] = NULL;
			}
		}

		delete arguments;
		nargs = 0;
	}
}

USBBaseMessage * USBBaseMessage::createInstance( const String** tokens, const int nTokens )
{
	USBBaseMessage *     newMessage = NULL;
	USBMessage::MsgType  msgType    = USBMessage::MsgType::UNKNOWN;
	int                  msgID = 0;
	CmdType              cmdType    = CmdType::UNKNOWN;

	if ( nTokens >= 3 )
	{
		msgType = USBMessage::whatType(*tokens[0]);
		msgID   = tokens[1]->toInt();
		cmdType = whatCommand(*tokens[2]);
	}

	if ( msgType == USBMessage::MsgType::COMMAND )
	{
		switch ( cmdType )
		{
		case CmdType::FIND_RANGE:
			newMessage = new SonarCommand( msgType, msgID, cmdType, tokens, nTokens  );
			break;

		case CmdType::STOP_WHEELS:
			newMessage = new StopCommand( msgType, msgID, cmdType, tokens, nTokens );
			break;

		case CmdType::TURN_WHEELS:
			newMessage = new TurnCommand( msgType, msgID, cmdType, tokens, nTokens );
			break;

		default:
			break;
		}
	}
	return newMessage;

}   // end of createInstance()

USBBaseMessage::CmdType USBBaseMessage::whatCommand()
{
	return cmdType;
}

USBBaseMessage::CmdType USBBaseMessage::whatCommand( const String token)
{
	CmdType result = UNKNOWN;

	if ( token.equals( STOP_WHEELS_TOKEN ) )
	{
		result = CmdType::STOP_WHEELS;
	}
	else if ( token.equals( TURN_WHEELS_TOKEN  ) )
	{
		result = TURN_WHEELS;
	}
	else if ( token.equals( FIND_RANGE_TOKEN ) )
	{
		result = FIND_RANGE;
	}
	else if ( token.equals( STATUS_REPORT_TOKEN ) )
	{
		result = USBBaseMessage::STATUS_REPORT;
	}

	return result;

}   // end of static whatCommand(cmdToken)

void USBBaseMessage::startResponse()
{
	response = RESPONSE_TOKEN + Separator + String(msgId) + Separator + cmdToken ;
	openResponse = true;

}   // end of startResponse()

void USBBaseMessage::startErrorResponse(int errorCode )
{
	response = ERROR_TOKEN + Separator + String(msgId) + Separator + cmdToken + Separator + String(errorCode);
	openResponse = true;
}

void USBBaseMessage::addResponse( const String value )
{
	response += Separator + value;
}

void USBBaseMessage::endResponse()
{
	response += Terminator;

	openResponse = false;
}

void USBBaseMessage::sendResponse()
{
	if ( openResponse )
	{
		endResponse();
	}

	int  len = response.length() + 1;

	char buffer[len];

	response.toCharArray( buffer, len );

	Serial.write(buffer);
}


} /* namespace RobotCar */
