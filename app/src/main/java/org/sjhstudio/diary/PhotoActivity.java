package org.sjhstudio.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import org.sjhstudio.diary.adapters.PhotoDetailAdapter;
import org.sjhstudio.diary.extensions.ViewExtensionKt;
import org.sjhstudio.diary.utils.BaseActivity;

import java.util.ArrayList;
import java.util.Objects;

public class PhotoActivity extends BaseActivity {
    private ArrayList<String> picturePaths = new ArrayList<>();
    private int position;

    private TextView totalText; // photo indicator total
    private TextView currentText;   // photo indicator current pos
    private PhotoDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ViewExtensionKt.enableSystemBarPadding(findViewById(R.id.root));

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("사진보기");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        totalText = findViewById(R.id.total_pos_text);
        currentText = findViewById(R.id.current_pos_text);

        processIntent();
        init();
    }

    public void processIntent() {
        picturePaths = getIntent().getStringArrayListExtra("picturePaths");
        position = getIntent().getIntExtra("position", 0);
        totalText.setText(String.valueOf(picturePaths.size()));
        currentText.setText(String.valueOf(position+1));

        adapter = new PhotoDetailAdapter(this);
        adapter.setItems(picturePaths);
    }

    public void init() {
        ViewPager2 viewPager2 = findViewById(R.id.photo_view_view_pager);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager2.setOffscreenPageLimit(1);
        viewPager2.setAdapter(adapter);
        viewPager2.setCurrentItem(position, false);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentText.setText(String.valueOf(position+1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}