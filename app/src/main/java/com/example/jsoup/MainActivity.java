package com.example.jsoup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jsoup.helpclass.RecyclerItemClickListener;
import com.example.jsoup.model.CardFilm;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String url = "https://kinots.org/filmy/";
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private ArrayList<CardFilm> dataList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        dataList = new ArrayList<>();
        adapter = new CustomAdapter(this, dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getApplicationContext(), FullActivity.class);
                        String url_image = dataList.get(position).getUrlImage();
                        String title = dataList.get(position).getTitle();
                        String url = dataList.get(position).getUrl();
                        String descr = dataList.get(position).getDescr();

                        if (url_image.isEmpty() | title.isEmpty() | url.isEmpty() | descr.isEmpty()){
                            System.out.println(url_image + ":" + title + ":" + url + ":" + descr);
                            Toast.makeText(MainActivity.this, "Одно из условий не выполнено", Toast.LENGTH_SHORT).show();
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

        new FetchDataTask(adapter).execute(url);

    }
}