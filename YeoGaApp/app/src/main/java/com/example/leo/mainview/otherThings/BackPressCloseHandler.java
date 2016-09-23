package com.example.leo.mainview.otherThings;
/*
    메인에서 뒤로가기를 1번 누를 경우 Toast를 띄워 종료를 물어본다.
    Toast가 나타난 시간동안 뒤로가기를 한 번 더 누를 경우 앱이 종료된다.
 */

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Leo on 2016-06-05.
 */
public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,"\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
