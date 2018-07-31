package com.sunxyaoyu.skincore;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.sunxyaoyu.skincore.utils.SkinThemeUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * -- LayoutInflater中创建xml中view的factory
 * <p>
 * Created by sunxy on 2018/7/30 0030.
 */
public class SkinLayoutFactory implements LayoutInflater.Factory2, Observer{

    private static final String[] mClassPrefixList = {
            "android.widget.",
            "android.view.",
            "android.webkit.",
    };

    private static final Class<?>[] mConstructorSignature = new Class[]{
            Context.class, AttributeSet.class
    };

    private static final HashMap<String, Constructor<? extends View>> sConstructorMap = new HashMap<>();

    private Activity activity;

    private SkinAttribute skinAttribute;

    public SkinLayoutFactory(Activity activity, Typeface typeface) {
        this.activity = activity;
        skinAttribute = new SkinAttribute(typeface);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = createViewFromTag(name, context, attrs);
        if (view == null){
            //自定义控件或者全类名控件
            view = createView(name, context, attrs);
        }
        //筛选符合属性的View
        skinAttribute.load(view, attrs);
        return view;
    }

    private View createViewFromTag(String name, Context context, AttributeSet attributeSet){
        if (name.contains(".")){
            //name中包含了 . 是自定义控件或者全类名控件
            return null;
        }
        View view = null;
        for (String prefix : mClassPrefixList) {
            view = createView(prefix+name, context, attributeSet);
            if (view != null){
                break;
            }
        }
        return view;
    }

    private View createView(String name, Context context, AttributeSet attributeSet) {
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        if (constructor == null){
            try {
                Class<? extends View> aClass = context.getClassLoader().loadClass(name).asSubclass(View.class);
                constructor = aClass.getConstructor(mConstructorSignature);
                sConstructorMap.put(name, constructor);
            } catch (Exception e) {
            }
        }
        if (constructor != null){
            try {
                return constructor.newInstance(context, attributeSet);
            } catch (Exception e) {
            }
        }
        return null;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        SkinThemeUtils.updateStatusBar(activity);
        Typeface typeface = SkinThemeUtils.getSkinTypeface(activity);
        skinAttribute.applySkin(typeface);
    }
}
