package ${contentPackage}<#if ("${vPackageName}"?length>0)>.${vPackageName}</#if>;
import android.os.Bundle;
import ${appPackageName}.databinding.${suffix?cap_first}Fragment${pageName}Binding;
<#if needVM>
    <#if ("${vPackageName}"?length>0) || ("${VMPackageName}"?length>0)>
import ${contentPackage}<#if ("${VMPackageName}"?length>0)>.${VMPackageName}</#if>.${VMClass};
    </#if>
<#else>
import com.openxu.core.base.XBaseViewModel;
</#if>
import com.openxu.core.base.XBaseFragment;
import androidx.annotation.Nullable;
import com.alibaba.android.arouter.facade.annotation.Route;
import ${appPackageName}.R;
/**
 * Author: openXu
 * Time: ${.now?string["yyyy/MM/dd HH:mm"]}
 * class: ${fragmentClass}
 * Description:
 * Update:
 */
@Route(path = RouterPath${suffix?cap_first}.PAGE_${pageName?upper_case})
public class ${fragmentClass} extends XBaseFragment<${suffix?cap_first}Fragment${pageName}Binding, <#if needVM> ${VMClass} <#else>XBaseViewModel</#if>> {

    @Override
    public int getContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.${suffix}_fragment_${classToResource(fragmentClass)};

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
