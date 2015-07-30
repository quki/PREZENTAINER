/*global define, console, navigator, window, setTimeout*/
/*jslint regexp: true*/

/**
 * Audio model module
 */

define({
    name: 'models/audio',
    requires: [
        'core/event',
        'helpers/date'
    ],
    def: function modelsAudio(req) {
        /*jshint maxstatements:42*/
        'use strict';

        var e = req.core.event,
            dateHelper = req.helpers.date,
            min = 30,
            MAX_RECORDING_TIME = min*60000,
            AUDIO_LENGTH_CHECK_INTERVAL = 10,

            AUDIO_DESTINATION_DIRECTORY = '/opt/usr/media/Sounds',
            mPath ='',
            audioControl = null,
            audioPath = '',
            audioLengthCheckInterval = null,
            audioRecordingStartTime = null,
            audioRecordingTime = 0,
            busy = false,  //busy가 뭘까
            stopRequested = false;
        
        // sendFile
        function sendFile(path) {
          ftSend(path, function(id) {
            console.log('Succeed to send file');
            transferId = id;
            tau.changePage('#sendPage');
            progressBarWidget = new tau.widget.CircleProgressBar(progressBar);
            progressBarWidget.value('0');
            
            if(SASocket != null){
              try {
                SASocket.close();
                SASocket = null;
                console.error('Success to close the socket');
              } catch (e) {
                console.error(e+'Cannot close the socket');
              }
            }
           
          }, function(err) {
            showMain('Failed to send File');
          });
        }

        /**
         * Executes when audio control is created from stream.
         * @param {audioControl} control
         */
        function onAudioControlCreated(control) {
            audioControl = control;
            e.fire('ready');
        }

        /**
         * Executes on audio control creation error
         * @param {object} error
         */
        function onAudioControlError(error) {
            console.error(error);
            e.fire('error', {error: error});
        }

        /**
         * Registers stream that audio controls.
         * @param {LocalMediaStream} mediaStream
         */
        function registerStream(mediaStream) {
            navigator.tizCamera.createCameraControl(
                mediaStream,
                onAudioControlCreated,
                onAudioControlError
            );
        }

        /**
         * Checks if audio length is greater then MAX_RECORDING_TIME.
         * If it does, recording will be stopped.
         */
        function checkAudioLength() {
            var currentTime = new Date();

            audioRecordingTime = currentTime - audioRecordingStartTime;
            
            //길이가 끝나면 종료.
            if (audioRecordingTime > MAX_RECORDING_TIME) {
                stopRecording();
            }
        }

        /**
         * Starts tracing audio length.
         * When audio length reaches MAX_RECORDING_TIME, recording
         * will be stopped automatically.
         */
        function startTracingAudioLength() {
            audioRecordingStartTime = new Date();
            audioLengthCheckInterval = window.setInterval(
                checkAudioLength,
                AUDIO_LENGTH_CHECK_INTERVAL
            );
        }

        /**
         * Stops tracing audio length.
         */
        function stopTracingAudioLength() {
            window.clearInterval(audioLengthCheckInterval);
            audioLengthCheckInterval = null;
        }

        /**
         * Executes when recording starts successfully.
         */
        function onRecordingStartSuccess() {
            startTracingAudioLength();
            e.fire('recording.start');
        }

        /**
         * Executes when error occurs during recording start.
         * @param {object} error
         */
        function onRecordingStartError(error) {
            busy = false;
            e.fire('recording.error', {error: error});
        }

        /**
         * Executes when audio settings are applied.
         */
        function onAudioSettingsApplied() {
            if (!stopRequested) {
                audioControl.recorder.start(
                    onRecordingStartSuccess,
                    onRecordingStartError
                );
            } else {
                e.fire('recording.cancel');
            }

        }

        /**
         * Executes when error occurs during applying audio settings
         * @param {object} error
         */
        function onAudioSettingsError(error) {
            console.error('settings.error');
            busy = false;
            e.fire('recording.error', {error: error});
        }

        /**
         * Returns recording format
         * @return {string}
         */
        function getRecordingFormat() {
            return 'amr';
        }

        /**
         * Creates filename for new audio.
         * @return {string}
         */
        function createAudioFileName() {
            var currentDate = new Date(),
                extension = getRecordingFormat(),
                fileName = '';

            fileName = dateHelper.format(currentDate, 'yyyymmdd_HHMMSS') +
                '.' + extension;

            return fileName;
        }

        /**
         * Starts audio recording.
         * When recording is started successfully, audio.recording.start event
         * is fired. If error occurs, audio.recording.error event is fired.
         * @return {boolean} If process starts true is returned,
         * false otherwise (audio other operation is in progress).
         */
        function startRecording() {
          
            alert("Recording Start !");
            var settings = {},
                fileName = '';

            if (busy) {
                return false;
            }

            stopRequested = false;
            busy = true;
            fileName = createAudioFileName();
            audioPath = AUDIO_DESTINATION_DIRECTORY + '/' + fileName;
            mPath = 'file:///opt/usr/media/Sounds/'+fileName;
            settings.fileName = fileName;
            settings.recordingFormat = getRecordingFormat();

            audioControl.recorder.applySettings(
                settings,
                onAudioSettingsApplied,
                onAudioSettingsError
            );

            return true;
        }

        /**
         * Executes when audio recording stops successfully.
         */
        function onAudioRecordingStopSuccess() {
            busy = false;
            alert("onAudioRecordingStopSuccess");
            e.fire('recording.done', {path: audioPath});
            audioRecordingTime = 0;
            
            //File Send to Android
            sendFile(mPath);
        }

        /**
         * Executes when audio recording stop fails.
         * @param {object} error
         */
        function onAudioRecordingStopError(error) {
            busy = false;
            e.fire('recording.error', {error: error});
            audioRecordingTime = 0;
        }

        /**
         * Stop audio recording.
         * When recording is stopped, audio.recording.done event is fired
         * with file path as a data.
         * If error occurs audio.recording error is fired.
         * Recording will stop also if MAX_RECORDING_TIME will be reached.
         */
        function stopRecording() {
            stopRequested = true;
            alert("stopRecording");
            
            //만일 Recording 중이었다면..
            if (isRecording()) {
                stopTracingAudioLength();
                audioControl.recorder.stop(
                    onAudioRecordingStopSuccess,
                    onAudioRecordingStopError
                );
            }

        }

        /**
         * Returns current recording time in milliseconds.
         * @return {number}
         */
        function getRecordingTime() {
            return audioRecordingTime;
        }

        /**
         * Releases audio.
         */
        function release() {
            if (busy) {
                stopRecording();
            }
            busy = false;
            if (audioControl) {
                audioControl.release();
                audioControl = null;
                e.fire('release');
            }
        }

        /**
         * Returns true if audio is ready to work,
         * false otherwise.
         * @return {boolean}
         */
        function isReady() {
            return audioControl !== null;
        }

        /**
         * Returns true if audio is recording,
         * false otherwise.
         * @return {boolean}
         */
        function isRecording() {
            return !!audioLengthCheckInterval;
        }

        return {
            MAX_RECORDING_TIME: MAX_RECORDING_TIME,

            registerStream: registerStream,
            release: release,
            isReady: isReady,
            isRecording: isRecording,

            startRecording: startRecording,
            stopRecording: stopRecording,
            getRecordingTime: getRecordingTime
        };
    }
});
