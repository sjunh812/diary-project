package org.sjhstudio.diary.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.sjhstudio.diary.R;
import org.sjhstudio.diary.calendar.CalendarAdapter;
import org.sjhstudio.diary.helper.OnRequestListener;
import org.sjhstudio.diary.note.Note;
import org.sjhstudio.diary.note.NoteDatabaseCallback;
import org.sjhstudio.diary.utils.Utils;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class CalendarFragment extends Fragment implements OnDateSelectedListener{

    private TextView dateTextView;
    private RecyclerView recyclerView;
    private LinearLayout showDiaryStateView;
    private TextView moodTextView;
    private MaterialCalendarView calendarView;

    private OnRequestListener requestListener;
    private NoteDatabaseCallback callback;

    private ArrayList<Note> items;
    private CalendarAdapter adapter;
    private String dateStr = null;
    private CalendarDay curCalDay = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof NoteDatabaseCallback) callback = (NoteDatabaseCallback)context;
        if(context instanceof OnRequestListener) requestListener = (OnRequestListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(callback != null) callback = null;
        if(requestListener != null) requestListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        items = callback.selectAllDB();
        setCalendarViewData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
//        items = callback.selectAllDB();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
    }

    private void initUi(View rootView) {
        Animation translateRightAnim = AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_animation);
        translateRightAnim.setDuration(350);

        TextView titleTextView = (TextView) rootView.findViewById(R.id.titleTextView);
        titleTextView.startAnimation(translateRightAnim);
        dateTextView = (TextView)rootView.findViewById(R.id.dateTextView);
        moodTextView = (TextView)rootView.findViewById(R.id.moodTextView);
        showDiaryStateView = (LinearLayout)rootView.findViewById(R.id.showDiaryStateView);
        Button writeButton = (Button) rootView.findViewById(R.id.writeButton);

        writeButton.setOnClickListener(v -> {
            if(dateStr != null) {
                try {
                    Date date = Utils.Companion.getDateFormat().parse(dateStr);
                    requestListener.onRequestWriteFragmentFromCal(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        initRecyclerView(rootView);
        initCalendarView(rootView);
    }

    private void initRecyclerView(View rootView) {
        adapter = new CalendarAdapter(requireContext());
        adapter.setOnCalItemClickListener((holder, view, position) -> {
            Note item = adapter.getItem(position);
            requestListener.onRequestDetailActivity(item);
        });
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if(parent.getChildAdapterPosition(view) !=
                        Objects.requireNonNull(parent.getAdapter()).getItemCount()-1) {
                    outRect.bottom = 30;
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void initCalendarView(View rootView) {
        calendarView = (MaterialCalendarView) rootView.findViewById(R.id.calendar);
        calendarView.getLeftArrow().setColorFilter(getResources().getColor(R.color.font), PorterDuff.Mode.SRC_IN);
        calendarView.getRightArrow().setColorFilter(getResources().getColor(R.color.font), PorterDuff.Mode.SRC_IN);
/*        calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2020, 1, 1))
                .setMaximumDate(CalendarDay.from(2021, 12, 31))
                .commit();*/
        calendarView.setOnDateChangedListener(this);
        calendarView.addDecorators(new SaturdayDecorator(), new SundayDecorator(), new TodayDecorator());
        curCalDay = CalendarDay.today();
    }

    private void setCalendarViewData() {
        calendarView.removeDecorators();    // 달력내 Decorators 초기화

        if(curCalDay == null) {
            calendarView.setSelectedDate(CalendarDay.today());
            onDateSelected(calendarView, CalendarDay.today(), true);
        } else {
            calendarView.setSelectedDate(curCalDay);
            onDateSelected(calendarView, curCalDay, true);
        }

        for(Note note : items) {
            try {
                LocalDate localDate = LocalDate.parse(note.getCreateDateStr2());
                int moodIndex = note.getMood();
                calendarView.addDecorator(new MyDayDecorator(requireContext(), CalendarDay.from(localDate), moodIndex));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setMoodTextView(int moodIndex) {
        switch (moodIndex) {
            case 0:
                moodTextView.setText("화남");
                moodTextView.setTextColor(getResources().getColor(R.color.red));
                break;
            case 1:
                moodTextView.setText("쿨");
                moodTextView.setTextColor(getResources().getColor(R.color.blue));
                break;
            case 2:
                moodTextView.setText("슬픔");
                moodTextView.setTextColor(getResources().getColor(R.color.skyblue));
                break;
            case 3:
                moodTextView.setText("아픔");
                moodTextView.setTextColor(getResources().getColor(R.color.lightgreen));
                break;
            case 4:
                moodTextView.setText("웃음");
                moodTextView.setTextColor(getResources().getColor(R.color.yellow));
                break;
            case 5:
                moodTextView.setText("보통");
                moodTextView.setTextColor(getResources().getColor(R.color.gray));
                break;
            case 6:
                moodTextView.setText("나쁨");
                moodTextView.setTextColor(getResources().getColor(R.color.black));
                break;
            case 7:
                moodTextView.setText("좋음");
                moodTextView.setTextColor(getResources().getColor(R.color.orange));
                break;
            case 8:
                moodTextView.setText("피곤");
                moodTextView.setTextColor(getResources().getColor(R.color.pink));
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDay();

        curCalDay = date;   // 선택한 CalendarDay 저장
        dateStr = year + "년 " + month + "월 " + day + "일";
        dateTextView.setText(dateStr + "은, ");

        /* 해당 날짜에 일기가 있을 경우, RecyclerView 를 이용해 보여줌 */
        adapter.clearItems();
        for(Note item : items) {
            try {
                Date _date = Utils.Companion.getDateFormat2().parse(item.getCreateDateStr2());

                if(_date != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(_date);

                    if((cal.get(Calendar.YEAR)) == year &&
                            (cal.get(Calendar.MONTH) + 1) == month &&
                            cal.get(Calendar.DAY_OF_MONTH) == day)
                    {
                        adapter.addItem(item);
                        setMoodTextView(item.getMood());
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        if(adapter.getItemCount() == 0) {
            showDiaryStateView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            moodTextView.setVisibility(View.GONE);
        } else {
            showDiaryStateView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            moodTextView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    static class MyDayDecorator implements DayViewDecorator {

        private final Context context;
        private final CalendarDay day;
        private final int moodIndex;

        public MyDayDecorator(Context context, CalendarDay day, int moodIndex) {
            this.context = context;
            this.day = day;
            this.moodIndex = moodIndex;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.equals(this.day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            switch(moodIndex) {
                case 0:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_red)));
                    break;
                case 1:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_blue)));
                    break;
                case 2:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_skyblue)));
                    break;
                case 3:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_green)));
                    break;
                case 4:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_yellow)));
                    break;
                case 5:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_gray)));
                    break;
                case 6:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_black)));
                    break;
                case 7:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_orange)));
                    break;
                case 8:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_pink)));
                    break;
            }
        }

    }

    class SaturdayDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            LocalDate date = day.getDate();
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            return dayOfWeek.getValue() == 6;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.skyblue)));
        }

    }

    class SundayDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            LocalDate date = day.getDate();
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            return dayOfWeek.getValue() == 7;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.red)));
        }

    }

    static class TodayDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.equals(CalendarDay.today());
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new RelativeSizeSpan(1.4f));
        }

    }
}
