package com.example.leo.mainview.Login;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;


import java.util.HashMap;
import java.util.Map;

/*
    Login을 서버에 요청
 */

public class LoginRequest extends StringRequest {
    private static final String  LOGIN_REQUEST_URL = "http://jym.dothome.co.kr/Login.php";
    private Map<String, String> params;

    public LoginRequest(String mb_id, String mb_password, Response.Listener<String> listener){
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("mb_id", mb_id);
        params.put("mb_password", mb_password);
    }

    @Override
    public Map<String, String>getParams(){
        return params;
    }
}
