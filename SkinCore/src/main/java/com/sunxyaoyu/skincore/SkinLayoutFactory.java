package com.sunxyaoyu.skincore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * 创建布局文件里面view的factory
 *
 * Created by Sunxy on 2018/3/17.
 */

public class SkinLayoutFactory implements LayoutInflater.Factory2, Observer {

    private static final String[] mClassPrefixList = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };

    private static final Class<?>[] mConstructorSignature = new Class[]{Context.class, AttributeSet.class};

    private static final HashMap<String, Constructor<? extends View>> sConstructorMap = new HashMap<String, Constructor<? extends View>>();

    // 属性处理类
    private SkinAttribute skinAttribute;

    public SkinLayoutFactory(){
        skinAttribute = new SkinAttribute();
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attributeSet) {
        View view = createViewFromTag(name, context, attributeSet);
        //筛选符合属性的View
        skinAttribute.load(view, attributeSet);
        return view;
    }

    private View createViewFromTag(String name, Context context, AttributeSet attributeSet) {
        View view;
        for (int i = 0; i < mClassPrefixList.length; i++) {
            view = createView(mClassPrefixList[i] + name, context, attributeSet);
            //系统的控件
            if (view != null){
                return view;
            }
        }
        //不是系统的控件，其中包含 .
        return createView(name, context, attributeSet);
    }

    private View createView(String name, Context context, AttributeSet attributeSet) {
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        if (constructor == null){
            try {
                Class<? extends View> aClass = context.getClassLoader().loadClass(name).asSubclass(View.class);
                constructor = aClass.getConstructor(mConstructorSignature);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (constructor != null){
            try {
                return constructor.newInstance(context, attributeSet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public View onCreateView(String s, Context context, AttributeSet attributeSet) {
        return null;
    }

    @Override
    public void update(Observable observable, Object o) {
        // 更换皮肤
        skinAttribute.applySkin();
    }
}
