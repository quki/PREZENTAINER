
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
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.puregodic.android.prezentainer.service.AccessoryService;
import com.puregodic.android.prezentainer.service.AccessoryService.MyBinder;
import com.puregodic.android.prezentainer.service.FileAction;

public class FileTransferRequestedActivity extends AppCompatActivity {

    private static final String TAG = FileTransferRequestedActivity.class.getSimpleName();
    public static boolean isUp = false;

    public int mTransId;
    public static final String DIR_PATH = "/sdcard/prezentainer/";
    private Context mCtxt;

    private String mFileName;
    private String mFileExtension;
    private AlertDialog mAlert;
    private ProgressBar mProgressBar;
    private TextView fileTransferStatus;
    private Button showBtn;
    private String title,yourId;
    private AccessoryService mAccessoryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_transfer_requested);
       
        title = getIntent().getStringExtra("title");
        yourId = getIntent().getStringExtra("yourId");
       
        isUp = true;
        mCtxt = getApplicationContext();

        fileTransferStatus = (TextView)findViewById(R.id.fileTransferStatus);
        mProgressBar = (ProgressBar)findViewById(R.id.mProgressBar);
        showBtn = (Button)findViewById(R.id.showBtn);
        mProgressBar.setMax(100);
        showBtn.setEnabled(false);
        fileTransferStatus.setText("제목 : "+title+"\n사용자 : "+yourId);

        // SD카드 존재유무 확인
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(mCtxt, "저장할 수 있는 곳이 존재하지 않습니다", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            File file = new File(DIR_PATH);
            
            // 최초 디렉토리 생성
            if (!file.exists()) {
                file.mkdirs();
                Toast.makeText(mCtxt, "폴더를 생성합니다 : "+DIR_PATH, Toast.LENGTH_SHORT).show();
            }
        }
        
        // Bind Service ( this and AccessoryService )
        mCtxt.bindService(new Intent(mCtxt, AccessoryService.class), this.mServiceConnection,
                Context.BIND_AUTO_CREATE);

        
        showBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                
               /* startActivity(new Intent()
                .setClass(FileTransferRequestedActivity.this, ResultActivity.class)
                .putExtra("title", mFileName)
                .putExtra("yourId", yourId));*/
                
                Intent i = new Intent(FileTransferRequestedActivity.this,ResultActivity.class);
                i.putExtra("title", mFileName);
                i.putExtra("yourId", yourId);
                startActivity(i);
                
            }
        });
        
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
            public void onFileActionTransferRequested(int id, String date) {
                mTransId = id;
                mFileExtension = ".amr";
                mFileName = title + date + mFileExtension;
                mAccessoryService.receiveFile(mTransId, DIR_PATH + mFileName, true);
                Log.i(TAG, "Transfer accepted");

                showQuitDialog();

            }

            @Override
            public void onFileActionProgress(final long progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileTransferStatus.setText("전송 중 입니다... " + progress + " / 100");
                        mProgressBar.setProgress((int)progress);
                    }
                });
            }

            @Override
            public void onFileActionTransferComplete() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileTransferStatus.setText("전송 완료 !"+title+yourId);
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

    // ServiceConnection 객체 생성, interface를 통해서
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Service connected");
            mAccessoryService = ((MyBinder)service).getService();
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
                AlertDialog.Builder alertbox = new AlertDialog.Builder(
                        FileTransferRequestedActivity.this);
                alertbox = new AlertDialog.Builder(FileTransferRequestedActivity.this);
                alertbox.setMessage("[" + DIR_PATH + "] 취소하시겠습니까?");
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
}
