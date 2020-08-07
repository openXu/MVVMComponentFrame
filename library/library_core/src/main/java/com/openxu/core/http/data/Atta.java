package com.openxu.core.http.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: openXu
 * Time: 2019/3/15 15:34
 * class: Atta
 * Description:
 */
public class Atta implements Parcelable {

    private String ID;
    private String path;
    private String url;
    private String date; //时间"yyyy.MM.dd HH:mm"
    private String name;     //图片的备注
    private String description;   //说明

    //文件上传
    private String serialKey;

    public Atta() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Atta(String name, String path, String url) {
        this.path = path;
        this.url = url;
        this.name = name;
    }

    public String getSerialKey() {
        return serialKey;
    }

    public void setSerialKey(String serialKey) {
        this.serialKey = serialKey;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Atta{" +
                "ID='" + ID + '\'' +
                ", path='" + path + '\'' +
                ", url='" + url + '\'' +
                ", date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", serialKey='" + serialKey + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ID);
        dest.writeString(this.path);
        dest.writeString(this.url);
        dest.writeString(this.date);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.serialKey);
    }

    protected Atta(Parcel in) {
        this.ID = in.readString();
        this.path = in.readString();
        this.url = in.readString();
        this.date = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        int tmpType = in.readInt();
        this.serialKey = in.readString();
    }

    public static final Creator<Atta> CREATOR = new Creator<Atta>() {
        @Override
        public Atta createFromParcel(Parcel source) {
            return new Atta(source);
        }

        @Override
        public Atta[] newArray(int size) {
            return new Atta[size];
        }
    };
}
