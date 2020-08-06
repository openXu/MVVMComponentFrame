package com.openxu.core.zxing.xuedaojie.qrcodelib.view;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.openxu.core.R;

import java.util.Collection;
import java.util.HashSet;

import static android.hardware.camera2.params.TonemapCurve.POINT_SIZE;


/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

    public static int RECT_OFFSET_X; // 扫描区域偏移量 默认位于屏幕中间
    public static int RECT_OFFSET_Y;

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final int OPAQUE = 0xFF;
    private static long ANIMATION_DELAY = 10L;


    private final Paint paint;
    private final int maskColor;
    private final int resultColor;
    private final int frameColor;
    private final int laserColor;
    private final int resultPointColor;
    private final int angleColor;
    private final Bitmap scanLight;
    private String hint;
    private int hintColor;
    private String errorHint;
    private int errorHintColor;
    private boolean showPossiblePoint;
    private Bitmap resultBitmap;
    private int scannerAlpha;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;

    private float translateY = 5f;
    private int cameraPermission = PackageManager.PERMISSION_DENIED;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.qr_ViewfinderView);
        angleColor = typedArray.getColor(R.styleable.qr_ViewfinderView_qr_angleColor, Color.WHITE);
        hint = typedArray.getString(R.styleable.qr_ViewfinderView_qr_hint);
        hintColor = typedArray.getColor(R.styleable.qr_ViewfinderView_qr_textHintColor, Color.GRAY);
        errorHint = typedArray.getString(R.styleable.qr_ViewfinderView_qr_errorHint);
        errorHintColor = typedArray.getColor(R.styleable.qr_ViewfinderView_qr_textErrorHintColor, Color.WHITE);
        showPossiblePoint = typedArray.getBoolean(R.styleable.qr_ViewfinderView_qr_showPossiblePoint, false);

        RECT_OFFSET_X = typedArray.getInt(R.styleable.qr_ViewfinderView_qr_offsetX, 0);
        RECT_OFFSET_Y = typedArray.getInt(R.styleable.qr_ViewfinderView_qr_offsetY, 0);

        if (TextUtils.isEmpty(hint)) {
//            hint = "将二维码/条形码置于框内即自动扫描";
            hint = "";
        }
        if (TextUtils.isEmpty(errorHint)) {
//            errorHint = "请允许访问摄像头后重试";
            errorHint = "";
        }
        if (showPossiblePoint) {
            ANIMATION_DELAY = 100L;
        }

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        frameColor = resources.getColor(R.color.color_red);
        laserColor = resources.getColor(R.color.viewfinder_laser);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        scannerAlpha = 0;
        possibleResultPoints = new HashSet<ResultPoint>(5);
        scanLight = BitmapFactory.decodeResource(resources, R.mipmap.qrcode_scan_line);

        typedArray.recycle();
    }

    @Override
    public void onDraw(Canvas canvas) {
//        这个是设置实际的预览框的大小
//        Rect frame1 = CameraManager.get().getFramingRect(RECT_OFFSET_X, RECT_OFFSET_Y);


        int frameWidth = getResources().getDisplayMetrics().widthPixels / 5 * 3;
        int frameHeight = frameWidth;
        int leftOffset = (getResources().getDisplayMetrics().widthPixels - frameWidth) / 2;
        int topOffset = (getResources().getDisplayMetrics().heightPixels - frameHeight) / 5 * 2;
        Rect frame = new Rect(leftOffset, topOffset, leftOffset + frameWidth,
                topOffset + frameHeight);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);// Rect_1
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint); // Rect_2
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
                paint); // Rect_3
        canvas.drawRect(0, frame.bottom + 1, width, height, paint); // Rect_4

        drawText(canvas, frame);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {
            // Draw a two pixel solid black border inside the framing rect
//            paint.setColor(frameColor);
            paint.setColor(Color.GRAY);
//            这是绘制线
            canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
            canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
            canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
            canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);

            drawAngle(canvas, frame);
            drawScanner(canvas, frame);
            if (showPossiblePoint) {
                drawPossiblePoint(canvas, frame);
            }

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

    private void drawAngle(Canvas canvas, Rect frame) {
        int angleLength = 50;
        int angleWidth = 10;
        int top = frame.top;
        int bottom = frame.bottom;
        int left = frame.left;
        int right = frame.right;

        paint.setColor(angleColor);
        // 左上
        canvas.drawRect(left - angleWidth, top - angleWidth, left + angleLength, top, paint);
        canvas.drawRect(left - angleWidth, top - angleWidth, left, top + angleLength, paint);
        // 左下
        canvas.drawRect(left - angleWidth, bottom, left + angleLength, bottom + angleWidth, paint);
        canvas.drawRect(left - angleWidth, bottom - angleLength, left, bottom + angleWidth, paint);
        // 右上
        canvas.drawRect(right - angleLength, top - angleWidth, right + angleWidth, top, paint);
        canvas.drawRect(right, top - angleWidth, right + angleWidth, top + angleLength, paint);
        // 右下
        canvas.drawRect(right - angleLength, bottom, right, bottom + angleWidth, paint);
        canvas.drawRect(right, bottom - angleLength, right + angleWidth, bottom + angleWidth, paint);
    }

    private void drawText(Canvas canvas, Rect frame) {
        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            paint.setColor(hintColor);
            paint.setTextSize(36);
            String text = hint;
            canvas.drawText(hint, frame.centerX() - text.length() * 36 / 2, frame.bottom + 35 + 20, paint);
        } else {
            paint.setColor(errorHintColor);
            paint.setTextSize(36);
            String text = errorHint;
            canvas.drawText(errorHint, frame.centerX() - text.length() * 36 / 2, frame.bottom + 35 + 20, paint);
        }
    }

    private int scanLineTop;
    // 扫描线移动速度
    private final int SCAN_VELOCITY = 10;

    // 绘制扫描线
    // 如果允许绘制 `possible points`则显示居中的红线
    private void drawScanner(Canvas canvas, Rect frame) {

//        绘制扫描线
        paint.setColor(laserColor);
        paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
        scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
        int middle = frame.height() / 2 + frame.top;
        //绘制中间的横线
//        canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1,
//                middle + 2, paint);

        if (scanLineTop == 0) {
            scanLineTop = frame.top;
        }

        if (scanLineTop >= frame.bottom) {
            scanLineTop = frame.top;
        } else {
            scanLineTop += SCAN_VELOCITY;
        }
        Rect scanRect = new Rect(frame.left, scanLineTop, frame.right, scanLineTop + 10);
        canvas.drawBitmap(scanLight, null, scanRect, paint);
        // Request another update at the animation interval, but only
        // repaint the laser line,
        // not the entire viewfinder mask.
        postInvalidateDelayed(ANIMATION_DELAY, frame.left - POINT_SIZE,
                frame.top - POINT_SIZE, frame.right + POINT_SIZE,
                frame.bottom + POINT_SIZE);
    }

    // Draw a yellow "possible points"
    private void drawPossiblePoint(Canvas canvas, Rect frame) {
        Collection<ResultPoint> currentPossible = possibleResultPoints;
        Collection<ResultPoint> currentLast = lastPossibleResultPoints;
        if (currentPossible.isEmpty()) {
            lastPossibleResultPoints = null;
        } else {
            possibleResultPoints = new HashSet<ResultPoint>(5);
            lastPossibleResultPoints = currentPossible;
            paint.setAlpha(OPAQUE);
            paint.setColor(resultPointColor);
            for (ResultPoint point : currentPossible) {
                canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
            }
        }
        if (currentLast != null) {
            paint.setAlpha(OPAQUE / 2);
            paint.setColor(resultPointColor);
            for (ResultPoint point : currentLast) {
                canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
            }
        }
    }
}
