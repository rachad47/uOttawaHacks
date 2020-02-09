
#include <Servo.h> 
Servo Creativesolution;
int in1 = 9; 
int in2 = 8; 
void setup() {
  Creativesolution.attach(7); 

  pinMode(in1, OUTPUT); 
  pinMode(in2, OUTPUT);
}

void TurnMotorA(){
  digitalWrite(in1, HIGH);
  digitalWrite(in2, LOW);
}
void TurnOFFA(){
  digitalWrite(in1, LOW);
  digitalWrite(in2, LOW);
}
void TurnMotorA2(){
  digitalWrite(in1,LOW);
  digitalWrite(in2,HIGH);
}
void loop() {
  TurnMotorA();
  
  delay(500);
  TurnOFFA();
  Creativesolution.write(110);
  delay(100);
  Creativesolution.write(0);
  delay(1300);
  TurnMotorA2(); 
  delay(500);
  TurnOFFA();
  delay(1300);
}
