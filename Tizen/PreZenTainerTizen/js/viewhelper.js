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