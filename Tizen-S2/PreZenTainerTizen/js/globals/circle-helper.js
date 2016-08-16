/*global tau, document */
/*jslint unparam: true */
(function helperWrapper() {
    'use strict';

    var page,
        elScroller,
        list,
        listHelper = [],
        snapList = [],
        i = 0,
        len = 0;

    function listViewHandler() {
        if (page.id !== 'pageMarqueeList' && page.id !==
                'pageTestVirtualList' && page.id !== 'pageAnimation') {
            len = list.length;
            for (i = 0; i < len; i += 1) {
                listHelper[i] = tau.helper.SnapListStyle
                    .create(list[i]);
            }
            len = listHelper.length;
            if (len) {
                for (i = 0; i < len; i += 1) {
                    snapList[i] = listHelper[i].getSnapList();
                }
            }
        }
        elScroller.setAttribute('tizen-circular-scrollbar', '');
    }

    function onPageBeforeShow(e) {
        page = e.target;
        elScroller = page.querySelector('.ui-scroller');
        if (elScroller) {
            list = elScroller.querySelectorAll('.ui-listview');
            if (list) {
                listViewHandler();
            }
        }
    }

    function onPageBeforeHide(e) {
        len = listHelper.length;
        if (len) {
            for (i = 0; i < len; i += 1) {
                listHelper[i].destroy();
            }
            listHelper = [];
        }
        if (elScroller) {
            elScroller.removeAttribute('tizen-circular-scrollbar');
        }
    }

    if (tau.support.shape.circle) {
        document.addEventListener('pagebeforeshow', onPageBeforeShow);
        document.addEventListener('pagebeforehide', onPageBeforeHide);
    }
}());
