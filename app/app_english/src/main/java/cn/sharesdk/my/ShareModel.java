package cn.sharesdk.my;

import android.graphics.Bitmap;

/**
 * @data: 2014-7-21 ����2:42:34
 * @version: V1.0
 */
public class ShareModel {
    private String title;
    private String text;
    private String url;
    private String imageUrl;
    private Bitmap bitmap;

    public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
