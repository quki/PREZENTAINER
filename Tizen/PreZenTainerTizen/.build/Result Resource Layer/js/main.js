var progressBarWidget,
    progressBar = document.getElementById("circleprogress");
var CHANNELID_EVENT = 104,
    CHANNELID_HR = 110;

var isConnect =false;


// Event btn clicked
function eventtopc() {
  try {
    mSASocket.sendData(CHANNELID_EVENT, "Hello Android");
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



// Heart Rate
function sendHR() {
  try {
    mSASocket.sendData(CHANNELID_HR,HR);
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
  }else{
    $('.ui-listview').empty();
    $('.ui-listview').append('<li>연결하세요</li>');
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
    $('.ui-listview').append('<li>연결하세요</li>');
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