package com.puregodic.android.prezentainer;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.lang.reflect.Field;

public class AlarmActivity extends AppCompatActivity {

    NumberPicker minutePicker, secondPicker;
    TextView settingAlarm;
    Intent returnAlarmTimeIntent;
    public static final int REQUEST_ALARM = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // Time Picker
        minutePicker = (NumberPicker) findViewById(R.id.minutePicker);
        secondPicker = (NumberPicker) findViewById(R.id.secondPicker);
        settingAlarm = (TextView) findViewById(R.id.settingAlarm);

        // Picker 설정 부분 (Max,Min설정,글자색 white설정)
        minutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        secondPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minutePicker.setMaxValue(60);
        minutePicker.setMinValue(0);
        secondPicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        setNumberPickerTextColor(minutePicker, ContextCompat.getColor(this, R.color.white));
        setNumberPickerTextColor(secondPicker, ContextCompat.getColor(this, R.color.white));

        settingAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // SettingFragment로 알람의 분,초 값 각각 전달
                int min = minutePicker.getValue();
                int sec = secondPicker.getValue();

                returnAlarmTimeIntent = new Intent();
                returnAlarmTimeIntent.putExtra("min", min).putExtra("sec", sec);
                setResult(REQUEST_ALARM, returnAlarmTimeIntent);
                finish();
            }
        });


    }

    // Picker의 글자색 설정 함수
    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                    Log.w("PickerTextColor", e);
                } catch (IllegalAccessException e) {
                    Log.w("PickerTextColor", e);
                } catch (IllegalArgumentException e) {
                    Log.w("PickerTextColor", e);
                }
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        returnAlarmTimeIntent = new Intent();
        returnAlarmTimeIntent.putExtra("min", 0).putExtra("sec", 0);
        setResult(REQUEST_ALARM, returnAlarmTimeIntent);
        finish();
    }

}




