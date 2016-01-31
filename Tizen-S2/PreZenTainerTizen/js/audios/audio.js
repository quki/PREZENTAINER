/*global define, console, navigator, window, setTimeout*/
/*jslint regexp: true*/

/**
 * Audio model module
 */

define({
    name: 'audios/audio',
    requires: [
        'core/event',
        'helpers/date'
    ],
    def: function modelsAudio(req) {
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
            busy = false, 
            stopRequested = false;
        
        // SA File Transfer를 이용해 안드로이드로 Audio File을 보냄
        function sendFile(path) {
          ftSend(path, function(id) {
            console.log('Succeed to send file');
            transferId = id;
            tau.changePage('#sendPage');
            progressBarWidget = new tau.widget.CircleProgressBar(progressBar);
            progressBarWidget.value('0');
            
          }, function(err) {
            showMain('Failed to send File');
          });
        }

        // 준비된 Stream으로 부터 AudioControl 객체를 얻음
        function onAudioControlCreated(control) {
            audioControl = control;
            e.fire('ready');
        }

        // AudioControl 객체 생성 실패
        function onAudioControlError(error) {
            console.error(error);
            e.fire('error', {error: error});
        }

        // Stream Register - > Audio Control 객체 생성
        function registerStream(mediaStream) {
            navigator.tizCamera.createCameraControl(
                mediaStream,
                onAudioControlCreated,
                onAudioControlError
            );
        }

         // audio length가 MAX_RECORDING_TIME큰지 체크
        function checkAudioLength() {
            var currentTime = new Date();

            audioRecordingTime = currentTime - audioRecordingStartTime;
            
            if (audioRecordingTime > MAX_RECORDING_TIME) {
                stopRecording();
            }
        }

        // audio length 측정 MAX_RECORDING_TIME에 도달하면 stop됨
        function startTracingAudioLength() {
            audioRecordingStartTime = new Date();
            audioLengthCheckInterval = window.setInterval(
                checkAudioLength,
                AUDIO_LENGTH_CHECK_INTERVAL
            );
        }

        
        //audio length 측정 중지
        function stopTracingAudioLength() {
            window.clearInterval(audioLengthCheckInterval);
            audioLengthCheckInterval = null;
        }

        // audio start가 성공했을 때
        function onRecordingStartSuccess() {
            startTracingAudioLength();
            e.fire('recording.start');
        }

        // audio start에 실패했을 때
        function onRecordingStartError(error) {
            busy = false;
            e.fire('recording.error', {error: error});
        }

        // start 성공 뒤, audio 설정 성공
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

        // audio setting 설정 실패
        function onAudioSettingsError(error) {
            console.error('settings.error');
            busy = false;
            e.fire('recording.error', {error: error});
        }

        // 오디오 파일형식
        function getRecordingFormat() {
            return 'amr';
        }

        // 오디오 파일 이름 생성
        function createAudioFileName() {
            var currentDate = new Date(),
                extension = getRecordingFormat(),
                fileName = '';

            fileName = dateHelper.format(currentDate, 'yyyymmdd_HHMMSS') +
                '.' + extension;

            return fileName;
        }

        /**
         * audio recording 시작, applySettings을 통해 recording관련 설정을 하고
         * start가 성공하면 onAudioSettingsApplied 호출,
         * 실패하면, onAudioSettingsError 호출됨
         * 정상적으로 start되면 true return, 진행중이여서 start를 못하게 되면 false return.
         */
        function startRecording() {
          
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
            
            /* ex ) 
             * 
             *      settings = { 'filename' : '20151003_164332.amr',
             *                    'recordingFormat' : 'amr' }
             *              
             */
            
            // audio 관련 setting
            audioControl.recorder.applySettings(
                settings,
                onAudioSettingsApplied,
                onAudioSettingsError
            );

            return true;
        }

        // 녹음 중지에 성공했을 때
        function onAudioRecordingStopSuccess() {
            busy = false;
            e.fire('recording.done', {path: audioPath});
            audioRecordingTime = 0;
            //File Send to Android
            sendFile(mPath);
        }

        // 오디오 중지 실패
        function onAudioRecordingStopError(error) {
            busy = false;
            e.fire('recording.error', {error: error});
            audioRecordingTime = 0;
        }

        /**
         * audio recording 중지, 
         * stop이 성공하면 onAudioRecordingStopSuccess 호출,
         * 실패하면, onAudioRecordingStopError 호출됨
         */
        function stopRecording() {
            stopRequested = true;
            
            //만일 Recording 중이었다면..
            if (isRecording()) {
                stopTracingAudioLength();
                audioControl.recorder.stop(
                    onAudioRecordingStopSuccess,
                    onAudioRecordingStopError
                );
            }

        }

        // 현재 오디오 시간 return (ms)
        function getRecordingTime() {
            return audioRecordingTime;
        }

        // 일지중지
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

        // 녹음이 준비되면 true, 그렇지 않으면 false return
        function isReady() {
            return audioControl !== null;
        }

        // 녹음 중이면 true, 그렇지 않으면 false return
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
