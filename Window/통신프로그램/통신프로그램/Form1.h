
#pragma once
#include <stdio.h>
#include "stdafx.h"
#include "SerialPort.h"
#include <windows.h>
#include <msclr\marshal_cppstd.h>
#include <boost/thread/thread.hpp>
#include "shellapi.h"


//#include <assert.h>
char name[20]="\\\\.\\COM";


int isStringDouble(const char *s) {
  size_t size = strlen(s);
  if (size == 0) return 0; // 0바이트 문자열은 숫자가 아님

  for (int i = 0; i < (int) size; i++) {
    if (s[i] == '.' || s[i] == '-' || s[i] == '+') continue;
    if (s[i] < '0' || s[i] > '9') return 0; // 알파벳 등이 있으면 숫자 아님
  }

  return 1; // 그밖의 경우는 숫자임
}


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
/*
void run_ready()
{
	CSerialPort com1;
	com1.Open (name, CBR_115200, 8, ONESTOPBIT, NOPARITY);
	com1.SetTimeout (10, 10, 1);

			char buff[6]="0";
			//int n;
			while (1) {
				//printf ("\nWRITE: ");
				//scanf ("%s", buff);
				//n = strlen(buff);

				//		com1.Write (buff, n);

				if(stop==true)  //중지버튼 눌렀을 때 무조건 정지
				{
					break;
				}
				//
				com1.Read (buff, 6);

				if(strcmp(buff,"conne")==0)
				{ 
					connection_check=true;
					break;
				}
				else
				{
					connection_check=false;
				}
			}
			com1.Close();
		
		}	
*/
void run_program()
		{

			CSerialPort com1;
			com1.Open (name, CBR_115200, 8, ONESTOPBIT, NOPARITY);
			com1.SetTimeout (10, 10, 1);


			char buff[6]="0";
			//int n;
			while (1) {
				//printf ("\nWRITE: ");
				//scanf ("%s", buff);
				//n = strlen(buff);

				//		com1.Write (buff, n);

		
				//
				com1.Read (buff, 6);
                



				if(strcmp(buff,"right")==0)
				{
					PressVirtualKeyboad(VK_RIGHT);
					strcpy(buff,"0");
				}
				else if(strcmp(buff,"left")==0)
				{
					PressVirtualKeyboad(VK_RETURN);
					strcpy(buff,"0");
				}
				else
				{
					continue;
				}
			}
			com1.Close();
		
		}


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

	private: System::Windows::Forms::Button^  button2;

	private: System::Windows::Forms::Label^  label2;

	private: System::Windows::Forms::Button^  button3;
	private: System::Windows::Forms::ToolTip^  toolTip1;
	private: System::Windows::Forms::LinkLabel^  linkLabel1;


	private: System::ComponentModel::IContainer^  components;

	private:
		/// <summary>
		/// 필수 디자이너 변수입니다.
		/// </summary>


#pragma region Windows Form Designer generated code
		/// <summary>
		/// 디자이너 지원에 필요한 메서드입니다.
		/// 이 메서드의 내용을 코드 편집기로 수정하지 마십시오.
		/// </summary>
		void InitializeComponent(void)
		{
			this->components = (gcnew System::ComponentModel::Container());
			System::ComponentModel::ComponentResourceManager^  resources = (gcnew System::ComponentModel::ComponentResourceManager(Form1::typeid));
			this->label1 = (gcnew System::Windows::Forms::Label());
			this->textBox1 = (gcnew System::Windows::Forms::TextBox());
			this->button2 = (gcnew System::Windows::Forms::Button());
			this->label2 = (gcnew System::Windows::Forms::Label());
			this->button3 = (gcnew System::Windows::Forms::Button());
			this->toolTip1 = (gcnew System::Windows::Forms::ToolTip(this->components));
			this->linkLabel1 = (gcnew System::Windows::Forms::LinkLabel());
			this->SuspendLayout();
			// 
			// label1
			// 
			this->label1->AccessibleName = L"";
			this->label1->Font = (gcnew System::Drawing::Font(L"굴림", 9, static_cast<System::Drawing::FontStyle>((System::Drawing::FontStyle::Bold | System::Drawing::FontStyle::Italic)), 
				System::Drawing::GraphicsUnit::Point, static_cast<System::Byte>(129)));
			this->label1->Location = System::Drawing::Point(3, 15);
			this->label1->Name = L"label1";
			this->label1->Size = System::Drawing::Size(109, 23);
			this->label1->TabIndex = 0;
			this->label1->Text = L"Port_number";
			this->label1->TextAlign = System::Drawing::ContentAlignment::MiddleCenter;
			this->label1->Click += gcnew System::EventHandler(this, &Form1::label1_Click);
			// 
			// textBox1
			// 
			this->textBox1->Location = System::Drawing::Point(107, 17);
			this->textBox1->Name = L"textBox1";
			this->textBox1->Size = System::Drawing::Size(115, 21);
			this->textBox1->TabIndex = 1;
			this->textBox1->TextChanged += gcnew System::EventHandler(this, &Form1::textBox1_TextChanged);
			this->textBox1->MouseHover += gcnew System::EventHandler(this, &Form1::textBox1_MouseHover);
			// 
			// button2
			// 
			this->button2->Enabled = false;
			this->button2->Location = System::Drawing::Point(118, 81);
			this->button2->Name = L"button2";
			this->button2->Size = System::Drawing::Size(104, 51);
			this->button2->TabIndex = 2;
			this->button2->Text = L"실행";
			this->button2->UseVisualStyleBackColor = true;
			this->button2->Click += gcnew System::EventHandler(this, &Form1::button2_Click);
			// 
			// label2
			// 
			this->label2->Location = System::Drawing::Point(79, 41);
			this->label2->Name = L"label2";
			this->label2->Size = System::Drawing::Size(143, 27);
			this->label2->TabIndex = 3;
			this->label2->TextAlign = System::Drawing::ContentAlignment::MiddleCenter;
			// 
			// button3
			// 
			this->button3->Location = System::Drawing::Point(5, 81);
			this->button3->Name = L"button3";
			this->button3->Size = System::Drawing::Size(104, 51);
			this->button3->TabIndex = 5;
			this->button3->Text = L"포트정보";
			this->button3->UseVisualStyleBackColor = true;
			this->button3->Click += gcnew System::EventHandler(this, &Form1::button3_Click);
			// 
			// toolTip1
			// 
			this->toolTip1->IsBalloon = true;
			// 
			// linkLabel1
			// 
			this->linkLabel1->AutoSize = true;
			this->linkLabel1->Location = System::Drawing::Point(12, 41);
			this->linkLabel1->Name = L"linkLabel1";
			this->linkLabel1->Size = System::Drawing::Size(41, 12);
			this->linkLabel1->TabIndex = 6;
			this->linkLabel1->TabStop = true;
			this->linkLabel1->Text = L"도움말";
			this->linkLabel1->LinkClicked += gcnew System::Windows::Forms::LinkLabelLinkClickedEventHandler(this, &Form1::linkLabel1_LinkClicked);
			// 
			// Form1
			// 
			this->AutoScaleDimensions = System::Drawing::SizeF(7, 12);
			this->AutoScaleMode = System::Windows::Forms::AutoScaleMode::Font;
			this->ClientSize = System::Drawing::Size(228, 154);
			this->Controls->Add(this->linkLabel1);
			this->Controls->Add(this->button3);
			this->Controls->Add(this->label2);
			this->Controls->Add(this->button2);
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

		private: System::Void button2_Click(System::Object^  sender, System::EventArgs^  e) {
					 // CSerialPort com1;
					 // char name[20]="\\\\.\\COM";
					 firstName = textBox1->Text;
					 msclr::interop::marshal_context context;
					 std::string num = context.marshal_as<std::string>(firstName); 


					 if(strcmp(num.c_str(),"")==0)
					 {
						 label2->Text = "Port번호를 입력해주세요";
					 }
					 else
					 {
						 if(isStringDouble(num.c_str()))
						 {
							 strcat(name, num.c_str());
							 button2->Enabled = false;
							 label2->Text = "Port :"+firstName+"접속!";
							 boost::thread t(&run_program);
							 textBox1->Enabled = false;
						 }
						 else
						 {
							 label2->Text = "error! 숫자를 입력해주세요!";
						 }
					 }

				 }



	     private: System::Void label1_Click(System::Object^  sender, System::EventArgs^  e) {
				 }

private: System::Void Form1_Load(System::Object^  sender, System::EventArgs^  e) {
		 }


private: System::Void button3_Click(System::Object^  sender, System::EventArgs^  e) {
			 WinExec("C:/Windows/System32/rundll32.exe shell32.dll,Control_RunDLL bthprops.cpl,,2",SW_SHOW);
		 }
private: System::Void textBox1_MouseHover(System::Object^  sender, System::EventArgs^  e) {
			 toolTip1->SetToolTip(textBox1,"Enter only Port Number!");
		 }
private: System::Void textBox1_TextChanged(System::Object^  sender, System::EventArgs^  e) {
			 button2->Enabled = true;
		 }
private: System::Void linkLabel1_LinkClicked(System::Object^  sender, System::Windows::Forms::LinkLabelLinkClickedEventArgs^  e) {
			  WinExec("C:\\Program Files\\Internet Explorer\\IEXPLORE.EXE http://google.com",SW_SHOW); 
		      //system("explorer http://naver.com");
			  // ShellExecute(NULL, NULL, "http://lp.com", NULL, NULL, SW_SHOW);

		 }
};
}

