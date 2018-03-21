package com.sunxyaoyu.skincore;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
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
 *  保存 一个xml文件中view和其属性的 类
 *
 * Created by Sunxy on 2018/3/17.
 */
public class SkinAttribute {

    /**
     *  SkinView集合
     */
    private List<SkinView> mSkinViews = new ArrayList<>();

    /**
     * 当前字体
     */
    private Typeface typeface;

    /**
     * 能进行更换皮肤操作 的属性
     */
    private static final List<String> mAttributes = new ArrayList<>();

    static {
        mAttributes.add("background");
        mAttributes.add("src");
        mAttributes.add("textColor");
        mAttributes.add("drawableLeft");
        mAttributes.add("drawableTop");
        mAttributes.add("drawableRight");
        mAttributes.add("drawableBottom");
    }


    public SkinAttribute(Typeface typeface) {
        this.typeface = typeface;
    }

    /**
     * 将view中符合可更换的attr筛选出来保存
     */
    public void load(View view, AttributeSet attrs){
        List<SkinPair> skinPairs = new ArrayList<>();
        for (int i = 0; i < attrs.getAttributeCount(); i++){
            String attributeName = attrs.getAttributeName(i);
            //是否符合需要筛选的属性名
            if (mAttributes.contains(attributeName)){
                String attributeValue = attrs.getAttributeValue(i);
                //写死了，不管了
                if (attributeValue.startsWith("#")){
                    continue;
                }
                int resId;
                if (attributeValue.startsWith("?")){
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    resId = SkinThemeUtils.getResId(view.getContext(), new int[]{attrId})[0];
                }else {
                    resId = Integer.parseInt(attributeValue.substring(1));
                }
                if (resId != 0){
                    skinPairs.add(new SkinPair(attributeName, resId));
                }
            }
        }

        if (!skinPairs.isEmpty()){
            SkinView skinView = new SkinView(view, skinPairs);
            skinView.applySkin(typeface);
            mSkinViews.add(skinView);
        }
    }

    /**
     * 换皮肤
     */
    public void applySkin(Typeface typeface){
        for (SkinView mSkinView : mSkinViews){
            mSkinView.applySkin(typeface);
        }
    }

    /**
     * 保存view 和 其 可修改的属性
     */
    class SkinView{
        View view;
        List<SkinPair> skinPairs;

        public SkinView(View view, List<SkinPair> skinPairs) {
            this.view = view;
            this.skinPairs = skinPairs;
        }

        /**
         * 更换皮肤 （主要执行类）
         */
        public void applySkin(Typeface typeface){
            //修改字体
            applySkinTypeface(typeface);
            //通知自定义view切换
            applySkinViewSupport();

            for (SkinPair skinPair : skinPairs) {
                Drawable left = null, top = null, right = null, bottom = null;
                switch (skinPair.attributeName) {
                    case "background":
                        Object background = SkinResources.getInstance().getBackground(skinPair
                                .resId);
                        //Color
                        if (background instanceof Integer) {
                            view.setBackgroundColor((Integer) background);
                        } else {
                            ViewCompat.setBackground(view, (Drawable) background);
                        }
                        break;
                    case "src":
                        background = SkinResources.getInstance().getBackground(skinPair
                                .resId);
                        if (background instanceof Integer) {
                            ((ImageView) view).setImageDrawable(new ColorDrawable((Integer)
                                    background));
                        } else {
                            ((ImageView) view).setImageDrawable((Drawable) background);
                        }
                        break;
                    case "textColor":
                        ((TextView) view).setTextColor(SkinResources.getInstance().getColorStateList
                                (skinPair.resId));
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
                    default:
                        break;
                }
                if (null != left || null != right || null != top || null != bottom) {
                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(left, top, right,
                            bottom);
                }
            }
        }

        /**
         * 更换字体
         */
        private void applySkinTypeface(Typeface typeface) {
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeface);
            }
        }

        /**
         * 通知自定义view换肤
         */
        private void applySkinViewSupport() {
            if (view instanceof SkinViewSupport) {
                ((SkinViewSupport) view).applySkin();
            }
        }


    }

    /**
     * 保存属性名 和 属性 id
     */
    class SkinPair{
        String attributeName;
        int resId;

        public SkinPair(String attributeName, int resId){
            this.attributeName = attributeName;
            this.resId = resId;
        }

    }
}
