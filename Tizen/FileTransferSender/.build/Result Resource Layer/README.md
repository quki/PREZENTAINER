#SAP_Gear
***  
This is **Gear side code**.  
Part of the our ongoing project by using the __*Samsung Accessory Protocol*__ and __*Accessory File Transfer*__.  
You can also see the **Android side code**, [SAP_Android](https://github.com/quki/SAP_Android).

## Usage
1. Connect
 * Initializing Peer Agents matched by same profile.
 * Creating a Service Connection and socket to be returned.
 * Having two channel, one is for sending Heart Rate and the other is for event to PC. 
 * ( Not necessary Service Connection to send file if you use the Accessory File Transfer. )
 
2. Start
 * Recording your voice.
 * Checking your Heart rate and transfering all data measured every per 5 sec.
 
3. Event To PC
 * Transfering event to PC paired with Android host device by SPP.
 
4. Stop
 * Sending your 'myvoice.amr' to Android host device.
 * Turnning off Heart rate sensor.
 * Disconnecting the Service Connection.  
 
## Developer
>Team : 맑은고딕  
Members : 김형곤, 오승호, 이병근, 최요한  
Contact : quki09@naver.com


