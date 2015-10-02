

/*
 * 
 * PC에 이벤트 주는 함수 
 * 
 * */

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
