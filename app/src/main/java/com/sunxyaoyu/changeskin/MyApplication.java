package com.sunxyaoyu.changeskin;

import android.app.Application;

import com.sunxyaoyu.skincore.SkinManager;

/**
 * Created by Sunxy on 2018/3/18.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.init(this);
    }
}
