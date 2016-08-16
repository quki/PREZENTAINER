
/*
 * 
 * 화면관리 관련함수
 * 
 * */



var progressBarWidget,
    progressBar = document.getElementById("circleprogress");
var isConnect = false;

// Service Connection 이후 화면 구성 변화 (button 활성화 비활성화)
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
    $('#motionbtn_toggle').removeAttr('disabled');
  }else{
    toastAlert('연결을 확인하세요.');
  }
  changeButtonStart();
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
    $('#motionbtn_toggle').attr('disabled','disabled');

  }
  
  changeButtonStart();
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
   $('#motionbtn_toggle').ready(function() {
         $('.settingButtonStart').on('touchstart', function(event){
           document.getElementById('motionbtn_toggle').style.backgroundImage= "url(./img/button/ic_settings_disable.png)"
          });
         $('.settingButtonStart').on('touchend', function(event){
           document.getElementById('motionbtn_toggle').style.backgroundImage= "url(./img/button/ic_settings_white.png)"
          });
      });
}
//start page에 start버튼
/*function startStartButtonClickEffect() {
   $('#startbtn').ready(function() {
         $('.startButton').on('touchstart', function(event){
            document.getElementById('stopbtn').style.backgroundImage= "url(./img/button/ic_start_disable.png)"
          });
         $('.startButton').on('touchend', function(event){
            document.getElementById('stopbtn').style.backgroundImage= "url(./img/button/ic_start.png)"
          });
      });
}*/
//start page에 stopt버튼
/*function startStopButtonClickEffect(){
   $('#stopbtn').ready(function() {
         $('.stopButton').on('touchstart', function(event){
            document.getElementById('stopbtn').style.backgroundImage= "url(./img/button/ic_stop_disable.png)"
          });
         $('.stopButton').on('touchend', function(event){
            document.getElementById('stopbtn').style.backgroundImage= "url(./img/button/ic_stop.png)"
          });
      });
}*/
//start page에 left버튼
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
//start page에 right버튼
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

/*
 * changeButton
 * 버튼의 활성화, 비활성화 효과 관리
 *
 *  */
//start page
function changeButtonStart() {

   /*
    * start button
    * */
   if(document.getElementById('startbtn').disabled == true) {
      document.getElementById('startbtn').style.backgroundImage= "url(./img/button/ic_start_disable.png)"
   }
   else {
      document.getElementById('startbtn').style.backgroundImage= "url(./img/button/ic_start.png)"
   }


    /*
   * stop button
   * */
   if(document.getElementById('stopbtn').disabled == true) {
      document.getElementById('stopbtn').style.backgroundImage= "url(./img/button/ic_stop_disable.png)"
   }
   else {
      document.getElementById('stopbtn').style.backgroundImage= "url(./img/button/ic_stop.png)"
   }

   /*
   * right button
   * */
   if(document.getElementById('pceventbtn_right').disabled == true) {
      document.getElementById('pceventbtn_right').style.backgroundImage= "url(./img/button/ic_right_disable.png)"
   }
   else {
      document.getElementById('pceventbtn_right').style.backgroundImage= "url(./img/button/ic_right.png)"
   }
   /*
   * left button
   * */
   if(document.getElementById('pceventbtn_left').disabled == true) {
      document.getElementById('pceventbtn_left').style.backgroundImage= "url(./img/button/ic_left_disable.png)"
   }
   else {
      document.getElementById('pceventbtn_left').style.backgroundImage= "url(./img/button/ic_left.png)"
   }
   /*
    * motion toggle button
    * */
   if(document.getElementById('motionbtn_toggle').disabled == true) {
      document.getElementById('motionbtn_toggle').style.backgroundImage= "url(./img/button/ic_settings_disable.png)"
   }
   else {
      document.getElementById('motionbtn_toggle').style.backgroundImage= "url(./img/button/ic_settings_white.png)"
   }

}

// enrollmotion page
function changeButtonEnrollMotion() {

  /*
   * test button
   * */
   if(document.getElementById('motion_test_button').disabled == true) {
      document.getElementById('motion_test_button').style.backgroundImage= "url(./img/button/ic_test_disable.png)"
   }
   else {
      document.getElementById('motion_test_button').style.backgroundImage= "url(./img/button/ic_test.png)"
   }

   /*
   * resetting button
   * */
   if(document.getElementById('reset_button').disabled == true) {
      document.getElementById('reset_button').style.backgroundImage= "url(./img/button/ic_resetting_disable.png)"
   }
   else {
      document.getElementById('reset_button').style.backgroundImage= "url(./img/button/ic_resetting.png)"
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

(function() {
// tau progress bar implementation
var sendPage = document.getElementById('sendPage');
sendPage.addEventListener('pagehide', function() {
 progressBarWidget.destroy();
});


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
