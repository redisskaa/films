package com.example.jsoup.model;

import java.util.ArrayList;
import java.util.List;

public class UniversalListItem<T> {
    private final List<T> listItem;

    public UniversalListItem() {
        listItem = new ArrayList<>();
    }

    public UniversalListItem(List<T> listItem) {
        this.listItem = listItem;
    }

    public void addItem(T item) {
        listItem.add(item);
    }

    public boolean remove(T item){
        listItem.remove(item);
        return false;
    }

    public List<T> getItems() {
        return listItem;
    }

    public T getItem(List<T> item, T searchQuery){

        T s = null;

        for (int i = 0; i < item.size(); i++) {
            if (item.get(i).equals(searchQuery)){
                System.out.println("Найден: " + item.get(i));
                s = item.get(i);
                break;
            }else {
                System.out.println("К сожелению запрос <" + searchQuery + "> в листе не найден");
            }
        }

        return s;
    }
}
