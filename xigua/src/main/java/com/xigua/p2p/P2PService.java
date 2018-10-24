package com.xigua.p2p;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

/**
 * P2PService
 * Created by fanchen on 2018/10/19.
 */
public class P2PService extends Service {
    public static final String TAG = "xigua_sdk";
    private Messenger messenger;

    public IBinder onBind(Intent var1) {
        return messenger.getBinder();
    }

    public void onCreate() {
        super.onCreate();
        TaskVideoList.getInstance().init(this);
        messenger = new Messenger(new IncomingHandler(this));
    }

    public void onDestroy() {
        super.onDestroy();
        TaskVideoList.getInstance().terminate();
    }

    private static class IncomingHandler extends Handler implements Runnable {

        private String lastUrl = "";
        private Context mContext;

        public IncomingHandler(Context context) {
            mContext = context;
            lastUrl = loadLastUrl();
            if (lastUrl.length() > 0) TaskVideoList.getInstance().start(lastUrl);
        }

        private String loadLastUrl() {
            if (mContext == null) return "";
            return mContext.getSharedPreferences(P2PManager.PREFERENCES, Context.MODE_PRIVATE).getString(P2PManager.TASK_LAST, "");
        }

        private void saveLastUrl() {
            if (mContext == null) return;
            mContext.getSharedPreferences(P2PManager.PREFERENCES, Context.MODE_PRIVATE).edit().putString(P2PManager.TASK_LAST, lastUrl).apply();
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.obj == null || !(msg.obj instanceof Bundle)) return;
            Bundle bundle = (Bundle) msg.obj;
            String url = bundle.getString("url", "");
            if (msg.what == P2PMessageWhat.START) {
                TaskVideoList.getInstance().start(lastUrl = url);
                saveLastUrl();
                Log.e(TAG, "PLAY " + url);
            } else if (msg.what == P2PMessageWhat.PAUSE) {
                TaskVideoList.getInstance().pause(url);
                if (lastUrl.equalsIgnoreCase(url)) {
                    lastUrl = "";
                    saveLastUrl();
                }
                Log.e(TAG, "PAUSE " + url);
            } else if (msg.what == P2PMessageWhat.MESSAGE_INIT_FINISHED) {
                lastUrl = "";
                saveLastUrl();
                new Thread(this).start();
                Log.e(TAG, "CLEAN_CACHE");
            } else if (msg.what == P2PMessageWhat.REMOVE) {
                TaskVideoList.getInstance().remove(url);
                if (lastUrl.equalsIgnoreCase(url)) {
                    lastUrl = "";
                    saveLastUrl();
                }
                Log.e(TAG, "REMOVE " + url);
            } else if (msg.what == P2PMessageWhat.RESTART_SERVICE) {
                Log.e(TAG, "RESTART_SERVICE");
                System.exit(0);
            }
        }

        @Override
        public void run() {
            synchronized (IncomingHandler.class) {
                TaskVideoList.getInstance().cleanCache();
            }
        }

    }
}
