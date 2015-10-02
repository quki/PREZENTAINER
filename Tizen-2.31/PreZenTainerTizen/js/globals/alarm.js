
/*
 * 
 * 타이머(알람) 관련 함수
 * 
 * */


// 진동 for 2 sec
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