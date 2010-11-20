#include <NewSoftSerial.h>

#define rxPin 2
#define txPin 3

NewSoftSerial BlueTooth(rxPin, txPin);

void setup()
{
  Serial.begin(9600); //open serial hardware
  BlueTooth.begin(38400); //open serial software
  
  pinMode(rxPin, INPUT); 
  pinMode(txPin, OUTPUT); 
  
  delay(1000);
  BlueTooth.print("AT\r"); //Communication returns OK
  
  //Set the bluetooth identification string
  delay(1000);
  BlueTooth.print("ATN=OBDMe\r");
  delay(500);
  while(BlueTooth.available()){
     Serial.print((char)BlueTooth.read());
  }
   
  //Confirm bluetooth identification string
  delay(1000);
  BlueTooth.print("ATN?\r");
  delay(500);
  while(BlueTooth.available()){
     Serial.print((char)BlueTooth.read());
  }
  
  //Set baud rate to 38400
  delay(1000);
  BlueTooth.print("ATL3\r");
  delay(500);
  while(BlueTooth.available()){
     Serial.print((char)BlueTooth.read());
  }
  
  //Confirm new baud rate
  delay(1000);
  BlueTooth.print("ATL?\r");
  delay(500);
  while(BlueTooth.available()){
     Serial.print((char)BlueTooth.read());
  }
  
  delay(1000);
}

void loop()
{
}
