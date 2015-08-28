#include "StdAfx.h"

#include "SerialPort.h"
#include <assert.h>
// 아래 afx.h 헤더는 TRACE()를 사용하기 위해 추가함.
#define _AFXDLL
//#include <afx.h>



CSerialPort::CSerialPort()
{
	_hSerial = INVALID_HANDLE_VALUE;
}

CSerialPort::~CSerialPort()
{
	Close ();
}

bool CSerialPort::Open (const char *portName, long baudRate, char dataBits, char parity, char stopBits)
{
	if (_hSerial != INVALID_HANDLE_VALUE) {
		//TRACE ("ERROR: Open(): %s, %d, Port is already opened\n", portName, baudRate);
		return false;
	}

	_hSerial = CreateFile (portName, GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0);
	if (_hSerial == INVALID_HANDLE_VALUE){
		//TRACE ("ERROR: CreateFile(): %s, %d, %s", portName, baudRate, GetLastErrorString());
		return false;
	}

	//SetupComm (_hSerial, 8192, 8192);
	PurgeComm (_hSerial, PURGE_TXABORT|PURGE_RXABORT|PURGE_TXCLEAR|PURGE_RXCLEAR);

	DCB dcbSerialParams;
	
	if (!GetCommState (_hSerial, &dcbSerialParams)) {
		//TRACE ("ERROR: GetCommState (): %s", GetLastErrorString());
		return false;
	}

	dcbSerialParams.BaudRate = baudRate;
	dcbSerialParams.ByteSize = dataBits;
	dcbSerialParams.Parity = parity;
	dcbSerialParams.StopBits = stopBits;
	dcbSerialParams.fBinary = true;
	dcbSerialParams.fDsrSensitivity = false;
	dcbSerialParams.fParity = 0;
	dcbSerialParams.fOutX = false;
	dcbSerialParams.fInX = false;
	dcbSerialParams.fNull = false;
	dcbSerialParams.fAbortOnError = false;
	dcbSerialParams.fOutxCtsFlow = false;
	dcbSerialParams.fOutxDsrFlow = false;
	dcbSerialParams.fDtrControl = DTR_CONTROL_DISABLE;
	dcbSerialParams.fDsrSensitivity = false;
	dcbSerialParams.fRtsControl = RTS_CONTROL_DISABLE;
	dcbSerialParams.fOutxCtsFlow = false;
	dcbSerialParams.fOutxCtsFlow = false;
	
	if (!SetCommState(_hSerial, &dcbSerialParams)) {
		//TRACE ("ERROR: SetCommState(): %s", GetLastErrorString());
		return false;
	}

	//TRACE ("SUCCESS: Open(): %s, %d, open serial port\n", portName, baudRate);
	return true;
}

void CSerialPort::Close ()
{
	if (_hSerial != INVALID_HANDLE_VALUE) {
		CloseHandle (_hSerial);

		_hSerial = INVALID_HANDLE_VALUE;
		//TRACE ("SUCCESS: Close(): close serial port\n");
	}
}

int CSerialPort::Read(char *data, int maxDataLength)
{
	DWORD readBytes = 0;

	if (!ReadFile (_hSerial, data, maxDataLength, &readBytes, NULL)){
		//TRACE ("ERROR: ReadFile(): %s", GetLastErrorString());
		return -1;
	}
	return readBytes;
}

int CSerialPort::Write(const char *data, int dataLength)
{
	DWORD writtenBytes = 0;

	if (!WriteFile (_hSerial, data, dataLength, &writtenBytes, NULL)) {
		//TRACE ("ERROR: WriteFile(): %s", GetLastErrorString());
		return -1;
	}
	return writtenBytes;
}

void CSerialPort::Flush()
{
	DWORD comError = 0;
	COMSTAT comStat;

	if (!ClearCommError (_hSerial, &comError, &comStat)) {
		//TRACE ("ERROR: ClearCommError(): %s", GetLastErrorString());
		return;
	}
	if (!PurgeComm (_hSerial, PURGE_TXABORT|PURGE_RXABORT|PURGE_TXCLEAR|PURGE_RXCLEAR)) {
		//TRACE ("ERROR: PurgeComm(): %s", GetLastErrorString());
		return;
	}
}

bool CSerialPort::SetTimeout(int readTimeout, int writeTimeout, int readIntervalTimeout)
{
	COMMTIMEOUTS commTimeout;

	if (!GetCommTimeouts (_hSerial, &commTimeout)) {
		//TRACE ("ERROR: GetCommTimeouts(): %s", GetLastErrorString());
		return false;
	}

	commTimeout.ReadIntervalTimeout = readIntervalTimeout;
	commTimeout.ReadTotalTimeoutMultiplier = 0;
	commTimeout.ReadTotalTimeoutConstant = readTimeout;
	commTimeout.WriteTotalTimeoutMultiplier = 0;
	commTimeout.WriteTotalTimeoutConstant = writeTimeout;

	if (!SetCommTimeouts (_hSerial, &commTimeout)) {
		//TRACE ("ERROR: SetCommTimeouts(): %s", GetLastErrorString());
		return false;
	}
	return true;
}

int CSerialPort::CountReadBuff()
{
	DWORD comError = 0;
	COMSTAT comStat;

	if (!ClearCommError(_hSerial, &comError, &comStat)) {
		//TRACE ("ERROR: ClearCommError(): %s", GetLastErrorString());
		return -1;
	}
	return comStat.cbInQue;
}

const char *CSerialPort::GetLastErrorString()
{
	static char lastError[1024];
	
	FormatMessage (FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
		NULL, GetLastError(), MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), lastError, 1024, NULL);

	return lastError;
}
