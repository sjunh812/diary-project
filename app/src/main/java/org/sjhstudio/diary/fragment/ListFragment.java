package org.sjhstudio.diary.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.sjhstudio.diary.custom.PeriodDialog;
import org.sjhstudio.diary.R;
import org.sjhstudio.diary.custom.CustomDeleteDialog;
import org.sjhstudio.diary.custom.CustomUpdateDialog;
import org.sjhstudio.diary.custom.SearchKeywordDialog;
import org.sjhstudio.diary.helper.OnRequestListener;
import org.sjhstudio.diary.note.NoteAdapter;
import org.sjhstudio.diary.note.NoteDatabaseCallback;
import org.sjhstudio.diary.helper.OnTabItemSelectedListener;
import org.sjhstudio.diary.note.Note;
import org.sjhstudio.diary.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class ListFragment extends Fragment {
    /** UI **/
    private TextView titleTextView;                    // 타이틀 텍스트 (default : 일기목록)
    private LinearLayout showDiaryStateView;           // DB 조회시 일기가 없는 경우에 나타나는 상태 뷰 (일기없음)
    private RecyclerView listRecyclerView;             // 일기를 보여주는 리사이클러 뷰
    private TextView selectedDateTextView;             // 일기 기간별 정렬에 따라 나타나는 텍스트 (ex) 전체보기)
    private ImageView photoButton;                     // 내용, 사진레이아웃을 선택하는 버튼
    private ImageView starButton;                      // 즐겨찾기 정렬 여부를 선택하는 버튼

    /** listener **/
    private OnTabItemSelectedListener tabListener;     // 메인 액티비티 하단 탭의 탭선택 콜백함수를 호출 해주는 리스너
    private OnRequestListener requestListener;         // MainActivity 에 특정 이벤트를 요청하는 리스너
    private NoteDatabaseCallback callback;             // DB 콜백 인터페이스

    /** adapter **/
    private NoteAdapter adapter;                       // 일기 목록을 담은 리사이클러 뷰의 어뎁터
    private MyArrayAdapter yearAdapter;                // 일기 기간별 정렬시 년도 스피너의 어뎁터
    private MyArrayAdapter monthAdapter;               // 일기 기간별 정렬시 월 스피너의 어뎁터

    /** data **/
    private int curYear;                               // 현재 년도 (ex)2021)
    private String[] years;                            // 년도 스피너의 어뎁터에 사용될 String 배열 (ArrayAdapter -> String[] 필요)
    private final String[] months = {"1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"};      // 월 스피너의 어뎁터에 사용될 String 배열
    private int selectedYear;                          // 년도 스피너에서 선택된 년도
    private int selectedMonth;                         // 월 스피너에서 선택된 월
    private int selectedYearPos;                       // 이전에 선택한 년도의 스피너 position
    private int selectedMonthPos;                      // 이전에 선택한 월 스피너 position
    private Note selectedItem;                         // 일기목록을 길게 눌렀을 때 선택되는 일기의 Note 객체
    private boolean isAligned = false;                 // 사용자가 일기 기간별 정렬했는지 여부
    private boolean isPhoto = false;                   // 사진보기 여부
    private boolean isStar = false;                    // 즐겨찾기 여부
    private boolean isPause = false;                   // onPause() 호출여부
    private Animation translateRightAnim;              // 타이틀 애니메이션 객체

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnTabItemSelectedListener) tabListener = (OnTabItemSelectedListener)context;
        if(context instanceof OnRequestListener) requestListener = (OnRequestListener)context;
        if(context instanceof NoteDatabaseCallback) callback = (NoteDatabaseCallback)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isPause = false;
        if(tabListener != null) tabListener = null;
        if(requestListener != null) requestListener = null;
        if(callback != null) callback = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(isPhoto) {
            photoButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_image_color));
            adapter.setLayoutType(1);
        } else {
            photoButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_image));
            adapter.setLayoutType(0);
        }

        if(isStar) {
            if(adapter != null) adapter.setStar();
            starButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_star_color));
            if(!isPause) titleTextView.setText(getString(R.string.bookmark));
            else isPause = false;
        } else {
            starButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_star));
            if(!isPause) titleTextView.setText(getString(R.string.diary_list));
            else isPause = false;
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
        curYear = Utils.INSTANCE.getCurrentYear();     // 현재년도 가져오기

        calculateYearArray();           // 현재년도와 DB 내 마지막 년도를 이용하여 years 배열 설정 (년도 스피너의 어뎁터에 사용될)
        initSpinnerPosition();          // 초기 스피너 포지션 값을 지정하기위해 selectedYearPos 와 selectedMonthPos 초기화 (금년, 금월로 지정)

        yearAdapter = new MyArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years);
        monthAdapter = new MyArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        initListener(view);
        setShowDiaryStateView();    // 일기가 비어있는지 확인
    }

    private void initUI(View rootView) {
        titleTextView = rootView.findViewById(R.id.titleTextView);
        titleTextView.startAnimation(translateRightAnim);
        showDiaryStateView = rootView.findViewById(R.id.showDiaryStateView);
        selectedDateTextView = rootView.findViewById(R.id.selectedDateTextView);
        photoButton = rootView.findViewById(R.id.photoButton);
        starButton = rootView.findViewById(R.id.starButton);

        initRecyclerView(rootView);
        rootView.findViewById(R.id.write_btn).setOnClickListener(v -> {
            tabListener.showWriteFragment(null);
        });
    }

    @SuppressLint("SetTextI18n")
    private void initRecyclerView(View rootView) {
        adapter = new NoteAdapter(getContext());

        if(!isAligned) {
            // 전체보기
            adapter.setItems(callback.selectAllDB());
            selectedDateTextView.setText("전체");
        } else {
            // 기간별정렬
            adapter.setItems(callback.selectPart(selectedYear, selectedMonth));
            selectedDateTextView.setText(selectedYear + "년 " + selectedMonth + "월");
        }

        adapter.setOnItemClickListener((holder, view, position) -> {
            Note item = adapter.getItem(position);
            requestListener.onRequestDetailActivity(item);
        });
        adapter.setOnItemLongClickListener((holder, view, position) -> {
            selectedItem = adapter.getItem(position);
            if(selectedItem != null) showUpdateDialog();
        });

        listRecyclerView = rootView.findViewById(R.id.listRecyclerView);
        listRecyclerView.setHasFixedSize(true);
        listRecyclerView.setAdapter(adapter);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        listRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if(parent.getChildAdapterPosition(view) != 0) {
                    outRect.top = 30;
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initListener(View rootView) {
        rootView.findViewById(R.id.search_btn).setOnClickListener(v -> {
            // 일기검색
            SearchKeywordDialog dialog = new SearchKeywordDialog(requireContext());
            dialog.show();
            dialog.setOnSearchBtnClickListener(v2 -> {
                if(dialog.getKeyword().trim().isEmpty()) {
                    Snackbar.make(dialog.getWindow().getDecorView(), getString(R.string.enter_search_keyword), 700).show();
                    return;
                }
                dialog.dismiss();
                Snackbar.make(photoButton, dialog.getKeyword().trim() + " 검색결과입니다.", 700).show();
                adapter.setItems(callback.selectKeyword(dialog.getKeyword().trim()));
                checkStar();
                adapter.notifyDataSetChanged();
                setShowDiaryStateView();
                titleTextView.setText("검색 : " + dialog.getKeyword().trim());
                selectedDateTextView.setText("전체");
            });
        });

        selectedDateTextView.setOnClickListener(v -> showAlignDialog());    // 날짜정렬

        starButton.setOnClickListener(v -> {
            // 즐겨찾기
            if(!isStar) {
                adapter.setStar();
                starButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_star_color));
                titleTextView.setText("즐겨찾기");
            } else {
                backupAdapter();
                starButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_star));
                titleTextView.setText("일기목록");
            }

            adapter.notifyDataSetChanged();
            setShowDiaryStateView();
            isStar = !isStar;
        });

        photoButton.setOnClickListener(v -> {
            // 사진보기
            if(!isPhoto) {
                photoButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_image_color));
                adapter.setLayoutType(1);
            } else {
                photoButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_image));
                adapter.setLayoutType(0);
            }

            adapter.notifyDataSetChanged();
            isPhoto = !isPhoto;
        });
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

    private void backupAdapter() {
        if(!isAligned) adapter.setItems(callback.selectAllDB());
        else adapter.setItems(callback.selectPart(selectedYear, selectedMonth));
    }

    private void initSpinnerPosition() {
        selectedYearPos = years.length - 1; // last index -> 현재 년도
        selectedMonthPos = Utils.INSTANCE.getCurrentMonth() - 1;   // ex) 4월 -> position=3
    }

    public void calculateYearArray() {
        // 마지막 년도 (DB 안에 있는)
        int lastYear = callback.selectLastYear();
        if(lastYear == 0) lastYear = curYear;
        int yearDiff = curYear - lastYear;
        ArrayList<String> yearsArray = new ArrayList<>();

        for(int i = yearDiff; i > 0; i--) {
            yearsArray.add(curYear - i + "년");
        }
        yearsArray.add(curYear + "년");

        years = yearsArray.toArray(new String[yearDiff + 1]);
    }

    public void update() {
        backupAdapter();
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

    // 일기수정 dialog
    public void showUpdateDialog() {
        CustomUpdateDialog dialog = new CustomUpdateDialog(requireContext());
        dialog.show();
        dialog.setDeleteButtonOnClickListener(v -> {
            dialog.dismiss();
            showDeleteDialog();
        });
        dialog.setUpdateButtonOnClickListener(v -> {
            dialog.dismiss();
            tabListener.showWriteFragment(selectedItem);
        });
    }

    // 일기삭제 dialog
    private void showDeleteDialog() {
        CustomDeleteDialog dialog = new CustomDeleteDialog(requireContext());
        dialog.show();

        dialog.setTitleTextView(getString(R.string.delete_diary));
        dialog.setDeleteTextView(getString(R.string.delete_diary_and_cant_restore));
        dialog.setDeleteButtonText(getString(R.string.delete));
        dialog.setCancelButton2Text(getString(R.string.cancel));
        dialog.setDeleteButtonOnClickListener(v -> {
            if(selectedItem != null) {
                int id = selectedItem.get_id();
                String paths = selectedItem.getPicture();

                if(paths != null && !paths.equals("")) {
                    String[] picturePaths = paths.split(",");

                    for (String picturePath : picturePaths) {
                        File file = new File(picturePath);
                        file.delete();
                    }
                }

                callback.deleteDB(id);          // 해당 db 삭제

                if(!isAligned) adapter.setItems(callback.selectAllDB());
                else adapter.setItems(callback.selectPart(selectedYear, selectedMonth));

                checkStar();
                adapter.notifyDataSetChanged();
                setShowDiaryStateView();
                dialog.dismiss();
            }
        });
    }

    // 일기정렬 dialog
    @SuppressLint("SetTextI18n")
    public void showAlignDialog() {
        PeriodDialog dialog = new PeriodDialog(requireContext());
        dialog.show();
        dialog.setYesButtonOnClickListener(v -> {
            // 기간보기
            isAligned = true;
            selectedDateTextView.setText(selectedYear + "년 " + selectedMonth + "월");
            adapter.setItems(callback.selectPart(selectedYear, selectedMonth));
            checkStar();
            adapter.notifyDataSetChanged();
            setShowDiaryStateView();
            dialog.dismiss();
        });
        dialog.setAllButtonOnClickListener(v -> {
            // 전체보기
            isAligned = false;
            selectedDateTextView.setText(getString(R.string.see_all));
            adapter.setItems(callback.selectAllDB());
            checkStar();
            adapter.notifyDataSetChanged();
            setShowDiaryStateView();
            dialog.dismiss();
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
            public void onNothingSelected(AdapterView<?> parent) {}
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
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    static class MyArrayAdapter extends ArrayAdapter<String> {

        public MyArrayAdapter(@NonNull Context context, int resource, @NonNull Object[] objects) {
            super(context, resource, (String[]) objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            ((TextView)view).setGravity(Gravity.CENTER);
            ((TextView)view).setTextSize(17);
            ((TextView)view).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
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

}
