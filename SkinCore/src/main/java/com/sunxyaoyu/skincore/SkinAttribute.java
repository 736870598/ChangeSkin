package com.sunxyaoyu.skincore;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunxyaoyu.skincore.utils.SkinResources;
import com.sunxyaoyu.skincore.utils.SkinThemeUtils;
import com.sunxyaoyu.skincore.view.SkinViewSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * --
 * <p>
 * Created by sunxy on 2018/7/30 0030.
 */
public class SkinAttribute {

    private static final List<String> mAttributes = new ArrayList<>();

    //view中可替换资源的文件...
    static {
        mAttributes.add("background");
        mAttributes.add("src");
        mAttributes.add("textColor");
        mAttributes.add("drawableLeft");
        mAttributes.add("drawableTop");
        mAttributes.add("drawableRight");
        mAttributes.add("drawableBottom");
        mAttributes.add("skinTypeface");
    }

    private Typeface typeface;
    private List<SkinView> mSkinViews = new ArrayList<>();

    public SkinAttribute(Typeface typeface){
        this.typeface = typeface;
    }

    public void load(View view, AttributeSet attrs){
        List<SkinPair> skinPairs = new ArrayList<>();
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String attributeName = attrs.getAttributeName(i);
            if (mAttributes.contains(attributeName)){
                String attributeValue = attrs.getAttributeValue(i);

                if (attributeValue.startsWith("#")){
                    //类似写的“#000000”这类写死了的，不去替换
                    continue;
                }
                int resId;
                if(attributeValue.startsWith("?")){
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    //获得 主题 style 中的 对应 attr 的资源id值
                    resId = SkinThemeUtils.getResId(view.getContext(), new int[]{attrId})[0];
                }else{
                    // @12343455332
                    resId = Integer.parseInt(attributeValue.substring(1));
                }
                if (resId != 0){
                    //可以被替换的属性
                    SkinPair skinPair = new SkinPair(attributeName, resId);
                    skinPairs.add(skinPair);
                }
            }
        }

        //将View与之对应的可以动态替换的属性集合 放入 集合中
        if (!skinPairs.isEmpty() || view instanceof TextView || view instanceof SkinViewSupport){
            SkinView skinView = new SkinView(view, skinPairs);
            skinView.applySkin(typeface);
            mSkinViews.add(skinView);
        }
    }

    public void applySkin(Typeface typeface){
        for (SkinView mSkinView : mSkinViews) {
            mSkinView.applySkin(typeface);
        }
    }


    static class SkinView {
        View view;
        List<SkinPair> skinPairs;

        public SkinView(View view, List<SkinPair> skinPairs) {
            this.view = view;
            this.skinPairs = skinPairs;
        }

        public void applySkin(Typeface typeface){
            applySkinTypeface(typeface);
            applySkinViewSupport();
            for (SkinPair skinPair : skinPairs) {
                Drawable left = null, top = null, right = null, bottom = null;
                switch (skinPair.attributeName){
                    case "background":
                        Object background = SkinResources.getInstance().getBackground(skinPair.resId);
                        if (background instanceof Integer){
                            view.setBackgroundColor((Integer) background);
                        }else{
                            view.setBackground((Drawable) background);
                        }
                        break;
                    case "src":
                        background = SkinResources.getInstance().getBackground(skinPair.resId);
                        if (background instanceof Integer) {
                            ((ImageView) view).setImageDrawable(new ColorDrawable((Integer)background));
                        } else {
                            ((ImageView) view).setImageDrawable((Drawable) background);
                        }
                        break;
                    case "textColor":
                        if (view instanceof TextView){
                            ((TextView) view).setTextColor(SkinResources.getInstance().getColorStateList(skinPair.resId));
                        }
                        break;
                    case "drawableLeft":
                        left = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableTop":
                        top = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableRight":
                        right = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableBottom":
                        bottom = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "skinTypeface":
                        Typeface typeface1 = SkinResources.getInstance().getTypeface(skinPair.resId);
                        applySkinTypeface(typeface1);
                        break;
                    default:
                        break;
                }
                if ((view instanceof TextView) && (null != left || null != right || null != top || null != bottom)){
                    ((TextView) view).setCompoundDrawablesRelativeWithIntrinsicBounds(left, top, right, bottom);
                }
            }
        }

        private void applySkinViewSupport(){
            if (view instanceof SkinViewSupport){
                ((SkinViewSupport) view).applySkin();
            }
        }

        private void applySkinTypeface(Typeface typeface){
            if (view instanceof TextView){
                ((TextView) view).setTypeface(typeface);
            }
        }
    }

    static class SkinPair {
        String attributeName;
        int resId;

        public SkinPair(String attributeName, int resId) {
            this.attributeName = attributeName;
            this.resId = resId;
        }
    }

}
