
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
// On Timer for 2 sec
function vibrator(){
  navigator.vibrate(2000);
}

// Start Timer by given interval time
function startTimer(){
  try {
    if(mTimeInterval !== 0){
      min = mTimeInterval*60*1000;
      vibratingInterval = setInterval(vibrator, min);
    }else{
      console.log('Timer Off !');
    }
    
  } catch (e) {
    console.error("Timer Error : "+e);
  }
}
// Stop Timer
function stopTimer(){
  if(mTimeInterval !== 0){
    clearInterval(vibratingInterval);
    console.log('Timer Stop !');
  }else{
    console.log('Timer Already Off !');
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