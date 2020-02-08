/**
 * CarMotors.cpp
 *
 * @date    August 8, 2017
 * @author  Steven F. LeBrun
 */

/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */



#include <CarMotors.h>

namespace RobotCar
{

Motors CarMotors::motors = Motors();

CarMotors::CarMotors()
{
}

CarMotors::~CarMotors()
{
}

Motors * CarMotors::getInstance()
{
	return &motors;
}

} /* namespace RobotCar */
