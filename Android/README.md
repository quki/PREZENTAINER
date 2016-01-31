#PREZENTAINER Android Side Code  
You can also see the **Tizen side code**, [Tizen](https://github.com/quki/PREZENTAINER/tree/master/Tizen-S2).  
You can also see the **Window side code**, [Window](https://github.com/quki/PREZENTAINER/tree/master/Window).
### Development Description
1. Pairing a PC(Window) by Serial Port Profile(SPP): Bluetooth Serial Communication. 
2. Pairing a wearable device(Tizen) by Samsung Accessory Protocol(SAP) and Samsung Accessory File Transfer(SAFT).
3. Requesting Service Connction to Peer Agents.
4. Receiving the voice recording file which is saved at the default path(SD card) by SAFT
5. Receiving the heart rate datas, motion info and event time(slide change time) and transfering to our AWS instance by JSON.
6. Making some Threads in order to reload UI and communicate with server in order to transfer the data such as user info, heart rate data and event time info.