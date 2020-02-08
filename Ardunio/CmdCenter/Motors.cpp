/**
 * Motors.cpp
 *
 * @date   August 8, 2017
 * @author Steven F. LeBrun
 */

/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */



#include <Motors.h>

namespace RobotCar
{

int Motors::FIRST_MOTOR = 1;
int Motors::LAST_MOTOR  = 4;

Motors::Motors() :
		FLMotor(1),
		FRMotor(2),
		RLMotor(3),
		RRMotor(4)
{
	motors[0] = NULL;
	motors[1] = &FLMotor;
	motors[2] = &FRMotor;
	motors[3] = &RLMotor;
	motors[4] = &RRMotor;

	int i;
	for ( i = FIRST_MOTOR ; i <= LAST_MOTOR ; ++i )
	{
		motors[i]->run(RELEASE);
		motors[i]->setSpeed(0);
	}

}

Motors::~Motors()
{
	// Nothing to do.
}

void Motors::StopWheels()
{
	int i;
	for ( i = FIRST_MOTOR ; i <= LAST_MOTOR ; ++i )
	{
		stopWheel(i);
	}
}

void Motors::SpinWheels(int speed[], int direction[] )
{
	int i;
	for ( i = FIRST_MOTOR ; i <= LAST_MOTOR ; ++i )
	{
		spinWheel(i, speed[i], direction[i] );
	}
}

void Motors::spinWheel(int wheel, int speed, int direction )
{
	// Make sure that Speed is in range [0..255]
	if ( speed > 255 )
	{
		speed = 255;
	}
	else if ( speed < 0 )
	{
		speed = 0;
	}

	motors[wheel]->run(direction);
	motors[wheel]->setSpeed(speed);

}

void Motors::stopWheel(int wheel )
{
	motors[wheel]->run(BRAKE);
	motors[wheel]->setSpeed(0);
}

} /* namespace RobotCar */
