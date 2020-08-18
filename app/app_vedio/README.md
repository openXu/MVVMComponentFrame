https://blog.csdn.net/weixin_34306593/article/details/88676946

# Vitamio

到官网或者github下载vitamio资源
官网地址:https://www.vitamio.org/ (最新版本5.0.0,但是官网很难打开…)
github地址:https://github.com/yixia/VitamioBundle (版本4.2.2)


# ijkplayer

[ijkplayer](https://github.com/bilibili/ijkplayer)是B站开源的一个基于FFmpeg的轻量级Android/iOS视频播放器。FFmpeg的是全球领先的多媒体框架，能够解码，编码， 转码，复用，解复用，流，过滤器和播放大部分的视频格式。它提供了录制、转换以及流化音视频的完整解决方案。它包含了非常先进的音频/视频编解码库libavcodec，为了保证高可移植性和编解码质量，libavcodec里很多code都是从头开发的。

android版本最低minSdkVersion 21

## 环境搭建

[下载安装sdk](https://www.androiddevtools.cn/)
[Android中文社区](http://www.android-studio.org/)

[ndk](https://developer.android.google.cn/ndk/downloads)

下载SDK Tools对应的Linux下的压缩包`android-sdk_r24.4.1-linux.tar`、ndk压缩包`android-ndk-r15c-linux-x86_64.zip`将其移动到ubuntu文件夹下：
```xml
#### ubuntu配置sdk\ndk
root@Node3-fzy-> cd soft/
root@Node3-fzy-> mkdir android
root@Node3-fzy-> cd android/
root@Node3-fzy-> mkdir sdk    
root@Node3-fzy-> mkdir ndk
#### 将windows上下载的压缩包拖到对应文件夹中
#### 解压
root@Node3-fzy-> cd sdk/
root@Node3-fzy-> tar zxvf android-sdk_r24.4.1-linux.tar
root@Node3-fzy-> cd ..
root@Node3-fzy-> cd ndk/
root@Node3-fzy-> tar zxvf android-ndk-r15c-linux-x86_64.zip
#### 配置环境
root@Node3-fzy-> vim /root/.bashrc
       export ANDROID_SDK=/soft/android/sdk/android-sdk-linux
       export PATH=$ANDROID_SDK/tools:$PATH
       export PATH=$ANDROID_SDK/platform-tools:$PATH
       export ANDROID_NDK=/soft/android/ndk/android-ndk-r15c
       export PATH=$ANDROID_NDK:$PATH
root@Node3-fzy-> source /root/.bashrc
#### 检查环境配置
root@Node3-fzy-> ndk-build -v
#### 安装git 、 yarm
root@Node3-fzy-> apt-get install yasm
root@Node3-fzy-> apt-get install git
```

## 编译

```xml
#### 1. 拉取源码
root@Node3-fzy-> git clone https://github.com/Bilibili/ijkplayer.git ijkplayer-android
root@Node3-fzy-> cd ijkplayer-android
#### 2. 创建并切换分支
root@Node3-fzy-> git checkout -B latest k0.8.8
#### 3. 初始化，会拉取一些依赖库包括ffmpeg，大概两三百兆
root@Node3-fzy-> ./init-android.sh

#### ★拉取慢解决办法1：为github配置hosts，这种方式没有成功，请看下面方法2
        == pull ffmpeg base ==
#### 配置github hosts( https://www.ipaddress.com/上搜索下面两个网址)
root@Node3-fzy-> vim /etc/hosts
        199.232.69.194  github.global.ssl.fastly.net  
        140.82.114.3  github.com   
#### 刷新DNS缓存（windows : ipconfig /flushdns）
root@Node3-fzy-> /etc/init.d/networking restart

#### ★★★拉取慢解决办法2：先将https://github.com/Bilibili/FFmpeg.git导入到码云，然后修改init-android.sh脚本
    IJK_FFMPEG_UPSTREAM=https://gitee.com/xuOpen/FFmpeg.git
    IJK_FFMPEG_FORK=https://gitee.com/xuOpen/FFmpeg.git

#### 4. 进入config目录，指定编译的格式与配置类
root@Node3-fzy-> cd config/
#### module-default.sh是默认配置，编译出来的so文件会比较大，可以使用修改了的module-little.sh
root@Node3-fzy-> vim module-lite.sh
    # 1.add by openxu for rtsp              避免出现缺少ID为8的codec的错误
    export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-decoder=mjpeg"
    export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-demuxer=mjpeg"

    export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-demuxer=rtsp"   打开rtsp音视频分离器
    export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-demuxer=sdp"
    export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-demuxer=rtp"
    # 3.update enable by openxu for rtsp
    export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-protocol=rtp"   打开rtsp协议
    export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-protocol=tcp"

#### 5. 指定配置为module-lite.sh
root@Node3-fzy-> rm module.sh

root@Node3-fzy-> ln -s module-lite.sh module.sh

#### 6. 支持 https：ijkplayer 默认不支持 https 链接，需要运行以下命令使其支持
./init-android-openssl.sh

#### 7. 进入android/contrib 目录，编译ffmpeg
root@Node3-fzy-> cd ..
root@Node3-fzy-> cd android/contrib/
root@Node3-fzy-> ./compile-ffmpeg.sh clean  //清除旧的编译记录与文件
root@Node3-fzy-> ./compile-ffmpeg.sh all    //编译ffmpeg，all：编译所有版本的so库，如果是特定CPU架构就跟cpu架构，比如：./compile-ffmpeg.sh armv7a

#### 问题：
    compile-ffmpeg.sh 不知道当前系统安装的 NDK 路径导致
    ANDROID_NDK=
    You must define ANDROID_NDK before starting.
    They must point to your NDK directories.
    解决：可以通过在 ijkplayer/android/contrib/tools/do-compile-ffmpeg.sh 文件的开头定义 NDK 路径来解决
    ANDROID_NDK=/usr/android/android-ndk-r14b   之后，重新运行命令即可

#### 问题：
    ANDROID_NDK=/soft/android/ndk/android-ndk-r15c
    IJK_NDK_REL=15.2.4203891
    You need the NDKr10e or later
    解决：下载android-ndk-r10e-linux-x86_64.zip解压，重新配置上面的do-compile-ffmpeg.sh 文件中的ANDROID_NDK


#### 8.编译 ijkplayer，报错的话一样需要在 compile-ijk.sh 文件头部定义 Android SDK 和 NDK 的路径
root@Node3-fzy-> cd ..
root@Node3-fzy-> vim compile-ijk.sh
        ANDROID_NDK=/soft/android/ndk/android-ndk-r10e
        ANDROID_SDK=/soft/android/ndk/android-sdk-linux
root@Node3-fzy-> ./compile-ijk.sh all
    
#### 编译成功后，在/soft/android/ijkplayer-android/android/目录下有一个ijkplayer文件夹，导出该文件夹




```




