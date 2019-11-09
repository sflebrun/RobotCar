# LCDDisplay

## About the LCDDisplay

The **LCDDisplay** Eclipse Java Project is one component of the
**RobotCar Project**.  This project is for controlling a
[RGB1602 LCD Module](https://wiki.52pi.com/index.php/RGB_1602\(English\),
that is the only display on the Robot Car.

The LCD Display Hat used communicates with the Raspberry Pi using
**[I2C](https://i2c.info/i2c-bus-specification)**.

## Requirements for building

### Language Requirement
* Originally built and tested using Java 1.8
* Compiles using Java 11 -- Has not been tested with Java 11

### External Jar Files Requirement

Uses the [Pi4J Project](https://pi4j.com/1.2/index.html) Version 1.2

Jar Files required:

* pi4j/pi4j-1.2-SNAPSHOT/lib/juint.jar
* pi4j/pi4j-1.2-SNAPSHOT/lib/pi4j-core.jar
* pi4j/pi4j-1.2-SNAPSHOT/lib/pi4j-device.jar
* pi4j/pi4j-1.2-SNAPSHOT/lib/pi4j-gpio-extension.jar

## Robot Car Blog

For more information about the Robot Car project check out the
[RobotCar Blog](https://steven.lebruns.com/category/robotics_blog/)




