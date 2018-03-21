## 换肤框架

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

有很多换肤情况要根据项目本身去处理，这个矿建也不是全部通用的、

#### 使用：

在application的onCreate中：

    SkinManager.getInstance().init(this);

进行初始化。

通过调用

    SkinManager.getInstance().loadSkin(String path)

方法进行更换皮肤包。path为空说明还原皮肤包


自定义view实现SkinViewSupport并重写applySkin方法进行换肤，可通过 SkinResources 获取资源

