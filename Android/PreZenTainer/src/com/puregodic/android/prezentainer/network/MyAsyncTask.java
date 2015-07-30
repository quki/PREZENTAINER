package com.puregodic.android.prezentainer.network;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

import com.puregodic.android.prezentainer.service.AccessoryService;

public class MyAsyncTask extends AsyncTask<String, Integer, Boolean>{
	
	public static final String TAG = "==Async==";
	AccessoryService accessoryService = new AccessoryService();
	private ArrayList<String> fromServiceArray = new ArrayList<String>();
	
	public MyAsyncTask() {
	}

	public MyAsyncTask(ArrayList<String> fromServiceArray) {
		this.fromServiceArray = fromServiceArray;
	}

	@Override
	protected Boolean doInBackground(String... urls) {

		for (String url : urls) {

			try {
				ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
				HttpClient client = new DefaultHttpClient();
				client.getConnectionManager();
				
				
				for (int i = 0; i < accessoryService.al.size(); i++) {
					Log.e(TAG, accessoryService.al.get(i));
				}
				if (fromServiceArray.size() != 0) {
					for (int i = 0; i < fromServiceArray.size(); i++) {
						Log.e(TAG, "" + fromServiceArray.get(i));
						HttpPost post = new HttpPost(url);
						//하나만 대입 시켜보자.
						pairs.add(new BasicNameValuePair("txtName", fromServiceArray
								.get(i)));
						// 입력하는 3개 요소를 namevaluepair 형식으로 만들어 전송한다.
						post.setEntity(new UrlEncodedFormEntity(pairs));
						client.execute(post);
						
					}
						
					
				} else {

					Log.e(TAG, "===넘겨줄 Array size = " + fromServiceArray.size());

				}
				if (pairs.size() != 0) {
					for (int i = 0; i < pairs.size(); i++) {
						Log.e(TAG, "pairs : " + pairs.get(i));
					}
				} else {
					Log.e(TAG, "pairs : " + pairs.size());
				}
				//HttpResponse response = client.execute(post);

			} /*catch (ClientProtocolException e) {
				Log.e(TAG, "===ClientProtocolException=== : " + e);
				return false;
			}*/ catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "===IOException=== : " + e);
				return false;
			}

		}
		return true;
	}

	@Override
	protected void onPreExecute() {
		Log.e(TAG, "===onPreExecute===");
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result == true) {
			Log.e(TAG, "===onPostExecute result true===");
			// ArrayList 초기화
			fromServiceArray.clear();
				Log.e(TAG, "===Iniatialize Array=== size : "+fromServiceArray.size());
				
		} else {
			Log.e(TAG, "===onPostExecute result falsee===");
		}

		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	@Override
	protected void onCancelled(Boolean result) {
		// TODO Auto-generated method stub
		super.onCancelled(result);
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}
	
	
	

}
