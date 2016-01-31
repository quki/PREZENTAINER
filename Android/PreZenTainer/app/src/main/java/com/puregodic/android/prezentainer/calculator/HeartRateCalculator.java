package com.puregodic.android.prezentainer.calculator;

import java.util.ArrayList;

/**
 * Created by darkstars77 on 2015-09-09.
 */
public class HeartRateCalculator {

    private ArrayList<Float> heartRateList;

    public HeartRateCalculator(ArrayList<Float> heartRateList){
        this.heartRateList=heartRateList;
    }

    // 최고 심박수 값 구하기
    public String HighHeartRateValue() { //배열의 값을 모두 검사해 최고값을 찾는 역할을 함.
        float  HighHeartRateValue= 0;
        String result = null;
        HighHeartRateValue=heartRateList.get(0);
        for (int i = 1; i < heartRateList.size(); i++) {
            if(HighHeartRateValue < heartRateList.get(i))
            {
                HighHeartRateValue=heartRateList.get(i);
            }
        }
        result = Integer.toString((int)(HighHeartRateValue));
        return result;
    }

    // 최저 심박수 값 구하기
    public String LowHeartRateValue() { //배열의 값을 모두 검사해 최저값을 찾는 역할을 함.
        float  LowHeartRateValue= 0;
        String result = null;
        LowHeartRateValue=heartRateList.get(0);
        for (int i = 1; i < heartRateList.size(); i++) {
            if(LowHeartRateValue > heartRateList.get(i))
            {
                LowHeartRateValue=heartRateList.get(i);
            }
        }
        result = Integer.toString((int)(LowHeartRateValue));
        return result;
    }




    // 총 심박수의 평균 값 구하기
    public int meanHeartRateValue() {
        float heartRateSum = 0;
        String meanHeartRate = null;
        for (int i = 0; i < heartRateList.size(); i++) {
            heartRateSum += heartRateList.get(i);
        }
        return ((int)(heartRateSum / heartRateList.size()));
    }

    //표준편차를 이용한 프레젠테이션 점수
    public String standardDeviation() {
        int offset = 3;
        float sum=0;
        double standardDeviationValue=0;
        String result=null;
        int average=meanHeartRateValue();

        for (int i = 0; i < heartRateList.size(); i++ )
        {
            sum += (heartRateList.get(i) - average) * (heartRateList.get(i) - average); //편차의 제곱의 합을 구함.
        }


        standardDeviationValue = Math.sqrt((double)sum/heartRateList.size());  //편차의 제곱의 합값에 루트연산을 통해 표준편차를 구함.

        result = Integer.valueOf(100-(int)standardDeviationValue*offset).toString(); //100에서 표준편차의 값을 빼서 프레젠테이션의 점수를 도출해냄.
        return result;
    }

    // 현재 시간에 해당하는 심박수 값 구하기
    public String currentHeartRateValue(int time) {
        int heartRateValueToInt = 0;
        String heartRateValueToString = null;

        if (time / (1000 * 5) > heartRateList.size() - 1) {
            heartRateValueToInt =(int)((float)heartRateList.get(heartRateList.size() - 1));
            heartRateValueToString = Integer.toString(heartRateValueToInt);
            return heartRateValueToString;
        }else {
            heartRateValueToInt = (int)((float)heartRateList.get(time / (1000 * 5)));
            heartRateValueToString = Integer.toString(heartRateValueToInt);
        }

        return heartRateValueToString;
    }
}
