package org.sjhstudio.diary.fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import org.sjhstudio.diary.MainActivity;
import org.sjhstudio.diary.R;
import org.sjhstudio.diary.adapters.PhotoAdapter;
import org.sjhstudio.diary.custom.CustomDatePickerDialog;
import org.sjhstudio.diary.custom.CustomDeleteDialog;
import org.sjhstudio.diary.helper.OnRequestListener;
import org.sjhstudio.diary.helper.OnTabItemSelectedListener;
import org.sjhstudio.diary.helper.WriteFragmentListener;
import org.sjhstudio.diary.model.LocalNote;
import org.sjhstudio.diary.note.Note;
import org.sjhstudio.diary.note.NoteDatabaseCallback;
import org.sjhstudio.diary.utils.DialogUtils;
import org.sjhstudio.diary.utils.PermissionUtils;
import org.sjhstudio.diary.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import kotlin.Unit;

public class WriteFragment extends Fragment implements WriteFragmentListener {

    private static final String LOG = "log";
    private static final String LOCAL_NOTE = "local_note";
    private static final String ADD_PHOTO_PATHS = "add_photo_paths";
    private static final String DELETE_PHOTO_PATHS = "delete_photo_paths";

    private TextView titleTextView;
    private ImageView weatherImageView;
    private ImageView weatherAddImageView;
    private LinearLayout weatherView;
    private TextView dateTextView;
    private EditText locationEditText;
    private EditText contentsEditText;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private Button button9;
    private Button curButton = null;                    // 현재 선택된 감정표현 버튼
    private ImageButton starButton;                     // 즐겨찾기 버튼
    private Button addPhotoBtn;                         // 사진추가 버튼(여러장 추가시)
    private CustomDeleteDialog deleteDialog;            // 사진 삭제시 띄워지는 커스텀 다이얼로그
    private CustomDeleteDialog deleteNoteDialog;        // 일기 삭제시 띄워지는 커스텀 다이얼로그
    private CustomDatePickerDialog pickerDialog;
    private SwipeRefreshLayout swipeRefreshLayout;      // 새로고침 뷰

    // Photo
    private ImageView addPictureImageView;
    private ViewPager2 photoViewPager;
    private PhotoAdapter photoAdapter;
    private LinearLayout photoIndicator;
    private TextView currentBanner;                         // 현재 view pager item 위치
    private TextView totalBanner;                           // view pager 총 개수

    // Listener
    private OnTabItemSelectedListener tabListener;          // 메인 액티비티에서 관리하는 하단 탭 선택 리스터
    private OnRequestListener requestListener;              // 메인 액티비티에서 현재 위치 정보를 가져오게 해주는 리스너

    // DB
    private NoteDatabaseCallback callback;                  // db 쿼리문 실행을 위한 콜백 인터페이스

    private final Calendar calendar = Calendar.getInstance();

    private Note updateItem = null;
    private Date calDate = null;
    private String filePaths = "";                          // 사진 경로 (여러개일 경우 ','로 구분!!)
    private String dateText = null;                         // yyyy-MM-dd HH:mm (사용자가 직접 일기 날짜를 지정한 경우 이용됨)
    private Uri fileUri;                                    // 카메라로 찍고 난 후 저장되는 파일의 Uri
    private int weatherIndex = -1;                          // 날씨 정보(0:맑음, 1:구름 조금, 2:구름 많음, 3:흐림, 4:비, 5:눈/비, 6:눈)
    private int moodIndex = -1;                             // 0~8 총 9개의 기분을 index 로 표현(-1은 사용자가 아무런 기분도 선택하지 않은 경우)
    private int curYear;
    private int curMonth;
    private int curDay;
    private int starIndex = 0;                              // 0: 즐겨찾기無,  1: 즐겨찾기有
    private boolean isWeatherViewOpen = false;

    private String existPaths = "";
    private ArrayList<String> addPaths = new ArrayList<>();
    private ArrayList<String> deletePaths = new ArrayList<>();

    private Animation moodAnim;
    private Animation translateLeftAnim;
    private Animation translateRightAnim;
    private Animation translateRightTitleAnim;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        combineFilePath();                                  // photoAdapter 에 저장되있는 파일경로들을 하나로 합침
        setMoodIndex();                                     // 현재 눌린 기분버튼 종류에 따라 moodIndex 설정

        LocalNote localNote = new LocalNote(
                weatherIndex,
                dateText,
                locationEditText.getText().toString(),
                moodIndex,
                contentsEditText.getText().toString(),
                filePaths,
                starIndex,
                updateItem
        );

        outState.putSerializable(LOCAL_NOTE, localNote);
        outState.putStringArrayList(ADD_PHOTO_PATHS, addPaths);
        outState.putStringArrayList(DELETE_PHOTO_PATHS, deletePaths);

        System.out.println("xxx [SavedInstanceState] local note: " + localNote);
        System.out.println("xxx [SavedInstanceState] add photo paths: " + addPaths);
        System.out.println("xxx [SavedInstanceState] delete photo paths: " + deletePaths);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTabItemSelectedListener)
            tabListener = (OnTabItemSelectedListener) context;
        if (context instanceof OnRequestListener) requestListener = (OnRequestListener) context;
        if (context instanceof NoteDatabaseCallback) callback = (NoteDatabaseCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        requestListener.stopLocationService();  // 위치탐색종료

        if (tabListener != null) tabListener = null;
        if (requestListener != null) requestListener = null;
        if (callback != null) callback = null;
    }

    public void deleteFilesCache(Boolean cancelWriting) {
        System.out.println("xxx =================== 사진경로 삭제 ===================");
        System.out.println("xxx 기존 경로: " + existPaths);
        System.out.println("xxx 저장할 경로: " + photoAdapter.getItems());
        System.out.println("xxx 삭제할 경로: " + deletePaths);
        System.out.println("xxx 추가된 경로: " + addPaths);
        System.out.println("xxx 일기작성 취소: " + cancelWriting);

        if (!cancelWriting) {   // 일반적인 경우
            for (String path : deletePaths) {
                new File(path).delete();
            }
        } else {    // 일기 작성 취소시!!
            for (String path : deletePaths) {
                if (addPaths.contains(path)) new File(path).delete();
            }

            for (String path : addPaths) {
                new File(path).delete();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestListener != null) requestListener.onRequest("checkGPS");

        if (photoAdapter.getItemCount() > 0) addPhotoBtn.setVisibility(View.VISIBLE);
        else addPhotoBtn.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_write,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAnimationUI();
        initBasicUI(view);
        initWeatherViewUI(view);
        initMoodUI(view);
        initPhoto(view);

        if (requestListener != null && updateItem == null) {
            if (PermissionUtils.INSTANCE.checkLocationPermission(requireContext())) {   // 위치권한 허용.
                if (calDate == null) requestListener.onRequest("getCurrentLocation");
                else requestListener.onRequest("getCurrentLocation", calDate);
            } else {    // 위치권한 거부.
                if (calDate == null) requestListener.getDateOnly(null);
                else requestListener.getDateOnly(calDate);
            }
        }

        if (updateItem != null) syncWithUpdateItem(true);

        initSaveInstanceState(savedInstanceState);
    }

    private void initAnimationUI() {
        MyAnimationListener animationListener = new MyAnimationListener();
        moodAnim = AnimationUtils.loadAnimation(getContext(), R.anim.mood_icon_animation);
        translateLeftAnim = AnimationUtils.loadAnimation(getContext(), R.anim.translate_left_animation);
        translateRightAnim = AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_animation);
        translateRightTitleAnim = AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_animation);
        translateLeftAnim.setAnimationListener(animationListener);
        translateRightAnim.setAnimationListener(animationListener);
        translateRightTitleAnim.setDuration(300);
    }

    private void initBasicUI(View rootView) {
        // Title
        titleTextView = rootView.findViewById(R.id.titleTextView);
        titleTextView.startAnimation(translateRightTitleAnim);
        titleTextView.setText(R.string.write_diary);

        // Date
        dateTextView = rootView.findViewById(R.id.dateTextView);
        ImageView dateTextImageView = rootView.findViewById(R.id.dateTextImageView);
        dateTextView.setOnClickListener(v -> showDatePickerDialog());
        dateTextImageView.setOnClickListener(v -> showDatePickerDialog());

        // Weather
        weatherImageView = rootView.findViewById(R.id.weatherImageView);
        weatherAddImageView = rootView.findViewById(R.id.weatherAddImageView);
        weatherView = rootView.findViewById(R.id.weatherView);                      // 날씨선택 뷰
        weatherImageView.setOnClickListener(new OpenWeatherClickListener());
        weatherAddImageView.setOnClickListener(new OpenWeatherClickListener());

        // EditText
        locationEditText = rootView.findViewById(R.id.locationTextView);            // 위치
        contentsEditText = rootView.findViewById(R.id.contentsEditText);            // 일기내용

        // Swipe refresh
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.pastel_700));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshListener());

        // Button
        starButton = rootView.findViewById(R.id.starButton);
        ImageButton saveButton = rootView.findViewById(R.id.saveButton);
        ImageButton deleteButton = rootView.findViewById(R.id.deleteButton);
        starButton.setOnClickListener(new StarButtonClickListener());
        saveButton.setOnClickListener(new SaveButtonClickListener());
        deleteButton.setOnClickListener(new DeleteButtonClickListener());
    }

    private void initWeatherViewUI(View rootView) {
        rootView.findViewById(R.id.weatherButton).setOnClickListener(new WeatherButtonClickListener(0));
        rootView.findViewById(R.id.weatherButton2).setOnClickListener(new WeatherButtonClickListener(1));
        rootView.findViewById(R.id.weatherButton3).setOnClickListener(new WeatherButtonClickListener(2));
        rootView.findViewById(R.id.weatherButton4).setOnClickListener(new WeatherButtonClickListener(3));
        rootView.findViewById(R.id.weatherButton5).setOnClickListener(new WeatherButtonClickListener(4));
        rootView.findViewById(R.id.weatherButton6).setOnClickListener(new WeatherButtonClickListener(5));
        rootView.findViewById(R.id.weatherButton7).setOnClickListener(new WeatherButtonClickListener(6));
    }

    private void initMoodUI(View rootView) {
        // 감정표현 버튼 눌림에 따른 버튼 스케일 효과를 위한 리스터
        MoodButtonClickListener moodButtonListener = new MoodButtonClickListener();     // 감정 선택에 따른 버튼 스케일 변화 리스너 초기화
        button1 = rootView.findViewById(R.id.button1);
        button2 = rootView.findViewById(R.id.button2);
        button3 = rootView.findViewById(R.id.button3);
        button4 = rootView.findViewById(R.id.button4);
        button5 = rootView.findViewById(R.id.button5);
        button6 = rootView.findViewById(R.id.button6);
        button7 = rootView.findViewById(R.id.button7);
        button8 = rootView.findViewById(R.id.button8);
        button9 = rootView.findViewById(R.id.button9);
        rootView.findViewById(R.id.angryView).setOnClickListener(moodButtonListener);
        rootView.findViewById(R.id.coolView).setOnClickListener(moodButtonListener);
        rootView.findViewById(R.id.cryingView).setOnClickListener(moodButtonListener);
        rootView.findViewById(R.id.illView).setOnClickListener(moodButtonListener);
        rootView.findViewById(R.id.laughView).setOnClickListener(moodButtonListener);
        rootView.findViewById(R.id.mehView).setOnClickListener(moodButtonListener);
        rootView.findViewById(R.id.sadView).setOnClickListener(moodButtonListener);
        rootView.findViewById(R.id.smileView).setOnClickListener(moodButtonListener);
        rootView.findViewById(R.id.yawnView).setOnClickListener(moodButtonListener);
    }

    private void initPhoto(View rootView) {
        addPictureImageView = rootView.findViewById(R.id.addPictureImageView);
        rootView.findViewById(R.id.pictureContainer).setOnClickListener(v -> showAddPhotoDialog());

        // Photo indicator
        photoIndicator = rootView.findViewById(R.id.photo_indicator);
        currentBanner = rootView.findViewById(R.id.current_banner);
        totalBanner = rootView.findViewById(R.id.total_banner);

        // ViewPager
        photoViewPager = rootView.findViewById(R.id.photo_view_pager);
        photoViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        photoAdapter = new PhotoAdapter(requireContext(), this);
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

        // Add photo
        addPhotoBtn = rootView.findViewById(R.id.add_photo_btn);
        addPhotoBtn.setOnClickListener(v -> showAddPhotoDialog());
    }

    private void initSaveInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            addPaths = savedInstanceState.getStringArrayList(ADD_PHOTO_PATHS);
            deletePaths = savedInstanceState.getStringArrayList(DELETE_PHOTO_PATHS);

            LocalNote localNote = (LocalNote) savedInstanceState.getSerializable(LOCAL_NOTE);
            Note updateNote = localNote.getUpdateNote();

            if (updateNote != null) {
                updateItem = updateNote;
                syncWithUpdateItem(!localNote.getFilePaths().isEmpty());
            }

            syncWithLocalItem(localNote);
        }
    }

    public void addPhoto(String path) {
        addPaths.add(path);
        photoAdapter.addItem(path);
        photoAdapter.notifyDataSetChanged();
        photoViewPager.setCurrentItem(photoAdapter.getItemCount() - 1);

        if (photoAdapter.getItemCount() > 0) addPhotoBtn.setVisibility(View.VISIBLE);

        addPictureImageView.setVisibility(View.GONE);
        photoViewPager.setVisibility(View.VISIBLE);
        photoIndicator.setVisibility(View.VISIBLE);
        totalBanner.setText(String.valueOf(photoAdapter.getItemCount()));
    }

    public void setPhoto(String paths) {
        if (paths != null && !paths.equals("")) {
            String[] picturePaths = paths.split(",");

            if (picturePaths.length > 0) {
                photoAdapter.setItems(new ArrayList<>(Arrays.asList(picturePaths)));
                photoAdapter.notifyDataSetChanged();
                photoViewPager.setVisibility(View.VISIBLE);
                photoIndicator.setVisibility(View.VISIBLE);
                addPictureImageView.setVisibility(View.GONE);
                totalBanner.setText(String.valueOf(photoAdapter.getItemCount()));
            } else {
                photoViewPager.setVisibility(View.GONE);
                photoIndicator.setVisibility(View.GONE);
                addPictureImageView.setVisibility(View.VISIBLE);
            }
        } else {
            photoViewPager.setVisibility(View.GONE);
            photoIndicator.setVisibility(View.GONE);
            addPictureImageView.setVisibility(View.VISIBLE);
        }
    }

    public void setWeatherImageView(String weatherStr) {
        switch (weatherStr) {
            case "맑음":
                weatherImageView.setImageResource(R.drawable.weather_icon_1);
                weatherIndex = 0;
                break;
            case "구름 조금":
                weatherImageView.setImageResource(R.drawable.weather_icon_2);
                weatherIndex = 1;
                break;
            case "구름 많음":
                weatherImageView.setImageResource(R.drawable.weather_icon_3);
                weatherIndex = 2;
                break;
            case "흐림":
                weatherImageView.setImageResource(R.drawable.weather_icon_4);
                weatherIndex = 3;
                break;
            case "비":
            case "소나기":
                weatherImageView.setImageResource(R.drawable.weather_icon_5);
                weatherIndex = 4;
                break;
            case "비/눈":
                weatherImageView.setImageResource(R.drawable.weather_icon_6);
                weatherIndex = 5;
                break;
            case "눈":
                weatherImageView.setImageResource(R.drawable.weather_icon_7);
                weatherIndex = 6;
                break;
            default:
                Log.d(LOG, "Unknown weather string: " + weatherStr);
                break;
        }
    }

    public void setWeatherImageView2(int weatherIndex) {
        if (weatherIndex == 0) weatherImageView.setImageResource(R.drawable.weather_icon_1);
        else if (weatherIndex == 1) weatherImageView.setImageResource(R.drawable.weather_icon_2);
        else if (weatherIndex == 2) weatherImageView.setImageResource(R.drawable.weather_icon_3);
        else if (weatherIndex == 3) weatherImageView.setImageResource(R.drawable.weather_icon_4);
        else if (weatherIndex == 4) weatherImageView.setImageResource(R.drawable.weather_icon_5);
        else if (weatherIndex == 5) weatherImageView.setImageResource(R.drawable.weather_icon_6);
        else if (weatherIndex == 6) weatherImageView.setImageResource(R.drawable.weather_icon_7);
        else Log.d(LOG, "Unknown weather index: " + weatherIndex);
    }

    public void setDateTextView(String date) {
        if (dateTextView != null) dateTextView.setText(date);
    }

    public void setCurDate(int year, int month, int day) {
        curYear = year;
        curMonth = month;
        curDay = day;
    }

    public void setCalDate(Date calDate) {
        this.calDate = calDate;
    }

    public void setLocationTextView(String location) {
        if (locationEditText != null) locationEditText.setText(location);
    }

    public void setMoodIndex() {
        if (curButton == button1) moodIndex = 0;
        else if (curButton == button2) moodIndex = 1;
        else if (curButton == button3) moodIndex = 2;
        else if (curButton == button4) moodIndex = 3;
        else if (curButton == button5) moodIndex = 4;
        else if (curButton == button6) moodIndex = 5;
        else if (curButton == button7) moodIndex = 6;
        else if (curButton == button8) moodIndex = 7;
        else if (curButton == button9) moodIndex = 8;
        else moodIndex = -1;
    }

    public void setMoodButton(int moodIndex) {
        if (curButton != null) {
            curButton.setScaleX(1.0f);
            curButton.setScaleY(1.0f);
            curButton.clearAnimation();
        }

        if (moodIndex == 0) setMoodButtonAnim(button1);
        else if (moodIndex == 1) setMoodButtonAnim(button2);
        else if (moodIndex == 2) setMoodButtonAnim(button3);
        else if (moodIndex == 3) setMoodButtonAnim(button4);
        else if (moodIndex == 4) setMoodButtonAnim(button5);
        else if (moodIndex == 5) setMoodButtonAnim(button6);
        else if (moodIndex == 6) setMoodButtonAnim(button7);
        else if (moodIndex == 7) setMoodButtonAnim(button8);
        else setMoodButtonAnim(button9);
    }

    private void setMoodButtonAnim(Button button) {
        button.setScaleX(1.4f);
        button.setScaleY(1.4f);
        button.startAnimation(moodAnim);
        curButton = button;
    }

    private void setStarButton(int index) {
        if (index == 0) {
            starIndex = 0;
            Glide.with(WriteFragment.this)
                    .load(ContextCompat.getDrawable(requireContext(), R.drawable.star_icon))
                    .into(starButton);
        } else {
            starIndex = 1;
            Glide.with(WriteFragment.this)
                    .load(ContextCompat.getDrawable(requireContext(), R.drawable.star_icon_color))
                    .into(starButton);
        }
    }

    public void setSwipeRefresh(boolean isRefresh) {
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(isRefresh);
    }

    public void setUpdateItem(Note item) {
        updateItem = item;
    }

    @SuppressLint("NonConstantResourceId")
    private View getSelectedMoodButton(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.angryView:
                return button1;
            case R.id.coolView:
                return button2;
            case R.id.cryingView:
                return button3;
            case R.id.illView:
                return button4;
            case R.id.laughView:
                return button5;
            case R.id.mehView:
                return button6;
            case R.id.sadView:
                return button7;
            case R.id.smileView:
                return button8;
            case R.id.yawnView:
                return button9;
        }

        return null;
    }

    private void syncWithLocalItem(LocalNote item) {
        weatherIndex = item.getWeatherIndex();
        contentsEditText.setText(item.getContents());

        if (item.getDate() != null) {
            try {
                Date date = Utils.INSTANCE.getDateFormat2().parse(item.getDate());

                if (date != null) {
                    curYear = Utils.INSTANCE.getYear(date);
                    curMonth = Utils.INSTANCE.getMonth(date);
                    curDay = Utils.INSTANCE.getDay(date);
                    setDateTextView(Utils.INSTANCE.getDateFormat().format(date));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setLocationTextView(item.getAddress());
        setStarButton(item.getStarIndex());
        setPhoto(item.getFilePaths());

        if (item.getWeatherIndex() != -1) setWeatherImageView2(item.getWeatherIndex());
        if (item.getMoodIndex() != -1) setMoodButton(item.getMoodIndex());
    }

    private void syncWithUpdateItem(Boolean updatePhoto) {
        titleTextView.setText(R.string.edit_diary);

        String date = updateItem.getCreateDateStr();
        String date2Str = updateItem.getCreateDateStr2();
        String address = updateItem.getAddress();
        String paths = updateItem.getPicture();             // ","으로 구분되어있는 파일경로 묶음
        String contents = updateItem.getContents();
        int weatherIndex = updateItem.getWeather();
        int moodIndex = updateItem.getMood();
        int starIndex = updateItem.getStarIndex();

        try {
            Date date2 = Utils.INSTANCE.getDateFormat2().parse(date2Str);

            if (date2 != null) {
                curYear = Utils.INSTANCE.getYear(date2);
                curMonth = Utils.INSTANCE.getMonth(date2);
                curDay = Utils.INSTANCE.getDay(date2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.weatherIndex = weatherIndex;
        this.contentsEditText.setText(contents);
        this.existPaths = paths;

        setDateTextView(date);
        setLocationTextView(address);
        setWeatherImageView2(weatherIndex);
        setStarButton(starIndex);
        setMoodButton(moodIndex);

        if (updatePhoto) setPhoto(paths);
    }

    public Boolean isEmptyContent() {
        return curButton == null
                && (contentsEditText == null || contentsEditText.getText() == null || contentsEditText.getText().toString().equals(""))
                && (photoAdapter.getItemCount() < 1 && deletePaths.isEmpty());
    }

    public void combineFilePath() {
        filePaths = "";

        for (int i = 0; i < photoAdapter.getItemCount(); i++) {
            String filePath = photoAdapter.getItems().get(i);

            if (i == photoAdapter.getItemCount() - 1) filePaths += filePath;
            else filePaths += filePath + ",";
        }
    }

    public void showDeleteNoteDialog() {
        deleteNoteDialog = new CustomDeleteDialog(requireContext());
        deleteNoteDialog.show();
        deleteNoteDialog.setCancelable(true);
        deleteNoteDialog.setTitleTextView("일기 삭제");
        deleteNoteDialog.setDeleteTextView("일기를 삭제하시겠습니까?");
        deleteNoteDialog.setDeleteButtonOnClickListener(v -> {
            deleteNoteDialog.dismiss();
            deleteFilesCache(false);

            for (String path : photoAdapter.getItems()) {
                new File(path).delete();
            }

            int id = updateItem.get_id();

            callback.deleteDB(id);  // 해당 db 삭제
            tabListener.setIsSelected(true);
            tabListener.onTabSelected(0);
        });
    }

    public void showDatePickerDialog() {
        pickerDialog = new CustomDatePickerDialog(requireContext(), curYear, curMonth, curDay);
        pickerDialog.show();
        pickerDialog.setCancelable(true);
        pickerDialog.setCancelButtonOnClickListener(v -> pickerDialog.dismiss());
        pickerDialog.setOkButtonOnClickListener(v -> {
            pickerDialog.dismiss();
            curYear = pickerDialog.getCurYear();
            curMonth = pickerDialog.getCurMonth() + 1;
            curDay = pickerDialog.getCurDay();

            calendar.set(Calendar.YEAR, curYear);
            calendar.set(Calendar.MONTH, curMonth - 1);
            calendar.set(Calendar.DAY_OF_MONTH, curDay);

            Date newDate = calendar.getTime();

            dateText = Utils.INSTANCE.getDateFormat2().format(newDate);
            dateTextView.setText(Utils.INSTANCE.getDateFormat().format(newDate));
        });
    }

    @Override
    public void showAddPhotoDialog() {
        if (!PermissionUtils.INSTANCE.checkStoragePermission(requireContext())
                || !PermissionUtils.INSTANCE.checkCameraPermission(requireContext())
        ) {
            DialogUtils.INSTANCE.showStoragePermissionDialog(
                    requireContext(),
                    () -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                        intent.setData(uri);
                        requireActivity().startActivity(intent);

                        return Unit.INSTANCE;
                    });
        } else {
            DialogUtils.INSTANCE.showAddPhotoDialog(
                    requireContext(),
                    () -> {
                        showCameraActivity();
                        return Unit.INSTANCE;
                    },
                    () -> {
                        showAlbumActivity();
                        return Unit.INSTANCE;
                    });
        }
    }

    @Override
    public void showDeletePictureDialog(int position) {
        deleteDialog = new CustomDeleteDialog(requireContext());
        deleteDialog.show();
        deleteDialog.setCancelable(true);
        deleteDialog.setDeleteButtonOnClickListener(v -> {
            deleteDialog.dismiss();
            deletePaths.add(photoAdapter.getItems().get(position));
            photoAdapter.getItems().remove(position);
            photoAdapter.notifyDataSetChanged();

            if (photoAdapter.getItemCount() < 1) {  // 사진을 모두 삭제한 경우
                addPhotoBtn.setVisibility(View.GONE);
                photoViewPager.setVisibility(View.GONE);
                photoIndicator.setVisibility(View.GONE);
                addPictureImageView.setVisibility(View.VISIBLE);
            }

            totalBanner.setText(String.valueOf(photoAdapter.getItemCount()));
        });
    }

    public void showCameraActivity() {
        Uri uri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = createUri();
        } else {
            File file = createFile();
            uri = FileProvider.getUriForFile(requireContext(), "org.sjhstudio.diary.fileprovider", file);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            ((MainActivity) requireActivity()).cameraResult.launch(intent);
        }
    }

    public void showAlbumActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((MainActivity) requireActivity()).albumResult.launch(intent);
    }

    private File createFile() {
        // 파일이름 생성(yyyyMMdd_)
        String fileName = Utils.INSTANCE.getDateFormat3().format(new Date()) + "_" + System.currentTimeMillis();

        // 파일 생성
        File storageFile = Environment.getExternalStorageDirectory();

        return new File(storageFile, fileName);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Uri createUri() {
        // 파일이름 생성(yyyyMMdd_)
        String fileName = Utils.INSTANCE.getDateFormat3().format(new Date()) + "_" + System.currentTimeMillis();

        // ContentValues 생성
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Diary");

        fileUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return fileUri;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    class SaveButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (tabListener != null) {
                if (curButton == null) {
                    Toast.makeText(getContext(), "오늘의 기분을 골라주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                combineFilePath();                                  // photoAdapter 에 저장되있는 파일경로들을 하나로 합침
                setMoodIndex();                                     // 현재 눌린 기분버튼 종류에 따라 moodIndex 설정

                String contents = contentsEditText.getText().toString();
                String address = locationEditText.getText().toString();

                if (updateItem == null) {
                    Object[] objs;

                    if (dateText != null) {
                        String date = dateText + " " + Utils.INSTANCE.getTimeFormat2().format(new Date());
                        objs = new Object[]{weatherIndex, address, "", "", contents, moodIndex, filePaths, curYear, curMonth, date, starIndex};
                        callback.insertDB2(objs);
                    } else {
                        if (calDate != null) {
                            String date = Utils.INSTANCE.getDateFormat2().format(calDate) + " " + Utils.INSTANCE.getTimeFormat2().format(new Date());
                            objs = new Object[]{weatherIndex, address, "", "", contents, moodIndex, filePaths, curYear, curMonth, date, starIndex};
                            callback.insertDB2(objs);
                        } else {
                            objs = new Object[]{weatherIndex, address, "", "", contents, moodIndex, filePaths, curYear, curMonth, starIndex};
                            callback.insertDB(objs);
                        }
                    }
                } else {
                    if (filePaths != null && !filePaths.equals("")) {
                        updateItem.setPicture(filePaths);
                    } else {
                        updateItem.setPicture("");
                    }

                    updateItem.setWeather(weatherIndex);
                    updateItem.setAddress(locationEditText.getText().toString());
                    updateItem.setContents(contents);
                    updateItem.setMood(moodIndex);
                    updateItem.setStarIndex(starIndex);

                    if (dateText != null) {
                        String date = dateText + " " + Utils.INSTANCE.getTimeFormat2().format(new Date());
                        updateItem.setYear(curYear);
                        updateItem.setDay(curMonth);
                        updateItem.setCreateDateStr2(date);
                        callback.updateDB2(updateItem);
                    } else {
                        callback.updateDB(updateItem);
                    }
                }

                deleteFilesCache(false);

                tabListener.setIsSelected(true);
                tabListener.onTabSelected(0);    // 일기목록 프래그먼트로 이동
            }
        }
    }

    class DeleteButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (tabListener != null) {
                if (updateItem == null && requestListener != null) {
                    requestListener.onRequest("showStopWriteDialog");
                } else {
                    showDeleteNoteDialog();
                }
            }
        }
    }

    class StarButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (starIndex == 0) {
                starIndex = 1;
                Glide.with(WriteFragment.this)
                        .load(ContextCompat.getDrawable(requireContext(), R.drawable.star_icon_color))
                        .into(starButton);
            } else {
                starIndex = 0;
                Glide.with(WriteFragment.this)
                        .load(ContextCompat.getDrawable(requireContext(), R.drawable.star_icon))
                        .into(starButton);
            }
        }
    }

    class MoodButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Button selectButton = (Button) getSelectedMoodButton(v);

            if (selectButton != null) {
                if (curButton == null) {
                    setMoodButtonAnim(selectButton);
                } else if (curButton == selectButton) {
                    selectButton.setScaleX(1.0f);
                    selectButton.setScaleY(1.0f);
                    selectButton.clearAnimation();
                    curButton = null;
                } else {
                    curButton.setScaleX(1.0f);
                    curButton.setScaleY(1.0f);
                    curButton.clearAnimation();
                    setMoodButtonAnim(selectButton);
                }
            }
        }
    }

    class MyAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (isWeatherViewOpen) {
                weatherAddImageView.setImageResource(R.drawable.navigate_down);
                weatherView.setVisibility(View.GONE);
            }

            isWeatherViewOpen = !isWeatherViewOpen;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    class OpenWeatherClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isWeatherViewOpen) {
                weatherView.startAnimation(translateLeftAnim);
            } else {
                weatherView.setVisibility(View.VISIBLE);
                weatherAddImageView.setImageResource(R.drawable.navigate_up);
                weatherView.startAnimation(translateRightAnim);
            }
        }
    }

    class WeatherButtonClickListener implements View.OnClickListener {

        private final int _weatherIndex;

        public WeatherButtonClickListener(int _weatherIndex) {
            this._weatherIndex = _weatherIndex;
        }

        @Override
        public void onClick(View v) {
            weatherIndex = _weatherIndex;
            setWeatherImageView2(weatherIndex);

            if (isWeatherViewOpen) {
                weatherView.startAnimation(translateLeftAnim);
            } else {
                weatherView.setVisibility(View.VISIBLE);
                weatherView.startAnimation(translateRightAnim);
            }
        }
    }

    class SwipeRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            if (requestListener != null) {
                if (PermissionUtils.INSTANCE.checkLocationPermission(requireContext())) {
                    requestListener.onRequest("checkGPS");

                    if (Utils.INSTANCE.checkGPS(requireContext())) {
                        if (calDate == null) requestListener.onRequest("getCurrentLocation");
                        else requestListener.onRequest("getCurrentLocation", calDate);
                    } else {
                        setSwipeRefresh(false);
                    }
                } else {
                    Toast.makeText(
                            getContext(),
                            "날씨 및 위치정보를 가져오기 위해 위치권한이 필요합니다.\n" + "설정>위치>앱 권한에서 허용해주세요.",
                            Toast.LENGTH_SHORT
                    ).show();
                    setSwipeRefresh(false);
                }
            } else {
                setSwipeRefresh(false);
            }
        }
    }
}
