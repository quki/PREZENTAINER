//실시간 가속도 값
var accelX = 0, accelY = 0, accelZ = 0;

//모션사용 check변수
var check = 0;

//사용자가 입력한 가속도 값의 평균값
var averageX = 0, averageY = 0, averageZ = 0;


//배열의
var userInputIndex = 5;
var arrayIndex = 0;
 

//사용자가 입력한 가속도값을 저장하는 배열
var accelXArr = new Array(userInputIndex);
var accelYArr = new Array(userInputIndex);
var accelZArr = new Array(userInputIndex);

function buttonSetting() {
	window.addEventListener("devicemotion", onDeviceMotion, true);
}

function showResult() {
	$("#count").html(arrayIndex+"/5");
}

function onDeviceMotion(e) {
    if( Math.abs(accelX) < Math.abs(e.acceleration.x) )
        accelX = e.acceleration.x;
    if( Math.abs(accelY) < Math.abs(e.acceleration.y) )
        accelY = e.acceleration.y;
    if( Math.abs(accelZ) < Math.abs(e.acceleration.z) )
        accelZ = e.acceleration.z;
    

    if(Math.abs(Number(accelX))+Math.abs(Number(accelY))+Math.abs(Number(accelZ))>15 && arrayIndex!=userInputIndex)
    {
    	$('#ok_button').removeAttr('disabled');
    }

    showResult();
    if(averageX != 0 && averageY != 0 && averageZ != 0 && check==1) {
    	motionSensor();
    }
}

function buttonPush() {
	//추가
	//입력완료를 누를때 마다 arrayIndex++;
	if(arrayIndex <= userInputIndex){

		accelXArr[arrayIndex] = Number(accelX);
		accelYArr[arrayIndex] = Number(accelY);
		accelZArr[arrayIndex] = Number(accelZ);

		arrayIndex += 1;

		accelX = 0;
		accelY = 0;
		accelZ = 0;
		$('#ok_button').attr('disabled','disabled');
		if (arrayIndex == userInputIndex) {
			var sumX=0; 
			var sumY =0;
			var sumZ =0;
			var gapSumX=0;
			var gapSumY=0;
			var gapSumZ=0;

			for (var i = 0; i < userInputIndex-1; i++) {    //일정하게 휘둘렀는지 체크하기 위함
				gapSumX += Math.abs(Number(accelXArr[i])-Number(accelXArr[i+1]));
				gapSumY += Math.abs(Number(accelYArr[i])-Number(accelYArr[i+1]));
				gapSumZ += Math.abs(Number(accelZArr[i])-Number(accelZArr[i+1]));
				console.log("sum : " + sumX + " ," + sumY + " ," + sumZ);
			}

			if(gapSumX<20 && gapSumY<20 && gapSumZ<20 )        //일정하게 휘둘렀다면
			{
				for (var i = 0; i < userInputIndex; i++) {
					sumX += Number(accelXArr[i]);
					sumY += Number(accelYArr[i]);
					sumZ += Number(accelZArr[i]);
					console.log("sum : " + sumX + " ," + sumY + " ," + sumZ);
				}
				averageX = sumX/userInputIndex;
				averageY = sumY/userInputIndex;
				averageZ = sumZ/userInputIndex;
				finish_motionSetting();
			}
			else	
			{
				retry_motionSetting()
			}
		}   
	}
	console.log("average : " + averageX + " ," + averageY + " ," + averageZ);
}

function motionSensor() {
	if (accelX < averageX+5 && accelX > averageX-5 && 
		accelY > averageY-5 && accelY < averageY+5 &&
		accelZ > averageZ-5 && accelZ < averageZ+5   ) {
		 navigator.vibrate(2000);
		 eventtopc();
	}
	accelX = 0;
	accelY = 0;
	accelZ = 0;
}

function ready_motionSetting() {
	if(averageX != 0 && averageY != 0 && averageZ != 0) {
		//모션이 저장되어있을 경우 해당페이지로 바로이동
    }
	else
	{
		$('#ok_button').attr('disabled','disabled');
		$('#reset_button').attr('disabled','disabled');
		alert('같은동작을 5번 해주세요!');
		arrayIndex=0;
		accelX = 0;
	    accelY = 0;
	    accelZ = 0;
	}
	
}
function retry_motionSetting() {

	alert('모션이 일정하지 않습니다.! 다시입력!');
	$('#ok_button').attr('disabled','disabled');
	arrayIndex=0;
	accelX = 0;
    accelY = 0;
    accelZ = 0;
}
function finish_motionSetting() {
	alert('완료되었습니다!');
	document.getElementById("ok_button").value="완료";
	$('#ok_button').attr('disabled','disabled');
	$('#reset_button').removeAttr('disabled');
	
}
function modify_motionSetting() {
	document.getElementById("ok_button").value="확인";
	$('#ok_button').attr('disabled','disabled');
	$('#reset_button').attr('disabled','disabled');
	arrayIndex=0;
	accelX = 0;
    accelY = 0;
    accelZ = 0;
    averageX=0;
	averageY=0;
	averageZ=0;
}
function is_motion() {
	if(averageX != 0 && averageY != 0 && averageZ != 0) {
		document.getElementById("motion_state").innerHTML="Modify Motion";
    }
	else
	{
		document.getElementById("motion_state").innerHTML="Enroll Motion";
		arrayIndex=0;
		accelX = 0;
	    accelY = 0;
	    accelZ = 0;

	}
}

function enable_motion(){
	if(averageX != 0 && averageY != 0 && averageZ != 0){        //motion이 설정되어있을 때 motion설정을 on/off가능  
		
		if(check==0)
		{
			document.getElementById("enable_motion").innerHTML="On";
			check=1;
		}
		else
		{
			document.getElementById("enable_motion").innerHTML="Off";
			check=0;
		}	
		
	}
	else{
		alert('Motion 등록을 먼저해주세요!');
	}
	
	
}

//Initialize function
window.onload = function () {
    // TODO:: Do your initialization job
	window.addEventListener("devicemotion", onDeviceMotion, true);
    console.log("init() called");
    // add eventListener for tizenhwkey
    document.addEventListener('tizenhwkey', function(e) {
        if(e.keyName == "back")
            tizen.application.getCurrentApplication().exit();
    });
};