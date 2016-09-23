package com.example.leo.mainview.Route;
/*
    경로 검색에서 사용된다.
 */

        import android.content.Intent;
        import android.location.Address;
        import android.location.Geocoder;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.view.Window;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.leo.mainview.Main;
        import com.example.leo.mainview.Map.RouteMap;
        import com.example.leo.mainview.R;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.LinkedList;
        import java.util.List;
        import java.util.Locale;
        import java.util.Queue;

public class FindRoute extends AppCompatActivity {

    RelativeLayout FoundLayout;
    EditText Dep;// 출발지 입력
    EditText Dst;// 도착지 입력
    Button search; // 검색버튼
    Button buttonGoToHome; // 메인으로 가는 버튼
    Button route1;
    Button route2;
    Button route3;
    TextView WeFound;

    Geocoder gc;
    String address1; String[] array1;
    String address2; String[] array2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 액션바 지우기
        setContentView(R.layout.activity_find_route);

        FoundLayout = (RelativeLayout) findViewById(R.id.foundLayout);
        Dep = (EditText) findViewById(R.id.et_departure); // 출발지 입력
        Dst = (EditText) findViewById(R.id.et_destination); // 목적지 입력

        // option을 선택하기 전 검색 결과는 가려놓는다.
        WeFound = (TextView) findViewById(R.id.howManyFound);
        route1 = (Button) findViewById(R.id.FoundButton1);
        route2 = (Button) findViewById(R.id.FoundButton2);
        route3 = (Button) findViewById(R.id.FoundButton3);
        WeFound.setVisibility(View.INVISIBLE);
        route1.setVisibility(View.INVISIBLE);
        route2.setVisibility(View.INVISIBLE);
        route3.setVisibility(View.INVISIBLE);


        // 검색버튼
        search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener(){
            String myJSON;
            String strPoint;//출발지
            String desPoint;//목적지
            String strlat, strlng, deslat, deslng;//출발지와 목적지의 위도, 경도
            String tempLat, tempLng;//출발지 저장(고정)
            String prepcity, preplat, preplng, prepnum;//비교할 경로와 위도와 경도와 사진장수

            private static final String TAG_RESULTS="result";
            private static final String TAG_ID = "id";
            private static final String TAG_CITY = "city";
            private static final String TAG_LAT ="lat";
            private static final String TAG_LNG ="lng";
            private static final String TAG_NUM ="num";

            JSONArray allCitys = null;

            ArrayList<HashMap<String, String>> cityList; //db에서 가져온 시도 list

            //첫 번째 추천경로
            Queue<String> RecommendPath1 = new LinkedList<String>();//입력된 출발지와 목적지에 대한 추천경로 저장
            Queue<String> RecommendLat1 = new LinkedList<String>(); //입력된 출발지와 목적지에 대한 추천경로의 위도
            Queue<String> RecommendLng1 = new LinkedList<String>(); //입력된 출발지와 목적지에 대한 추천경로의 경도

            ArrayList<String> RecommendPath_Map1 = new ArrayList<String>();
            ArrayList<String> RecommendLat_Map1 = new ArrayList<String>();
            ArrayList<String> RecommendLng_Map1 = new ArrayList<String>();

            //두 번째 추천경로
            Queue<String> RecommendPath2 = new LinkedList<String>();//입력된 출발지와 목적지에 대한 추천경로 저장
            Queue<String> RecommendLat2 = new LinkedList<String>(); //입력된 출발지와 목적지에 대한 추천경로의 위도
            Queue<String> RecommendLng2 = new LinkedList<String>(); //입력된 출발지와 목적지에 대한 추천경로의 경도

            ArrayList<String> RecommendPath_Map2 = new ArrayList<String>();
            ArrayList<String> RecommendLat_Map2 = new ArrayList<String>();
            ArrayList<String> RecommendLng_Map2 = new ArrayList<String>();

            //세 번째 추천경로
            Queue<String> RecommendPath3 = new LinkedList<String>();//입력된 출발지와 목적지에 대한 추천경로 저장
            Queue<String> RecommendLat3 = new LinkedList<String>(); //입력된 출발지와 목적지에 대한 추천경로의 위도
            Queue<String> RecommendLng3 = new LinkedList<String>(); //입력된 출발지와 목적지에 대한 추천경로의 경도

            ArrayList<String> RecommendPath_Map3 = new ArrayList<String>();
            ArrayList<String> RecommendLat_Map3 = new ArrayList<String>();
            ArrayList<String> RecommendLng_Map3 = new ArrayList<String>();


            ArrayList exceptPath = new ArrayList(); // 한번 추천해준 경로는 넣어주어 다시 추천하지 않도록한다.

            @Override
            public void onClick(View v){
                final String str = Dep.getText().toString();
                final String des = Dst.getText().toString();
                List<Address> addressList1 = null;// 출발지의 주소
                List<Address> addressList2 = null;// 목적지의 주소

                try {
                    addressList1 = gc.getFromLocationName(str, 3);

                    if (addressList1 != null) {
                        for (int i = 0; i < addressList1.size(); i++) {
                            Address outAddr1 = addressList1.get(i);
                            int addrCount = outAddr1.getMaxAddressLineIndex() + 1;
                            StringBuffer outAddrStr1 = new StringBuffer();
                            for (int k = 0; k < addrCount; k++) {
                                outAddrStr1.append(outAddr1.getAddressLine(k));
                            }
                            //주소에서 특별시 또는 도 추출하기
                            address1 = outAddrStr1.toString();
                            array1 = address1.split(" ");
                            strPoint = array1[1];
                        }
                    }

                    addressList2 = gc.getFromLocationName(des, 3);

                    if (addressList2 != null) {
                        for (int i = 0; i < addressList2.size(); i++) {
                            Address outAddr2 = addressList2.get(i);
                            int addrCount = outAddr2.getMaxAddressLineIndex() + 1;
                            StringBuffer outAddrStr2 = new StringBuffer();
                            for (int k = 0; k < addrCount; k++) {
                                outAddrStr2.append(outAddr2.getAddressLine(k));
                            }
                            //주소에서 특별시 또는 도 추출하기
                            address2 = outAddrStr2.toString();
                            array2 = address2.split(" ");
                            desPoint = array2[1];
                        }
                    }

                    //입력된 값이 있으면 실행
                    if(array1 != null && array2 != null){
                        cityList = new ArrayList<HashMap<String,String>>();
                        getData("http://jym.dothome.co.kr/FindRoute.php");
                    }
                    //입력된 값이 없으면 실행
                    else{
                        if(array1 == null || array2 == null)
                            Toast.makeText(FindRoute.this, "출발지와 목적지를 입력해주세요." , Toast.LENGTH_LONG).show();
                    }
                } catch(IOException ex) {
                    Log.d("지오코딩", "예외 : " + ex.toString());
                }
            }

            protected void showList(){
                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    allCitys = jsonObj.getJSONArray(TAG_RESULTS);
                    RecommendPath1.clear(); RecommendPath2.clear(); RecommendPath3.clear();
                    RecommendLat1.clear(); RecommendLat2.clear(); RecommendLat3.clear();
                    RecommendLng1.clear(); RecommendLng2.clear(); RecommendLng3.clear();
                    RecommendPath_Map1.clear(); RecommendPath_Map2.clear(); RecommendPath_Map3.clear();
                    RecommendLat_Map1.clear(); RecommendLat_Map2.clear(); RecommendLat_Map3.clear();
                    RecommendLng_Map1.clear(); RecommendLng_Map2.clear(); RecommendLng_Map3.clear();
                    exceptPath.clear();



                    //디비에서 가져온 정보를 리스트에 저장하고 입력한 출발지와 목적지의 위도 경도를 저장한다.
                    for(int i=0;i<allCitys.length();i++){
                        JSONObject c = allCitys.getJSONObject(i);
                        String id = c.getString(TAG_ID);
                        String city = c.getString(TAG_CITY);
                        String lat = c.getString(TAG_LAT);
                        String lng = c.getString(TAG_LNG);
                        String num = c.getString(TAG_NUM);

                        if(strPoint.equals(city)) {//입력한 출발지와 같으면 위도와 경도를 가지고온다.
                            strlat = lat; tempLat = lat;
                            strlng = lng; tempLng = lng;
                        }

                        if(desPoint.equals(city)){//입력한 목적지와 같으면 위도와 경도를 가지고온다.
                            deslat = lat;
                            deslng = lng;
                        }

                        //HashMap에 서버에서 가져온 값을 각 타입에 맞게 저장하고 다시 list에 저장한다.
                        HashMap<String,String> citys = new HashMap<String,String>();

                        citys.put(TAG_ID,id);
                        citys.put(TAG_CITY,city);
                        citys.put(TAG_LAT,lat);
                        citys.put(TAG_LNG,lng);
                        citys.put(TAG_NUM, num);

                        cityList.add(citys);
                    }

                    //추천경로에 출발지를 담는다.
                    //첫 번째 추천경로
                    RecommendPath1.add(strPoint);// 출발지의 이름
                    RecommendLat1.add(strlat);// 출발지의 위도
                    RecommendLng1.add(strlng);// 출발지의 경도
                    //두 번째 추천경로
                    RecommendPath2.add(strPoint);// 출발지의 이름
                    RecommendLat2.add(strlat);// 출발지의 위도
                    RecommendLng2.add(strlng);// 출발지의 경도
                    //세 번째 추천경로
                    RecommendPath3.add(strPoint);// 출발지의 이름
                    RecommendLat3.add(strlat);// 출발지의 위도
                    RecommendLng3.add(strlng);// 출발지의 경도

                    float strlatF = Float.parseFloat(strlat);
                    float strlngF = Float.parseFloat(strlng);
                    float deslatF = Float.parseFloat(deslat);
                    float deslngF = Float.parseFloat(deslng);

                    int checkRoot = 0;

                    if(strlatF >= deslatF && strlngF >= deslngF)//출발지가 오른편 위쪽 목적지가 왼편 아래쪽(↙)
                        checkRoot = 1;
                    else if(strlatF >= deslatF && strlngF <= deslngF)//출발지가 왼편 위쪽 목적지가 오른편 아래쪽(↘)
                        checkRoot = 2;
                    else if(strlatF <= deslatF && strlngF >= deslngF)//출발지가 오른편 아래쪽 목적지가 왼편 위쪽(↖)
                        checkRoot = 3;
                    else if(strlatF <= deslatF && strlngF <= deslngF)//출발지가 왼편 아래쪽 목적지가 오른편 위쪽(↗)
                        checkRoot = 4;

                    int maxNum = 0;
                    String maxName = null, maxLat = null, maxLng = null;
                    int count = 0;

                    while(count != 3) {
                        maxNum = 0; // 영역안에 있는 경로중 가장 높은 num을 가지고 있는 경로 확인
                        maxName = null; maxLat = null; maxLng = null;
                        strlatF = Float.parseFloat(strlat); strlngF = Float.parseFloat(strlng);

                        switch (checkRoot) {
                            case 1:
                                for (int i = 0; i < cityList.size(); i++) {
                                    HashMap<String, String> takeMap = (HashMap<String, String>) cityList.get(i);
                                    prepcity = takeMap.get(TAG_CITY);
                                    preplat = takeMap.get(TAG_LAT);
                                    preplng = takeMap.get(TAG_LNG);
                                    prepnum = takeMap.get(TAG_NUM);

                                    float preplatF = Float.parseFloat(preplat);
                                    float preplngF = Float.parseFloat(preplng);
                                    int prepnumI = Integer.parseInt(prepnum);

                                    if ((deslatF < preplatF && strlatF > preplatF)
                                            && (deslngF < preplngF && strlngF > preplngF)) {// 영역안에 들어오면
                                        if (maxNum < prepnumI && !exceptPath.contains(prepcity)) { // 비교대상의 num이 더 크면 maxNum값을 바꾼다.
                                            maxNum = prepnumI;
                                            maxName = prepcity;
                                            maxLat = preplat;
                                            maxLng = preplng;
                                        }
                                    }
                                }
                                if (maxLat != null && maxLng != null) {
                                    //중간경로지가 존재한다.
                                    if(count == exceptPath.size())
                                        exceptPath.add(maxName);// 다시 선택되지 않도록 저장해둔다.
                                    strlat = maxLat;// 선택된 중간경로지의 위도로 출발지를 바꾼다.
                                    strlng = maxLng;// 선택된 중간경로지의 경도로 출발지를 바꾼다.

                                    if(count == 0) {
                                        RecommendPath1.add(maxName);// 중간경로의 이름
                                        RecommendLat1.add(maxLat);// 중간경로의 위도
                                        RecommendLng1.add(maxLng);// 중간경로의 경도
                                    }
                                    else if(count == 1){
                                        RecommendPath2.add(maxName);// 중간경로의 이름
                                        RecommendLat2.add(maxLat);// 중간경로의 위도
                                        RecommendLng2.add(maxLng);// 중간경로의 경도
                                    }
                                    else if(count == 2){
                                        RecommendPath3.add(maxName);// 중간경로의 이름
                                        RecommendLat3.add(maxLat);// 중간경로의 위도
                                        RecommendLng3.add(maxLng);// 중간경로의 경도
                                    }
                                }
                                else{
                                    //중간경로지가 존재하지 않는다.
                                    count++;
                                    if(count == 1) {
                                        RecommendPath1.add(desPoint);// 목적지의 이름
                                        RecommendLat1.add(deslat);// 목적지의 위도
                                        RecommendLng1.add(deslng);// 목적지의 경도
                                        strlat = tempLat; strlng = tempLng; // 추가적인 추천경로를 찾기위해 시작점을 원래자리로 돌려준다.
                                    }
                                    else if(count == 2) {
                                        RecommendPath2.add(desPoint);// 목적지의 이름
                                        RecommendLat2.add(deslat);// 목적지의 위도
                                        RecommendLng2.add(deslng);// 목적지의 경도
                                        strlat = tempLat; strlng = tempLng; // 추가적인 추천경로를 찾기위해 시작점을 원래자리로 돌려준다.
                                    }
                                    else if(count == 3){
                                        RecommendPath3.add(desPoint);// 목적지의 이름
                                        RecommendLat3.add(deslat);// 목적지의 위도
                                        RecommendLng3.add(deslng);// 목적지의 경도
                                        strlat = tempLat; strlng = tempLng; // 추가적인 추천경로를 찾기위해 시작점을 원래자리로 돌려준다.
                                    }
                                }
                                break;


                            case 2:
                                for (int i = 0; i < cityList.size(); i++) {
                                    HashMap<String, String> takeMap = (HashMap<String, String>) cityList.get(i);
                                    prepcity = takeMap.get(TAG_CITY);
                                    preplat = takeMap.get(TAG_LAT);
                                    preplng = takeMap.get(TAG_LNG);
                                    prepnum = takeMap.get(TAG_NUM);

                                    float preplatF = Float.parseFloat(preplat);
                                    float preplngF = Float.parseFloat(preplng);
                                    int prepnumI = Integer.parseInt(prepnum);

                                    if ((deslatF < preplatF && strlatF > preplatF)
                                            && (strlngF < preplngF && deslngF > preplngF)) {// 영역안에 들어오면
                                        if (maxNum < prepnumI && !exceptPath.contains(prepcity)) { // 비교대상의 num이 더 크면 maxNum값을 바꾼다.
                                            maxNum = prepnumI;
                                            maxName = prepcity;
                                            maxLat = preplat;
                                            maxLng = preplng;
                                        }
                                    }
                                }
                                if (maxLat != null && maxLng != null) {
                                    //중간경로지가 존재한다.
                                    if(count == exceptPath.size())
                                        exceptPath.add(maxName);// 다시 선택되지 않도록 저장해둔다.
                                    strlat = maxLat;// 선택된 중간경로지의 위도로 출발지를 바꾼다.
                                    strlng = maxLng;// 선택된 중간경로지의 경도로 출발지를 바꾼다.

                                    if(count == 0) {
                                        RecommendPath1.add(maxName);// 중간경로의 이름
                                        RecommendLat1.add(maxLat);// 중간경로의 위도
                                        RecommendLng1.add(maxLng);// 중간경로의 경도
                                    }
                                    else if(count == 1){
                                        RecommendPath2.add(maxName);// 중간경로의 이름
                                        RecommendLat2.add(maxLat);// 중간경로의 위도
                                        RecommendLng2.add(maxLng);// 중간경로의 경도
                                    }
                                    else if(count == 2){
                                        RecommendPath3.add(maxName);// 중간경로의 이름
                                        RecommendLat3.add(maxLat);// 중간경로의 위도
                                        RecommendLng3.add(maxLng);// 중간경로의 경도
                                    }
                                }
                                else{
                                    //중간경로지가 존재하지 않는다.
                                    count++;
                                    if(count == 1) {
                                        RecommendPath1.add(desPoint);// 목적지의 이름
                                        RecommendLat1.add(deslat);// 목적지의 위도
                                        RecommendLng1.add(deslng);// 목적지의 경도
                                        strlat = tempLat; strlng = tempLng; // 추가적인 추천경로를 찾기위해 시작점을 원래자리로 돌려준다.
                                    }
                                    else if(count == 2) {
                                        RecommendPath2.add(desPoint);// 목적지의 이름
                                        RecommendLat2.add(deslat);// 목적지의 위도
                                        RecommendLng2.add(deslng);// 목적지의 경도
                                        strlat = tempLat; strlng = tempLng; // 추가적인 추천경로를 찾기위해 시작점을 원래자리로 돌려준다.
                                    }
                                    else if(count == 3){
                                        RecommendPath3.add(desPoint);// 목적지의 이름
                                        RecommendLat3.add(deslat);// 목적지의 위도
                                        RecommendLng3.add(deslng);// 목적지의 경도
                                        strlat = tempLat; strlng = tempLng; // 추가적인 추천경로를 찾기위해 시작점을 원래자리로 돌려준다.
                                    }
                                }
                                break;
                            case 3:
                                for (int i = 0; i < cityList.size(); i++) {
                                    HashMap<String, String> takeMap = (HashMap<String, String>) cityList.get(i);
                                    prepcity = takeMap.get(TAG_CITY);
                                    preplat = takeMap.get(TAG_LAT);
                                    preplng = takeMap.get(TAG_LNG);
                                    prepnum = takeMap.get(TAG_NUM);

                                    float preplatF = Float.parseFloat(preplat);
                                    float preplngF = Float.parseFloat(preplng);
                                    int prepnumI = Integer.parseInt(prepnum);

                                    if ((strlatF < preplatF && deslatF > preplatF)
                                            && (deslngF < preplngF && strlngF > preplngF)) {// 영역안에 들어오면
                                        if (maxNum < prepnumI && !exceptPath.contains(prepcity)) { // 비교대상의 num이 더 크면 maxNum값을 바꾼다.
                                            maxNum = prepnumI;
                                            maxName = prepcity;
                                            maxLat = preplat;
                                            maxLng = preplng;
                                        }
                                    }
                                }
                                if (maxLat != null && maxLng != null) {
                                    //중간경로지가 존재한다.
                                    if(count == exceptPath.size())
                                        exceptPath.add(maxName);// 다시 선택되지 않도록 저장해둔다.
                                    strlat = maxLat;// 선택된 중간경로지의 위도로 출발지를 바꾼다.
                                    strlng = maxLng;// 선택된 중간경로지의 경도로 출발지를 바꾼다.

                                    if(count == 0) {
                                        RecommendPath1.add(maxName);// 중간경로의 이름
                                        RecommendLat1.add(maxLat);// 중간경로의 위도
                                        RecommendLng1.add(maxLng);// 중간경로의 경도
                                    }
                                    else if(count == 1){
                                        RecommendPath2.add(maxName);// 중간경로의 이름
                                        RecommendLat2.add(maxLat);// 중간경로의 위도
                                        RecommendLng2.add(maxLng);// 중간경로의 경도
                                    }
                                    else if(count == 2){
                                        RecommendPath3.add(maxName);// 중간경로의 이름
                                        RecommendLat3.add(maxLat);// 중간경로의 위도
                                        RecommendLng3.add(maxLng);// 중간경로의 경도
                                    }
                                }
                                else{
                                    //중간경로지가 존재하지 않는다.
                                    count++;
                                    if(count == 1) {
                                        RecommendPath1.add(desPoint);// 목적지의 이름
                                        RecommendLat1.add(deslat);// 목적지의 위도
                                        RecommendLng1.add(deslng);// 목적지의 경도
                                        strlat = tempLat; strlng = tempLng; // 추가적인 추천경로를 찾기위해 시작점을 원래자리로 돌려준다.
                                    }
                                    else if(count == 2) {
                                        RecommendPath2.add(desPoint);// 목적지의 이름
                                        RecommendLat2.add(deslat);// 목적지의 위도
                                        RecommendLng2.add(deslng);// 목적지의 경도
                                        strlat = tempLat; strlng = tempLng; // 추가적인 추천경로를 찾기위해 시작점을 원래자리로 돌려준다.
                                    }
                                    else if(count == 3){
                                        RecommendPath3.add(desPoint);// 목적지의 이름
                                        RecommendLat3.add(deslat);// 목적지의 위도
                                        RecommendLng3.add(deslng);// 목적지의 경도
                                        strlat = tempLat; strlng = tempLng; // 추가적인 추천경로를 찾기위해 시작점을 원래자리로 돌려준다.
                                    }
                                }
                                break;
                            case 4:
                                for (int i = 0; i < cityList.size(); i++) {
                                    HashMap<String, String> takeMap = (HashMap<String, String>) cityList.get(i);
                                    prepcity = takeMap.get(TAG_CITY);
                                    preplat = takeMap.get(TAG_LAT);
                                    preplng = takeMap.get(TAG_LNG);
                                    prepnum = takeMap.get(TAG_NUM);

                                    float preplatF = Float.parseFloat(preplat);
                                    float preplngF = Float.parseFloat(preplng);
                                    int prepnumI = Integer.parseInt(prepnum);

                                    if ((strlatF < preplatF && deslatF > preplatF)
                                            && (strlngF < preplngF && deslngF > preplngF)) {// 영역안에 들어오면
                                        if (maxNum < prepnumI && !exceptPath.contains(prepcity)) { // 비교대상의 num이 더 크면 maxNum값을 바꾼다.
                                            maxNum = prepnumI;
                                            maxName = prepcity;
                                            maxLat = preplat;
                                            maxLng = preplng;
                                        }
                                    }
                                }
                                if (maxLat != null && maxLng != null) {
                                    //중간경로지가 존재한다.
                                    if(count == exceptPath.size())
                                        exceptPath.add(maxName);// 다시 선택되지 않도록 저장해둔다.
                                    strlat = maxLat;// 선택된 중간경로지의 위도로 출발지를 바꾼다.
                                    strlng = maxLng;// 선택된 중간경로지의 경도로 출발지를 바꾼다.

                                    if(count == 0) {
                                        RecommendPath1.add(maxName);// 중간경로의 이름
                                        RecommendLat1.add(maxLat);// 중간경로의 위도
                                        RecommendLng1.add(maxLng);// 중간경로의 경도
                                    }
                                    else if(count == 1){
                                        RecommendPath2.add(maxName);// 중간경로의 이름
                                        RecommendLat2.add(maxLat);// 중간경로의 위도
                                        RecommendLng2.add(maxLng);// 중간경로의 경도
                                    }
                                    else if(count == 2){
                                        RecommendPath3.add(maxName);// 중간경로의 이름
                                        RecommendLat3.add(maxLat);// 중간경로의 위도
                                        RecommendLng3.add(maxLng);// 중간경로의 경도
                                    }
                                }
                                else{
                                    //중간경로지가 존재하지 않는다.

                                    count++;
                                    if(count == 1) {
                                        RecommendPath1.add(desPoint);// 목적지의 이름
                                        RecommendLat1.add(deslat);// 목적지의 위도
                                        RecommendLng1.add(deslng);// 목적지의 경도
                                        strlat = tempLat; strlng = tempLng; // 추가적인 추천경로를 찾기위해 시작점을 원래자리로 돌려준다.
                                    }
                                    else if(count == 2) {
                                        RecommendPath2.add(desPoint);// 목적지의 이름
                                        RecommendLat2.add(deslat);// 목적지의 위도
                                        RecommendLng2.add(deslng);// 목적지의 경도
                                        strlat = tempLat; strlng = tempLng; // 추가적인 추천경로를 찾기위해 시작점을 원래자리로 돌려준다.
                                    }
                                    else if(count == 3){
                                        RecommendPath3.add(desPoint);// 목적지의 이름
                                        RecommendLat3.add(deslat);// 목적지의 위도
                                        RecommendLng3.add(deslng);// 목적지의 경도
                                        strlat = tempLat; strlng = tempLng; // 추가적인 추천경로를 찾기위해 시작점을 원래자리로 돌려준다.
                                    }
                                }
                                break;
                        }
                    }

                    if(RecommendPath1.equals(RecommendPath2) && RecommendPath1.equals(RecommendPath3)) {
                        WeFound.setVisibility(View.VISIBLE);
                        WeFound.setText("1개의 경로를 찾았습니다!");
                        route1.setVisibility(View.VISIBLE);
                        route2.setVisibility(View.INVISIBLE);
                        route3.setVisibility(View.INVISIBLE);
                    }
                    else if(!RecommendPath1.equals(RecommendPath2) && RecommendPath3.size() == 2) {
                        WeFound.setVisibility(View.VISIBLE);
                        WeFound.setText("2개의 경로를 찾았습니다!");
                        route1.setVisibility(View.VISIBLE);
                        route2.setVisibility(View.VISIBLE);
                        route3.setVisibility(View.INVISIBLE);
                    }
                    else{
                        WeFound.setVisibility(View.VISIBLE);
                        WeFound.setText("3개의 경로를 찾았습니다!");
                        route1.setVisibility(View.VISIBLE);
                        route2.setVisibility(View.VISIBLE);
                        route3.setVisibility(View.VISIBLE);
                    }



                    //-----------------------------------------------------------------(1)
                    while(!RecommendPath1.isEmpty()){
                        RecommendPath_Map1.add(RecommendPath1.remove());
                    }
                    while(!RecommendLat1.isEmpty()){
                        RecommendLat_Map1.add(RecommendLat1.remove());
                    }
                    while(!RecommendLng1.isEmpty()){
                        RecommendLng_Map1.add(RecommendLng1.remove());
                    }
                    //-----------------------------------------------------------------(2)
                    while(!RecommendPath2.isEmpty()){
                        RecommendPath_Map2.add(RecommendPath2.remove());
                    }
                    while(!RecommendLat2.isEmpty()){
                        RecommendLat_Map2.add(RecommendLat2.remove());
                    }
                    while(!RecommendLng2.isEmpty()){
                        RecommendLng_Map2.add(RecommendLng2.remove());
                    }
                    //-----------------------------------------------------------------(3)
                    while(!RecommendPath3.isEmpty()){
                        RecommendPath_Map3.add(RecommendPath3.remove());
                    }
                    while(!RecommendLat3.isEmpty()){
                        RecommendLat_Map3.add(RecommendLat3.remove());
                    }
                    while(!RecommendLng3.isEmpty()){
                        RecommendLng_Map3.add(RecommendLng3.remove());
                    }

                    route1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(FindRoute.this, RouteMap.class);
                            intent.putExtra("city1", RecommendPath_Map1);
                            intent.putExtra("lat1", RecommendLat_Map1);
                            intent.putExtra("lng1", RecommendLng_Map1);
                            // 뒤로가기 했을경우 다시 돌아오면 안됨
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });

                    route2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(FindRoute.this, RouteMap.class);
                            intent.putExtra("city2", RecommendPath_Map2);
                            intent.putExtra("lat2", RecommendLat_Map2);
                            intent.putExtra("lng2", RecommendLng_Map2);
                            // 뒤로가기 했을경우 다시 돌아오면 안됨
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });

                    route3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(FindRoute.this, RouteMap.class);
                            intent.putExtra("city3", RecommendPath_Map3);
                            intent.putExtra("lat3", RecommendLat_Map3);
                            intent.putExtra("lng3", RecommendLng_Map3);
                            // 뒤로가기 했을경우 다시 돌아오면 안됨
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });


/*
                    Intent intent = new Intent(FindRoute.this, RouteMap.class);
                    intent.putExtra("city1", RecommendPath_Map1);
                    intent.putExtra("lat1", RecommendLat_Map1);
                    intent.putExtra("lng1", RecommendLng_Map1);
                    intent.putExtra("city2", RecommendPath_Map2);
                    intent.putExtra("lat2", RecommendLat_Map2);
                    intent.putExtra("lng2", RecommendLng_Map2);
                    intent.putExtra("city3", RecommendPath_Map3);
                    intent.putExtra("lat3", RecommendLat_Map3);
                    intent.putExtra("lng3", RecommendLng_Map3);
                    startActivity(intent);
*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //서버에서 원하는 값을 가져온다.
            public void getData(String url){
                class GetDataJSON extends AsyncTask<String, Void, String> {

                    @Override
                    protected String doInBackground(String... params) {

                        String uri = params[0];

                        BufferedReader bufferedReader = null;
                        try {
                            URL url = new URL(uri);
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            StringBuilder sb = new StringBuilder();

                            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                            String json;
                            while((json = bufferedReader.readLine())!= null){
                                sb.append(json+"\n");
                            }

                            return sb.toString().trim();

                        }catch(Exception e){
                            return null;
                        }
                    }

                    //가져온 데이터를 원하는 방식으로 처리한다.
                    @Override
                    protected void onPostExecute(String result){
                        myJSON=result;
                        showList();
                    }
                }
                GetDataJSON g = new GetDataJSON();
                g.execute(url);
            }



            //====

        });


        // 메인으로 가는 버튼
        buttonGoToHome = (Button)findViewById(R.id.gotomain);
        buttonGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FindRoute.this,Main.class);

                // 뒤로가기 했을경우 다시 돌아오면 안됨
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });

        // 지오코더 객체 생성
        gc = new Geocoder(this, Locale.KOREAN);
    }

    // 주소를 이용해 위치 좌표를 찾는다.
    private void searchLocation(String searchStr) {
        // 결과값이 들어갈 리스트 선언
        List<Address> addressList = null;
        try {
            addressList = gc.getFromLocationName(searchStr, 3);

            if (addressList != null) {
                //Toast.makeText(FindRoute.this,"\nCount of Addresses for [" + searchStr + "] : " + addressList.size(),Toast.LENGTH_LONG).show();
                for (int i = 0; i < addressList.size(); i++) {
                    Address outAddr = addressList.get(i);
                    int addrCount = outAddr.getMaxAddressLineIndex() + 1;
                    StringBuffer outAddrStr = new StringBuffer();
                    for (int k = 0; k < addrCount; k++) {
                        outAddrStr.append(outAddr.getAddressLine(k));
                    }
                    outAddrStr.append("\n\tLatitude : " + outAddr.getLatitude());
                    outAddrStr.append("\n\tLongitude : " + outAddr.getLongitude());

                    //주소에서 특별시 또는 도 추출하기
                    String add = outAddrStr.toString();
                    String[] arr = add.split(" ");
                    // arr[1]이 우리가 원하는 시도 정보이다.
                    Toast.makeText(FindRoute.this, "\n\tAddress #" + i + " : " + arr[1], Toast.LENGTH_LONG).show();
                }
            }

        } catch(IOException ex) {
            Log.d("지오코딩", "예외 : " + ex.toString());
        }
    }
}