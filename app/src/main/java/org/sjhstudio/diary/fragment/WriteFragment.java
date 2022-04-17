package org.sjhstudio.diary.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.FrameLayout;
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
import org.sjhstudio.diary.adapters.PhotoAdapter;
import org.sjhstudio.diary.custom.CustomDatePickerDialog;
import org.sjhstudio.diary.custom.CustomDeleteDialog;
import org.sjhstudio.diary.custom.CustomDialog;
import org.sjhstudio.diary.R;
import org.sjhstudio.diary.helper.OnRequestListener;
import org.sjhstudio.diary.helper.OnTabItemSelectedListener;
import org.sjhstudio.diary.helper.WriteFragmentListener;
import org.sjhstudio.diary.note.Note;
import org.sjhstudio.diary.note.NoteDatabaseCallback;
import org.sjhstudio.diary.utils.DialogUtils;
import org.sjhstudio.diary.utils.PermissionUtils;
import org.sjhstudio.diary.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import kotlin.Unit;

public class WriteFragment extends Fragment implements WriteFragmentListener {

    private static final String LOG = "WriteFragment";  // log

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
    private CustomDialog dialog;                        // 사진 추가시 띄워지는 커스텀 다이얼로그
    private CustomDeleteDialog deleteDialog;            // 사진 삭제시 띄워지는 커스텀 다이얼로그
    private CustomDeleteDialog deleteNoteDialog;        // 일기 삭제시 띄워지는 커스텀 다이얼로그
    private CustomDatePickerDialog pickerDialog;
    private SwipeRefreshLayout swipeRefreshLayout;      // 새로고침 뷰

    /** 사진관련 **/
    private ImageView addPictureImageView;
    private FrameLayout pictureContainer;               // 아무 사진도 넣지않은 경우, 사진 추가시 클릭 이벤트가 발생하는 container
    private ViewPager2 photoViewPager;
    private PhotoAdapter photoAdapter;
    private LinearLayout photoIndicator;
    private TextView currentBanner;                     // 현재 view pager item 위치
    private TextView totalBanner;                       // view pager 총 개수

    /** 리스너 **/
    private OnTabItemSelectedListener tabListener;      // 메인 액티비티에서 관리하는 하단 탭 선택 리스터
    private OnRequestListener requestListener;          // 메인 액티비티에서 현재 위치 정보를 가져오게 해주는 리스너
    private MoodButtonClickListener moodButtonListener; // 감정표현 버튼 눌림에 따른 버튼 스케일 효과를 위한 리스터

    /** DB **/
    private NoteDatabaseCallback callback;              // db 쿼리문 실행을 위한 콜백 인터페이스

    /** 포멧 **/
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");     // 카메라 앱을 이용해 촬영시 저장되는 파일이름에 사용될 날짜포멧 ex)20210418

    /** 애니메이션 **/
    private Animation moodAnim;
    private Animation translateLeftAnim;
    private Animation translateRightAnim;
    private Animation translateRightTitleAnim;

    /** 데이터 **/
    private Context context;
    private Note updateItem = null;
    private Date calDate = null;
    private String address = "";                        // 위치 정보
    private String contents = "";                       // 일기 내용
    private String filePaths = "";                      // 사진 경로 (여러개일 경우 ','로 구분!!)
    private String recentFilePaths = "";                // 사진이 있는 일기 수정 시, 기존 사진 경로 (백업 필요)
    private String dateText = null;                     // yyyy-MM-dd HH:mm (사용자가 직접 일기 날짜를 지정한 경우 이용됨)
    private Uri fileUri;                                // 카메라로 찍고 난 후 저장되는 파일의 Uri
    private Object[] objs;                              // db 에 데이터 삽입을 위해 필요한 Object[] 객체
    private int weatherIndex = -1;                      // 날씨 정보(0:맑음, 1:구름 조금, 2:구름 많음, 3:흐림, 4:비, 5:눈/비, 6:눈)
    private int moodIndex = -1;                         // 0~8 총 9개의 기분을 index 로 표현(-1은 사용자가 아무런 기분도 선택하지 않은 경우)
    private int curYear;
    private int curMonth;
    private int curDay;
    private int starIndex = 0;                          // 0 = 즐겨찾기 x, 1 = 즐겨찾기
    private boolean isWeatherViewOpen = false;
    public boolean needDeleteCache = true;            // 캐시 삭제 필요 여부
    private ArrayList<String> deletePath = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

        if(context instanceof OnTabItemSelectedListener) tabListener = (OnTabItemSelectedListener)context;
        if(context instanceof OnRequestListener) requestListener = (OnRequestListener)context;
        if(context instanceof NoteDatabaseCallback)  callback = (NoteDatabaseCallback)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        requestListener.stopLocationService();  // 위치탐색종료
        deleteFilesCache(); // 남아있는 파일캐시 삭제

        if(context != null) {
            context = null;
            tabListener = null;
            requestListener = null;
            callback = null;
        }
    }

    /**
     * 파일캐쉬 삭제
     * (여러개 사진경로)
     */
    public void deleteFilesCache() {
        if(updateItem != null) {
            for(String filePath : photoAdapter.getItems()) {
                Log.d(LOG, "xxx picture path : " + filePath);
                if(!recentFilePaths.contains(filePath)) {
                    Log.d(LOG, "    delete picture path : " + filePath + "\n");
                    new File(filePath).delete();
                }
            }
            for(String deletePath : deletePath) {
                Log.d(LOG, "xxx delete dialog picture path : " + deletePath + "\n");
                new File(deletePath).delete();
            }
        } else {
            if(needDeleteCache) {
                if(filePaths == null || filePaths.equals("")) combineFilePath();
                if(filePaths != null && !filePaths.equals("")) {
                    String[] picturePaths = filePaths.split(",");

                    for (String picturePath : picturePaths) {
                        Log.d(LOG, "xxx delete picture path : " + picturePath);
                        File file = new File(picturePath);
                        file.delete();
                    }

                    filePaths = "";
                }
            }
        }
    }

    /**
     * 파일캐쉬 삭제
     * (단일 경로)
     * @param filePath // 경로명
     */
    public void deleteFileCache(String filePath) {
        if(filePath != null && !filePath.equals("")) {
            File file = new File(filePath);
            file.delete();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 사용자가 GPS 기능을 켰는지 확인
        if(requestListener != null) {
            Log.d(LOG, "onRequest(checkGPS) 호출됨.");
            requestListener.onRequest("checkGPS");
        }
        if(photoAdapter.getItemCount() > 0) addPhotoBtn.setVisibility(View.VISIBLE);
        else addPhotoBtn.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_write, container, false);
        initAnimationUI();
        initBasicUI(rootView);
        initWeatherViewUI(rootView);
        initMoodUI(rootView);
        initPhoto(rootView);;

        if(requestListener != null && updateItem == null) {
            if(PermissionUtils.Companion.checkLocationPermission(requireContext())) {
                // 위치권한 허용.
              if(calDate == null) requestListener.onRequest("getCurrentLocation");  // 메인 액티비티로부터 현재위치정보 가져오기
              else requestListener.onRequest("getCurrentLocation", calDate);    // 메인 액티비티로부터 현재위치정보 가져오기(달력에서 넘어온 Date 사용)
            } else {
                // 위치권한 거부.
                if(calDate == null) requestListener.getDateOnly(null);
                else requestListener.getDateOnly(calDate);

//                Toast.makeText(getContext(), "날씨 및 위치정보를 가져오려면 위치권한이 필요합니다.\n위치권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
            }
        }

        if(updateItem != null) setUpdateItem();

        return rootView;
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
        // title
        TextView titleTextView = rootView.findViewById(R.id.titleTextView);
        titleTextView.startAnimation(translateRightTitleAnim);
        if(updateItem == null) titleTextView.setText("일기작성");
        else titleTextView.setText("일기수정");

        // 날짜 관련
        dateTextView = rootView.findViewById(R.id.dateTextView);
        ImageView dateTextImageView = rootView.findViewById(R.id.dateTextImageView);
        dateTextView.setOnClickListener(v -> setDatePickerDialog());
        dateTextImageView.setOnClickListener(v -> setDatePickerDialog());

        // 날씨 관련
        weatherImageView = rootView.findViewById(R.id.weatherImageView);
        weatherAddImageView = rootView.findViewById(R.id.weatherAddImageView);
        weatherView = rootView.findViewById(R.id.weatherView);                // 날씨를 직접 설정시 나타나는 뷰
        weatherImageView.setOnClickListener(new OpenWeatherClickListener());
        weatherAddImageView.setOnClickListener(new OpenWeatherClickListener());

        // edit text
        locationEditText = rootView.findViewById(R.id.locationTextView);          // 위치를 나타내는 edit text
        contentsEditText = rootView.findViewById(R.id.contentsEditText);          // 일기 내용

        // 새로고침
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.pastel_700));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshListener());

        // 버튼
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
        moodButtonListener = new MoodButtonClickListener();     // 감정 선택에 따른 버튼 스케일 변화 리스너 초기화
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
        // 기본 photo UI
        pictureContainer = rootView.findViewById(R.id.pictureContainer);
        addPictureImageView = rootView.findViewById(R.id.addPictureImageView);
        pictureContainer.setOnClickListener( v -> showAddPhotoDialog());

        // photo indicator 관련
        photoIndicator = rootView.findViewById(R.id.photo_indicator);
        currentBanner = rootView.findViewById(R.id.current_banner);
        totalBanner = rootView.findViewById(R.id.total_banner);

        // photo view pager 관련
        photoViewPager = rootView.findViewById(R.id.photo_view_pager);
        photoViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        photoAdapter = new PhotoAdapter(context, this);
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

        // 사진추가(여러장)
        addPhotoBtn = rootView.findViewById(R.id.add_photo_btn);
        addPhotoBtn.setOnClickListener(v -> showAddPhotoDialog());
    }

    /** 문자열로 나타낸 날씨를 통해 날씨 이미지 설정 **/
    public void setWeatherImageView(String weatherStr) {
        if(weatherStr.equals("맑음")) {
            weatherImageView.setImageResource(R.drawable.weather_icon_1);
            weatherIndex = 0;
        } else if(weatherStr.equals("구름 조금")) {
            weatherImageView.setImageResource(R.drawable.weather_icon_2);
            weatherIndex = 1;
        } else if(weatherStr.equals("구름 많음")) {
            weatherImageView.setImageResource(R.drawable.weather_icon_3);
            weatherIndex = 2;
        } else if(weatherStr.equals("흐림")) {
            weatherImageView.setImageResource(R.drawable.weather_icon_4);
            weatherIndex = 3;
        } else if(weatherStr.equals("비")) {
            weatherImageView.setImageResource(R.drawable.weather_icon_5);
            weatherIndex = 4;
        } else if(weatherStr.equals("비/눈")) {
            weatherImageView.setImageResource(R.drawable.weather_icon_6);
            weatherIndex = 5;
        } else if(weatherStr.equals("눈")){
            weatherImageView.setImageResource(R.drawable.weather_icon_7);
            weatherIndex = 6;
        } else if(weatherStr.equals("소나기")) {
            weatherImageView.setImageResource(R.drawable.weather_icon_5);
            weatherIndex = 4;
        } else {
            Log.d(LOG, "Unknown weather string : " + weatherStr);
        }
    }

    /** 날씨 인덱스를 통해 날씨 이미지 설정 **/
    public void setWeatherImageView2(int weatherIndex) {
        if(weatherIndex == 0) {
            weatherImageView.setImageResource(R.drawable.weather_icon_1);
        } else if(weatherIndex == 1) {
            weatherImageView.setImageResource(R.drawable.weather_icon_2);
        } else if(weatherIndex == 2) {
            weatherImageView.setImageResource(R.drawable.weather_icon_3);
        } else if(weatherIndex == 3) {
            weatherImageView.setImageResource(R.drawable.weather_icon_4);
        } else if(weatherIndex == 4) {
            weatherImageView.setImageResource(R.drawable.weather_icon_5);
        } else if(weatherIndex == 5) {
            weatherImageView.setImageResource(R.drawable.weather_icon_6);
        } else if(weatherIndex == 6){
            weatherImageView.setImageResource(R.drawable.weather_icon_7);
        } else {
            Log.d(LOG, "Unknown weather index : " + weatherIndex);
        }
    }

    public void setLocationTextView(String location) {
        locationEditText.setText(location);
        address = location;
    }

    public void setDateTextView(String date) {
        if(dateTextView != null) dateTextView.setText(date);
    }

    /** photo adapter 세팅 (사진 추가 시) **/
    public void setPhotoAdapter(String filePath) {
        photoAdapter.addItem(filePath);
        photoAdapter.notifyDataSetChanged();
        photoViewPager.setCurrentItem(photoAdapter.getItemCount() - 1);
        if(photoAdapter.getItemCount() > 0) addPhotoBtn.setVisibility(View.VISIBLE);
        addPictureImageView.setVisibility(View.GONE);
        photoViewPager.setVisibility(View.VISIBLE);
        photoIndicator.setVisibility(View.VISIBLE);
        totalBanner.setText(String.valueOf(photoAdapter.getItemCount()));
    }

    public void setCurDate(int curYear, int curMonth, int curDay) {
        this.curYear = curYear;
        this.curMonth = curMonth;
        this.curDay = curDay;
    }

    public void setContents() {
        contents = contentsEditText.getText().toString();
    }

    /** curButton 객체 값으로 기분 인덱스 설정 **/
    public void setMoodIndex() {
        if(curButton == null) {
            // 사용자가 아무런 기분도 선택하지 않는 상황
            // 선택할 수 있도록 토스트바를 띄워줘야함
            moodIndex = -1;
        } else if(curButton == button1) {
            moodIndex = 0;
        } else if(curButton == button2) {
            moodIndex = 1;
        } else if(curButton == button3) {
            moodIndex = 2;
        } else if(curButton == button4) {
            moodIndex = 3;
        } else if(curButton == button5) {
            moodIndex = 4;
        } else if(curButton == button6) {
            moodIndex = 5;
        } else if(curButton == button7) {
            moodIndex = 6;
        } else if(curButton == button8) {
            moodIndex = 7;
        } else {
            moodIndex = 8;
        }
    }

    /** 기분 인덱스를 이용하여 버튼 애니메이션 및 curButton 설정 **/
    public void setMoodButton(int moodIndex) {
        if (moodIndex == 0) {
            setButtonAnim(button1);
        } else if (moodIndex == 1) {
            setButtonAnim(button2);
        } else if (moodIndex == 2) {
            setButtonAnim(button3);
        } else if (moodIndex == 3) {
            setButtonAnim(button4);
        } else if (moodIndex == 4) {
            setButtonAnim(button5);
        } else if (moodIndex == 5) {
            setButtonAnim(button6);
        } else if (moodIndex == 6) {
            setButtonAnim(button7);
        } else if (moodIndex == 7) {
            setButtonAnim(button8);
        } else {
            setButtonAnim(button9);
        }
    }

    private void setButtonAnim(Button button) {
        button.setScaleX(1.4f);
        button.setScaleY(1.4f);
        button.startAnimation(moodAnim);
        curButton = button;
    }

    public void setCalDate(Date calDate) {
        this.calDate = calDate;
    }

    public void setSwipeRefresh(boolean isRefresh) {
        if(swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(isRefresh);
    }

    /** 일기삭제 Dialog (일기 수정 시에만 나타나는 다이얼로그) **/
    public void setDeleteNoteDialog() {
        deleteNoteDialog = new CustomDeleteDialog(requireContext());
        deleteNoteDialog.show();
        deleteNoteDialog.setCancelable(true);
        deleteNoteDialog.setTitleTextView("일기 삭제");
        deleteNoteDialog.setDeleteTextView("일기를 삭제하시겠습니까?");
        deleteNoteDialog.setDeleteButtonOnClickListener(v -> {
            deleteNoteDialog.dismiss();

            int id = updateItem.get_id();
            String paths = updateItem.getPicture();

            if(paths != null && !paths.equals("")) {
                String picturePaths[] = paths.split(",");

                for(int i = 0; i < picturePaths.length; i++) {
                    File file = new File(picturePaths[i]);
                    file.delete();
                }
            }

            callback.deleteDB(id);                  // 해당 db 삭제
            tabListener.setIsSelected(true);
            tabListener.onTabSelected(0);
        });
    }

    /** 날짜선택 Dialog **/
    public void setDatePickerDialog() {
        pickerDialog = new CustomDatePickerDialog(requireContext(), curYear, curMonth, curDay);
        pickerDialog.show();
        pickerDialog.setCancelable(true);

        pickerDialog.setCancelButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerDialog.dismiss();
            }
        });

        pickerDialog.setOkButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerDialog.dismiss();

                curYear = pickerDialog.getCurYear();
                curMonth = pickerDialog.getCurMonth() + 1;
                curDay = pickerDialog.getCurDay();
                Date newDate = new Date(curYear - 1900, curMonth - 1, curDay);
                dateText = MainActivity.dateFormat2.format(newDate);
                dateTextView.setText(MainActivity.dateFormat.format(newDate));
            }
        });
    }

    /** 사진삭제 Dialog **/
    @Override
    public void setDeletePictureDialog(int position) {
        deleteDialog = new CustomDeleteDialog(requireContext());
        deleteDialog.show();
        deleteDialog.setCancelable(true);

        deleteDialog.setDeleteButtonOnClickListener(v -> {
            deleteDialog.dismiss();

            if(updateItem != null) {            // 일기 수정 시
                deletePath.add(photoAdapter.getItems().get(position));
            } else {                            // 일기 처음 작성 시
                deleteFileCache(photoAdapter.getItems().get(position));     // 삭제한 사진경로의 파일 삭제
            }

            photoAdapter.getItems().remove(position);
            photoAdapter.notifyDataSetChanged();

            if(photoAdapter.getItemCount() < 1) {                       // 사진을 모두 삭제하여 사진이 없는 경우
                addPhotoBtn.setVisibility(View.GONE);
                photoViewPager.setVisibility(View.GONE);
                photoIndicator.setVisibility(View.GONE);
                addPictureImageView.setVisibility(View.VISIBLE);
            }
            totalBanner.setText(String.valueOf(photoAdapter.getItemCount()));
        });
    }

    /**
     * 사진촬영
     * (결과는 MainActivity 로 전달.)
     */
    public void showCameraActivity() {
        Uri uri;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = createUri();
        } else {
            File file = createFile();
            uri = FileProvider.getUriForFile(requireContext(), "org.sjhstudio.diary.fileprovider", file);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        if(intent.resolveActivity(requireContext().getPackageManager()) != null) {
            ((MainActivity)requireActivity()).cameraResult.launch(intent);
        }
    }

    /**
     * 앨범 가져오기
     * (결과는 MainActivity 로 전달.)
     */
    public void showAlbumActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((MainActivity)requireActivity()).albumResult.launch(intent);
    }

    public void setItem(Note item) {
        updateItem = item;
    }

    public void setUpdateItem() {
        String date = updateItem.getCreateDateStr();
        String date2Str = updateItem.getCreateDateStr2();
        String address = updateItem.getAddress();
        String paths = updateItem.getPicture();             // ","으로 구분되어있는 파일경로 묶음
        String contents = updateItem.getContents();
        int weatherIndex = updateItem.getWeather();
        int moodIndex = updateItem.getMood();
        int starIndex = updateItem.getStarIndex();
        try {
            Date date2 = MainActivity.dateFormat2.parse(date2Str);
            curYear = date2.getYear() + 1900;
            curMonth = date2.getMonth() + 1;
            curDay = date2.getDate();
        } catch(Exception e) {
            e.printStackTrace();
        }

        this.weatherIndex = weatherIndex;
        this.contentsEditText.setText(contents);
        setDateTextView(date);
        setLocationTextView(address);
        setWeatherImageView2(weatherIndex);
        checkStarButton(starIndex);
        setMoodButton(moodIndex);
        setUpdatePhoto(paths);
        needDeleteCache = false;
    }

    public void setUpdatePhoto(String paths) {
        if(paths != null && !paths.equals("")) {
            String[] picturePaths = paths.split(",");
            if(picturePaths.length > 0) {
                recentFilePaths = paths;      // 수정하기 취소 시, 기존에 올렸던 파일을 복구하기위해 recentFilePath 에 미리 경로를 저장

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

    public Uri getFileUri() {
        return fileUri;
    }

    private View getSelectedMoodButton(View v) {
        int id = v.getId();

        switch(id) {
            case R.id.angryView: return button1;
            case R.id.coolView: return button2;
            case R.id.cryingView: return button3;
            case R.id.illView: return button4;
            case R.id.laughView: return button5;
            case R.id.mehView: return button6;
            case R.id.sadView: return button7;
            case R.id.smileView: return button8;
            case R.id.yawnView: return button9;
        }

        return null;
    }

    /** 사용자가 기분을 골랐는지 판단 **/
    public boolean isEmpty() {
        if(curButton == null) {
            return true;
        }
        return false;
    }

    private void checkStarButton(int index) {
        if(index == 0) {
            starIndex = 0;
            Glide.with(WriteFragment.this).load(getResources().getDrawable(R.drawable.star_icon)).into(starButton);
        } else {
            starIndex = 1;
            Glide.with(WriteFragment.this).load(getResources().getDrawable(R.drawable.star_icon_color)).into(starButton);
        }
    }

    /** 안드로이드 10 이전 **/
    private File createFile() {
        // 파일 이름 생성
        Date date = new Date();
        String fileName = format.format(date) + "_" + System.currentTimeMillis();

        // 파일 경로 생성
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();

            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Diary");

            fileUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            String[] filePathColums = { MediaStore.MediaColumns.DATA };     /** 안드로이드 Q(10)이상 부터 쓸 수 없는 코드 **/

            Cursor cursor = getContext().getContentResolver().query(fileUri, filePathColums, null, null);
            cursor.moveToFirst();

            int index = cursor.getColumnIndex(filePathColums[0]);
            String filePath = cursor.getString(index);

            cursor.close();
            return new File(filePath);
        } else {
            File storageFile = Environment.getExternalStorageDirectory();
            return new File(storageFile, fileName);
        }
    }

    /** 안드로이드 10 이후 **/
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Uri createUri() {
        // 파일 이름 생성
        Date date = new Date();
        String fileName = format.format(date) + "_" + System.currentTimeMillis();

        // ContentValues 생성
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Diary");

        fileUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return fileUri;
    }

    /** 일기내용이 비어있는지 체크 ->
     * 비어있는 경우 작성취소 팝업없이 일기작성 종료가능 **/
    public Boolean isEmptyContent() {
        Log.d(LOG, "moodIndex : " + moodIndex + ", contentEditText : " + contentsEditText.getText());
        if(curButton == null && (contentsEditText.getText().toString().equals("") || contentsEditText.getText() == null)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Photo Adapter 내 사진경로 합치기
     * (','로 구분)
     */
    public void combineFilePath() {
        for(String filePath : photoAdapter.getItems()) {
            filePaths += filePath + ",";
        }
    }

    /**
     * 사진추가 다이얼로그
     * (저장공간 권한확인 필요)
     */
    @Override
    public void showAddPhotoDialog() {
        if(!PermissionUtils.Companion.checkStoragePermission(requireContext()) ||
            !PermissionUtils.Companion.checkCameraPermission(requireContext()))
        {
            DialogUtils.Companion.showStoragePermissionDialog(requireContext(), () -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                intent.setData(uri);
                requireActivity().startActivity(intent);
                return Unit.INSTANCE;
            });
        } else {
            DialogUtils.Companion.showAddPhotoDialog(
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

    class StarButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(starIndex == 0) {
                starIndex = 1;
                Glide.with(WriteFragment.this)
                        .load(ContextCompat.getDrawable(context, R.drawable.star_icon_color))
                        .into(starButton);
            } else {
                starIndex = 0;
                Glide.with(WriteFragment.this)
                        .load(ContextCompat.getDrawable(context, R.drawable.star_icon))
                        .into(starButton);
            }
        }
    }

    class SaveButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(tabListener != null) {
                if(isEmpty()) {     // 사용자가 기분을 고르지 않은 경우
                    Toast.makeText(getContext(), "오늘의 기분을 골라주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                needDeleteCache = false;
                combineFilePath();                                  // photoAdapter 에 저장되있는 파일경로들을 하나로 합침
                setMoodIndex();                                     // 현재 눌린 기분버튼 종류에 따라 moodIndex 설정
                setContents();                                      // contentsEditText 에 사용자가 입력한 내용을 contents 에 저장
                address = locationEditText.getText().toString();    // GPS 로 받아온 위치 or 사용자가 직접 입력한 위치정보를 address(String)에 저장

                if(updateItem == null) {                            // 새로 일기를 작성하는 경우
                    if(dateText != null) {                          // 사용자가 날짜를 바꾼 경우
                        String date = dateText + " " + MainActivity.timeFormat2.format(new Date());
                        objs = new Object[]{weatherIndex, address, "", "", contents, moodIndex, filePaths, curYear, curMonth, date, starIndex};
                        callback.insertDB2(objs);
                    } else {                                        // 사용자가 날짜를 바꾸지 않은 경우
                        if(calDate != null) {                       // 기분달력으로 넘어온 날짜로 작성하는 경우
                            String date = MainActivity.dateFormat2.format(calDate) + " " + MainActivity.timeFormat2.format(new Date());
                            objs = new Object[]{weatherIndex, address, "", "", contents, moodIndex, filePaths, curYear, curMonth, date, starIndex};
                            callback.insertDB2(objs);
                        } else {
                            objs = new Object[]{weatherIndex, address, "", "", contents, moodIndex, filePaths, curYear, curMonth, starIndex};
                            callback.insertDB(objs);
                        }
                    }

                } else {                                            // 일기를 수정하는 경우
                    if(filePaths != null && !filePaths.equals("")) {
                        updateItem.setPicture(filePaths);
                    } else {
                        updateItem.setPicture("");
                    }

                    for(String path : deletePath) {
                        File file = new File(path);
                        file.delete();
                    }

//                    else {
//                        if(deleteRecentFilePath) {      // 기존 사진을 모두 삭제한 경우
//                            updateItem.setPicture("");
//                            if(recentFilePaths != null && !recentFilePaths.equals("")) {
//                                File file = new File(recentFilePaths);
//                                file.delete();
//                            }
//                        } else {                        // 수정 전 사진 복구
//                            updateItem.setPicture(recentFilePaths);
//                        }
//                    }

                    updateItem.setWeather(weatherIndex);
                    updateItem.setAddress(locationEditText.getText().toString());
                    updateItem.setContents(contents);
                    updateItem.setMood(moodIndex);
                    updateItem.setStarIndex(starIndex);

                    if(dateText != null) {
                        String date = dateText + " " + MainActivity.timeFormat2.format(new Date());
                        updateItem.setYear(curYear);
                        updateItem.setDay(curMonth);
                        updateItem.setCreateDateStr2(date);
                        callback.updateDB2(updateItem);
                    } else {
                        callback.updateDB(updateItem);
                    }
                }

                filePaths = null;                        // filePath 를 null 로 지정함으로써 detach()호출에도 삭제되지않음
                tabListener.setIsSelected(true);
                tabListener.onTabSelected(0);    // 일기목록 프래그먼트로 이동
            }
        }
    }

    class DeleteButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(tabListener != null) {
                if(updateItem == null) {                    // 일기 작성을 멈출 때
                    if(requestListener != null) {
                        requestListener.onRequest("showStopWriteDialog");
                    }
                } else {                                    // 수정 중일때 삭제
                    setDeleteNoteDialog();
                }
            }
        }
    }

    class MoodButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Button selectButton = (Button)getSelectedMoodButton(v);

            if(curButton == null) {
                setButtonAnim(selectButton);
            } else if(curButton == selectButton){
                selectButton.setScaleX(1.0f);
                selectButton.setScaleY(1.0f);
                selectButton.clearAnimation();
                curButton = null;
            }
            else {
                curButton.setScaleX(1.0f);
                curButton.setScaleY(1.0f);
                curButton.clearAnimation();
                setButtonAnim(selectButton);
            }
        }
    }

    class MyAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) { }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(isWeatherViewOpen) {
                weatherAddImageView.setImageResource(R.drawable.navigate_down);
                weatherView.setVisibility(View.GONE);
            }
            isWeatherViewOpen = !isWeatherViewOpen;
        }

        @Override
        public void onAnimationRepeat(Animation animation) { }
    }

    class OpenWeatherClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(isWeatherViewOpen) {
                weatherView.startAnimation(translateLeftAnim);
            } else {
                weatherView.setVisibility(View.VISIBLE);
                weatherAddImageView.setImageResource(R.drawable.navigate_up);
                weatherView.startAnimation(translateRightAnim);
            }
        }
    }

    class WeatherButtonClickListener implements View.OnClickListener {
        private int _weatherIndex = -1;

        public WeatherButtonClickListener(int _weatherIndex) {
            this._weatherIndex = _weatherIndex;
        }

        @Override
        public void onClick(View v) {
            weatherIndex = _weatherIndex;
            setWeatherImageView2(weatherIndex);
            if(isWeatherViewOpen) {
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
            if(requestListener != null) {
                if(PermissionUtils.Companion.checkLocationPermission(requireContext())) {
                    requestListener.onRequest("checkGPS");

                    if(Utils.Companion.checkGPS(requireContext())) {
                        if(calDate == null)requestListener.onRequest("getCurrentLocation"); // 메인 액티비티로부터 현재위치정보 가져오기
                        else requestListener.onRequest("getCurrentLocation", calDate);  // 메인 액티비티로부터 현재위치정보 가져오기(달력에서 넘어온 Date 사용)
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
