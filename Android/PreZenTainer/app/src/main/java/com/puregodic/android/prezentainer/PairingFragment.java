package com.puregodic.android.prezentainer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.puregodic.android.prezentainer.bluetooth.BluetoothConfig;

import java.util.ArrayList;

/**
 * Created by quki on 2015-09-07.
 */
public class PairingFragment extends Fragment {

    int deviceCount;
    public View rootView;

    public PairingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_paired, container, false);
        return rootView;
    }

    // 새롭게 찾은 주변의 Device(블루투스 페어링이 가능한)를 Icon, device name, adress와 함께 배치함.
    public void setDeviceIconFound(BluetoothDevice device){

        String name = device.getName();
        if(name==null){
            name = "이름없음";
        }
        int type = device.getBluetoothClass().getMajorDeviceClass();
        Log.d("DeviceFound", "name : " + name + "\ntype : " + type);
        deviceCount++;

        switch(deviceCount){
            case 1 : {
                LinearLayout r = (LinearLayout)rootView.findViewById(R.id.layout1);
                TextView t = (TextView)rootView.findViewById(R.id.textView1);
                ImageView i = (ImageView)rootView.findViewById(R.id.imageView1);
                r.setTag(device);
                t.setText(name);
                setIconImage(i, type);
                setIconRisingAnimator(r);
                break;
            }
            case 2 : {
                LinearLayout r = (LinearLayout)rootView.findViewById(R.id.layout2);
                TextView t = (TextView)rootView.findViewById(R.id.textView2);
                ImageView i = (ImageView)rootView.findViewById(R.id.imageView2);
                r.setTag(device);
                t.setText(name);
                setIconImage(i, type);
                setIconRisingAnimator(r);
                break;
            }
            case 3 : {
                LinearLayout r = (LinearLayout)rootView.findViewById(R.id.layout3);
                TextView t = (TextView)rootView.findViewById(R.id.textView3);
                ImageView i = (ImageView)rootView.findViewById(R.id.imageView3);
                r.setTag(device);
                t.setText(name);
                setIconImage(i, type);
                setIconRisingAnimator(r);
                break;
            }
            case 4 : {
                LinearLayout r = (LinearLayout)rootView.findViewById(R.id.layout4);
                TextView t = (TextView)rootView.findViewById(R.id.textView4);
                ImageView i = (ImageView)rootView.findViewById(R.id.imageView4);
                r.setTag(device);
                t.setText(name);
                setIconImage(i, type);
                setIconRisingAnimator(r);
                break;
            }
            case 5 : {
                LinearLayout r = (LinearLayout)rootView.findViewById(R.id.layout5);
                TextView t = (TextView)rootView.findViewById(R.id.textView5);
                ImageView i = (ImageView)rootView.findViewById(R.id.imageView5);
                r.setTag(device);
                t.setText(name);
                setIconImage(i, type);
                setIconRisingAnimator(r);
                break;
            }
            case 6 : {
                LinearLayout r = (LinearLayout)rootView.findViewById(R.id.layout6);
                TextView t = (TextView)rootView.findViewById(R.id.textView6);
                ImageView i = (ImageView)rootView.findViewById(R.id.imageView6);
                r.setTag(device);
                t.setText(name);
                setIconImage(i, type);
                setIconRisingAnimator(r);
                break;
            }
            case 7 : {
                LinearLayout r = (LinearLayout)rootView.findViewById(R.id.layout7);
                TextView t = (TextView)rootView.findViewById(R.id.textView7);
                ImageView i = (ImageView)rootView.findViewById(R.id.imageView7);
                r.setTag(device);
                t.setText(name);
                setIconImage(i, type);
                setIconRisingAnimator(r);
                break;
            }
            case 8 : {
                LinearLayout r = (LinearLayout)rootView.findViewById(R.id.layout8);
                TextView t = (TextView)rootView.findViewById(R.id.textView8);
                ImageView i = (ImageView)rootView.findViewById(R.id.imageView8);
                r.setTag(device);
                t.setText(name);
                setIconImage(i, type);
                setIconRisingAnimator(r);
                break;
            }
            case 9 : {
                LinearLayout r = (LinearLayout)rootView.findViewById(R.id.layout9);
                TextView t = (TextView)rootView.findViewById(R.id.textView9);
                ImageView i = (ImageView)rootView.findViewById(R.id.imageView9);
                r.setTag(device);
                t.setText(name);
                setIconImage(i, type);
                setIconRisingAnimator(r);
                break;
            }
            case 10 : {
                LinearLayout r = (LinearLayout)rootView.findViewById(R.id.layout10);
                TextView t = (TextView)rootView.findViewById(R.id.textView10);
                ImageView i = (ImageView)rootView.findViewById(R.id.imageView10);
                r.setTag(device);
                t.setText(name);
                setIconImage(i, type);
                setIconRisingAnimator(r);
                deviceCount = 0;
                break;
            }
        }
    }

    // Icon이 떠오를 때 효과를 위한 애니메이션
    private void setIconRisingAnimator(LinearLayout r){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400); // 지연효과 0.4초
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList = new ArrayList<>();
        // X-Y방향으로 천천히 커지는 듯한 애니메이션
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(r, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(r, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        r.setVisibility(View.VISIBLE);
        animatorSet.start();
    }


    // Image를 device type에 따라 나누어 각각 다른 drawable image로 뿌려줌
    private void setIconImage(ImageView i , int type){

        switch(type){
            case BluetoothConfig.COMPUTER : {
                i.setImageResource(R.drawable.ic_pc);
                break;
            }
            case BluetoothConfig.PHONE:  {
                i.setImageResource(R.drawable.ic_phone);
                break;
            }
            case BluetoothConfig.WEARABLE : {
                i.setImageResource(R.drawable.ic_watch);
                break;
            }
            case BluetoothConfig.AUDIO_VIDEO : {
                i.setImageResource(R.drawable.ic_headphone);
                break;
            }
            case BluetoothConfig.UNCATEGORIZED : {
                i.setImageResource(R.drawable.ic_phone);
                break;
            }
            default:{
                i.setImageResource(R.drawable.ic_phone);
                break;
            }

        }
    }

}
