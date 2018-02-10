#!/bin/bash
CLASSPATH=$CLASSPATH:libs/android-sdk-5.1.1.jar		
CLASSPATH=$CLASSPATH:libs/rxtx-2.2.jar			
CLASSPATH=$CLASSPATH:libs/slf4j-api-1.7.12.jar		
CLASSPATH=$CLASSPATH:libs/slf4j-nop-1.7.12.jar
CLASSPATH=$CLASSPATH:libs/android-sdk-addon-3.jar	
CLASSPATH=$CLASSPATH:libs/slf4j-android-1.7.12.jar
CLASSPATH=$CLASSPATH:libs/slf4j-jdk14-1.7.12.jar	
CLASSPATH=$CLASSPATH:libs/slf4j-simple-1.7.12.jar
CLASSPATH=$CLASSPATH:build/powerUpTimer.jar
echo CLASSPATH=$CLASSPATH
java -Djava.library.path="libs" IOSamplingSample
