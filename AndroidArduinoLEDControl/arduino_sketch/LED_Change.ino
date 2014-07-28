#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

#define  LED_RED       3
#define  LED_GREEN     5
#define  LED_BLUE      7

AndroidAccessory acc("PTRPrograms",
		     "ArduinoLEDControl",
		     "Arduino ADK LED Control Demo",
		     "1.0",
		     "http://ptrprograms.blogspot.com",
		     "1234567890987654321");

void setup();
void loop();

void init_leds()
{
	digitalWrite( LED_RED, 1 );
	pinMode( LED_RED, OUTPUT );

  	digitalWrite( LED_GREEN, 1 );
	pinMode( LED_GREEN, OUTPUT );

	digitalWrite( LED_BLUE, 1 );
	pinMode( LED_BLUE, OUTPUT );
}

void setup()
{
	Serial.begin( 115200 );
	init_leds();

	acc.powerOn();
}

void loop()
{
	byte msg[3];

	if ( acc.isConnected() ) {
        	int len = acc.read( msg, sizeof( msg ), 1 );
                if (len > 0) {
                  //msg[0] is the component ID
                  //msg[1] is 0-255, higher being a brighter LED
                  if( msg[0] == 0x0 ) {
                    analogWrite( LED_RED, msg[1] );
                  } else if( msg[0] == 0x1 ) {
		    analogWrite( LED_GREEN, msg[1] );
                  } else if( msg[0] == 0x2 ) {
                    analogWrite( LED_BLUE, msg[1] ); 
                  }
		}
	} else {
            //If an android device with a control app isn't connected, default to LEDs off
	  analogWrite( LED_RED, 0 );
  	  analogWrite( LED_GREEN, 0 );
          analogWrite( LED_BLUE, 0 );
	}

	delay(10);
}
