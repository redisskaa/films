// FullActivity.java
package com.example.jsoup.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jsoup.R;
import com.example.jsoup.model.CardFilm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FullActivity extends Activity {

    private TextView textViewTitle, view_descr;
    private Button buttonView, buttonFavorite;
    private ImageView imgViewFull;
    private ProgressBar progressBar;

    private final int[] textViewIds = {
            R.id.raitingTv, R.id.yearTv, R.id.ageTv, R.id.janrTv,
            R.id.stranaTv, R.id.directorTv, R.id.roleTv, R.id.timeTv, R.id.premieraTv
    };
    private TextView[] infoTextViews;

    private String url, title, url_image;
    private SharedPreferences cache;
    private SharedPreferences favoritesPref;
    private CardFilm currentFilm;

    ImageView favoriteImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full); // ← ПЕРВЫЙ!

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        cache = getSharedPreferences("FilmCache", MODE_PRIVATE);
        favoritesPref = getSharedPreferences("Favorites", MODE_PRIVATE);

        initViews();
        getIntentData();
        loadFilmDetails();
        setupWatchButton();
        setupFavoriteButton();
    }

    private void initViews() {
        textViewTitle = findViewById(R.id.view_title_full);
        imgViewFull = findViewById(R.id.view_image_full);
        view_descr = findViewById(R.id.view_descr);
        buttonView = findViewById(R.id.view_film_button);
        progressBar = findViewById(R.id.progressBar);
        favoriteImage = findViewById(R.id.favoriteStar);

        infoTextViews = new TextView[textViewIds.length];
        for (int i = 0; i < textViewIds.length; i++) {
            int id = textViewIds[i];
            infoTextViews[i] = findViewById(id);
            if (infoTextViews[i] == null) {
                Log.e("FullActivity", "TextView НЕ НАЙДЕН: " + getResources().getResourceEntryName(id));
            } else {
                Log.d("FullActivity", "TextView найден: " + getResources().getResourceEntryName(id));
            }
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        url_image = intent.getStringExtra("url_image");

        if (textViewTitle != null) {
            textViewTitle.setText(title);
        }

        if (imgViewFull != null) {
            if (url_image != null && !url_image.isEmpty()) {
                Picasso.get()
                        .load(url_image)
                        .fit()
                        .error(R.drawable.image)
                        .placeholder(R.drawable.image)
                        .into(imgViewFull);
            } else {
                imgViewFull.setImageResource(R.drawable.image);
            }
        }
    }

    private void loadFilmDetails() {
        if (url == null || url.isEmpty()) {
            showError("Нет ссылки на фильм");
            return;
        }

        // === КЭШ ===
        String cachedData = cache.getString(url, null);
        if (cachedData != null) {
            String[] parts = cachedData.split(";;;");
            if (parts.length >= 10) {
                String descr = parts[0];
                List<String> info = Arrays.asList(parts).subList(1, 10);
                runOnUiThread(() -> {
                    view_descr.setText(descr);
                    updateInfoFields(info);
                    currentFilm = new CardFilm(title, url, descr, url_image, info.get(0));
                    updateFavoriteButton();
                });
                return;
            }
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Linux; Android 10; Pixel 3)")
                        .timeout(10000)
                        .get();

                String descr = parseDescription(doc);
                List<String> info = parseInfoList(doc);

                // === СОХРАНЕНИЕ В КЭШ ===
                StringBuilder cacheData = new StringBuilder(descr);
                for (String s : info) cacheData.append(";;;").append(s);
                cache.edit().putString(url, cacheData.toString()).apply();

                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (view_descr != null) view_descr.setText(descr);
                    updateInfoFields(info);
                    currentFilm = new CardFilm(title, url, descr, url_image, info.get(0));
                    updateFavoriteButton();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    showError("Нет сети или сервер недоступен");
                });
                Log.e("FullActivity", "Ошибка загрузки", e);
            }
        }).start();
    }

    private String parseDescription(Document doc) {
        Element block = doc.selectFirst("div.descriptionnew");
        if (block == null) return "Описание отсутствует.";
        block.select("h2.fsubtitle").remove();
        String text = block.text().trim();
        return text.isEmpty() ? "Описание отсутствует." : text;
    }

    private List<String> parseInfoList(Document doc) {
        List<String> info = new ArrayList<>();
        for (Element item : doc.select("div.item div.info")) {
            String text = item.text().trim();
            if (!text.isEmpty() && !text.equals("Скоро на сайте")) {
                info.add(text);
            }
        }
        while (info.size() < 9) info.add("Нет информации");
        return info;
    }

    private void updateInfoFields(List<String> info) {
        if (info == null || info.size() < 9 || infoTextViews == null) {
            Log.e("FullActivity", "updateInfoFields: данные неполные");
            return;
        }

        String[] labels = {
                getString(R.string.raiting),
                getString(R.string.year),
                getString(R.string.age),
                getString(R.string.janr),
                getString(R.string.strana),
                getString(R.string.director),
                getString(R.string.role),
                getString(R.string.time),
                getString(R.string.premiera)
        };

        for (int i = 0; i < infoTextViews.length; i++) {
            TextView tv = infoTextViews[i];
            if (tv != null) {
                String value = info.get(i);
                String label = labels[i];

                String displayText = label + " " + value;
                tv.setText(displayText);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tv.setTooltipText(value);
                }
            }
        }
    }

    private void showError(String message) {
        if (view_descr != null) {
            view_descr.setText(message);
        }
    }

    private void setupWatchButton() {
        if (buttonView != null) {
            buttonView.setOnClickListener(v -> {
                Intent intent = new Intent(this, FilmActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            });
        }
    }

    private void setupFavoriteButton() {

        if (favoriteImage != null){
            favoriteImage.setOnClickListener(v -> {
                toggleFavorite();
            });
        }
    }

    private void updateFavoriteButton() {
        if (favoriteImage != null) {
            if (isFavorite()) {
                favoriteImage.setImageResource(R.drawable.star3);
            } else {
                favoriteImage.setImageResource(R.drawable.star2);
            }
        }
    }

    private boolean isFavorite() {
        List<CardFilm> favorites = getFavorites();
        for (CardFilm film : favorites) {
            if (film.getUrl().equals(url)) {
                return true;
            }
        }
        return false;
    }

    private void toggleFavorite() {
        List<CardFilm> favorites = getFavorites();
        if (isFavorite()) {
            favorites.removeIf(film -> film.getUrl().equals(url));
            Toast.makeText(this, "Удалено из избранного", Toast.LENGTH_SHORT).show();
        } else {
            favorites.add(currentFilm);
            Toast.makeText(this, "Добавлено в избранное", Toast.LENGTH_SHORT).show();
        }
        saveFavorites(favorites);
        updateFavoriteButton();
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

    private void saveFavorites(List<CardFilm> favorites) {
        Gson gson = new Gson();
        String json = gson.toJson(favorites);
        favoritesPref.edit().putString("favorites_list", json).apply();
    }
}