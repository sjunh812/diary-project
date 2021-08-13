package org.sjhstudio.diary.fragment;

import android.content.Context;
import android.graphics.Color;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.sjhstudio.diary.MainActivity;
import org.sjhstudio.diary.R;
import org.sjhstudio.diary.calendar.CalendarAdapter;
import org.sjhstudio.diary.calendar.CalendarViewHolder;
import org.sjhstudio.diary.helper.OnCalItemClickListener;
import org.sjhstudio.diary.helper.OnRequestListener;
import org.sjhstudio.diary.note.Note;
import org.sjhstudio.diary.note.NoteDatabaseCallback;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Date;

public class CalendarFragment extends Fragment implements OnDateSelectedListener{
    /* UI */
    private TextView titleTextView;
    private MaterialCalendarView calendarView;
    private TextView dateTextView;
    private RecyclerView recyclerView;
    private LinearLayout showDiaryStateView;
    private Button writeButton;
    private TextView moodTextView;

    /* Helper */
    private OnRequestListener requestListener;
    private NoteDatabaseCallback callback;

    /* Data */
    private ArrayList<Note> items;
    private CalendarAdapter adapter;
    private String dateStr = null;
    private Animation translateRightAnim;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof NoteDatabaseCallback) {
            callback = (NoteDatabaseCallback)context;
        }
        if(context instanceof OnRequestListener) {
            requestListener = (OnRequestListener)context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(callback != null) {
            callback = null;
        }
        if(requestListener != null) {
            requestListener = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        items = callback.selectAllDB();
        translateRightAnim = AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_animation);
        translateRightAnim.setDuration(350);

        titleTextView = (TextView)rootView.findViewById(R.id.titleTextView);
        titleTextView.startAnimation(translateRightAnim);
        dateTextView = (TextView)rootView.findViewById(R.id.dateTextView);
        moodTextView = (TextView)rootView.findViewById(R.id.moodTextView);
        showDiaryStateView = (LinearLayout)rootView.findViewById(R.id.showDiaryStateView);
        writeButton = (Button)rootView.findViewById(R.id.writeButton);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateStr != null) {
                    try {
                        Date date = MainActivity.dateFormat.parse(dateStr);
                        requestListener.onRequestWriteFragmentFromCal(date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        initRecyclerView(rootView);
        initCalendarView(rootView);

        return rootView;
    }

    private void initRecyclerView(View rootView) {
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                if(parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = 30;
                }
            }
        });

        adapter = new CalendarAdapter(getContext());
        adapter.setOnCalItemClickListener(new OnCalItemClickListener() {
            @Override
            public void onItemClick(CalendarViewHolder holder, View view, int position) {
                Note item = adapter.getItem(position);

                requestListener.onRequestDetailActivity(item);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void initCalendarView(View rootView) {
        calendarView = (MaterialCalendarView)rootView.findViewById(R.id.calendar);
        calendarView.getLeftArrow().setColorFilter(getResources().getColor(R.color.font), PorterDuff.Mode.SRC_IN);
        calendarView.getRightArrow().setColorFilter(getResources().getColor(R.color.font), PorterDuff.Mode.SRC_IN);
/*        calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2020, 1, 1))
                .setMaximumDate(CalendarDay.from(2021, 12, 31))
                .commit();*/
        calendarView.setOnDateChangedListener(this);
        calendarView.setSelectedDate(CalendarDay.today());
        onDateSelected(calendarView, CalendarDay.today(), true);
        calendarView.addDecorators(new SaturdayDecorator(), new SundayDecorator(), new TodayDecorator());

        for(Note note : items) {
            try {
                LocalDate localDate = LocalDate.parse(note.getCreateDateStr2());
                int moodIndex = note.getMood();

                calendarView.addDecorator(new MyDayDecorator(getContext(), CalendarDay.from(localDate), moodIndex));
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

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        /* 선택한 날짜에 맞게 dateTextView 설정 */
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDay();

        dateStr = year + "년 " + month + "월 " + day + "일";
        dateTextView.setText(dateStr + "은, ");

        /* 해당 날짜에 일기가 있을 경우, RecyclerView 를 이용해 보여줌 */
        adapter.clearItems();
        for(Note item : items) {
            try {
                Date _date = MainActivity.dateFormat2.parse(item.getCreateDateStr2());
                if((_date.getYear() + 1900) == year && (_date.getMonth() + 1) == month && _date.getDate() == day) {
                    adapter.addItem(item);
                    setMoodTextView(item.getMood());
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

    class MyDayDecorator implements DayViewDecorator {
        private Context context;
        private CalendarDay day;
        private int moodIndex;

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
            //view.addSpan(new RelativeSizeSpan(0.7f));
            switch(moodIndex) {
                case 0:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_red)));
                    //view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.mood_angry_color));
                    break;
                case 1:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_blue)));
                    //view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.mood_cool_color));
                    break;
                case 2:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_skyblue)));
                    //view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.mood_crying_color));
                    break;
                case 3:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_green)));
                    //view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.mood_ill_color));
                    break;
                case 4:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_yellow)));
                    //view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.mood_laugh_color));
                    break;
                case 5:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_gray)));
                    //view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.mood_meh_color));
                    break;
                case 6:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_black)));
                    //view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.mood_sad));
                    break;
                case 7:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_orange)));
                    //view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.mood_smile_color));
                    break;
                case 8:
                    view.addSpan(new DotSpan(10f, context.getResources().getColor(R.color.pastel_pink)));
                    //view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.mood_yawn_color));
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
            view.addSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.skyblue)));
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
            view.addSpan(new ForegroundColorSpan(Color.RED));
        }
    }

    class TodayDecorator implements DayViewDecorator {

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
