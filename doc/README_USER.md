
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

## 2.1 


 