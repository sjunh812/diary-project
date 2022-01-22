package org.sjhstudio.diary;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;
import com.stanfy.gsonxml.GsonXml;
import com.stanfy.gsonxml.GsonXmlBuilder;
import com.stanfy.gsonxml.XmlParserCreator;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.sjhstudio.diary.custom.CustomGPSDialog;
import org.sjhstudio.diary.custom.CustomStopWriteDialog;
import org.sjhstudio.diary.fragment.CalendarFragment;
import org.sjhstudio.diary.fragment.GraphFragment;
import org.sjhstudio.diary.fragment.ListFragment;
import org.sjhstudio.diary.fragment.OptionFragment;
import org.sjhstudio.diary.fragment.WriteFragment;
import org.sjhstudio.diary.utils.DialogUtils;
import org.sjhstudio.diary.helper.KMAGrid;
import org.sjhstudio.diary.helper.MyApplication;
import org.sjhstudio.diary.helper.MyTheme;
import org.sjhstudio.diary.helper.OnRequestListener;
import org.sjhstudio.diary.helper.OnTabItemSelectedListener;
import org.sjhstudio.diary.note.Note;
import org.sjhstudio.diary.note.NoteDatabaseCallback;
import org.sjhstudio.diary.note.NoteDatabase;
import org.sjhstudio.diary.utils.PermissionUtils;
import org.sjhstudio.diary.utils.Utils;
import org.sjhstudio.diary.weather.WeatherItem;
import org.sjhstudio.diary.weather.WeatherResult;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import kotlin.Unit;

public class MainActivity extends BaseActivity implements OnTabItemSelectedListener,
        AutoPermissionsListener, OnRequestListener, MyApplication.OnResponseListener, NoteDatabaseCallback {

    private static final String LOG = "MainActivity";

    private BottomNavigationView bottomNavigationView;  // 하단 탭
    private ListFragment listFragment;                  // 일기 목록
    private CalendarFragment calendarFragment;          // 기분 달력
    private WriteFragment writeFragment;                // 일기 작성
    private GraphFragment graphFragment;                // 일기 통계
    private OptionFragment optionFragment;              // 더보기

    private LocationManager locationManager;

    private GPSListener gpsListener;                    // 위치 정보를 가져오기 위해 필요한 리스너
    private LowVersionGPSListener lowVersionGPSListener;// 위치 정보를 가져오기 위해 필요한 리스너 (api 29 이전 버전)

    // date format
    // 추후 Utils 클래스에 정리필요..
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
    public static SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("a HH:mm");
    public static SimpleDateFormat timeFormat2 = new SimpleDateFormat("HH:mm:SS");
    public static SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    public static SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    public static SimpleDateFormat dayFormat = new SimpleDateFormat("dd");

    private NoteDatabase db;                            // 일기 목록을 담은 db
    private Location curLocation;                       // 현재 위치
    private String curWeatherStr;                       // 현재 날씨 String
    private Date curDate;                               // 현재 날짜
    private Note updateItem = null;                     // 일기 목록에서 수정할 Note 객체
    private int selectedTabIndex = 0;                   // 현재 선택되어있는 탭 번호 (onSaveInstanceState() 호출시 Bundle 객체로 저장)
    private Date calDate = null;
    private long backPressTime = 0;
    private boolean isSelected2 = false;                // 일기작성 취소시, 다른메뉴를 선택했을 때 setSelectedTabItem() 호출을 위한 flag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!Pref.getPPermissionGuide(this)) {
            Log.e(LOG, "권한안내 필요함.");
            DialogUtils.Companion.showPermissionGuideDialog(this, () -> {
                AutoPermissions.Companion.loadAllPermissions(this, Val.REQUEST_ALL_PERMISSIONS);
                Pref.setPPermissionGuide(this, true);

                return Unit.INSTANCE;
            });
        } else {
            Log.e(LOG, "권한안내 완료됨.");
            AutoPermissions.Companion.loadAllPermissions(this, Val.REQUEST_ALL_PERMISSIONS);
        }

        // DB
        db = new NoteDatabase(this);         // DB 객체 생성
        db.dbInit(NoteDatabase.DB_NAME);            // 지정된 이름의 일기 DB 생성
        db.createTable(NoteDatabase.NOTE_TABLE);    // Note 테이블 생성 (중복 제외)

        // fragment
        listFragment = new ListFragment();
        graphFragment = new GraphFragment();
        optionFragment = new OptionFragment();

        // location manager
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        gpsListener = new GPSListener();
        lowVersionGPSListener = new LowVersionGPSListener();

        // bottom navigation
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            if(!isSelected2 && selectedTabIndex == 2 &&
                    writeFragment != null && !(writeFragment.isEmptyContent())) {
                DialogUtils.Companion.showStopWriteDialog(this, () -> {
                    int position;

                    if(item.getItemId() == R.id.tab1) position = 0;
                    else if(item.getItemId() == R.id.tab2) position = 1;
                    else if(item.getItemId() == R.id.tab3) position = 2;
                    else if(item.getItemId() == R.id.tab4) position = 3;
                    else position = 4;

                    isSelected2 = true;
                    onTabSelected(position);
                    Log.d(LOG, "position : " + position);
                    return Unit.INSTANCE;
                });
            } else {
                isSelected2 = false;
                return setSelectedTabItem(item.getItemId(), transaction);
            }

            return false;
        });

        // savedInstanceState(테마설정, 폰트설정시 이용)
        if(savedInstanceState == null) onTabSelected(0);
        else onTabSelected(savedInstanceState.getInt(Val.SELECTED_TAB_INDEX));

        registerRemovedReceiver();
    }

    private void registerRemovedReceiver() {
        // 리시버등록(앱제거시, 비밀번호 삭제)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            intentFilter.addDataScheme(getPackageName());

            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("MyReceiver", "onReceive: " + intent);

                    SharedPreferences pref = context.getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                    if(pref != null) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.remove(MyTheme.PASSWORD);
                        editor.apply();
                    }
                }
            };

            registerReceiver(receiver, intentFilter);
        }
    }

    @Override
    public void setIsSelected(Boolean flag) {
        isSelected2 = flag;
    }

    /**
     * 위치 가져오기
     * @param date
     */
    public void getCurrentLocation(Date date) {
        if(date == null) curDate = new Date();                  // 현재 날짜정보
        else curDate = date;                                    // 사용자가 선택한 날짜정보

        String curYear = yearFormat.format(curDate);            // yyyy
        String curMonth = monthFormat.format(curDate);          // MM
        String curDay = dayFormat.format(curDate);              // dd
        String _date = dateFormat.format(curDate);              // yyyy년 MM월 dd일

        if(writeFragment != null) {
            writeFragment.setDateTextView(_date);
            try {
                writeFragment.setCurDate(Integer.parseInt(curYear), Integer.parseInt(curMonth), Integer.parseInt(curDay));
            } catch(Exception e) { e.printStackTrace(); }
        }

        try {
            if(PermissionUtils.Companion.checkLocationPermission(this)) {
//                curLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);   // 최근 위치정보
                long minTime = 1000;                                          // 업데이트 주기 10초
                float minDistance = 0;                                         // 업데이트 거리간격 0

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    gpsListener = new GPSListener();                           // 위치정보를 가져오기 위해 리스너 설정
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);    // 위치 업데이트
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);    // 위치 업데이트
                } else {
//                    lowVersionGPSListener = new LowVersionGPSListener();       // 위치정보를 가져오기 위해 리스너 설정
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, lowVersionGPSListener);    // 위치 업데이트
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, lowVersionGPSListener);    // 위치 업데이트
                }
            }
        } catch(Exception e) { e.printStackTrace(); }
    }

    /**
     * 주소 가져오기
     */
    public void getCurrentAddress() {
        Geocoder geoCoder = new Geocoder(this);

        try {
            List<Address> list = geoCoder.getFromLocation(curLocation.getLatitude(), curLocation.getLongitude(), 5);
            Log.d(LOG, "latitude : " + curLocation.getLatitude() + " longitude : " + curLocation.getLongitude());

            if(list != null && list.size() > 0) {
                Address address = list.get(0);                          // 현재 주소 정보를 가진 Address 객체
                String adminArea = address.getAdminArea();              // 인천광역시, 서울특별시..
                String locality = address.getLocality();                // 시
                String subLocality = address.getSubLocality();          // 구
                String thoroughfare = address.getThoroughfare();        // 동
                String subThoroughfare = address.getSubThoroughfare();  // 읍 면?
                String subStr = subLocality;

                if(locality == null) {                                  // 서울특별시나 인천광역시와 같이 '시'가 없는 지역인 경우 예외 처리
                    locality = adminArea;
                }

                if(subLocality == null) {                               // 구, 동이 없는 지역인 경우 예외 처리
                    subStr = thoroughfare;
                    if(thoroughfare == null) {
                        subStr = subThoroughfare;
                        if(subThoroughfare == null) {
                            subStr = "";
                        }
                    }
                }

                StringBuilder stringBuilder = new StringBuilder().append(locality).append(" ").append(subStr);
                Log.d(LOG, stringBuilder.toString());
                if(writeFragment != null) {
                    writeFragment.setLocationTextView(stringBuilder.toString());
                }
            }
        } catch(Exception e) { e.printStackTrace(); }
    }

    /**
     * 날씨 가져오기
     */
    public void getCurrentWeather() {
        /* 현재 위치의 위도, 경도들 이용하여 기상청이 만든 격자포멧으로 변환 */
        Map<String, Double> map = KMAGrid.getKMAGrid(curLocation.getLatitude(), curLocation.getLongitude());

        double gridX = map.get("X");
        double gridY = map.get("Y");
        //Log.d(LOG, "LOG : gridX = " + gridX + ", gridY = " + gridY);

        String url = "https://www.kma.go.kr/wid/queryDFS.jsp";
        url += "?gridx=" + Math.round(gridX);
        url += "&gridy=" + Math.round(gridY);

        Map<String, String> params = new HashMap<>();
        MyApplication.request(Val.REQUEST_WEATHER_BY_GRID, Request.Method.GET, url, params, this);
    }

    @Override
    public void stopLocationService() {
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) locationManager.removeUpdates(gpsListener);
            else locationManager.removeUpdates(lowVersionGPSListener);

            Log.d(LOG, "위치 업데이트 종료!!");
        } catch(Exception e) { e.printStackTrace(); }
    }

    @Override
    public void getDateOnly(Date date) {
        if(date == null) curDate = new Date();                  // 현재 날짜정보
        else curDate = date;

        String curYear = yearFormat.format(curDate);            // yyyy
        String curMonth = monthFormat.format(curDate);          // MM
        String curDay = dayFormat.format(curDate);              // dd
        String _date = dateFormat.format(curDate);              // yyyy년 MM월 dd일

        if(writeFragment != null && _date != null) {
            writeFragment.setDateTextView(_date);
            try {
                writeFragment.setCurDate(Integer.parseInt(curYear), Integer.parseInt(curMonth), Integer.parseInt(curDay));
            } catch(Exception e) { e.printStackTrace(); }
        }
    }

    public static String getDayOfWeek(Date date) {
        String dayOfWeek = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                dayOfWeek = "일";
                break;
            case 2:
                dayOfWeek = "월";
                break;
            case 3:
                dayOfWeek = "화";
                break;
            case 4:
                dayOfWeek = "수";
                break;
            case 5:
                dayOfWeek = "목";
                break;
            case 6:
                dayOfWeek = "금";
                break;
            case 7:
                dayOfWeek = "토";
                break;
        }

        return dayOfWeek;
    }

    @Override
    public void insertDB(Object[] objs) {
        if(db != null) db.insert(NoteDatabase.NOTE_TABLE, objs);
    }

    @Override
    public void insertDB2(Object[] objs) {
        if(db != null) db.insert2(NoteDatabase.NOTE_TABLE, objs);
    }

    @Override
    public ArrayList<Note> selectAllDB() {
        ArrayList<Note> items = new ArrayList<>();

        if(db != null) items = db.selectAll(NoteDatabase.NOTE_TABLE);

        return items;
    }

    @Override
    public ArrayList<Note> selectPart(int year, int month) {
        ArrayList<Note> items = new ArrayList<>();

        if(db != null) items = db.selectPart(NoteDatabase.NOTE_TABLE, year, month);


        return items;
    }

    @Override
    public HashMap<Integer, Integer> selectMoodCount(boolean isAll, boolean isYear, boolean isMonth) {
        HashMap<Integer, Integer> hashMap = new HashMap<>();

        if(db != null) hashMap = db.selectMoodCount(isAll, isYear, isMonth);

        return hashMap;
    }

    @Override
    public HashMap<Integer, Integer> selectMoodCountWeek(int weekOfDay) {
        HashMap<Integer, Integer> hashMap = new HashMap<>();

        if(db != null) hashMap = db.selectMoodCountWeek(weekOfDay);

        return hashMap;
    }

    @Override
    public int selectLastYear() {
        if(db != null) return db.selectLastYear();

        return 0;
    }

    @Override
    public int selectAllCount() {
        if(db != null) return db.selectAllCount();

        return 0;
    }

    @Override
    public int selectStarCount() {
        if(db != null) return db.selectStarCount();

        return 0;
    }

    @Override
    public void deleteDB(int id) {
        if(db != null) db.delete(NoteDatabase.NOTE_TABLE, id);
    }

    @Override
    public void updateDB(Note item) {
        if(db != null) db.update(NoteDatabase.NOTE_TABLE, item);
    }

    @Override
    public void updateDB2(Note item) {
        if(db != null) db.update2(NoteDatabase.NOTE_TABLE, item);
    }

    @Override
    public void onTabSelected(int position) {
        switch(position) {
            case 0:
                bottomNavigationView.setSelectedItemId(R.id.tab1);
                break;
            case 1:
                bottomNavigationView.setSelectedItemId(R.id.tab2);
                break;
            case 2:
                bottomNavigationView.setSelectedItemId(R.id.tab3);
                break;
            case 3:
                bottomNavigationView.setSelectedItemId(R.id.tab4);
                break;
            case 4:
                bottomNavigationView.setSelectedItemId(R.id.tab5);
                break;
            default:
                Log.e(LOG, "onTabSelected() error..");
                break;
        }
    }

    private Boolean setSelectedTabItem(int id, FragmentTransaction transaction) {
        // WriteFragment 가 아닌경우 위치탐색종료(stopLocationService())
        if(id != R.id.tab3) {
            if(locationManager != null) stopLocationService();
        }

        switch(id) {
            case R.id.tab1:
                selectedTabIndex = 0;
                transaction.replace(R.id.container, listFragment);
                transaction.commit();
                return true;

            case R.id.tab2:
                selectedTabIndex = 1;
                calendarFragment = new CalendarFragment();
                transaction.replace(R.id.container, calendarFragment);
                transaction.commit();
                return true;

            case R.id.tab3:
                selectedTabIndex = 2;
                writeFragment = new WriteFragment();

                if(calDate != null) {                   // 기분달력으로부터 넘어온 경우
                    writeFragment.setCalDate(calDate);  // 기분달력에서 가져온 Date 정보 전달
                    calDate = null;                     // calDate 초기화
                }

                if(updateItem != null) {                // 일기수정시
                    writeFragment.setItem(updateItem);  // updateItem 을 WriteFragment 로 전달
                    updateItem = null;                  // updateItem 초기화
                }

                transaction.replace(R.id.container, writeFragment);
                transaction.commit();
                return true;

            case R.id.tab4:
                selectedTabIndex = 3;
                transaction.replace(R.id.container, graphFragment);
                transaction.commit();
                return true;

            case R.id.tab5:
                selectedTabIndex = 4;
                transaction.replace(R.id.container, optionFragment);
                transaction.commit();
                return true;
        }

        return false;
    }

    /**
     * OnRequestListener
     * (1) onRequest(String command)
     * (2) onRequest(String command, Date date)
     * (3) onRequestDetailActivity(Note item)
     * (4) onRequestWriteFragmentFromCal(Date date) : CalendarFragment -> WriteFragment
     */
    @Override
    public void onRequest(String command) {
        switch(command) {
            case "getCurrentLocation":
                getCurrentLocation(null);
                break;
            case "checkGPS":
                if(!Utils.Companion.checkGPS(this)) {
                    DialogUtils.Companion.showGPSDialog(this, () -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        return Unit.INSTANCE;
                    });
                }
                break;
            case "showStopWriteDialog":
                DialogUtils.Companion.showStopWriteDialog(this, () -> {
                    selectedTabIndex = 0;
                    bottomNavigationView.setSelectedItemId(R.id.tab1);
                    return Unit.INSTANCE;
                });
                break;
            default:
                Log.d(LOG, "onRequest() 예외발생..");
                break;
        }
    }

    @Override
    public void onRequest(String command, Date date) {
        if(command.equals("getCurrentLocation")) getCurrentLocation(date);
    }

    @Override
    public void onRequestDetailActivity(Note item) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("item", item);
        detailActivityResult.launch(intent);
    }

    @Override
    public void onRequestWriteFragmentFromCal(Date date) {
        calDate = date;
        onTabSelected(2);
    }

    /**
     * OnResponseListener
     * using Volley
     * @param requestCode
     * @param responseCode
     * @param response
     */
    @Override
    public void onResponse(int requestCode, int responseCode, String response) {
        if(responseCode == Val.VOLLEY_RESPONSE_OK) {
            if(requestCode == Val.REQUEST_WEATHER_BY_GRID) {    // 기상청으로 날씨요청
                XmlParserCreator creator = new XmlParserCreator() { // Xml -> Gson
                    @Override
                    public XmlPullParser createParser() {
                        try {
                            return XmlPullParserFactory.newInstance().newPullParser();
                        } catch(Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                };

                GsonXml gsonXml = new GsonXmlBuilder()
                        .setXmlParserCreator(creator)
                        .setSameNameLists(true)
                        .create();

                try {
                    WeatherResult result = gsonXml.fromXml(response, WeatherResult.class);
                    WeatherItem item = result.body.data.get(0);
                    curWeatherStr = item.getWfKor();
                    Log.e(LOG, "getWfKor(): " + curWeatherStr);

                    if(writeFragment != null) writeFragment.setWeatherImageView(curWeatherStr);
                } catch(Exception e) { e.printStackTrace(); }

                if(writeFragment != null) writeFragment.setSwipeRefresh(false);
            }
        } else {
            Log.e(LOG, "ERROR : Failure response code = " + responseCode);
        }
    }

    @Override
    public void showWriteFragment(Note item) {
        updateItem = item;
        onTabSelected(2);
    }

    @Override
    public void onBackPressed() {
        if(bottomNavigationView.getSelectedItemId() == R.id.tab1) {
            if(System.currentTimeMillis() > backPressTime + 2000) {
                backPressTime = System.currentTimeMillis();
                Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(System.currentTimeMillis() <= backPressTime + 2000) super.onBackPressed();
        } else if(bottomNavigationView.getSelectedItemId() == R.id.tab3) {
            DialogUtils.Companion.showStopWriteDialog(this, () -> {
                selectedTabIndex = 0;
                bottomNavigationView.setSelectedItemId(R.id.tab1);
                return Unit.INSTANCE;
            });
        } else {
            bottomNavigationView.setSelectedItemId(R.id.tab1);
        }
    }

    /**
     * ActivityResultLauncher
     * (startActivityForResult() is deprecated)
     */
    public final ActivityResultLauncher<Intent> cameraResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        // 카메라 콜백
        int resultCode = result.getResultCode();

        switch(resultCode) {
            case RESULT_OK:
                Log.d(LOG, "xxx cameraResult: RESULT_OK");

                if (writeFragment != null) {
                    CropImage.activity(writeFragment.getFileUri()).setGuidelines(CropImageView.Guidelines.ON).start(this);
                }
                break;
            default:
                Log.d(LOG, "xxx cameraResult: RESULT_NOT_OK");

                if (writeFragment != null) {
                    Objects.requireNonNull(getContentResolver()).delete(writeFragment.getFileUri(), null, null);
                }
                break;
        }
    });

    public final ActivityResultLauncher<Intent> albumResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        // 앨범 콜백
        int resultCode = result.getResultCode();
        Intent data = result.getData();

        switch(resultCode) {
            case RESULT_OK:
                Log.d(LOG, "xxx albumResult: RESULT_OK");

                Uri uri = Objects.requireNonNull(data).getData();
                CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).start(this);
                break;
            default:
                Log.d(LOG, "xxx albumResult: RESULT_NOT_OK");
                break;
        }
    });

    final ActivityResultLauncher<Intent> cropImageActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        // crop image activity 콜백
        int resultCode = result.getResultCode();
        Intent data = result.getData();

        switch(resultCode) {
            case RESULT_OK:
                Log.d(LOG, "xxx cropImageActivityResult: RESULT_OK");

                CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
                String filePath = Objects.requireNonNull(activityResult).getUri().getPath();

                if (writeFragment != null) writeFragment.setPhotoAdapter(filePath);
                break;
            default:
                Log.d(LOG, "xxx cropImageActivityResult: RESULT_NOT_OK");
                break;
        }
    });

    final ActivityResultLauncher<Intent> fontChangeResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        int resultCode = result.getResultCode();

        switch(resultCode) {
            case RESULT_OK:
                recreate();
                break;
            default:
                Log.d(LOG, "xxx fontChangeResult: RESULT_NOT_OK");
                break;
        }
    });

    final ActivityResultLauncher<Intent> detailActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        int resultCode = result.getResultCode();
        Intent data = result.getData();

        switch(resultCode) {
            case Val.DETAIL_ACTIVITY_RESULT_DELETE:
                Log.d(LOG, "xxx detailActivityResult: 일기삭제됨");

                int id = Objects.requireNonNull(data).getIntExtra("id", -1);

                if (id != -1) {
                    deleteDB(id);

                    if (selectedTabIndex == 0) {
                        if(listFragment != null) listFragment.update();
                    } else {
                        if (calendarFragment != null) onTabSelected(1);
                    }
                }
                break;
            case Val.DETAIL_ACTIVITY_RESULT_UPDATE:
                Log.d(LOG, "xxx detailActivityResult: 일기수정됨");

                Note item = (Note)(Objects.requireNonNull(data).getSerializableExtra("item"));

                if(item != null) showWriteFragment(item);
                break;
            default:
                Log.d(LOG, "xxx detailActivityResult: 예외 응답");
                break;
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Log.d(LOG, "onActivityResult : CROP_IMAGE_ACTIVITY_REQUEST_CODE (RESULT_OK)");

                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    String filePath = Objects.requireNonNull(result).getUri().getPath();

                    if (writeFragment != null) {
                        writeFragment.setPhotoAdapter(filePath);
                    }
                } else {
                    Log.d(LOG, "onActivityResult : CROP_IMAGE_ACTIVITY_REQUEST_CODE (NOT RESULT_OK)");
                }
                break;

            case OptionFragment.REQUEST_FONT_CHANGE:
                if (resultCode == RESULT_OK) recreate();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Val.SELECTED_TAB_INDEX, selectedTabIndex);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int i, String[] strings) {
//        String loc = "";    // 위치
//        String cam = "";    // 카메라
//        String storage = "";    // 저장공간
//        String addr = "";   // 주소록
//
//        for(String permission: strings) {
//            switch(permission) {
//                case Manifest.permission.ACCESS_FINE_LOCATION:
//                case Manifest.permission.ACCESS_COARSE_LOCATION:
//                    loc = "위치 권한(날씨 및 위치 정보)\n";
//                    break;
//                case Manifest.permission.CAMERA:
//                    cam = "카메라 권한(사진추가)\n";
//                    break;
//                case Manifest.permission.READ_EXTERNAL_STORAGE:
//                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
//                    storage = "저장공간 권한(사진추가)\n";
//                    break;
//                case Manifest.permission.GET_ACCOUNTS:
//                    addr = "주소록 권한(백업)\n";
//                    break;
//                default:
//                    Log.e(LOG, "Permission denied exception: " + permission);
//            }
//        }
//
//        if(!(loc.isEmpty() && cam.isEmpty() && storage.isEmpty() && addr.isEmpty())) {
//            Toast.makeText(
//                    getApplicationContext(),
//                    loc + cam + storage + addr + "이 거부되었습니다.\n위 기능 사용을 위해 해당 권한들이 필요합니다.",
//                    Toast.LENGTH_LONG
//            ).show();
//        }

//        int deny = 0;
//
//        for(String permission : strings) {
//            if(permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)
//                    || permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) deny++;
//        }
//
//        Log.d(LOG, "onDenied(): deny(location)=" + deny);
//
//        if(deny > 1) {
//            Toast.makeText(
//                    getApplicationContext(),
//                    "날씨 및 작성 위치를 가져오기 위해 위치정보가 필요합니다.\n" + "설정->위치->앱 권한에서 허용해주세요.",
//                    Toast.LENGTH_LONG
//            ).show();
//        }
    }

    @Override
    public void onGranted(int i, String[] strings) {}

    /**
     * LocationListener
     */
    class GPSListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            curLocation = location;                                 // 가져온 위치정보를 curLocation 객체에 대입
            getCurrentAddress();                                    // 갱신된 위치정보를 주소로 반환 (작성 프래그먼트의 locationTextView 갱신)
            getCurrentWeather();                                    // 갱신된 위치정보를 날씨로 반환 (작성 프래그먼트의 weatherImageView 갱신)
            stopLocationService();
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {}

        @Override
        public void onProviderDisabled(@NonNull String provider) {}
    }

    class LowVersionGPSListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            curLocation = location;                                 // 가져온 위치정보를 curLocation 객체에 대입
            getCurrentAddress();                                    // 갱신된 위치정보를 주소로 반환 (작성 프래그먼트의 locationTextView 갱신)
            getCurrentWeather();                                    // 갱신된 위치정보를 날씨로 반환 (작성 프래그먼트의 weatherImageView 갱신)
            stopLocationService();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(@NonNull String provider) {}

        @Override
        public void onProviderDisabled(@NonNull String provider) {}
    }
}