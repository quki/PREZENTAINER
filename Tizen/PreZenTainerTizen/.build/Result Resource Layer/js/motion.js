//실시간 가속도 값
var accelX = 0, accelY = 0, accelZ = 0;

//모션사용 check변수
var check = 0;

//1번모션 인식 위한 변수
var motion_check=0;


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

			if(gapSumX<25 && gapSumY<25 && gapSumZ<25 )        //일정하게 휘둘렀다면
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
	if (accelX < averageX+7 && accelX > averageX-7 && 
		accelY > averageY-7 && accelY < averageY+7 &&
		accelZ > averageZ-7 && accelZ < averageZ+7 &&
		motion_check == 0 ) {
		 console.log('call function');
		 navigator.vibrate(1000);
		 eventtopc();
		 p_makeJsonEventTime();
		 motion_check=1;
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
	save_setLocalStorage();
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
	console.log(averageX);
	console.log(averageY);
	console.log(averageZ);
	console.log(arrayIndex);
	save_setLocalStorage();
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
function load_setLocalStorage(){
	if(window.localStorage['averageX'] == null && 
			  window.localStorage['averageY'] == null &&
			           window.localStorage['averageZ'] == null &&
			           							window.localStorage['arrayIndex'] == null)
	{
		alert('Motion을 사용하려면 모션을 등록하셔주세요!');
		//어플시작!
	}	
	else if(window.localStorage['averageX'] == 0 && 
			  window.localStorage['averageY'] == 0 &&
	           window.localStorage['averageZ'] == 0 &&
	           		window.localStorage['arrayIndex'] == 0)
	{
		alert('Motion을 사용하려면 모션을 등록하셔주세요!');
		//어플시작!
	}
	else
	{
		averageX=Number(window.localStorage['averageX']);
		averageY=Number(window.localStorage['averageY']);
		averageZ=Number(window.localStorage['averageZ']);
		arrayIndex=Number(window.localStorage['arrayIndex']);
		document.getElementById("motion_state").innerHTML="Modify Motion";
		
		console.log(averageX);
		console.log(averageY);
		console.log(averageZ);
		console.log(arrayIndex);
		
		alert('Motion 등록이 되어있습니다.!');
	}
}

function save_setLocalStorage(){
	window.localStorage['averageX']=averageX;
	window.localStorage['averageY']=averageY;
	window.localStorage['averageZ']=averageZ;
	window.localStorage['arrayIndex']=arrayIndex;
}


//Initialize function
window.onload = function () {
    // TODO:: Do your initialization job
	window.addEventListener("devicemotion", onDeviceMotion, true);
	load_setLocalStorage();
    console.log("init() called");
    // add eventListener for tizenhwkey
    document.addEventListener('tizenhwkey', function(e) {
        if(e.keyName == "back")
            tizen.application.getCurrentApplication().exit();
    });
};