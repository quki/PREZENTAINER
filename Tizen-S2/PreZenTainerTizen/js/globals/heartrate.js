/*
 * 
 * 심박수 관련함수
 * 
 * */

var heartRateArray;
var sendingInterval;
// 심박수를 JSON으로 만들기
function makeJsonHR() {

	try {
		heartRateArray.push(HR);
		console.log(heartRateArray);
	} catch (err) {
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}

}

// 심박수 JSON을 ANDROID로 보내기
function sendJsonHR() {

	try {
		jsonInfoHR = JSON.stringify(heartRateArray);
		console.log(jsonInfoHR);
		mSASocket.sendData(CHANNELID_HR, jsonInfoHR);
		console.log("Heart Rate sent : " + jsonInfoHR);
		stopHR();
	} catch (err) {
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}

}

// Start to measuring Heart Rate and making the data to JSON per 5 sec
function startHR() {
	heartRateArray = new Array();
	window.webapis.motion.start("HRM", function onSuccess(hrmInfo) {
		if (hrmInfo.heartRate > 0)
			HR = hrmInfo.heartRate;
	});
	sendingInterval = setInterval(makeJsonHR, 5000);
}
// Stop HR
function stopHR() {
	window.webapis.motion.stop("HRM");
	if(sendingInterval != null)
	clearInterval(sendingInterval);
	console.log("heartRateArray Initialize : " + heartRateArray);
	console.log('HR Stop !');
}