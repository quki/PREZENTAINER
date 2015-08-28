package com.puregodic.android.prezentainer.service;

public interface ConnectionActionGear {
    
    void onFindingPeerAgent();
    
    void onFindingPeerAgentError();
    
    void onConnectionActionRequest();
    
    void onConnectionActionComplete();
    
    void onConnectionActionError();

}
