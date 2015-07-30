var progressBarWidget,
    progressBar = document.getElementById("circleprogress");
var CHANNELID_EVENT = 104,
    CHANNELID_HR = 110;
var SAAgent,
    connectionListener,
    SASocket,
    SAFiletransfer,
    SARemotePeerAgent,
    transferId = 0;
var isConnect =false;


// Event btn clicked
function eventtopc() {
  try {
    SASocket.sendData(CHANNELID_EVENT, "Hello Android");
  } catch (err) {
    console.log("exception [" + err.name + "] msg[" + err.message + "]");
  }
}


// Popup toast of TAU
function toastAlert(msg) {
  var toastMsg = document.getElementById("popupToastMsg");
  toastMsg.innerHTML = msg;
  tau.openPopup('#popupToast');
  console.log(msg);
}
// Show popup toast at 'main'
function showMain(message) {
  tau.changePage('#main');
  if (message != undefined) {
    toastAlert(message);
  }
  transferId = 0;
}

// Initialize File Transfer 
function ftInit(successCb, errorCb) {
  if (SAAgent == null) {
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
    SAFiletransfer = SAAgent.getSAFileTransfer();
    SAFiletransfer.setFileSendListener(filesendcallback);
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
  if (SAAgent === null || SAFiletransfer === null || SARemotePeerAgent === null) {
    errorCb({
      name : 'NotConnectedError',
        message : 'SAP is not connected'
    });
    return;
  }
  
  try {
    //재귀 호출
    transferId = SAFiletransfer.sendFile(SARemotePeerAgent, path);
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
  if (SAAgent === null || SAFiletransfer === null || SARemotePeerAgent === null) {
    errorCb({
      name : 'NotConnectedError',
        message : 'SAP is not connected'
    });
    return;
  }

  try {
    SAFiletransfer.cancelFile(id);
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
           console.log('====RemotepeerAgent : '+peerAgent);
                 SAAgent.acceptServiceConnectionRequest(peerAgent);
                 SARemotePeerAgent = peerAgent;
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
             SASocket = socket;
             console.log('====SASocket is initialize====');
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
    console.log('agents role is '+ agents[i].role);
      if (agents[i].role === "PROVIDER") { 
          SAAgent = agents[i]; // SAAgent found
          break;
      }
  }
  
  SAAgent.setServiceConnectionListener(connectionListener);
}

//onServiceConnectionRequested Error Call Back
function onRequestedError (e) {
  console.log('requestOnError '+ e);
}

//Requested the SAAgent specified in the Accessory Service Profile
webapis.sa.requestSAAgent(onSAAgentRequested, onRequestedError);

// Heart Rate
function sendHR() {
  try {
    SASocket.sendData(CHANNELID_HR,HR);
    console.log("Heart Rate sent : "+HR);
  } catch (err) {
    console.log("exception [" + err.name + "] msg[" + err.message + "]");
  }
}
// Start to measuring Heart Rate and sending the data per 5 sec
function startHR() {
  
  window.webapis.motion.start("HRM", function onSuccess(hrmInfo) {
    if (hrmInfo.heartRate > 0) 
      HR = hrmInfo.heartRate;
  });
  sendingInterval = setInterval(sendHR, 5000);
}
// Stop HR
function stopHR() {
  window.webapis.motion.stop("HRM");
  clearInterval(sendingInterval);
}
// 화면구성변화
function updateContents() {
  if(isConnect){
    $('.ui-listview').empty();
    $('.ui-listview').append('<li>연결됨</li>');
  }
}

// Handler for flick down gesture
function backkeyhandler(e) {
  if (e.keyName == "back") {
    var page = document.getElementsByClassName('ui-page-active')[0], 
                  pageid = page? page.id : " ";
    if (pageid === "main") {
      tizen.application.getCurrentApplication().exit();
    } else {
      window.history.back();
    }
  }
}

(function() {
  // tau progress bar implementation
  var sendPage = document.getElementById('sendPage');
  sendPage.addEventListener('pagehide', function() {
    progressBarWidget.destroy();
  });

  window.addEventListener('tizenhwkey', backkeyhandler);
  window.addEventListener('load', function(ev) {
    $('.ui-listview').append('<li>Connect하세요</li>');
  });
}());
(function(tau) {
  // tau popup toast implementation
  var toastPopup = document.getElementById('popupToast');
  toastPopup.addEventListener('popupshow', function(ev) {
    setTimeout(function() {
      tau.closePopup();
    }, 2000);
  }, false);
})(window.tau);