package com.example.leo.mainview.Map;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.example.leo.mainview.Main;
import com.example.leo.mainview.R;
import com.example.leo.mainview.Route.FindRoute;
import com.example.leo.mainview.otherThings.AboutCityInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RouteMap extends AppCompatActivity {

    private GoogleMap map;
    ArrayList<String> getCityCheck;
    ArrayList<String> getCity1;
    ArrayList<String> getLat1;
    ArrayList<String> getLng1;
    ArrayList<String> getCity2;
    ArrayList<String> getLat2;
    ArrayList<String> getLng2;
    ArrayList<String> getCity3;
    ArrayList<String> getLat3;
    ArrayList<String> getLng3;
    ArrayList<String> tempCity;
    ArrayList<String> tempLat;
    ArrayList<String> tempLng;
    String WhatIsPath;
    String WhatIsLat;
    String WhatIsLng;
    int countCity;

    static final LatLng KOREA_CAMERA = new LatLng( 36.41, 127.96);

    /*
    Queue<String> Lat = new LinkedList<String>();
    Queue<String> Lng = new LinkedList<String>();
    Queue<String> tempPath = new LinkedList<String>();
    Queue<String> tempLat = new LinkedList<String>();
    Queue<String> tempLng = new LinkedList<String>();
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 액션바 지우기
        setContentView(R.layout.activity_route_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map = mapFragment.getMap();

        // 초기화면
        //현재 위치로 가는 버튼 표시
        map.setMyLocationEnabled(true);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(KOREA_CAMERA, 7));


        Intent intent = getIntent();

        getCity1 = (ArrayList<String>)getIntent().getSerializableExtra("city1");
        getLat1 = (ArrayList<String>)getIntent().getSerializableExtra("lat1");
        getLng1 = (ArrayList<String>)getIntent().getSerializableExtra("lng1");

        getCity2 = (ArrayList<String>)getIntent().getSerializableExtra("city2");
        getLat2 = (ArrayList<String>)getIntent().getSerializableExtra("lat2");
        getLng2 = (ArrayList<String>)getIntent().getSerializableExtra("lng2");

        getCity3 = (ArrayList<String>)getIntent().getSerializableExtra("city3");
        getLat3 = (ArrayList<String>)getIntent().getSerializableExtra("lat3");
        getLng3 = (ArrayList<String>)getIntent().getSerializableExtra("lng3");





        /*for(int i=0; i<getCity.size(); i++) {
            Toast.makeText(RouteMap.this, "d:" + getCity.get(i) , Toast.LENGTH_LONG).show();
        }*/

        onMapReady(map); //  사진 뿌리기!
    }

    /*
    // 뒤로가기를 눌렀을 때
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RouteMap.this, FindRoute.class);
        // 뒤로가기 했을경우 다시 돌아오면 안됨
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
*/
    // 사진을 지도상에 marker로 표시하는 부분.
    // stackoverflow(http://stackoverflow.com/questions/30317020/android-google-maps-sqlite) 참고


    public void onMapReady(GoogleMap map){
    //                Queue<String> RecommendPath = new LinkedList<String>();//입력된 출발지와 목적지에 대한 추천경로 저장
    //                Queue<String> RecommendLat = new LinkedList<String>(); //입력된 출발지와 목적지에 대한 추천경로의 위도
    //                Queue<String> RecommendLng = new LinkedList<String>(); //입력된 출발지와 목적지에 대한 추천경로의 경도

        countCity = 0;


        ArrayList<String> tempCity = new ArrayList<String>();
        ArrayList<String> tempLat = new ArrayList<String>();
        ArrayList<String> tempLng = new ArrayList<String>();



        float floatLat;
        float floatLng;



        if(getCity1 != null){
            for(int i = 0; i < getCity1.size(); i++){
                WhatIsPath = getCity1.get(i);
                WhatIsLat = getLat1.get(i);
                WhatIsLng = getLng1.get(i);

                tempCity.add(WhatIsPath);
                tempLat.add(WhatIsLat);
                tempLng.add(WhatIsLng);

                floatLat = Float.parseFloat(WhatIsLat);
                floatLng = Float.parseFloat(WhatIsLng);

                LatLng position_ = new LatLng(floatLat, floatLng);

                // 마커에 주소를 추가하기 위하여 => 주소를 추가하면 한국을 기준으로 하므로 해외는 에러가 발생
                String add = findAddress(floatLat,floatLng);
                String[] arr = add.split(" ");
                // 주소 추출 성공!

                // 본격적으로 마커 띄우기
                map.addMarker(new MarkerOptions()
                        .title(arr[0] + " " + arr[1]) // title = tag
                        .snippet("자세히")//한국에서만 쓸 수 있는 주소 띄우기
                                //.snippet(count + "번째")
                        .position(position_));
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }

        else if(getCity2 != null){
            for(int i = 0; i < getCity2.size(); i++){
                WhatIsPath = getCity2.get(i);
                WhatIsLat = getLat2.get(i);
                WhatIsLng = getLng2.get(i);

                tempCity.add(WhatIsPath);
                tempLat.add(WhatIsLat);
                tempLng.add(WhatIsLng);

                floatLat = Float.parseFloat(WhatIsLat);
                floatLng = Float.parseFloat(WhatIsLng);

                LatLng position_ = new LatLng(floatLat, floatLng);

                // 마커에 주소를 추가하기 위하여 => 주소를 추가하면 한국을 기준으로 하므로 해외는 에러가 발생
                String add = findAddress(floatLat,floatLng);
                String[] arr = add.split(" ");
                // 주소 추출 성공!

                // 본격적으로 마커 띄우기
                map.addMarker(new MarkerOptions()
                        .title(arr[0] + arr[1]) // title = tag
                        .snippet("순서 : " + countCity++)//한국에서만 쓸 수 있는 주소 띄우기
                                //.snippet(count + "번째")
                        .position(position_));
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }

        else if(getCity3 != null){
            for(int i = 0; i < getCity3.size(); i++){
                WhatIsPath = getCity3.get(i);
                WhatIsLat = getLat3.get(i);
                WhatIsLng = getLng3.get(i);

                tempCity.add(WhatIsPath);
                tempLat.add(WhatIsLat);
                tempLng.add(WhatIsLng);

                floatLat = Float.parseFloat(WhatIsLat);
                floatLng = Float.parseFloat(WhatIsLng);

                LatLng position_ = new LatLng(floatLat, floatLng);

                // 마커에 주소를 추가하기 위하여 => 주소를 추가하면 한국을 기준으로 하므로 해외는 에러가 발생
                String add = findAddress(floatLat,floatLng);
                String[] arr = add.split(" ");
                // 주소 추출 성공!

                // 본격적으로 마커 띄우기
                map.addMarker(new MarkerOptions()
                        .title(arr[0] + arr[1]) // title = tag
                        .snippet("자세히")//한국에서만 쓸 수 있는 주소 띄우기
                                //.snippet(count + "번째")
                        .position(position_));
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }


        // 경로를 순서대로 연결하기
        int count = 0;
        double second_lat= 0;
        double second_lon = 0;
        double first_lat = 0;
        double first_lon = 0;

        for(int i = 0; i < tempCity.size(); i++){

            WhatIsPath = tempCity.get(i);
            WhatIsLat = tempLat.get(i);
            WhatIsLng = tempLng.get(i);

            floatLat = Float.parseFloat(WhatIsLat);
            floatLng = Float.parseFloat(WhatIsLng);

            if(count == 1) {
                second_lat = floatLat;
                second_lon = floatLng;
            }
            else if(count == 0){
                first_lat = floatLat;
                first_lon = floatLng;
            }
            count++;

            if (count % 2 == 0) {
                Polyline line = map.addPolyline(new PolylineOptions()
                        .add(new LatLng(first_lat, first_lon), new LatLng(second_lat, second_lon))
                        .width(5)
                        .color(Color.BLUE)
                        .visible(true));

                count = 1;
                first_lat = second_lat;
                first_lon = second_lon;
            }
        }

        // 마커 클릭 이벤트
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            public boolean onMarkerClick(Marker marker) {
                /*
                String text = "[마커 클릭 이벤트] latitude ="
                        + marker.getPosition().latitude + ", longitude ="
                        + marker.getPosition().longitude;
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG)
                        .show();
                        */
                // 마커에 주소를 추가하기 위하여 => 주소를 추가하면 한국을 기준으로 하므로 해외는 에러가 발생
                String address = findAddress(marker.getPosition().latitude,marker.getPosition().longitude);
                String[] arr = address.split(" ");
                // 주소 추출 성공!


                Intent intent = new Intent(RouteMap.this, AboutCityInfo.class);
                intent.putExtra("address", arr[1]);

                startActivity(intent);

                return false;
            }
        });

    }







    // 위도, 경도 -> 주소
    private String findAddress(double lat, double lng) {
        StringBuffer bf = new StringBuffer();
        String currentLocationAddress;

        // 해외에서도 사용할 수 있도록 하기


        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                // 세번째 인수는 최대결과값인데 하나만 리턴받도록 설정했다
                address = geocoder.getFromLocation(lat, lng, 1);
                // 설정한 데이터로 주소가 리턴된 데이터가 있으면
                if (address != null && address.size() > 0) {
                    // 주소
                    currentLocationAddress = address.get(0).getAddressLine(0).toString();

                    // 전송할 주소 데이터 (위도/경도 포함 편집)
                    bf.append(currentLocationAddress).append("#");
                    bf.append(lat).append("#");
                    bf.append(lng);
                }
            }

        } catch (IOException e) {
            Toast.makeText(this, "위도와 경도가 설정되어있지 않습니다.", Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }
        return bf.toString();
    }

}
