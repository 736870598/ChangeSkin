package com.sunxyaoyu.skincore;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.Log;
import android.view.LayoutInflater;

import com.sunxyaoyu.skincore.utils.SkinThemeUtils;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * activity生命周期回调函数
 * Created by Sunxy on 2018/3/17.
 */
public class SkinActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    private HashMap<Activity, SkinLayoutFactory> mLayoutFactoryMap = new HashMap<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

        // 更新状态栏
        SkinThemeUtils.updateStatusBar(activity);

        try {
            //替换activity里LayoutInflater的布局加载器的factory
            //如果LayoutInflater设置过factory的话，那么mFactorySet为true，再次设置的话会抛出异常
            //这里先将LayoutInflater里的mFactorySet设置为false
            LayoutInflater layoutInflater = LayoutInflater.from(activity);

            //Android 布局加载器 使用 mFactorySet 标记是否设置过Factory
            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
            field.setAccessible(true);
            //设置 mFactorySet 标签为false
            field.setBoolean(layoutInflater, false);

            // 获取字体，并初始化factory，该factory作用在activity及fragment上
            Typeface typeface = SkinThemeUtils.getSkinTypeface(activity);
            SkinLayoutFactory skinLayoutFactory = new SkinLayoutFactory(activity, typeface);

            //为layoutInflater 设置 factory
            LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutFactory);
            mLayoutFactoryMap.put(activity, skinLayoutFactory);

            //注册观察者
            SkinManager.getInstance().addObserver(skinLayoutFactory);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //删除观察者
        SkinLayoutFactory skinLayoutFactory = mLayoutFactoryMap.remove(activity);
        SkinManager.getInstance().deleteObserver(skinLayoutFactory);

    }
}
