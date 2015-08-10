
var CHANNELID_EVENT = 104,
    CHANNELID_HR = 110,
    CHANNELID_EVENTTIME = 114;

var isConnect = false;
var heartRateArray ;

//전역변수 추가
var totalTime = 0; //슬라이드 개별설정 처리해주기 위한 전역변수
var currentSlide = 0 ; //현재 슬라이드 위치
var vibratingIntervalArr = []; //슬라이드 개별설정 저장하는 배열
//

// Event btn clicked
function eventtopc() {
  try {
	//코드수정
	//슬라이드 개별설정 처리해주는 부분(2번째 슬라이드부터)
	if (mTimeInterval.length !== 0 && mTimeInterval.length > 1 && currentSlide < mTimeInterval.length) {
		vibratingIntervalArr.push(setTimeout(vibrator, mTimeInterval[currentSlide]*1000));
		//이전 슬라이드 타이머설정 제거
		clearTimeout(vibratingIntervalArr[currentSlide-1]);
	}
	++currentSlide; //슬라이드 +1
	//  
    mSASocket.sendData(CHANNELID_EVENT, "Current Slide : " + currentSlide);
    console.log('Event to PC !' + currentSlide);
    
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
    		vibratingInterval = setInterval(vibrator, mTimeInterval[0]*1000);
    	}
    	console.log('Timer Start ! Time Interval : '+ mTimeInterval[0] );
    	
    	//개별설정 일 때 첫번째 페이지 처리
    	if (mTimeInterval.length > 1) {
    		vibratingIntervalArr.push(setTimeout(vibrator, mTimeInterval[currentSlide]*1000));
    		++currentSlide;
    	}
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
  if(mTimeInterval !== 0){
	//일정간격 초기화
    clearInterval(vibratingInterval);
    //코드수정
    //슬라이드 개별설정 초기화
    for (var i = 0; i < vibratingIntervalArr.length; i++) {
        clearTimeout(vibratingIntervalArr[i]);
    }
    vibratingIntervalArr = [];
    //
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