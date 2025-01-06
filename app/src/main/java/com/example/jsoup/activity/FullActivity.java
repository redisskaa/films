package com.example.jsoup.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jsoup.R;
import com.squareup.picasso.Picasso;

import java.io.File;

public class FullActivity extends Activity {

    String url, title, url_image, descr;
    TextView textView, view_descr;
    ImageView imgViewFull;
    Button buttonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        getData();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void getData(){
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        url_image = intent.getStringExtra("url_image");
        descr = intent.getStringExtra("descr");
        buttonView = findViewById(R.id.view_film_button);
        textView = findViewById(R.id.view_title_full);
        imgViewFull = findViewById(R.id.view_image_full);
        view_descr = findViewById(R.id.view_descr);

//        ImageLoader imageLoader = new ImageLoader();
//        Bitmap image = imageLoader.loadImage("saved_image.jpg");
//        Bitmap resizedImage = imageLoader.resizeImage(image, 160, 260);
//
//        if (image != null) {
//            imgViewFull.setImageBitmap(resizedImage);
//        } else {
//            Toast.makeText(this, "Изображение не найдено!", Toast.LENGTH_SHORT).show();
//        }
        buttonView.setOnClickListener(v -> {
            Intent filmIntent = new Intent(FullActivity.this, FilmActivity.class);
            filmIntent.putExtra("url", url);
            startActivity(filmIntent);
        });

        Picasso.get().load(url_image).error(R.drawable.image).placeholder(R.drawable.image).into(imgViewFull);
        textView.setText(title);
        view_descr.setText(descr);
    }

    public static class ImageLoader {
        public Bitmap loadImage(String imageName) {

            File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(picturesDir, imageName);

            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                return bitmap;
            } else {
                // Обработка случая, когда файл не найден
                return null;
            }
        }

        public Bitmap resizeImage(Bitmap originalImage, int newWidth, int newHeight) {
            return Bitmap.createScaledBitmap(originalImage, newWidth, newHeight, true);
        }
    }
}