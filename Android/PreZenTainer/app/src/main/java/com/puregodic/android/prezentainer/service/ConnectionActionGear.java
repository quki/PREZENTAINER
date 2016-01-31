package com.puregodic.android.prezentainer.service;

public interface ConnectionActionGear {

    void onErrorSAPFramework(Boolean isError);

    void onFindingPeerAgent();
    
    void onFindingPeerAgentError();
    
    void onConnectionActionRequest();
    
    void onConnectionActionComplete();
    
    void onConnectionActionError();

}
