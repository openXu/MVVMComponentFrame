package com.openxu.core.zxing.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.openxu.core.R;
import com.openxu.core.RouterPathCore;
import com.openxu.core.utils.XLog;
import com.openxu.core.utils.toasty.XToast;
import com.openxu.core.zxing.xuedaojie.qrcodelib.camera.CameraManager;
import com.openxu.core.zxing.xuedaojie.qrcodelib.common.ActionUtils;
import com.openxu.core.zxing.xuedaojie.qrcodelib.common.QrUtils;
import com.openxu.core.zxing.xuedaojie.qrcodelib.decoding.CaptureActivityHandler;
import com.openxu.core.zxing.xuedaojie.qrcodelib.decoding.InactivityTimer;
import com.openxu.core.zxing.xuedaojie.qrcodelib.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;


/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
@Route(path = RouterPathCore.PAGE_ZXING)
public class DecoderActivity extends Activity implements Callback {
    @Autowired(name = INFO_INTENT_KEY)
    CaptureInfo info;
    public static final String INFO_INTENT_KEY = "info";
    public static final int BAR_SCAN_REQUEST_CODE = 9999;
    //BAR_SCAN_RESULT_CODE
    public static final String EXTRA_KEY = "qr_code";
    public static final String EXTRA_TYPE_KEY = "code_type";   //扫码类型
    //
    public static final int TYPE_BARCODE = 1;   //条形码
    public static final int TYPE_QRRCODE = 2;   //二维码
    public static final int TYPE_OTHERCODE = 3;   //其他吗

    private static final String TAG = "DecoderActivity";
    private static final int REQUEST_PERMISSION_CAMERA = 1000;
    private static final int REQUEST_PERMISSION_PHOTO = 1001;
    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    private DecoderActivity mActivity;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean vibrate;
    private boolean flashLightOpen = false;
    private TextView tvLocalScan;
    private ImageView imageButton_back;
    private ImageView iv_splash;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        mActivity = this;
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        CameraManager.init(getApplication());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_PERMISSION_CAMERA);
            }
        }
        // 保持Activity处于唤醒状态
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.title_bar_color));
        }
        setContentView(R.layout.core_activity_qr_camera);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "xxxxxxxxxxxxxxxxxxxonResume");
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        final AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "xxxxxxxxxxxxxxxxxxxonPause");
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        if (iv_splash != null) {
            iv_splash.setImageResource(R.mipmap.qrcode_icon_splash_down);
        }

        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // 未获得Camera权限
                new AlertDialog.Builder(mActivity)
                        .setTitle("提示")
                        .setMessage("请在系统设置中为App开启摄像头权限后重试")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mActivity.finish();
                            }
                        })
                        .show();
            }
        } else if (grantResults.length > 0 && requestCode == REQUEST_PERMISSION_PHOTO) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(mActivity)
                        .setTitle("提示")
                        .setMessage("请在系统设置中为App中开启文件权限后重试")
                        .setPositiveButton("确定", null)
                        .show();
            } else {
                ActionUtils.startActivityForGallery(mActivity, ActionUtils.PHOTO_REQUEST_GALLERY);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && data != null
                && requestCode == ActionUtils.PHOTO_REQUEST_GALLERY) {
            Uri inputUri = data.getData();
            String path = null;

            if (URLUtil.isFileUrl(inputUri.toString())) {
                // 小米手机直接返回的文件路径
                path = inputUri.getPath();
            } else {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(inputUri, proj, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                }
            }
            if (!TextUtils.isEmpty(path)) {
                Result result = QrUtils.decodeImage(path);
                if (result != null) {
                    handleDecode(result, null);
                    Log.e("TAG", "竟然走这里？？");
                } else {
                    new AlertDialog.Builder(DecoderActivity.this)
                            .setTitle("提示")
                            .setMessage("此图片无法识别")
                            .setPositiveButton("确定", null)
                            .show();
                }
            } else {
                XToast.warning("图片路径未找到");
            }
        }
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {

        try {
            inactivityTimer.onActivity();
            playBeepSoundAndVibrate();

            String resultString = result.getText();
            int CODE_TYPE = -1;
            String mBarcodeFormat = result.getBarcodeFormat().toString();  //拍码后返回的编码格式
            if ("DATA_MATRIX".equals(mBarcodeFormat)) {
                CODE_TYPE = TYPE_OTHERCODE;  //其它码
            } else if ("QR_CODE".equals(mBarcodeFormat)) {
                CODE_TYPE = TYPE_QRRCODE;   //二维码
            } else {
                CODE_TYPE = TYPE_BARCODE;  //一维码
            }
            if (resultString.equals("")) {
                Toast.makeText(DecoderActivity.this, "扫描失败", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: 2020/3/3 需求：不允许汉字和非法字符
                XLog.e("打印结果====" + resultString);
                Intent data1 = getIntent();
                //设置识别点位（黄点）
                data1.putExtra(EXTRA_KEY, resultString);
                data1.putExtra(EXTRA_TYPE_KEY, CODE_TYPE);
                setResult(RESULT_OK, data1);
                finish();
                //重新进行预览
//            restartPreview();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(DecoderActivity.this, "扫描失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);
            try {
                AssetFileDescriptor file = getResources().openRawResourceFd(
                        R.raw.qrcode_beep);
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    protected void initView() {

        try {
            XLog.e("info===" + info);
            if (info != null) {
                try {
                    ((TextView) findViewById(R.id.textNotice0)).setText(info.getInfo0());
                    ((TextView) findViewById(R.id.textNotice1)).setText(info.getInfo1());
                    ((TextView) findViewById(R.id.textNotice2)).setText(info.getInfo2());
                    ((TextView) findViewById(R.id.tvTitle)).setText(info.getTitle());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        tvLocalScan = (TextView) findViewById(R.id.tvLocalScan);
        //默认不能识别本地二维码
        tvLocalScan.setVisibility(info.isDecodeLoacal() ? View.VISIBLE : View.GONE);
        imageButton_back = (ImageView) findViewById(R.id.capture_imageview_back);
        imageButton_back.setOnClickListener(v -> finish());
        iv_splash = (ImageView) findViewById(R.id.iv_splash);
        iv_splash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashLightOpen) {
                    iv_splash.setImageResource(R.mipmap.qrcode_icon_splash_up);
                } else {
                    iv_splash.setImageResource(R.mipmap.qrcode_icon_splash_down);
                }
                toggleFlashLight();
            }
        });
        tvLocalScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    /**
     * 切换散光灯状态
     */
    public void toggleFlashLight() {
        if (flashLightOpen) {
            setFlashLightOpen(false);
        } else {
            setFlashLightOpen(true);
        }
    }

    /**
     * 打开相册
     */
    public void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_PHOTO);
        } else {
            ActionUtils.startActivityForGallery(mActivity, ActionUtils.PHOTO_REQUEST_GALLERY);
        }
    }

    /**
     * 当前散光灯是否打开
     *
     * @return
     */
    public boolean isFlashLightOpen() {
        return flashLightOpen;
    }

    /**
     * 设置闪光灯是否打开
     *
     * @param open
     */
    public void setFlashLightOpen(boolean open) {
        if (flashLightOpen == open) return;

        flashLightOpen = !flashLightOpen;
        CameraManager.get().setFlashLight(open);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    protected void setViewfinderView(ViewfinderView view) {
        viewfinderView = view;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    protected void restartPreview() {
        // 当界面跳转时 handler 可能为null
        if (handler != null) {
            Message restartMessage = Message.obtain();
            restartMessage.what = R.id.restart_preview;
            handler.handleMessage(restartMessage);
        }
    }

}