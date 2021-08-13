package org.sjhstudio.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.github.chrisbanes.photoview.PhotoView;

import org.sjhstudio.diary.helper.MyTheme;

public class PhotoActivity extends AppCompatActivity {
    private String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyTheme.applyTheme(this);
        setContentView(R.layout.activity_photo);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("사진보기");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PhotoView photoView = (PhotoView)findViewById(R.id.photoView);

        Intent intent = getIntent();
        picturePath = intent.getStringExtra("picturePath");
        photoView.setImageURI(Uri.parse("file://" + picturePath));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {   // back 버튼 (상단 바)
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}