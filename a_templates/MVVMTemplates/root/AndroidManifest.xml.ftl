<manifest xmlns:android="http://schemas.android.com/apk/res/android"
	package="${appPackageName}">

    <application>

        <#if isLauncher >
             <activity android:name="${contentPackage}<#if ("${vPackageName}"?length>0)>.${vPackageName}</#if>.${activityClass}">
                    <intent-filter>
                        <action android:name="android.intent.action.MAIN" />
                        <category android:name="android.intent.category.LAUNCHER" />
                    </intent-filter>
             </activity>
        <#else>
           <activity android:name="${contentPackage}<#if ("${vPackageName}"?length>0)>.${vPackageName}</#if>.${activityClass}"/>
        </#if>
        
    </application>
</manifest>
