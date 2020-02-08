/*
 * Debug.h
 *
 *  Created on: Aug 7, 2017
 *      Author: steven
 */

#ifndef DEBUG_H_
#define DEBUG_H_

#include  <USBMessage.h>

namespace RobotCar
{
#ifdef  DEBUG
void writeInt(     int value );
void writeLong(    long value );
void writeString(  String value );
void writeMsgType( USBMessage::MsgType msgType);

#endif  // DEBUG
} /* namespace RobotCar */

#endif /* DEBUG_H_ */
