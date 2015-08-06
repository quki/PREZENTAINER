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
  }else{
    toastAlert('연결을 확인하세요.');
  }
}

function updateAfterStop(){
  if(!isConnect){
    $('#pceventbtn').attr('disabled','disabled');
    $('#stopbtn').attr('disabled','disabled');
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