
package com.puregodic.android.prezentainer;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.puregodic.android.prezentainer.service.AccessoryService;
import com.puregodic.android.prezentainer.service.AccessoryService.MyBinder;
import com.puregodic.android.prezentainer.service.FileAction;

import java.io.File;

public class FileTransferRequestedActivity extends AppCompatActivity {

    private static final String TAG = FileTransferRequestedActivity.class.getSimpleName();
    public static boolean isUp = false;

    public int mTransId;
    private String mDate;
    public static final String DIR_PATH = "/sdcard/prezentainer/";
    private Context mCtxt;

    private String mFileExtension;
    private TextView fileTransferStatus,alarmInfo,ptTitle;
    private Button showBtn;
    private String title,yourId,alarmTime;
    private AccessoryService mAccessoryService;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_transfer_requested);

        //파일 전송 진행 사항 progress dialog
        mProgressDialog = new ProgressDialog(FileTransferRequestedActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage("녹음파일 전송중...");
       
        title = getIntent().getStringExtra("title");
        yourId = getIntent().getStringExtra("yourId");
        alarmTime = getIntent().getStringExtra("alarmTime");
        isUp = true;
        mCtxt = getApplicationContext();

        fileTransferStatus = (TextView)findViewById(R.id.fileTransferStatus);
        ptTitle = (TextView)findViewById(R.id.ptTitle);
        alarmInfo = (TextView)findViewById(R.id.alarmInfo);
        showBtn = (Button)findViewById(R.id.showBtn);
        showBtn.setEnabled(false);

        ptTitle.setText(title);
        alarmInfo.setText(alarmTime);

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
                
                
               startActivity(new Intent()
                .setClass(FileTransferRequestedActivity.this, ResultActivity.class)
                .putExtra("title", title)
                .putExtra("date", mDate)
                .putExtra("yourId", yourId));
               
                finish();
                
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
        finish();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        isUp = false;
        finish();
        //moveTaskToBack(true);
    }

    // For Android before 2.0, Back key event
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            isUp = false;
            //moveTaskToBack(true);
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
                mDate = date;
                mFileExtension = ".amr";
                String mFileName = title + mDate + mFileExtension;
                mAccessoryService.receiveFile(mTransId, DIR_PATH + mFileName, true);
                Log.i(TAG, "Transfer accepted");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mProgressDialog.show();
                    }
                });

            }

            @Override
            public void onFileActionProgress(final long progress) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                            mProgressDialog.setProgress((int) progress);
                    }
                });
            }

            @Override
            public void onFileActionTransferComplete() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mProgressDialog.dismiss();
                        setIcon(ContextCompat.getDrawable(FileTransferRequestedActivity.this, R.drawable.ic_check));
                        fileTransferStatus.setText("프레젠테이션 결과 확인 준비 완료");
                        showBtn.setEnabled(true);
                    }
                });
            }

            @Override
            public void onFileActionError() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        setIcon(ContextCompat.getDrawable(FileTransferRequestedActivity.this, R.drawable.ic_alert));
                        fileTransferStatus.setText("전송 중 오류가 발생했습니다.");
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

    private void setIcon(Drawable icon){
        int height = icon.getIntrinsicHeight();
        int width = icon.getIntrinsicWidth();
        icon.setBounds(0, 0, width, height);
        fileTransferStatus.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
    }

}
