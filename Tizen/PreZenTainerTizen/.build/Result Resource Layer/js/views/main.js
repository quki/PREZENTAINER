  

  /* *
   *     < Main Page Module >
   * 1. Recording 관련 함수를 정의한다. 
   * 2. Stop watch 관련 함수를 정의한다.
   * 3. 모든 메인페이지에서 일어나는 이벤트를 처리한다. 
   * 
   * */

define({
  name: 'views/main',
  requires: ['core/event', 'core/application', 'audios/stream', 'audios/audio','timers/timer','helpers/timer'],
  def: function viewsMain(req) {
    'use strict';

    var e = req.core.event,
        s = req.audios.stream,
        a = req.audios.audio,
        Timer = req.timers.timer.Timer,
        Time = req.helpers.timer.Time,
        
        page = null,
        pageId = 'main',
        timer = null,
        initialised = false,
        startbtn = null,
        stopbtn = null,
        pceventbtn = null,

    ERROR_FILE_WRITE = 'FILE_WRITE_ERR',
    NO_FREE_SPACE_MSG = 'No free space.',
    CANNOT_ACCESS_AUDIO_MSG = 'Cannot access audio stream. '+'Please close all applications that use the audio stream and '+'open the application again.',

    recordProgress = null,
    recordProgressVal = null,
    
    stream = null,
    RECORDING_INTERVAL_STEP = 100,
    recordingInterval = null, isRecording = false, recordingTime = 0, exitInProgress = false;

    var rightEventTimeArray = new Array(),
        leftEventTimeArray =  new Array();
    //////////////////////////////////////////////////RECORDING////////////////////////////////////////////////////////////////////////
    
    // 처음시작할때 recording 상태로 만들어준다. ----> toggleRecording();
    function toggleRecording(forceValue) {
      if (forceValue !== undefined) {
        isRecording = !!forceValue; // 왜 이렇게 두개씩이나.
      } else {
        isRecording = !isRecording;
      }
    }

    // Recording ProgressBar 렌더링 값 설정
    function renderRecordingProgressBarValue(value) {
      recordProgressVal.style.width = value + 'px';
    }

    // Recording ProgressBar 렌더링 작업
    function renderRecordingProgressBar() {
      var parentWidth = recordProgress.clientWidth, width = recordingTime
              / a.MAX_RECORDING_TIME * parentWidth;
      renderRecordingProgressBarValue(width);
    }

    // Reset Recording ProgressBar 
    function resetRecordingProgress() {
      recordingTime = 0;
      renderRecordingProgressBar();
    }

    // Remove Recording ProgressBar Interval 
    function removeRecordingInterval() {
      clearInterval(recordingInterval);
    }

    // Update Recording ProgressBar
    function updateRecordingProgress() {
      recordingTime = a.getRecordingTime();

      renderRecordingProgressBar();
    }

    // Sets recording interval
    function setRecordingInterval() {
      recordingInterval = setInterval(updateRecordingProgress,
              RECORDING_INTERVAL_STEP);
    }

    // Starts audio recording
    function startRecording() {
      a.startRecording();
      resetRecordingProgress();
    }

    // Stops audio recording
    function stopRecording() {
      a.stopRecording();
      resetRecordingProgress();
      isRecording = false;
    }

    // Handles models.stream.ready event
    function onStreamReady(ev) {
      stream = ev.detail.stream;
      a.registerStream(stream);
    }

    // Handles models.stream.cannot.access.audio event
    function onStreamCannotAccessAudio() {
      if (document.visibilityState === 'visible') {
        alert(CANNOT_ACCESS_AUDIO_MSG);
      }
    }

    // Initialize Stream
    function initStream() {
      s.getStream();
    }

    // Handles audio.ready event
    function onAudioReady() {
      console.log('onAudioReady()');
    }

    // Handles audio.error event
    function onAudioError() {
      console.error('onAudioError()');
    }

    // Handles audio.recording.start event
    function onRecordingStart() {
      setRecordingInterval();
      toggleRecording(true);
    }

    // Handles audio.recording.done event
    function onRecordingDone(ev) {
      var path = ev.detail.path;

      removeRecordingInterval();
      toggleRecording(false);
      updateRecordingProgress();
      if (!exitInProgress) {
        e.fire('show.preview', {
          audio: path
        });
      }
    }

    // Handles audio.recording.cancel event
    function onRecordingCancel() {
      toggleRecording(false);
    }

    // Handles audio.recording.error event
    function onRecordingError(ev) {
      var error = ev.detail.error;

      if (error === ERROR_FILE_WRITE) {
        console.error(NO_FREE_SPACE_MSG);
      } else {
        console.error('Error: ' + error);
      }

      removeRecordingInterval();
      toggleRecording(false);
    }

    // Handles application exit event
    function onApplicationExit() {
      exitInProgress = true;
      if (a.isReady()) {
        a.release();
        stream.stop();
      }
    }
    
    //////////////////////////////////////////////////STOP WATCH///////////////////////////////////////////////////////////////////////
    
    /**
     * Refresh timer digits.
     *
     * @return {array} Array of digits.
     */
    function refreshTimer() {
        /**
         * Array of digits
         * @type {array}
         */
        var time = new Time(timer.getTimeElapsed()),
            i,
            element;

        for (i = time.length - 1; i >= 0; i -= 1) {
            element = document.getElementById('d' + i);
            element.classList.remove.apply(
                element.classList,
                [
                    'd0',
                    'd1',
                    'd2',
                    'd3',
                    'd4',
                    'd5',
                    'd6',
                    'd7',
                    'd8',
                    'd9'
                ]
            );
            element.classList.add('d' + time[i]);
        }
        return time;
    }
    
    /*
     * 
     * JSON Object를 생성해서 right / left를 구분
     * 
     * { 

        "right" : [ 3972,6794,12456,19789 ],

​         "left" : [4524,10032,15764]

        }

     * 
     * 
     */
    
    // Event가 일어난 시점(milliseconds)을 배열에 넣는다. ex [6294,8254,10234]
    function pushEventTimeToArray(direction){
      var currentEventTime = timer.getTimeElapsed();
      
      if(direction === "right"){
        
        rightEventTimeArray.push(currentEventTime);
        console.log("rightEventTimeArray : "+rightEventTimeArray);
        
      }else if(direction === "leftt"){
        
        leftEventTimeArray.push(currentEventTime);
        console.log("leftEventTimeArray : "+leftEventTimeArray);
        
      }
    }
    
    
    function makeJsonObjEventTime(){
      var eventTimeObject = new Object();
      eventTimeObject.right = rightEventTimeArray;
      eventTimeObject.left = leftEventTimeArray;
      return eventTimeObject;
    }
    
  //time event JSON을 ANDROID로 보내기
    function sendJsonEventTime(){
      
      try {
          var jsonInfoET = JSON.stringify(makeJsonObjEventTime());
          mSASocket.sendData(CHANNELID_EVENTTIME, jsonInfoET);
          console.log("Event Time sent : " + jsonInfoET);
          // JSON Array Initialize...
          rightEventTimeArray = [];
          leftEventTimeArray = [];
          console.log("eventTimeArray Initialize Success : " + rightEventTimeArray + leftEventTimeArray);
      } catch (err) {
        console.log("exception [" + err.name + "] msg[" + err.message + "]");
      }
      
    }
    
    function startTimeWatch(e){
      //e.preventDefault();
      timer.run();
    }
    
    function stopTimeWatch(e){
      //e.preventDefault();
      timer.reset();
      //window.scrollTo(0);
      refreshTimer();
    }


    /**
     * Initialize the stopwatch - timer and events.
     *
     * @return {boolean} True if any action was performed.
     */
    function initStopWatch() {
        if (initialised) {
            return false;
        }
        // init model
        timer = new Timer(10, 'tick');

        // init UI by binding events

        initialised = true;
        return true;
    }

    
    //////////////////////////////////////////////////SETTINGS/////////////////////////////////////////////////////////////////////////    
    
    // 각종 Start 설정
    function setStart() {
      if (!isRecording) {
        toastAlert('Start !');
        startRecording();
        startHR();
        startTimer();
        isRecording = true;
      } else {
        toastAlert('StopButton을 이용해서 끄세요 !');
      }
    }
    
    // 각종 Stop 설정
    function setStop(){
      if (isRecording) {
        toastAlert('Stop !');
        sendJsonHR();
        sendJsonEventTime();
        stopRecording();
        stopHR();
        stopTimer();
        disconnectSAP();
        //코드수정
        currentSlide = 0; //현재슬라이드 0으로 초기화
        //
      }
    }

    // Button Click Event
    function onStartBtnClick() {
      startTimeWatch();
      updateAfterStart();
      setStart();
      // pointer로 pushEventTimeToArray함수 메모리 공간 참조
      p_pushEventTimeToArray = pushEventTimeToArray; 
    }

    function onStopBtnClick() {
      stopTimeWatch();
      setStop();
      updateAfterStop();
    }
    
    function onPcEventBtnClick(){
      eventtopc("right");
    }

    // Registers event listeners
    function bindEvents() {
      startbtn.addEventListener('click', onStartBtnClick);
      stopbtn.addEventListener('click', onStopBtnClick);
      pceventbtn.addEventListener('click', onPcEventBtnClick);
    }
    
    // Initialize modules
    function init() {
      startbtn = document.getElementById('startbtn');
      stopbtn = document.getElementById('stopbtn');
      pceventbtn = document.getElementById('pceventbtn');
      recordProgress = document.getElementById('record-progress');
      recordProgressVal = document.getElementById('record-progress-val');
      bindEvents();
      initStream();
      initStopWatch();
    }
    
    /** 
     *  명령이 떨어지면 onAudioReady callback 함수 호출!!
     */
    e.listeners({
      'application.exit': onApplicationExit,

      'audios.stream.ready': onStreamReady,
      'audios.stream.cannot.access.audio': onStreamCannotAccessAudio,

      'audios.audio.ready': onAudioReady,
      'audios.audio.error': onAudioError,

      'audios.audio.recording.start': onRecordingStart,
      'audios.audio.recording.done': onRecordingDone,
      'audios.audio.recording.error': onRecordingError,
      'audios.audio.recording.cancel': onRecordingCancel,
      
      'timers.timer.tick': refreshTimer

    });

    return {
      init: init
    };
  }

});
