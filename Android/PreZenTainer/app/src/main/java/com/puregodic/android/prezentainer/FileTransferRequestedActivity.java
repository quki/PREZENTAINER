
package com.puregodic.android.prezentainer;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
    public static String DIR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath(); // /storage/emulated/0
    private Context mCtxt;

    private String mFileExtension;
    private TextView fileTransferStatus,alarmInfo,ptTitle;
    private Button showBtn;
    private String title,userId;
    private AccessoryService mAccessoryService;
    private ProgressDialog mProgressDialog;
    String mDirPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_transfer_requested);

       final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_transparent);
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        Drawable backArrow =  ContextCompat.getDrawable(this,R.drawable.ic_arrow_left);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(FileTransferRequestedActivity.this);
                builder.setTitle("재설정").setMessage("현재 진행하는 발표는 비정상적으로 종료됩니다.\n발표설정을 다시 하시겠습니까?").setCancelable(false)
                        .setPositiveButton("다시설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

               AlertDialog mDialog =  builder.create();
                mDialog.show();
            }
        });

        //파일 전송 진행 사항 progress dialog
        mProgressDialog = new ProgressDialog(FileTransferRequestedActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage("녹음파일 전송중...");
       
        title = getIntent().getStringExtra("title");
        userId = getIntent().getStringExtra("userId");
        String alarmTime = getIntent().getStringExtra("alarmTime");
        isUp = true;
        mCtxt = getApplicationContext();

        fileTransferStatus = (TextView)findViewById(R.id.fileTransferStatus);
        ptTitle = (TextView)findViewById(R.id.ptTitle);
        alarmInfo = (TextView)findViewById(R.id.alarmInfo);
        showBtn = (Button)findViewById(R.id.showBtn);
        showBtn.setEnabled(false);

        ptTitle.setText(title);
        alarmInfo.setText(alarmTime);

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(mCtxt, "저장할 수 있는 곳이 존재하지 않습니다", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            File file = new File(DIR_PATH, "Prezentainer");
            Log.e("==FILE PATH TEST==",file.getAbsolutePath());
            if (file.mkdirs()) {
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
                .putExtra("userId", userId));
               
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
                String dirPath = DIR_PATH + File.separator+"Prezentainer"+File.separator;
                //mAccessoryService.receiveFile(mTransId, mDirPath + mFileName, true);
                mAccessoryService.receiveFile(mTransId, dirPath + mFileName, true);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.file_transfer_requested, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
