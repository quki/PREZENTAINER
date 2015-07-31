package com.puregodic.android.prezentainer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.puregodic.android.prezentainer.FileTransferRequestedActivity;
import com.puregodic.android.prezentainer.connectpchelper.ConnecToPcHelper;
import com.puregodic.android.prezentainer.network.MyAsyncTask;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;
import com.samsung.android.sdk.accessoryfiletransfer.SAFileTransfer;
import com.samsung.android.sdk.accessoryfiletransfer.SAFileTransfer.EventListener;

public class AccessoryService extends SAAgent {

	private Context mContext;
	private static String TAG = "==SERVICE==";
	private final IBinder mBinder = new MyBinder();
	private SAFileTransfer mSAFileTransfer = null;
	private EventListener ftEventCallBack;
	private FileAction mFileAction;
	HashMap<Integer, AccessoryServiceConnection> mConnectionsMap = null;
	public AccessoryServiceConnection mConnectionHandler;
	public static final int CHANNEL_ID_SETTING = 100;
	public static final int CHANNEL_ID_EVENT = 104;
	public static final int CHANNEL_ID_HR = 110;
	ConnecToPcHelper mConnecToPcHelper;
	public ArrayList<String> al = new ArrayList<String>();
	

	// ???????????????????
	public AccessoryService() {
		super("AccessoryService", AccessoryServiceConnection.class);
	}
	
	// Find PeerAgent
	public void findPeers() {
		findPeerAgents();
		Log.e(TAG, "==Finding Peer==");
	}

	// PeerAgent Found
	public void onPeerAgentFound(SAPeerAgent peerAgent) {

		if (peerAgent != null) {
			establishConnection(peerAgent);
		} else {
			Toast.makeText(getApplicationContext(), "PeerAgent가 없습니다.",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	// Receive the File
	public void receiveFile(int transId, String path, boolean bAccept) {
		if (mSAFileTransfer != null) {
			if (bAccept) {
				mSAFileTransfer.receive(transId, path);
				Log.e(TAG, "===Receive file PATH === : "+path);
			} else {
				mSAFileTransfer.reject(transId);
			}
		}
	}
	
	public void sendDataToGear(String mData) {
		final String message = new String(mData);
		if(mConnectionHandler!= null){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						mConnectionHandler.send(CHANNEL_ID_SETTING, message.getBytes());
						Log.e(TAG, message);
					} catch (IOException e) {
						Log.e(TAG, "==Cannot Send data to Gear==");
						e.printStackTrace();
					}
				}
			}).start();
			
		}else{
			
			Log.e(TAG, "mConnectionHandler null");
		}
	}
	// Cancel File Transfer
	public void cancelFileTransfer(int transId) {
		if (mSAFileTransfer != null) {
			mSAFileTransfer.cancel(transId);
		}
	}
	// File Action Interface Initialize
	public void registerFileAction(FileAction mFileAction) {
		this.mFileAction = mFileAction;
	}

	public boolean establishConnection(SAPeerAgent peerAgent) {
		if (peerAgent != null) {
			// Request Service Connection
			Toast.makeText(getApplicationContext(),
					"PeerAgent에게 connection요청을 보냈습니다.", Toast.LENGTH_SHORT).show();
			Log.e(TAG, "===requestServiceConnection=== peerAgent found : "
					+ peerAgent);
			requestServiceConnection(peerAgent);
			return true;
		}
		return false;
	}
	// Socket Close
	public boolean closeConnection() {
        if (mConnectionHandler != null) {
            mConnectionHandler.close();
            mConnectionHandler = null;
        }
        return true;
    }

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		Toast.makeText(getApplicationContext(), "OnCreat()", Toast.LENGTH_SHORT)
				.show();
		
		SA sa = new SA();
		try {
			// Initialize Accessory
			sa.initialize(this);
		} catch (SsdkUnsupportedException e) {
			if (e.getType() == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
				Toast.makeText(getBaseContext(),
						"Cannot initialize, DEVICE_NOT_SUPPORTED",
						Toast.LENGTH_SHORT).show();
			} else if (e.getType() == SsdkUnsupportedException.LIBRARY_NOT_INSTALLED) {
				Toast.makeText(getBaseContext(),
						"Cannot initialize, LIBRARY_NOT_INSTALLED.",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getBaseContext(), "Cannot initialize, UNKNOWN.",
						Toast.LENGTH_SHORT).show();
			}

			e.printStackTrace();
			return;
		} catch (Exception e1) {
			Toast.makeText(getBaseContext(), "Cannot initialize, SA.",
					Toast.LENGTH_SHORT).show();
			e1.printStackTrace();
			return;
		}
		
		ftEventCallBack = new EventListener() {
			
			
			//File Transfer Requested
			@Override
			public void onTransferRequested(int transId, String fileName) {
				
				if (FileTransferRequestedActivity.isUp) {
					Log.d(TAG, "Activity is Already up");
					mFileAction.onFileActionTransferRequested(transId, fileName); //put data into FileAction Interface
				} else {
					Log.e(TAG, "Activity is not up, invoke activity");
					mContext.startActivity(new Intent()
							.setClass(mContext,
									FileTransferRequestedActivity.class)
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
							.setAction("incomingFT").putExtra("tx", transId)
							.putExtra("fileName", fileName));
					
					//??????????????????????????????????????????????????????
					int counter = 0;
					while (counter < 10) {
						counter++;
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (mFileAction != null) {
							mFileAction.onFileActionTransferRequested(transId, fileName);
							break;
						}
					}
				}
			}
			
			
			
			//File Transfer Status changed
			@Override
			public void onProgressChanged(int transId, int progress) {
				if (mFileAction != null) {
					mFileAction.onFileActionProgress(progress);
				}
				
			}
			//File Transfer Completed
			@Override
			public void onTransferCompleted(int transId, String fileName, int errCode) {
				
				Log.e(TAG, "===onTransferCompleted: tr id : " + transId
						+ " file name : " + fileName + " error : " + errCode);	
				
				if (al.size() != 0) {
					for (int i = 0; i < al.size(); i++) {
						Log.v(TAG, al.get(i));
					}
					MyAsyncTask asyncTask = new MyAsyncTask(al);
					asyncTask
							.execute(new String[] { "http://cyh1704.dothome.co.kr/tizen/wow.php" });
					//al.clear();
					//Log.v(TAG, "Initialize the ArrayList");
				} else {
					Log.v(TAG, "HR was not transfered");
				}
				
				if (errCode == SAFileTransfer.ERROR_NONE) {
					mFileAction.onFileActionTransferComplete();
				} else {
					mFileAction.onFileActionError();
				}
				
			}
			
			
		};
		
		// SAFile Transfer Instantiate
		mSAFileTransfer = new SAFileTransfer(AccessoryService.this, ftEventCallBack);
	}

	@Override
	public void onDestroy() {
		/*Toast.makeText(getApplicationContext(), "onDestroy()", Toast.LENGTH_SHORT).show();
		closeConnection();*/
		super.onDestroy();
	}

	// Service Connection Success and Initialize Socket
	@Override
	protected void onServiceConnectionResponse(SAPeerAgent peerAgent,
			SASocket socket, int result) {
		if (result == SAAgent.CONNECTION_SUCCESS) {
			
			Toast.makeText(getApplicationContext(), "연결완료", Toast.LENGTH_SHORT).show();
			
			if (socket != null) {
				mConnectionHandler = (AccessoryServiceConnection) socket;
				if (mConnectionsMap == null) {
					mConnectionsMap = new HashMap<Integer, AccessoryServiceConnection>();
				}
				// Connection ID 생성
				mConnectionHandler.mConnectionId = (int) (System.currentTimeMillis() & 255);

				Log.e(TAG, "===onServiceConnection connectionID = "
						+ mConnectionHandler.mConnectionId);

				mConnectionsMap.put(mConnectionHandler.mConnectionId, mConnectionHandler);
				Log.e(TAG, "===Service Connection Success===");
				Toast.makeText(getApplicationContext(), "Connection완료 !!", Toast.LENGTH_SHORT).show();
				
			} else {
				Log.e(TAG, "===SASocket object is null===");
			}
		}else if(result == SAAgent.CONNECTION_ALREADY_EXIST){
			
			Toast.makeText(getApplicationContext(), "이미 연결되있습니다.", Toast.LENGTH_SHORT).show();
			
		}else if(result == SAAgent.CONNECTION_FAILURE_PEERAGENT_NO_RESPONSE){
			
			Toast.makeText(getApplicationContext(), "기어로부터 응답이 없습니다.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onFindPeerAgentResponse(SAPeerAgent peerAgent, int result) {
		//내가 찾는다 !!
				if (result == PEER_AGENT_FOUND) {
					Log.e(TAG, "===onFindPeerAgentResponse=== : result = " + result);
					onPeerAgentFound(peerAgent);
		        }
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}



	// Binder 상속 받은 클래스
	public class MyBinder extends Binder {
		
		public AccessoryService getService() {
			return AccessoryService.this;
		}
	}

	// Socket 생성
	public class AccessoryServiceConnection extends SASocket {
		private int mConnectionId;

		// ???????????????????????
		public AccessoryServiceConnection() {
			super(AccessoryServiceConnection.class.getName());
		}

		@Override
		public void onError(int channelId, String errorMessage, int errorCode) {

			Log.e(TAG, "Connection is not alive ERROR: " + errorMessage + "  "
					+ errorCode);
		}

		@Override
		public void onReceive(int channelId, byte[] data) {
			String fromGearMessage = "";
			// MAP 에서 해당 Connection ID값을 id로 value값을 찾아낸다.
			if (channelId == CHANNEL_ID_EVENT) {

				new Thread(new Runnable() {
					public void run() {
						try {
							// event 전달
							mConnecToPcHelper = new ConnecToPcHelper();
							mConnecToPcHelper.transferToPc();
						} catch (Exception e) {
							Log.e(TAG, "Cannot transfer data to PC");
						}

					}
				}).start();
			}
			if (channelId == CHANNEL_ID_HR) {
				fromGearMessage = new String(data);
				try {
					al.add(fromGearMessage);
				} catch (Exception e) {
					Log.e(TAG, "Cannot add HR to ArrayList");
				}

			}

		}

		@Override
		protected void onServiceConnectionLost(int reason) {
			closeConnection();
			Log.e(TAG, "onServiceConnectionLost ==socket close== reason : "
					+ reason);
		}
	}
	
}
