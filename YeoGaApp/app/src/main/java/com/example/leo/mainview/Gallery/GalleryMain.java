package com.example.leo.mainview.Gallery;
/*
    사용자가 메인에서 '사진 정리' 버튼을 눌렀을 경우 GalleryMain.java가 실행된다.
        [주요 기능]
        1. 갤러리에서 사진을 선택
        2. 선택한 사진을 안드로이드폰 내부에 있는 SQLite DB에 저장
        3. 선택한 사진을 PHP를 통하여 서버로 전송
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.leo.mainview.Database.DbOpenHelper;
import com.example.leo.mainview.Login.LoginRequest;
import com.example.leo.mainview.Main;
import com.example.leo.mainview.R;
import com.example.leo.mainview.Database.Travel;

import org.json.JSONException;
import org.json.JSONObject;


public class GalleryMain extends AppCompatActivity {

    private static final int REQUEST_CODE_LIBRARY = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private static final int REQUEST_CODE_CAMERA_FULL = 3;
    static final int NOT_SELECTED = -1;
    int listViewSelectedItemId = NOT_SELECTED;

    private ImageView imageView; // 레이아웃에서 정의한 이미지 뷰
    private Uri cameraPhotoUri; // 카메라에서 찍은 원본 이미지를 저장하고 읽는 데 사용할 파일 경로
    String Photo_name; // 사진 경로 저장

    TextView status;
    double lat;
    double lon;
    String tag;
    String time;
    ImageView blobImg;
    String Photo_path;

    Button createTableBtn;
    Button selectPhoto;
    EditText tableNameInput;
    DbOpenHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 액션바 지우기
        setContentView(R.layout.activity_gallery_main);

        // DB
        tableNameInput = (EditText) findViewById(R.id.tableNameInput);
        database = new DbOpenHelper(GalleryMain.this);

        // DB : 확인 버튼을 눌렀을 때 사진, 위도, 경도, 시간, 태그가 저장된다.
        createTableBtn = (Button) findViewById(R.id.createTableBtn);
        tableNameInput.setVisibility(View.INVISIBLE);
        createTableBtn.setVisibility(View.INVISIBLE);



        // 사진을 띄울 부분
        this.imageView = (ImageView) findViewById(R.id.imageView);
        selectPhoto = (Button) findViewById(R.id.selectPhoto);

        // 사진 선택 버튼
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE_LIBRARY);

                selectPhoto.setVisibility(View.INVISIBLE);
            }
        });

        // 메인으로 가는 버튼
        Button buttonGoToHome = (Button)findViewById(R.id.gotomain);
        buttonGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GalleryMain.this, Main.class);

                // 뒤로가기 했을경우 다시 돌아오면 안됨
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 라이브러리로부터 얻은 데이터
        if (requestCode == REQUEST_CODE_LIBRARY && resultCode == Activity.RESULT_OK) {
            try {
                Uri uri = data.getData();
                Log.d("Selected Image URI", uri.toString());
                InputStream imageStream = getContentResolver().openInputStream(uri);

                String realPath = getPathForMediaStoreImageURI(uri);
                Bitmap originalBitmap;
                Bitmap thumbBitmap;

                // 이미지 회전 방지
                try {
                    // 비트맵 이미지로 가져온다
                    Bitmap image = BitmapFactory.decodeFile(realPath);

                   // 이미지를 상황에 맞게 회전시킨다
                    ExifInterface exif = new ExifInterface(realPath);
                    int exifOrientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    image = rotate(image, exifDegree);

                    // 변환된 이미지 사용

                    imageView.setImageBitmap(image);
                    showExif(realPath, image);

                    } catch(Exception e) {
                    //Toast.makeText(this, "오류발생: " + e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

                //회전방지 끝


                //Uri에서 이미지 이름을 얻어온다.
                Photo_name = getImageNameToUri(data.getData());
                // Uri => 이미지 경로
                //String realPath = getRealImagePath(data.getData());

                //Photo_name.substring(0, Photo_name.length()-4);

                //Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                //imageView.setImageBitmap(bitmap);


                Log.d("Selected Image Path", realPath);
                //showExif(realPath, bitmap);
            } catch (FileNotFoundException e) { // openInputStream Exception
                e.printStackTrace();
            }
        }

        // 카메라 이미지 데이터 (썸네일)
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            // 이미지 회전 방지


            // 회전 방지 완료

            imageView.setImageBitmap(photo);
        }

        // 카메라 이미지 데이터 (원본)
        if (requestCode == REQUEST_CODE_CAMERA_FULL && resultCode == Activity.RESULT_OK) {

            String realPath = this.cameraPhotoUri.getPath();

            File tmpImageFile = new File(this.cameraPhotoUri.getPath());
            if (tmpImageFile.exists()) {
                Bitmap photo = BitmapFactory.decodeFile(this.cameraPhotoUri.getPath());


                // 이미지 회전 방지
                try {
                    // 비트맵 이미지로 가져온다
                    Bitmap image = BitmapFactory.decodeFile(realPath);

                    // 이미지를 상황에 맞게 회전시킨다
                    ExifInterface exif = new ExifInterface(realPath);
                    int exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    image = rotate(image, exifDegree);

                    // 변환된 이미지 사용

                    imageView.setImageBitmap(image);
                    showExif(this.cameraPhotoUri.getPath(), image);

                } catch(Exception e) {
                    //Toast.makeText(this, "오류발생: " + e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

                //회전방지 끝
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * ExifInterface.TAG_GPS_LATITUDE<br>
     * "num1/denom1,num2/denom2,num3/denom3"<br>
     * <br>
     * '37/1,30/1,25603/1000' to 37.3025603
     */
    double convertToDegree(String stringDMS) {
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        double D0 = Double.valueOf(stringD[0]);
        double D1 = Double.valueOf(stringD[1]);
        double d = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        double M0 = Double.valueOf(stringM[0]);
        double M1 = Double.valueOf(stringM[1]);
        double m = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        double S0 = Double.valueOf(stringS[0]);
        double S1 = Double.valueOf(stringS[1]);
        double s = S0 / S1;

        return d + (m / 60) + (s / 3600);
    };

    void showExif(String path, Bitmap photo) {

        try {
            ExifInterface exif = new ExifInterface(path);
            // 사진을 db에 저장하려 했지만 용량문제로 접어둔다.
            //picture_bitmap = bitmapToByteArray(photo); // 비트맵 -> 바이트



            double lat_ = convertToDegree(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
            double lon_ = convertToDegree(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
            lat = lat_;
            lon = lon_;
            Photo_path = path;
            String time_ = exif.getAttribute(ExifInterface.TAG_DATETIME);
            time = time_;
            String msg = String.format("촬영 일시: %s\n촬영 위치: %s, %s",
                    exif.getAttribute(ExifInterface.TAG_DATETIME),
                    new DecimalFormat("#.##").format(lat),
                    new DecimalFormat("#.##").format(lon));


           // dbManager.insert("INSERT INTO PICTURE_TABLE values( null" + ", '" + Photo_name + "', " + lat + ", " + lon + ", '" + time_ + "');");

            new AlertDialog.Builder(this)
                    .setTitle("EXIF 메타데이터")
                    .setMessage(msg)
                    .create().show();


            // 확인버튼 활성화
            tableNameInput.setVisibility(View.VISIBLE);
            createTableBtn.setVisibility(View.VISIBLE);


            createTableBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // 서버 DB 저장

                    String add = findAddress(lat, lon);
                    String[] arr = add.split(" ");

                    final String address = arr[1];

                    // Response received from the server
                    Response.Listener<String> responseListener = new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response){
                            try{
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if(success){
                                    Toast.makeText(GalleryMain.this, "서버에 사진이 저장 되었습니다.", Toast.LENGTH_LONG).show();

                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(GalleryMain.this);
                                    builder.setMessage("사진 정보를 확인해주세요.\nEXIF정보가 없으면 업로드되지 않습니다.")
                                            .setNegativeButton("다시 시도",null)
                                            .create()
                                            .show();
                                }

                            } catch(JSONException e){
                                e.printStackTrace();
                            }

                        }
                    };

                    Add_Photo_Request Add_Photo_Request = new Add_Photo_Request(address, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(GalleryMain.this);
                    queue.add(Add_Photo_Request);



                    // 안드로이드 폰 내부 DB에 저장
                    DbOpenHelper db = new DbOpenHelper(GalleryMain.this);

                    tag = tableNameInput.getText().toString();
                    Travel travel = new Travel();
                    travel._photo = Photo_path;
                    travel._lat = lat;
                    travel._lon = lon;
                    travel._time = time;
                    travel._tag = tag;

                    database.create(travel);



                    //int count = insertRecord(tableName, Photo_name, lat, lon, time);
                    // Toast.makeText(GalleryMain.this, count + " records inserted.", Toast.LENGTH_LONG).show();

                    // test mode로 넘어갑니다~~
                    //Intent intent = new Intent(GalleryMain.this, DatabaseTEST.class);



                    Intent intent = new Intent(GalleryMain.this, Main.class);

                    // 뒤로가기 했을경우 다시 돌아오면 안됨
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    Toast.makeText(GalleryMain.this, "사진이 지도상에 추가되었습니다.", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                }
            });

        } catch (IOException e) { // ExifInterface Exception
            e.printStackTrace();
        }
        catch(SQLiteException e){
            e.printStackTrace();
        }
    }

    String getPathForMediaStoreImageURI(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    // 이미지에서 이름을 추출하는 방법
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        return imgName;
    }

    // 이미지 데이터베이스 저장
    // 비트맵 -> 바이트
    public byte[] bitmapToByteArray( Bitmap bitmap ) {


        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
        //bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        //byte[] byteArray = stream.toByteArray() ;
        //return byteArray ;
    }

    // 사진의 절대경로 구하기
    // @param uriPath URI : URI 경로
    // @return String : 실제 파일 경로
    // 블로그(http://btd86.tistory.com/94) 참고
    public String getRealImagePath(Uri uriPath){
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uriPath, proj, null, null, null);
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String path = cursor.getString(index);
        path = path.substring(5);

        return path;
    }

    // 이미지를 저장하면 회전되는 현상 보정
        // EXIF 정보를 회전 각도로 변환
    public int exifOrientationToDegrees(int exifOrientation){
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        else {
            return 0;
        }
    }

        // 이미지 본격 회전
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