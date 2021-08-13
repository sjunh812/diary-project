package org.sjhstudio.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import org.jetbrains.annotations.NotNull;
import org.sjhstudio.diary.custom.CustomBackupDialog;
import org.sjhstudio.diary.googledrive.DriveServiceHelper;
import org.sjhstudio.diary.helper.MyTheme;
import org.sjhstudio.diary.note.NoteDatabase;

import java.util.Collections;
import java.util.Date;

public class BackupActivity extends AppCompatActivity {
    /** 상수 **/
    private static final String LOG = "BackupActivity";
    private static final int REQUEST_CODE_SIGN_IN = 1;
    public static final String SIGN_KEY = "sign_key";

    /** UI **/
    private RelativeLayout accountLayout;           // 로그인 창
    private RelativeLayout backupLayout;            // 백업 창
    private RelativeLayout restoreLayout;           // 복원 창
    private TextView accountTextView;               // 로그인 시 출력되는 로그인 이메일
    private TextView recentDateTextView;            // 백업 시 출력되는 백업 시간
    private static ProgressDialog dialog;           // 백업 또는 복원 시 출력되는 Progress 다이얼로그 창
    private CustomBackupDialog backupDialog;        // 백업 또는 복원 시 출력되는 다이얼로그 창

    /** Data **/
    private DriveServiceHelper mDriveServiceHelper; // 구글 드라이브 API 와의 통신을 위한 Helper
    private GoogleSignInClient client;              // 구글 로그인 시 client
    private String signedAccount = "";              // 로그인 이메일
    private String backupDate = null;               // 백업 날짜
    private String fileId = null;                   // 백업 파일 이름
    private boolean isDeleteFile = false;           // 백업 파일이 삭제됐는지 여부
    public Handler handler;                         // dead lock 방지를 위한 핸들러 (메인 스레드 <-> 구글 통신 시 사용되는 스레드)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyTheme.applyTheme(this);
        setContentView(R.layout.activity_backup);

        startProgressDialog("잠시만 기다려주세요.");
        initUI();
        requestSignIn();

        accountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((signedAccount != null) && !(signedAccount.equals(""))) {
                    requestSignOut();
                } else {
                    startProgressDialog("로그인 중입니다.");
                    requestSignIn();
                }
            }
        });

        backupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBackupDialog();
            }
        });

        restoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRestoreDialog();
            }
        });

        handler = new Handler() {                       // 서브 스레드 -> 메인 스레드(UI)
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.arg1 == 1) {                     // 백업파일 생성 및 업데이트가 완료된 경우
                    query();
                } else if(msg.arg1 == 100) {            // 로그인한 계정에 백업파일이 존재하지않는 경우
                    recentDateTextView.setText("");

                    SharedPreferences preferences = getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove(signedAccount + "'s id");
                    editor.commit();
                }
            }
        };
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("백업 및 복원하기");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recentDateTextView = (TextView)findViewById(R.id.recentDateTextView);
        accountTextView = (TextView)findViewById(R.id.accountTextView);
        accountLayout = (RelativeLayout)findViewById(R.id.accountLayout);
        backupLayout = (RelativeLayout)findViewById(R.id.backupLayout);
        restoreLayout = (RelativeLayout)findViewById(R.id.restoreLayout);
    }

    /**
     * Starts a sign-in activity using {@link #REQUEST_CODE_SIGN_IN}.
     */
    private void requestSignIn() {
        Log.d(LOG, "Requesting sign-in");

        client = getClient();

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private  void requestSignOut() {
        Log.d(LOG, "Requesting sign-out");

        getClient().signOut().addOnCompleteListener(BackupActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                addSignedAccountSharedPreferences(signedAccount, false);

                signedAccount = "";
                backupDate = "";
                accountTextView.setText("로그인이 필요합니다.");
                recentDateTextView.setText("");
            }
        });
    }

    private GoogleSignInClient getClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();

        return GoogleSignIn.getClient(this, signInOptions);
    }

    private void checkSigendSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if(preferences.contains(signedAccount) && preferences.getBoolean(signedAccount, false)) {
            fileId = preferences.getString(signedAccount + "'s id", "");
            query();

            if(!isDeleteFile) {
                if((fileId != null) && !(fileId.equals(""))) {
                    Log.d(LOG, "File ID : " + fileId + " from checkSignedSharedPreferences()");
                } else {
                    Log.d(LOG,  "계정 " + signedAccount + "에 백업 파일이 없습니다.");

                    fileId = null;
                    editor.remove(signedAccount + "'s id");
                    editor.commit();
                }
            } else {
                Log.d(LOG,  "계정 " + signedAccount + "의 백업 파일이 삭제되었습니다.");

                fileId = null;
                editor.remove(signedAccount + "'s id");
                editor.commit();
            }
        }
    }

    private void addSignedAccountSharedPreferences(String signedAccount, boolean isSigned) {
        SharedPreferences preferences = getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(signedAccount, isSigned);
        editor.commit();
    }

    /**
     * Handles the {@code result} of a completed sign-in activity initiated from {@link
     * #requestSignIn()}.
     */
    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    Log.d(LOG, "Signed in as " + googleAccount.getEmail());

                    signedAccount = googleAccount.getEmail();
                    accountTextView.setText(signedAccount);
                    addSignedAccountSharedPreferences(signedAccount, true);

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    new NetHttpTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("Drive API Migration")
                                    .build();

                    // AndroidHttp.newCompatibleTransport() (AndroidHttp -> NetHttpTransport)
                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService, handler);
                    checkSigendSharedPreferences();
                })
                .addOnFailureListener(exception -> Log.e(LOG, "Unable to sign in.", exception));
    }

    /**
     * Creates a new file via the Drive REST API.
     */
    private void createFile() {
        if (mDriveServiceHelper != null) {
            Log.d(LOG, "Creating a file.");

            startProgressDialog("백업 중입니다.");
            java.io.File filePath = getDatabasePath(NoteDatabase.DB_NAME);

            mDriveServiceHelper.createDBFile(filePath)
                    .addOnSuccessListener(fileId -> readFile(fileId))
                    .addOnFailureListener(exception ->
                            Log.e(LOG, "Couldn't create file.", exception));
        }
    }

    /**
     * Creates a new file via the Drive REST API.
     */
    private void updateFile() {
        if (mDriveServiceHelper != null) {
            Log.d(LOG, "Updating a file.");

            startProgressDialog("백업 중입니다.");
            java.io.File filePath = getDatabasePath(NoteDatabase.DB_NAME);

            if(fileId != null) {
                mDriveServiceHelper.updateDBFile(fileId, filePath)
                        .addOnSuccessListener(fileId -> readFile(fileId))
                        .addOnFailureListener(exception ->
                                Log.e(LOG, "Couldn't create file.", exception));
            }
        }
    }

    /**
     * Retrieves the title and content of a file identified by {@code fileId} and populates the UI.
     */
    private void readFile(String fileId) {
        if (mDriveServiceHelper != null) {
            Log.d(LOG, "Reading file " + fileId);

            mDriveServiceHelper.readFile(fileId)
                    .addOnSuccessListener(nameAndContent -> {
                        String name = nameAndContent.first;
                        String content = nameAndContent.second;

                        Log.d(LOG, "Name : " + name + " content : " + content);
                        Log.d(LOG, "File ID(from readFile) : " + fileId);

                        setFileId(fileId);
                    })
                    .addOnFailureListener(exception ->
                            Log.e(LOG, "Couldn't read file.", exception));
        }
    }

    /**
     * Queries the Drive REST API for files visible to this app and lists them in the content view.
     */
    private void query() {
        if (mDriveServiceHelper != null) {
            Log.d(LOG, "Querying for files.");

            mDriveServiceHelper.queryFiles()
                    .addOnSuccessListener(fileList -> {
                        for (File file : fileList.getFiles()) {
                            if(file.getName().equals(NoteDatabase.DB_NAME)) {
                                Log.d(LOG, "file ID : " + file.getId());
                                Log.d(LOG, "file ModifiedTime : " + file.getModifiedTime());

                                Date date = new Date(file.getModifiedTime().getValue());
                                backupDate = MainActivity.dateFormat.format(date);

                                recentDateTextView.setText("최근 수정 날짜 : " + backupDate);
                                setIsDeleteFile(false);
                                setFileId(file.getId());
                                stopProgressDialog();

                                return;
                            }
                        }

                        Log.d(LOG, fileId + " 파일 존재하지 않음.");
                        setIsDeleteFile(true);
                        stopProgressDialog();

                        // Send to Message (to MainThread -> Change UI)
                        Message message = new Message();
                        message.arg1 = 100;
                        handler.sendMessage(message);
                    })
                    .addOnFailureListener(exception -> Log.e(LOG, "Unable to query files.", exception));
        }
    }

    private void downloadFile(String fileId) {
        try {
            Thread subThread = new Thread() {
                @Override
                public void run() {
                    mDriveServiceHelper.downFile(fileId, getDatabasePath(NoteDatabase.DB_NAME));
                }
            };
            subThread.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void setFileId(String fileId) {
        SharedPreferences pref = getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(signedAccount + "'s id", fileId);
        editor.commit();

        this.fileId = fileId;
    }

    private void setIsDeleteFile(boolean bool) {
        isDeleteFile = bool;
    }

    private void startProgressDialog(String message) {
        dialog = new ProgressDialog(this);
        dialog.setMessage(message);
        dialog.show();
    }

    public static void stopProgressDialog() {
        if(dialog != null) {
            dialog.dismiss();
        }
    }

    private void startBackup() {
        if((signedAccount != null) && !(signedAccount.equals(""))) {
            if((backupDate != null) && !(backupDate.equals(""))) {
                updateFile();
            } else {
                createFile();
            }
        } else {
            requestSignIn();
        }
    }

    private void startRestore() {
        if((signedAccount != null) && !(signedAccount.equals(""))) {
            if((backupDate != null) && !(backupDate.equals(""))) {
                if((fileId != null) && !(fileId.equals(""))) {
                    startProgressDialog("복원 중입니다.");
                    downloadFile(fileId);
                }
            } else {
                Toast.makeText(getApplicationContext(), "백업 파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            requestSignIn();
        }
    }

    private void startBackupDialog() {
        backupDialog = new CustomBackupDialog(this);
        backupDialog.show();

        backupDialog.setCancelButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backupDialog.dismiss();
            }
        });
        backupDialog.setOkButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBackup();
                backupDialog.dismiss();
            }
        });
    }

    private void startRestoreDialog() {
        backupDialog = new CustomBackupDialog(this);
        backupDialog.show();

        backupDialog.setTitleTextView("복원 하기");
        backupDialog.setQuestionTextView("복원하시겠습니까?");
        backupDialog.setSubTextView1("현재 작성된 일기는 삭제되고 구글 드라이브에\n저장된 일기로 복원됩니다.");
        backupDialog.setSubTextView2("(사진은 복원되지 않으며 현재 올린 사진은\n모두 삭제됩니다.)");
        backupDialog.setOkButtonText("복원하기");

        backupDialog.setCancelButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backupDialog.dismiss();
            }
        });
        backupDialog.setOkButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRestore();
                backupDialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}