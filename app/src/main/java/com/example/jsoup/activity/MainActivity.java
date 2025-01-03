package com.example.jsoup.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jsoup.R;
import com.example.jsoup.helpclass.CustomAdapter;
import com.example.jsoup.helpclass.FetchDataTask;
import com.example.jsoup.helpclass.RecyclerItemClickListener;
import com.example.jsoup.model.CardFilm;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private ArrayList<CardFilm> dataList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ProgressBar progressBar = findViewById(R.id.pBar);
        dataList = new ArrayList<>();
        CustomAdapter adapter = new CustomAdapter(this, dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getApplicationContext(), FullActivity.class);
                        String url_image = dataList.get(position).getUrlImage();
                        String title = dataList.get(position).getTitle();
                        String url = dataList.get(position).getUrl();
                        String descr = dataList.get(position).getDescr();

                        if (url_image.isEmpty()
                                | title.isEmpty()
                                | url.isEmpty()
                                | descr.isEmpty())
                        {
                            System.out.println(url_image + ":" + title + ":" + url + ":" + descr);
                            Toast.makeText(MainActivity.this, "Данные для работы приложения не были получены, попробуйте позже", Toast.LENGTH_SHORT).show();
                        }else {
                            intent.putExtra("url_image", url_image);
                            intent.putExtra("title", title);
                            intent.putExtra("url", url);
                            intent.putExtra("descr", descr);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        String url = "https://kinots.org/filmy/";
        new FetchDataTask(adapter, progressBar).execute(url);

    }
}