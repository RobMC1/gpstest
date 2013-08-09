GPSData
=======


This is only a test application. Not everything is working, feel free to reuse some parts of it but pay attention to comments, it generally indicates if things are working or not. There's no garanties of any kind about this work.



Main menu : 
Test GPS : Test of geolocation using GPS
Test Network : Test of geolocation using Network
Test Gyroscope : Test of the gyroscope
Test Accelerometre : Test of the accelerometer
Test Gravité : Test of the gravity
Test Orientation : Test of the acceleration in the terrestrial reference (using data about the device orientation)



Activities :
From each activity you can display saved data through the menu and delete it.
For each activity, time, informations about wifi, data and gsm activation, battery level and screen brightness is stored to files when activity is created. At the end time and battery level is also stored.

Test GPS : 
 - Stores data in sdcard0/TestGeoLoc/GPSOutput.txt
 - Gives access to Test Network and Test Sensors
 - Stores regularly current mean latitude, longitude and accuracy, whether the following value is valid, the last value acquired, the fix time and its accuracy.

Test Network : 
 - Stores data in sdcard0/TestGeoLoc/NetworkOutput.txt
 - Gives access to Test GPS and Test Sensors
 - Stores regularly current mean latitude, longitude and accuracy, whether the following value is valid, the last value acquired, the fix time and its accuracy.

Test Sensors : 
 - Stores data in sdcard0/TestGeoLoc/SensorOutput.txt
 - NOT WORKING !!!
 - Is basically supposed to compute the current location using the motion sensors and a previous location acquired with the GPS or the network (that's why this activity cannot be launched from the main menu).

Test Gyroscope : 
 - Stores data in sdcard0/TestGeoLoc/GyroOutput.txt
 - Stores regularly current time, mean values in x, y and z, the amount of values used to compute the mean values and the power consumption in mA.

Test Gravité : 
 - Stores data in sdcard0/TestGeoLoc/GravityOutput.txt
 - Stores regularly current time, mean values in x, y and z, the amount of values used to compute the mean values and the power consumption in mA.

Test Accelerometre : 
 - Stores data in sdcard0/TestGeoLoc/AccOutput.txt
 - Stores regularly current time, mean values in x, y and z, the amount of values used to compute the mean values and the power consumption in mA.

Test Orientation : 
 - Stores data in sdcard0/TestGeoLoc/OrientationOutput.txt
 - Stores regularly current time, mean values in x, y and z, the amount of values used to compute the mean values and the power consumption in mA (but I'm not confident in this one).


"Test GPS.pdf" contains a lot of informations to complete this work, everything I could not complete in the application is explained there. Just take a look (in french).
