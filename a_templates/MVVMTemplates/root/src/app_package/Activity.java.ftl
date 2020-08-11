package ${contentPackage}<#if ("${vPackageName}"?length>0)>.${vPackageName}</#if>;
import android.os.Bundle;
import ${appPackageName}.databinding.${suffix?cap_first}Activity${pageName}Binding;
<#if needVM>
    <#if ("${vPackageName}"?length>0) || ("${VMPackageName}"?length>0)>
        import ${contentPackage}<#if ("${VMPackageName}"?length>0)>.${VMPackageName}</#if>.${VMClass};
    </#if>
<#else>
import com.openxu.core.base.XBaseViewModel;
</#if>
import com.openxu.core.base.XBaseActivity;
import com.alibaba.android.arouter.facade.annotation.Route;
import ${appPackageName}.R;
/**
 * Author: openXu
 * Time: ${.now?string["yyyy/MM/dd HH:mm"]}
 * class: ${activityClass}
 * Description:
 * Update:
 */
@Route(path = RouterPath${suffix?cap_first}.PAGE_${pageName?upper_case})
public class ${activityClass} extends XBaseActivity<${suffix?cap_first}Activity${pageName}Binding, <#if needVM> ${VMClass} <#else>XBaseViewModel</#if>> {


    @Override
    public int getContentView(Bundle savedInstanceState) {
        return R.layout.${suffix}_${activityToLayout(pageName)};
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
