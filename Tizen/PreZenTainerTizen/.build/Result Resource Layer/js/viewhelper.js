var progressBarWidget,
    progressBar = document.getElementById("circleprogress");


// 화면구성변화
function updateConnection() {
  if(isConnect){
    $('.ui-listview').empty();
    $('.ui-listview').append('<li>연결됨</li>');
  }else{
    $('.ui-listview').empty();
    $('.ui-listview').append('<li>연결하세요</li>');
    updateAfterStop();
  }
}
// start button 활성화
function updateAfterOnReceivce(){
  if(isConnect){
    $('#startbtn').removeAttr('disabled');
  }
}
// start button 클릭 이후
function updateAfterStart(){
  if(isConnect){
    $('#startbtn').attr('disabled','disabled');
    $('#pceventbtn').removeAttr('disabled');
    $('#stopbtn').removeAttr('disabled');
    $('#motionbtn').removeAttr('disabled');
  }else{
    toastAlert('연결을 확인하세요.');
  }
}

function updateAfterStop(){
  if(!isConnect){
    $('#pceventbtn').attr('disabled','disabled');
    $('#stopbtn').attr('disabled','disabled');
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