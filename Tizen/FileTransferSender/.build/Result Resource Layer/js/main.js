/*
 * Copyright (c) 2014 Samsung Electronics Co., Ltd. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of Samsung Electronics Co., Ltd.
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

var progressBarWidget,
    progressBar = document.getElementById("circleprogress");
var CHANNELID_EVENT = 104,
    CHANNELID_HR = 110;
var SAAgent,
    connectionListener,
    SASocket,
    SAFiletransfer,
    SAPeerAgent,
    transferId = 0;

// Heart Rate
var hrm = window.webapis.motion.start("HRM", function onSuccess(hrmInfo) {
  if (hrmInfo.heartRate > 0) HR = hrmInfo.heartRate;
});

function HR_send() {

  try {
    //sapRequest(HR, CHANNELID_HR);
    SASocket.sendData(CHANNELID_HR,HR);
  } catch (err) {
    console.log("exception [" + err.name + "] msg[" + err.message + "]");
  }
}
// Start HR per 5 sec
function startHR() {
  setinterval = setInterval(HR_send, 5000);
}
// Stop HR
function stopHR() {
  clearInterval(setinterval);
  window.webapis.motion.stop("HRM");
}

// Alert message with toast popup of TAU
function toastAlert(msg) {
  var toastMsg = document.getElementById("popupToastMsg");
  toastMsg.innerHTML = msg;
  tau.openPopup('#popupToast');
  console.log(msg);
}

function showMain(message) {
  tau.changePage('#main');
  if (message != undefined) {
    toastAlert(message);
  }
  transferId = 0;
}
// File Transfer success Call back
var ftSuccessCb = {
  onsuccess: function() {
    toastAlert('Succeed to connect');
    updateContents();
  },
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
// clearList
function clearList(reconnect) {
  console.log('clear List');
  $('.ui-listview').empty();
  if (reconnect) {
    $('.ui-listview').append('<li>다시 Connect하세요</a></li>');
  } else {
    $('.ui-listview').append('<li>BT Disconnected. Connection waiting...</li>');
  }
  var snaplistEl = document.getElementsByClassName('ui-snap-listview')[0];
  var snaplistWidget = tau.widget.SnapListview(snaplistEl);
  snaplistWidget.refresh();
}

function reconnect() {
  $('.ui-listview').empty();
  sapFindPeer(function() {
    console.log('Succeed to find peer');
    ftInit(ftSuccessCb, function(err) {
      toastAlert('Failed to get File Transfer');
      clearList(true);
    });
  }, function(err) {
    toastAlert('Failed to reconnect to service');
    clearList(true);
  });

}
// 넘겼을때 화면구성
function updateContents() {
  $('.ui-listview').empty();
  $('.ui-listview').append('<li>프레젠테이션 시작</li>');
}



/*// Initialize
function initialize() {

  // SAP Initialize success call back
  var sapinitsuccesscb = {
    onsuccess: function() {
      console.log('Succeed to connect');
      ftInit(ftSuccessCb, function(err) {
        toastAlert('Failed to get File Transfer');
      });
    },
    // Device connection status
    ondevicestatus: function(status) {
      if (status == "DETACHED") {
        console.log('Detached remote peer device');
        clearList();
      } else if (status == "ATTACHED") {
        console.log('Attached remote peer device');
        reconnect();
      }
    }
  };

  // SAP Initialize
  sapInit(sapinitsuccesscb, function(err) {
    toastAlert('Failed to SAP Initialize');
  });
}*/

function mcancelFile() {
  ftCancel(transferId, function() {
    console.log('Succeed to cancel file');
    showMain();
  }, function(err) {
    toastAlert('Failed to cancel File');
  });
}
// Event btn clicked
function eventtopc() {
  try {
    SASocket.sendData(CHANNELID_EVENT, "Hello Android");
    //sapRequest("EVENT", CHANNELID_EVENT);
  } catch (err) {
    console.log("exception [" + err.name + "] msg[" + err.message + "]");
  }
}

function backkeyhandler(e) {

  /* For the flick down gesture */
  if (e.keyName == "back") {
    var page = document.getElementsByClassName('ui-page-active')[0], pageid = page
            ? page.id : " ";
    if (pageid === "main") {
      /*
       * When a user flicks down, the application exits
       */
      tizen.application.getCurrentApplication().exit();
    } else {
      mcancelFile();
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
  var toastPopup = document.getElementById('popupToast');
  toastPopup.addEventListener('popupshow', function(ev) {
    setTimeout(function() {
      tau.closePopup();
    }, 3000);
  }, false);
})(window.tau);

///////////////////////////////////////////////////////////

connectionListener = {
        /* Remote peer agent (Consumer) requests a service (Provider) connection */
        onrequest: function (peerAgent) {
          console.log(peerAgent);
            /* Authentication of requesting peer agent */
               /* if (peerAgent.appName === "FileTransferReceiver") {
                    SAAgent.acceptServiceConnectionRequest(peerAgent);
                    console.log('====accept !!====');

                } else {
                    SAAgent.rejectServiceConnectionRequest(peerAgent);
                    console.log('====reject !!===');
            }*/
                SAAgent.acceptServiceConnectionRequest(peerAgent);
                SAPeerAgent = peerAgent;
                console.log('====accept !!====');
                toastAlert('ACCEPT!');
                
                ftInit(ftSuccessCb, function(err) {
                  toastAlert('Failed to get File Transfer Class');
                });
                
                
                
        },

        /* Connection between Provider and Consumer is established */
        onconnect: function (socket) {
          
            var onConnectionLost,
                dataOnReceive;
           
            SASocket = socket;
            console.log('====SASocket is initialize====');
            toastAlert('SOCKET INITIALIZED!');
        },
        onerror: function (errorCode) {
          console.log(errorCode);
        }
    };


//request 

function requestOnSuccess (agents) {
  var i = 0;
  console.log('===requestOnSuccess===');
  for (i; i < agents.length; i += 1) {
      if (agents[i].role === "PROVIDER") { //??????????????????????????????????????????????????
        console.log('agents role is PROVIDER');
          SAAgent = agents[i];
          break;
      }
  }
  /* Set listener for upcoming connection from Consumer */
  SAAgent.setServiceConnectionListener(connectionListener);
};

function requestOnError (e) {
  console.log('requestOnError');
};

/* Requests the SAAgent specified in the Accessory Service Profile */
webapis.sa.requestSAAgent(requestOnSuccess, requestOnError);






//File Transfer Initialize
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
    successCb.onsuccess();
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

function ftSend(path, successCb, errorCb) {
  if (SAAgent == null || SAFiletransfer == null || SAPeerAgent == null) {
    errorCb({
      name : 'NotConnectedError',
        message : 'SAP is not connected'
    });
    return;
  }
  
  try {
    //재귀 호출
    transferId = SAFiletransfer.sendFile(SAPeerAgent, path);
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

function ftCancel(id, successCb, errorCb) {
  if (SAAgent == null || SAFiletransfer == null || SAPeerAgent == null) {
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





