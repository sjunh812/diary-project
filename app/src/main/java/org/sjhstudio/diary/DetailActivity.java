package org.sjhstudio.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.sjhstudio.diary.custom.CustomDeleteDialog;
import org.sjhstudio.diary.helper.MyTheme;
import org.sjhstudio.diary.note.Note;

import java.io.File;

public class DetailActivity extends AppCompatActivity {
    public static final int RESULT_DELETE = -10;
    public static final int RESULT_UPDATE = -11;

    /* UI */
    private ImageView moodImageView;
    private TextView dateTextView;
    private TextView weekTextView;
    private TextView timeTextView;
    private ImageView pictureImageView;
    private TextView contentsTextView;
    private ImageView weatherImageView;
    private TextView locationTextView;
    private CustomDeleteDialog deleteDialog;
    private ImageView starImageView;

    /* Data */
    private Intent intent;
    private Note item;
    private Animation moodAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyTheme.applyTheme(this);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("일기상세");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        moodAnim = AnimationUtils.loadAnimation(this, R.anim.mood_icon_animation);

        initUI();

        intent = getIntent();
        processIntent();
    }

    private void initUI() {
        moodImageView = (ImageView)findViewById(R.id.moodImageView);
        moodImageView.startAnimation(moodAnim);

        dateTextView = (TextView)findViewById(R.id.dateTextView);
        weekTextView = (TextView)findViewById(R.id.weekTextView);
        timeTextView = (TextView)findViewById(R.id.timeTextView);

        pictureImageView = (ImageView)findViewById(R.id.pictureImageView);
        pictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getPicture() != null && !item.getPicture().equals("")) {
                    Intent intent  = new Intent(getApplicationContext(), PhotoActivity.class);
                    intent.putExtra("picturePath", item.getPicture());
                    startActivity(intent);
                }
            }
        });

        contentsTextView = (TextView)findViewById(R.id.contentsTextView);
        weatherImageView = (ImageView)findViewById(R.id.weatherImageView);
        locationTextView = (TextView)findViewById(R.id.locationTextView);
        starImageView = (ImageView)findViewById(R.id.starImageView);
    }

    private void processIntent() {
        if(intent != null) {
            item = (Note)intent.getSerializableExtra("item");
            int moodIndex = item.getMood();
            int weatherIndex = item.getWeather();
            int starIndex = item.getStarIndex();
            String date = item.getCreateDateStr();
            String week = item.getDayOfWeek();
            String time = item.getTime();
            String contents = item.getContents();
            String location = item.getAddress();

            setMoodImage(moodIndex);
            setWeatherImage(weatherIndex);
            setStarImage(starIndex);
            dateTextView.setText(date);
            weekTextView.setText(week);
            timeTextView.setText(time);
            contentsTextView.setText(contents);
            locationTextView.setText(location);

            if(item.getPicture() != null && !item.getPicture().equals("")) {
                pictureImageView.setVisibility(View.VISIBLE);
                Glide.with(this).load(Uri.parse("file://" + item.getPicture())).apply(RequestOptions.bitmapTransform(MainActivity.option)).into(pictureImageView);
            } else {
                pictureImageView.setVisibility(View.GONE);
            }
        }
    }

    private void setMoodImage(int index) {
        switch(index) {
            case 0:     // 화남
                moodImageView.setImageResource(R.drawable.mood_angry_color);
                break;
            case 1:     // 쿨
                moodImageView.setImageResource(R.drawable.mood_cool_color);
                break;
            case 2:     // 슬픔
                moodImageView.setImageResource(R.drawable.mood_crying_color);
                break;
            case 3:     // 아픔
                moodImageView.setImageResource(R.drawable.mood_ill_color);
                break;
            case 4:     // 웃음
                moodImageView.setImageResource(R.drawable.mood_laugh_color);
                break;
            case 5:     // 보통
                moodImageView.setImageResource(R.drawable.mood_meh_color);
                break;
            case 6:     // 나쁨
                moodImageView.setImageResource(R.drawable.mood_sad);
                break;
            case 7:     // 좋음
                moodImageView.setImageResource(R.drawable.mood_smile_color);
                break;
            case 8:     // 졸림
                moodImageView.setImageResource(R.drawable.mood_yawn_color);
                break;
            default:    // default(미소)
                moodImageView.setImageResource(R.drawable.mood_smile_color);
                break;
        }
    }

    private void setWeatherImage(int index) {
        switch(index) {
            case 0:
                weatherImageView.setImageResource(R.drawable.weather_icon_1);
                break;
            case 1:
                weatherImageView.setImageResource(R.drawable.weather_icon_2);
                break;
            case 2:
                weatherImageView.setImageResource(R.drawable.weather_icon_3);
                break;
            case 3:
                weatherImageView.setImageResource(R.drawable.weather_icon_4);
                break;
            case 4:
                weatherImageView.setImageResource(R.drawable.weather_icon_5);
                break;
            case 5:
                weatherImageView.setImageResource(R.drawable.weather_icon_6);
                break;
            case 6:
                weatherImageView.setImageResource(R.drawable.weather_icon_7);
                break;
            default:
                //weatherImageView.setImageResource(R.drawable.weather_icon_1);
                break;
        }
    }

    private void setStarImage(int index) {
        if(index == 0) {
            starImageView.setVisibility(View.GONE);
        } else {
            starImageView.setVisibility(View.VISIBLE);
        }
    }

    private void showDeleteDialog() {
        deleteDialog = new CustomDeleteDialog(this);
        deleteDialog.show();

        deleteDialog.setCancelButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.setDeleteButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = item.get_id();
                String path = item.getPicture();

                if(path != null && !path.equals("")) {
                    File file = new File(path);
                    file.delete();
                }
                deleteDialog.dismiss();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("id", id);

                setResult(RESULT_DELETE, intent);
                finish();
            }
        });

        deleteDialog.setCancelButton2OnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.setTitleTextView("일기 삭제");
        deleteDialog.setDeleteTextView("일기를 삭제하시겠습니까?\n삭제한 일기는 복구가 불가능합니다.");
        deleteDialog.setDeleteButtonText("삭제");
        deleteDialog.setCancelButton2Text("취소");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {       // back
            finish();
            return true;
        } else if(id == R.id.tab1) {        // delete
            showDeleteDialog();
        } else if(id == R.id.tab2) {        // edit
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("item", this.item);

            setResult(RESULT_UPDATE, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}