#!/bin/bash
CLASSPATH=$CLASSPATH:libs/android-sdk-5.1.1.jar		
CLASSPATH=$CLASSPATH:libs/slf4j-api-1.7.12.jar		
CLASSPATH=$CLASSPATH:libs/slf4j-nop-1.7.12.jar
CLASSPATH=$CLASSPATH:libs/android-sdk-addon-3.jar	
CLASSPATH=$CLASSPATH:libs/slf4j-android-1.7.12.jar
CLASSPATH=$CLASSPATH:libs/slf4j-jdk14-1.7.12.jar	
CLASSPATH=$CLASSPATH:libs/slf4j-simple-1.7.12.jar
CLASSPATH=$CLASSPATH:build/powerUpTimer.jar

CLASSPATH=$CLASSPATH:libs/rxtx-2.2pre1/rxtx-2.2.jar			
RXTX_NATIVE=libs/rxtx-2.2

#CLASSPATH=$CLASSPATH:libs/rxtx-2.2pre1/rxtx-2.2.jar
#RXTX_NATIVE=libs/rxtx-2.2pre2/macos

#CLASSPATH=$CLASSPATH:libs/rxtx-2.2pre2/RXTXcomm.jar
#RXTX_NATIVE=libs/rxtx-2.2pre2/macos 

echo CLASSPATH=$CLASSPATH
java -Djava.library.path="$RXTX_NATIVE" XBeeListener
