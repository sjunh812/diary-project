package org.sjhstudio.diary.note;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import org.sjhstudio.diary.R;
import org.sjhstudio.diary.adapters.PhotoAdapter;
import org.sjhstudio.diary.helper.OnNoteItemClickListener;
import org.sjhstudio.diary.helper.OnNoteItemLongClickListener;
import org.sjhstudio.diary.helper.OnNoteItemTouchListener;
import org.sjhstudio.diary.helper.OnRequestListener;
import org.sjhstudio.diary.utils.Pref;

import java.util.ArrayList;
import java.util.Arrays;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    // Default view
    private LinearLayout contentsLayout;
    private ImageView moodImageView;
    private ImageView weatherImageView;
    private ImageView existPictureImageView;
    private TextView contentsTextView;
    private TextView locationTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView weekTextView;
    private LinearLayout bookmarkView;
    private RelativeLayout weatherAndLocationLayout;

    // Photo view
    private LinearLayout photoLayout;
    private ImageView moodImageView2;
    private ImageView weatherImageView2;
    private TextView contentsTextView2;
    private TextView locationTextView2;
    private TextView dateTextView2;
    private TextView timeTextView2;
    private TextView weekTextView2;
    private LinearLayout showPhotoStateView;

    private ViewPager2 photoViewPager;
    private PhotoAdapter photoAdapter;
    private LinearLayout photoIndicator;
    private TextView currentBanner;
    private TextView totalBanner;

    private OnNoteItemClickListener clickListener;
    private OnNoteItemTouchListener touchListener;
    private OnNoteItemLongClickListener longClickListener;
    private Context context;

    @SuppressLint("ClickableViewAccessibility")
    public NoteViewHolder(@NonNull View itemView, int type, Context context) {
        super(itemView);
        this.context = context;
        initPhoto();
        contentsLayout = itemView.findViewById(R.id.contentsLayout);
        moodImageView = itemView.findViewById(R.id.moodImageView);
        weatherImageView = itemView.findViewById(R.id.weatherImageView);
        existPictureImageView = itemView.findViewById(R.id.existPictureImageView);
        contentsTextView = itemView.findViewById(R.id.contentsTextView);
        locationTextView = itemView.findViewById(R.id.locationTextView);
        dateTextView = itemView.findViewById(R.id.dateTextView);
        timeTextView = itemView.findViewById(R.id.timeTextView);
        weekTextView = itemView.findViewById(R.id.weekTextView);
        bookmarkView = itemView.findViewById(R.id.bookmark_view);
        weatherAndLocationLayout = itemView.findViewById(R.id.weatherAndLocationLayout);
        photoLayout = itemView.findViewById(R.id.photoLayout);
        moodImageView2 = itemView.findViewById(R.id.moodImageView2);
        weatherImageView2 = itemView.findViewById(R.id.weatherImageView2);
        contentsTextView2 = itemView.findViewById(R.id.contentsTextView2);
        locationTextView2 = itemView.findViewById(R.id.locationTextView2);
        dateTextView2 = itemView.findViewById(R.id.dateTextView2);
        timeTextView2 = itemView.findViewById(R.id.timeTextView2);
        weekTextView2 = itemView.findViewById(R.id.weekTextView2);
        showPhotoStateView = itemView.findViewById(R.id.showPhotoStateView);

        contentsLayout.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if(clickListener != null) {
                clickListener.onItemClick(NoteViewHolder.this, v, position);
            }
        });
        photoLayout.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if(clickListener != null) {
                clickListener.onItemClick(NoteViewHolder.this, v, position);
            }
        });
        itemView.setOnTouchListener((v, event) -> {
            int position = getAdapterPosition();
            if(touchListener != null) {
                touchListener.onItemTouch(NoteViewHolder.this, v, position, event);
            }

            return true;
        });
        contentsLayout.setOnLongClickListener(v -> {
            int position = getAdapterPosition();
            if(longClickListener != null) {
                longClickListener.onLongClick(NoteViewHolder.this, v, position);
                return true;
            }

            return false;
        });
        photoLayout.setOnLongClickListener(v -> {
            int position = getAdapterPosition();
            if(longClickListener != null) {
                longClickListener.onLongClick(NoteViewHolder.this, v, position);
                return true;
            }

            return false;
        });

        setLayoutType(type);
    }

    private void initPhoto() {
        photoIndicator = itemView.findViewById(R.id.photo_indicator);
        currentBanner = itemView.findViewById(R.id.current_banner);
        totalBanner = itemView.findViewById(R.id.total_banner);
        photoViewPager = itemView.findViewById(R.id.photo_view_pager);
        photoViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        photoAdapter = new PhotoAdapter(context, null);
        photoViewPager.setAdapter(photoAdapter);
        photoViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                Log.d("Log", "onPageScrolled");
                photoViewPager.getParent().requestDisallowInterceptTouchEvent(true);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentBanner.setText(String.valueOf(position + 1));
                Log.d("LOG", photoAdapter.getItems().get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    public void setItem(Note item) {
        // 기분 설정
        int moodIndex = item.getMood();
        setMoodImage(moodIndex);

        // 사진 설정
        if(item.getPicture() != null && !item.getPicture().equals("")) {
            String picturePaths[] = item.getPicture().split(",");
            if(picturePaths.length > 0) {
                photoAdapter.setItems(new ArrayList<String>(Arrays.asList(picturePaths)));
//                photoAdapter.getItems().remove(photoAdapter.getItemCount()-1);
                photoAdapter.notifyDataSetChanged();
                totalBanner.setText(String.valueOf(photoAdapter.getItemCount()));

                existPictureImageView.setVisibility(View.VISIBLE);
                photoViewPager.setVisibility(View.VISIBLE);
                photoIndicator.setVisibility(View.VISIBLE);
                showPhotoStateView.setVisibility(View.GONE);
            }
        } else {
            existPictureImageView.setVisibility(View.INVISIBLE);
            photoViewPager.setVisibility(View.GONE);
            photoIndicator.setVisibility(View.GONE);
            showPhotoStateView.setVisibility(View.VISIBLE);
        }

        // 날씨 설정
        int weatherIndex = item.getWeather();
        setWeatherImage(weatherIndex);

        // 내용 설정
        String contents = item.getContents();
        contentsTextView.setText(contents);
        contentsTextView2.setText(contents);
        if(Pref.getPSkipNote(context)) {
            contentsTextView.setMaxLines(3);
            contentsTextView2.setMaxLines(3);
        } else {
            contentsTextView.setMaxLines(Integer.MAX_VALUE);
            contentsTextView2.setMaxLines(Integer.MAX_VALUE);
        }

        // 위치 설정
        String location = item.getAddress();
        locationTextView.setText(location);
        locationTextView2.setText(location);
        locationTextView.setSelected(true);
        locationTextView2.setSelected(true);

        // 날짜 설정
        String date = item.getCreateDateStr();
        dateTextView.setText(date);
        dateTextView2.setText(date);

        // 시간 설정
        String time = item.getTime();
        timeTextView.setText(time);
        timeTextView2.setText(time);

        // 요일 설정
        String dayOfWeek = item.getDayOfWeek();
        weekTextView.setText(dayOfWeek);
        weekTextView2.setText(dayOfWeek);

        // 즐겨찾기 설정
        int starIndex = item.getStarIndex();
        setStarImage(starIndex);

        if(contents == null || contents.equals("")) {
            contentsTextView.setVisibility(View.GONE);
            contentsTextView2.setVisibility(View.GONE);
        } else {
            contentsTextView.setVisibility(View.VISIBLE);
            contentsTextView2.setVisibility(View.VISIBLE);
        }

        if((location == null || location.equals("")) && (weatherIndex == -1)) {
            weatherAndLocationLayout.setVisibility(View.GONE);
        } else {
            weatherAndLocationLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setMoodImage(int index) {
        switch(index) {
            case 0:     // 화남
                moodImageView.setImageResource(R.drawable.mood_angry_color);
                moodImageView2.setImageResource(R.drawable.mood_angry_color);
                break;
            case 1:     // 쿨
                moodImageView.setImageResource(R.drawable.mood_cool_color);
                moodImageView2.setImageResource(R.drawable.mood_cool_color);
                break;
            case 2:     // 슬픔
                moodImageView.setImageResource(R.drawable.mood_crying_color);
                moodImageView2.setImageResource(R.drawable.mood_crying_color);
                break;
            case 3:     // 아픔
                moodImageView.setImageResource(R.drawable.mood_ill_color);
                moodImageView2.setImageResource(R.drawable.mood_ill_color);
                break;
            case 4:     // 웃음
                moodImageView.setImageResource(R.drawable.mood_laugh_color);
                moodImageView2.setImageResource(R.drawable.mood_laugh_color);
                break;
            case 5:     // 보통
                moodImageView.setImageResource(R.drawable.mood_meh_color);
                moodImageView2.setImageResource(R.drawable.mood_meh_color);
                break;
            case 6:     // 나쁨
                moodImageView.setImageResource(R.drawable.mood_sad);
                moodImageView2.setImageResource(R.drawable.mood_sad);
                break;
            case 7:     // 좋음
                moodImageView.setImageResource(R.drawable.mood_smile_color);
                moodImageView2.setImageResource(R.drawable.mood_smile_color);
                break;
            case 8:     // 졸림
                moodImageView.setImageResource(R.drawable.mood_yawn_color);
                moodImageView2.setImageResource(R.drawable.mood_yawn_color);
                break;
            default:    // default(미소)
                moodImageView.setImageResource(R.drawable.mood_smile_color);
                moodImageView2.setImageResource(R.drawable.mood_smile_color);
                break;
        }
    }

    private void setWeatherImage(int index) {
        switch(index) {
            case 0:
                weatherImageView.setImageResource(R.drawable.weather_icon_1);
                weatherImageView2.setImageResource(R.drawable.weather_icon_1);
                break;
            case 1:
                weatherImageView.setImageResource(R.drawable.weather_icon_2);
                weatherImageView2.setImageResource(R.drawable.weather_icon_2);
                break;
            case 2:
                weatherImageView.setImageResource(R.drawable.weather_icon_3);
                weatherImageView2.setImageResource(R.drawable.weather_icon_3);
                break;
            case 3:
                weatherImageView.setImageResource(R.drawable.weather_icon_4);
                weatherImageView2.setImageResource(R.drawable.weather_icon_4);
                break;
            case 4:
                weatherImageView.setImageResource(R.drawable.weather_icon_5);
                weatherImageView2.setImageResource(R.drawable.weather_icon_5);
                break;
            case 5:
                weatherImageView.setImageResource(R.drawable.weather_icon_6);
                weatherImageView2.setImageResource(R.drawable.weather_icon_6);
                break;
            case 6:
                weatherImageView.setImageResource(R.drawable.weather_icon_7);
                weatherImageView2.setImageResource(R.drawable.weather_icon_7);
                break;
            default:
/*                weatherImageView.setImageResource(R.drawable.weather_icon_1);
                weatherImageView2.setImageResource(R.drawable.weather_icon_1);*/
                break;
        }
    }

    private void setStarImage(int index) {
        if(index == 0) bookmarkView.setVisibility(View.GONE);
        else bookmarkView.setVisibility(View.VISIBLE);
    }

    public void setLayoutType(int type) {
        if(type == 0) {
            contentsLayout.setVisibility(View.VISIBLE);
            photoLayout.setVisibility(View.GONE);
        } else if(type == 1) {
            contentsLayout.setVisibility(View.GONE);
            photoLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setOnItemClickListener(OnNoteItemClickListener listener) {
        clickListener = listener;
    }

    public void setOnItemTouchListener(OnNoteItemTouchListener listener) {
        touchListener = listener;
    }

    public void setOnItemLongClickListener(OnNoteItemLongClickListener listener) {
        longClickListener = listener;
    }

}
