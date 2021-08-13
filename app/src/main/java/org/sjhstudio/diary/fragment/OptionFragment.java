package org.sjhstudio.diary.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.sjhstudio.diary.AlarmActivity;
import org.sjhstudio.diary.BackupActivity;
import org.sjhstudio.diary.DarkModeActivity;
import org.sjhstudio.diary.FontActivity;
import org.sjhstudio.diary.PasswordActivity;
import org.sjhstudio.diary.R;
import org.sjhstudio.diary.helper.AppHelper;
import org.sjhstudio.diary.helper.MyTheme;
import org.sjhstudio.diary.note.NoteDatabaseCallback;

public class OptionFragment extends Fragment {
    /** 상수 **/
    public static final int REQUEST_FONT_CHANGE = 101;
    public static final int REQUEST_ALARM_SETTING = 102;
    public static final int REQUEST_DARK_MODE = 103;

    /** UI **/
    private TextView curFontTextView;
    private TextView allCountTextView;
    private TextView starCountTextView;

    /** data **/
    private NoteDatabaseCallback callback;  // DB 접근을 위한 callback
    private int allCount = 0;               // 작성한 일기 총 개수
    private int starCount = 0;              // 즐겨찾기 총 개수
    private int curFontIndex = 0;           // 현재 선택한 폰트종류

    /** animation **/
    private Animation translateRightAnim;   // 타이틀 애니메이션

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

        if(callback != null) {
            callback = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_option, container, false);

        setTitleTextView(rootView);
        setCountTextView(rootView);
        setCurFontText(rootView);

        /* 폰트 설정 */
        RelativeLayout fontLayout = (RelativeLayout)rootView.findViewById(R.id.fontLayout);
        fontLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FontActivity.class);
                getActivity().startActivityForResult(intent, REQUEST_FONT_CHANGE);
            }
        });

        /* 알림 설정 */
        RelativeLayout noticeLayout = (RelativeLayout)rootView.findViewById(R.id.noticeLayout);
        noticeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AlarmActivity.class);
                getActivity().startActivityForResult(intent, REQUEST_ALARM_SETTING);
            }
        });

        /* 다크모드 설정 */
        RelativeLayout darkmodLayout = (RelativeLayout)rootView.findViewById(R.id.darkmodeLayout);
        darkmodLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DarkModeActivity.class);
                getActivity().startActivityForResult(intent, REQUEST_DARK_MODE);
            }
        });

        /* 잠금설정 */
        RelativeLayout passwordLayout = (RelativeLayout)rootView.findViewById(R.id.passwordlayout);
        passwordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PasswordActivity.class);
                getActivity().startActivity(intent);
            }
        });

        /* 잠금해제 */
        RelativeLayout releasePasswordLayout = (RelativeLayout)rootView.findViewById(R.id.releasepasswordlayout);
        releasePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getContext().getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
                if(pref != null) {
                   String password = pref.getString(MyTheme.PASSWORD, "");
                   if(!password.equals("")) {
                       SharedPreferences.Editor editor = pref.edit();
                       editor.remove(MyTheme.PASSWORD);
                       editor.commit();

                       Toast.makeText(getContext(), "비밀번호가 해제되었습니다.", Toast.LENGTH_SHORT).show();
                       return;
                   }
                }

                Toast.makeText(getContext(), "설정된 비밀번호가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        /* 백업 및 복원하기 */
        RelativeLayout backupLayout = (RelativeLayout)rootView.findViewById(R.id.backupLayout);
        backupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BackupActivity.class);
                startActivity(intent);
            }
        });

        /* 스토어 리뷰 */
        RelativeLayout reviewLayout = (RelativeLayout)rootView.findViewById(R.id.reviewLayout);
        reviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + getContext().getPackageName()));
                startActivity(intent);
            }
        });

        /* 피드백 보내기 */
        RelativeLayout ideaLayout = (RelativeLayout)rootView.findViewById(R.id.ideaLayout);
        ideaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppHelper helper = new AppHelper(getContext());
                String[] email = {"sjunh812@naver.com"};
                String appVersion = helper.getVersionName();
                String osVersion = helper.getOsName();
                String modelName = helper.getModelName();
                String contents = "안녕하세요!\n" +
                        "소중한 의견을 보내주셔서 감사합니다 :)\n" +
                        "신중하게 검토 후 답변드리겠습니다.\n" +
                        "----------------------------\n" +
                        "앱 버전 : " + appVersion +
                        "\n기기명 : " + modelName +
                        "\n안드로이드 OS : " + osVersion +
                        "\n----------------------------\n";

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/Text");
                intent.putExtra(Intent.EXTRA_EMAIL, email);
                intent.putExtra(Intent.EXTRA_SUBJECT, "[" + getString(R.string.app_name) + "] " + getString(R.string.report));
                intent.putExtra(Intent.EXTRA_TEXT, contents);
                intent.setType("message/rfc822");
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void setTitleTextView(View rootView) {
        // animation
        translateRightAnim = AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_animation);
        translateRightAnim.setDuration(350);

        TextView titleTextView = (TextView)rootView.findViewById(R.id.titleTextView);
        titleTextView.startAnimation(translateRightAnim);
    }

    private void setCountTextView(View rootView) {
        allCountTextView = (TextView)rootView.findViewById(R.id.allCountTextView);
        starCountTextView = (TextView)rootView.findViewById(R.id.starCountTextView);
        allCount = callback.selectAllCount();
        starCount = callback.selectStarCount();

        allCountTextView.setText(allCount + "개");
        starCountTextView.setText(starCount + "개");
    }

    private void setCurFontText(View rootView) {
        Log.d("LOG", "index : " + curFontIndex);
        SharedPreferences pref = getContext().getSharedPreferences(MyTheme.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);
        if(pref != null) {
            curFontIndex = pref.getInt(MyTheme.FONT_KEY, 0);
        }

        curFontTextView = (TextView)rootView.findViewById(R.id.curFontTextView);
        switch(curFontIndex) {
            case 100:
                curFontTextView.setText("시스템 서체");
                break;
            case -1:
                curFontTextView.setText("THE얌전해진언니체");
                break;
            case 0:
                curFontTextView.setText("교보 손글씨체");
                break;
            case 1:
                curFontTextView.setText("점꼴체");
                break;
            case 2:
                curFontTextView.setText("넥슨 배찌체");
                break;
            case 3:
                curFontTextView.setText("미니콩다방체");
                break;
            case 4:
                curFontTextView.setText("꼬마나비체");
                break;
            case 5:
                curFontTextView.setText("IM혜민체");
                break;
            case 6:
                curFontTextView.setText("칠곡할매 이원순체");
                break;
            case 7:
                curFontTextView.setText("카페24 숑숑체");
                break;
            case 8:
                curFontTextView.setText("온글잎 만두몽키체");
                break;
            case 9:
                curFontTextView.setText("봄이조아체");
                break;
            default:
                curFontTextView.setText("THE얌전해진언니체");
                break;
        }
    }
}
