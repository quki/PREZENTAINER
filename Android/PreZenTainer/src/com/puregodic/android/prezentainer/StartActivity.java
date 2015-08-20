package com.puregodic.android.prezentainer;

import java.io.File;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.puregodic.android.prezentainer.service.AccessoryService;
import com.puregodic.android.prezentainer.service.AccessoryService.MyBinder;
import com.puregodic.android.prezentainer.service.FileAction;

public class StartActivity extends AppCompatActivity {
    
    public int mTransId;
    private static final String TAG = StartActivity.class.getSimpleName();
    private static final String DEST_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();   
    private String title,yourId;
    private Context mCtxt;
    private AlertDialog mAlert;
    private String mDirPath,mFilePath;
    private ProgressBar mProgressBar;
    private TextView fileTransferStatus;
    private Button showBtn;
    private AccessoryService mAccessoryService;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		mCtxt = getApplicationContext();
		
		fileTransferStatus = (TextView)findViewById(R.id.fileTransferStatus);
		mProgressBar = (ProgressBar)findViewById(R.id.mProgressBar);
		showBtn = (Button)findViewById(R.id.showBtn);
		showBtn.setEnabled(false);
		mProgressBar.setMax(100);
		
		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		yourId = intent.getStringExtra("yourId");
		Toast.makeText(getApplicationContext(), title+yourId, Toast.LENGTH_SHORT).show();
		
		// 외장(SD카드) 존재유무 확인 및 외장경로에 저장
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(mCtxt, " SD카드가 없습니다 ", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            mDirPath = Environment.getExternalStorageDirectory() + File.separator + "Prezentainer";
            File file = new File(mDirPath);
            if (file.mkdirs()) {
                Toast.makeText(mCtxt, " Stored in " + mDirPath, Toast.LENGTH_LONG).show();
            }
        }
		
		// Bind Service ( this and AccessoryService )
		mCtxt.bindService(new Intent(mCtxt, AccessoryService.class),this.mServiceConnection, Context.BIND_AUTO_CREATE);
		
		
		showBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                //mCtxt.startActivity(new Intent()
                startActivity(new Intent()
                .setClass(StartActivity.this, ResultActivity.class)
                .putExtra("title", title)
                .putExtra("yourId", yourId));
                
                /*Intent resultIntent = new Intent(StartActivity.this, ResultActivity.class);
                resultIntent.putExtra("title", title);
                resultIntent.putExtra("yourId", yourId);
                startActivity(resultIntent);*/
                
            }
        });
		
	}
	
	
	// Method getFileAction()
    private FileAction getFileAction() {
        return new FileAction() {
         // Define FileAction Interface
            @Override
            public void onFileActionTransferRequested(int id, String path) {
                mFilePath = path;
                mTransId = id;
                
                String receiveFileName = mFilePath.substring(mFilePath.lastIndexOf("/"), mFilePath.length());
                
                // 해당 경로로 파일을 전송을 요청 받는다.
                mAccessoryService.receiveFile(mTransId, DEST_DIRECTORY + receiveFileName, true);
                Log.i(TAG, "Transfer accepted");

               showQuitDialog();
                
            }
            

            @Override
            public void onFileActionProgress(final long progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileTransferStatus.setText("전송 중 입니다... "+progress+" / 100");
                        mProgressBar.setProgress((int) progress);
                    }
                });
            }

            @Override
            public void onFileActionTransferComplete() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileTransferStatus.setText("전송 완료 !\n"+ DEST_DIRECTORY );
                        mProgressBar.setProgress(0);
                        mAlert.dismiss();
                        showBtn.setEnabled(true);
                    }
                });
            }
            
         
            @Override
            public void onFileActionError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAlert != null && mAlert.isShowing()) {
                            mAlert.dismiss();
                        }
                        fileTransferStatus.setText("전송 중 오류가 발생했습니다.");
                        mProgressBar.setProgress(0);
                    }
                });
            }
            
        };
    }
 // Alert Dialog
    private void showQuitDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(StartActivity.this);
                alertbox = new AlertDialog.Builder(StartActivity.this);
                alertbox.setMessage("파일을 전송 받습니다 : [" + mFilePath + "] 취소하시겠습니까?");
                alertbox.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            mAccessoryService.cancelFileTransfer(mTransId);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mCtxt, "IllegalArgumentException", Toast.LENGTH_SHORT).show();
                        }
                        mAlert.dismiss();
                        mProgressBar.setProgress(0);
                    }
                });
                alertbox.setCancelable(false);
                mAlert = alertbox.create();
                mAlert.show();
            }
        });
    }
	
	
    private ServiceConnection mServiceConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Service connected");
            mAccessoryService = ((MyBinder) service).getService();
            mAccessoryService.registerFileAction(getFileAction());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "Service disconnected");
            mAccessoryService = null;            
        }
    };
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
