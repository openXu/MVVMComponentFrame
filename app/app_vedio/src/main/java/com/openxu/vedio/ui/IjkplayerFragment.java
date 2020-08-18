package com.openxu.vedio.ui;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.openxu.core.base.XBaseFragment;
import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.utils.XLog;
import com.openxu.vedio.Constant;
import com.openxu.vedio.R;
import com.openxu.vedio.RouterPathVedio;
import com.openxu.vedio.databinding.FragmentIjkplayerBinding;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.functions.Consumer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * https://github.com/Bilibili/ijkplayer
 *
 * ijkplayer的一些重要的视频返回码
 * int MEDIA_INFO_VIDEO_RENDERING_START = 3;//视频准备渲染
 * int MEDIA_INFO_BUFFERING_START = 701;//开始缓冲
 * int MEDIA_INFO_BUFFERING_END = 702;//缓冲结束
 * int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;//视频选择信息
 * int MEDIA_ERROR_SERVER_DIED = 100;//视频中断，一般是视频源异常或者不支持的视频类型。
 * int MEDIA_ERROR_IJK_PLAYER = -10000,//一般是视频源有问题或者数据格式不支持，比如音频不是AAC之类的
 * int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;//数据错误没有有效的回收
 *
 */

@Route(path = RouterPathVedio.PAGE_IJKPLAYER)
public class IjkplayerFragment extends XBaseFragment<FragmentIjkplayerBinding, XBaseViewModel> {
    boolean hasStarted = false;

    //由ijkplayer提供，用于播放视频，需要给他传入一个surfaceView
    IMediaPlayer mMediaPlayer;
    Map<String,String> mHeader;
    boolean  mEnableMediaCodec = false;   //是否开启硬解码

    @Override
    public int getContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_ijkplayer;
    }

    @Override
    public void initView() {
        XLog.v("1. 初始化Ijk init");
        //初始化播放器
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);

        binding.surfaceview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    //开始加载视频
                    if(mMediaPlayer != null){
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                    }
                    mMediaPlayer = createPlayer();
                    XLog.i("2. 设置监听");
                    mMediaPlayer.setOnBufferingUpdateListener(listener);
                    mMediaPlayer.setOnCompletionListener(listener);
                    mMediaPlayer.setOnErrorListener(listener);
                    mMediaPlayer.setOnInfoListener(listener);
                    mMediaPlayer.setOnPreparedListener(listener);
                    mMediaPlayer.setOnSeekCompleteListener(listener);
                    mMediaPlayer.setOnVideoSizeChangedListener(listener);
                    mMediaPlayer.setDisplay(binding.surfaceview.getHolder());
                    mMediaPlayer.setDataSource(getContext(), Uri.parse(Constant.rtspPath), mHeader);
                    XLog.i("3. 异步准备");
                    mMediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        binding.tvStart.setOnClickListener(view -> {
//            mMediaPlayer.reset();
//            mMediaPlayer.getDuration();
//            mMediaPlayer.stop();
//            mMediaPlayer.getCurrentPosition();
//            mMediaPlayer.seekTo(100);
//            mMediaPlayer.isPlaying();
            if (!hasStarted) {
                if (mMediaPlayer != null)
                    mMediaPlayer.start();
                hasStarted = true;
                binding.tvStart.setText("暂停");
            } else {
                if (mMediaPlayer != null)
                    mMediaPlayer.pause();
                binding.tvStart.setText("继续");
                hasStarted = false;
            }
        });
    }

    //创建一个新的player
    private IMediaPlayer createPlayer() {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();

//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 3000);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 1);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "islive", 1);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");//UDP
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024 * 16);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 50000);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_frame", 0);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "buffer_size", 1024*1600);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"reconnect",5);

//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 60);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 30);   //最大fps
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fps", 10);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV16);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max-buffer-size", 1024);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 3);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probsize", "4096");
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", "2000000");

//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
//
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "http-detect-range-support", 1);
//
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "min-frames", 100);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);  //进度条添加seekTo支持
//
//        ijkMediaPlayer.setVolume(1.0f, 1.0f);

        //设置是否开启硬解码
        //IjkPlayer支持硬解码和软解码， 软解码时不会旋转视频角度，这时需要你通过onInfo的what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED去获取角度，自己旋转画面。
        // 或者开启硬解硬解码，不过硬解码容易造成黑屏无声（硬件兼容问题），下面是设置硬解码相关的代码
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", mEnableMediaCodec ? 1 : 0);//开启硬解码1 开启: 0关闭
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", mEnableMediaCodec ? 1 : 0);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", mEnableMediaCodec ? 1 : 0);









        /**配置ijkplayer相应参数，解决卡顿、延迟*/

        // 支持硬解 1：开启 O:关闭
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", 1);
        // 设置播放前的探测时间 1,达到首屏秒开效果
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);

        /*播放延时的解决方案*/
        // 如果是rtsp协议，可以优先用tcp(默认是用udp)
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
        // 设置播放前的最大探测时间 （100未测试是否是最佳值）
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);
        // 每处理一个packet之后刷新io上下文
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
        // 需要准备好后自动播放
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
        // 不额外优化（使能非规范兼容优化，默认值0 ）
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fast", 1);
        // 是否开启预缓冲，一般直播项目会开启，达到秒开的效果，不过带来了播放丢帧卡顿的体验
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering",  0);
        // 自动旋屏
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
        // 处理分辨率变化
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
        // 最大缓冲大小,单位kb
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max-buffer-size", 0);
        // 默认最小帧数2
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 2);
        // 最大缓存时长
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,  "max_cached_duration", 3); //300
        // 是否限制输入缓存数，无限制收流
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,  "infbuf", 1);
        // 缩短播放的rtmp视频延迟在1s内
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
        // 播放前的探测Size，默认是1M, 改小一点会出画面更快
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 200); //1024L)
        // 播放重连次数
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"reconnect",5);
        // TODO:
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        // 设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48L);
        // 跳过帧 ？？
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_frame", 0);
        // 视频帧处理不过来的时候丢弃一些帧达到同步的效果
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5);

  /* 暂未使用
  // 超时时间，timeout参数只对http设置有效，若果你用rtmp设置timeout，ijkplayer内部会忽略timeout参数。rtmp的timeout参数含义和http的不一样。
  ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 10000000);
  // 因为项目中多次调用播放器，有网络视频，resp，本地视频，还有wifi上http视频，所以得清空DNS才能播放WIFI上的视频
  ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
  */




        return ijkMediaPlayer;
    }
    @Override
    public void registObserve() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onStop() {
        super.onStop();
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        IjkMediaPlayer.native_profileEnd();
    }


    public abstract class VideoPlayerListener implements IMediaPlayer.OnBufferingUpdateListener,
            IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener,
            IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnSeekCompleteListener {
    }
    private VideoPlayerListener listener = new VideoPlayerListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
            XLog.v("监听onBufferingUpdate"+i);
        }
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            XLog.v("监听onCompletion===");
        }
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            XLog.v("监听onError"+i+i1);
            return false;
        }
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            XLog.v("监听onInfo==="+i+i1);
            return false;
        }
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            XLog.v("监听onPrepared");
            // 视频宽高：360/240
            //视频解码后原本的Video size大小
            binding.surfaceview.getHolder().setFixedSize(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
            changeVideoSize();

           /* Observable.interval(2, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Throwable {
                            XLog.d("当前播放进度："+mMediaPlayer.getCurrentPosition()+"/"+mMediaPlayer.getDuration());
                        }
                    });*/
        }
        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            XLog.v("监听onSeekComplete");
        }
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            XLog.v("监听onVideoSizeChanged："+i+"   "+i1+"   "+i2+"   "+i3);
        }
    };

    //改变视频的尺寸自适应。
    public void changeVideoSize() {
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();

        Rect outSize = new Rect();
        getActivity().getWindowManager().getDefaultDisplay().getRectSize(outSize);
        float screenWith = outSize.right - outSize.left;
        float screenHight = outSize.bottom - outSize.top;

        int hight = (int)(screenWith/videoWidth * videoHeight);
        XLog.d("视频宽高："+videoWidth+"*"+videoHeight+"    "+"屏幕宽高："+screenWith+"*"+screenHight);
        XLog.v("重新计算高度:"+hight);
        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.surfaceview.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, R.id.rl);
        layoutParams.width = (int)screenWith;
        layoutParams.height = hight;
        binding.surfaceview.setLayoutParams(layoutParams);
    }


}
