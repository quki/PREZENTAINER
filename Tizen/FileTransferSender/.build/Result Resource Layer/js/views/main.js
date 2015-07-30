/* global define, console, document, window, tau, setInterval, clearInterval */
/* jslint plusplus: true */

/**
 * Main page module
 */

define({
  name: 'views/main',
  requires: ['core/event', 'core/application', 'models/stream', 'models/audio'],
  def: function viewsMain(req) {
    'use strict';

    var e = req.core.event, s = req.models.stream, a = req.models.audio,

    ERROR_FILE_WRITE = 'FILE_WRITE_ERR', NO_FREE_SPACE_MSG = 'No free space.', CANNOT_ACCESS_AUDIO_MSG = 'Cannot access audio stream. '
            + 'Please close all applications that use the audio stream and '
            + 'open the application again.',

    recordProgress = null, recordProgressVal = null, startbtn = null, stopbtn = null,

    stream = null,

    RECORDING_INTERVAL_STEP = 100,

    recordingInterval = null, isRecording = false, recordingTime = 0, exitInProgress = false;

    /**
     * Toggles between recording/no recording state.
     * 
     * @param {boolean}
     *          forceValue
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
     * @param {number}
     *          value
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
        alert("Heart Rate Start !");
        isRecording = true;
      } else {
        alert("StopButton을 이용해서 끄세요!");
      }
    }

    /**
     * Handles click event on record button.
     */
    function onStartBtnClick() {
      setStart();
    }

    function onStopBtnClick() {
      alert("onStopBtnClick");
      if (isRecording) {
        stopRecording();
        stopHR();
      }
    }

    /**
     * Registers event listeners.
     */
    function bindEvents() {
      startbtn.addEventListener('click', onStartBtnClick);
      stopbtn.addEventListener('click', onStopBtnClick);

    }

    /**
     * Handles models.stream.ready event.
     * 
     * @param {event}
     *          ev
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
     * @param {event}
     *          ev
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
     * @param {CustomEvent}
     *          ev
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
      bindEvents();
      initStream();
    }

    // 얘가 무슨 역할을 하는지 모르겠다. listeners라...event를 정의하는데 handler를 name에다가 대응하고 있다.예)
    // 'models.audio.ready' 명령이 떨어지면 onAudioReady callback함수 호출!!
    e.listeners({
      'application.exit': onApplicationExit,

      'models.stream.ready': onStreamReady,
      'models.stream.cannot.access.audio': onStreamCannotAccessAudio,

      'models.audio.ready': onAudioReady,
      'models.audio.error': onAudioError,

      'models.audio.recording.start': onRecordingStart,
      'models.audio.recording.done': onRecordingDone,
      'models.audio.recording.error': onRecordingError,
      'models.audio.recording.cancel': onRecordingCancel

    });

    return {
      init: init
    };
  }

});
