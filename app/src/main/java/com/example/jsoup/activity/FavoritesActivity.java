// FavoritesActivity.java
package com.example.jsoup.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jsoup.R;
import com.example.jsoup.helpclass.RecyclerItemClickListener;
import com.example.jsoup.helpclass.adapters.CustomAdapter;
import com.example.jsoup.model.CardFilm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private List<CardFilm> favoritesList;
    private SharedPreferences favoritesPref;
    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites); // Создайте layout похожий на activity_main, но без пагинации

        favoritesPref = getSharedPreferences("Favorites", MODE_PRIVATE);

        recyclerView = findViewById(R.id.recyclerView);
        favoritesList = getFavorites();
        adapter = new CustomAdapter(this, favoritesList);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CardFilm film = favoritesList.get(position);
                Intent intent = new Intent(FavoritesActivity.this, FullActivity.class);
                intent.putExtra("url_image", film.getUrlImage());
                intent.putExtra("title", film.getTitle());
                intent.putExtra("url", film.getUrl());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {}
        }));

        startRatingLoading(favoritesList);
    }

    private List<CardFilm> getFavorites() {
        String json = favoritesPref.getString("favorites_list", null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<CardFilm>>(){}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public void startRatingLoading(List<CardFilm> films) {
        for (CardFilm film : films) {
            executor.execute(() -> {
                String rating = fetchRating(film.getUrl());
                film.setRating(rating);
                mainHandler.post(() -> {
                    int position = favoritesList.indexOf(film);
                    if (position != -1) adapter.notifyItemChanged(position);
                });
            });
        }
    }

    private String fetchRating(String filmUrl) {
        try {
            Document doc = Jsoup.connect(filmUrl).userAgent("Mozilla/5.0").timeout(8000).get();
            Element ratingEl = doc.selectFirst("li.current-rating");
            if (ratingEl != null) {
                try {
                    return String.valueOf(Integer.parseInt(ratingEl.text()));
                } catch (Exception e) {
                    return "0";
                }
            }
        } catch (Exception ignored) {}
        return "0";
    }
}