/*global define*/
/*jslint plusplus: true*/

/**
 * Helpers module
 */

define({
    name: 'helpers/timer',
    def: function helpersTimer() {
        'use strict';

        /**
         * Divisor table used by the splitTime.
         *
         * The array describes the ratio between two consecutive digits
         * on the stopwatch.
         * Eg. the 3rd digit (tens of minutes) described by divs[2]
         * is 6 times less worth than the 2nd digit (ones of hours)
         * @type {array}
         */
        var divs = [10, 10, 6, 10, 6, 10, 10, 10];


        /**
         * Calculate digits for the timer
         *
         * @param {int} ms Milliseconds since the start.
         * @return {array} Int-indexed array with data for the stopwatch.
         */
        function Time(ms) {
            if (ms === undefined) {
                return;
            }
            var r = 0, i = divs.length;

            if (ms < 0) {
                throw new Error('Can\'t split time smaller than 0');
            }

            this.input = ms;

            r = Math.floor(ms / 10); // we're not interested in milliseconds

            while (i--) {
                // Calculates digits from right to the left, one at a time.
                //
                // 'r' is the remaining time in current units (eg. in seconds
                // on the 3rd interation or in minutes on the 5th one)
                // 'divs' describe the ratio between digits on the stopwatch.
                //
                // In order to get the current digit, the remainder 'r' from
                // the previous step is modulo-divided by the value (ratio)
                // of the next (higher) unit.
                r = (r - (this[i] = r % divs[i])) / divs[i];
            }

            this.length = divs.length;

            return this;

        }

        /**
         * Return value if it's truthy and 0 otherwise.
         *
         * @param {*} value
         * @return {*}
         */
        function getValue(value) {
            return value || 0;
        }

        Time.prototype = [];

        /**
         * Convert Time to a string.
         * @param {bool} [fullFormat=false] Full format.
         * @return {string}
         */
        Time.prototype.toString = function Time_toString(fullFormat) {
            fullFormat = fullFormat ? true : false;

            var str = '';
            str += getValue(this[0]);
            str += getValue(this[1]);
            str += ':';
            str += getValue(this[2]);
            str += getValue(this[3]);
            str += ':';
            str += getValue(this[4]);
            str += getValue(this[5]);

            if (fullFormat) {
                str += '.';
                str += getValue(this[6]);
                str += getValue(this[7]);
            }
            return str;
        };

        return {
            Time: Time
        };
    }
});
