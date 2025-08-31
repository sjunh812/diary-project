package org.sjhstudio.diary.calendar;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.sjhstudio.diary.R;
import org.sjhstudio.diary.helper.OnCalItemClickListener;
import org.sjhstudio.diary.note.Note;

public class CalendarViewHolder extends RecyclerView.ViewHolder {
    private LinearLayout _itemView;
    private ImageView moodImageView;
    private ImageView weatherImageView;
    private ImageView starImageView;
    private TextView locationTextView;
    private TextView timeTextView;
    private TextView contentsTextView;

    private OnCalItemClickListener clickListener;

    public CalendarViewHolder(@NonNull View itemView) {
        super(itemView);

        _itemView = (LinearLayout) itemView.findViewById(R.id.itemView);
        moodImageView = (ImageView) itemView.findViewById(R.id.moodImageView);
        weatherImageView = (ImageView) itemView.findViewById(R.id.weatherImageView);
        starImageView = (ImageView) itemView.findViewById(R.id.starImageView);
        locationTextView = (TextView) itemView.findViewById(R.id.locationTextView);
        timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
        contentsTextView = (TextView) itemView.findViewById(R.id.contentsTextView);

        _itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (clickListener != null) {
                    clickListener.onItemClick(CalendarViewHolder.this, v, position);
                }
            }
        });
    }

    public void setItem(Note item) {
        setMoodImage(item.getMood());                   // 기분 이미지 설정
        setWeatherImage(item.getWeather());             // 날씨 이미지 설정
//        setStarImage(item.getStarIndex());              // 즐겨찾기 설정
        locationTextView.setSelected(true);             // 위치 marquee 처리
        timeTextView.setText(item.getTime());           // 시간 설정
        // 내용 설정
        if (item.getContents() == null || item.getContents().equals("")) {
            contentsTextView.setVisibility(View.GONE);
        } else {
            contentsTextView.setVisibility(View.VISIBLE);
            contentsTextView.setText(item.getContents());
        }
        // 위치 설정
        if (item.getAddress() == null || item.getAddress().isEmpty()) {
            locationTextView.setVisibility(View.GONE);
        } else {
            locationTextView.setVisibility(View.VISIBLE);
            locationTextView.setText(item.getAddress());
        }
    }

    private void setMoodImage(int index) {
        switch (index) {
            case 0:     // 화남
                moodImageView.setImageResource(R.drawable.ic_mood_angry);
                break;
            case 1:     // 쿨
                moodImageView.setImageResource(R.drawable.ic_mood_cool);
                break;
            case 2:     // 슬픔
                moodImageView.setImageResource(R.drawable.ic_mood_crying);
                break;
            case 3:     // 아픔
                moodImageView.setImageResource(R.drawable.ic_mood_ill);
                break;
            case 4:     // 웃음
                moodImageView.setImageResource(R.drawable.ic_mood_laugh);
                break;
            case 5:     // 보통
                moodImageView.setImageResource(R.drawable.ic_mood_meh);
                break;
            case 6:     // 나쁨
                moodImageView.setImageResource(R.drawable.ic_mood_sad);
                break;
            case 7:     // 좋음
                moodImageView.setImageResource(R.drawable.ic_mood_smile);
                break;
            case 8:     // 졸림
                moodImageView.setImageResource(R.drawable.ic_mood_yawn);
                break;
            default:    // default(미소)
                moodImageView.setImageResource(R.drawable.ic_mood_smile);
                break;
        }
    }

    private void setWeatherImage(int index) {
        switch (index) {
            case 0:
                weatherImageView.setImageResource(R.drawable.weather_icon_1);
                break;
            case 1:
                weatherImageView.setImageResource(R.drawable.weather_icon_2);
                break;
            case 2:
                weatherImageView.setImageResource(R.drawable.weather_icon_3);
                break;
            case 3:
                weatherImageView.setImageResource(R.drawable.weather_icon_4);
                break;
            case 4:
                weatherImageView.setImageResource(R.drawable.weather_icon_5);
                break;
            case 5:
                weatherImageView.setImageResource(R.drawable.weather_icon_6);
                break;
            case 6:
                weatherImageView.setImageResource(R.drawable.weather_icon_7);
                break;
            default:
                weatherImageView.setImageResource(R.drawable.weather_icon_1);
                break;
        }
    }

//    private void setStarImage(int index) {
//        if (index == 0) {
//            starImageView.setVisibility(View.GONE);
//        } else {
//            starImageView.setVisibility(View.VISIBLE);
//        }
//    }

    public void setClickListener(OnCalItemClickListener listener) {
        clickListener = listener;
    }
}
