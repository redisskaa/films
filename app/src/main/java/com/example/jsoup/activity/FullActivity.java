package com.example.jsoup.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jsoup.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FullActivity extends Activity {

    String url, title, url_image, descr, page;
    TextView textView, view_descr;
    ImageView imgViewFull;
    Button buttonView;
    TextView tViewRaiting,
            tViewYear,
            tViewAge,
            tViewJanr,
            tViewStrana,
            tViewDirector,
            tViewRole,
            tViewTime,
            tViewPremiera;
    List<String> infoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        infoList = new ArrayList<>();
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

        tViewRaiting = findViewById(R.id.raitingTv);
        tViewYear = findViewById(R.id.yearTv);
        tViewAge = findViewById(R.id.ageTv);
        tViewJanr = findViewById(R.id.janrTv);
        tViewStrana = findViewById(R.id.stranaTv);
        tViewDirector = findViewById(R.id.directorTv);
        tViewRole = findViewById(R.id.roleTv);
        tViewTime = findViewById(R.id.timeTv);
        tViewPremiera = findViewById(R.id.premieraTv);

        getInfo();
        buttonView.setOnClickListener(v -> {
            Intent filmIntent = new Intent(FullActivity.this, FilmActivity.class);
            filmIntent.putExtra("url", url);
            startActivity(filmIntent);
        });

        Picasso.get().load(url_image).error(R.drawable.image).placeholder(R.drawable.image).into(imgViewFull);
        textView.setText(title);
        view_descr.setText(descr);
    }

    private void getInfo() {
        new Thread(() -> {
            try {

                Document document = Jsoup.connect(url).get();

                for (Element pages : document.select("div.item").select("div.info")) {
                    page = pages.text(); /// все страницы
                    infoList.add(page);
                }

                System.out.println(page);
                System.out.println("Размер: " + infoList.size());

                Iterator<String> iterator = infoList.iterator();
                while (iterator.hasNext()) {
                    String value = iterator.next();
                    if (value.equals("Скоро на сайте")) {
                        iterator.remove(); // Удаляем элемент через итератор
                        System.out.println("Размер: " + infoList.size());
                    }
                }

                runOnUiThread(() -> {

                    if (infoList.size() == 9){
                        tViewRaiting.setText(infoList.get(0));
                        tViewYear.setText(infoList.get(1));
                        tViewAge.setText(infoList.get(2));
                        tViewJanr.setText(infoList.get(3));
                        tViewStrana.setText(infoList.get(4));
                        tViewDirector.setText(infoList.get(5));
                        tViewRole.setText(infoList.get(6));
                        tViewTime.setText(infoList.get(7));
                        tViewPremiera.setText(infoList.get(8));
                        System.out.println("Размер: " + infoList.size());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            tViewRaiting.setTooltipText(infoList.get(0));
                            tViewYear.setTooltipText(infoList.get(1));
                            tViewAge.setTooltipText(infoList.get(2));
                            tViewJanr.setTooltipText(infoList.get(3));
                            tViewStrana.setTooltipText(infoList.get(4));
                            tViewDirector.setTooltipText(infoList.get(5));
                            tViewRole.setTooltipText(infoList.get(6));
                            tViewTime.setTooltipText(infoList.get(7));
                            tViewPremiera.setTooltipText(infoList.get(8));
                        }

                    }else {
                        tViewRaiting.setText("0.0");
                        tViewYear.setText(infoList.get(1));
                        tViewAge.setText(infoList.get(2));
                        tViewJanr.setText(infoList.get(3));
                        tViewStrana.setText(infoList.get(4));
                        tViewDirector.setText(infoList.get(5));
                        tViewRole.setText(infoList.get(6));
                        tViewTime.setText(infoList.get(7));
                        tViewPremiera.setText("Нет информации");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            tViewPremiera.setTooltipText(tViewPremiera.getText());
                        }
                    }

                    System.out.println("UI Thread 2");
                    System.out.println(Arrays.toString(infoList.toArray()));
                });
            } catch (IndexOutOfBoundsException | IOException e) {
                e.fillInStackTrace();
            }
        }).start();
    }
}