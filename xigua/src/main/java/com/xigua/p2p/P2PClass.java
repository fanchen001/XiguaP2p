package com.xigua.p2p;

import android.os.StatFs;

import java.io.File;

/**
 * P2PClass
 * Created by fanchen on 2018/10/19.
 */
public class P2PClass {

    private static P2PClass instance;

    static {
        try {
            System.loadLibrary("p2p");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private P2PClass() {
    }

    public static P2PClass getInstance() {
        if (instance == null) {
            synchronized (P2PClass.class) {
                if (instance == null) {
                    instance = new P2PClass();
                }
            }
        }
        return instance;
    }

    private final native int dosetupload(int var1);

    private final native int doxadd(byte[] var1);

    private final native int doxcheck(byte[] var1);

    private final native int doxdel(byte[] var1);

    private final native int doxdownload(byte[] var1);

    private final native int doxendhttpd();

    private final native int doxpause(byte[] var1);

    private final native int doxsave();

    private final native int doxsetduration(int var1);

    private final native int doxstart(byte[] var1);

    private final native int doxstarthttpd(byte[] var1);

    private final native int doxterminate();

    private final native long getdownsize(int var1);

    private final native long getfilesize(int var1);

    private final native long getlocalfilesize(byte[] var1);

    private final native int getpercent();

    private final native long getspeed(int var1);

    public long P2PGetFree() {
        StatFs var1 = new StatFs(StorageUtils.getCachePath());
        return var1.getAvailableBlocks() * var1.getBlockSize();
    }

    public long P2Pdosetduration(int var1) {
        return (long) this.doxsetduration(var1);
    }

    public int P2Pdosetupload(int var1) {
        return this.dosetupload(var1);
    }

    public int P2Pdoxadd(byte[] var1) {
        return this.doxadd(var1);
    }

    public int P2Pdoxcheck(byte[] var1) {
        return this.doxcheck(var1);
    }

    public int P2Pdoxdel(byte[] var1) {
        return this.doxdel(var1);
    }

    public int P2Pdoxdownload(byte[] var1) {
        return this.doxdownload(var1);
    }

    public int P2Pdoxpause(byte[] var1) {
        return this.doxpause(var1);
    }

    public int P2Pdoxstart(byte[] var1) {
        return this.doxstart(var1);
    }

    public int P2Pdoxstarthttpd(byte[] var1) {
        return this.doxstarthttpd(var1);
    }

    public int P2Pdoxterminate() {
        return doxterminate();
    }

    public long P2Pgetdownsize(int var1) {
        return getdownsize(var1);
    }

    public long P2Pgetfilesize(int var1) {
        return getfilesize(var1);
    }

    public long P2Pgetlocalfilesize(byte[] var1) {
        return getlocalfilesize(var1);
    }

    public int P2Pgetpercent() {
        return getpercent();
    }

    public long P2Pgetspeed(int var1) {
        return getspeed(var1);
    }

    public int getVersion() {
        return 18;
    }

    public void init() {
        try {
            String path = StorageUtils.getCachePath();
            File file = new File(path + "/xigua");
            if(!file.exists())file.mkdirs();
            P2Pdoxstarthttpd(path.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reinit() {
        try {
            String exec = StorageUtils.getCachePath();
            exec = "rm -r " + exec + "/xigua";
            Runtime.getRuntime().exec(exec).waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
