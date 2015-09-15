//실시간 가속도 값
var accelX = 0, accelY = 0, accelZ = 0;

//한번모션 인식 위한 변수,통신을 원활히 하기위한 변수
var motion_check=0;


//사용자가 입력한 가속도 값의 평균값 (오른쪽 슬라이드 넘길값)
var averageX_right = 0, averageY_right = 0, averageZ_right = 0;

//사용자가 입력한 가속도 값의 평균값 (왼쪽 슬라이드 넘길값)
var averageX_left = 0, averageY_left = 0, averageZ_left = 0;

//사용할 모션축 가속도 결정
var rightDirection = 0; //0->x축 사용 ,1->y축 사용 ,2->z축 사용
var leftDirection = 0; //0->x축 사용 ,1->y축 사용 ,2->z축 사용
//배열의
var userInputIndex = 5;
var arrayIndex = 0;

//motion test하기위한 on/off 제어 변수
var motion_test=0;  //0일때 off ,1 일때 on !!

//모션사용 check변수
var check = 0;      //0일때 off ,1 일때 on !!

//좌우측 모션 사용제어 변수
var right_motion_enable=0; // 0일때 off ,1 일때 on !!
var left_motion_enable=0; // 0일때 off ,1 일때 on !!

//좌,우 측 모션을 컨트롤 할 변수
var control_right_left=0;          //0일경우 오른쪽!   1 일경우 왼쪽!!

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
    	document.getElementById('ok_button').style.backgroundImage= "url(./img/button/Red.png)";
    }

    showResult();
    if(averageX_right != 0 && averageY_right != 0 && averageZ_right != 0 && check==1) {    //통신 할때 작동하는 코드!
    	motionSensor();
    }

    if(averageX_right != 0 && averageY_right != 0 && averageZ_right != 0 && motion_test==1) {    //test할때 작동하는 코드
    	test_motionSensor();
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
		document.getElementById('ok_button').style.backgroundImage= "url(./img/button/Gray.png)";
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

			if(gapSumX<40 && gapSumY<40 && gapSumZ<40 )        //일정하게 휘둘렀다면
			{
				for (var i = 0; i < userInputIndex; i++) {
					sumX += Number(accelXArr[i]);
					sumY += Number(accelYArr[i]);
					sumZ += Number(accelZArr[i]);
					console.log("sum : " + sumX + " ," + sumY + " ," + sumZ);
				}
				if(control_right_left==0)
				{
					averageX_right = sumX/userInputIndex;
					averageY_right = sumY/userInputIndex;
					averageZ_right = sumZ/userInputIndex;
					RightSelcetDirection();
					control_right_left=1;
					arrayIndex=0;
					alert('오른쪽 넘김 설정 완료!! 왼쪽 넘김을 위한 같은동작을 5번 해주세요!!');
				}
				else
				{
					averageX_left = sumX/userInputIndex;
					averageY_left = sumY/userInputIndex;
					averageZ_left = sumZ/userInputIndex;
					LeftSelcetDirection();
					finish_motionSetting();
				}
			}
			else
			{
				retry_motionSetting();
			}
		}
	}
	changeButtonEnrollMotion();
	console.log("average_right : " + averageX_right + " ," + averageY_right + " ," + averageZ_right);
	console.log("average_left : " + averageX_left + " ," + averageY_left + " ," + averageZ_left);
}

function motionSensor() {
	if (accelX < averageX_right+5 && accelX > averageX_right-5 &&              //오른쪽 이벤트
		accelY > averageY_right-5 && accelY < averageY_right+5 &&
		accelZ > averageZ_right-5 && accelZ < averageZ_right+5 &&
		motion_check == 0 && right_motion_enable==1) {
		 console.log('call right function');
		 navigator.vibrate(1000);  //나중에 뺄 코드
		 eventtopc("right"); //오른쪽 이벤트!
		 motion_check=1;
	}
	if (accelX < averageX_left+5 && accelX > averageX_left-5 &&               //왼쪽이벤트
		accelY > averageY_left-5 && accelY < averageY_left+5 &&
		accelZ > averageZ_left-5 && accelZ < averageZ_left+5 &&
		motion_check == 0 && left_motion_enable==1) {
		console.log('call left function');
		navigator.vibrate(1000);  //나중에 뺄 코드
		eventtopc("leftt"); //왼쪽 이벤트!
		motion_check=1;
	}
	accelX = 0;
	accelY = 0;
	accelZ = 0;
}

function test_motionSensor() {

	if(rightDirection==0 && right_motion_enable==1 && accelX < averageX_right+5 && accelX > averageX_right-5)  //오른쪽 모션,x축가속도측정
	{
		navigator.vibrate(2000);
		console.log('call right x');
	}
	if(rightDirection==1 && right_motion_enable==1 && accelY > averageY_right-5 && accelY < averageY_right+5)  //오른쪽 모션,y축가속도측정
	{
		navigator.vibrate(2000);
		console.log('call right y');
	}
	if(rightDirection==2 && right_motion_enable==1 && accelZ > averageZ_right-5 && accelZ < averageZ_right+5)   //오른쪽 모션,z축가속도측정
	{
		navigator.vibrate(2000);
		console.log('call right z');
	}


	if(leftDirection==0 && left_motion_enable==1 && accelX < averageX_left+5 && accelX > averageX_left-5)   //왼쪽 모션,x축가속도측정
	{
		console.log('call left x');
		navigator.vibrate(1000);
	}
	if(leftDirection==1 && left_motion_enable==1 && accelY > averageY_left-5 && accelY < averageY_left+5)   //왼쪽 모션,y축가속도측정
	{
		console.log('call left y');
		navigator.vibrate(1000);
	}
	if(leftDirection==2 && left_motion_enable==1 && accelZ > averageZ_left-5 && accelZ < averageZ_left+5)   //왼쪽 모션,z축가속도측정
	{
		console.log('call left z');
		navigator.vibrate(1000);
	}

	accelX = 0;
	accelY = 0;
	accelZ = 0;

}



function RightSelcetDirection(){  // 사용할 가속도 방향을 정함(오른쪽 모션)
	rightDirection=0;
	if(Math.abs(averageX_right)<Math.abs(averageY_right)){
		rightDirection=1;
	}
	if(Math.abs(averageY_right)<Math.abs(averageZ_right)){
		rightDirection=2;
	}
}

function LeftSelcetDirection(){  // 사용할 가속도 방향을 정함(왼쪽 모션)
	leftDirection=0;
	if(Math.abs(averageX_left)<Math.abs(averageY_left)){
		leftDirection=1;
	}
	if(Math.abs(averageY_left)<Math.abs(averageZ_left)){
		leftDirection=2;
	}
}


/* changeButton함수 추가 */

function changeButtonStart() {
	if(document.getElementById('startbtn').disabled == true) {
		document.getElementById('startbtn').style.backgroundImage= "url(./img/button/Gray.png)"
	}
	else {
		document.getElementById('startbtn').style.backgroundImage= "url(./img/button/Green.png)"
	}

	if(document.getElementById('stopbtn').disabled == true) {
		document.getElementById('stopbtn').style.backgroundImage= "url(./img/button/Gray.png)"
	}
	else {
		document.getElementById('stopbtn').style.backgroundImage= "url(./img/button/Green.png)"
	}

	if(document.getElementById('pceventbtn').disabled == true) {
		document.getElementById('pceventbtn').style.backgroundImage= "url(./img/button/Gray.png)"
	}
	else {
		document.getElementById('pceventbtn').style.backgroundImage= "url(./img/button/Red.png)"
	}

	if(document.getElementById('pceventbtn2').disabled == true) {
		document.getElementById('pceventbtn2').style.backgroundImage= "url(./img/button/Gray.png)"
	}
	else {
		document.getElementById('pceventbtn2').style.backgroundImage= "url(./img/button/Red.png)"
	}

}

function changeButtonMotionSetting() {
	if(document.getElementById('motionbtn').disabled == true) {
		document.getElementById('motionbtn').style.backgroundImage= "url(./img/button/Gray.png)"
	}
	else {
		document.getElementById('motionbtn').style.backgroundImage= "url(./img/button/Brown.png)"
	}

	if(document.getElementById('right_motion_btn').disabled == true) {
		document.getElementById('right_motion_btn').style.backgroundImage= "url(./img/button/Gray.png)"
	}
	else {
		document.getElementById('right_motion_btn').style.backgroundImage= "url(./img/button/Orange.png)"
	}

	if(document.getElementById('left_motion_btn').disabled == true) {
		document.getElementById('left_motion_btn').style.backgroundImage= "url(./img/button/Gray.png)"
	}
	else {
		document.getElementById('left_motion_btn').style.backgroundImage= "url(./img/button/Orange.png)"
	}

}

function changeButtonEnrollMotion() {
	if(document.getElementById('motion_test_button').disabled == true) {
		document.getElementById('motion_test_button').style.backgroundImage= "url(./img/button/Gray.png)"
	}
	else {
		document.getElementById('motion_test_button').style.backgroundImage= "url(./img/button/Brown.png)"
	}

	if(document.getElementById('test_right_motion_btn').disabled == true) {
		document.getElementById('test_right_motion_btn').style.backgroundImage= "url(./img/button/Gray.png)"
	}
	else {
		document.getElementById('test_right_motion_btn').style.backgroundImage= "url(./img/button/Orange.png)"
	}

	if(document.getElementById('test_left_motion_btn').disabled == true) {
		document.getElementById('test_left_motion_btn').style.backgroundImage= "url(./img/button/Gray.png)"
	}
	else {
		document.getElementById('test_left_motion_btn').style.backgroundImage= "url(./img/button/Orange.png)"
	}

	if(document.getElementById('reset_button').disabled == true) {
		document.getElementById('reset_button').style.backgroundImage= "url(./img/button/Gray.png)"
	}
	else {
		document.getElementById('reset_button').style.backgroundImage= "url(./img/button/Red.png)"
	}
}

function ready_motionSetting() {
	if(averageX_right != 0 && averageY_right != 0 && averageZ_right != 0 &&
			averageX_left != 0 && averageY_left != 0 && averageZ_left != 0	) {
		//모션이 저장되어있을 경우 해당페이지로 바로이동
		$('#reset_button').removeAttr('disabled');
		$('#motion_test_button').removeAttr('disabled');
    }
	else
	{
		$('#ok_button').attr('disabled','disabled');
		$('#reset_button').attr('disabled','disabled');
		$('#motion_test_button').attr('disabled','disabled');
		alert('오른쪽 넘김을 위한 같은동작을 5번 해주세요!');
		arrayIndex=0;
		accelX = 0;
	    accelY = 0;
	    accelZ = 0;
	    document.getElementById('ok_button').style.backgroundImage= "url(./img/button/Gray.png)"
	}
	save_setLocalStorage();
	changeButtonEnrollMotion();
}
function retry_motionSetting() {
	if(control_right_left==0)
	{
		alert('오른쪽넘김 모션이 일정하지 않습니다.! 다시입력!');
	}
	else
	{
		alert('왼쪽넘김 모션이 일정하지 않습니다.! 다시입력!');
	}

	$('#ok_button').attr('disabled','disabled');
	arrayIndex=0;
	accelX = 0;
    accelY = 0;
    accelZ = 0;
}
function finish_motionSetting() {
	alert('완료되었습니다!');
	console.log(averageX_right);
	console.log(averageY_right);
	console.log(averageZ_right);
	console.log(averageX_left);
	console.log(averageY_left);
	console.log(averageZ_left);
	console.log(arrayIndex);
	save_setLocalStorage();
	document.getElementById("ok_button").value="완료";
	$('#ok_button').attr('disabled','disabled');
	$('#reset_button').removeAttr('disabled');
	$('#motion_test_button').removeAttr('disabled');
}
function modify_motionSetting() {
	alert('오른쪽 넘김을 위한 같은동작을 5번 해주세요!');
	document.getElementById("ok_button").value="확인";
	$('#ok_button').attr('disabled','disabled');
	$('#reset_button').attr('disabled','disabled');
	$('#motion_test_button').attr('disabled','disabled');
	/* test_right_motion_btn, test_left_motion_btn에 disabled 속성 추가 */
	$('#test_right_motion_btn').attr('disabled','disabled');
	$('#test_left_motion_btn').attr('disabled','disabled');
	check = 0;
	document.getElementById("enable_motion").innerHTML="Off";
	motion_test=0;
	document.getElementById("test_motion").innerHTML="Off";
	arrayIndex=0;

	control_right_left=0;
	accelX = 0;
  accelY = 0;
  accelZ = 0;
  averageX_right=0;
	averageY_right=0;
	averageZ_right=0;
	averageX_left=0;
	averageY_left=0;
	averageZ_left=0;
	rightDirection=0;
	leftDirection=0;
	save_setLocalStorage();
	changeButtonEnrollMotion();
}
function is_motion() {   //back버튼 눌렀을 때
	if(averageX_right != 0 && averageY_right != 0 && averageZ_right != 0) {
		document.getElementById("motion_state").innerHTML="Modify";
    }
	else
	{
		document.getElementById("motion_state").innerHTML="Enroll";
		arrayIndex=0;
		accelX = 0;
	    accelY = 0;
	    accelZ = 0;

	}

	motion_test=0;  //설정화면에서 나가면 저장된 모션을 동작하지 않도록 하기 위함
	document.getElementById("test_motion").innerHTML="Off";

	$('#test_right_motion_btn').attr('disabled','disabled');
	$('#test_left_motion_btn').attr('disabled','disabled');

	document.getElementById("test_right_motion").innerHTML="Off";
	document.getElementById("right_motion").innerHTML="Off";
	right_motion_enable=0;

	document.getElementById("test_left_motion").innerHTML="Off";
	document.getElementById("left_motion").innerHTML="Off";
	left_motion_enable=0;


}
function main_to_back(){
	check=0;   //메인화면 에서 나가면 저장된 모션을 동작하지 않도록 하기 위함
	document.getElementById("enable_motion").innerHTML="Off";

	$('#right_motion_btn').attr('disabled','disabled');
	$('#left_motion_btn').attr('disabled','disabled');

	document.getElementById("right_motion").innerHTML="Off";
	document.getElementById("test_right_motion").innerHTML="Off";
	right_motion_enable=0;

	document.getElementById("left_motion").innerHTML="Off";
	document.getElementById("test_left_motion").innerHTML="Off";
	left_motion_enable=0;

	changeButtonStart();
	changeButtonMotionSetting();
	changeButtonEnrollMotion();
}


function enable_motion(){
	if(averageX_right != 0 && averageY_right != 0 && averageZ_right != 0){        //motion이 설정되어있을 때 motion설정을 on/off가능

		if(check==0)
		{
			document.getElementById("enable_motion").innerHTML="On";
			check=1;
			$('#motionbtn').removeAttr('disabled');
			$('#right_motion_btn').removeAttr('disabled');
			$('#left_motion_btn').removeAttr('disabled');
		}
		else
		{
			document.getElementById("enable_motion").innerHTML="Off";
			check=0;
			$('#right_motion_btn').attr('disabled','disabled');
			$('#left_motion_btn').attr('disabled','disabled');

			document.getElementById("right_motion").innerHTML="Off";
			right_motion_enable=0;

			document.getElementById("left_motion").innerHTML="Off";
			left_motion_enable=0;
		}

	}
	else{
		$('#motionbtn').attr('disabled','disabled');
		alert('Motion 등록을 먼저해주세요!');
	}

	changeButtonMotionSetting();
}
function load_setLocalStorage(){
	if(window.localStorage['averageX_right'] == null &&
			  window.localStorage['averageY_right'] == null &&
			           window.localStorage['averageZ_right'] == null &&
			           		window.localStorage['averageX_left'] == null &&
			           				window.localStorage['averageY_left'] == null &&
			           					window.localStorage['averageZ_left'] == null &&
			           								window.localStorage['arrayIndex'] == null &&
															  			window.localStorage['rightDirection'] == null &&
																		 				window.localStorage['leftDirection'] == null)
	{
		alert('Motion을 사용하려면 모션을 등록하셔주세요!');
		//어플시작!
	}
	else if(window.localStorage['averageX_right'] == 0 &&
			  	window.localStorage['averageY_right'] == 0 &&
			  		window.localStorage['averageZ_right'] == 0 &&
			  			window.localStorage['averageX_left'] == 0 &&
			  				window.localStorage['averageY_left'] == 0 &&
			  					window.localStorage['averageZ_left'] == 0 &&
			  							window.localStorage['arrayIndex'] == 0 &&
												window.localStorage['rightDirection'] == 0 &&
														window.localStorage['leftDirection'] == 0)
	{
		alert('Motion을 사용하려면 모션을 등록하셔주세요!');
		//어플시작!
	}
	else
	{
		averageX_right=Number(window.localStorage['averageX_right']);
		averageY_right=Number(window.localStorage['averageY_right']);
		averageZ_right=Number(window.localStorage['averageZ_right']);
		averageX_left=Number(window.localStorage['averageX_left']);
		averageY_left=Number(window.localStorage['averageY_left']);
		averageZ_left=Number(window.localStorage['averageZ_left']);
		arrayIndex=Number(window.localStorage['arrayIndex']);
		rightDirection=Number(window.localStorage['rightDirection']);
		leftDirection=Number(window.localStorage['leftDirection']);
		console.log(averageX_right);
		console.log(averageY_right);
		console.log(averageZ_right);
		console.log(averageX_left);
		console.log(averageY_left);
		console.log(averageZ_left);
		console.log(arrayIndex);
		console.log(rightDirection);
		console.log(leftDirection);


		alert('Motion 등록이 되어있습니다.!');
	}
}

function save_setLocalStorage(){
	window.localStorage['averageX_right']=averageX_right;
	window.localStorage['averageY_right']=averageY_right;
	window.localStorage['averageZ_right']=averageZ_right;
	window.localStorage['averageX_left']=averageX_left;
	window.localStorage['averageY_left']=averageY_left;
	window.localStorage['averageZ_left']=averageZ_left;
	window.localStorage['arrayIndex']=arrayIndex;
	window.localStorage['rightDirection']=rightDirection;
	window.localStorage['leftDirection']=leftDirection;
}

function test_motion_on_off(){

	if(motion_test==0)
	{
		document.getElementById("test_motion").innerHTML="On";
		motion_test=1;
		check = 0;

		$('#test_right_motion_btn').removeAttr('disabled');
		$('#test_left_motion_btn').removeAttr('disabled');

	}
	else
	{
		document.getElementById("test_motion").innerHTML="Off";
		motion_test=0;

		$('#test_right_motion_btn').attr('disabled','disabled');
		$('#test_left_motion_btn').attr('disabled','disabled');

		document.getElementById("test_right_motion").innerHTML="Off";
		right_motion_enable=0;

		document.getElementById("test_left_motion").innerHTML="Off";
		left_motion_enable=0;

	}
	changeButtonEnrollMotion();
}

function right_enable_motion(){
	if(right_motion_enable==0)
	{
		document.getElementById("test_right_motion").innerHTML="On";
		document.getElementById("right_motion").innerHTML="On";
		right_motion_enable=1;
	}
	else
	{
		document.getElementById("test_right_motion").innerHTML="Off";

		document.getElementById("right_motion").innerHTML="Off";
		right_motion_enable=0;
	}
	changeButtonEnrollMotion();
	changeButtonMotionSetting();
}

function left_enable_motion(){
	if(left_motion_enable==0)
	{
		document.getElementById("test_left_motion").innerHTML="On";
		document.getElementById("left_motion").innerHTML="On";
		left_motion_enable=1;
	}
	else
	{
		document.getElementById("test_left_motion").innerHTML="Off";
		document.getElementById("left_motion").innerHTML="Off";
		left_motion_enable=0;
	}
	changeButtonEnrollMotion();
	changeButtonMotionSetting();
}


//Initialize function
window.onload = function () {
    // TODO:: Do your initialization job
	window.addEventListener("devicemotion", onDeviceMotion, true);
	load_setLocalStorage();
    console.log("init() called");


};
