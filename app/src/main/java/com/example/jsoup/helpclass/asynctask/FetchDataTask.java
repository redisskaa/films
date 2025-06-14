package com.example.jsoup.helpclass.asynctask;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.example.jsoup.helpclass.adapters.CustomAdapter;
import com.example.jsoup.model.CardFilm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class FetchDataTask extends AsyncTask<String, Integer, List<CardFilm>> {
    private final CustomAdapter adapter;
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;

    public FetchDataTask(CustomAdapter adapter, ProgressBar pBar) {
        this.adapter = adapter;
        this.progressBar = pBar;
    }

    @Override
    protected List<CardFilm> doInBackground(String... urls) {

        List<CardFilm> dataList = new ArrayList<>();

        String descr = null;

        String rate = null;

        try {

//            String html_test = "<div class=\"descriptionnew\" itemprop=\"description\">\n" +
//                    "<h2 class=\"fsubtitle\">Охотники за молниями - смотреть онлайн в качестве Full HD</h2>\n" +
//                    "Мия Ибарра — инженер-электрик, которая отчаянно стремится сделать карьеру и получить повышение по службе. " +
//                    "Она отправляется на Аляску, в местный исследовательский центр по изучению молний, " +
//                    "задавшись целью превратить их в мощный источник возобновляемой энергии. " +
//                    "Там Мия обнаруживает, что исследователь в центре всего один — самоуверенный и одержимый " +
//                    "фантазиями Лукас Флетчер, который с помощью подручных средств пытается укротить стихию.\n" +
//                    "</div>";
//            Document doc = Jsoup.parse(html_test);
//            Elements elements = doc.select("h2.fsubtitle");
//            elements.remove(); // Удаление элемента
//            System.out.println("Конечный код: " + doc.html());
//
            Document document = Jsoup.connect(urls[0]).get();

            for (Element element : document.select("div.th-item")) {
                String title = element.select("div.th-desc").select("h2").text();
                String image_url = "https://hd.kinotac.net" + element.select("img").attr("src");
                String url = element.select("a").attr("href");

                Document docfull = Jsoup.connect(url).get();

                for (Element rating : docfull.select("div.rating")) {
                    rate = rating.select("li.current-rating").text();
                }

                Document parserHtml = Jsoup.parse(docfull.html());

                for (Element element1 : parserHtml.select("article.full")) {
                    Elements elements_all = element1.select("div.descriptionnew");
                    Elements elements = elements_all.select("h2.fsubtitle");
                    elements.empty();// Удаление
                    descr = elements_all.select("div.descriptionnew").text();
                    System.out.println("Конечный код парсера: " + descr);
                }

                dataList.add(new CardFilm(title, url, descr, image_url, rate));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return dataList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (progressBar != null){
            progressBar.setProgress(values[0]);
            System.out.println(values[0]);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onPostExecute(List<CardFilm> result) {
        adapter.updateData(result);
        adapter.notifyDataSetChanged();
        if (progressBar != null){
            progressBar.setVisibility(View.GONE);
        }
        super.onPostExecute(result);
    }

}
