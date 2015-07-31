var mSAAgent,
    connectionListener,
    mSASocket,
    mSAFiletransfer,
    mSARemotePeerAgent,
    transferId = 0;
var mTimeInterval = 0;

//Initialize File Transfer 
function ftInit(successCb, errorCb) {
  if (mSAAgent == null) {
    errorCb({
        name : 'NetworkError',
        message : 'Connection failed'
    });
    return;
  }

  // file send event callback definition
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
// Send File by given path
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

// Cancel sending File
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

function cancelFile() {
  ftCancel(transferId, function() {
    console.log('Succeed to cancel file');
    showMain();
  }, function(err) {
    toastAlert('Failed to cancel File');
  });
}

// Define File Transfer Success Call Back
var ftSuccessCb = {
  onsendprogress: function(id, progress) {
    console.log('onprogress id : ' + id + ' progress : ' + progress);
    progressBarWidget.value(progress);
  },
  onsendcomplete: function(id, localPath) {
    progressBarWidget.value('100');
    showMain('send Completed!! id : ' + id + ' localPath :' + localPath);
  },
  onsenderror: function(errCode, id) {
    showMain('Failed to send File. id : ' + id + ' errorCode :' + errCode);
  }
};


function disconnectSAP(){
  if(mSASocket !== null){
    try {
      mSASocket.close();
      mSASocket = null;
      isConnect=false;
      updateContents();
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

//Service Connection Handler
connectionListener = {
        //Remote peer agent (Consumer) requests a service (Provider) connection
         onrequest: function (peerAgent) {
                 mSAAgent.acceptServiceConnectionRequest(peerAgent);
                 mSARemotePeerAgent = peerAgent;
                 toastAlert('ACCEPT!');
                 
                 // Initialize File transfer
                 ftInit(ftSuccessCb, function(err) {
                   toastAlert('Failed to initialize the File Transfer');
                 });
         },
         // Connection between Provider and Consumer is established
         onconnect: function (socket) {
             isConnect = true;
             updateContents();
             var onConnectionLost,
                 dataOnReceive;
             mSASocket = socket;
             console.log('SASocket is initialize');
             
             dataOnReceive = function dataOnReceive(channelId,data){
               
               mTimeInterval = Number(data);
               console.log('channel ID : '+channelId);
               console.log('data : '+ data);
             }
             
             mSASocket.setDataReceiveListener(dataOnReceive);
         },
         onerror: function (errorCode) {
           console.log(errorCode);
         }
};

//onServiceConnectionRequested Success Call Back
function onSAAgentRequested (agents) {
  var i = 0;
  console.log('===my SAAgent===');
  for (i; i < agents.length; i += 1) {
      if (agents[i].role === "PROVIDER") { 
          mSAAgent = agents[i]; // get the SAAgent 
          break;
      }
  }
  
  mSAAgent.setServiceConnectionListener(connectionListener);
}

//onServiceConnectionRequested Error Call Back
function onRequestedError (e) {
  console.log('requestOnError '+ e);
}

//Requested the SAAgent specified in the Accessory Service Profile
webapis.sa.requestSAAgent(onSAAgentRequested, onRequestedError);