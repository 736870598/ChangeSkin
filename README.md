## 手写换肤框架核心

一个强大的换肤框架，皮肤包可以是一个apk文件

#### 说明：

默认只是更换了以下的资源：

        mAttributes.add("background");
        mAttributes.add("src");
        mAttributes.add("textColor");
        mAttributes.add("drawableLeft");
        mAttributes.add("drawableTop");
        mAttributes.add("drawableRight");
        mAttributes.add("drawableBottom");
        mAttributes.add("skinTypeface");

有很多换肤情况要根据项目本身去处理，这个也不是全部通用的、

#### 使用：

在application的onCreate中：

    SkinManager.getInstance().init(this);

进行初始化。

通过调用

    SkinManager.getInstance().loadSkin(String path)

方法进行更换皮肤包。path为空说明还原皮肤包


自定义view实现SkinViewSupport并重写applySkin方法进行换肤，可通过 SkinResources 获取资源


#### 核心流程说明：

1. 在application的onCreate中调用：SkinManager().init(application)
   调用后会在application中注册一个ActivityLifecycleCallbacks（SkinActivityLifecycle），
   通过callBack来监听activity的生命周期（该回调方法在super对应方法中被调用），

2. 在activity的onCreate时候将创建xml文件中View所使用的 LayoutInflater.Factory2
   替换成我们自己的 SkinLayoutFactory，

3. 在Factory2中创建view的时候通过attrs来过滤这个view是否有需要替换的属性，有的话保存起来

4. 在factory2中每创建一个view后都会调用load方法来使得需要换肤的在第一时间替换。
   在SkinManager中applySkin的最后也会通知factory进行替换工作。

5. 在SkinManager中applySkin方法将创建出皮肤包的Resource，并交给SkinResource工具类进行管理。

6. 对应关系：
       Activity -> Factory2 -> Map<View, Map<attrName, resId>>

       其中在该框架中：
              Map<View, Map<attrName, resId>> == SkinAttribute 类
              Map<attrName, resId> == SkinPair 类

7. 注意：

    创建Resource：
    先创建出AssertManager，执行AssertManager的addAssetPath(皮肤包apk路径)方法后，
    根据当前的显示与配置(横竖屏、语言等)创建Resources

        Resources skinResource = new Resources(assetManager, appResource.getDisplayMetrics(), appResource.getConfiguration());

    替换 Factory2 方法：
    先将activity对应的LayoutInflater中的mFactorySet的值设置成false，因为在源码中，
    设置factory2的时候回对mFactorySet进行判断，如果为true则不容许设置factory2，并且抛出异常。

    获取一个id值得类型：

        String resType = resource.getResourceTypeName(resId);

    获取一个id值对应的名字：

        String resName = resource.getResourceEntryName(resId);

    通过 名字 及 类型 获取资源id

        int skinId = resource.getIdentifier(resName, resType, mSkinPkgName);

        相当于获取resource中的：R.resType.resName

    对于字体：
    在xml中的textView控件设置：skinTypeface="@string/typeface2" 指向地址。
    或者在application的style中添加 <item name="skinTypeface">@string/typeface2</item> 来设置全局的字体。




