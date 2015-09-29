/*
 *
 * motion관련 함수
 *
 * */


 //실시간 가속도 값
 var accelX = 0, accelY = 0, accelZ = 0;

 //한번모션 인식 위한 변수,통신을 원활히 하기위한 변수
 var motion_check=0;


 //사용자가 입력한 가속도 값의 평균값
 var averageX = 0, averageY = 0, averageZ = 0;


 //가속도 방향 설정 (디테일 하게 모션인식을 하기 위해서)
 var Direction = 0; //0~7까지 설정됨


 //모션 설정제어 하기 위한 변수
 var userInputIndex = 3;
 var arrayIndex = 0;

 //motion test하기위한 on/off 제어 변수
 var motion_test=0;  //0일때 off ,1 일때 on !!

 //모션사용 check변수
 var check = 0;      //0일때 off ,1 일때 on !!

 //사용자가 입력한 가속도값을 저장하는 배열
 var accelXArr = new Array(userInputIndex);
 var accelYArr = new Array(userInputIndex);
 var accelZArr = new Array(userInputIndex);

 function showResult() {
 	$("#count").html(arrayIndex+"/"+userInputIndex);
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
     	document.getElementById('ok_button').style.backgroundImage= "url(./img/button/ic_check.png)";
     }

     showResult();
     if(averageX != 0 && averageY != 0 && averageZ != 0 && check==1) {    //통신 할때 작동하는 코드!
     	motionSensor();
     }

     if(averageX != 0 && averageY != 0 && averageZ != 0 && motion_test==1) {    //test할때 작동하는 코드
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
 		document.getElementById('ok_button').style.backgroundImage= "url(./img/button/ic_check_disable.png)";
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

 			if(gapSumX<50 && gapSumY<50 && gapSumZ<50 )        //일정하게 휘둘렀다면
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
 					setDirection();
 					finish_motionSetting();
 					alert('설정 완료!');
 			}
 			else
 			{
 				retry_motionSetting();
 			}
 		}
 	}
 	changeButtonEnrollMotion();
 	console.log("average : " + averageX + " ," + averageY + " ," + averageZ);
 }

 function motionSensor() {
 	if (Math.abs(averageX)-5 < Math.abs(accelX) &&             //오른쪽 이벤트
 		Math.abs(averageY)-5 < Math.abs(accelY) &&
 		Math.abs(averageZ)-5 < Math.abs(accelZ) &&
 		motion_check == 0) {
 		 console.log('call right function');
 		 navigator.vibrate(1000);  //나중에 뺄 코드
 		 eventtopc("right"); //오른쪽 이벤트!
 		 motion_check=1;
 	}
 	accelX = 0;
 	accelY = 0;
 	accelZ = 0;
 }

 function test_motionSensor() {

 	if (Math.abs(averageX)-5 < Math.abs(accelX) &&  //오른쪽 이벤트
 		Math.abs(averageY)-5 < Math.abs(accelY) &&
 		Math.abs(averageZ)-5 < Math.abs(accelZ) )
 	{
 		if(checkDirection()== Direction){
 			console.log('Direction:'+Direction);
 			navigator.vibrate(2000);
 		}


 	}
 	accelX = 0;
 	accelY = 0;
 	accelZ = 0;
 }

 function setDirection(){  // 사용할 가속도 방향을 정함
 	Direction=0;
 	if( 0 < averageX && 0 < averageY &&  0 > averageZ ){
 		Direction=1;
 	}
 	if( 0 < averageX && 0 > averageY &&  0 < averageZ){
 		Direction=2;
 	}
 	if( 0 < averageX && 0 > averageY &&  0 > averageZ){
 		Direction=3;
 	}
 	if( 0 > averageX && 0 < averageY &&  0 < averageZ){
 		Direction=4;
 	}
 	if( 0 > averageX && 0 < averageY &&  0 > averageZ){
 		Direction=5;
 	}
 	if( 0 > averageX && 0 > averageY &&  0 < averageZ){
 		Direction=6;
 	}
 	if( 0 > averageX && 0 > averageY &&  0 > averageZ){
 		Direction=7;
 	}
 }

 function checkDirection(){
 	var direction = 0;
 	if( 0 < accelX && 0 < accelY &&  0 > accelZ ){
 		direction=1;
 	}
 	if( 0 < accelX && 0 > accelY &&  0 < accelZ){
 		direction=2;
 	}
 	if( 0 < accelX && 0 > accelY &&  0 > accelZ){
 		direction=3;
 	}
 	if( 0 > accelX && 0 < accelY &&  0 < accelZ){
 		direction=4;
 	}
 	if( 0 > accelX && 0 < accelY &&  0 > accelZ){
 		direction=5;
 	}
 	if( 0 > accelX && 0 > accelY &&  0 < accelZ){
 		direction=6;
 	}
 	if( 0 > accelX && 0 > accelY &&  0 > accelZ){
 		direction=7;
 	}

 	return direction;
 }



 /*
  * changeButton
  * 버튼의 활성화, 비활성화 효과 관리
  *
  *  */


 // start page
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

 function ready_motionSetting() {
 	if(averageX != 0 && averageY != 0 && averageZ != 0 ){
 		//모션이 저장되어있을 경우 해당페이지로 바로이동
 		$('#reset_button').removeAttr('disabled');
 		$('#motion_test_button').removeAttr('disabled');
     }
 	else
 	{
 		$('#ok_button').attr('disabled','disabled');
 		$('#reset_button').attr('disabled','disabled');
 		$('#motion_test_button').attr('disabled','disabled');
 		alert('같은동작을' +userInputIndex +'번 해주세요!');
 		arrayIndex=0;
 		accelX = 0;
 	    accelY = 0;
 	    accelZ = 0;
 	    document.getElementById('ok_button').style.backgroundImage= "url(./img/button/ic_check_disable.png)"
 	}
 	save_setLocalStorage();
 	changeButtonEnrollMotion();
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
 	$('#ok_button').attr('disabled','disabled');
 	$('#reset_button').removeAttr('disabled');
 	$('#motion_test_button').removeAttr('disabled');
 }
 function modify_motionSetting() {
 	alert('오른쪽 넘김을 위한 같은동작을' + userInputIndex + '번 해주세요!');
 	$('#ok_button').attr('disabled','disabled');
 	$('#reset_button').attr('disabled','disabled');
 	$('#motion_test_button').attr('disabled','disabled');
 	/* test_right_motion_btn, test_left_motion_btn에 disabled 속성 추가 */
 	check = 0;
 	document.getElementById("enable_motion").innerHTML="Off";
 	motion_test=0;
 	document.getElementById("test_motion").innerHTML="Off";
 	arrayIndex=0;
 	accelX = 0;
     accelY = 0;
     accelZ = 0;
     averageX=0;
 	averageY=0;
 	averageZ=0;
 	Direction=0;

 	save_setLocalStorage();
 	changeButtonEnrollMotion();
 }

 function main_to_back(){
 	check=0;   //메인화면 에서 나가면 저장된 모션을 동작하지 않도록 하기 위함
 	document.getElementById("enable_motion").innerHTML="Off";
 	changeButtonStart();
 	changeButtonEnrollMotion();
 }


 function enable_motion(){
 	if(averageX != 0 && averageY != 0 && averageZ != 0){        //motion이 설정되어있을 때 motion설정을 on/off가능

 		if(check==0)
 		{
 		  startSettingButtonClickEffect();
 			document.getElementById("enable_motion").innerHTML="On";
 			accelX = 0;
      accelY = 0;
      accelZ = 0;
 			check=1;
 		}
 		else
 		{
 		  startSettingButtonClickEffect();
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
 			           					window.localStorage['arrayIndex'] == null &&
 			           								window.localStorage['Direction'] == null)
 	{
 		alert('Motion을 사용하려면 모션을 등록하셔주세요!');
 		//어플시작!
 	}
 	else if(window.localStorage['averageX'] == 0 &&
 			  	window.localStorage['averageY'] == 0 &&
 			  		window.localStorage['averageZ'] == 0 &&
 			  				window.localStorage['arrayIndex'] == 0 &&
 			  						window.localStorage['Direction'] == 0)
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
 		Direction=Number(window.localStorage['Direction']);;
 		console.log(averageX);
 		console.log(averageY);
 		console.log(averageZ);
 		console.log(arrayIndex);
 		console.log(Direction);
 		alert('Motion 등록이 되어있습니다.!');
 	}
 }

 function save_setLocalStorage(){
 	window.localStorage['averageX']=averageX;
 	window.localStorage['averageY']=averageY;
 	window.localStorage['averageZ']=averageZ;
 	window.localStorage['Direction']=Direction;
 	window.localStorage['arrayIndex']=arrayIndex;
 }

 function test_motion_on_off(){

 	if(motion_test==0)
 	{
 		document.getElementById("test_motion").innerHTML="On";
 		motion_test=1;
 		check = 0;

 	}
 	else
 	{
 		document.getElementById("test_motion").innerHTML="Off";
 		motion_test=0;
 	}
 	changeButtonEnrollMotion();
 }

 //Initialize function
 window.onload = function () {
     // TODO:: Do your initialization job
 	window.addEventListener("devicemotion", onDeviceMotion, true);
 	load_setLocalStorage();
     console.log("init() called");
 };
