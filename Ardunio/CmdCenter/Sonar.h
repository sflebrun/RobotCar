/**
 * Sonar.h
 *
 * @date   August 1, 2017
 * @author Steven F. LeBrun
 */

/* Copyright © 2018, Steven F. LeBrun.  All rights reserved. */


#ifndef SONAR_H_
#define SONAR_H_

#include <Arduino.h>
#include <AFMotor.h>
#include <NewPing.h>
#include <Servo.h>

namespace RobotCar
{

/**
 * This class encapsulate the Ultrasonic Range Finder sensor and
 * the servo motor used to point the sensor.
 *
 * This class was originally going to be a singleton class.  Turns out
 * that that was not necessary since creating a new Sonar object does
 * not result in the Servo/Sensor changing position.
 *
 * The Robot Car uses an
 * <a href="http://akizukidenshi.com/download/ds/towerpro/SG90.pdf">
 * SG90 9 g Micro Servo Motor</a> and an
 * <a href="http://howtomechatronics.com/tutorials/arduino/ultrasonic-sensor-hc-sr04/">
 * HC-SR04 Ultrasonic Sensor</a>.
 */
class Sonar
{
//private:
//	static Sonar *   sensor;


protected:
	/**
	 * The object that talks directly to the Ultrasonic Sensor hardware.
	 * Supplied by the NewPing library.
	 */
	NewPing *         sonar;

	/**
	 * The object that talks directly to the Servo Motor hardware.
	 */
	Servo   *         servo;

	/**
	 * The GPIO pin that the Servo Motor control wire is connected to.
	 * This needs to be a PWM pin.
	 */
	int               ServoPin;

	/**
	 * Fudge Factor.  Set to the angle of the Servo Motor that points
	 * straight ahead on the Robot Car.  This angle compensates for the
	 * Servo Motor Mount being not perfectly straight.
	 *
	 * If the Servo Mount was installed correctly, the value of this
	 * variable would be 90 degrees.
	 */
	int               ServoFront;

	/**
	 * The smallest value that the Servo Motor can be turned to.  Any
	 * value smaller would cause the motor to exceed its lower limit.
	 */
	int               ServoMin;

	/**
	 * The largest value that the Servo Motor can be turned to.  Any
	 * value greater will cause the motor to exceed its upper limit.
	 */
	int               ServoMax;

	/**
	 * The GPIO pin that is attached to the Ultrasonic Sensor Trigger.
	 * The Trigger input (to the sensor) starts a range finding session.
	 */
	int               TriggerPin;

	/**
	 * The GPIO pin that is attached to the Ultrasonic Sensor Echo.
	 * The Echo Pin (input to the GPIO) receives a signal after the
	 * Trigger signal is sent.  The time difference between the sending
	 * of the Trigger and receiving the Echo represents twice the
	 * distance to an object in front of the Sensor.
	 */
	int               EchoPin;

	/**
	 * The maximum distance, in centimeters, to attempt to sensor an object.
	 * This value is used to limit the time it takes to look for an object
	 * ahead of the sensor.  Without this value, the sensor would have to
	 * timeout if there is no objects detected.
	 */
	int               MaxDistanceCM;


public:
	/**
	 * Constructor for this class.
	 */
	Sonar();

	/**
	 * Virtual Destructor for this class.
	 */
	virtual ~Sonar();

//	static Sonar * getInstance();

	/**
	 * Determines the distance to any object in front of the sensor in
	 * centimeters.
	 *
	 * @param  angle  The Angle to point the sensor in degrees.
	 *                Range:  [-90..90], Zero is straight ahead from the
	 *                perspective of the Robot Car.
	 * @param  repeat The number of measurements to take.  The answer
	 *                will be the average from all the measurements.
	 *
	 * @return Returns the distance to an object detected in front of the
	 *         sensor.  The distance is given in centimeters.  If the
	 *         @b repeat argument is greater than 1, the return value will
	 *         be the average of each measurement taken.  If no object is
	 *         detected, a zero is returned.
	 */
	int  findRange( int angle, int repeat );

	/**
	 * Changes the default Maximum Distance to search, in centimeters.
	 * The initial default maximum distance is set to 400 cm.
	 */
	int  changeMaxRange( int newMaxCM );

protected:

	/**
	 * Changes the angle that the sensor is facing.  Uses the servo motor
	 * to turn the sensor.
	 *
	 * @param angle  The direction to point the sensor in degrees.  The range
	 *               is from -90 to +90 degrees.  This value is absolute and
	 *               is independent of the direction that the servo motor is
	 *               pointing to before the method is run.
	 */
	void turn( int angle );

	/**
	 * Take an distance measurement to an object in front of the sensor.
	 *
	 * @param  repeat The number of measurements to take.  The answer
	 *                will be the average from all the measurements.
	 *
	 * @return Returns the distance to an object detected in front of the
	 *         sensor.  The distance is given in centimeters.  If the
	 *         @b repeat argument is greater than 1, the return value will
	 *         be the average of each measurement taken.  If no object is
	 *         detected, a zero is returned.
	 *
	 */
	int  findDistance( int repeats );

#ifdef   DEBUG
	/**
	 * Debug method that dumps the internal data members to the Debug Output.
	 */
	void dumpInfo();
#endif

};   // end of class Sonar

} /* namespace RobotCar */

#endif /* SONAR_H_ */
