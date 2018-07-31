package com.sunxyaoyu.skincore;

import android.app.Activity;
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
 * -- 皮肤包管理
 * <p>
 * Created by sunxy on 2018/7/30 0030.
 */
public class SkinManager extends Observable {

    private static SkinManager instance;
    private Application mContext;
    private SkinActivityLifecycle skinActivityLifecycle;


    private SkinManager(Application application){
        this.mContext = application;
        SkinPreference.init(application);
        SkinResources.init(application);

        skinActivityLifecycle = new SkinActivityLifecycle();
        application.registerActivityLifecycleCallbacks(skinActivityLifecycle);

        loadSkin(SkinPreference.getInstance().getSkin());
    }

    public static void init(Application application){
        if (instance == null){
            synchronized (SkinManager.class){
                if (instance == null){
                    instance = new SkinManager(application);
                }
            }
        }
    }

    public static SkinManager getInstance(){
        return instance;
    }

    /**
     * 应用皮肤
     * @param skinPath  皮肤路径， 为空则使用默认皮肤
     */
    public void loadSkin(String skinPath){
        skinPath = skinPath.trim();

        if (TextUtils.isEmpty(skinPath)){
            //如果为空，说明不使用其他皮肤包
            SkinPreference.getInstance().setSkin("");
            SkinResources.getInstance().reset();
        }else{
            //拿到apk的Resources
            try {
                //Resources的创建需要用到AssetManager，所以要先创建AssetManager
                AssetManager assetManager = AssetManager.class.newInstance();
                //获取assetManager的 addAssetPath 方法。
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                addAssetPath.invoke(assetManager, skinPath);

                Resources appResource = mContext.getResources();
                //根据当前的显示与配置(横竖屏、语言等)创建Resources
                Resources skinResource = new Resources(assetManager, appResource.getDisplayMetrics(),
                        appResource.getConfiguration());

                //获取外部Apk(皮肤包) 包名
                PackageManager mPm = mContext.getPackageManager();
                PackageInfo info = mPm.getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES);
                String packName = info.packageName;

                //应用皮肤包
                SkinResources.getInstance().applySkin(skinResource, packName);

                //记录
                SkinPreference.getInstance().setSkin(skinPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setChanged();
        notifyObservers(null);
    }

    public void updateSkin(Activity activity){
        skinActivityLifecycle.updateSkin(activity);
    }


}
