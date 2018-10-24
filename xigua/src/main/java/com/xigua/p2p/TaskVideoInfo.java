package com.xigua.p2p;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TaskVideoInfo
 * Created by fanchen on 2018/10/19.
 */
public class TaskVideoInfo implements Parcelable {
    public static final int PAUSE = 1;
    public static final int START = 2;
    private long downSize;//已下载大小
    private long localSize;//本地文件大小
    private long percent;
    private long speed;//下载速度
    private int state;//状态
    private long totalSize;//总大小
    private String url;//url地址

    public TaskVideoInfo() {
    }

    protected TaskVideoInfo(Parcel in) {
        downSize = in.readLong();
        localSize = in.readLong();
        percent = in.readLong();
        speed = in.readLong();
        state = in.readInt();
        totalSize = in.readLong();
        url = in.readString();
    }

    public long getDownSize() {
        return downSize;
    }

    public void setDownSize(long downSize) {
        this.downSize = downSize;
    }

    public long getLocalSize() {
        return localSize;
    }

    public void setLocalSize(long localSize) {
        this.localSize = localSize;
    }

    public long getPercent() {
        return percent;
    }

    public void setPercent(long percent) {
        this.percent = percent;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(downSize);
        dest.writeLong(localSize);
        dest.writeLong(percent);
        dest.writeLong(speed);
        dest.writeInt(state);
        dest.writeLong(totalSize);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TaskVideoInfo> CREATOR = new Creator<TaskVideoInfo>() {
        @Override
        public TaskVideoInfo createFromParcel(Parcel in) {
            return new TaskVideoInfo(in);
        }

        @Override
        public TaskVideoInfo[] newArray(int size) {
            return new TaskVideoInfo[size];
        }
    };

    @Override
    public String toString() {
        return "[url -> " + url + ", state -> " + state +",speed -> " + speed + "]";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object != null && object instanceof TaskVideoInfo) {
            return url.equals(((TaskVideoInfo) object).url);
        }
        return false;
    }
}
