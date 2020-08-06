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
                    XLog.i("3. 设置SurfaceView，设置视频源 "+ Constant.rtmpPath);
                    mMediaPlayer.setDisplay(binding.surfaceview.getHolder());
                    mMediaPlayer.setDataSource(getContext(), Uri.parse(Constant.rtmpPath), mHeader);
                    XLog.i("4. 异步准备");
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
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "http-detect-range-support", 1);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "min-frames", 100);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        
        ijkMediaPlayer.setVolume(1.0f, 1.0f);

        //设置是否开启硬解码
        //IjkPlayer支持硬解码和软解码， 软解码时不会旋转视频角度，这时需要你通过onInfo的what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED去获取角度，自己旋转画面。
        // 或者开启硬解硬解码，不过硬解码容易造成黑屏无声（硬件兼容问题），下面是设置硬解码相关的代码
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", mEnableMediaCodec ? 1 : 0);//开启硬解码1 开启: 0关闭
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", mEnableMediaCodec ? 1 : 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", mEnableMediaCodec ? 1 : 0);
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
        int screenWith = outSize.right - outSize.left;
        int screenHight = outSize.bottom - outSize.top;

        int hight = screenWith/videoWidth * videoHeight;
        XLog.d("视频宽高："+videoWidth+"*"+videoHeight+"    "+"屏幕宽高："+screenWith+"*"+screenHight);
        XLog.v("重新计算高度:"+hight);
        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.surfaceview.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, R.id.rl);
        layoutParams.width = screenWith;
        layoutParams.height = hight;
        binding.surfaceview.setLayoutParams(layoutParams);
    }


}
