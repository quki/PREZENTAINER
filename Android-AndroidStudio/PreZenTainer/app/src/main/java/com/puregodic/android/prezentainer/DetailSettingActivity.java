
package com.puregodic.android.prezentainer;

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

import java.util.ArrayList;



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
       
       //동적으로 원하는 갯수 만큼 edit text를 생성하는 확인 버튼
       btn.setOnClickListener(new Button.OnClickListener() {
          public void onClick(View v) {
             //이전값 삭제
             timeInterval.clear();
             editTextArr.clear();
             list.removeAllViews();
             incrementID = 0;
             
             //edittext 몇개 생성할 것인지 입력한 숫자 처리
             String inputNumString = num.getText().toString();
             final int inputNum = Integer.valueOf(inputNumString);
             
             //프레젠테이션 수가 너무 많으면 다시 물음
             if (inputNum >= 100) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(DetailSettingActivity.this);
                alert_confirm.setMessage("입력하신 갯수가 맞습니까?").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'YES' -> 입력한대로 EditText 동적생성
                       for (int i = 0; i < inputNum; i++) {
                         incrementID++;
                         final EditText numAdd = new EditText(DetailSettingActivity.this); 
                         
                         //edit text 설정
                         numAdd.setHint(incrementID + "번째 슬라이드");
                         numAdd.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                         numAdd.setPadding(50, 10, 50, 10);
                         numAdd.setBackgroundColor(Color.YELLOW);
                         numAdd.setId(incrementID);
                         numAdd.setInputType(0x00000002); //숫자키패드만 뜨도록
                         
                         //edit text 뷰에 올리는 메소드
                         list.addView(numAdd, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                         //list.addView(numAdd, i);
                         
                         editTextArr.add(numAdd);
                      }
                       
                    }
                }).setNegativeButton("재입력",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'No' -> 재입력
                       
                    return;
                    }
                });
                AlertDialog alert = alert_confirm.create();
                alert.show();
             }
             else {
                //edit Text 동적생성 (100개를 넘지 않는 경우)   
                for (int i = 0; i < inputNum; i++) {
                   incrementID++;
                   final EditText numAdd = new EditText(DetailSettingActivity.this); 
                   
                   //edit text 설정
                   numAdd.setHint(incrementID + "번째 슬라이드");
                   numAdd.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                   numAdd.setPadding(50, 10, 50, 10);
                   numAdd.setBackgroundColor(Color.YELLOW);
                   numAdd.setId(incrementID);
                   numAdd.setInputType(0x00000002); //숫자키패드만 뜨도록
                   
                   //edit text 뷰에 올리는 메소드
                   list.addView(numAdd, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                   //list.addView(numAdd, i);
                   
                   editTextArr.add(numAdd);
                }
             }
          
          }   
       });
       
       
       //최종 결정된 time interval들을 입력하는 최종확인 버튼
        btnResult.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                for (int i = 0; i < editTextArr.size(); i++) {
                    // 사용자가 입력한 슬라이드시간 timeInteval에 입력
                    timeInterval.add(editTextArr.get(i).getText().toString());

                    Log.i("timeInterval", "tI size : " + timeInterval.size() + "  tIvalue : "
                            + timeInterval.get(i));

                }

                Intent intent = new Intent();
                intent.putStringArrayListExtra("timeInterval", timeInterval);
                setResult(REQUEST_DETAIL, intent);
                finish();
            }
        });
       
    }
    
}


