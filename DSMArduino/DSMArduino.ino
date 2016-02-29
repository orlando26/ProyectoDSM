char data;
int valX = 0;
int valY = 0;
int valZ = 0;
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(Serial.available() > 0){
    valX = Serial.read();
    valY = Serial.read();
    valZ = Serial.read();
    Serial.print("Acelerometro X: ");
    Serial.print(valX);
    Serial.print("\tAcelerometro Y: ");
    Serial.print(valY);
    Serial.print("\tAcelerometro Z: ");
    Serial.println(valZ);
  }
}
