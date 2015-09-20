var CHANNELID_SETTING = 100,
	CHANNELID_EVENT = 104,
    CHANNELID_HR = 110,
    CHANNELID_EVENTTIME = 114;


var isConnect = false;
var heartRateArray ;

//makeJsonEventTime 함수참조 포인터 만들기
var p_pushEventTimeToArray;

// Event btn clicked
function eventtopc(direction) {       
  try {
	if(direction === "right") //오른쪽 이벤트 발생시!
	{
	  p_pushEventTimeToArray(direction); // event time을 "Right" Array에 Push
		mSASocket.sendData(CHANNELID_EVENT,direction);
		console.log("sendData(RIGHT)");
	}
	else if(direction === "leftt")//왼쪽 이벤트 발생시!
	{
	  p_pushEventTimeToArray(direction); // event time을 "Left" Array에 Push
		mSASocket.sendData(CHANNELID_EVENT, direction);
		console.log("sendData(LEFT)");
	}

  } catch (err) {
    console.log("exception [" + err.name + "] msg[" + err.message + "]");
  }
}

// 심박수를 JSON으로 만들기
function makeJsonHR(){
  
  try {
      heartRateArray.push(HR);
      console.log(heartRateArray);
  } catch (err) {
    console.log("exception [" + err.name + "] msg[" + err.message + "]");
  }
    
}

// 심박수 JSON을 ANDROID로 보내기
function sendJsonHR(){
  
  try {
      jsonInfoHR = JSON.stringify(heartRateArray);
      console.log(jsonInfoHR);
      mSASocket.sendData(CHANNELID_HR, jsonInfoHR);
      console.log("Heart Rate sent : " + jsonInfoHR);
      
  } catch (err) {
    console.log("exception [" + err.name + "] msg[" + err.message + "]");
  }
  
}

// Start to measuring Heart Rate and making the data to JSON per 5 sec
function startHR() {
  heartRateArray = new Array();
  window.webapis.motion.start("HRM", function onSuccess(hrmInfo) {
    if (hrmInfo.heartRate > 0) 
      HR = hrmInfo.heartRate;
  });
  //sendingInterval = setInterval(sendHR, 5000);
  sendingInterval = setInterval(makeJsonHR, 5000);
}
// Stop HR
function stopHR() {
  window.webapis.motion.stop("HRM");
  clearInterval(sendingInterval);
  heartRateArray = null;
  console.log("heartRateArray Initialize : " + heartRateArray);
  console.log('HR Stop !');
}

// On Timer for 2 sec
function vibrator(){
  navigator.vibrate(2000);
}

// Start Timer by given interval time
function startTimer(){
  try {
	//코드수정
    if(mTimeInterval.length !== 0){
    	//일정한 진동간격 일 때
    	if (mTimeInterval.length == 1) {
    		vibratingInterval = setInterval(vibrator, mTimeInterval[0]*1000*60);
    	}
    	console.log('Timer Start ! Time Interval : '+ mTimeInterval[0] );
    	
    //
    }else{
      console.log('Timer Off !');
    }
  } catch (e) {
    console.error("Timer Error : "+e);
  }
}
// Stop Timer
function stopTimer(){
  if(mTimeInterval.length !== 0){
	//일정간격 초기화
    clearInterval(vibratingInterval);
    //코드수정
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