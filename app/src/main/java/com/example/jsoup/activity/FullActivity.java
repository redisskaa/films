package com.example.jsoup.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jsoup.R;
import com.squareup.picasso.Picasso;

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
        buttonView.setOnClickListener(v -> {
            Intent filmIntent = new Intent(FullActivity.this, FilmActivity.class);
            filmIntent.putExtra("url", url);
            startActivity(filmIntent);
        });

        Picasso.get().load(url_image).placeholder(R.drawable.image).into(imgViewFull);
        textView.setText(title);
        view_descr.setText(descr);
    }
}