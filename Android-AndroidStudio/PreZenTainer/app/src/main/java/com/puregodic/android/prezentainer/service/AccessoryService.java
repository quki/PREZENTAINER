package com.puregodic.android.prezentainer.service;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.puregodic.android.prezentainer.FileTransferRequestedActivity;
import com.puregodic.android.prezentainer.bluetooth.ConnecToPcHelper;
import com.puregodic.android.prezentainer.network.AppController;
import com.puregodic.android.prezentainer.network.NetworkConfig;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;
import com.samsung.android.sdk.accessoryfiletransfer.SAFileTransfer;
import com.samsung.android.sdk.accessoryfiletransfer.SAFileTransfer.EventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AccessoryService extends SAAgent {

	private Context mContext;
	private static String TAG = "==SERVICE==";
	private final IBinder mBinder = new MyBinder();
	private SAFileTransfer mSAFileTransfer = null;
	private EventListener ftEventCallBack;
	private FileAction mFileAction;
	private ConnectionActionGear mConnectionActionGear;
	HashMap<Integer, AccessoryServiceConnection> mConnectionsMap = null;
	public AccessoryServiceConnection mConnectionHandler;
	public static final int CHANNEL_ID_SETTING = 100;
	public static final int CHANNEL_ID_EVENT = 104;
	public static final int CHANNEL_ID_HR = 110;
	public static final int CHANNEL_ID_EVENTTIME = 114;
	public String mDeviceName,mPtTitle,yourId;
	private String jsonHR,jsonET;
	private StringBuffer date ;
	private Boolean isGearConnected =false;
	
	public AccessoryService() {
		super("AccessoryService", AccessoryServiceConnection.class);
	}
	
	// FileAction Interface Initialize
    public void registerFileAction(FileAction mFileAction) {
        this.mFileAction = mFileAction;
    }
    // ConnectionAction (Gear) Interface Initailize
    public void registerConnectionAction(ConnectionActionGear mConnectionActionGear){
        this.mConnectionActionGear = mConnectionActionGear;
    }
	

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		
		// Initialize SAP and catch the error
		SA sa = new SA();
		try {
			sa.initialize(this);
		} catch (SsdkUnsupportedException e) {
			if (e.getType() == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
				Toast.makeText(getBaseContext(),
						"SAP를 지원하지 않는 단말입니다",
						Toast.LENGTH_SHORT).show();
			} else if (e.getType() == SsdkUnsupportedException.LIBRARY_NOT_INSTALLED) {
				Toast.makeText(getBaseContext(),
						"유심 등록이 안되어있거나 Framework를 지원하지 않습니다",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getBaseContext(), "SAP통신에 오류가 있습니다",
						Toast.LENGTH_SHORT).show();
			}

			e.printStackTrace();
			return;
		} catch (Exception e1) {
			Toast.makeText(getBaseContext(), "SAP통신 초기화에 오류가 있습니다",
					Toast.LENGTH_SHORT).show();
			e1.printStackTrace();
			return;
		}
		
		
		ftEventCallBack = new EventListener() {
			
			
			//File Transfer Requested
			@Override
			public void onTransferRequested(int transId, String fileName) {
			    
			    
			    // 파일 전송이 시작된 시간 측정
                Calendar calendar = Calendar.getInstance();
                date = new StringBuffer();
                date.append(String.valueOf(calendar.get(Calendar.YEAR)));
                date.append("년 ");
                date.append(String.valueOf(calendar.get(Calendar.MONTH)+1));
                date.append("월 ");
                date.append(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                date.append("일 ");
                date.append(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
                date.append("시 ");
                date.append(String.valueOf(calendar.get(Calendar.MINUTE)));
                date.append("분");
			    
				if (FileTransferRequestedActivity.isUp) {
					Log.d(TAG, "Activity is Already up");
					mFileAction.onFileActionTransferRequested(transId, date.toString()); 
					//put data into FileAction Interface
				} else {
					Log.d(TAG, "Activity is not up, invoke activity");
					mContext.startActivity(new Intent()
							.setClass(mContext,
									FileTransferRequestedActivity.class)
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
							.setAction("incomingFT")
							.putExtra("title", mPtTitle)
							.putExtra("yourId", yourId));    
					
					// 5초 이내에 응답을 해야한다
					int counter = 0;
					while (counter < 10) {
						counter++;
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (mFileAction != null) {
							mFileAction.onFileActionTransferRequested(transId, date.toString());
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
			    
			    
			    
			    Log.e(TAG, "Transfer Completed filename :  "+fileName + "errCode : "+errCode+" \n and  PT tittle is "+mPtTitle);
				if (errCode == SAFileTransfer.ERROR_NONE) {
				    
					
                    mFileAction.onFileActionTransferComplete();
                    
					StringRequest str = new StringRequest(Method.POST, NetworkConfig.URL_INSERT,
                            new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.e(TAG, "Volley onResponse : " + response);
                        }
                    },      new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                        }
                    }){

                        @Override
                        protected Map<String, String> getParams() {
                            // Create Parameter to insert Table
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("email", yourId);
                            params.put("title", mPtTitle);
                            params.put("date", date.toString());
                            params.put("hbr", jsonHR);
                            params.put("time", jsonET);
                            return params;
                        }

                    };
             
                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(str, TAG);
                    
					
				} else {
				    Log.e(TAG, "Transfer error, errCode : "+errCode);
					mFileAction.onFileActionError();
				}
			}
		};
		
		// SAFile Transfer Instantiate
		mSAFileTransfer = new SAFileTransfer(AccessoryService.this, ftEventCallBack);
	}

	
	// PeerAgent를 찾았을 때
	@Override
    protected void onFindPeerAgentResponse(SAPeerAgent peerAgent, int result) {
                if (result == PEER_AGENT_FOUND) {
                    Log.d(TAG, "onFindPeerAgentResponse : peerAgent = " + peerAgent);
                    onPeerAgentFound(peerAgent);
                }else{
                    Log.e(TAG, "onFindPeerAgentResponse : result = " + result);
                    mConnectionActionGear.onFindingPeerAgentError();
                }
    }
	
	
    // PeerAgent찾고 service connection request에 대한 대답(거절이나 승낙)
	@Override
	public void onServiceConnectionResponse(SAPeerAgent peerAgent, SASocket socket, int result) {
		if (result == SAAgent.CONNECTION_SUCCESS) {
		    
		    isGearConnected = true;
		    mConnectionActionGear.onConnectionActionComplete();
			if (socket != null) {
				mConnectionHandler = (AccessoryServiceConnection) socket;
				if (mConnectionsMap == null) {
					mConnectionsMap = new HashMap<Integer, AccessoryServiceConnection>();
				}
				// Connection ID 생성
				mConnectionHandler.mConnectionId = (int) (System.currentTimeMillis() & 255);

				mConnectionsMap.put(mConnectionHandler.mConnectionId, mConnectionHandler);
				Log.d(TAG, "Connection  Success");

			} else {
				Log.e(TAG, "SASocket object is null");
			}
		}else if(result == SAAgent.CONNECTION_ALREADY_EXIST){
		    
		    mConnectionActionGear.onConnectionActionComplete();
			Toast.makeText(mContext, "이미 연결되있습니다.", Toast.LENGTH_SHORT).show();
			Log.w(TAG, "CONNECTION_ALREADY_EXIST");
			
			
		}else if(result == SAAgent.CONNECTION_FAILURE_PEERAGENT_NO_RESPONSE){
		    if( !isGearConnected ){
		        Toast.makeText(mContext, "기어측 어플로부터 응답이 없습니다.", Toast.LENGTH_SHORT).show();
	            Log.w(TAG, "CONNECTION_FAILURE_PEERAGENT_NO_RESPONSE");
	            mConnectionActionGear.onConnectionActionError();
		    }
			
		}
	}
	
	@Override
    protected void onError(SAPeerAgent peerAgent, String str, int i) {
        Log.e(TAG, peerAgent +"==="+str+"==="+i);
        super.onError(peerAgent, str, i);
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

		public AccessoryServiceConnection() {
			super(AccessoryServiceConnection.class.getName());
		}

		@Override
		public void onError(int channelId, String errorMessage, int errorCode) {

			Log.e(TAG, "Connection is not alive ERROR: " + errorMessage + "  "
					+ errorCode);
		}

		// 기어로부터 받는 모든 데이터를 onReceive에서 받음
		@Override
		public void onReceive(int channelId, byte[] data) {
		    
		 	// MAP 에서 해당 Connection ID값을 id로 value값을 찾아낸다

			if (channelId == CHANNEL_ID_EVENT) {
				// 우측키 : "right", 좌측키 : "left"
			    final String direction =  new String(data);
				new Thread(new Runnable() {
					public void run() {
						try {
						    Log.e(TAG, direction);
							// direction에 따른 event 전달, 이후 PC측에서 write된 direction을 바탕으로 event를 구별함
							ConnecToPcHelper mConnecToPcHelper = new ConnecToPcHelper();
							mConnecToPcHelper.transferToPc(mDeviceName, direction);
							if(mConnectionHandler != null){
							    final String unlockMessage =  new String("UNLOCK");
							    Thread.sleep(1000);
							    mConnectionHandler.send(CHANNEL_ID_EVENT, unlockMessage.getBytes());
							    Log.e(TAG,unlockMessage);
							}
							 
						} catch (Exception e) {
							Log.e(TAG, "Cannot transfer data to PC");
						}

					}
				}).start();
			}else if (channelId == CHANNEL_ID_HR) {
			    
			    jsonHR = new String(data);
			    Log.v(TAG, jsonHR);
			    
				
			}else if (channelId == CHANNEL_ID_EVENTTIME){

			       jsonET = new String(data);
			       Log.v(TAG, jsonET);
			        
			}

		}

		private void send(int channelIdEvent, String string) {
            // TODO Auto-generated method stub
            
        }

        @Override
		protected void onServiceConnectionLost(int reason) {
			closeConnection();
			Log.e(TAG, "onServiceConnectionLost ==socket close== reason : "
					+ reason);
		}
	}
	
	// Find PeerAgent
    public void findPeers() {
        mConnectionActionGear.onFindingPeerAgent();
        findPeerAgents();
        Log.d(TAG, "findPeerAgents...");
    }

    // PeerAgent Found
    public void onPeerAgentFound(SAPeerAgent peerAgent) {
        if (peerAgent != null) 
            establishConnection(peerAgent);
    }
    
    // Receive the File
    public void receiveFile(int transId, String path, boolean bAccept) {
        if (mSAFileTransfer != null) {
            if (bAccept) {
                mSAFileTransfer.receive(transId, path);
                Log.d(TAG, "Receive file PATH : "+path);
            } else {
                mSAFileTransfer.reject(transId);
            }
        }
    }
    
    // Send Timer data to gear by JSON
    public void sendDataToGear(String mData) {
        final String message = new String(mData);
        if(mConnectionHandler!= null){
            new Thread(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        mConnectionHandler.send(CHANNEL_ID_SETTING, message.getBytes());
                        Log.v(TAG, message);
                    } catch (IOException e) {
                        Log.e(TAG, "Cannot Send time data to Gear");
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
    

    public boolean establishConnection(SAPeerAgent peerAgent) {
        if (peerAgent != null) {
            // Request Service Connection
            mConnectionActionGear.onConnectionActionRequest();
            requestServiceConnection(peerAgent);
            return true;
        }
        return false;
    }
    
    
    // Socket Close
    public boolean closeConnection() {
        if (mConnectionHandler != null) {
            isGearConnected = false;
            Log.d(TAG, "Connection Close");
            mConnectionHandler.close();
            mConnectionHandler = null;
        }
        return true;
    }
    
    @Override
    public void onDestroy() {
        closeConnection();
        super.onDestroy();
    }
	
}
