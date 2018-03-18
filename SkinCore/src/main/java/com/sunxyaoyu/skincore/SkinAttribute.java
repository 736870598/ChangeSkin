package com.sunxyaoyu.skincore;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunxyaoyu.skincore.utils.SkinResources;
import com.sunxyaoyu.skincore.utils.SkinThemeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Sunxy on 2018/3/17.
 */
public class SkinAttribute {

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

    List<SkinView> mSkinViews = new ArrayList<>();

    public void load(View view, AttributeSet attrs){
        List<SkinPair> skinPairs = new ArrayList<>();
        for (int i = 0; i < attrs.getAttributeCount(); i++){
            String arrtibuteName = attrs.getAttributeName(i);
            //是否符合需要筛选的属性名
            if (mAttributes.contains(arrtibuteName)){
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
                    skinPairs.add(new SkinPair(arrtibuteName, resId));
                }
            }
        }

        if (!skinPairs.isEmpty()){
            SkinView skinView = new SkinView(view, skinPairs);
            skinView.applySkin();
            mSkinViews.add(skinView);
        }
    }

    /**
     * 换皮肤
     */
    public void applySkin(){
        for (SkinView mSkinView : mSkinViews){
            mSkinView.applySkin();
        }
    }

    class SkinView{
        View view;
        List<SkinPair> skinPairs;

        public SkinView(View view, List<SkinPair> skinPairs) {
            this.view = view;
            this.skinPairs = skinPairs;
        }

        public void applySkin(){
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
    }

    class SkinPair{

        String attributeName;
        int resId;

        public SkinPair(String attributeName, int resId){
            this.attributeName = attributeName;
            this.resId = resId;
        }

    }
}
