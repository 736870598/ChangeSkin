package com.sunxyaoyu.skincore.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;

/**
 * --
 * <p>
 * Created by sunxy on 2018/7/30 0030.
 */
public class SkinResources {

    private static SkinResources instance;

    private String mSkinPkgName;
    private boolean isDefaultSkin = true;
    private Resources mSkinResources;
    private Resources mAppResources;

    private SkinResources(Context context) {
        mAppResources = context.getResources();
    }

    /**
     * init
     */
    public static void init(Context context){
        if (instance == null){
            synchronized (SkinResources.class){
                if (instance == null){
                    instance = new SkinResources(context);
                }
            }
        }
    }

    public static SkinResources getInstance(){
        return instance;
    }

    /**
     * 重置
     */
    public void reset(){
        mSkinPkgName ="";
        mSkinResources = null;
        isDefaultSkin = true;
    }

    /**
     * 设置换肤
     */
    public void applySkin(Resources resources, String pkgName){
        mSkinResources = resources;
        mSkinPkgName = pkgName;
        isDefaultSkin = TextUtils.isEmpty(pkgName) || resources == null;
    }

    /**
     * 根据id获取在皮肤包中的id
     */
    public int getIdentifier(int resId){
        if (isDefaultSkin){
            return resId;
        }
        //在皮肤包中不一定就是 当前程序的id
        //获取对应的 id 在当前程序中的 名字 及 类型
        String resName = mAppResources.getResourceEntryName(resId);
        String resType = mAppResources.getResourceTypeName(resId);
        //通过 名字 及 类型 获取资源id
        int skinId = mSkinResources.getIdentifier(resName, resType, mSkinPkgName);
        return skinId;
    }

    /**
     * 得到皮肤包中的color
     */
    public int getColor(int resId){
        if (isDefaultSkin){
            return mAppResources.getColor(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0){
            return mAppResources.getColor(resId);
        }
        return mSkinResources.getColor(skinId);
    }

    /**
     * 得到皮肤包中的color
     */
    public ColorStateList getColorStateList(int resId){
        if (isDefaultSkin) {
            return mAppResources.getColorStateList(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getColorStateList(resId);
        }
        return mSkinResources.getColorStateList(skinId);
    }

    /**
     * 得到皮肤包中的 drawable
     */
    public Drawable getDrawable(int resId) {
        //如果有皮肤  isDefaultSkin false 没有就是true
        if (isDefaultSkin) {
            return mAppResources.getDrawable(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getDrawable(resId);
        }
        return mSkinResources.getDrawable(skinId);
    }

    /**
     * 得到皮肤包中的 Background
     */
    public Object getBackground(int resId){
        // 可能是Color 也可能是drawable
        String resourceTypeName = mAppResources.getResourceTypeName(resId);
        if (resourceTypeName.equalsIgnoreCase("color")){
            return getColor(resId);
        }else{
            return getDrawable(resId);
        }
    }


    /**
     * 得到皮肤包中的 string
     */
    public String getString(int resId){
        if (isDefaultSkin) {
            return mAppResources.getString(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getString(skinId);
        }
        return mSkinResources.getString(skinId);
    }

    /**
     * 得到皮肤包中的 字体
     */
    public Typeface getTypeface(int resId){
        if (resId == 0){
            return Typeface.DEFAULT;
        }
        try {
            String skinTypefacePath = getString(resId);
            if (TextUtils.isEmpty(skinTypefacePath)){
                return Typeface.DEFAULT;
            }
            if (isDefaultSkin){
                return Typeface.createFromAsset(mAppResources.getAssets(), skinTypefacePath);
            }
            return Typeface.createFromAsset(mSkinResources.getAssets(), skinTypefacePath);

        }catch (Exception e){
            return Typeface.DEFAULT;
        }
    }

}
