#pragma once
#include <windows.h>
typedef void *HANDLE;

class CSerialPort
{
public:
	CSerialPort ();
	~CSerialPort ();

	bool Open (const char *portName, long baudRate, char dataBits, char parity, char stopBits);
	void Close ();

	int Read (char *data, int maxDataLength);
	int Write (const char *data, int dataLength);
	
	void Flush ();
	bool SetTimeout (int readTimeout, int writeTimeout, int readIntervalTimeout);

	int CountReadBuff ();


private:
	HANDLE _hSerial; 

	const char *GetLastErrorString();
};