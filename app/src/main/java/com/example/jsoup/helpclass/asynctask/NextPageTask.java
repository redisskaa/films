package com.example.jsoup.helpclass.asynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.example.jsoup.helpclass.adapters.CustomAdapter;
import com.example.jsoup.model.UniversalListItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class NextPageTask extends AsyncTask<String, Integer, List<String>> {

    List<String> listPages = new ArrayList<>();

    public List<String> getListPages() {
        return listPages;
    }

    public NextPageTask() {}

    public NextPageTask(CustomAdapter adapter, ProgressBar progressBar) {

    }

    @Override
    protected List<String> doInBackground(String... strings) {

        String page;

        try {

            Document document = Jsoup.connect(strings[0]).get();

            /// последняя страница
            ///Element element = document.select("div.navigation").select("a").last();

            ///assert element != null;

            ///int element_last = Integer.parseInt(element.text());
            ///System.out.println(element_last);

            UniversalListItem<String> pagesListObj = new UniversalListItem<>();
            UniversalListItem<String> urlsObject = new UniversalListItem<>();

            for (Element pages : document.select("div.navigation").select("a")) {
                page = pages.text(); /// все страницы
                pagesListObj.addItem(page);
                System.out.println(Arrays.toString(new UniversalListItem[]{pagesListObj}));
            }

            listPages.add("Элемент 1");
            listPages.add("Элемент 2");
            listPages.add("Элемент 3");
            listPages.add("Элемент 4");

            for (Element pages : document.select("div.navigation").select("a")) {
                String attr_href = pages.attr("href"); /// ссылки url на страницы
                urlsObject.addItem(attr_href);
                System.out.println(Arrays.toString(new UniversalListItem[]{urlsObject}));
            }

            String s = pagesListObj.getItem(pagesListObj.getItems(), "5");
            String a = urlsObject.getItem(urlsObject.getItems(), "dfsdfsd");

            System.out.println("Вернули: " + s);
            System.out.println("Вернули: " + a);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return listPages;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        for (Integer value : values) {
            System.out.println("Выполняем: " + value);
        }

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<String> strings) {

        for (int i = 0; i < strings.size(); i++) {
            System.out.println("Завершение: " + strings.get(i));
        }

        super.onPostExecute(strings);
    }

    @Override
    protected void onPreExecute() {
        System.out.println("Запуск PageTask");
        super.onPreExecute();
    }
}
