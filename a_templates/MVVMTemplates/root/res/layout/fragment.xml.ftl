<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <data>
        <variable name="cat" type="String"/>
    <#if needVM>
        <variable name="viewModel" type="${contentPackage}<#if ("${VMPackageName}"?length>0)>.${VMPackageName}</#if>.${VMClass}"/>
    <#else>
        <variable name="viewModel" type="com.openxu.core.base.XBaseViewModel"/>
    </#if>
    </data>
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">
        <com.openxu.core.view.TitleLayout
            android:id="@id/titleLayout"
            style="@style/TitleDefStyle"
            app:textcenter="标题" />
    </LinearLayout>
</layout>

