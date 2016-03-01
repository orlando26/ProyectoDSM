#include<Servo.h>
char data;
int valX = 0;
int valY = 0;
int valZ = 0;
Servo servo1; //Servo que mueve el eje x
Servo servo2;  //Servo que mueve el eje y
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  servo1.attach(3);
  servo2.attach(9);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(Serial.available() > 0){
    valX = Serial.read() - 65;
    valY = Serial.read() - 65;
    valZ = Serial.read() - 65;
    Serial.print("Accel X: ");
    Serial.print(valX);
    Serial.print("\tAccel Y: ");
    Serial.print(valY);
    Serial.print("\tAccel Z: ");
    Serial.print(valZ);

    valX = constrain(valX, 0, 20);
    valX = map(valX, 0, 20, 0, 180);
    valY = constrain(valY, 0, 20);
    valY = map(valY, 0, 20, 0, 180);
    valZ = constrain(valY, 0, 20);
    valZ = map(valY, 0, 20, 0, 180);

    Serial.print("\tServoX :");
    Serial.print(valX);
    Serial.print("\tServoY :");
    Serial.println(valY);
  }
  
  servo1.write(valX);
  servo2.write(valY);
}
