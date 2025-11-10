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

import com.example.jsoup.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FullActivity extends Activity {

    private TextView textViewTitle, view_descr;
    private Button buttonView;
    private ImageView imgViewFull;
    private ProgressBar progressBar;

    private final int[] textViewIds = {
            R.id.raitingTv, R.id.yearTv, R.id.ageTv, R.id.janrTv,
            R.id.stranaTv, R.id.directorTv, R.id.roleTv, R.id.timeTv, R.id.premieraTv
    };
    private TextView[] infoTextViews;

    private String url, title, url_image;
    private SharedPreferences cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full); // ← ПЕРВЫЙ!

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        cache = getSharedPreferences("FilmCache", MODE_PRIVATE);

        initViews();
        getIntentData();
        loadFilmDetails();
        setupWatchButton();
    }

    private void initViews() {
        textViewTitle = findViewById(R.id.view_title_full);
        imgViewFull = findViewById(R.id.view_image_full);
        view_descr = findViewById(R.id.view_descr);
        buttonView = findViewById(R.id.view_film_button);
        progressBar = findViewById(R.id.progressBar);

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
                        .error(R.drawable.image)
                        .placeholder(R.drawable.image)
                        .fit()
                        .centerCrop()
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
                runOnUiThread(() -> {
                    view_descr.setText(parts[0]);
                    List<String> info = Arrays.asList(parts).subList(1, 10);
                    updateInfoFields(info);
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

        for (int i = 0; i < infoTextViews.length; i++) {
            TextView tv = infoTextViews[i];
            if (tv != null) {
                String value = info.get(i);
                tv.setText(value);
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
}