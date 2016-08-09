/*
 The circuit:
 * LCD RS pin to digital pin 12
 * LCD Enable pin to digital pin 11
 * LCD D4 pin to digital pin 5
 * LCD D5 pin to digital pin 4
 * LCD D6 pin to digital pin 3
 * LCD D7 pin to digital pin 2
 * LCD R/W pin to ground
 * LCD VSS pin to ground
 * LCD VCC pin to 5V
 * LCD VO pin to ground
 * LCD A pin to 5V
 * LCD K pin to ground
 */

#include <LiquidCrystal.h>

LiquidCrystal lcd(12, 11, 5, 4, 3, 2);

struct _message{
  String message;
  char command[2];
};

void stringToChar(String dataIn, char dataOut[], int lengthData){
  dataIn.toCharArray(dataOut, lengthData+1);
}

boolean isContainChar(char dataText[], char text){
  boolean isFound = false;
  for(int i=0;i<strlen(dataText);i++){
    if(dataText[i]==text){
      isFound=true;
      break;
    }
  }
  return isFound;
}

void splitCharCommand(char input[], char command[], char delimiter){
  int panjang = strlen(input);
  command[0] = input[panjang-1];
  command[1] = '\0';
}

_message *getData(String message){
  _message *data = NULL;

  int panjang = message.length();
  char buff[panjang+1];
  stringToChar(message, buff, panjang);
  if(isContainChar(buff, ':')){
    data = new _message;
    splitCharCommand(buff, data->command, ':');
    for(int i=0;i<panjang-2;i++){
      data->message += buff[i];
    }
    data->message[panjang-1] ='\0';
  }
  return data;
}

void setup() {
  // set up the LCD's number of columns and rows:
  lcd.begin(16, 2);
  // Print a message to the LCD.
  //lcd.print("hello, world!");
  Serial.begin(9600);
  lcd.setCursor(0,0);
  lcd.print("Hello World");
}

int lastTime = 0;
int time = 0;
int counter = 0;

String receivedText;

_message *data;
void loop() {
  if(Serial.available()){
    data = NULL;
    lcd.setCursor(0,0);
    receivedText = Serial.readString();
    data = getData(receivedText);
    if(data==NULL){
      lcd.clear();
      lcd.print(receivedText);
    }else{
      if(data->command[0]=='c' || data->command[0] == 'C'){
        lcd.clear();
      }else if(data->command[0]=='r' || data->command[0] == 'R'){
        lcd.clear();
        counter = -1;
        lcd.print(data->message);
      }else if(data->command[0]=='m' || data->command[0] == 'M'){
        lcd.clear();
        lcd.print(data->message);
      }
    }
  }
  // set the cursor to column 0, line 1
  // (note: line 1 is the second row, since counting begins with 0):
  lcd.setCursor(0, 1);
  
  time = millis()/1000;
  
  // print the number of seconds since reset:
  if(time!=lastTime){   
    lastTime = time;
    counter++;
    lcd.print(counter);
  }
  delay(50);
}

