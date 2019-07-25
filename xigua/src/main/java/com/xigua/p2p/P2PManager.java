package com.xigua.p2p;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

/**
 * P2PManager
 * Created by fanchen on 2018/10/19.
 */
public class P2PManager {
    public static final String PREFERENCES = "xigua_preferences";
    public static final String TASK_3G = "task_3g";
    public static final String TASK_LIST = "task_list";
    public static final String TASK_LAST = "task_last";
    public static final String TASK_PATH = "task_path";
    public static final String SO_VERSION = "so_version";

    private static P2PManager instance;

    private ConnectionCallback connectionCallback;
    private Context mContext;
    private boolean mIsConnect = false;
    private Messenger messenger;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            messenger = new Messenger(binder);
            mIsConnect = true;
            if (connectionCallback != null) {
                connectionCallback.onServiceConnected();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName var1) {
            mIsConnect = false;
            bindP2PService();
        }
    };

    /**
     * getInstance
     *
     * @return
     */
    public static P2PManager getInstance() {
        if (instance == null) {
            synchronized (P2PManager.class) {
                if (instance == null) {
                    instance = new P2PManager();
                }
            }
        }
        return instance;
    }
    
     /**
     *
     * @param uri
     * @return
     */
    public static boolean isXiguaUrl(Uri uri){
        String scheme = uri.getScheme();
        return scheme != null && (scheme.equalsIgnoreCase("xg") || scheme.equalsIgnoreCase("xgadd") || scheme.equalsIgnoreCase("xgplay"));
    }

    /**
     *
     * @param url
     * @return
     */
    public static boolean isXiguaUrl(String url){
        if(TextUtils.isEmpty(url))return false;
        return isXiguaUrl(Uri.parse(url));
    }

    /**
     * 解绑服务
     */
    private void unbindP2PService() {
        if (mContext == null) return;
        mContext.unbindService(connection);
        Intent var1 = new Intent(mContext, P2PService.class);
        mContext.stopService(var1);
    }

    /**
     * 绑定服务
     */
    private void bindP2PService() {
        if (mContext == null) return;
        Intent var1 = new Intent(mContext, P2PService.class);
        mContext.startService(var1);
        mContext.bindService(var1, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 清空缓存
     *
     * @return
     */
    public boolean cleanCache() {
        if (!isConnect()) {
            return false;
        } else {
            try {
                messenger.send(Message.obtain(null, P2PMessageWhat.CLEAN_CACHE));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 获取是否使用流量下载
     *
     * @return
     */
    public boolean getAllow3G() {
        if (mContext == null) return true;
        return mContext.getSharedPreferences(P2PManager.PREFERENCES, Context.MODE_PRIVATE).getBoolean(P2PManager.TASK_3G, true);
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context.getApplicationContext();
        StorageUtils.init(context.getApplicationContext());
        bindP2PService();
    }

    /**
     * 是否连接服务
     *
     * @return
     */
    public boolean isConnect() {
        if (!mIsConnect) {
            bindP2PService();
        }
        return this.mIsConnect;
    }

    /**
     * 暂停一个任务
     *
     * @param var1
     * @return
     */
    public boolean pause(String var1) {
        if (!isConnect()) {
            return false;
        } else {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("url", var1);
                messenger.send(Message.obtain(null, P2PMessageWhat.PAUSE, bundle));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 开始一个任务
     *
     * @param var1
     * @return
     */
    public boolean play(String var1) {
        if (!isConnect()) {
            return false;
        } else {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("url", var1);
                messenger.send(Message.obtain(null, P2PMessageWhat.START, bundle));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 释放资源
     */
    public void release() {
        unbindP2PService();
    }

    /**
     * 移除一个任务
     *
     * @param url
     * @return
     */
    public boolean remove(String url) {
        if (!isConnect()) {
            return false;
        } else {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                messenger.send(Message.obtain(null, P2PMessageWhat.REMOVE, bundle));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 重启服务
     *
     * @return
     */
    public boolean restartService() {
        if (!isConnect()) {
            return false;
        } else {
            try {
                messenger.send(Message.obtain(null, P2PMessageWhat.RESTART_SERVICE));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 设置是否允许流量下载
     *
     * @param allow3G
     */
    public void setAllow3G(boolean allow3G) {
        if (mContext == null) return;
        mContext.getSharedPreferences(P2PManager.PREFERENCES, Context.MODE_PRIVATE).edit().putBoolean(P2PManager.TASK_3G, allow3G).apply();
    }

    /**
     * @param connectionCallback
     */
    public void setConnectionCallback(ConnectionCallback connectionCallback) {
        this.connectionCallback = connectionCallback;
    }

    /**
     * ConnectionCallback
     */
    public interface ConnectionCallback {
        void onServiceConnected();
    }
}
