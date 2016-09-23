package com.example.leo.mainview.Gallery;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Add_Photo_Request extends StringRequest {
    private static final String  LOGIN_REQUEST_URL = "http://jym.dothome.co.kr/insertAddress.php";
    private Map<String, String> params;


    public Add_Photo_Request(String address, Response.Listener<String> listener){
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("address", address);
    }

    @Override
    public Map<String, String>getParams(){
        return params;
    }
}


