(function(tau) {
	var page,
		pageWidget,
		enablePageScroll,
		elScroller,
		list,
		listHelper = [],
		snapList = [],
		header,
		headerHelper,
		headerExpandHandler = [],
		headerCollapseHandler = [],
		i, len;

	if (tau.support.shape.circle) {
		document.addEventListener("pagebeforeshow", function (e) {
			page = e.target;

			pageWidget = tau.widget.page(page);
			enablePageScroll = pageWidget.option("enablePageScroll");
			elScroller = page.querySelector(".ui-scroller");
			list = page.querySelectorAll(".ui-listview");
			header = page.querySelector(".ui-header:not(.ui-fixed)");

			if (page.id !== "pageMarqueeList") {
				len = list.length;
				for (i = 0; i < len; i++) {
					listHelper[i] = tau.helper.SnapListStyle.create(list[i]);
				}
			}
			if (elScroller) {
				elScroller.setAttribute("tizen-circular-scrollbar", "");
			}
			if (header && enablePageScroll) {
				headerHelper = tau.helper.ExpandableHeaderMarqueeStyle.create(header, {});
				len = listHelper.length;
				if (len) {
					for (i = 0; i < len; i++) {
						snapList[i] = listHelper[i].getSnapList();
						headerCollapseHandler[i] = snapList[i].enable.bind(snapList[i]);

						headerExpandHandler[i] = snapList[i].disable.bind(snapList[i]);
						header.addEventListener("headercollapse", headerCollapseHandler[i], false);
						header.addEventListener("headerbeforeexpand", headerExpandHandler[i], false);
					}
				}
			}
		});

		document.addEventListener("pagebeforehide", function (e) {
			len = listHelper.length;
			if (len) {
				if (headerHelper) {
					headerHelper.destroy();
					headerHelper = null;
				}
				for (i = 0; i < len; i++) {
					listHelper[i].destroy();
					if (header) {
						header.removeEventListener("headercollapse", headerCollapseHandler[i], false);
						header.removeEventListener("headerbeforeexpand", headerExpandHandler[i], false);
					}
				}
				listHelper = [];
			}
			if (elScroller) {
				elScroller.removeAttribute("tizen-circular-scrollbar");
			}
		});
	}
}(tau));
