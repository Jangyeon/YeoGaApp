package com.example.leo.mainview.Map;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.leo.mainview.Database.DbOpenHelper;
import com.example.leo.mainview.Database.Travel;
import com.example.leo.mainview.Login.LoginRequest;
import com.example.leo.mainview.Main;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.leo.mainview.R;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapPhotoBook extends AppCompatActivity {

    static final LatLng SEOUL = new LatLng( 37.56, 126.97);
    private GoogleMap map;
    DbOpenHelper database;
    SQLiteDatabase mDb;
    SimpleCursorAdapter adapter;
    Button SearchButton;
    EditText SearchEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 액션바 지우기
        setContentView(R.layout.activity_map_photo_book);



        database = new DbOpenHelper(MapPhotoBook.this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map = mapFragment.getMap();

        //현재 위치로 가는 버튼 표시
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 17));//초기 위치...수정필요

        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {

                String msg = "lon: "+location.getLongitude()+" -- lat: "+location.getLatitude();
                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                drawMarker(location);

            }
        };

        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(getApplicationContext(), locationResult);
        onMapReady(map); //  사진 뿌리기!

        // 검색 기능
        SearchButton = (Button) findViewById(R.id.searchButton);
        SearchEdit = (EditText) findViewById(R.id.searchEdittext);

        // 검색 버튼 클릭시 실행된다.
        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTag();
            }
        });
    }


    private void drawMarker(Location location) {

        //기존 마커 지우기
        //map.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        //currentPosition 위치로 카메라 중심을 옮기고 화면 줌을 조정한다. 줌범위는 2~21, 숫자클수록 확대
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 17));
        map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

        //마커 추가
        map.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat:" + location.getLatitude() + "Lng:" + location.getLongitude())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("현재위치"));


    }


        // 사진을 지도상에 marker로 표시하는 부분.
        // stackoverflow(http://stackoverflow.com/questions/30317020/android-google-maps-sqlite) 참고

    public void onMapReady(GoogleMap map){

        Bitmap image;

        database = new DbOpenHelper(this);
        mDb = this.openOrCreateDatabase("Travel.db", MODE_WORLD_WRITEABLE, null);
        String sql ="select * from Travel";
        Cursor cursor = mDb.rawQuery(sql, null);
        Travel travel;



        int countPhoto = 1;
        cursor.moveToFirst();
        Log.d("Count", String.valueOf(cursor.getCount()));
        if(cursor.getCount() > 0) {
            do {
                cursor.getInt(0);
                LatLng position_ = new LatLng(cursor.getDouble(2), cursor.getDouble(3));

                StringBuffer outAddrStr = new StringBuffer();


                // 마커에 주소를 추가하기 위하여 => 주소를 추가하면 한국을 기준으로 하므로 해외는 에러가 발생
                String add = findAddress(cursor.getDouble(2),cursor.getDouble(3));
                String[] arr = add.split(" ");
                // 주소 추출 성공!

                //Toast.makeText(this, "이미지 경로 : " + cursor.getString(1), Toast.LENGTH_LONG).show();

                // 사진이 너무 커서 지도에 안띄워지는 것을 방지하기 위해, 사진 크기를 줄인다.
                int height = 100;
                int width = 100;
                // 경로에서 읽어들일 이미지의 크기 구하기 : http://ukzzang.tistory.com/62
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap src = BitmapFactory.decodeFile(cursor.getString(1), options);
                Bitmap resized = Bitmap.createScaledBitmap(src, height, width, true);
                // 사진 줄이기 성공!

                // 사진 회전시키기
                try {
                    //image = BitmapFactory.decodeFile(cursor.getString(1));

                    ExifInterface exif = new ExifInterface(cursor.getString(1));
                    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    resized = rotate(resized, exifDegree); // 변환된 bitmap image
                }catch(Exception e){}

                String count = String.valueOf(countPhoto++);

                // 본격적으로 사진 띄우기
                map.addMarker(new MarkerOptions()
                        .title(cursor.getString(5)) // title = tag
                        .snippet(arr[0] + " " + arr[1])//한국에서만 쓸 수 있는 주소 띄우기
                        //.snippet(count + "번째 사진")
                        .position(position_)
                        .icon(BitmapDescriptorFactory.fromBitmap(resized))
                        .anchor(0.5f, 0.5f));
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // 사진을 시간 순서대로 연결하기
        // 1. 시간 순서대로 sqlite를 정렬하기
        String order = "SELECT * FROM Travel order by _time asc";
        cursor = mDb.rawQuery(order, null);

        cursor.moveToFirst();
        Log.d("Count", String.valueOf(cursor.getCount()));

        int count = 0;
        double second_lat= 0;
        double second_lon = 0;
        double first_lat = 0;
        double first_lon = 0;

        if(cursor.getCount() > 0) {
            do {
                if(count == 1) {
                    second_lat = cursor.getDouble(2);
                    second_lon = cursor.getDouble(3);
                }
                else if(count == 0){
                    first_lat = cursor.getDouble(2);
                    first_lon = cursor.getDouble(3);
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
            } while (cursor.moveToNext());
        }
        cursor.close();
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

    // 이미지 회전
    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }


    public void searchTag(){
        String _searchTag = SearchEdit.getText().toString();
        double __lat;
        double __lon;
        Boolean found = false;

        database = new DbOpenHelper(this);
        mDb = this.openOrCreateDatabase("Travel.db", MODE_WORLD_WRITEABLE, null);
        String sql = "select * from Travel WHERE _tag = '" + _searchTag+"'";
        Cursor cursor = mDb.rawQuery(sql, null);
        Travel travel;

        Log.d("Count", String.valueOf(cursor.getCount()));

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                if(!found) {
                    __lat = cursor.getDouble(2);
                    __lon = cursor.getDouble(3);
                    LatLng FoundTag = new LatLng(__lat, __lon);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(FoundTag, 17));
                    map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
                    found = true;
                    break;
                }
            } while (cursor.moveToNext());
        }


        if(found == false) {
            sql = "select * from Travel WHERE _tag LIKE '%" + _searchTag + "%'";
            cursor = mDb.rawQuery(sql, null);
            Log.d("Count", String.valueOf(cursor.getCount()));

            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {

                    if (!found) {
                        __lat = cursor.getDouble(2);
                        __lon = cursor.getDouble(3);
                        LatLng FoundTag = new LatLng(__lat, __lon);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(FoundTag, 17));
                        map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
                        found = true;
                        break;
                    }

                }while (cursor.moveToNext()) ;
            }
        }


        if(found == false) {
            Toast.makeText(this, "해당하는 태그가 없습니다.", Toast.LENGTH_LONG).show();
        }
        cursor.close();
    }
}