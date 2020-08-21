package com.openxu.vedio.ui;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.openxu.core.base.XBaseActivity;
import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.utils.XLog;
import com.openxu.vedio.R;
import com.openxu.vedio.databinding.ActivityMediaplayerBinding;
import com.openxu.vedio.databinding.ActivityVedioviewBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MediaPlayerActivity extends XBaseActivity<ActivityMediaplayerBinding, XBaseViewModel> {
    private ImageView mStartAndStop;
    private MediaPlayer mMediaPlayer;
    private String mPath;
    private boolean isInitFinish = false;
    private SurfaceHolder mSurfaceHolder;
    @Override
    public int getContentView(Bundle savedInstanceState) {
        return R.layout.activity_mediaplayer;
    }

    @Override
    public void initView() {

        mMediaPlayer = new MediaPlayer();
        mSurfaceHolder = binding.surfaceview.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mMediaPlayer.setDisplay(holder);//给mMediaPlayer添加预览的SurfaceHolder
                setPlayVideo();//添加播放视频的路径
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });

//
    }
    private void setPlayVideo() {
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.test;
        try {
            mMediaPlayer.setDataSource(this, Uri.parse(uri));
            mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);//缩放模式
            mMediaPlayer.setLooping(true);//设置循环播放
            mMediaPlayer.prepareAsync();//异步准备
//            mMediaPlayer.prepare();//同步准备,因为是同步在一些性能较差的设备上会导致UI卡顿
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { //准备完成回调
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isInitFinish = true;
                    mp.start();
                    XLog.i("开始播放咯");
                }
            });

            //void setDataSource(String path) 通过一个具体的路径来设置MediaPlayer的数据源，path可以是本地的一个路径，也可以是一个网络路径
            //void setDataSource(Context context, Uri uri) 通过给定的Uri来设置MediaPlayer的数据源，这里的Uri可以是网络路径或是一个ContentProvider的Uri。
            //void setDataSource(MediaDataSource dataSource) 通过提供的MediaDataSource来设置数据源
            //void setDataSource(FileDescriptor fd) 通过文件描述符FileDescriptor来设置数据源
            //int getCurrentPosition() 获取当前播放的位置
            //int getAudioSessionId() 返回音频的session ID
            //int getDuration() 得到文件的时间
            //TrackInfo[] getTrackInfo() 返回一个track信息的数组
            //boolean isLooping () 是否循环播放
            //boolean isPlaying() 是否正在播放
            //void pause () 暂停
            //void start () 开始
            //void stop () 停止
            //void prepare() 同步的方式装载流媒体文件。
            //void prepareAsync() 异步的方式装载流媒体文件。
            //void reset() 重置MediaPlayer至未初始化状态。
            //void release () 回收流媒体资源。
            //void seekTo(int msec) 指定播放的位置（以毫秒为单位的时间）
            //void setAudioStreamType(int streamtype) 指定流媒体类型
            //void setLooping(boolean looping) 设置是否单曲循环
            //void setNextMediaPlayer(MediaPlayer next) 当 当前这个MediaPlayer播放完毕后，MediaPlayer next开始播放
            //void setWakeMode(Context context, int mode)：设置CPU唤醒的状态。
            //setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener listener) 网络流媒体的缓冲变化时回调
            //setOnCompletionListener(MediaPlayer.OnCompletionListener listener) 网络流媒体播放结束时回调
            //setOnErrorListener(MediaPlayer.OnErrorListener listener) 发生错误时回调
            //setOnPreparedListener(MediaPlayer.OnPreparedListener listener)：当装载流媒体完毕的时候回调。
            //setOnInfoListener(OnInfoListener l) 信息监听
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void registObserve() {

    }

    @Override
    public void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null){
            if (mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }



    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        List<String> list = new ArrayList<>();
        list.add("11");
        list.add("2");
        EventBus.getDefault().post(list);
        EventBus.getDefault().postSticky(list);

    }
    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(List<String> event) {
        XLog.i("收到消息："+event);
    }
}
