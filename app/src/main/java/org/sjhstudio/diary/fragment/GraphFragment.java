package org.sjhstudio.diary.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.MPPointF;

import org.sjhstudio.diary.R;
import org.sjhstudio.diary.custom.MyRadioButton;
import org.sjhstudio.diary.note.NoteDatabaseCallback;
import org.sjhstudio.diary.utils.Pref;
import org.sjhstudio.diary.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphFragment extends Fragment {

    private MyRadioButton allButton;
    private MyRadioButton yearButton;
    private MyRadioButton monthButton;
    private TextView moodTitleTextView;
    private TextView moodTotalCountTextView;
    private TextView angryCount;
    private TextView coolCount;
    private TextView cryingCount;
    private TextView illCount;
    private TextView laughCount;
    private TextView mehCount;
    private TextView sadCount;
    private TextView smileCount;
    private TextView yawnCount;
    private ImageView angryImageView;
    private ImageView coolImageView;
    private ImageView cryingImageView;
    private ImageView illImageView;
    private ImageView laughImageView;
    private ImageView mehImageView;
    private ImageView sadImageView;
    private ImageView smileImageView;
    private ImageView yawnImageView;
    private ImageView crown;
    private ImageView crown2;
    private ImageView crown3;
    private ImageView crown4;
    private ImageView crown5;
    private ImageView crown6;
    private ImageView crown7;
    private ImageView crown8;
    private ImageView crown9;
    private LinearLayout textView;
    private TextView describeTextView;
    private TextView moodTextView;
    private FrameLayout backgroundGraphLayout;

    private PieChart chart1;
    private NoteDatabaseCallback callback;

    int[] moodIconRes = {
            R.drawable.icon_mood_angry_color_small, R.drawable.icon_mood_cool_color_small, R.drawable.icon_mood_crying_color_small,
            R.drawable.icon_mood_ill_color_small, R.drawable.icon_mood_laugh_color_small, R.drawable.icon_mood_meh_color_small,
            R.drawable.icon_mood_sad_color_small, R.drawable.icon_mood_smile_color_small, R.drawable.icon_mood_yawn_color_small
    };
    private ArrayList<Integer> colors = new ArrayList<>();      // ?????? ????????? ?????? ??????
    private int curFontIndex = -1;                              // ?????? ???????????? ?????? ??????
    private int selectRadioIndex = 0;                           // ??????: 0, ??????: 1, ?????????: 2 (default: ??????)
    private int maxMoodIndex = -1;                              // ?????? ?????? ?????? ??????
    private int maxCount = -1;                                  // ?????? ?????? ????????? ??????

    private Animation translateRightAnim;
    private Animation moodAnimation;
    private Animation crownAnimation;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof NoteDatabaseCallback) {
            callback = (NoteDatabaseCallback)context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clearResource();
    }

    private void clearResource() {
        if(callback != null) callback = null;
        clearAnimation();
    }

    private void clearAnimation() {
        crown.clearAnimation();
        crown2.clearAnimation();
        crown3.clearAnimation();
        crown4.clearAnimation();
        crown5.clearAnimation();
        crown6.clearAnimation();
        crown7.clearAnimation();
        crown8.clearAnimation();
        crown9.clearAnimation();
        angryImageView.clearAnimation();
        coolImageView.clearAnimation();
        cryingImageView.clearAnimation();
        illImageView.clearAnimation();
        laughImageView.clearAnimation();
        mehImageView.clearAnimation();
        sadImageView.clearAnimation();
        smileImageView.clearAnimation();
        yawnImageView.clearAnimation();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_graph,
                container,
                false
        );

        curFontIndex = Pref.getPFontKey(requireContext());  // ?????? ??????
        initAnimation();    // ??????????????? ?????????
        initChartUI(rootView);  // ?????? ?????????
        initUI(rootView);   // UI ?????????
        initRadioButton(rootView);  // ????????? ?????? ?????????

        return rootView;
    }

    private void initAnimation() {
        translateRightAnim = AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_animation);
        moodAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.mood_icon_animation);
        crownAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.crown_icon_animation);
        translateRightAnim.setDuration(350);    // ????????? ??????????????? ?????? -> 0.35???
    }

    private void initChartUI(View rootView) {
        chart1 = (PieChart)rootView.findViewById(R.id.chart1);
        chart1.setUsePercentValues(true);
        chart1.getDescription().setEnabled(false);  // ?????? ?????? x
        chart1.setDrawHoleEnabled(false);   // ?????? ??? x
        chart1.setExtraOffsets(5,10,5,10);
        chart1.setHighlightPerTapEnabled(false);    // ?????? ???, ???????????? x

//        chart1.setTransparentCircleColor(getResources().getColor(R.color.white)); // ???????????? ????????? ????????? ?????? ???????????? ?????? ??????
//        chart1.setTransparentCircleAlpha(110);    // ???????????? ????????? ????????? ?????? ???????????? ?????? ??? ??????
//        chart1.setTransparentCircleRadius(66f);   // ???????????? ????????? ????????? ?????? ???????????? ?????????
//        chart1.setHoleRadius(58f);    // ???????????? ?????????
//        chart1.setHoleColor(getResources().getColor(R.color.azure2));
//        chart1.setDrawCenterText(true);

        Legend legend1 = chart1.getLegend();    // ???????????? ?????????????????? ????????? ??????????????? ??????
        legend1.setEnabled(false);  // ?????? ???????????? ?????? false
        chart1.setEntryLabelColor(Color.WHITE); // entry label ??????
//        chart1.setEntryLabelTextSize(12f);    // entry label ??????
        chart1.animateXY(1200, 1200);   // ?????? ???????????????
    }

    private void initUI(View rootView) {
        TextView titleTextView = (TextView) rootView.findViewById(R.id.titleTextView);
        titleTextView.startAnimation(translateRightAnim);

        moodTotalCountTextView = (TextView)rootView.findViewById(R.id.moodTotalCountTextView);
        moodTitleTextView = (TextView)rootView.findViewById(R.id.moodTitleTextView);
        angryCount = (TextView)rootView.findViewById(R.id.angryCount);
        coolCount = (TextView)rootView.findViewById(R.id.coolCount);
        cryingCount = (TextView)rootView.findViewById(R.id.cryingCount);
        illCount = (TextView)rootView.findViewById(R.id.illCount);
        laughCount = (TextView)rootView.findViewById(R.id.laughCount);
        mehCount = (TextView)rootView.findViewById(R.id.mehCount);
        sadCount = (TextView)rootView.findViewById(R.id.sadCount);
        smileCount = (TextView)rootView.findViewById(R.id.smileCount);
        yawnCount = (TextView)rootView.findViewById(R.id.yawnCount);
        crown = (ImageView)rootView.findViewById(R.id.crown);
        crown2 = (ImageView)rootView.findViewById(R.id.crown2);
        crown3 = (ImageView)rootView.findViewById(R.id.crown3);
        crown4 = (ImageView)rootView.findViewById(R.id.crown4);
        crown5 = (ImageView)rootView.findViewById(R.id.crown5);
        crown6 = (ImageView)rootView.findViewById(R.id.crown6);
        crown7 = (ImageView)rootView.findViewById(R.id.crown7);
        crown8 = (ImageView)rootView.findViewById(R.id.crown8);
        crown9 = (ImageView)rootView.findViewById(R.id.crown9);
        angryImageView = (ImageView)rootView.findViewById(R.id.angryImageView);
        coolImageView = (ImageView)rootView.findViewById(R.id.coolImageView);
        cryingImageView = (ImageView)rootView.findViewById(R.id.cryingImageView);
        illImageView = (ImageView)rootView.findViewById(R.id.illImageView);
        laughImageView = (ImageView)rootView.findViewById(R.id.laughImageView);
        mehImageView = (ImageView)rootView.findViewById(R.id.mehImageView);
        sadImageView = (ImageView)rootView.findViewById(R.id.sadImageView);
        smileImageView = (ImageView)rootView.findViewById(R.id.smileImageView);
        yawnImageView = (ImageView)rootView.findViewById(R.id.yawnImageView);
        textView = (LinearLayout)rootView.findViewById(R.id.textView);
        describeTextView = (TextView)rootView.findViewById(R.id.describeTextView);
        moodTextView = (TextView)rootView.findViewById(R.id.moodTextView);
        backgroundGraphLayout = (FrameLayout)rootView.findViewById(R.id.background_graph);
    }

    @SuppressLint("SetTextI18n")
    private void initRadioButton(View rootView) {
        allButton = (MyRadioButton)rootView.findViewById(R.id.allButton);
        yearButton = (MyRadioButton)rootView.findViewById(R.id.yearButton);
        monthButton = (MyRadioButton)rootView.findViewById(R.id.monthButton);
        RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            HashMap<Integer, Integer> hashMap = null;

            if(checkedId == R.id.allButton) {
                moodTitleTextView.setText("??????");
                selectRadioIndex = 0;
                hashMap = callback.selectMoodCount(true, false, false);
                describeTextView.setText("?????? ?????? ??? ?????? ?????? ????????? ");
            } else if(checkedId == R.id.yearButton) {
                moodTitleTextView.setText(Utils.INSTANCE.getCurrentYear() + "???");
                selectRadioIndex = 1;
                hashMap = callback.selectMoodCount(false, true, false);
                describeTextView.setText("?????? ?????? ??? ?????? ?????? ????????? ");
            } else if(checkedId == R.id.monthButton) {
                moodTitleTextView.setText(Utils.INSTANCE.getCurrentMonth() + "???");
                selectRadioIndex = 2;
                hashMap = callback.selectMoodCount(false, false, true);
                describeTextView.setText("????????? ?????? ??? ?????? ?????? ????????? ");
            }

            chart1.setCenterTextTypeface(getCurTypeFace());
            chart1.setCenterTextSize(17f);
            setData1(hashMap);
        });

        setSelectedRadioButton();       // ????????? ??????????????? index ??? ?????? ??????????????? Checked ?????????
    }

    private void setSelectedRadioButton() {
        switch(selectRadioIndex) {
            case 0:
                allButton.setChecked(true);
                break;
            case 1:
                yearButton.setChecked(true);
                break;
            case 2:
                monthButton.setChecked(true);
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setData1(HashMap<Integer, Integer> hashMap) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        int totalCount = 0; // ??? ?????? ??? (??????, ??????, ??????????????? ???????????? ???????????? ???????????? ?????????)
        maxMoodIndex = -1;  // ?????? ?????? ?????? ??????
        maxCount = -1;      // ?????? ?????? ????????? ??????
        colors.clear();     // ??????????????? ????????? ???????????? clear (??????, ??????, ??????????????? ???????????? ???????????? clear ??????)

        for(int i = 0; i < 9; i++) {
            int count = 0;

            if(hashMap.containsKey(i)) {
                count = hashMap.get(i);
                setMoodCount(i, count);
                totalCount += count;
                addColor(i);                // ?????? ????????? ?????? ?????? ??????
                entries.add(new PieEntry(
                        count,
                        "",
                        ContextCompat.getDrawable(requireContext(), moodIconRes[i])
                ));
            } else {
                setMoodCount(i, count);     // ?????? 0
            }
        }

        moodTotalCountTextView.setText("(??? " + totalCount + "??? ???)");        // ??? ?????? ??????
        setCrownImage();                                        // ?????? ?????? ????????? ?????? ????????? ?????????????????? ??????

        PieDataSet dataSet = new PieDataSet(entries, "????????? ??????");
        dataSet.setDrawIcons(true);                             // ????????? ?????? ??????
        dataSet.setSliceSpace(10f);                             // ????????? ??????
        dataSet.setIconsOffset(new MPPointF(0, 55));      // ????????? offset
//        dataSet.setSelectionShift(5f);                       // ???????????? ????????? ???????????? ??????
        dataSet.setColors(colors);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f", value) + "%";
            }
        });

        PieData data = new PieData(dataSet);
        data.setValueTextSize(15f);                         // ????????? ??? text ??????
        data.setValueTextColor(Color.WHITE);                // ????????? ??? text ??????
        data.setValueTypeface(getCurTypeFace());            // ????????? ??? text ??????

        chart1.setData(data);
        chart1.invalidate();

        if(totalCount == 0) backgroundGraphLayout.setVisibility(View.GONE);
        else backgroundGraphLayout.setVisibility(View.VISIBLE);
    }

    private void setMoodCount(int moodIndex, int count) {
        if(maxCount < count) {
            maxCount = count;
            maxMoodIndex = moodIndex;
        } else if(maxCount == count) {      // ?????? ?????? ?????? max ?????? ????????????
            maxMoodIndex = -1;
        }

        switch(moodIndex) {
            case 0:
                angryCount.setText(String.valueOf(count));
                break;
            case 1:
                coolCount.setText(String.valueOf(count));
                break;
            case 2:
                cryingCount.setText(String.valueOf(count));
                break;
            case 3:
                illCount.setText(String.valueOf(count));
                break;
            case 4:
                laughCount.setText(String.valueOf(count));
                break;
            case 5:
                mehCount.setText(String.valueOf(count));
                break;
            case 6:
                sadCount.setText(String.valueOf(count));
                break;
            case 7:
                smileCount.setText(String.valueOf(count));
                break;
            case 8:
                yawnCount.setText(String.valueOf(count));
                break;
        }
    }

    private void setCrownImage() {
        clearAnimation();
        crown.setVisibility(View.GONE);
        crown2.setVisibility(View.GONE);
        crown3.setVisibility(View.GONE);
        crown4.setVisibility(View.GONE);
        crown5.setVisibility(View.GONE);
        crown6.setVisibility(View.GONE);
        crown7.setVisibility(View.GONE);
        crown8.setVisibility(View.GONE);
        crown9.setVisibility(View.GONE);

        if(maxMoodIndex == -1) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);

            switch(maxMoodIndex) {
                case 0:
                    crown.setVisibility(View.VISIBLE);
                    angryImageView.startAnimation(moodAnimation);
                    crown.startAnimation(crownAnimation);

                    moodTextView.setText("'??????'");
                    moodTextView.setTextColor(getResources().getColor(R.color.red));
                    break;
                case 1:
                    crown2.setVisibility(View.VISIBLE);
                    coolImageView.startAnimation(moodAnimation);
                    crown2.startAnimation(crownAnimation);

                    moodTextView.setText("'???'");
                    moodTextView.setTextColor(getResources().getColor(R.color.blue));
                    break;
                case 2:
                    crown3.setVisibility(View.VISIBLE);
                    cryingImageView.startAnimation(moodAnimation);
                    crown3.startAnimation(crownAnimation);

                    moodTextView.setText("'??????'");
                    moodTextView.setTextColor(getResources().getColor(R.color.skyblue));
                    break;
                case 3:
                    crown4.setVisibility(View.VISIBLE);
                    illImageView.startAnimation(moodAnimation);
                    crown4.startAnimation(crownAnimation);

                    moodTextView.setText("'??????'");
                    moodTextView.setTextColor(getResources().getColor(R.color.lightgreen));
                    break;
                case 4:
                    crown5.setVisibility(View.VISIBLE);
                    laughImageView.startAnimation(moodAnimation);
                    crown5.startAnimation(crownAnimation);

                    moodTextView.setText("'??????'");
                    moodTextView.setTextColor(getResources().getColor(R.color.yellow));
                    break;
                case 5:
                    crown6.setVisibility(View.VISIBLE);
                    mehImageView.startAnimation(moodAnimation);
                    crown6.startAnimation(crownAnimation);

                    moodTextView.setText("'??????'");
                    moodTextView.setTextColor(getResources().getColor(R.color.gray));
                    break;
                case 6:
                    crown7.setVisibility(View.VISIBLE);
                    sadImageView.startAnimation(moodAnimation);
                    crown7.startAnimation(crownAnimation);

                    moodTextView.setText("'??????'");
                    moodTextView.setTextColor(getResources().getColor(R.color.black));
                    break;
                case 7:
                    crown8.setVisibility(View.VISIBLE);
                    smileImageView.startAnimation(moodAnimation);
                    crown8.startAnimation(crownAnimation);

                    moodTextView.setText("'??????'");
                    moodTextView.setTextColor(getResources().getColor(R.color.orange));
                    break;
                case 8:
                    crown9.setVisibility(View.VISIBLE);
                    yawnImageView.startAnimation(moodAnimation);
                    crown9.startAnimation(crownAnimation);

                    moodTextView.setText("'??????'");
                    moodTextView.setTextColor(getResources().getColor(R.color.pink));
                    break;
            }
        }
    }

    private void addColor(int moodIndex) {
        switch(moodIndex) {
            case 0:
                colors.add(getResources().getColor(R.color.pastel_red));
                break;
            case 1:
                colors.add(getResources().getColor(R.color.pastel_blue));
                break;
            case 2:
                colors.add(getResources().getColor(R.color.pastel_skyblue));
                break;
            case 3:
                colors.add(getResources().getColor(R.color.pastel_green));
                break;
            case 4:
                colors.add(getResources().getColor(R.color.pastel_yellow));
                break;
            case 5:
                colors.add(getResources().getColor(R.color.pastel_gray));
                break;
            case 6:
                colors.add(getResources().getColor(R.color.pastel_black));
                break;
            case 7:
                colors.add(getResources().getColor(R.color.pastel_orange));
                break;
            case 8:
                colors.add(getResources().getColor(R.color.pastel_pink));
                break;
        }
    }

    private Typeface getCurTypeFace() {
        Typeface typeface;

        switch(curFontIndex) {
            case 100:
                typeface = Typeface.SANS_SERIF;
                break;
            case -1:
                typeface = Typeface.createFromAsset(requireContext().getAssets(), "font.ttf");
                break;
            case 0:
                typeface = Typeface.createFromAsset(requireContext().getAssets(), "font1.ttf");
                break;
            case 1:
                typeface = Typeface.createFromAsset(requireContext().getAssets(), "font2.ttf");
                break;
            case 2:
                typeface = Typeface.createFromAsset(requireContext().getAssets(), "font3.ttf");
                break;
            case 3:
                typeface = Typeface.createFromAsset(requireContext().getAssets(), "font4.ttf");
                break;
            case 4:
                typeface = Typeface.createFromAsset(requireContext().getAssets(), "font5.ttf");
                break;
            case 5:
                typeface = Typeface.createFromAsset(requireContext().getAssets(), "font6.ttf");
                break;
            case 6:
                typeface = Typeface.createFromAsset(requireContext().getAssets(), "font7.ttf");
                break;
            case 7:
                typeface = Typeface.createFromAsset(requireContext().getAssets(), "font8.ttf");
                break;
            case 8:
                typeface = Typeface.createFromAsset(requireContext().getAssets(), "font9.ttf");
                break;
            case 9:
                typeface = Typeface.createFromAsset(requireContext().getAssets(), "font10.ttf");
                break;
            case 10:
                typeface = Typeface.createFromAsset(requireContext().getAssets(), "font11.ttf");
                break;
            case 11:
                typeface = Typeface.createFromAsset(requireContext().getAssets(), "font12.ttf");
                break;
            default:
                typeface = Typeface.createFromAsset(requireContext().getAssets(), "font1.ttf");
                break;
        }

        return typeface;
    }

//    private Drawable resizeDrawable(int res) {
//        BitmapDrawable drawable = (BitmapDrawable)getResources().getDrawable(res);
//        Bitmap bitmap = drawable.getBitmap();
//        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 55, 55, true);
//        Drawable newDrawable = new BitmapDrawable(context.getResources(), newBitmap);
//
//        return newDrawable;
//    }

}
