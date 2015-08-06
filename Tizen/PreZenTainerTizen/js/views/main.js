/* global define, console, document, window, tau, setInterval, clearInterval */
/* jslint plusplus: true */

/**
 * Main page module
 */

define({
  name: 'views/main',
  requires: ['core/event', 'core/application', 'audios/stream', 'audios/audio','timers/timer','helpers/timer','core/template'],
  def: function viewsMain(req) {
    'use strict';

    var e = req.core.event,
        s = req.audios.stream,
        a = req.audios.audio,
        
        Timer = req.timers.timer.Timer,
        Time = req.helpers.timer.Time,
        tpl = req.core.template,
        page = null,
        pageId = 'main',
        timer = null,
        initialised = false,


    ERROR_FILE_WRITE = 'FILE_WRITE_ERR',
    NO_FREE_SPACE_MSG = 'No free space.',
    CANNOT_ACCESS_AUDIO_MSG = 'Cannot access audio stream. '+'Please close all applications that use the audio stream and '+'open the application again.',

    recordProgress = null,
    recordProgressVal = null,
    startbtn = null,
    stopbtn = null,
    lapbtn = null,
    stream = null,
    RECORDING_INTERVAL_STEP = 100,
    recordingInterval = null, isRecording = false, recordingTime = 0, exitInProgress = false;

    /**
     * Toggles between recording/no recording state.
     * 
     */

    // 처음시작할때 recording 상태로 만들어준다. ----> toggleRecording();
    function toggleRecording(forceValue) {
      if (forceValue !== undefined) {
        isRecording = !!forceValue; // 왜 이렇게 두개씩이나.
      } else {
        isRecording = !isRecording;
      }
    }

    /**
     * Renders recording progress bar value.
     * 
     */
    function renderRecordingProgressBarValue(value) {
      recordProgressVal.style.width = value + 'px';
    }

    /**
     * Renders recording progress bar.
     */
    function renderRecordingProgressBar() {
      var parentWidth = recordProgress.clientWidth, width = recordingTime
              / a.MAX_RECORDING_TIME * parentWidth;
      renderRecordingProgressBarValue(width);
    }

    /**
     * Resets recording progress.
     */
    function resetRecordingProgress() {
      recordingTime = 0;
      renderRecordingProgressBar();
    }

    /**
     * Removes recording interval.
     */
    function removeRecordingInterval() {
      clearInterval(recordingInterval);
    }

    /**
     * Updates recording progress.
     */
    function updateRecordingProgress() {
      recordingTime = a.getRecordingTime();

      renderRecordingProgressBar();
    }

    /**
     * Sets recording interval.
     */
    function setRecordingInterval() {
      recordingInterval = setInterval(updateRecordingProgress,
              RECORDING_INTERVAL_STEP);
    }

    /**
     * Starts audio recording.
     */
    function startRecording() {
      a.startRecording();
      resetRecordingProgress();
    }

    /**
     * Stops audio recording.
     */
    function stopRecording() {
      a.stopRecording();
      resetRecordingProgress();
      isRecording = false;
    }

    /**
     * Starts or stops audio recording.
     */
    function setStart() {
      if (!isRecording) {
        startRecording();
        startHR();
        startTimer();
        isRecording = true;
        toastAlert('Start !');
      } else {
        alert("StopButton을 이용해서 끄세요!");
      }
    }
    function setStop(){
      if (isRecording) {
        sendJsonHR();
        stopRecording();
        stopHR();
        stopTimer();
      }
    }

    /**
     * Handles click event on record button.
     */
    function onStartBtnClick() {
      startTimeWatch();
      updateAfterStart();
      setStart();
    }

    function onStopBtnClick() {
      stopTimeWatch();
      setStop();
      updateAfterStop();
    }
    
    function lap(){
      try {
        var currentLap = timer.lap(),
        html,
        tmptable = null,
        newitem = null;
        html = tpl.get('lapRow', {
          no: currentLap.no > 9 ? currentLap.no : '0' + currentLap.no,
          totalTime: new Time(timer.getTimeElapsed()),
          lapTime: new Time(currentLap.time)
      });
        tmptable = document.createElement('table');
        tmptable.innerHTML = html;
      } catch (e) {
        console.error(e);
      }
      
    }

    /**
     * Registers event listeners.
     */
    function bindEvents() {
      startbtn.addEventListener('click', onStartBtnClick);
      stopbtn.addEventListener('click', onStopBtnClick);
      lapbtn.addEventListener('click',lap);
    }

    /**
     * Handles models.stream.ready event.
     * 
     */
    function onStreamReady(ev) {
      stream = ev.detail.stream;
      a.registerStream(stream);
    }

    /**
     * Handles models.stream.cannot.access.audio event.
     */
    function onStreamCannotAccessAudio() {
      if (document.visibilityState === 'visible') {
        showExitAlert(CANNOT_ACCESS_AUDIO_MSG);
      }
    }

    /**
     * Inits stream.
     */
    function initStream() {
      s.getStream();
    }

    /**
     * Handles audio.ready event.
     */
    function onAudioReady() {
      console.log('onAudioReady()');
    }

    /**
     * Handles audio.error event.
     */
    function onAudioError() {
      console.error('onAudioError()');
    }

    /**
     * Handles audio.recording.start event.
     */
    function onRecordingStart() {
      setRecordingInterval();
      toggleRecording(true);
    }

    /**
     * Handles audio.recording.done event.
     * 
     */
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

    /**
     * Handles audio.recording.cancel event.
     */
    function onRecordingCancel() {
      toggleRecording(false);
    }

    /**
     * Handles audio.recording.error event.
     * 
     */
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

    /**
     * Handles application exit event.
     */
    function onApplicationExit() {
      exitInProgress = true;
      if (a.isReady()) {
        a.release();
        stream.stop();
      }
    }

    /**
     * Inits module.
     */
    function init() {
      startbtn = document.getElementById('startbtn');
      stopbtn = document.getElementById('stopbtn');
      recordProgress = document.getElementById('record-progress');
      recordProgressVal = document.getElementById('record-progress-val');
      lapbtn = document.getElementById('lapbtn');
      bindEvents();
      initStream();
      bindPageShow();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
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
     * Initialise the stopwatch - timer and events.
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

    function pageShow() {
        initStopWatch();
        // scroll laps list to previous position
    }

    /**
     * Bind the pageshow event.
     */
    function bindPageShow() {
        page = page || document.getElementById(pageId);

        page.addEventListener('pageshow', pageShow);

        if (page.classList.contains('ui-page')) {
            // the page is already active and the handler didn't run
            pageShow();
        }
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
