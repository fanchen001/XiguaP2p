package com.xigua.p2p;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * InfoThread
 * Created by fanchen on 2018/10/19.
 */
public class InfoThread {
    private MainThreadHandler handler = new MainThreadHandler();
    private AtomicBoolean isRun = new AtomicBoolean(true);
    private Context mContext;

    public InfoThread(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     *发送下载速度广播
     */
    private void sendBroadcastSpeed() {
        if (mContext == null) return;
        P2PClass p2PClass = P2PClass.getInstance();
        long speed = p2PClass.P2Pgetspeed(-1);
        long percent = p2PClass.P2Pgetpercent();
        TaskVideoInfo localTaskVideoInfo = new TaskVideoInfo();
        localTaskVideoInfo.setSpeed(speed);
        localTaskVideoInfo.setPercent(percent);
        Intent intent = new Intent();
        intent.setAction(P2PMessageWhat.P2P_CALLBACK);
        intent.putExtra(P2PMessageWhat.WHAT, P2PMessageWhat.MESSAGE_SPEED);
        intent.putExtra(P2PMessageWhat.DATA, localTaskVideoInfo);
        mContext.sendBroadcast(intent);
    }

    public void start() {
        new Thread(new MainRunnable()).start();
    }

    public void stop() {
        isRun.set(false);
    }

    private class MainRunnable implements Runnable {

        @Override
        public void run() {
            try {
                int position = 0;
                while (isRun.get()) {
                    SystemClock.sleep(2000L);//每2秒发送一次下载速度
                    handler.sendEmptyMessage(P2PMessageWhat.MESSAGE_SPEED);
                    if (position % 2 == 0) {//每4秒发送一次下载列表
                        handler.sendEmptyMessage(P2PMessageWhat.MESSAGE_TASK_LIST);
                    } else if (position % 5 == 0) {
                        position = 0;//每10秒发送一次
                        handler.sendEmptyMessage(P2PMessageWhat.MESSAGE_TASK_MSG);
                    }
                    position++;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }

    private class MainThreadHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
            if (message.what == P2PMessageWhat.MESSAGE_TASK_LIST) {
                TaskVideoList.getInstance().sendBroadcastTaskList();
            } else if (message.what == P2PMessageWhat.MESSAGE_SPEED) {
                InfoThread.this.sendBroadcastSpeed();
            } else if (message.what == P2PMessageWhat.MESSAGE_TASK_MSG) {
                TaskVideoList.getInstance().checkFreeSize();
            }
        }

    }
}
