/*
 * 
 * SAP/SA File Transfer 관련모듈
 * 
 * */

    var CHANNELID_SETTING = 100,
    CHANNELID_EVENT = 104, 
    CHANNELID_HR = 110,
    CHANNELID_EVENTTIME = 114;

var mSAAgent,
    connectionListener,
    mSASocket,
    mSAFiletransfer,
    mSARemotePeerAgent,
    transferId = 0;
 
var mTimeInterval = null;


/* ----------------------------
    SA File Transfer 관련 함수
-----------------------------*/

// Initialize File Transfer 
function ftInit(successCb, errorCb) {
  if (mSAAgent == null) {
    errorCb({
        name : 'NetworkError',
        message : 'Connection failed'
    });
    return;
  }

  // 파일 전송 event Listener의 call back함수 정의 
  var filesendcallback = {
    onprogress : successCb.onsendprogress,
    oncomplete : successCb.onsendcomplete,
    onerror : successCb.onsenderror
  };
  
  try {
    //Get the SAFileTransfer From SAAgent
    mSAFiletransfer = mSAAgent.getSAFileTransfer();
    mSAFiletransfer.setFileSendListener(filesendcallback);
  } catch (err) {
    console.log('getSAFileTransfer exception <' + err.name + '> : ' + err.message);
    window.setTimeout(function() {
      errorCb({
          name : 'NetworkError',
          message : 'Connection failed'
      });
    }, 0);
  }
}
// 파일 전송
function ftSend(path, successCb, errorCb) {
  if (mSAAgent === null || mSAFiletransfer === null || mSARemotePeerAgent === null) {
    errorCb({
      name : 'NotConnectedError',
        message : 'SAP is not connected'
    });
    return;
  }
  
  try {
    //재귀 호출
    transferId = mSAFiletransfer.sendFile(mSARemotePeerAgent, path);
    successCb(transferId);
  } catch (err) {
    console.log('sendFile exception <' + err.name + '> : ' + err.message);
    window.setTimeout(function() {
      errorCb({
          name : 'RequestFailedError',
          message : 'send request failed'
      });
    }, 0);
  }
}

// 파일 전송 취소
function ftCancel(id, successCb, errorCb) {
  if (mSAAgent === null || mSAFiletransfer === null || mSARemotePeerAgent === null) {
    errorCb({
      name : 'NotConnectedError',
        message : 'SAP is not connected'
    });
    return;
  }

  try {
    mSAFiletransfer.cancelFile(id);
    successCb();
  } catch (err) {
    console.log('cancelFile exception <' + err.name + '> : ' + err.message);
    window.setTimeout(function() {
      errorCb({
          name : 'RequestFailedError',
          message : 'cancel request failed'
      });
    }, 0);
  }
  
}
// 파일 전송 취소
function cancelFile() {
  ftCancel(transferId, function() {
    console.log('Succeed to cancel file');
    showMain();
  }, function(err) {
    toastAlert('Failed to cancel File');
  });
}

// File Transfer Success Call Back 정의
var ftSuccessCb = {
  onsendprogress: function(id, progress) {
    console.log('onprogress id : ' + id + ' progress : ' + progress);
    progressBarWidget.value(progress);
  },
  onsendcomplete: function(id, localPath) {
    progressBarWidget.value('100');
    showMain('SEND COMPLETED');
  },
  onsenderror: function(errCode, id) {
    showMain('Failed to send File. id : ' + id + ' errorCode :' + errCode);
  }
};
/* ----------------------------
  Samsung Accessory Protocol 관련 함수
-----------------------------*/

function disconnectSAP(){
  if(mSASocket !== null){
    try {
      mSASocket.close();
      mSASocket = null;
      isConnect=false;
      updateConnection();
      console.log('Success to close the socket');
    } catch (e) {
      console.error(e+' Cannot close the socket');
    }
  }
}


/**
 * Initialize SAAgent,
 * Accept requested Service Connection,
 * Initailize File Transfer,
 * Initialize Socket
 */

// Service Connection Handler
connectionListener = {
        //Remote peer agent (Consumer, Android)가 Service Connection을 요청했을 때
         onrequest: function (peerAgent) {
                 
                 mSAAgent.acceptServiceConnectionRequest(peerAgent);  // Service Connection 허락
                 mSARemotePeerAgent = peerAgent;  // PeerAgent(Android)로 초기화
                 toastAlert('CONNECTED');
                 
                 // Initialize File transfer
                 ftInit(ftSuccessCb, function(err) {
                   toastAlert('Failed to initialize the File Transfer');
                 });
         },
         // Service Connection이 연결되었을 때, socket생성
         onconnect: function (socket) {
             isConnect = true;
             updateConnection(); // viewhelper.js 참조
             var onConnectionLost,
                 dataOnReceive;
             
             // socket 초기화
             mSASocket = socket;
             console.log('SASocket is initialized');
             
             // 연결이 끊겼을 때
             onConnectionLost = function onConnectionLost (reason) {
               isConnect = false;
               updateConnection();
               console.error("Service Connection disconnected due to following reason: " + reason);
             };
             mSASocket.setSocketStatusListener(onConnectionLost);
             
             // Data를 받을 때
             dataOnReceive = function dataOnReceive(channelId,data){
            	 if(channelId == CHANNELID_SETTING)
            	 {
            		 //json으로 진동간격 입력받기
            		 //mTimeInterval = JSON.stringify(data);
            		 mTimeInterval = JSON.parse(data);  
            		 for (var i = 0; i < mTimeInterval.length; i++) {
            			 console.log('data : '+ mTimeInterval[i]);
            		 }
            		 updateAfterOnReceivce();
            	 }
            	 else //unlock 하기 위한 통신 부분 , 채널ID=104 일 때를 뜻함!
            	 {
            		 motion_check=0;
            		 console.log(channelId);

            	 }
             }
             mSASocket.setDataReceiveListener(dataOnReceive);
         },
         onerror: function (errorCode) {
           console.log(errorCode);
         }
};
// SAAgent객체 생성, 역할은 provider
function onSAAgentRequested (agents) {
  var i = 0;
  console.log('Enable to use SAAgent');
  for (i; i < agents.length; i += 1) {
      if (agents[i].role === "PROVIDER") { 
          mSAAgent = agents[i]; // get the SAAgent 
          break;
      }
  }
  
  mSAAgent.setServiceConnectionListener(connectionListener);
}
// SAAgent객체생성 실패
function onRequestedError (e) {
  console.log('requestOnError '+ e);
}

// Accessory Service Profile을 이용하기 위한 SAAgent를 요청함. 
webapis.sa.requestSAAgent(onSAAgentRequested, onRequestedError);