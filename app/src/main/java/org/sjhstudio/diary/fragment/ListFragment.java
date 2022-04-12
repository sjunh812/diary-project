package org.sjhstudio.diary.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.sjhstudio.diary.MainActivity;
import org.sjhstudio.diary.custom.CustomAlignDialog;
import org.sjhstudio.diary.R;
import org.sjhstudio.diary.custom.CustomDeleteDialog;
import org.sjhstudio.diary.custom.CustomUpdateDialog;
import org.sjhstudio.diary.custom.SearchKeywordDialog;
import org.sjhstudio.diary.helper.OnNoteItemClickListener;
import org.sjhstudio.diary.helper.OnNoteItemLongClickListener;
import org.sjhstudio.diary.helper.OnRequestListener;
import org.sjhstudio.diary.note.NoteAdapter;
import org.sjhstudio.diary.note.NoteDatabaseCallback;
import org.sjhstudio.diary.note.NoteViewHolder;
import org.sjhstudio.diary.helper.OnTabItemSelectedListener;
import org.sjhstudio.diary.note.Note;

import java.io.File;;
import java.util.ArrayList;
import java.util.Date;

public class ListFragment extends Fragment {
    /** UI **/
    private TextView titleTextView;                    // 타이틀 텍스트 (default : 일기목록)
    private LinearLayout showDiaryStateView;           // DB 조회시 일기가 없는 경우에 나타나는 상태 뷰 (일기없음)
    private RecyclerView listRecyclerView;             // 일기를 보여주는 리사이클러 뷰
    private TextView selectedDateTextView;             // 일기 기간별 정렬에 따라 나타나는 텍스트 (ex) 전체보기)
    private ImageButton photoButton;                   // 내용, 사진레이아웃을 선택하는 버튼
    private ImageButton starButton;                    // 즐겨찾기 정렬 여부를 선택하는 버튼

    /** listener **/
    private OnTabItemSelectedListener tabListener;     // 메인 액티비티 하단 탭의 탭선택 콜백함수를 호출 해주는 리스너
    private OnRequestListener requestListener;         // MainActivity 에 특정 이벤트를 요청하는 리스너
    private GestureListener gestureListener;           // 제스처 객체에 필요한 리스너

    /** adapter **/
    private NoteAdapter adapter;                       // 일기 목록을 담은 리사이클러 뷰의 어뎁터
    private MyArrayAdapter yearAdapter;                // 일기 기간별 정렬시 년도 스피너의 어뎁터
    private MyArrayAdapter monthAdapter;               // 일기 기간별 정렬시 월 스피너의 어뎁터

    /** data **/
    private int curYear;                               // 현재 년도 (ex)2021)
    private int lastYear;                              // 마지막 년도 (DB 안에 있는)
    private int layoutType = 0;                        // 0 : 내용 레이아웃, 1 : 사진 레이아웃
    private String[] years;                            // 년도 스피너의 어뎁터에 사용될 String 배열 (ArrayAdapter -> String[] 필요)
    private String[] months = {"1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"};      // 월 스피너의 어뎁터에 사용될 String 배열
    private int selectedYear;                          // 년도 스피너에서 선택된 년도
    private int selectedMonth;                         // 월 스피너에서 선택된 월
    private int selectedYearPos;                       // 이전에 선택한 년도의 스피너 position
    private int selectedMonthPos;                      // 이전에 선택한 월 스피너 position
    private Note selectedItem;                         // 일기목록을 길게 눌렀을 때 선택되는 일기의 Note 객체
    private boolean isAligned = false;                 // 사용자가 일기 기간별 정렬했는지 여부
    private boolean isPhoto = false;                   // 사진보기 여부
    private boolean isStar = false;                    // 즐겨찾기 여부
    private ArrayList<Note> savedItems = null;         // 즐겨찾기 탐색 이전 adapter 로 돌아가기 위해 저장해둔 adapter
    private Animation translateRightAnim;              // 타이틀 애니메이션 객체

    /** 기타 **/
    private NoteDatabaseCallback callback;             // DB 콜백 인터페이스
    private GestureDetector detector;                  // 일기 목록을 길게 눌렀을 때의 이벤트를 위한 제스처 객체

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof OnTabItemSelectedListener) {
            tabListener = (OnTabItemSelectedListener)context;
        }
        if(context instanceof OnRequestListener) {
            requestListener = (OnRequestListener)context;
        }
        if(context instanceof NoteDatabaseCallback) {
            callback = (NoteDatabaseCallback)context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(tabListener != null) {
            tabListener = null;
        }
        if(requestListener != null) {
            requestListener = null;
        }
        if(callback != null) {
            callback = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(isPhoto) {
            photoButton.setImageDrawable(getResources().getDrawable(R.drawable.photo_clicked_icon));
            adapter.setLayoutType(1);
        } else {
            photoButton.setImageDrawable(getResources().getDrawable(R.drawable.photo_icon));
            adapter.setLayoutType(0);
        }

        if(isStar) {
            starButton.setImageDrawable(getResources().getDrawable(R.drawable.star_icon_color));
            if(adapter != null) {
                adapter.setStar();
            }
            titleTextView.setText("즐겨찾기");
        } else {
            starButton.setImageDrawable(getResources().getDrawable(R.drawable.star_icon));
            titleTextView.setText("일기목록");
        }

        adapter.notifyDataSetChanged();
        setShowDiaryStateView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        translateRightAnim = AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_animation);
        translateRightAnim.setDuration(350);

        curYear = getCurrentYear();     // 현재년도 가져오기
        calculateYearArray();           // 현재년도와 DB 내 마지막 년도를 이용하여 years 배열 설정 (년도 스피너의 어뎁터에 사용될)
        initSpinnerPosition();          // 초기 스피너 포지션 값을 지정하기위해 selectedYearPos 와 selectedMonthPos 초기화 (금년, 금월로 지정)

        gestureListener = new GestureListener();        // GestureDetector.OnGestureListener 를 상속받은 객체
        detector = new GestureDetector(getContext(), gestureListener);  // 리스트 아이템 별 길게 누르는 이벤트를 위해 필요한 제스처 객체

        yearAdapter = new MyArrayAdapter(getContext(), android.R.layout.simple_spinner_item, years);
        monthAdapter = new MyArrayAdapter(getContext(), android.R.layout.simple_spinner_item, months);

        initUI(rootView);
        initListener(rootView);
        initRecyclerView(rootView);     // 리사이클러 뷰에 관한 초기설정
        setShowDiaryStateView();        // 일기목록이 비어있는 확인

        return rootView;
    }

    private void initUI(View rootView) {
        titleTextView = rootView.findViewById(R.id.titleTextView);
        titleTextView.startAnimation(translateRightAnim);
        showDiaryStateView = rootView.findViewById(R.id.showDiaryStateView);
        selectedDateTextView = rootView.findViewById(R.id.selectedDateTextView);
        photoButton = rootView.findViewById(R.id.photoButton);
        starButton = rootView.findViewById(R.id.starButton);
    }

    private void initListener(View rootView) {
        rootView.findViewById(R.id.search_btn).setOnClickListener(v -> {
            SearchKeywordDialog dialog = new SearchKeywordDialog(requireContext());
            dialog.show();
            dialog.setOnSearchBtnClickListener(v2 -> {
                dialog.dismiss();
                Snackbar.make(selectedDateTextView,dialog.getKeyword() + " 검색결과 입니다.", 1000).show();
                adapter.setItems(callback.selectKeyword(dialog.getKeyword()));
                checkStar();
                adapter.notifyDataSetChanged();
                titleTextView.setText(dialog.getKeyword());
                selectedDateTextView.setText("전체");
            });
        });

        selectedDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlignDialog();
            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPhoto) {
                    photoButton.setImageDrawable(getResources().getDrawable(R.drawable.photo_clicked_icon));
                    adapter.setLayoutType(1);
                } else {
                    photoButton.setImageDrawable(getResources().getDrawable(R.drawable.photo_icon));
                    adapter.setLayoutType(0);
                }

                adapter.notifyDataSetChanged();
                isPhoto = !isPhoto;
            }
        });

        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isStar) {
                    starButton.setImageDrawable(getResources().getDrawable(R.drawable.star_icon_color));
                    adapter.setStar();
                    titleTextView.setText("즐겨찾기");
                } else {
                    starButton.setImageDrawable(getResources().getDrawable(R.drawable.star_icon));
                    backupAdapter();
                    titleTextView.setText("일기목록");
                }

                adapter.notifyDataSetChanged();
                setShowDiaryStateView();
                isStar = !isStar;
            }
        });

    }

    private void backupAdapter() {
        if(!isAligned) {
            adapter.setItems(callback.selectAllDB());
        } else {
            adapter.setItems(callback.selectPart(selectedYear, selectedMonth));
        }
    }

    private void initRecyclerView(View rootView) {
        listRecyclerView = (RecyclerView)rootView.findViewById(R.id.listRecyclerView);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        listRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                if(parent.getChildAdapterPosition(view) != 0) {
                    outRect.top = 30;
                }
            }
        });

        adapter = new NoteAdapter(getContext());

        if(!isAligned) {        // 전체보기 상태
            adapter.setItems(callback.selectAllDB());
            selectedDateTextView.setText("전체");
        } else {                // 일기 기간별 정렬 상태
            adapter.setItems(callback.selectPart(selectedYear, selectedMonth));
            selectedDateTextView.setText(selectedYear + "년 " + selectedMonth + "월");
        }

        adapter.setOnItemClickListener(new OnNoteItemClickListener() {
            @Override
            public void onItemClick(NoteViewHolder holder, View view, int position) {
                Note item = adapter.getItem(position);

                requestListener.onRequestDetailActivity(item);
            }
        });

        adapter.setOnItemLongClickListener(new OnNoteItemLongClickListener() {
            @Override
            public void onLongClick(NoteViewHolder holder, View view, int position) {
                selectedItem = adapter.getItem(position);

                if(selectedItem != null) {
                    showUpdateDialog();
                }
            }
        });

        listRecyclerView.setAdapter(adapter);
    }

    private void setShowDiaryStateView() {
        if(adapter.getItemCount() == 0) {
            showDiaryStateView.setVisibility(View.VISIBLE);
            listRecyclerView.setVisibility(View.GONE);
        } else {
            showDiaryStateView.setVisibility(View.GONE);
            listRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void initSpinnerPosition() {
        selectedYearPos = years.length - 1;             // 제일 마지막 인덱스 = 현재 년도
        selectedMonthPos = getCurrentMonth() - 1;       // ex) 4월 -> 3 (pos)
    }

    /** 일기 수정 다이얼로그 **/
    public void showUpdateDialog() {
        CustomUpdateDialog dialog = new CustomUpdateDialog(getContext());
        dialog.show();

        dialog.setDeleteButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showDeleteDialog();
            }
        });

        dialog.setUpdateButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                tabListener.showWriteFragment(selectedItem);
            }
        });
    }

    /** 일기 삭제 다이얼로그 **/
    private void showDeleteDialog() {
        CustomDeleteDialog dialog = new CustomDeleteDialog(getContext());
        dialog.show();

        dialog.setTitleTextView("일기 삭제");
        dialog.setDeleteTextView("일기를 삭제하시겠습니까?\n삭제한 일기는 복구가 불가능합니다.");
        dialog.setDeleteButtonText("삭제");
        dialog.setCancelButton2Text("취소");
        dialog.setDeleteButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedItem != null) {
                    int id = selectedItem.get_id();
                    String paths = selectedItem.getPicture();

                    if(paths != null && !paths.equals("")) {
                        String picturePaths[] = paths.split(",");

                        for(int i = 0; i < picturePaths.length; i++) {
                            File file = new File(picturePaths[i]);
                            file.delete();
                        }
                    }

                    callback.deleteDB(id);          // 해당 db 삭제

                    if(!isAligned) {                // 전체보기 상태
                        adapter.setItems(callback.selectAllDB());
                    } else {                        // 기간별 보기 상태
                        adapter.setItems(callback.selectPart(selectedYear, selectedMonth));
                    }

                    checkStar();
                    adapter.notifyDataSetChanged();
                    setShowDiaryStateView();
                    dialog.dismiss();
                }
            }
        });
    }

    /** 일기 정렬 다이얼로그 **/
    public void showAlignDialog() {
        CustomAlignDialog dialog = new CustomAlignDialog(getContext());
        dialog.show();

        dialog.setYesButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDateTextView.setText(selectedYear + "년 " + selectedMonth + "월");

                ArrayList<Note> items = callback.selectPart(selectedYear, selectedMonth);
                isAligned = true;
                adapter.setItems(items);
                checkStar();
                adapter.notifyDataSetChanged();
                setShowDiaryStateView();

                dialog.dismiss();
            }
        });

        dialog.setAllButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDateTextView.setText("전체");

                ArrayList<Note> items = callback.selectAllDB();
                isAligned = false;
                adapter.setItems(items);

                checkStar();

                adapter.notifyDataSetChanged();
                setShowDiaryStateView();

                dialog.dismiss();
            }
        });

        dialog.setYearSpinnerAdapter(yearAdapter);
        dialog.setSelectedYearSpinner(selectedYearPos);
        dialog.setYearSpinnerItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int size = years.length;
                int gap = size - (position + 1);

                selectedYearPos = position;
                selectedYear = curYear - gap;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        dialog.setMonthSpinnerAdapter(monthAdapter);
        dialog.setSelectMonthSpinner(selectedMonthPos);
        dialog.setMonthSpinnerItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonthPos = position;
                selectedMonth = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    public static int getCurrentYear() {
        Date date = new Date();
        String yearStr = MainActivity.yearFormat.format(date);
        int year = Integer.parseInt(yearStr);

        return year;
    }

    public static int getCurrentMonth() {
        Date date = new Date();
        String monthStr = MainActivity.monthFormat.format(date);
        int month = Integer.parseInt(monthStr);

        return month;
    }

    public void calculateYearArray() {
        lastYear = callback.selectLastYear();
        if(lastYear == 0) {
            lastYear = curYear;
        }
        int yearDiff = curYear - lastYear;

        ArrayList<String> yearsArray = new ArrayList<>();
        for(int i = yearDiff; i > 0; i--) {
            yearsArray.add(curYear - i + "년");
        }
        yearsArray.add(curYear + "년");

        years = yearsArray.toArray(new String[yearDiff + 1]);
    }

    public void update() {
        if(!isAligned) {                // 전체보기 상태
            adapter.setItems(callback.selectAllDB());
        } else {                        // 기간별 보기 상태
            adapter.setItems(callback.selectPart(selectedYear, selectedMonth));
        }

        checkStar();
        adapter.notifyDataSetChanged();
        setShowDiaryStateView();
    }

    private void checkStar() {
        if(isStar) {
            adapter.setStar();
            titleTextView.setText("즐겨찾기");
        } else {
            titleTextView.setText("일기목록");
        }
    }

    class MyArrayAdapter extends ArrayAdapter<String> {
        public MyArrayAdapter(@NonNull Context context, int resource, @NonNull Object[] objects) {
            super(context, resource, (String[]) objects);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            ((TextView)view).setGravity(Gravity.CENTER);
            ((TextView)view).setTextSize(17);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                ((TextView)view).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }

            return view;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);
            ((TextView)view).setGravity(Gravity.CENTER_VERTICAL);
            ((TextView)view).setHeight(88);
            ((TextView)view).setTextSize(17);

            return view;
        }
    }

    private class GestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) { return false; }

        @Override
        public void onShowPress(MotionEvent e) { }

        @Override
        public boolean onSingleTapUp(MotionEvent e) { return false; }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }

        @Override
        public void onLongPress(MotionEvent e) {
            // 길게 터치한 경우
            if(selectedItem != null) {
                showUpdateDialog();
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { return false; }
    }
}
