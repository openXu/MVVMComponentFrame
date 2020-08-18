
# 1. 准备工作

由于框架设计的特殊性，本框架没有打包上传到远程仓库，拉取源码到本地直接在框架上开发项目更加方便自友。
  
Star并Fork项目，然后拉取项目到本地，使用Android Studio打开项目即可开发。

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

## 2.2 页面

页面默认统一使用自封装的`TitleLayout`，基类中已经对其做沉侵式处理，当然你也可以使用其他标题栏。该部分主要关心三个内容，xml布局，Activity/Fragment处理页面逻辑调用viewmodel请求数据，viewmodel编写数据请求代码通知页面刷新。

### XBaseActivity\XBaseFragment

页面可继承XBaseActivity或者XBaseFragment，其区别就是XBaseActivity需要在清单文件注册，而XBaseFragment不需要。

```Java
//注册路由路径
@Route(path = RouterPathLogin.PAGE_LOGIN)
//继承XBaseActivity，第一个泛型为布局生成的binding类，第二个为该页面的ViewModel类，如果页面没有数据处理需求不需要ViewModel，可以传入XBaseViewModel
public class LoginActivity extends XBaseActivity<LoginActivityLoginBinding,  LoginActivityVM > {
    //返回布局id
    @Override
    public int getContentView(Bundle savedInstanceState) {
        return R.layout.login_activity_login;
    }
    @Override
    public void initView() {
        // 控件初始化，设置事件
        binding.tvLogin.setOnClickListener(v->{
            //调用viewmodel的登录方法
            viewModel.login(binding.etName.getText().toString().trim(), binding.etPsw.getText().toString().trim());
        });
        binding.tvRegist.setOnClickListener(v->{
            //路由到注册页，由于注册页是继承XBaseFragment，使用XFragmentActivity提供的路由方法
            XFragmentActivity.start(LoginActivity.this,
                    ARouter.getInstance().build(RouterPathLogin.PAGE_REGIST)
                            .withString("username", binding.etName.getText().toString().trim()));
        });
    }
    @Override
    public void registObserve() {
        // 为ViewModel中LiveData类型数据注册监听，当数据变化时更新UI (通常通过binding.setXXX)
        viewModel.userData.observe(this, user -> {
            XToast.success("恭喜"+user.getName()+"登成功").show();
            /**
             * 路由到主页面
             * 注意：该模块将登录、注册抽取为单独模块，是假设有多个项目共用登陆注册逻辑，但是每个项目的主页面不同，该怎么路由到各个项目的主页面去呢？
             * 可以将主页的路由清单放到library_core中，这样就可以在这里引用了，每个项目主页面虽然注册的路由路径一样，但是只能同时运行其中一个
             */
//            ARouter.getInstance().build(RouterPathCore.PAGE_MAIN).navigation();
        });
    }
    @Override
    public void initData() {
        // 通常调用viewModel获取数据
//        viewModel.init();
    }
}


@Route(path = RouterPathLogin.PAGE_REGIST)
public class RegistFragment extends XBaseFragment<LoginFragmentRegistBinding, RegistFragmentVM> {
    @Override
    public int getContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.login_fragment_regist;
    }
    @Override
    public void initView() {
        // 控件初始化，设置事件
    }
    @Override
    public void registObserve() {
        // 为ViewModel中LiveData类型数据注册监听，当数据变化时更新UI (通常通过binding.setXXX)
    }
    @Override
    public void initData() {
        // 通常调用viewModel获取数据
    }
}
```

### XBaseViewModel

ViewModel负责数据请求，并通知页面刷新

```Java
public class LoginActivityVM extends XBaseViewModel {
    public MutableLiveData<User> userData = new MutableLiveData<>();
	public LoginActivityVM(@NonNull Application application) {
        super(application);
    }
    //登录
    public void login(String name, String pwd){
        NetworkManager.getInstance().build()
                //必须
                .viewModel(this)   //ViewModel对象
                .url(LoginService.loginUrl)          //url，绝对路径或者相对路径都行，如果设置相对路径，请在conf_app.gradle中配置baseUrl
                .putParam("name", name)
                .putParam("pwd", pwd)
//                .putParams(map)
                //可选
                .showDialog(true)   //是否显示进度条，默认true
                .doGet(new ResponseCallback<User>(User.class) {
                    @Override
                    public void onSuccess(User result){
                        XLog.d("请求数据结果："+result);
                        //将结果设置给livedata，通知ui刷新
                        userData.setValue(result);
                    }
                    @Override
                    public void onFail(String message) {
                        //错误统一Toast处理
                        super.onFail(message);
                    }
                });
    }
}
```

### XBaseListActivity/XBaseListFragment

如果有需要，可以使用统一的list列表页面封装，可继承XBaseListActivity/XBaseListFragment

```Java
public class MainActivity extends XBaseListActivity<CoreActivityBaseListBinding, XBaseViewModel, String> {
    @Override
    protected void initListPageParams() {
        titleLayout.setTextcenter("列表页测试").show();  //设置标题
        itemLayout = R.layout.main_test_item;        //item布局id
        itemClickId.add(R.id.tv_item);
        binding.refreshLayout.setEnableRefresh(false);  //是否允许下拉刷新
        binding.refreshLayout.setEnableLoadMore(false);  //是否允许上拉加载更多
        //添加分割线
        binding.recyclerView.addItemDecoration(new CommandItemDecoration(mContext, LinearLayoutManager.VERTICAL,
                getResources().getColor(R.color.line_bg_color),
                (int)(getResources().getDimension(R.dimen.line_height))));
    }
    @Override
    protected void getListData() {
        //按道理此处应该调用viewmodel的方法请求数据，这里使用静态数据模拟
        List<String> data = new ArrayList<>();
        data.add("测试1");
        data.add("测试2");
        //刷新页面
        responseData(data);
    }
    /**条目点击*/
    @Override
    protected void onViewClick(int id, String data, int position) {
        super.onViewClick(id, data, position);
    }
    @Override
    protected void onItemClick(String data, int position) {
    }
    
    @Override
    public void registObserve() {
    }
}

```

### 路由



## 2.3 MVVM模式模板创建
> 该模板用于方便创建基于MVVM模式的Activity、Fragment，会自动创建ViewModel及layout文件，并自动在**AndroidManifest.xml**中注册。<br/>
> 需要注意的是，该模板默认包名为3级，如果在3级目录下创建，会自动创建**business**业务包，相关文件会生成在此目录下，你也可以修改业务包名。<br/>
> 如果选中>3级目录，则认为已经具备业务包，相关文件将会生成在最后一级目录中。<br/>
> 当然，你也可以在配置时指定Activity和ViewModel的包名<br/>

**导入模板**<br/>

将a_templates文件夹下的**MVVMTemplates**文件夹整个复制到android-studio\plugins\android\lib\templates\activities\目录下，重启Android Studio即可

**模板修改**<br/>
> 如有必要，可根据需求修改模板（FreeMarker语法），一般情况不要擅自修改

**创建Activity**

对应包下->new->Activity->MVVM 模板

![activity模板创建-1](a_image/activity模板创建-1.png)<br/>
![activity模板创建-2](a_image/activity模板创建-2.png)


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


### 异常处理

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

## 3.2 项目

`app`目录下都是`com.android.application`独立的项目，可以直接运行
