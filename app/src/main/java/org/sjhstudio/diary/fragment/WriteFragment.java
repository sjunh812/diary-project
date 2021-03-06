package org.sjhstudio.diary.fragment;

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
import org.sjhstudio.diary.adapters.PhotoAdapter;
import org.sjhstudio.diary.custom.CustomDatePickerDialog;
import org.sjhstudio.diary.custom.CustomDeleteDialog;
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
    private Button curButton = null;                    // ?????? ????????? ???????????? ??????
    private ImageButton starButton;                     // ???????????? ??????
    private Button addPhotoBtn;                         // ???????????? ??????(????????? ?????????)
    private CustomDeleteDialog deleteDialog;            // ?????? ????????? ???????????? ????????? ???????????????
    private CustomDeleteDialog deleteNoteDialog;        // ?????? ????????? ???????????? ????????? ???????????????
    private CustomDatePickerDialog pickerDialog;
    private SwipeRefreshLayout swipeRefreshLayout;      // ???????????? ???

    // Photo
    private ImageView addPictureImageView;
    private ViewPager2 photoViewPager;
    private PhotoAdapter photoAdapter;
    private LinearLayout photoIndicator;
    private TextView currentBanner;                     // ?????? view pager item ??????
    private TextView totalBanner;                       // view pager ??? ??????

    // Listener
    private OnTabItemSelectedListener tabListener;      // ?????? ?????????????????? ???????????? ?????? ??? ?????? ?????????
    private OnRequestListener requestListener;          // ?????? ?????????????????? ?????? ?????? ????????? ???????????? ????????? ?????????
    private MoodButtonClickListener moodButtonListener; // ???????????? ?????? ????????? ?????? ?????? ????????? ????????? ?????? ?????????

    // DB
    private NoteDatabaseCallback callback;              // db ????????? ????????? ?????? ?????? ???????????????

    // Data
    private Note updateItem = null;
    private Date calDate = null;
    private String address = "";                        // ?????? ??????
    private String contents = "";                       // ?????? ??????
    private String filePaths = "";                      // ?????? ?????? (???????????? ?????? ','??? ??????!!)
    private String recentFilePaths = "";                // ????????? ?????? ?????? ?????? ???, ?????? ?????? ?????? (?????? ??????)
    private String dateText = null;                     // yyyy-MM-dd HH:mm (???????????? ?????? ?????? ????????? ????????? ?????? ?????????)
    private Uri fileUri;                                // ???????????? ?????? ??? ??? ???????????? ????????? Uri
    private Object[] objs;                              // db ??? ????????? ????????? ?????? ????????? Object[] ??????
    private int weatherIndex = -1;                      // ?????? ??????(0:??????, 1:?????? ??????, 2:?????? ??????, 3:??????, 4:???, 5:???/???, 6:???)
    private int moodIndex = -1;                         // 0~8 ??? 9?????? ????????? index ??? ??????(-1??? ???????????? ????????? ????????? ???????????? ?????? ??????)
    private int curYear;
    private int curMonth;
    private int curDay;
    private int starIndex = 0;                          // 0: ???????????????,  1: ???????????????
    private boolean isWeatherViewOpen = false;
    public boolean needDeleteCache = true;            // ?????? ?????? ?????? ??????
    private ArrayList<String> deletePath = new ArrayList<>();
    private Animation moodAnim;
    private Animation translateLeftAnim;
    private Animation translateRightAnim;
    private Animation translateRightTitleAnim;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnTabItemSelectedListener) tabListener = (OnTabItemSelectedListener)context;
        if(context instanceof OnRequestListener) requestListener = (OnRequestListener)context;
        if(context instanceof NoteDatabaseCallback)  callback = (NoteDatabaseCallback)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        requestListener.stopLocationService();  // ??????????????????
        deleteFilesCache(); // ???????????? ???????????? ??????

        if(tabListener != null) tabListener = null;
        if(requestListener != null) requestListener = null;
        if(callback != null) callback = null;
    }

    /**
     * Delete files cache
     * (?????? ??????)
     */
    public void deleteFilesCache() {
        if(updateItem != null) {
            // ????????????
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
            // ????????????
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
     * Delete file cache
     * (?????? ??????)
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
        // ???????????? GPS ????????? ????????? ??????
        if(requestListener != null) {
            requestListener.onRequest("checkGPS");
        }
        if(photoAdapter.getItemCount() > 0) addPhotoBtn.setVisibility(View.VISIBLE);
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

        if(requestListener != null && updateItem == null) {
            if(PermissionUtils.INSTANCE.checkLocationPermission(requireContext())) {
                // ???????????? ??????.
                if(calDate == null) requestListener.onRequest("getCurrentLocation");  // ?????? ????????????????????? ?????????????????? ????????????
                else requestListener.onRequest("getCurrentLocation", calDate);    // ?????? ????????????????????? ?????????????????? ????????????(???????????? ????????? Date ??????)
            } else {
                // ???????????? ??????.
                if(calDate == null) requestListener.getDateOnly(null);
                else requestListener.getDateOnly(calDate);
            }
        }

        if(updateItem != null) setUpdateItem();
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
        if(updateItem == null) titleTextView.setText("????????????");
        else titleTextView.setText("????????????");

        // ?????? ??????
        dateTextView = rootView.findViewById(R.id.dateTextView);
        ImageView dateTextImageView = rootView.findViewById(R.id.dateTextImageView);
        dateTextView.setOnClickListener(v -> setDatePickerDialog());
        dateTextImageView.setOnClickListener(v -> setDatePickerDialog());

        // ?????? ??????
        weatherImageView = rootView.findViewById(R.id.weatherImageView);
        weatherAddImageView = rootView.findViewById(R.id.weatherAddImageView);
        weatherView = rootView.findViewById(R.id.weatherView);                // ????????? ?????? ????????? ???????????? ???
        weatherImageView.setOnClickListener(new OpenWeatherClickListener());
        weatherAddImageView.setOnClickListener(new OpenWeatherClickListener());

        // edit text
        locationEditText = rootView.findViewById(R.id.locationTextView);          // ????????? ???????????? edit text
        contentsEditText = rootView.findViewById(R.id.contentsEditText);          // ?????? ??????

        // ????????????
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.pastel_700));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshListener());

        // ??????
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
        moodButtonListener = new MoodButtonClickListener();     // ?????? ????????? ?????? ?????? ????????? ?????? ????????? ?????????
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
        // ?????? photo UI
        addPictureImageView = rootView.findViewById(R.id.addPictureImageView);
        rootView.findViewById(R.id.pictureContainer).setOnClickListener( v -> showAddPhotoDialog());

        // photo indicator ??????
        photoIndicator = rootView.findViewById(R.id.photo_indicator);
        currentBanner = rootView.findViewById(R.id.current_banner);
        totalBanner = rootView.findViewById(R.id.total_banner);

        // photo view pager ??????
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

        // ????????????(?????????)
        addPhotoBtn = rootView.findViewById(R.id.add_photo_btn);
        addPhotoBtn.setOnClickListener(v -> showAddPhotoDialog());
    }

    /** ???????????? ????????? ????????? ?????? ?????? ????????? ?????? **/
    public void setWeatherImageView(String weatherStr) {
        if(weatherStr.equals("??????")) {
            weatherImageView.setImageResource(R.drawable.weather_icon_1);
            weatherIndex = 0;
        } else if(weatherStr.equals("?????? ??????")) {
            weatherImageView.setImageResource(R.drawable.weather_icon_2);
            weatherIndex = 1;
        } else if(weatherStr.equals("?????? ??????")) {
            weatherImageView.setImageResource(R.drawable.weather_icon_3);
            weatherIndex = 2;
        } else if(weatherStr.equals("??????")) {
            weatherImageView.setImageResource(R.drawable.weather_icon_4);
            weatherIndex = 3;
        } else if(weatherStr.equals("???")) {
            weatherImageView.setImageResource(R.drawable.weather_icon_5);
            weatherIndex = 4;
        } else if(weatherStr.equals("???/???")) {
            weatherImageView.setImageResource(R.drawable.weather_icon_6);
            weatherIndex = 5;
        } else if(weatherStr.equals("???")){
            weatherImageView.setImageResource(R.drawable.weather_icon_7);
            weatherIndex = 6;
        } else if(weatherStr.equals("?????????")) {
            weatherImageView.setImageResource(R.drawable.weather_icon_5);
            weatherIndex = 4;
        } else {
            Log.d(LOG, "Unknown weather string : " + weatherStr);
        }
    }

    /** ?????? ???????????? ?????? ?????? ????????? ?????? **/
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

    /** photo adapter ?????? (?????? ?????? ???) **/
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

    /** curButton ?????? ????????? ?????? ????????? ?????? **/
    public void setMoodIndex() {
        if(curButton == null) {
            // ???????????? ????????? ????????? ???????????? ?????? ??????
            // ????????? ??? ????????? ??????????????? ???????????????
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

    /** ?????? ???????????? ???????????? ?????? ??????????????? ??? curButton ?????? **/
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

    /** ???????????? Dialog (?????? ?????? ????????? ???????????? ???????????????) **/
    public void setDeleteNoteDialog() {
        deleteNoteDialog = new CustomDeleteDialog(requireContext());
        deleteNoteDialog.show();
        deleteNoteDialog.setCancelable(true);
        deleteNoteDialog.setTitleTextView("?????? ??????");
        deleteNoteDialog.setDeleteTextView("????????? ?????????????????????????");
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

            callback.deleteDB(id);                  // ?????? db ??????
            tabListener.setIsSelected(true);
            tabListener.onTabSelected(0);
        });
    }

    /** ???????????? Dialog **/
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
                dateText = Utils.INSTANCE.getDateFormat2().format(newDate);
                dateTextView.setText(Utils.INSTANCE.getDateFormat().format(newDate));
            }
        });
    }

    /** ???????????? Dialog **/
    @Override
    public void setDeletePictureDialog(int position) {
        deleteDialog = new CustomDeleteDialog(requireContext());
        deleteDialog.show();
        deleteDialog.setCancelable(true);

        deleteDialog.setDeleteButtonOnClickListener(v -> {
            deleteDialog.dismiss();

            if(updateItem != null) {            // ?????? ?????? ???
                deletePath.add(photoAdapter.getItems().get(position));
            } else {                            // ?????? ?????? ?????? ???
                deleteFileCache(photoAdapter.getItems().get(position));     // ????????? ??????????????? ?????? ??????
            }

            photoAdapter.getItems().remove(position);
            photoAdapter.notifyDataSetChanged();

            if(photoAdapter.getItemCount() < 1) {                       // ????????? ?????? ???????????? ????????? ?????? ??????
                addPhotoBtn.setVisibility(View.GONE);
                photoViewPager.setVisibility(View.GONE);
                photoIndicator.setVisibility(View.GONE);
                addPictureImageView.setVisibility(View.VISIBLE);
            }
            totalBanner.setText(String.valueOf(photoAdapter.getItemCount()));
        });
    }

    /**
     * ????????????
     * (????????? MainActivity ??? ??????.)
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
     * ?????? ????????????
     * (????????? MainActivity ??? ??????.)
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
        String paths = updateItem.getPicture();             // ","?????? ?????????????????? ???????????? ??????
        String contents = updateItem.getContents();
        int weatherIndex = updateItem.getWeather();
        int moodIndex = updateItem.getMood();
        int starIndex = updateItem.getStarIndex();
        try {
            Date date2 = Utils.INSTANCE.getDateFormat2().parse(date2Str);
//            curYear = date2.getYear() + 1900;
//            curMonth = date2.getMonth() + 1;
//            curDay = date2.getDate();
            if(date2 != null) {
                curYear = Utils.INSTANCE.getYear(date2);
                curMonth = Utils.INSTANCE.getMonth(date2);
                curDay = Utils.INSTANCE.getDay(date2);
                System.out.println("xxx " + curYear + "," + curMonth + "," + curDay);
            }
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
                recentFilePaths = paths;      // ???????????? ?????? ???, ????????? ????????? ????????? ?????????????????? recentFilePath ??? ?????? ????????? ??????

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

    private void checkStarButton(int index) {
        if(index == 0) {
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

    private File createFile() {
        // ???????????? ??????(yyyyMMdd_)
        String fileName = Utils.INSTANCE.getDateFormat3().format(new Date()) + "_" + System.currentTimeMillis();

        // ?????? ??????
        File storageFile = Environment.getExternalStorageDirectory();

        return new File(storageFile, fileName);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Uri createUri() {
        // ???????????? ??????(yyyyMMdd_)
        String fileName = Utils.INSTANCE.getDateFormat3().format(new Date()) + "_" + System.currentTimeMillis();

        // ContentValues ??????
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Diary");

        fileUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return fileUri;
    }

    // ???????????? ????????? ????????????..
    public boolean isEmpty() {
        return curButton == null;
    }

    // ??????????????? ???????????????..
    // ?????????????????? ???????????? ????????????!!
    public Boolean isEmptyContent() {
//        Log.d(LOG, "moodIndex : " + moodIndex + ", contentEditText : " + contentsEditText.getText());
        return curButton == null && (contentsEditText == null || contentsEditText.getText() == null || contentsEditText.getText().toString().equals(""));
    }

    /**
     * Photo Adapter ??? ???????????? ?????????
     * (','??? ??????)
     */
    public void combineFilePath() {
        for(String filePath : photoAdapter.getItems()) {
            filePaths += filePath + ",";
        }
    }

    /**
     * ???????????? ???????????????
     * (???????????? ???????????? ??????)
     */
    @Override
    public void showAddPhotoDialog() {
        if(!PermissionUtils.INSTANCE.checkStoragePermission(requireContext()) ||
            !PermissionUtils.INSTANCE.checkCameraPermission(requireContext()))
        {
            DialogUtils.INSTANCE.showStoragePermissionDialog(requireContext(), () -> {
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

    class StarButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(starIndex == 0) {
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

    class SaveButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(tabListener != null) {
                if(isEmpty()) {     // ???????????? ????????? ????????? ?????? ??????
                    Toast.makeText(getContext(), "????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }

                needDeleteCache = false;
                combineFilePath();                                  // photoAdapter ??? ??????????????? ?????????????????? ????????? ??????
                setMoodIndex();                                     // ?????? ?????? ???????????? ????????? ?????? moodIndex ??????
                setContents();                                      // contentsEditText ??? ???????????? ????????? ????????? contents ??? ??????
                address = locationEditText.getText().toString();    // GPS ??? ????????? ?????? or ???????????? ?????? ????????? ??????????????? address(String)??? ??????

                if(updateItem == null) {                            // ?????? ????????? ???????????? ??????
                    if(dateText != null) {                          // ???????????? ????????? ?????? ??????
                        String date = dateText + " "
                                + Utils.INSTANCE.getTimeFormat2().format(new Date());
                        objs = new Object[]{weatherIndex, address, "", "", contents, moodIndex, filePaths, curYear, curMonth, date, starIndex};
                        callback.insertDB2(objs);
                    } else {                                        // ???????????? ????????? ????????? ?????? ??????
                        if(calDate != null) {                       // ?????????????????? ????????? ????????? ???????????? ??????
                            String date = Utils.INSTANCE.getDateFormat2().format(calDate) + " "
                                    + Utils.INSTANCE.getTimeFormat2().format(new Date());
                            objs = new Object[]{weatherIndex, address, "", "", contents, moodIndex, filePaths, curYear, curMonth, date, starIndex};
                            callback.insertDB2(objs);
                        } else {
                            objs = new Object[]{weatherIndex, address, "", "", contents, moodIndex, filePaths, curYear, curMonth, starIndex};
                            callback.insertDB(objs);
                        }
                    }

                } else {                                            // ????????? ???????????? ??????
                    if(filePaths != null && !filePaths.equals("")) {
                        updateItem.setPicture(filePaths);
                        recentFilePaths = filePaths;
                    } else {
                        updateItem.setPicture("");
                    }

//                    for(String path : deletePath) {
//                        File file = new File(path);
//                        file.delete();
//                    }

                    updateItem.setWeather(weatherIndex);
                    updateItem.setAddress(locationEditText.getText().toString());
                    updateItem.setContents(contents);
                    updateItem.setMood(moodIndex);
                    updateItem.setStarIndex(starIndex);

                    if(dateText != null) {
                        String date = dateText + " "
                                + Utils.INSTANCE.getTimeFormat2().format(new Date());
                        updateItem.setYear(curYear);
                        updateItem.setDay(curMonth);
                        updateItem.setCreateDateStr2(date);
                        callback.updateDB2(updateItem);
                    } else {
                        callback.updateDB(updateItem);
                    }
                }

                filePaths = null;                        // filePath ??? null ??? ?????????????????? detach()???????????? ??????????????????
                tabListener.setIsSelected(true);
                tabListener.onTabSelected(0);    // ???????????? ?????????????????? ??????
            }
        }
    }

    class DeleteButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(tabListener != null) {
                if(updateItem == null) {                    // ?????? ????????? ?????? ???
                    if(requestListener != null) {
                        requestListener.onRequest("showStopWriteDialog");
                    }
                } else {                                    // ?????? ????????? ??????
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
                if(PermissionUtils.INSTANCE.checkLocationPermission(requireContext())) {
                    requestListener.onRequest("checkGPS");

                    if(Utils.INSTANCE.checkGPS(requireContext())) {
                        if(calDate == null)requestListener.onRequest("getCurrentLocation"); // ?????? ????????????????????? ?????????????????? ????????????
                        else requestListener.onRequest("getCurrentLocation", calDate);  // ?????? ????????????????????? ?????????????????? ????????????(???????????? ????????? Date ??????)
                    } else {
                        setSwipeRefresh(false);
                    }
                } else {
                    Toast.makeText(
                            getContext(),
                            "?????? ??? ??????????????? ???????????? ?????? ??????????????? ???????????????.\n" + "??????>??????>??? ???????????? ??????????????????.",
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
