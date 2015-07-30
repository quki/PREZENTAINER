/* global define, console, window, document, history, tizen */

/**
 * Init page module
 */

define({
  name: 'views/init',
  requires: ['core/application', 'views/main'],
  def: function viewsInit(req) {
    'use strict';

    var app = req.core.application;

    /**
     * Handles tizenhwkey event. backKey
     * 
     * @param {event}
     *          ev
     */
    function onHardwareKeysTap(ev) {
      var keyName = ev.keyName, page = document
              .getElementsByClassName('ui-page-active')[0], pageid = (page && page.id)
              || '';

      if (keyName === 'back') {
        if (pageid === 'main') {
          app.exit();
        } else {
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
    }

    return {
      init: init
    };
  }

});
