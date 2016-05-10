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
  if(bt.available() > 0){ //Condicion para asegurar que existen datos en el puerto serie
    data = bt.read(); //se recibe un valor tipo char del puerto serie y se guarda en la variable data de tipo int para convertir a su valor decimal(Representacion decimal del codigo ASCII)
    if(data <= 180){ //si el dato es un valor menor a 180, entonces se mandan los valores a los servos
      roll = data;  //el primer valor que se manda desde android es el roll de la posicion del celular
      delay(20);
      pitch = bt.read(); //se lee el siguiente valor en el puerto serie que es el pitch y se asigna a la variable pitch
      delay(20);
      Serial.print("Roll: ");
      Serial.print(roll);
      Serial.print("\tPitch: ");
      Serial.println(pitch);
      posServo1 = roll;
      posServo2 = pitch;
      servo1.write(posServo1); //se manda el valor de la variable roll al primer servo
      servo2.write(posServo2); //se manda el valor de la variable pitch al segundo servo
    }else{
      Serial.print("Valor: ");
      Serial.print(data);
      Serial.print("ascii: ");
      Serial.println((char)data);
      if(data == 183){ // si el valor recibido por el puerto serie es 183 entonces se considera que se presiono el boton de disparar en la aplicacion
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
