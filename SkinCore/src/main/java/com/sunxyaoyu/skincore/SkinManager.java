package com.sunxyaoyu.skincore;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.sunxyaoyu.skincore.utils.SkinPreference;
import com.sunxyaoyu.skincore.utils.SkinResources;

import java.lang.reflect.Method;
import java.util.Observable;

/**
 * 皮肤管理器
 * Created by Sunxy on 2018/3/17.
 */
public class SkinManager extends Observable{

    private SkinManager(){}

    private static SkinManager manager;
    private Application application;

    public static SkinManager getInstance(){
        if (manager == null){
            synchronized (SkinManager.class){
                if (manager == null){
                    manager = new SkinManager();
                }
            }
        }
        return manager;
    }


    public void init(Application application){
        this.application = application;

        SkinPreference.init(application);
        SkinResources.init(application);
        //注册activity的生命周期回调函数
        application.registerActivityLifecycleCallbacks(new SkinActivityLifecycle());
        loadSkin(SkinPreference.getInstance().getSkin());
    }

    public void loadSkin(String path){
        if (TextUtils.isEmpty(path)){
            SkinPreference.getInstance().setSkin("");
            SkinResources.getInstance().reset();
        }else {
            try {
                AssetManager assetManager = AssetManager.class.newInstance();
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                addAssetPath.setAccessible(true);
                addAssetPath.invoke(assetManager, path);

                Resources resources = application.getResources();

                Resources shinResource = new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());
                PackageManager mPm = application.getPackageManager();
                PackageInfo info = mPm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
                String packageName = info.packageName;
                SkinResources.getInstance().applySkin(shinResource, packageName);
                //保存当前皮肤包
                SkinPreference.getInstance().setSkin(path);
            }catch (Exception e){
                e.printStackTrace();
            }
        }



        //应用皮肤包
        setChanged();
        //通知观察者
        notifyObservers();
    }


}
