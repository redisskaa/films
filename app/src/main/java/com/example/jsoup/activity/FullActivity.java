package com.example.jsoup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jsoup.R;
import com.example.jsoup.helpclass.CustomAdapter;
import com.example.jsoup.helpclass.FetchDataTask;
import com.example.jsoup.model.CardFilm;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FullActivity extends AppCompatActivity {

    String url;
    String title;
    String url_image;
    String descr;
    TextView textView, view_descr;
    ImageView imgViewFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_full);
        getData();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void getData(){
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        url_image = intent.getStringExtra("url_image");
        descr = intent.getStringExtra("descr");

        textView = findViewById(R.id.view_title_full);
        imgViewFull = findViewById(R.id.view_image_full);
        view_descr = findViewById(R.id.view_descr);
        Picasso.get().load(url_image).placeholder(R.drawable.image).into(imgViewFull);
        textView.setText(title);
        view_descr.setText(descr);
        List<CardFilm> list = new ArrayList<>();
        CustomAdapter adapter = new CustomAdapter(this, list);
        new FetchDataTask(adapter).execute(url);

        System.out.println(url);
        System.out.println(title);
        System.out.println(url_image);
        System.out.println(descr);
    }
}