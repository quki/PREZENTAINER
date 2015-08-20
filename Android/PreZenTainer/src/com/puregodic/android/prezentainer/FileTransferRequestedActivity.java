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
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.puregodic.android.prezentainer.service.AccessoryService;
import com.puregodic.android.prezentainer.service.AccessoryService.MyBinder;
import com.puregodic.android.prezentainer.service.FileAction;

public class FileTransferRequestedActivity extends AppCompatActivity {
	
	
	 private static final String TAG = "==REQUESTED ACTIVITY==";

	    public static boolean isUp = false;
	    public int mTransId;

	    private static final String DEST_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();	
	    private Context mCtxt;
	    private String mDirPath;
	    private String mFilePath;
	    private AlertDialog mAlert;
	    private ProgressBar progressBar;
	    private TextView fileTransferStatus;
	    private AccessoryService mAccessoryService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_transfer_requested);
		
		isUp = true;
        mCtxt = getApplicationContext();

        fileTransferStatus = (TextView)findViewById(R.id.fileTransferStatus);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(mCtxt, " No SDCARD Present", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            mDirPath = Environment.getExternalStorageDirectory() + File.separator + "FileTransferReceiver";
            File file = new File(mDirPath);
            if (file.mkdirs()) {
                Toast.makeText(mCtxt, " Stored in " + mDirPath, Toast.LENGTH_LONG).show();
            }
        }
		
		
        mCtxt.bindService(new Intent(mCtxt, AccessoryService.class),
                this.mServiceConnection, Context.BIND_AUTO_CREATE);
		
	}
	@Override
    protected void onStart() {
        isUp = true;
        super.onStart();
    }

    @Override
    protected void onStop() {
        isUp = false;
        super.onStop();
    }

    @Override
    protected void onPause() {
        isUp = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        isUp = true;
        super.onResume();
    }

    public void onDestroy() {
        isUp = false;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        isUp = false;
        moveTaskToBack(true);
    }
 
    // For Android before 2.0, Back key event
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isUp = false;
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    // Method getFileAction()
    private FileAction getFileAction() {
        return new FileAction() {
        	
        	// Define FileAction Interface
            @Override
            public void onFileActionError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAlert != null && mAlert.isShowing()) {
                            mAlert.dismiss();
                        }
                        fileTransferStatus.setText("전송 중 오류가 발생했습니다.");
                        progressBar.setProgress(0);
                    }
                });
            }

            @Override
            public void onFileActionProgress(final long progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileTransferStatus.setText("전송 중 입니다... "+progress+" / 100");
                    	progressBar.setProgress((int) progress);
                    }
                });
            }

            @Override
            public void onFileActionTransferComplete() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileTransferStatus.setText("전송 완료 !\n"+ DEST_DIRECTORY );
                        progressBar.setProgress(0);
                        mAlert.dismiss();
                        finish();
                    }
                });
            }

            @Override
            public void onFileActionTransferRequested(int id, String path) {
                mFilePath = path;
                mTransId = id;
                String receiveFileName = mFilePath.substring(mFilePath.lastIndexOf("/"), mFilePath.length());
                mAccessoryService.receiveFile(mTransId, DEST_DIRECTORY + receiveFileName, true);
                Log.i(TAG, "Transfer accepted");

               showQuitDialog();
                
            }
        };
    }
    
    
    // ServiceConnection 객체 생성, interface를 통해서
    private ServiceConnection mServiceConnection = new ServiceConnection() {

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
    
    // Alert Dialog
    private void showQuitDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(FileTransferRequestedActivity.this);
                alertbox = new AlertDialog.Builder(FileTransferRequestedActivity.this);
                alertbox.setMessage("Receiving file : [" + mFilePath + "] QUIT?");
                alertbox.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                        	mAccessoryService.cancelFileTransfer(mTransId);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mCtxt, "IllegalArgumentException", Toast.LENGTH_SHORT).show();
                        }
                        mAlert.dismiss();
                        progressBar.setProgress(0);
                    }
                });
                alertbox.setCancelable(false);
                mAlert = alertbox.create();
                mAlert.show();
            }
        });
    }
}
