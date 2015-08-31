package com.example.kalpak44.mychat.utils;

import android.app.Application;

/**
 * Created by kalpak44 on 15-8-24.
 */
public class MyChat extends Application {
    private Settings settings;
    private BlackList blackList;
    private FavList favlist;


    @Override
    public void onCreate() {
        settings = new Settings(getApplicationContext());
        blackList = new BlackList(getApplicationContext());
        favlist = new FavList(getApplicationContext());

        super.onCreate();
    }

    public Settings getSettings() {
        return settings;
    }

    public BlackList getBlackList() {
        return blackList;
    }

    public FavList getFavList() {
        return favlist;
    }


}
