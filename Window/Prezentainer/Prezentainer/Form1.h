#pragma once
#include <stdio.h>
#include "stdafx.h"
#include "SerialPort.h"
#include <windows.h>
#include <msclr\marshal_cppstd.h>
#include <boost/thread/thread.hpp> //Visual Studio2010���� �����带 ����ϱ����� boost���̺귯���� thread�� �̿���.
#include "shellapi.h"

char name[20]="\\\\.\\COM";

int isString(const char *s) { //�Է¹��� string�� ���������� üũ�ϴ� �Լ���.
  size_t size = strlen(s);
  if (size == 0) return 0; // 0����Ʈ ���ڿ��� ���ڰ� �ƴ�.

  for (int i = 0; i < (int) size; i++) {
    if (s[i] < '0' || s[i] > '9') return 0; // ���ĺ� ���� ������ ���� �ƴ�.
  }

  return 1; // �׹��� ���� ������.
}

void PressVirtualKeyboad(BYTE vk) {

	// Simulate a key press
	keybd_event( vk, vk, 0, 0 );

	// Simulate a key release
	keybd_event( vk, vk, KEYEVENTF_KEYUP, 0);

}
void PressLeft() {

	PressVirtualKeyboad(VK_LEFT); //������ api ��� ����Ű �̺�Ʈ �߻���Ŵ.

}
void PressRight() {

	PressVirtualKeyboad(VK_RIGHT); //������ api ��� ������Ű �̺�Ʈ �߻���Ŵ.
	
}

void run_program() //������� ����� ���� serialPort�� ���� ������ �̺�Ʈ�� ó���ϴ� �κ���.
		{

			CSerialPort com1;                                      //�����Ʈ ��ü ������.
			com1.Open (name, CBR_115200, 8, ONESTOPBIT, NOPARITY);  //�����Ʈ Open��.
			com1.SetTimeout (10, 10, 1);//SetTimeout�� ������.

			char buff[6]="0"; //read�� ���۸� ����δ� ������ ��.(0���� �־��.)

			while (1)
			
			{
				com1.Read (buff, 6);
                
				if(strcmp(buff,"right")==0)        //��Ʈ������ ���� �о���� ���� right�ϰ�� ->�̺�Ʈ ������.
				{
					PressRight();
					strcpy(buff,"0");  //�κ�Ʈ �߻��Ŀ� ���� Ű�̺�Ʈ�� �޾Ƶ��̱� ���� ���۸� 0 ���θ���.
				}
				else if(strcmp(buff,"leftt")==0) //��Ʈ������ ���� �о���� ���� left�ϰ�� ->�̺�Ʈ ������.
				{
					PressLeft();
					strcpy(buff,"0"); //�κ�Ʈ �߻��Ŀ� ���� Ű�̺�Ʈ�� �޾Ƶ��̱� ���� ���۸� 0 ���θ���.
				}
				else
				{
					continue;        //�¿� �̺�Ʈ�� ������ ������ ���� ������ �����°� �ǵ�����.
				}
			}
		}

namespace Prezentainer {

	using namespace System;
	using namespace System::ComponentModel;
	using namespace System::Collections;
	using namespace System::Windows::Forms;
	using namespace System::Data;
	using namespace System::Drawing;

	/// <summary>
	/// Form1�� ���� ����Դϴ�.
	/// </summary>




	public ref class Form1 : public System::Windows::Forms::Form
	{
	public:
		Form1(void)
		{
			InitializeComponent();
			//
			//TODO: ������ �ڵ带 ���⿡ �߰��մϴ�.
			//
		}

	protected:
		/// <summary>
		/// ��� ���� ��� ���ҽ��� �����մϴ�.
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
		/// �ʼ� �����̳� �����Դϴ�.
		/// </summary>


#pragma region Windows Form Designer generated code
		/// <summary>
		/// �����̳� ������ �ʿ��� �޼����Դϴ�.
		/// �� �޼����� ������ �ڵ� ������� �������� ���ʽÿ�.
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
			this->label1->Font = (gcnew System::Drawing::Font(L"���� ���", 9, System::Drawing::FontStyle::Bold, System::Drawing::GraphicsUnit::Point, 
				static_cast<System::Byte>(129)));
			this->label1->Location = System::Drawing::Point(3, 15);
			this->label1->Name = L"label1";
			this->label1->Size = System::Drawing::Size(109, 23);
			this->label1->TabIndex = 0;
			this->label1->Text = L"Port Number";
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
			this->button2->Text = L"�Է�";
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
			this->button3->Text = L"��Ʈ����";
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
			this->linkLabel1->Font = (gcnew System::Drawing::Font(L"���� ���", 9, System::Drawing::FontStyle::Regular, System::Drawing::GraphicsUnit::Point, 
				static_cast<System::Byte>(129)));
			this->linkLabel1->Location = System::Drawing::Point(12, 53);
			this->linkLabel1->Name = L"linkLabel1";
			this->linkLabel1->Size = System::Drawing::Size(55, 15);
			this->linkLabel1->TabIndex = 6;
			this->linkLabel1->TabStop = true;
			this->linkLabel1->Text = L"�����";
			this->linkLabel1->LinkClicked += gcnew System::Windows::Forms::LinkLabelLinkClickedEventHandler(this, &Form1::linkLabel1_LinkClicked);
			// 
			// Form1
			// 
			this->AutoScaleDimensions = System::Drawing::SizeF(7, 12);
			this->AutoScaleMode = System::Windows::Forms::AutoScaleMode::Font;
			this->AutoSize = true;
			this->AutoSizeMode = System::Windows::Forms::AutoSizeMode::GrowAndShrink;
			this->ClientSize = System::Drawing::Size(242, 155);
			this->Controls->Add(this->linkLabel1);
			this->Controls->Add(this->button3);
			this->Controls->Add(this->label2);
			this->Controls->Add(this->button2);
			this->Controls->Add(this->textBox1);
			this->Controls->Add(this->label1);
			this->Icon = (cli::safe_cast<System::Drawing::Icon^  >(resources->GetObject(L"$this.Icon")));
			this->Name = L"Form1";
			this->Text = L"Prezentainer";
			this->Load += gcnew System::EventHandler(this, &Form1::Form1_Load);
			this->ResumeLayout(false);
			this->PerformLayout();

		}

		
#pragma endregion


		String^  firstName;

		private: System::Void button2_Click(System::Object^  sender, System::EventArgs^  e) { //�ؽ�Ʈ�� �Է¹����� �����ư�� ������ �� ������.
					 firstName = textBox1->Text;
					 msclr::interop::marshal_context context;
					 std::string num = context.marshal_as<std::string>(firstName);  //edit_Text�� �Է¹��� ���ڸ� String^�� ������.


					 if(strcmp(num.c_str(),"")==0)                    //�Է����� �ʾ������
					 {
						 label2->Text = "Port��ȣ�� �Է����ּ���";
					 }
					 else                                             //�Է����� ���
					 {
						 if(isString(num.c_str()))      //���ڶ�� �ش���Ʈ Open��Ű�� �κ���.
						 {
							 strcat(name, num.c_str());
							 button2->Enabled = false;
							 button2->Text="������";
							 label2->Text = "Port :"+firstName+"����!";
							 boost::thread t(&run_program);     //Ui�� ���� �ʱ� ���� boost thread�� ������ run_program()�Լ��� �����Ŵ.
							 textBox1->Enabled = false;

						 }
						 else //���ڰ� �ƴϸ� �ٽ� �Է�!
						 {
							 label2->Text = "error! ���ڸ� �Է����ּ���!";
						 }
					 }

				 }

	     private: System::Void label1_Click(System::Object^  sender, System::EventArgs^  e) {
				 }

private: System::Void Form1_Load(System::Object^  sender, System::EventArgs^  e) {
		 }


private: System::Void button3_Click(System::Object^  sender, System::EventArgs^  e) {
			 WinExec("C:/Windows/System32/rundll32.exe shell32.dll,Control_RunDLL bthprops.cpl,,2",SW_SHOW); //������� port��ȣ�� Ȯ���� �� �ִ� process�� �����.
		 }
private: System::Void textBox1_MouseHover(System::Object^  sender, System::EventArgs^  e) {
			 toolTip1->SetToolTip(textBox1,"Enter only Port Number!");
		 }
private: System::Void textBox1_TextChanged(System::Object^  sender, System::EventArgs^  e) {
			 button2->Enabled = true;
		 }
private: System::Void linkLabel1_LinkClicked(System::Object^  sender, System::Windows::Forms::LinkLabelLinkClickedEventArgs^  e) {
			  WinExec("C:\\Program Files\\Internet Explorer\\IEXPLORE.EXE http://cyh1704.dothome.co.kr/ex/#pc",SW_SHOW);  //���� ��ư, ���� ��뼳���������� �̵���.

		 }
};
}


