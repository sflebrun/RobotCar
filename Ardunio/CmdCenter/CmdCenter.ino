/**
 * CmdCenter
 */

#include <Arduino.h>

#include <CarMotors.h>
#include <USBMessage.h>
#include <USBBaseMessage.h>
#include <Sonar.h>

#define   DEBUG_OFF
#include <Debug.h>

/**
 * @namespace RobotCar
 *
 * The namespace that holds the bulk of the source code for the Robot Car.
 */
namespace RobotCar
{
	// Place Holder for Doxygen Comment
}

/**
 * Serialized Message container.
 */
RobotCar::USBMessage           message;

/**
 * Message Type.
 */
RobotCar::USBMessage::MsgType  msgType;



/**
 * Function that takes a complete message, turns it into
 * object derived from USBBaseMessage and executes its
 * command function if the message type is Command.
 */
void processCmd()
{
	int       nTokens = 0;
	String ** tokens  = message.getTokens(&nTokens);

#ifdef  DEBUG
	Serial.write("Processing Command:\n");
	Serial.write("Command = ");
	RobotCar::writeString(*tokens[2]);
	Serial.write("\n Number of Tokens = ");
	RobotCar::writeInt(nTokens);
	Serial.write("\n");
#endif

	// Call Factory Method to convert the array of tokens into a Message Object.
	RobotCar::USBBaseMessage * command =
			RobotCar::USBBaseMessage::createInstance((const String **)tokens, nTokens);

	if ( command != NULL )
	{
		// Run the command only if the message type is a Command.
		command->runCommand();

		delete command;
	}

	// Reset serialize message buffer so it is ready to start receiving the
	// next command.
	message.reset();

	return;

}   // end of processCmd();

//The setup function is called once at startup of the sketch


void setup()
{
	// Initialize USB Serial Communications.
	Serial.begin(9600);
	Serial.write("\nReady\n");
	// Initialize Singleton

#ifdef  DEBUG
	Serial.write("Creating Sonar Singleton\n");
	Serial.write("Sonar ID = ");
	RobotCar::writeInt(RobotCar::Sonar::FrontSonarId);
	Serial.write("\nCreate Instance\n");

	RobotCar::Sonar * sonar = RobotCar::Sonar::getInstance();

	message.reset();

sonar->dumpInfo();
#endif

}

int data;


// The loop function is called in an endless loop
void loop()
{
	// Loop to read in UTF-8 Characters from the USB Port
	// which is the Serial I/O connection.
	while ( Serial.available() )
	{
		// Read a single character from the input channel
		data = Serial.read();

		// Append the character to the serialized message buffer.
		msgType = message.put(data);

#ifdef  DEBUG
		Serial.write(data);
#endif

		if ( msgType == RobotCar::USBMessage::MsgType::INCOMPLETE )
		{
			// More characters needed in order to complete the serialized
			// message.
			continue;
		}
		else if ( msgType == RobotCar::USBMessage::MsgType::COMMAND )
		{
			// Message is complete.  Process it.
#ifdef      DEBUG
			Serial.write("\nProcessing Command\n");
#endif
			processCmd();
		}
		else
		{
			// Message is complete.
			/**
			 * @todo Add code to handle Response and Error Message Types.
			 */
			// Ignore this message for now.
			message.reset();
		}

	}
}   // end of loop()
