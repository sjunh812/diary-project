package org.sjhstudio.diary;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.sjhstudio.diary.adapters.PhotoAdapter;
import org.sjhstudio.diary.custom.CustomDeleteDialog;
import org.sjhstudio.diary.note.Note;
import org.sjhstudio.diary.utils.BaseActivity;
import org.sjhstudio.diary.utils.Constants;
import org.sjhstudio.diary.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class DetailActivity extends BaseActivity {

    private static final String LOG = "DetailActivity";

    private ImageView moodImageView;
    private TextView dateTextView;
    private TextView weekTextView;
    private TextView timeTextView;
    private TextView contentsTextView;
    private ImageView weatherImageView;
    private TextView locationTextView;
    private CustomDeleteDialog deleteDialog;
    private ImageView starImageView;
    private FrameLayout photoContainer;
    private PhotoAdapter photoAdapter;
    private TextView currentBanner;
    private TextView totalBanner;

    private Note item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();
        processIntent();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("일기상세");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        moodImageView = findViewById(R.id.moodImageView);
        dateTextView = findViewById(R.id.dateTextView);
        weekTextView = findViewById(R.id.weekTextView);
        timeTextView = findViewById(R.id.timeTextView);
        contentsTextView = findViewById(R.id.contentsTextView);
        weatherImageView = findViewById(R.id.weatherImageView);
        locationTextView = findViewById(R.id.locationTextView);
        starImageView = findViewById(R.id.starImageView);

        Animation moodAnim = AnimationUtils.loadAnimation(this, R.anim.mood_icon_animation);
        moodImageView.startAnimation(moodAnim);

        initPhoto();
    }

    private void initPhoto() {
        photoContainer = findViewById(R.id.photo_container);
        currentBanner = findViewById(R.id.current_banner);
        totalBanner = findViewById(R.id.total_banner);

        photoAdapter = new PhotoAdapter(this, null);
        ViewPager2 photoViewPager = findViewById(R.id.photo_view_pager);
        photoViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        photoViewPager.setAdapter(photoAdapter);
        photoViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentBanner.setText(String.valueOf(position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    private void processIntent() {
        Intent intent = getIntent();

        if (intent != null) {
            item = (Note) intent.getSerializableExtra("item");
            setMoodImage(item.getMood());
            setWeatherImage(item.getWeather());
            setStarImage(item.getStarIndex());
            dateTextView.setText(item.getCreateDateStr());
            weekTextView.setText(item.getDayOfWeek());
            timeTextView.setText(item.getTime());
            contentsTextView.setText(item.getContents());
            locationTextView.setText(item.getAddress());
            setPhoto(item.getPicture());

            // txt 파일로 내보내기
            findViewById(R.id.txt_btn).setOnClickListener(v -> {
                exportToTXTFile();
            });
        }
    }

    // txt 파일로 내보내기
    private void exportToTXTFile() {
        try {
            Date date = Utils.INSTANCE.getDateFormat().parse(item.getCreateDateStr());
            if(date == null) {
                Snackbar.make(
                        dateTextView,
                        "예상치 못한 오류가 발생했습니다. 피드백 해주시면 감사하겠습니다.",
                        1000
                ).show();

                return;
            }

            String folderName = getString(R.string.app_name);
            String subFolderName = Utils.INSTANCE.getYearFormat().format(date);
            String fileName = Utils.INSTANCE.getMonthDayFormat().format(date) + " " + item.getDayOfWeek() + "(" + item.get_id() + ").txt";
            String contents = (item.getCreateDateStr()
                    + " " + item.getDayOfWeek()
                    + "\n" + item.getTime()
                    + "\n기분 : " + Utils.INSTANCE.getMoodString(item.getMood())
                    + "\n" + item.getContents()
            );

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                System.out.println("xxx Android 30이상 : ContentValues 이용");
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Download" + "/" + folderName + "/" + subFolderName);

                Uri fileUri = getContentResolver().insert(MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values);
                ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(fileUri, "w", null);
                FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
                fos.write(contents.getBytes());
                fos.flush();
                fos.close();
                Snackbar.make(
                        dateTextView,
                        "성공적으로 txt 파일을 생성했습니다!",
                        1000
                ).show();
            } else {
                System.out.println("xxx Android 29이하 : File 이용");
                File storage = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);

                if(!storage.exists()) {
                    boolean success = storage.mkdirs();
                    if(!success) {
                        storage = null;
                    }
                }

                File file = new File(storage, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                osw.append(contents);
                osw.flush();
                osw.close();
                Snackbar.make(
                        dateTextView,
                        "성공적으로 txt 파일을 생성했습니다!",
                        1000
                ).show();
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            Snackbar.make(
                    dateTextView,
                    "예상치 못한 오류가 발생했습니다. 피드백 해주시면 감사하겠습니다.",
                    1000
            ).show();
        }
    }

    public void setPhoto(String paths) {
        if(paths != null && !paths.equals("")) {
            String[] picturePaths = paths.split(",");

            if(picturePaths.length > 0) {
                photoAdapter.setItems(new ArrayList<>(Arrays.asList(picturePaths)));
                photoAdapter.notifyDataSetChanged();
                totalBanner.setText(String.valueOf(photoAdapter.getItemCount()));
                photoContainer.setVisibility(View.VISIBLE);
            } else {
                photoContainer.setVisibility(View.GONE);
            }
        } else {
            photoContainer.setVisibility(View.GONE);
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
            case 8:     // 피곤
                moodImageView.setImageResource(R.drawable.mood_yawn_color);
                break;
            default:    // default(미소)
                Log.d(LOG, "xxx 기분 이미지 에러: index(" + index + ")");
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
                Log.d(LOG, "xxx 날씨 이미지 에러: index(" + index + ")");
                break;
        }
    }

    private void setStarImage(int index) {
        if(index == 0)  starImageView.setVisibility(View.GONE);
        else starImageView.setVisibility(View.VISIBLE);
    }

    private void showDeleteDialog() {
        deleteDialog = new CustomDeleteDialog(this);
        deleteDialog.show();
        deleteDialog.setTitleTextView("일기 삭제");
        deleteDialog.setDeleteTextView("일기를 삭제하시겠습니까?\n삭제한 일기는 복구가 불가능합니다.");
        deleteDialog.setDeleteButtonText("삭제");
        deleteDialog.setCancelButton2Text("취소");
        deleteDialog.setDeleteButtonOnClickListener( v -> {
            int id = item.get_id();
            String path = item.getPicture();

            if(path != null && !path.equals("")) {
                File file = new File(path);
                file.delete();
            }

            deleteDialog.dismiss();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("id", id);

            setResult(Constants.DETAIL_ACTIVITY_RESULT_DELETE, intent);
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home: // 뒤로가기
                finish();
                return true;
            case R.id.tab1: // 일기삭제
                showDeleteDialog();
                break;
            case R.id.tab2: // 일기수정
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("item", this.item);
                setResult(Constants.DETAIL_ACTIVITY_RESULT_UPDATE, intent);
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

}