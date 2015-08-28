
package com.puregodic.android.prezentainer;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;



public class DetailSettingActivity extends AppCompatActivity {
    
    public static int incrementID = 0;
    public static ArrayList<String> timeInterval = new ArrayList<String>();
    public static ArrayList<EditText> editTextArr = new ArrayList<EditText>();
    public static final int REQUEST_DETAIL = 3;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_detailsetting);
       
       
       final EditText num = (EditText)findViewById(R.id.num);
       final LinearLayout list =(LinearLayout)findViewById(R.id.list);
       Button btn = (Button)findViewById(R.id.btn);
       Button btnResult = (Button)findViewById(R.id.btnResult);
       
       //�������� ���ϴ� ���� ��ŭ edit text�� �����ϴ� Ȯ�� ��ư
       btn.setOnClickListener(new Button.OnClickListener() {
          public void onClick(View v) {
             //������ ����
             timeInterval.clear();
             editTextArr.clear();
             list.removeAllViews();
             incrementID = 0;
             
             //edittext � ������ ������ �Է��� ���� ó��
             String inputNumString = num.getText().toString();
             final int inputNum = Integer.valueOf(inputNumString);
             
             //���������̼� ���� �ʹ� ������ �ٽ� ����
             if (inputNum >= 100) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(DetailSettingActivity.this);
                alert_confirm.setMessage("�Է��Ͻ� ������ �½��ϱ�?").setCancelable(false).setPositiveButton("Ȯ��",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'YES' -> �Է��Ѵ�� EditText ��������
                       for (int i = 0; i < inputNum; i++) {
                         incrementID++;
                         final EditText numAdd = new EditText(DetailSettingActivity.this); 
                         
                         //edit text ����
                         numAdd.setHint(incrementID + "��° �����̵�");
                         numAdd.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                         numAdd.setPadding(50, 10, 50, 10);
                         numAdd.setBackgroundColor(Color.YELLOW);
                         numAdd.setId(incrementID);
                         numAdd.setInputType(0x00000002); //����Ű�е常 �ߵ���
                         
                         //edit text �信 �ø��� �޼ҵ�
                         list.addView(numAdd, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                         //list.addView(numAdd, i);
                         
                         editTextArr.add(numAdd);
                      }
                       
                    }
                }).setNegativeButton("���Է�",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'No' -> ���Է�
                       
                    return;
                    }
                });
                AlertDialog alert = alert_confirm.create();
                alert.show();
             }
             else {
                //edit Text �������� (100���� ���� �ʴ� ���)   
                for (int i = 0; i < inputNum; i++) {
                   incrementID++;
                   final EditText numAdd = new EditText(DetailSettingActivity.this); 
                   
                   //edit text ����
                   numAdd.setHint(incrementID + "��° �����̵�");
                   numAdd.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                   numAdd.setPadding(50, 10, 50, 10);
                   numAdd.setBackgroundColor(Color.YELLOW);
                   numAdd.setId(incrementID);
                   numAdd.setInputType(0x00000002); //����Ű�е常 �ߵ���
                   
                   //edit text �信 �ø��� �޼ҵ�
                   list.addView(numAdd, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                   //list.addView(numAdd, i);
                   
                   editTextArr.add(numAdd);
                }
             }
          
          }   
       });
       
       
       //���� ������ time interval���� �Է��ϴ� ����Ȯ�� ��ư
        btnResult.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                for (int i = 0; i < editTextArr.size(); i++) {
                    // ����ڰ� �Է��� �����̵�ð� timeInteval�� �Է�
                    timeInterval.add(editTextArr.get(i).getText().toString());

                    Log.i("timeInterval", "tI size : " + timeInterval.size() + "  tIvalue : "
                            + timeInterval.get(i));

                }

                //Intent intent = new Intent(DetailSettingActivity.this, SettingActivity.class);
                Intent intent = new Intent();
                intent.putStringArrayListExtra("timeInterval", timeInterval);
                setResult(REQUEST_DETAIL, intent);
                finish();
            }
        });
       
    }
    
}


