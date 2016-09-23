package com.example.leo.mainview.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.example.leo.mainview.Main;
import com.example.leo.mainview.R;

public class WebViewRegister extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // 액션바 숨기기
        setContentView(R.layout.activity_web_view_register);




        WebView WebView = (WebView) findViewById(R.id.webviewRegister);
        WebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = WebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        WebView.loadUrl("http://jym.dothome.co.kr/g5/bbs/register.php");

        // 메인으로 가는 버튼
        Button buttonGoToHome = (Button)findViewById(R.id.gotomain);
        buttonGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WebViewRegister.this,Main.class);

                // 뒤로가기 했을경우 다시 돌아오면 안됨
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });
    }
}
