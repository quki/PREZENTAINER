#include "StdAfx.h"

#include "SerialPort.h"
#include <assert.h>
// 아래 afx.h 헤더는 TRACE()를 사용하기 위해 추가함.
#define _AFXDLL
//#include <afx.h>



CSerialPort::CSerialPort() //포트 상태를 초기화(생성자)
{
	_hSerial = INVALID_HANDLE_VALUE;
}

CSerialPort::~CSerialPort() //Close호출(소멸자)
{
	Close ();
}

bool CSerialPort::Open (const char *portName, long baudRate, char dataBits, char parity, char stopBits) //포트를 OPEN하는 함수임.
{
	if (_hSerial != INVALID_HANDLE_VALUE) {  //포트가 사용중인지 확인,사용중일 경우 false리턴함.
		return false;
	}

	_hSerial = CreateFile (portName, GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0);

	if (_hSerial == INVALID_HANDLE_VALUE){  //포트가 정상적으로 열였는지 확인, 정상적으로 열리지 않았으면 false 리턴함.
		return false;
	}

	PurgeComm (_hSerial, PURGE_TXABORT|PURGE_RXABORT|PURGE_TXCLEAR|PURGE_RXCLEAR);

	DCB dcbSerialParams;
	
	if (!GetCommState (_hSerial, &dcbSerialParams)) {
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
		return false;
	}


	return true;
}

void CSerialPort::Close () //포트를 닫아주는 역할을 하는 함수임.
{
	if (_hSerial != INVALID_HANDLE_VALUE) {
		CloseHandle (_hSerial);

		_hSerial = INVALID_HANDLE_VALUE;

	}
}

int CSerialPort::Read(char *data, int maxDataLength) //포트에서 데이터를 읽어들이는 역할을 하는 함수임.
{
	DWORD readBytes = 0;

	if (!ReadFile (_hSerial, data, maxDataLength, &readBytes, NULL)){

		return -1;
	}
	return readBytes;
}

int CSerialPort::Write(const char *data, int dataLength) //OPEN된 포트로 데이터를 쓰는 역할을 하는 함수임. 
{
	DWORD writtenBytes = 0;

	if (!WriteFile (_hSerial, data, dataLength, &writtenBytes, NULL)) {

		return -1;
	}
	return writtenBytes;
}

void CSerialPort::Flush() //포트의 버퍼를 비우는 역할을 하는 함수임.
{
	DWORD comError = 0;
	COMSTAT comStat;

	if (!ClearCommError (_hSerial, &comError, &comStat)) {
		return;
	}
	if (!PurgeComm (_hSerial, PURGE_TXABORT|PURGE_RXABORT|PURGE_TXCLEAR|PURGE_RXCLEAR)) {
		return;
	}
}

bool CSerialPort::SetTimeout(int readTimeout, int writeTimeout, int readIntervalTimeout) //통신포트에서 timeout을 설정해주는 함수임.
{
	COMMTIMEOUTS commTimeout;

	if (!GetCommTimeouts (_hSerial, &commTimeout)) {
		return false;
	}

	commTimeout.ReadIntervalTimeout = readIntervalTimeout;
	commTimeout.ReadTotalTimeoutMultiplier = 0;
	commTimeout.ReadTotalTimeoutConstant = readTimeout;
	commTimeout.WriteTotalTimeoutMultiplier = 0;
	commTimeout.WriteTotalTimeoutConstant = writeTimeout;

	if (!SetCommTimeouts (_hSerial, &commTimeout)) {
		return false;
	}
	return true;
}

int CSerialPort::CountReadBuff() //버퍼안의 갯수를 세는 함수임.
{
	DWORD comError = 0;
	COMSTAT comStat;

	if (!ClearCommError(_hSerial, &comError, &comStat)) {
		return -1;
	}
	return comStat.cbInQue;
}

const char *CSerialPort::GetLastErrorString() //에러 string 보여주는 함수임.
{
	static char lastError[1024];
	
	FormatMessage (FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
		NULL, GetLastError(), MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), lastError, 1024, NULL);

	return lastError;
}
