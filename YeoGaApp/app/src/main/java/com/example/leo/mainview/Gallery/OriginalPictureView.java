package com.example.leo.mainview.Gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.leo.mainview.R;

/**
 * Created by Leo on 2016-05-31.
 */
public class OriginalPictureView extends Activity {
    private ImageView imgView;
    private String filename;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pictureview);

        imgView = (ImageView) findViewById(R.id.imageView);
        processIntent();
    }

    private void processIntent() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        filename = extras.getString("filename");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        Bitmap bitmap = BitmapFactory.decodeFile(filename, options);

        imgView.setImageBitmap(bitmap);
    }
}
