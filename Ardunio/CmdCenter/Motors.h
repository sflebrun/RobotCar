/**
 * Motors.h
 *
 * @date    August 8, 2017
 * @author Steven F. LeBrun
 */

/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */



#ifndef MOTORS_H_
#define MOTORS_H_

#include <Arduino.h>
#include <AFMotor.h>


namespace RobotCar
{

/**
 * Front Left Wheel Offset.
 */
#define  FLWheel  1

/**
 * Front Right Wheel Offset.
 */
#define  FRWheel  2

/**
 * Rear Left Wheel Offset
 */
#define  RLWheel  3

/**
 * Rear Right Wheel Offset.
 */
#define  RRWheel  4


/**
 * Class that encapsulates the Motor Controller Shield for the Arduino.
 *
 * This class also hides the type of Motor Controller Shield being used.
 * In this case, it is the Mult-Motor Driver Shield (2-L293D).
 *
 * The motors used by the Robot Car are all DC Motors.
 *
 * See <a href="http://www.yourduino.com/sunshop/index.php?l=product_detail&p=292">
 * Motor Driver Shield L293D </a>
 *
 * @todo This class should be turned into a singleton class which would eliminate
 * the need for the CarMotor class, which turns Motor into a singleton.  The
 * reason this class needs to be a singleton is because each time an instance
 * of this class is created, the motors would be reset and stop.  If the car is
 * in motion, to would stop before changing speeds.
 */
class Motors
{
protected:
	/**
	 * The Adafruit Motor Shield Object that controls the Front Left Motor.
	 */
	AF_DCMotor   FLMotor;

	/**
	 * The Adafruit Motor Shield Object that controls the Front Right Motor.
	 */
	AF_DCMotor   FRMotor;

	/**
	 * The Adafruit Motor Shield Object that controls the Rear Left Motor.
	 */
	AF_DCMotor   RLMotor;

	/**
	 * The Adafruit Motor Shield Object that controls the Rear Right Motor.
	 */
	AF_DCMotor   RRMotor;

	/**
	 * An array to allow iterative access to the DC Motor objects for each wheel.
	 *
	 * @note motors[0] is not used.  The offsets used are defined by the <em>XX</em>Wheel
	 *       macros.
	 */
	AF_DCMotor * motors[5];

public:

	/**
	 * Defines the first offset to use in the motors[] array in loops.
	 */
	static int   FIRST_MOTOR;

	/**
	 * Defines the last offset to use in the motors[] array in loops.
	 */
	static int   LAST_MOTOR;

	/**
	 * Constructor for this class.
	 */
	Motors();

	/**
	 * Virtual Destructor for this class.
	 */
	virtual ~Motors();

	/**
	 * Fast method for stopping the Robot Car by causing all four motors
	 * to stop turning.
	 */
	void  StopWheels();

	/**
	 * Causes all four wheels to turn.
	 *
	 * @param speed[]     An array of five integer elements that determine the
	 *                    speed at which each wheel turns.  First, speed[0],
	 *                    is ignored.  The other four elements are for each
	 *                    of the four wheels, using the <em>XX</em>Wheel macros
	 *                    for the array element offset.  Legal values are [0..255].
	 *                    The value of 0 means stop.
	 * @param direction[] An array of five integer elements that determine the
	 *                    direction that each wheel turns.  First, speed[0],
	 *                    is ignored.  The other four elements are for each
	 *                    of the four wheels, using the <em>XX</em>Wheel macros
	 *                    for the array element offset.  Legal values are
	 *                    [FORWARD, BACKWARD, and RELEASE] as defined in the
	 *                    AFMotor.h header file.
	 *
	 * This method causes each wheel to start to turn.  Each wheel can turn at
	 * a different speed and direction from the other wheels.
	 */
	void  SpinWheels( int speed[], int direction[] );

protected:
	/**
	 * Causes an individual wheel to turn.
	 *
	 * @param wheel     The offset associated with the motor.  Legal values are
	 *                  defined by the <em>XX<</em>Wheel macros.
	 * @param speed     The speed in which the wheel is to turn.  Legal values
	 *                  are from 0 to 255. @note that not all lower values will
	 *                  result in the wheels turning due to the voltage applied
	 *                  to the motors and their natural resistance.
	 * @param direction The direction in which the wheel is to turn.  Legal values
	 *                  are defined in the AFMotor.h header file and are
	 *                  FORWARD, BACKWARD, and RELEASE.
	 */
	void  spinWheel( int wheel, int speed, int direction = FORWARD );

	/**
	 * Causes an individual wheel to stop turning.
	 *
	 * @param wheel     The offset associated with the motor.  Legal values are
	 *                  defined by the <em>XX<</em>Wheel macros.
	 */
	void  stopWheel( int wheel );
};

} /* namespace RobotCar */

#endif /* MOTORS_H_ */
