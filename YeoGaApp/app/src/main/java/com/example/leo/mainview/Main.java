package com.example.leo.mainview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.leo.mainview.Gallery.select_gallery_menu;
import com.example.leo.mainview.Login.WebViewLogin;
import com.example.leo.mainview.Login.WebViewRegister;
import com.example.leo.mainview.Route.FindRoute;
import com.example.leo.mainview.otherThings.BackPressCloseHandler;
/*
    [메인 화면]
    로그인 버튼 -> 로그인 or 로그아웃 화면
    회원가입 버튼 -> 회원가입 화면 (웹뷰)
    사진 -> 사진 정리 및 지도상에서 사진 보기
    경로검색 -> 경로 검색 화면
 */

public class Main extends AppCompatActivity {

    public static final int REQUEST_CODE_ANOTHER = 1001;
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // main이미지 불러오기
        //ImageView image = (ImageView)this.findViewById(R.id.main_pic);

        // 1. 로그인 버튼
        ImageButton buttonLogin = (ImageButton)findViewById(R.id.b_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Main.this,WebViewLogin.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });
        // 2. 회원가입 버튼
        ImageButton buttonRegister = (ImageButton)findViewById(R.id.b_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Main.this,WebViewRegister.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });

        // 3. 사진버튼
        ImageButton buttonPicture = (ImageButton)findViewById(R.id.b_selectPicture);
        /*
        buttonPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, GalleryMain.class);
                startActivity(intent);
            }
        });
        */
        buttonPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, select_gallery_menu.class);
                startActivity(intent);
            }
        });

        // 4. 경로 버튼
        ImageButton buttonRoute = (ImageButton)findViewById(R.id.b_selectRoute);
        buttonRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, FindRoute.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });

        // 5. 뒤로가기 버튼을 눌렀을 때 나오는 Toast 메시지
        backPressCloseHandler = new BackPressCloseHandler(this);


        // 6. 액션바 숨기기
        getSupportActionBar().hide();

        // 7. Database 관리 (사용자 갤러리 정리용)

    }

    // 뒤로가기 처리
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    // Database

}
