#include<Servo.h>
#include<SoftwareSerial.h>
int data;
int roll;
int pitch;
int posServo1 = 90;
int posServo2 = 90;
int on = 3;
int disparo = 4;
int regreso = 7;
Servo servo1; //Servo que mueve el eje x
Servo servo2;  //Servo que mueve el eje y
SoftwareSerial bt(10, 11);
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  bt.begin(9600);
  servo1.attach(8);
  servo2.attach(9);
  servo1.write(posServo1);
  servo2.write(posServo2);
  pinMode(on, OUTPUT);
  pinMode(disparo, OUTPUT);
  pinMode(regreso, OUTPUT);
  digitalWrite(disparo, LOW);
  digitalWrite(regreso, HIGH);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(bt.available() > 0){
    data = bt.read();
    if(data <= 180){
      roll = data;  
      delay(20);
      pitch = bt.read();
      delay(20);
      Serial.print("Roll: ");
      Serial.print(roll);
      Serial.print("\tPitch: ");
      Serial.println(pitch);
      posServo1 = roll;
      posServo2 = pitch;
      servo1.write(posServo1);
      servo2.write(posServo2);
    }else{
      Serial.print("Valor: ");
      Serial.print(data);
      Serial.print("ascii: ");
      Serial.println((char)data);
      if(data == 181){
        digitalWrite(on, HIGH);
        Serial.print("asdasd");
      }else if(data == 182){
        digitalWrite(on, LOW);
      }else if(data == 183){
        Serial.print("shooot");
        digitalWrite(regreso, LOW);
        digitalWrite(disparo, HIGH);
        delay(250);
        digitalWrite(disparo, LOW);
        digitalWrite(regreso, HIGH);
      }
    }
    delay(30);  
  }
}
