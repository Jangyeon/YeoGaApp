package com.example.leo.mainview.Login;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.leo.mainview.Main;
import com.example.leo.mainview.R;

import org.json.JSONException;
import org.json.JSONObject;
/*
    [로그인 or 로그아웃 화면]
    계획 변경 : 웹뷰로 구성 -> 네이티브 앱
    로그인 필요성 : 사용자가 사진을 서버로 보냈을 경우 사용자의 정보를 서버 DB에 저장
 */

public class WebViewLogin extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 액션바 지우기
        setContentView(R.layout.activity_web_view_login);

        // 로그인 창 활성화
        final TextView txtView = (TextView) findViewById(R.id.text_login);
        final EditText etID = (EditText) findViewById(R.id.main_id);
        final EditText etPassword = (EditText) findViewById(R.id.main_password);
        final Button bLogin = (Button) findViewById(R.id.main_login);
        final Button bGoHome = (Button) findViewById(R.id.gotomain);
        final Button b_logout = (Button) findViewById(R.id.b_logout);

        // 로그인인지 로그아웃인지 모르기때문에 우선 가린다.
        txtView.setVisibility(View.INVISIBLE);
        etID.setVisibility(View.INVISIBLE);
        etPassword.setVisibility(View.INVISIBLE);
        bLogin.setVisibility(View.INVISIBLE);
        bGoHome.setVisibility(View.INVISIBLE);
        b_logout.setVisibility(View.INVISIBLE);

        // 로그인 체크 준비
        SharedPreferences prefRead = getSharedPreferences("logInfo", MODE_PRIVATE);
        String checkTheLogin = "";
        checkTheLogin = prefRead.getString("ID","");
        //prefRead.getString("PASSWORD","");

        // 1. 로그인이 안되어있는 경우 -> 로그인
        if(checkTheLogin == "") {
            txtView.setVisibility(View.VISIBLE);
            etID.setVisibility(View.VISIBLE);
            etPassword.setVisibility(View.VISIBLE);
            bLogin.setVisibility(View.VISIBLE);
            bGoHome.setVisibility(View.VISIBLE);
        }

        // 2. 로그인이 되어있는 경우 -> 로그아웃
        else {
            b_logout.setVisibility(View.VISIBLE);
            bGoHome.setVisibility(View.VISIBLE);
        }

        //SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        //SharedPreferences.Editor editor = pref.edit();
        //editor.putString("SaveID", mb_id);

        // 로그인 버튼 클릭시 실행된다.
        bLogin.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View v){
                                        final String id = etID.getText().toString();
                                        final String password = etPassword.getText().toString();

                                        // Response received from the server
                                        Response.Listener<String> responseListener = new Response.Listener<String>(){
                                            @Override
                                            public void onResponse(String response){
                                                try{
                                                    JSONObject jsonResponse = new JSONObject(response);
                                                    boolean success = jsonResponse.getBoolean("success");

                                                    if(success){
                                                        String name = jsonResponse.getString("mb_name");
                                //int age = jsonResponse.getInt("mb_age");

                                //Intent intent = new Intent(WebViewLogin.this, UserAreaActivity.class);
                                Intent intent = new Intent(WebViewLogin.this, Main.class);

                                // 뒤로가기 했을경우 다시 돌아오면 안됨
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                intent.putExtra("mb_name", name);
                                intent.putExtra("mb_id", id);
                                //intent.putExtra("mb_age",age);

                                // 로그인 정보 저장
                                SharedPreferences pref = getSharedPreferences("logInfo",MODE_PRIVATE);
                                SharedPreferences.Editor prefEditor = pref.edit();
                                prefEditor.putString("ID",id);
                                prefEditor.putString("PASSWORD", password);
                                prefEditor.commit();

                                Toast.makeText(WebViewLogin.this, "로그인 되었습니다.", Toast.LENGTH_LONG).show();

                                // 창 전환
                                WebViewLogin.this.startActivity(intent);
                                                        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(WebViewLogin.this);
                                builder.setMessage("아이디와 비밀번호를 확인해주세요.")
                                        .setNegativeButton("다시 시도",null)
                                        .create()
                                        .show();
                            }

                        } catch(JSONException e){
                            e.printStackTrace();
                        }

                    }
                };

                LoginRequest loginRequest = new LoginRequest(id, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(WebViewLogin.this);
                queue.add(loginRequest);
            }
        });


        // 메인으로 가는 버튼클릭시 실행된다.
        bGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WebViewLogin.this, Main.class);

                // 뒤로가기 했을경우 다시 돌아오면 안됨
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });

        // 로그아웃 버튼 클릭시 실행된다.

        b_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getSharedPreferences("logInfo",MODE_PRIVATE).edit();
                editor.clear(); //clear all stored data
                editor.commit();

                Toast.makeText(WebViewLogin.this, "로그아웃 되었습니다.", Toast.LENGTH_LONG).show();


                Intent intent = new Intent(WebViewLogin.this, Main.class);

                // 뒤로가기 했을경우 다시 돌아오면 안됨
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });
    }
}
