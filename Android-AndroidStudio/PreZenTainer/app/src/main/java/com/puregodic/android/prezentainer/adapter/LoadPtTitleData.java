package com.puregodic.android.prezentainer.adapter;

import com.puregodic.android.prezentainer.calculator.HeartRateCalculator;

import java.util.ArrayList;

public class LoadPtTitleData {

    private String title;
    private String date;
    private ArrayList<Float> hbr;
    private String score;

    public void setDate(String date){
        this.date = date;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setHbr(ArrayList<Float> hbr){
        this.hbr = hbr;
    }

    public String getDate(){
        return date;
    }
    public String getTitle(){
        return title;
    }
    public String getScore(){

        score = new HeartRateCalculator(hbr).standardDeviation();
        return score;
    }

}
