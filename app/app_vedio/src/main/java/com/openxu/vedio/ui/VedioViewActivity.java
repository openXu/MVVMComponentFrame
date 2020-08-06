package com.openxu.vedio.ui;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;

import com.openxu.core.base.XBaseActivity;
import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.utils.XLog;
import com.openxu.vedio.Constant;
import com.openxu.vedio.R;
import com.openxu.vedio.databinding.ActivityVedioviewBinding;

public class VedioViewActivity extends XBaseActivity<ActivityVedioviewBinding, XBaseViewModel> {

    @Override
    public int getContentView(Bundle savedInstanceState) {
        return R.layout.activity_vedioview;
    }

    @Override
    public void initView() {
        //本地视频
//        String uri = "android.resource://" + getPackageName() + "/" + R.raw.test;
//        binding.videoView.setVideoURI(Uri.parse(uri));

        binding.videoView.setVideoURI(Uri.parse(Constant.rtspPath));
        MediaController mediaController = new MediaController(this);
        binding.videoView.setMediaController(mediaController);
//        binding.videoView.setVideoPath(file.getAbsolutePath());//设置视频文件
        binding.videoView.setOnPreparedListener(mp -> {
            XLog.i("视频加载完成,准备好播放视频");
            //播放视频时一定要确定setOnPreparedListener返回了已经视频准备完成的回调.
            binding.videoView.start();
            //暂停视频 mVideoView.pause();
            //mVideoView.getDuration();//获取视频的总时长
            //mVideoView.getCurrentPosition();//获取视频的当前播放位置
            //mVideoView.stopPlayback();//停止播放视频,并且释放
            //mVideoView.suspend();//在任何状态下释放媒体播放器
            //mVideoView.canPause();　　//是否可以暂停
            //mVideoView.canSeekBackward();　　//视频是否可以向后调整播放位置
            //mVideoView.canSeekForward();　　//视频是否可以向前调整播放位置
            //mVideoView.getBufferPercentage();　　//获取视频缓冲百分比
            //mVideoView.resolveAdjustedSize();　　//获取自动解析后VideoView的大小
            //mVideoView.resume();　　//重新开始播放
            //mVideoView.isPlaying();　　//是否在播放中
            //mVideoView.setMediaController();　　//设置多媒体控制器
            //mVideoView.onKeyDown();　　//发送物理按键值
            //mVideoView.setVideoURI(); 　　//播放网络视频,参数为网络地址
        });
        binding.videoView.setOnCompletionListener(mp -> XLog.i("视频播放完成"));
        binding.videoView.setOnErrorListener((mp, what, extra) -> {
            XLog.e("视频播放异常"+mp);
            XLog.e("视频播放异常"+what);
            XLog.e("视频播放异常"+extra);
            //如果方法处理了错误，则为true；否则为false。返回false或根本没有OnErrorListener，将导致调用OnCompletionListener。
            return false;
        });
        binding.videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                //信息回调
//                what 对应返回的值如下
//                public static final int MEDIA_INFO_UNKNOWN = 1;  媒体信息未知
//                public static final int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700; 媒体信息视频跟踪滞后
//                public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3; 媒体信息\视频渲染\开始
//                public static final int MEDIA_INFO_BUFFERING_START = 701; 媒体信息缓冲启动
//                public static final int MEDIA_INFO_BUFFERING_END = 702; 媒体信息缓冲结束
//                public static final int MEDIA_INFO_NETWORK_BANDWIDTH = 703; 媒体信息网络带宽（703）
//                public static final int MEDIA_INFO_BAD_INTERLEAVING = 800; 媒体-信息-坏-交错
//                public static final int MEDIA_INFO_NOT_SEEKABLE = 801; 媒体信息找不到
//                public static final int MEDIA_INFO_METADATA_UPDATE = 802; 媒体信息元数据更新
//                public static final int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901; 媒体信息不支持字幕
//                public static final int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902; 媒体信息字幕超时

                return false; //如果方法处理了信息，则为true；如果没有，则为false。返回false或根本没有OnInfoListener，将导致丢弃该信息。
            }
        });
    }

    @Override
    public void registObserve() {

    }

    @Override
    public void initData() {

    }
}
