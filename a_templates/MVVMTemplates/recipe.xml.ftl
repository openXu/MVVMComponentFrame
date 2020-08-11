<?xml version="1.0"?>
<recipe>

    <dependency mavenUrl="com.android.support:appcompat-v7:${buildApi}.+"/>
    <dependency mavenUrl="com.android.support.constraint:constraint-layout:+" />
    <dependency mavenUrl="android.arch.lifecycle:extensions:1.+"/>

<#if needActivity>
  <merge from="root/AndroidManifest.xml.ftl"
           to="${escapeXmlAttribute(manifestOut)}/AndroidManifest.xml" />
</#if>

 <#if needActivity>
        <instantiate from="root/src/app_package/Activity.${ktOrJavaExt}.ftl"
        to="${projectOut}/src/main/java/${slashedPackageName(((vPackageName?length)>0)?string(contentPackage+"."+vPackageName,contentPackage))}/${activityClass}.${ktOrJavaExt}" />
        <open file="${projectOut}/src/main/java/${slashedPackageName(((vPackageName?length)>0)?string(contentPackage+"."+vPackageName,contentPackage))}/${activityClass}.${ktOrJavaExt}" />
   </#if>
    <#if needFragment>
        <instantiate from="root/src/app_package/Fragment.${ktOrJavaExt}.ftl"
        to="${projectOut}/src/main/java/${slashedPackageName(((vPackageName?length)>0)?string(contentPackage+"."+vPackageName,contentPackage))}/${fragmentClass}.${ktOrJavaExt}" />
        <open file="${projectOut}/src/main/java/${slashedPackageName(((vPackageName?length)>0)?string(contentPackage+"."+vPackageName,contentPackage))}/${fragmentClass}.${ktOrJavaExt}" />
    </#if>

<#if needVM>
    <instantiate from="root/src/app_package/ViewModel.${ktOrJavaExt}.ftl"
    to="${projectOut}/src/main/java/${slashedPackageName(((VMPackageName?length)>0)?string(contentPackage+"."+VMPackageName,contentPackage))}/${VMClass}.${ktOrJavaExt}" />
    <open file="${projectOut}/src/main/java/${slashedPackageName(((VMPackageName?length)>0)?string(contentPackage+"."+VMPackageName,contentPackage))}/${VMClass}.${ktOrJavaExt}" />
</#if>
<#if false>
    <instantiate from="root/res/layout/activity.xml.ftl"
    to="${escapeXmlAttribute(resOut)}/layout/${escapeXmlAttribute(
    activityToLayout(pageName)?substring(0,activityToLayout(pageName)?index_of('_')+1)
    +suffix+
    activityToLayout(pageName)?substring(activityToLayout(pageName)?index_of("_"),activityToLayout(pageName)?length)
    )}.xml" />
</#if>

<#if needLayout>
    <#if needActivity>
            <instantiate from="root/res/layout/activity.xml.ftl"
            to="${escapeXmlAttribute(resOut)}/layout/${escapeXmlAttribute(suffix+'_'+activityToLayout(pageName))}.xml" />
    </#if>
    <#if needFragment>
      <#assign flayout1="${suffix+'_'+activityToLayout(pageName)}"?replace("activity_", "fragment_")/>
      <#assign flayout2="${suffix+'_fragment_'+classToResource(fragmentClass)}"/>
      <instantiate from="root/res/layout/fragment.xml.ftl"
            to="${escapeXmlAttribute(resOut)}/layout/${flayout2}.xml" />
    </#if>
</#if>

</recipe>
