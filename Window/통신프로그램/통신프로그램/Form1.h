#pragma once
#include <stdio.h>
#include "stdafx.h"
#include "SerialPort.h"
#include <windows.h>
#include <msclr\marshal_cppstd.h>
#include <boost/thread/thread.hpp>
#include "shellapi.h"


//#include <assert.h>
CSerialPort com1;
char name[20]="\\\\.\\COM";
bool check=false;
bool overlap_check=false;
void PressVirtualKeyboad(BYTE vk) {

	// Simulate a key press

	keybd_event( vk, vk, 0, 0 );


	// Simulate a key release

	keybd_event( vk, vk, KEYEVENTF_KEYUP, 0);

}
void PressLeft() {

	PressVirtualKeyboad(VK_RETURN);

}
void PressRight() {

	PressVirtualKeyboad(VK_RIGHT);
	
}
void run_program()
		{

			com1.Open (name, CBR_115200, 8, ONESTOPBIT, NOPARITY);
			com1.SetTimeout (10, 10, 1);


			char buff[6]="0";
			//int n;
			while (1) {
				//printf ("\nWRITE: ");
				//scanf ("%s", buff);
				//n = strlen(buff);

				//		com1.Write (buff, n);

				if(check==false)
				{
					break;
				}

				com1.Read (buff, 6);




				if(strcmp(buff,"0")==0)
				{}
				else
				{
					PressVirtualKeyboad(VK_RIGHT);

					//	printf ("READ: %s\n", buff);
					strcpy(buff,"0");
				}
			}
			com1.Close();
		}


/*  http://stackoverflow.com/questions/4666635/run-threads-of-class-member-function-in-c 참조사이트
class runnable
{
public:
    virtual ~runnable() {}
    static DWORD WINAPI run_thread(LPVOID args)
    {
        runnable *prunnable = static_cast<runnable*>(args);
        return prunnable->run();
    }
 protected:
    virtual DWORD run() = 0; //derived class must implement this!
};

class Thread : public runnable //derived from runnable!
{
public:
    void newthread()
    {
        CreateThread(NULL, 0, &runnable::run_thread, this, 0, NULL);
    }
protected:
    DWORD run() //implementing the virtual function!
    {
        // .....your thread execution code.....

		while(1)
		{
			printf("asd\n");
		}
    }
};
*/

namespace 통신프로그램 {

	using namespace System;
	using namespace System::ComponentModel;
	using namespace System::Collections;
	using namespace System::Windows::Forms;
	using namespace System::Data;
	using namespace System::Drawing;

	/// <summary>
	/// Form1에 대한 요약입니다.
	/// </summary>




	public ref class Form1 : public System::Windows::Forms::Form
	{
	public:
		Form1(void)
		{
			InitializeComponent();
			//
			//TODO: 생성자 코드를 여기에 추가합니다.
			//
		}

	protected:
		/// <summary>
		/// 사용 중인 모든 리소스를 정리합니다.
		/// </summary>
		~Form1()
		{
			if (components)
			{
				delete components;
			}
		}
	private: System::Windows::Forms::Label^  label1;
	protected: 
	private: System::Windows::Forms::TextBox^  textBox1;
	private: System::Windows::Forms::Button^  button1;
	private: System::Windows::Forms::Button^  button2;

	private: System::Windows::Forms::Label^  label2;
	private: System::Windows::Forms::Button^  button4;














	private:
		/// <summary>
		/// 필수 디자이너 변수입니다.
		/// </summary>
		System::ComponentModel::Container ^components;

#pragma region Windows Form Designer generated code
		/// <summary>
		/// 디자이너 지원에 필요한 메서드입니다.
		/// 이 메서드의 내용을 코드 편집기로 수정하지 마십시오.
		/// </summary>
		void InitializeComponent(void)
		{
			System::ComponentModel::ComponentResourceManager^  resources = (gcnew System::ComponentModel::ComponentResourceManager(Form1::typeid));
			this->label1 = (gcnew System::Windows::Forms::Label());
			this->textBox1 = (gcnew System::Windows::Forms::TextBox());
			this->button1 = (gcnew System::Windows::Forms::Button());
			this->button2 = (gcnew System::Windows::Forms::Button());
			this->label2 = (gcnew System::Windows::Forms::Label());
			this->button4 = (gcnew System::Windows::Forms::Button());
			this->SuspendLayout();
			// 
			// label1
			// 
			this->label1->AccessibleName = L"";
			this->label1->Font = (gcnew System::Drawing::Font(L"굴림", 9, static_cast<System::Drawing::FontStyle>((System::Drawing::FontStyle::Bold | System::Drawing::FontStyle::Italic)), 
				System::Drawing::GraphicsUnit::Point, static_cast<System::Byte>(129)));
			this->label1->Location = System::Drawing::Point(12, 15);
			this->label1->Name = L"label1";
			this->label1->Size = System::Drawing::Size(100, 23);
			this->label1->TabIndex = 0;
			this->label1->Text = L"Port_number";
			this->label1->TextAlign = System::Drawing::ContentAlignment::MiddleCenter;
			this->label1->Click += gcnew System::EventHandler(this, &Form1::label1_Click);
			// 
			// textBox1
			// 
			this->textBox1->Location = System::Drawing::Point(107, 17);
			this->textBox1->Name = L"textBox1";
			this->textBox1->Size = System::Drawing::Size(127, 21);
			this->textBox1->TabIndex = 1;
			// 
			// button1
			// 
			this->button1->Location = System::Drawing::Point(12, 60);
			this->button1->Name = L"button1";
			this->button1->Size = System::Drawing::Size(69, 51);
			this->button1->TabIndex = 2;
			this->button1->Text = L"입력";
			this->button1->UseVisualStyleBackColor = true;
			this->button1->Click += gcnew System::EventHandler(this, &Form1::button1_Click);
			// 
			// button2
			// 
			this->button2->Location = System::Drawing::Point(87, 60);
			this->button2->Name = L"button2";
			this->button2->Size = System::Drawing::Size(69, 51);
			this->button2->TabIndex = 2;
			this->button2->Text = L"실행";
			this->button2->UseVisualStyleBackColor = true;
			this->button2->Click += gcnew System::EventHandler(this, &Form1::button2_Click);
			// 
			// label2
			// 
			this->label2->Location = System::Drawing::Point(12, 38);
			this->label2->Name = L"label2";
			this->label2->Size = System::Drawing::Size(100, 19);
			this->label2->TabIndex = 3;
			this->label2->TextAlign = System::Drawing::ContentAlignment::MiddleCenter;
			// 
			// button4
			// 
			this->button4->Location = System::Drawing::Point(166, 60);
			this->button4->Name = L"button4";
			this->button4->Size = System::Drawing::Size(68, 51);
			this->button4->TabIndex = 4;
			this->button4->Text = L"중지";
			this->button4->UseVisualStyleBackColor = true;
			this->button4->Click += gcnew System::EventHandler(this, &Form1::button4_Click_1);
			// 
			// Form1
			// 
			this->AutoScaleDimensions = System::Drawing::SizeF(7, 12);
			this->AutoScaleMode = System::Windows::Forms::AutoScaleMode::Font;
			this->ClientSize = System::Drawing::Size(246, 131);
			this->Controls->Add(this->button4);
			this->Controls->Add(this->label2);
			this->Controls->Add(this->button2);
			this->Controls->Add(this->button1);
			this->Controls->Add(this->textBox1);
			this->Controls->Add(this->label1);
			this->Icon = (cli::safe_cast<System::Drawing::Icon^  >(resources->GetObject(L"$this.Icon")));
			this->Name = L"Form1";
			this->Text = L"블루투스 통신";
			this->Load += gcnew System::EventHandler(this, &Form1::Form1_Load);
			this->ResumeLayout(false);
			this->PerformLayout();

		}

#pragma endregion
		String^  firstName;

		private: System::Void button1_Click(System::Object^  sender, System::EventArgs^  e) {

					 firstName = textBox1->Text;
					 label2->Text = "Port :"+firstName;
					 check=false;
					 overlap_check=false;
					// WinExec("C:/Windows/System32/rundll32.EXE",SW_SHOW);
					WinExec("C:/Windows/System32/rundll32.exe shell32.dll,Control_RunDLL bthprops.cpl,,2",SW_SHOW);
					 // C:\Windows\System32

				 }
		private: System::Void button2_Click(System::Object^  sender, System::EventArgs^  e) {
					 // CSerialPort com1;
					 // char name[20]="\\\\.\\COM";

					 if(overlap_check==false)
					 {

					 check=true;
					 msclr::interop::marshal_context context;
					 std::string num = context.marshal_as<std::string>(firstName);  //http://www.cplusplus.com/forum/windows/137117/ 참고자료
					 strcat(name, num.c_str());                                      //포트입력버튼에서 입력한 포트번호로 포트통로열기!

					 label2->Text = "Port :"+firstName+" 연결중";
					 boost::thread t(&run_program);
					 overlap_check=true;
					 }
					 else
					 {
					 }
					 
				 }

		 private: System::Void button4_Click_1(System::Object^  sender, System::EventArgs^  e) {
					  check=false;
					  overlap_check=false;
					  label2->Text = "Port :"+firstName+" 연결 중지";
				  }


	     private: System::Void label1_Click(System::Object^  sender, System::EventArgs^  e) {
				 }




private: System::Void Form1_Load(System::Object^  sender, System::EventArgs^  e) {
		 }


};
}

