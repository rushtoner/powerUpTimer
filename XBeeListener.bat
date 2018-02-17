set CP=libs/xbee-java-library-1.2.1.jar
set CP=%CP%;libs/android-sdk-5.1.1.jar
set CP=%CP%;libs/slf4j-api-1.7.12.jar
set CP=%CP%;libs/slf4j-nop-1.7.12.jar
set CP=%CP%;libs/android-sdk-addon-3.jar
set CP=%CP%;libs/slf4j-android-1.7.12.jar
set CP=%CP%;libs/slf4j-jdk14-1.7.12.jar
set CP=%CP%;libs/slf4j-simple-1.7.12.jar
set CP=%CP%;build/powerUpTimer.jar
REM set CP=%CP%;libs\rxtx-2.2pre2\RXTXcomm.jar
REM set CP=%CP%;libs/mfz-rxtx-2.2-20081207-win-x64/RXTXcomm.jar
REM set CP=%CP%;libs\qbang\rxtx-2.2pre2-bins\RXTXcomm.jar
set CP=%CP%;libs\rxtx-v2.2pre3\RXTXcomm.jar

REM set RXTX_NATIVE=libs/rxtx-2.2pre2/win64/rxtxSerial.dll
REM set RXTX_NATIVE=libs/mfz-rxtx-2.2-20081207-win-x64/rxtxSerial.dll
REM set RXTX_NATIVE=libs/mfz-rxtx-2.2-20081207-win-x64/rxtxSerial.dll
REM set RXTX_NATIVE=c:\users\david\w\powerUpTimer\libs\qbang\rxtx-2.2pre2-bins\win64\rxtxSerial.dll
set RXTX_NATIVE=libs\rxtx-v2.2pre3\win64\rxtxSerial.dll

echo CP=%CP%
echo RXTX_NATIVE=%RXTX_NATIVE%
dir %RXTX_NATIVE%
java -Djava.library.path=%RXTX_NATIVE% -cp %CP% XBeeListener
REM java -cp %CP% XBeeListener
