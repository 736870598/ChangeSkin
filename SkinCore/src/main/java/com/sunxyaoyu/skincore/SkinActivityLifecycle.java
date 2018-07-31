package com.sunxyaoyu.skincore;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.LayoutInflater;

import com.sunxyaoyu.skincore.utils.SkinThemeUtils;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * --activity 的生命周期回调函数，在activity中对应的生命周期函数中super()方法中被回调，
 * <p>
 * Created by sunxy on 2018/7/30 0030.
 */
public class SkinActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    private HashMap<Activity, SkinLayoutFactory> mLayoutFactoryMap = new HashMap<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        /**
         *  更新状态栏
         */
        SkinThemeUtils.updateStatusBar(activity);

        /**
         * 更新字体
         */
        Typeface typeface = SkinThemeUtils.getSkinTypeface(activity);

        /**
         *  更新布局视图
         */
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        try {
            //将layoutInflater中的 mFactorySet 参数设置为false，
            // 在源码中，设置factory2的时候回对mFactorySet进行判断，
            // 如果为true则不容许设置factory2，并且抛出异常。
            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
            field.setAccessible(true);
            field.setBoolean(layoutInflater, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SkinLayoutFactory skinLayoutFactory = new SkinLayoutFactory(activity, typeface);
        LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutFactory);

//       注册观察者
        SkinManager.getInstance().addObserver(skinLayoutFactory);
        mLayoutFactoryMap.put(activity, skinLayoutFactory);

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
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        SkinManager.getInstance().deleteObserver(mLayoutFactoryMap.remove(activity));
    }

    public void updateSkin(Activity activity){
        SkinLayoutFactory skinLayoutFactory = mLayoutFactoryMap.get(activity);
        skinLayoutFactory.update(null, null);
    }
}
