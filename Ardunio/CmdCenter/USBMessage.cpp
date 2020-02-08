/**
 * USBMessage.cpp
 *
 *  @date    July 30, 2017
 *  @author  Steven F. LeBrun
 */

/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */

#include "USBMessage.h"

#define   DEBUG_OFF
#include <Debug.h>


namespace RobotCar
{

	const String  USBMessage::COMMAND_TOKEN   = "C";
	const String  USBMessage::RESPONSE_TOKEN  = "R";
	const String  USBMessage::ERROR_TOKEN     = "E";

	const  char   USBMessage::delimiter   = ':';
	const  char   USBMessage::terminator  = ';';

	USBMessage::USBMessage() :
				bufferStop(0),
                bufferFull(false),
				tokens(NULL),
				nTokens(0)

	{
		reset();
	}

	USBMessage::~USBMessage()
	{
		if ( tokens != NULL )
		{
			int i;

			for ( i = 0 ; i < nTokens ; ++i )
			{
				if ( tokens[i] != NULL )
				{
					delete tokens[i];
				}
			}

			delete tokens;
		}

	}   // end of ~USBMessage destructor

	void USBMessage::reset()
	{
		clearBuffer();

		if ( tokens != NULL )
		{
			int i;
			for ( i = 0 ; i < nTokens ; ++i )
			{
				delete tokens[i];
			}
			delete tokens;
			tokens = NULL;
			nTokens = 0;
		}
	}

	USBMessage::MsgType USBMessage::put(char value)
	{
		if ( bufferStop >= BufferMax )
		{
#ifdef  DEBUG
			Serial.write("Buffer Overflow\n");
#endif
			bufferFull = true;
			return MsgType::OVERFLOW;
		}

		buffer[bufferStop] = value;

		++bufferStop;

		if ( value == terminator )
		{
#ifdef  DEBUG
			Serial.write("Message Terminator found\n");
#endif
			parseMessage();
			bufferFull = true;
			return whatType();
		}


		return MsgType::INCOMPLETE;
	}

	String ** USBMessage::getTokens(int * tokenCnt )
	{
		if ( !full() )
		{
			if ( tokenCnt != NULL )
			{
				*tokenCnt = 0;
			}

			return NULL;
		}

		if ( tokens == NULL )
		{
			parseMessage();
		}

		if ( tokenCnt != NULL )
		{
			*tokenCnt = nTokens;
		}

		return tokens;
	}

	bool USBMessage::full()
	{
		return bufferFull;
	}

	USBMessage::MsgType USBMessage::whatType()
	{
		// Make sure we have a complete message
		if ( nTokens == 0 || tokens == NULL )
		{
			// We do not have a full buffer yet
			return MsgType::INCOMPLETE;
		}

		MsgType  msgType = MsgType::UNKNOWN;

		if ( tokens[0]->equals(COMMAND_TOKEN) )
		{
#ifdef  DEBUG
			Serial.write("Message Type is Command\n");
#endif
			msgType = MsgType::COMMAND;
		}
		else if ( tokens[0]->equals(RESPONSE_TOKEN) )
		{
#ifdef  DEBUG
			Serial.write("Message Type is Response\n");
#endif
			msgType = MsgType::RESPONSE;
		}
		else if ( tokens[0]->equals(ERROR_TOKEN) )
		{
#ifdef  DEBUG
			Serial.write("Message Type is Error\n");
#endif
			msgType = MsgType::ERROR;
		}

#ifdef  DEBUG
		Serial.write("What Message Type for ");
		writeString(*tokens[0]);
		Serial.write(", Type = ");
		writeMsgType(msgType);
		Serial.write("\n");
#endif

		return msgType;

	}   // end of whatType()

	USBMessage::MsgType USBMessage::whatType( const String type )
	{
		MsgType  msgType = MsgType::UNKNOWN;

		if ( type.equals(COMMAND_TOKEN) )
		{
			msgType = MsgType::COMMAND;
		}
		else if ( type.equals(RESPONSE_TOKEN) )
		{
			msgType = MsgType::RESPONSE;
		}
		else if ( type.equals(ERROR_TOKEN) )
		{
			msgType = MsgType::ERROR;
		}

		return msgType;

	}

	//************************************************************************
	// Private Methods
	//************************************************************************

	void USBMessage::clearBuffer()
	{
		int i = 0;
		for (i = 0 ; i < BufferMax ; ++i)
		{
			buffer[i] = NUL;
		}

		bufferStop = 0;
		bufferFull = false;

		return;
	}

	void USBMessage::parseMessage()
	{
		int  i;  // Miscellaneous Offset
		int  t;  // Token Offset
		int  b;  // Buffer Offset
		int  c;  // Char   Offset

#ifdef  DEBUG
		Serial.write("Buffer1 = ");
		Serial.write(buffer);
		Serial.write("\n");
#endif

		char data;

		char token[bufferStop];

		nTokens = countTokens();

#ifdef  DEBUG
		Serial.write("Buffer2 = ");
		Serial.write(buffer);
		Serial.write("\n");
#endif


		tokens  = new String*[nTokens];

		for ( i = 0 ; i < nTokens ; ++i )
		{
			tokens[i] = new String();
		}

#ifdef  DEBUG
		Serial.write("Buffer3 = ");
		Serial.write(buffer);
		Serial.write("\n");


		Serial.write("Number of Tokens = ");
		writeInt(nTokens);
		Serial.write("\n");
		Serial.write("Buffer Size = ");
		writeInt(bufferStop);
		Serial.write("\n");
		Serial.write("Buffer = ");
		buffer[bufferStop] = NUL;
		Serial.write(buffer);
		Serial.write("\n");
#endif

		for ( t = b = c = 0 ; b < bufferStop ; ++b )
		{
			data = buffer[b];

#ifdef      DEBUG
			Serial.write("t = ");
			writeInt(t);
			Serial.write(", c = ");
			writeInt(c);
			Serial.write(", b = ");
			writeInt(b);
			Serial.write(", Data = ");
			Serial.write(data);
			Serial.write("\n");
#endif

			if ( data == delimiter || data == terminator )
			{
#ifdef          DEBUG
				Serial.write("Delimiter or Terminator Found\n");
#endif

				token[c] = '\0';

#ifdef          DEBUG
				Serial.write("Token[");
				writeInt(t);
				Serial.write("] = ");
				Serial.write(token);
				Serial.write("\n");
#endif

				*tokens[t] = token;

				++t;

				if ( t >= nTokens )
				{
					// We are done.
					break;
				}

				c = 0;

#ifdef          DEBUG
				Serial.write("b = ");
				writeInt(b);
				Serial.write("\n");
#endif

				continue;
			}

#ifdef      DEBUG
			Serial.write("B,C = ");
			writeInt(b);
			Serial.write(", ");
			writeInt(c);
			Serial.write(", Data = ");
			writeInt((int) data);
			Serial.write(" = ");
			Serial.write(data);
			Serial.write("\n");
#endif

			token[c] = data;
			++c;

		}   // end of for loop

#ifdef  DEBUG
		for ( i = 0 ; i < nTokens ; ++i )
		{
			Serial.write("Token #");
			writeInt(i);
			Serial.write(" == ");
			writeString(*tokens[i]);
			Serial.write("\n");
		}
#endif

		return;

	}   // end of USBMessage::parseMessage()


	int  USBMessage::countTokens()
	{
		// Start Counter at 1 instead of 0 because the terminator marks
		// the end of a token that will not detected by the loop below.
		int cnt = 1;
		int i;

		for (i = 0 ; buffer[i] != terminator ; ++i)
		{
			if ( buffer[i] == delimiter )
			{
				++cnt;
			}
		}

		return cnt;

	}   // end of countTokens()



} /* namespace RobotCar */
