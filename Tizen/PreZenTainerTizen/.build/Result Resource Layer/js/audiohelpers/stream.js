/*global define, console, window, navigator*/
/*jslint plusplus: true*/

/**
 * Month module
 */

define({
    name: 'audiohelpers/stream',
    requires: [
        'core/event'
    ],
    def: function modelsStream(e) {
        'use strict';

        var INIT_MAX_ATTEMPTS = 3,
            INIT_ATTEMPT_TIMEOUT = 500,

            initAttemtps = 0;

        /**
         * Gets media stream.
         */
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

        /**
         * Fires models.stream.ready event onGetUserMediaSuccess.
         * @param {LocalMediaStream} stream
         */
        function onUserMediaSuccess(stream) {
            initAttemtps = 0;
            e.fire('ready', {stream: stream});
        }

        /**
         * Fires models.stream.cannot.access.audio event on onGetUserMediaError.
         */
        function onUserMediaError() {
            if (initAttemtps < INIT_MAX_ATTEMPTS) {
                //application tries to obtain audio stream up to 3 times
                //because other application may not release it yet
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
