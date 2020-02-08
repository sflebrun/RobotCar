/**
 * CarMotors.h
 *
 *  @date   August 8, 2017
 *  @author Steven F. LeBrun
 */

/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */



#ifndef CARMOTORS_H_
#define CARMOTORS_H_

#include <Motors.h>

namespace RobotCar
{

/**
 * Singleton Class used to make the Motors class, the
 * Motor Controller Shield on the Arduino.  This allows for a
 * single creation of the Motors object that can be shared
 * with all Commands.
 *
 * This class is a wrapper for the Motors class which turns
 * it into a Singleton class.
 */
class CarMotors
{
private:
	/**
	 * The singleton object that represents all the motors on
	 * the Robot Car that are controlled through the Motor Control
	 * Shield that is mounted on top of the Arduino board.
	 */
	static Motors motors;

public:
	/**
	 * The Getter method for obtaining the motors object.
	 *
	 * If this is the first call to this method, the motors object
	 * will be initialized.
	 */
	static Motors * getInstance();

	/**
	 * The virtual destructor for the class.
	 */
	virtual ~CarMotors();

private:
	/**
	 * The constructor for this class.
	 *
	 * @note This constructor is private in accordance to the Singleton Pattern.
	 *       This forces users to call getInstance() to obtain the motors object.
	 */
	CarMotors();
};

} /* namespace RobotCar */

#endif /* CARMOTORS_H_ */
