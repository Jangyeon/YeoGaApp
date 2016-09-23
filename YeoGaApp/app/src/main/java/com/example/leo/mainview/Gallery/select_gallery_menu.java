package com.example.leo.mainview.Gallery;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.leo.mainview.Map.MapPhotoBook;
import com.example.leo.mainview.R;

/**
 * Created by Leo on 2016-06-10.
 */
public class select_gallery_menu extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 팝업창 액션바 지우기
        setContentView(R.layout.popup_gallery_select_menu);

        Button ToMapView = (Button)findViewById(R.id.ToMapView);
        Button ToSelectPicture = (Button)findViewById(R.id.ToSelectPicture);


        ToMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(select_gallery_menu.this, MapPhotoBook.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });

        ToSelectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(select_gallery_menu.this, GalleryMain.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            }
        });
    }
}
