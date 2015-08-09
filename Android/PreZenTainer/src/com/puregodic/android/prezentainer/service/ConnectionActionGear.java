package com.puregodic.android.prezentainer.service;

public interface ConnectionActionGear {
    
    void onConnectionActionFindingPeerAgent();
    
    void onConnectionActionRequest();
    
    void onConnectionActionComplete();
    
    void onConnectionActionNoResponse();

}
