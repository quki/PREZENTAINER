package com.puregodic.android.prezentainer.adapter;

public class LoadPtTitleData {

    String ptTitle;
    String currentTime;
    
    
    public LoadPtTitleData(String ptTitle, String currentTime){
        this.ptTitle = ptTitle;
        this.currentTime = currentTime;
    }
    
    public String currentTime(){
        
        return currentTime; 
    }
        
    public String getPtTitle(){
        
        return ptTitle; 
    }

}
