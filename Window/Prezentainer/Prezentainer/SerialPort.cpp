#include "StdAfx.h"

#include "SerialPort.h"
#include <assert.h>
// �Ʒ� afx.h ����� TRACE()�� ����ϱ� ���� �߰���.
#define _AFXDLL
//#include <afx.h>



CSerialPort::CSerialPort() //��Ʈ ���¸� �ʱ�ȭ(������)
{
	_hSerial = INVALID_HANDLE_VALUE;
}

CSerialPort::~CSerialPort() //Closeȣ��(�Ҹ���)
{
	Close ();
}

bool CSerialPort::Open (const char *portName, long baudRate, char dataBits, char parity, char stopBits) //��Ʈ�� OPEN�ϴ� �Լ���.
{
	if (_hSerial != INVALID_HANDLE_VALUE) {  //��Ʈ�� ��������� Ȯ��,������� ��� false������.
		return false;
	}

	_hSerial = CreateFile (portName, GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0);

	if (_hSerial == INVALID_HANDLE_VALUE){  //��Ʈ�� ���������� �������� Ȯ��, ���������� ������ �ʾ����� false ������.
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

void CSerialPort::Close () //��Ʈ�� �ݾ��ִ� ������ �ϴ� �Լ���.
{
	if (_hSerial != INVALID_HANDLE_VALUE) {
		CloseHandle (_hSerial);

		_hSerial = INVALID_HANDLE_VALUE;

	}
}

int CSerialPort::Read(char *data, int maxDataLength) //��Ʈ���� �����͸� �о���̴� ������ �ϴ� �Լ���.
{
	DWORD readBytes = 0;

	if (!ReadFile (_hSerial, data, maxDataLength, &readBytes, NULL)){

		return -1;
	}
	return readBytes;
}

int CSerialPort::Write(const char *data, int dataLength) //OPEN�� ��Ʈ�� �����͸� ���� ������ �ϴ� �Լ���. 
{
	DWORD writtenBytes = 0;

	if (!WriteFile (_hSerial, data, dataLength, &writtenBytes, NULL)) {

		return -1;
	}
	return writtenBytes;
}

void CSerialPort::Flush() //��Ʈ�� ���۸� ���� ������ �ϴ� �Լ���.
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

bool CSerialPort::SetTimeout(int readTimeout, int writeTimeout, int readIntervalTimeout) //�����Ʈ���� timeout�� �������ִ� �Լ���.
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

int CSerialPort::CountReadBuff() //���۾��� ������ ���� �Լ���.
{
	DWORD comError = 0;
	COMSTAT comStat;

	if (!ClearCommError(_hSerial, &comError, &comStat)) {
		return -1;
	}
	return comStat.cbInQue;
}

const char *CSerialPort::GetLastErrorString() //���� string �����ִ� �Լ���.
{
	static char lastError[1024];
	
	FormatMessage (FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
		NULL, GetLastError(), MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), lastError, 1024, NULL);

	return lastError;
}
