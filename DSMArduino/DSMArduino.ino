#include<Servo.h>
#include<SoftwareSerial.h>
int data;
int roll;
int pitch;
int posServo1 = 90;
int posServo2 = 90;
Servo servo1; //Servo que mueve el eje x
Servo servo2;  //Servo que mueve el eje y
SoftwareSerial bt(10, 11);
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  bt.begin(9600);
  servo1.attach(3);
  servo2.attach(9);
  servo1.write(posServo1);
  servo2.write(posServo2);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(bt.available() > 0){
    /*valX = (int)bt.read();
    valY = (int)bt.read();
    if(valX < 0) valX = 0;
    if(valY < 0) valY = 0;

    Serial.print("Roll: ");
    Serial.print(valX);
    Serial.print("\tchar: ");
    Serial.print((char)valX);
    Serial.print("\tAccel Y: ");
    Serial.print(valY);
    Serial.print("\tchar: ");
    Serial.println((char)valY);

    Serial.print("\tServoX :");
    Serial.print(valX);
    Serial.print("\tServoY :");
    Serial.println(valY);*/
    bt.flush();
    roll = bt.read();
    delay(20);
    pitch = bt.read();
    Serial.print("Roll: ");
    Serial.print(roll);
    Serial.print("\tPitch: ");
    Serial.println(pitch);
    delay(100);
    /*switch(data){
      case 'l':
        if(posServo1 < 180)posServo1 += 10;
        break;
      case 'r':
        if(posServo1 > 0)posServo1 -= 10;
        break;
      case 'u':
        if(posServo2 < 180)posServo2 += 10;
        break;
      case 'd': 
        if(posServo2 > 0)posServo2 -= 10;
        break;
    }
    servo1.write(posServo1);
    servo2.write(posServo2);*/
  }
  
  //servo1.write(valX);
  //servo2.write(valY);
}
