/**
 * Sonar.cpp
 *
 * @date    August 1, 2017
 * @author  Steven F. LeBrun
 */

/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */



#include <Sonar.h>

#define   DEBUG_OFF
#include <Debug.h>

namespace RobotCar
{


Sonar::Sonar() :
		ServoPin(9),
		ServoFront(93),
		ServoMin(1),
		ServoMax(180),
		TriggerPin(14),
		EchoPin(15),
		MaxDistanceCM(400)

{
#ifdef  DEBUG
	Serial.write("Creating Sonar:\n");
#endif

	sonar         = new NewPing(TriggerPin, EchoPin, MaxDistanceCM);

	servo = new Servo;

#ifdef  DEBUG
	dumpInfo();
#endif

	servo->attach(ServoPin);
	servo->write(ServoFront);

#ifdef  DEBUG
	Serial.write("Servo at Front Position\n");
#endif

}   // end of Constructor Sonar()

Sonar::~Sonar()
{
	servo->detach();

	delete servo;
	delete sonar;

}   // end of Destructor for Sonar

void Sonar::turn(int angle)
{
#ifdef  DEBUG
	Serial.write("Turn Sensor\n");
	dumpInfo();
#endif

	// Determine actual Angle based on [0..180] when angle is [-90..+90]
	int actualAngle = ServoFront + angle;

#ifdef  DEBUG
	Serial.write("Angle to turn = ");
	writeInt(angle);
	Serial.write(" => ");
	writeInt(actualAngle);
	Serial.write("\n");
	Serial.write("Front Adjustment = ");
	writeInt(ServoFront);
	Serial.write("\n");
#endif

	// Adjust angle if it goes past the limits of the servo motor
	if ( actualAngle < ServoMin )
	{
		actualAngle = ServoMin;
	}
	else if ( actualAngle > ServoMax )
	{
		actualAngle = ServoMax;
	}

	// Turn Sonar Sensor

#ifdef  DEBUG
	Serial.write("Turning Sensor to ");
	writeInt(actualAngle);
	Serial.write(" degrees\n");
#endif

	servo->write(actualAngle);
	delay(50);
//	servo->detach();

	return;

}   // end of Sonar::turn(angle)

int Sonar::findRange(int angle = 0, int repeats = 1 )
{
#ifdef  DEBUG
	Serial.write("Find Range\n");
	dumpInfo();
#endif

	// Position Sonar Sensor
	turn(angle);

	int distanceCM = findDistance(repeats);

	return distanceCM;

}   // end of Sonar::findRange(angle, repeats)

int Sonar::findDistance(int repeats = 1)
{
#ifdef  DEBUG
	Serial.write("Find Distance: repeats = ");
	writeInt(repeats);
	Serial.write("\n");

	dumpInfo();
#endif

	unsigned long duration = (repeats <= 1) ?
			sonar->ping(MaxDistanceCM) :
			sonar->ping_median(repeats, MaxDistanceCM);

#ifdef  DEBUG
	Serial.write("Duration = ");
	writeLong(duration);
	Serial.write("\n");
#endif

	int distanceCM = sonar->convert_cm(duration);

	return distanceCM;

}   // end of Sonar::findDistance(repeats)

int Sonar::changeMaxRange( int newMaxCM )
{
	int oldMaxCM = MaxDistanceCM;

	if ( newMaxCM > 0 )
	{
		MaxDistanceCM = newMaxCM;
	}

	return oldMaxCM;
}

#ifdef  DEBUG
void Sonar::dumpInfo()
{
	Serial.write("\nSensor @");
	writeLong((long) this);
	Serial.write("\n");
	Serial.write("Trigger Pin at ");
	writeInt(TriggerPin);
	Serial.write("\nEcho Pin at ");
	writeInt(EchoPin);
	Serial.write("\nMax Distance is ");
	writeInt(MaxDistanceCM);
	Serial.write("cm\nServo Pin at ");
	writeInt(ServoPin);
	Serial.write("\nServo Front at ");
	writeInt(ServoFront);
	Serial.write("\nServo Min = ");
	writeInt(ServoMin);
	Serial.write("\nServo Max = ");
	writeInt(ServoMax);
	Serial.write("\n");

}
#endif

} /* namespace RobotCar */
