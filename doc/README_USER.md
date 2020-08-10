
# 1. 准备工作




# 2. 快速上手

## 2.1 

## 2.1 Application

请继承`com.openxu.core.application.XCoreApplication`，如果不能继承该类，请在自己的Application的onCreate方法中完成类库初始化：
```xml
//初始化类库
XAppInitManager.init(this);
```

如果某个模块需要在`Application`创建时完成相关初始化操作，请继承`com.openxu.core.application.XBaseAppLogic`在`onApplicationCreate(String processName)`方法中完成初始化，其中processName是当前进程名，然后在`conf_app.gradle`脚本中配置`applicationInitClass`，有多个模块初始化类用`,`隔开：

- 继承XBaseAppLogic编写初始化逻辑

```Java
/**
 * Author: openXu
 * Time: 2019/2/23 14:49
 * class: ModuleXXXInitLogic
 * Description: 示例：ModuleXXX模块需要在Application启动时初始化的内容，在onApplicationCreate中完成初始化
 */
public class ModuleXXXInitLogic extends XBaseAppLogic {
    @SuppressLint("RestrictedApi")
    @Override
    public void onApplicationCreate(String processName) {
        /*
         * 如果应用有多个进程，而初始化逻辑只需要在主进程，可使用processName.equals(AppConfig.appId)判断，
         * 其中processName是当前启动的Applicaiton进程名，AppConfig.appId为应用包名（主进程）
         */
        if (processName.equals(AppConfig.appId)) {
            //初始化友盟统计
            UMConfigure.init(mApplication, xxx,"Umeng", xxx,xxx);
            //初始化极光推送
            JPushInterface.init(mApplication);
            //...
        }
    }
}
```

- 配置初始化类

```xml
//配置某个应用的BuildConfig常量
Map<String, String> getConfigMap_test() {
    return [
        //Application初始化类，使用,分割
        applicationInitClass :"\"com.openxu.english.ModuleXXXInitLogic,com.openxu.english.AppInitLogic\""
    ]
}
```

## 2.2 第一个页面

### XBaseActivity

### XBaseFragment


### 

## 2.3 网络请求

本框架采用**Retrofit+Okhttp+RxJava**作为网络请求类库，这也是当前最为流行的框架，功能强大，本框架对其进行统一封装。支持日志打印、进度显示、生命周期共存亡，请求错误统一封装，详情参照`com.openxu.core.http.error.NetErrorHandle`。需要注意的是，本框架网络请求必须在ViewModel中完成。

### get请求

```Java
NetworkManager.getInstance().build()
    //必须
    .viewModel(this)   //ViewModel对象
    .url(url)          //url，绝对路径或者相对路径都行，如果设置相对路径，请在conf_app.gradle中配置baseUrl
    .putParams(map)
    //可选
    .showDialog(true)   //是否显示进度条，默认true
    /**
     * 调用get请求，传入回调对象，泛型为返回的数据类型
     * 如果传递了泛型，一定要为ResponseCallback的构造方法传递对应的class对象
     * 如果不传递泛型，onSuccess(Object o)将接受Object类型，其实为XResponse类型
     * 注意：由于每个公司项目返回数据格式不同，如果需要对数据格式统一封装(状态码，错误信息等)，请参照XResponse，修改ParseDataFunction类重新解析数据，然后将泛型类型全部改为您封装的类型即可
     * 此处提供有偿解答，如有需求请私聊我
     */
    .doGet(new ResponseCallback<FanyiResult>(FanyiResult.class) {
        @Override
        public void onSuccess(FanyiResult result){
            XLog.d("请求数据结果："+result);
            fanyiData.setValue(result);
        }
        @Override
        public void onFail(String message) {
            //错误统一Toast处理
            super.onFail(message);
        }
    });
```

### post请求

## 工具类

### XToast

土司，支持自定义样式

```Java
XToast.error("错误-红色").show();
XToast.warning("警告-黄色").show();
XToast.success("成功-绿色").show();
XToast.info("信息提示-紫色").show();
XToast.normal("信息提示-灰色").show();
```

### XLog

logcat日志，支持日志代码定位，支持json格式打印

```Java
XLog.json("json格式化");
XLog.i("xxxxx");
XLog.v("xxxxx");
XLog.d("xxxxx");
XLog.w("xxxxx");
XLog.e("xxxxx");
```



# 3. 项目运行

`app`、`templete`、`module`目录下的模块都能运行，具体操作如下

## 3.1 独立模块运行

在开发项目时，根据业务将项目划分为多个模块，当我们在开发某个模块时可以通过运行**app_singlemd**对模块进行调试，节省编译和运行速度。

首先应该在**app_singlemd**的`build.gradle`中添加模块的依赖，比如下面我们要调试翻译模块：

```xml
apply from: '../app.gradle'
dependencies {
    implementation project(':module:module_fanyi')
}
```

然后在`com.openxu.single.SplashActivity`中设置需要调试模块的入口页面路由：

```Java
public class SplashActivity extends XBaseActivity<ActivitySplashBinding, XBaseViewModel> {
    @Override
    public int getContentView(Bundle savedInstanceState) {
        return R.layout.activity_splash;
    }
    @Override
    public void initView() {
        /**★★★设置调试模块入口页面路由*/
        XFragmentActivity.start(this,
                ARouter.getInstance().build(RouterPathFanyi.PAGE_FRAGMENT_FANYI));
    }
    @Override
    public void registObserve() {
    }
    @Override
    public void initData() {
    }
}
```

最后修改`conf_app.gradle`，运行**app_singlemd**即可：

```xml
ext {
    appTypeStr = "TYPE_ALONG"
    ...
```

