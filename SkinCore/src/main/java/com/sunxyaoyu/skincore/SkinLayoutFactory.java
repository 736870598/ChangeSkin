package com.sunxyaoyu.skincore;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.sunxyaoyu.skincore.utils.SkinThemeUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * LayoutInflater里创建view的factory
 *
 * Created by Sunxy on 2018/3/17.
 */

public class SkinLayoutFactory implements LayoutInflater.Factory2, Observer {

    // android自带的view的包名
    private static final String[] mClassPrefixList = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };

    private Activity activity;

    //反射创建view时使用到的view 的构造函数
    private static final Class<?>[] mConstructorSignature = new Class[]{Context.class, AttributeSet.class};

    //将控件的构造函数保存起来，同一个界面中可能有很多个相同的控件。
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap = new HashMap<String, Constructor<? extends View>>();

    // 属性处理类
    private SkinAttribute skinAttribute;

    public SkinLayoutFactory(Activity activity, Typeface typeface){
        this.activity = activity;
        skinAttribute = new SkinAttribute(typeface);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attributeSet) {
        View view = createViewFromTag(name, context, attributeSet);
        if (view != null){
            //将View中符合条件的属性筛选出来
            skinAttribute.load(view, attributeSet);
        }
        return view;
    }

    /**
     * 根据xml的tag创建view
     * @param name          xml文件中的tag，如果其中包含 . 说明是自定义控件，否则为系统控件
     * @param context       上下文
     * @param attributeSet  属性
     * @return  view
     */
    private View createViewFromTag(String name, Context context, AttributeSet attributeSet) {
        View view = null;
        if (name.contains(".")){
            //自定义控件
            view = createView(name, context, attributeSet);
        }
        if (view == null){
            //系统控件，包名拼接创建
            for (int i = 0; i < mClassPrefixList.length; i++) {
                view = createView(mClassPrefixList[i] + name, context, attributeSet);
                if (view != null){
                    break;
                }
            }
        }
        return view;
    }

    /**
     *  创建view
     */
    private View createView(String name, Context context, AttributeSet attributeSet) {
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        if (constructor == null){
            try {
                Class<? extends View> aClass = context.getClassLoader().loadClass(name).asSubclass(View.class);
                constructor = aClass.getConstructor(mConstructorSignature);
                sConstructorMap.put(name, constructor);
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


    /**
     * SkinManager发出了通知，更换了皮肤
     */
    @Override
    public void update(Observable observable, Object o) {
        //更改状态栏颜色
        SkinThemeUtils.updateStatusBar(activity);
        Typeface typeface = SkinThemeUtils.getSkinTypeface(activity);
        // 更换皮肤
        skinAttribute.applySkin(typeface);
    }
}
