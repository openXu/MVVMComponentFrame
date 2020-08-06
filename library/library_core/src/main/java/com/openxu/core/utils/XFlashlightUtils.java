package com.openxu.core.utils;

import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;

import static android.hardware.Camera.Parameters.FLASH_MODE_OFF;
import static android.hardware.Camera.Parameters.FLASH_MODE_TORCH;

/**
 * Author: openXu
 * Time: 2019/3/14 11:58
 * class: FlashlightUtils
 * Description: 闪光灯工具
 *
 * isFlashlightEnable : 判断设备是否支持闪光灯
 * isFlashlightOn     : 判断闪光灯是否打开
 * setFlashlightStatus: 设置闪光灯状态
 * destroy            : 销毁
 */
public final class XFlashlightUtils {

    private static Camera         mCamera;
    private static SurfaceTexture mSurfaceTexture;

    private XFlashlightUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Return whether the device supports flashlight.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFlashlightEnable() {
        return XUtils.getApp()
                .getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    /**
     * Return whether the flashlight is working.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFlashlightOn() {
        if (!init()) return false;
        Camera.Parameters parameters = mCamera.getParameters();
        return FLASH_MODE_TORCH.equals(parameters.getFlashMode());
    }

    /**
     * Turn on or turn off the flashlight.
     *
     * @param isOn True to turn on the flashlight, false otherwise.
     */
    public static void setFlashlightStatus(final boolean isOn) {
        if (!init()) return;
        final Camera.Parameters parameters = mCamera.getParameters();
        if (isOn) {
            if (!FLASH_MODE_TORCH.equals(parameters.getFlashMode())) {
                try {
                    mCamera.setPreviewTexture(mSurfaceTexture);
                    mCamera.startPreview();
                    parameters.setFlashMode(FLASH_MODE_TORCH);
                    mCamera.setParameters(parameters);
                } catch (IOException e) {
                    XLog.e("FlashlightUtils", "setFlashlightStatusOn: "+e);
                }
            }
        } else {
            if (!FLASH_MODE_OFF.equals(parameters.getFlashMode())) {
                parameters.setFlashMode(FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            }
        }
    }

    /**
     * Destroy the flashlight.
     */
    public static void destroy() {
        if (mCamera == null) return;
        mCamera.release();
        mSurfaceTexture = null;
        mCamera = null;
    }

    private static boolean init() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open(0);
                mSurfaceTexture = new SurfaceTexture(0);
            } catch (Throwable t) {
                XLog.e("FlashlightUtils", "init failed: "+t);
                return false;
            }
        }
        if (mCamera == null) {
            XLog.e("FlashlightUtils", "init failed.");
            return false;
        }
        return true;
    }
}