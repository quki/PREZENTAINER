
/**
 * Get Stream module
 */

define({
    name: 'audios/stream',
    requires: [
        'core/event'
    ],
    def: function modelsStream(e) {
        'use strict';

        var INIT_MAX_ATTEMPTS = 3,
            INIT_ATTEMPT_TIMEOUT = 500,

            initAttemtps = 0;

        
        // webkitGetUserMedia를 통해 Stream 얻음, audio만 capture함
        function getStream() {
            initAttemtps += 1;
            navigator.webkitGetUserMedia(
                {
                    video: false,
                    audio: true
                },
                onUserMediaSuccess,
                onUserMediaError
            );
        }

        // Stream을 정상적으로 얻었을때 stream ready!
        function onUserMediaSuccess(stream) {
            initAttemtps = 0;
            e.fire('ready', {stream: stream});
        }

        // Stream을 얻는 것을 실패 했을 때 3번 시도함
        function onUserMediaError() {
            if (initAttemtps < INIT_MAX_ATTEMPTS) {
                window.setTimeout(getStream,
                    INIT_ATTEMPT_TIMEOUT);
            } else {
                initAttemtps = 0;
                e.fire('cannot.access.audio');
            }
        }

        return {
            getStream: getStream
        };
    }

});
