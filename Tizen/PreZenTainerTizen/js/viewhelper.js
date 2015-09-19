var progressBarWidget,
    progressBar = document.getElementById("circleprogress");

//페이지 전환 변수
var mainPage = $('#main');
var startPage = $('#start');
var motionSettingPage = $('#motionSetting');
var enrollMotionPage = $('#enroll_motion');

//모션세팅에서 메인으로 페이지 전환
enrollMotionPage.on("swipedown", function() {
	is_motion(); //모션이 setting되어있는지 확인
	tau.changePage(mainPage);
	});

//스타트페이지에서 메인으로 페이지 전환
startPage.on("swipedown", function() {
	main_to_back(); 
	tau.changePage(mainPage);
	});

motionSettingPage.on("swipedown", function() {
	tau.changePage(startPage);
	});

// 화면구성변화
function updateConnection() {
  if(!isConnect){
	    $('#startbtn').attr('disabled', 'disabled');
	    changeButtonStart();
  }
  updateAfterStop();
}
// start button 활성화
function updateAfterOnReceivce(){
  if(isConnect){
    $('#startbtn').removeAttr('disabled');
    changeButtonStart();
  }
}
// start button 클릭 이후
function updateAfterStart(){
  if(isConnect){
	$('#startbtn').attr('disabled','disabled');
	$('#startbtn').attr('type','hidden');
    $('#pceventbtn_right').removeAttr('disabled');
    $('#pceventbtn_left').removeAttr('disabled');
    $('#stopbtn').removeAttr('disabled');
    $('#stopbtn').attr('type','button');
    $('#motionbtn').removeAttr('disabled');
    
  }else{
    toastAlert('연결을 확인하세요.');
  }
  changeButtonStart();
  changeButtonMotionSetting();
}
//stop button 클릭 이후
function updateAfterStop(){
  if(!isConnect){
    $('#pceventbtn_right').attr('disabled','disabled');
    $('#pceventbtn_left').attr('disabled','disabled');
    $('#stopbtn').attr('disabled','disabled');
    $('#stopbtn').attr('type','hidden');
    
    
    check = 0;
	document.getElementById("enable_motion").innerHTML="Off";
    $('#motionbtn').attr('disabled','disabled');
    $('#right_motion_btn').attr('disabled','disabled');
	$('#left_motion_btn').attr('disabled','disabled');
	
	document.getElementById("right_motion").innerHTML="Off";
	right_motion_enable=0;
	
	document.getElementById("left_motion").innerHTML="Off";
	left_motion_enable=0;
  }
  
  changeButtonMotionSetting();
  changeButtonStart();
}

//Popup toast of TAU
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



/*
 * 버튼의 클릭효과
 * */

// main page에 start버튼
function mainStartButtonClickEffect() {
   $('#mainStartButton').ready(function() {
         $('.startButtonMain').on('touchstart', function(event){
            $(this).addClass('active');
          });
         $('.startButtonMain').on('touchend', function(event){
            $(this).removeClass('active');
          });
      });
}


//main page에 setting버튼
function mainSettingButtonClickEffect() {
   $('#mainSettingButton').ready(function() {
         $('.settingButtonMain').on('touchstart', function(event){
            $(this).addClass('active');
          });
         $('.settingButtonMain').on('touchend', function(event){
            $(this).removeClass('active');
          });
      });
}

//start page에 setting버튼
function startSettingButtonClickEffect() {
   $('#startSettingButton').ready(function() {
         $('.settingButtonStart').on('touchstart', function(event){
            $(this).addClass('active');
          });
         $('.settingButtonStart').on('touchend', function(event){
            $(this).removeClass('active');
          });
      });
}
//start page에 start버튼
function startStartButtonClickEffect() {
   $('#startbtn').ready(function() {
         $('.startButton').on('touchstart', function(event){
            document.getElementById('stopbtn').style.backgroundImage= "url(./img/button/ic_start_disable.png)"
          });
         $('.startButton').on('touchend', function(event){
            document.getElementById('stopbtn').style.backgroundImage= "url(./img/button/ic_start.png)"
          });
      });
}
//start page에 stopt버튼
function startStopButtonClickEffect(){
   $('#stopbtn').ready(function() {
         $('.stopButton').on('touchstart', function(event){
            document.getElementById('stopbtn').style.backgroundImage= "url(./img/button/ic_stop_disable.png)"
          });
         $('.stopButton').on('touchend', function(event){
            document.getElementById('stopbtn').style.backgroundImage= "url(./img/button/ic_stop.png)"
          });
      });
}
//start page에 prevt버튼
function startLeftButtonClickEffect() {
   $('#pceventbtn_left').ready(function() {
         $('.prevButton').on('touchstart', function(event){
            document.getElementById('pceventbtn_left').style.backgroundImage= "url(./img/button/ic_left_disable.png)"
          });
         $('.prevButton').on('touchend', function(event){
            document.getElementById('pceventbtn_left').style.backgroundImage= "url(./img/button/ic_left.png)"
          });
      });
}
//start page에 next버튼
function startRightButtonClickEffect() {
   $('#pceventbtn_right').ready(function() {
         $('.nextButton').on('touchstart', function(event){
            document.getElementById('pceventbtn_right').style.backgroundImage= "url(./img/button/ic_right_disable.png)"
          });
         $('.nextButton').on('touchend', function(event){
            document.getElementById('pceventbtn_right').style.backgroundImage= "url(./img/button/ic_right.png)"
          });
      });
}

