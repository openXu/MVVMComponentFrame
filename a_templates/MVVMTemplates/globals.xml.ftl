<?xml version="1.0"?>
<globals>

    <global id="hasNoActionBar" type="boolean" value="false" />
    <global id="parentActivityClass" value="" />
    <global id="excludeMenu" type="boolean" value="true" />
    <global id="generateActivityTitle" type="boolean" value="false" />
    <!-- 当前包名 -->
    <global id="packageName" type="string" value="com.mycompany.myapp" />

    <#-- packageName：当前包名
         contentPackage:Activity和viewmodel最后生成的目录， packageName >3级的包名最后一级为业务包名，如果当前包名<=3级将自动创建业务包business
    	 appPackageName：module包名（应用程序包名）为了正确的导入R文件，这里默认取3级作为appPackageName
    	 suffix：模块后缀名，避免资源冲突
    -->
    <#assign packages="${packageName}"?split(".")/> 
    <#if (packages?size>=4) >
		<global id="contentPackage" type="string" value="${packageName}" />
		<global id="appPackageName" type="string" value="${packages[0]}.${packages[1]}.${packages[2]}" />
		<global id="suffix" type="string" value="${packages[2]}" />
		<global id="hasBusinessPackageName" type="boolean" value="true" />
	<#else>
		<global id="contentPackage" type="string" value="${packageName}.business" />
		<global id="appPackageName" type="string" value="${packageName}" />
		<global id="suffix" type="string" value="${packages[packages?size-1]}" />
		<global id="hasBusinessPackageName" type="boolean" value="false" />
	</#if>

    <#include "../common/common_globals.xml.ftl" />
</globals>

<#macro fileHeader>
/**
 * Author: openXu
 * Time: ${.now?string["yyyy/MM/dd HH:mm"]}
 * class:
 * Description:
 * Update:
 */
</#macro>