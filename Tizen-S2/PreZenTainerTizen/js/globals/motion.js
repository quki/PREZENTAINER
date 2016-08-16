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
 


 function showResult() {   //모션등록할때 동작을 몇번 행했는지 나타내주는 역할을 함.
    $("#count").html(arrayIndex+"/"+userInputIndex);
 }

 function onDeviceMotion(e) {       //실시간으로 모션을 인식해서 저장된 모션에 따른 동작을 실행하는 부분임.(가속도를 이용해 모션을 제어함.)
    //x,y,z축 가속도의 최대값을 측정함.
    if( Math.abs(accelX) < Math.abs(e.acceleration.x) )  
         accelX = e.acceleration.x;
     if( Math.abs(accelY) < Math.abs(e.acceleration.y) )
         accelY = e.acceleration.y;
     if( Math.abs(accelZ) < Math.abs(e.acceleration.z) )
         accelZ = e.acceleration.z;

     //어느정도 움직였을 때 확인버튼을 활성화 시킴.(사용자에게 움직임을 유도하는 역할을 함.)
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
       $('#ok_button').attr('disabled','disabled'); //확인버튼을 눌렀을 때 버튼을 비활성화 시킴. 어느정도 움직였을 때 다시 활성화 시켜서 사용자의 움직임을 유도함.(onDeviceMotion함수에 구현되어 있음.)
       document.getElementById('ok_button').style.backgroundImage= "url(./img/button/ic_check_disable.png)";
       if (arrayIndex == userInputIndex) { //일정 횟수의 동작을 모두 수행했을 경우.
          
          //측정된 모션의 평균을 구하기 위해 sum변수를 선언함.
          var sumX=0;
          var sumY =0;
          var sumZ =0;
          
          //각 모션사이의 차이값을 측정하기 위한 변후임. 차이가 너무 크다면 모션을 다시 등록하도록 하기위함.
          var gapSumX=0;
          var gapSumY=0;
          var gapSumZ=0;

          for (var i = 0; i < userInputIndex-1; i++) {    //일정하게 휘둘렀는지 체크하기 위하여 각 모션 측정값들의 차이를 구함.
             gapSumX += Math.abs(Number(accelXArr[i])-Number(accelXArr[i+1]));
             gapSumY += Math.abs(Number(accelYArr[i])-Number(accelYArr[i+1]));
             gapSumZ += Math.abs(Number(accelZArr[i])-Number(accelZArr[i+1]));
             console.log("sum : " + sumX + " ," + sumY + " ," + sumZ);
          }

          if(gapSumX<50 && gapSumY<50 && gapSumZ<50 )        //일정하게 휘둘렀다면 (차이 값들의 합이 어느정도 값 이하라면)
          {
             for (var i = 0; i < userInputIndex; i++) { //각 값들의 합을 구함.
                sumX += Number(accelXArr[i]);
                sumY += Number(accelYArr[i]);
                sumZ += Number(accelZArr[i]);
                console.log("sum : " + sumX + " ," + sumY + " ," + sumZ);
             }
                averageX = sumX/userInputIndex;   //sum값을 이용해 평균값을 구함.
                averageY = sumY/userInputIndex;
                averageZ = sumZ/userInputIndex;
                setDirection();           //모션의 정확도를 주기 위해서 3가지 방향의 가속도의 부호를 측정해  Direction변수에 0~7사이의 값으로 저장함.            
                finish_motionSetting();   //모션 저장을 완료하면서 해야할 일들을 처리함.
                alert('설정 완료!');
          }
          else
          {
             retry_motionSetting(); //일정하게 휘두르지 않았더라면 다시 설정하도록 함.
          }
       }
    }
    changeButtonEnrollMotion();
    console.log("average : " + averageX + " ," + averageY + " ," + averageZ);
 }

 function motionSensor() {
    //측정한 가속도값들의 절대값 이상이면 이벤트를 발생시킴.
    if (Math.abs(averageX)-3 < Math.abs(accelX) &&           
       Math.abs(averageY)-3 < Math.abs(accelY) &&
       Math.abs(averageZ)-3 < Math.abs(accelZ) &&
       motion_check == 0) {
        
      // if(checkDirection()== Direction){  //모션의 가속도 방향체크.(모션 디테일을 위함)
        console.log('call right function');
        navigator.vibrate(500);  //모션이 인식되었는지 사용자가 알수 있게 하기 위해 0.5초 정도의 진동을 주어서 알려줌.
        eventtopc("right"); //오른쪽 이벤트!
        motion_check=1;     //오른쪽 이벤트를 발생시키고나면 lock을 걸어줌. 이부분이 없다면 중복호출문제가 발생함. lock은 안드로이드에서 신호를 받으면 1초뒤에 풀어줌.
      // }
    }
    accelX = 0;
    accelY = 0;
    accelZ = 0;
 }

 function test_motionSensor() {
   //측정한 가속도값을의 절대값 이상이면 이벤트를 발생시킴.
    if (Math.abs(averageX)-3 < Math.abs(accelX) &&  
       Math.abs(averageY)-3 < Math.abs(accelY) &&
       Math.abs(averageZ)-3 < Math.abs(accelZ) )
    {
      // if(checkDirection() == Direction){ //모션의 가속도 방향체크.(모션 디테일을 위함)
          console.log('Direction:'+Direction);
          navigator.vibrate(1000);
      // }


    }
    accelX = 0;
    accelY = 0;
    accelZ = 0;
 }

 function setDirection(){  // 사용할 가속도 방향을 정해서 설정함. (0~7까지 총 8가지 경우의 수,모션인식의 디테일을 높이기 위함.)
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

 function checkDirection(){  //가속도 방향을 실시간체크하여 이 함수의 리턴값을 전역변수인 Direction와 비교함.
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

 function ready_motionSetting() {
    if(averageX != 0 && averageY != 0 && averageZ != 0 ){
       //모션이 저장되어있을 경우 해당페이지로 바로이동
       $('#reset_button').removeAttr('disabled');
       $('#motion_test_button').removeAttr('disabled');
     }
    else //모션이 저장되어 있지 않을 경우
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
    save_setLocalStorage(); //모션을 설정하는 도중에 어플을 꺼버릴 경우 localStorage에 설정되지 않았다고 알려줘야 하기 떄문에 이 함수를 호출한다.
    changeButtonEnrollMotion();
 }
 function retry_motionSetting() { //모션 설정중에 error가 있을 경우 수행되는 함수.
    alert('모션이 일정하지 않습니다.! 다시입력!');
    $('#ok_button').attr('disabled','disabled');
    arrayIndex=0;
     accelX = 0;
     accelY = 0;
     accelZ = 0;
 }
 function finish_motionSetting() { //모션 설정이 완료 되었을 때 호출되는 함수.
    alert('완료되었습니다!');
    console.log(averageX);
    console.log(averageY);
    console.log(averageZ);
    console.log(arrayIndex);
    save_setLocalStorage();
    $('#ok_button').attr('disabled','disabled'); //모션 설정이 완료되면 확인버튼은 비활성화 되도록 함.
    $('#reset_button').removeAttr('disabled');   //모션 설정이 완료되면 재설정 버튼은 활성화 되도록 함. 
    $('#motion_test_button').removeAttr('disabled'); //모션 설정이 완료되면 테스트 버튼이 활성화 되도록 함.
 }
 function modify_motionSetting() { //재설정 버튼을 눌렀을 때 수행되는 함수.
    alert('오른쪽 넘김을 위한 같은동작을' + userInputIndex + '번 해주세요!');
    $('#ok_button').attr('disabled','disabled');
    $('#reset_button').attr('disabled','disabled');
    $('#motion_test_button').attr('disabled','disabled');
    /* test_right_motion_btn, test_left_motion_btn에 disabled 속성 추가 */
    
    
    //재설정 버튼을 누르면 모든 변수들 초기화 시킴.
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

    save_setLocalStorage(); //모션을 설정하는 도중에 어플을 꺼버릴 경우 localStorage에 설정되지 않았다고 알려줘야 하기 떄문에 이 함수를 호출한다.
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

       if(check==0) //모션이 OFF상태에서 on상태로 바꾸려고 할때
       {
         startSettingButtonClickEffect();
          document.getElementById("enable_motion").innerHTML="On";
          accelX = 0;
          accelY = 0;
          accelZ = 0;
          check=1; //check=1이 되면 모션센서가 동작함.
       }
       else
       {
         startSettingButtonClickEffect();
          document.getElementById("enable_motion").innerHTML="Off";
          check=0; //check=0이 되면 모션센서가 동작안함.
       }

    }
    else{
       alert('Motion 등록을 먼저해주세요!');
    }

 }
 function load_setLocalStorage(){
    //어플이 처음 설치되었을 때 localStorage는 비어 있기 때문에 해당 변수들이 null값으로 초기화 되어있음. 그 경우에 대한 if문처리.
    if(window.localStorage['averageX'] == null &&
            window.localStorage['averageY'] == null &&
                     window.localStorage['averageZ'] == null &&
                                    window.localStorage['arrayIndex'] == null &&
                                             window.localStorage['Direction'] == null)
    {
       alert('Motion을 사용하려면 모션을 등록하셔주세요!');
       //어플시작!
    }
    //어플이 한번 설치된 이후에는 어플 실행시에 save_setLocalStorage()함수가 최소 1번 호출되기 때문에 모션이 등록 안되있을 시에 모두 0으로 초기화 시켜 놓았음. 따라서 그에 따른 if문 처리임.
    else if(window.localStorage['averageX'] == 0 &&
               window.localStorage['averageY'] == 0 &&
                  window.localStorage['averageZ'] == 0 &&
                        window.localStorage['arrayIndex'] == 0 &&
                              window.localStorage['Direction'] == 0)
    {
       alert('Motion을 사용하려면 모션을 등록하셔주세요!');
       //어플시작!
    }
    //localStorage에 값이 있다는 것은 모션이 등록되어 있다는 뜻이므로 localStorage에 있는 값을 프로그램이 시작될 때 프로그램 변수로 가져와 초기화 시킴.
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

 function save_setLocalStorage(){   //localStorage에 평균가속도값들, 가속도 방향 변수 , arrayIndex를 저장함. 
    window.localStorage['averageX']=averageX;
    window.localStorage['averageY']=averageY;
    window.localStorage['averageZ']=averageZ;
    window.localStorage['Direction']=Direction;
    window.localStorage['arrayIndex']=arrayIndex; //arrayIndex를 저장하는 이유는 모션이 등록되어있을 경우 "3/3" 이런식으로 모션이 등록되어있다는 것을 시각적으로 표현해주기위함.
 }

 function test_motion_on_off(){ //test모드를 동작시키는 함수.

    if(motion_test==0)//motion_test=0일때 버튼을 누르면 test모드를 on 시킴.
    {
       document.getElementById("test_motion").innerHTML="On";
       motion_test=1;
       check = 0;

    }
    else //motion_test=1일때 버튼을 누르면 test모드를 off 시킴.
    {
       document.getElementById("test_motion").innerHTML="Off";
       motion_test=0;
    }
    changeButtonEnrollMotion();
 }
 
 //Initialize function
 window.onload = function () {
    window.addEventListener("devicemotion", onDeviceMotion, true);
    

    // load_setLocalStorage();
     console.log("init() called");
 };
 
