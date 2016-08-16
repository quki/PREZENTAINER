/* global define, console, window, document, history, tizen */

/**
 * Init page module
 */

define({
	name : 'views/init',
	requires : [ 'core/application', 'views/main' ],
	def : function viewsInit(req) {
		'use strict';

		var app = req.core.application;

		/**
		 * Handles tizenhwkey event. backKey
		 */
		function onHardwareKeysTap(ev) {
			var keyName = ev.keyName, page = document
					.getElementsByClassName('ui-page-active')[0], pageid = (page && page.id)
					|| '';

			if (keyName === 'back') {
				if (pageid === 'main') {
					tizen.power.release("SCREEN");
					app.exit();
				} else if (pageid === 'enrollMotion') {
					motion_test = 0; // 설정화면에서 나가면 저장된 모션을 동작하지 않도록 하기 위함
					document.getElementById("test_motion").innerHTML = "Off";
					history.back();
				} else if (pageid === 'start') {
					main_to_back();
					history.back();
				}
			}
		}

		/**
		 * Registers event listeners.
		 */
		function bindEvents() {
			document.addEventListener('tizenhwkey', onHardwareKeysTap);

		}

		/**
		 * Inits module.
		 */
		function init() {
			bindEvents();
			tizen.power.request("SCREEN", "SCREEN_NORMAL");
		}

		return {
			init : init
		};
	}

});
