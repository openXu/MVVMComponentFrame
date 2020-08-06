package com.openxu.core.zxing.dlazaro66.qrcodereaderview;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import com.google.zxing.ResultPoint;


public class QRToViewPointTransformer {

    public PointF[] transform(ResultPoint[] qrPoints, boolean isMirrorPreview,
                              Orientation orientation,
                              Point viewSize, Point cameraPreviewSize) {
        PointF[] transformedPoints = new PointF[qrPoints.length];
        int index = 0;
        for (ResultPoint qrPoint : qrPoints) {
            PointF transformedPoint = transform(qrPoint, isMirrorPreview, orientation, viewSize,
                    cameraPreviewSize);
            transformedPoints[index] = transformedPoint;
            index++;
        }
        return transformedPoints;
    }

    public PointF transform(ResultPoint qrPoint, boolean isMirrorPreview, Orientation orientation,
                            Point viewSize, Point cameraPreviewSize) {
        float previewX = cameraPreviewSize.x;
        float previewY = cameraPreviewSize.y;

        PointF transformedPoint = null;
        float scaleX;
        float scaleY;

        if (orientation == Orientation.PORTRAIT) {
            scaleX = viewSize.x / previewY;
            scaleY = viewSize.y / previewX;
            transformedPoint = new PointF((previewY - qrPoint.getY()) * scaleX, qrPoint.getX() * scaleY);
            if (isMirrorPreview) {
                transformedPoint.y = viewSize.y - transformedPoint.y;
            }
        } else if (orientation == Orientation.LANDSCAPE) {
            scaleX = viewSize.x / previewX;
            scaleY = viewSize.y / previewY;
//            transformedPoint = new PointF(viewSize.x - qrPoint.getX() * scaleX,
//                    viewSize.y - qrPoint.getY() * scaleY);
            transformedPoint = new PointF(viewSize.x - qrPoint.getX() * scaleX,
                    (viewSize.y - qrPoint.getY())/2);
            Log.e("TAG", "viewSize.x======" + viewSize.x + "    viewSize.y===" + viewSize.y
                    + "    qrPoint.getX()=== " + qrPoint.getX() + "    qrPoint.getY()====" + qrPoint.getY()
                    + "    scaleX===" + scaleX + "     scaleY===" + scaleY
            );
            if (isMirrorPreview) {
                transformedPoint.x = viewSize.x - transformedPoint.x;
            }
        }
        return transformedPoint;
    }
}
