package ${contentPackage}<#if ("${VMPackageName}"?length>0)>.${VMPackageName}</#if>;
import ${getMaterialComponentName('androidx.annotation.NonNull', useAndroidX)};
import com.openxu.core.base.XBaseViewModel;
import android.app.Application;

/**
 * Author: openXu
 * Time: ${.now?string["yyyy/MM/dd HH:mm"]}
 * class: ${VMClass}
 * Description:
 * Update:
 */
public class ${VMClass} extends XBaseViewModel {

	public ${VMClass}(@NonNull Application application) {
        super(application);
    }

}
