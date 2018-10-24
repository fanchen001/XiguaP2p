package com.xigua.p2p;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * TaskVideoList
 * Created by fanchen on 2018/10/19.
 */
public class TaskVideoList {
    public static final String TAG = "xigua_sdk";
    private static TaskVideoList instance;
    private InfoThread infoThread;
    private Context mContext;
    private Set<TaskVideoInfo> tastList = new CopyOnWriteArraySet<>();

    /**
     * getInstance
     * @return
     */
    public static TaskVideoList getInstance() {
        if (instance == null) {
            synchronized (TaskVideoList.class) {
                if (instance == null) {
                    instance = new TaskVideoList();
                }
            }
        }
        return instance;
    }

    /**
     * 构建下载信息
     * @param url
     * @return
     */
    private TaskVideoInfo buildTaskInfo(String url) {
        try {
            P2PClass p2PClass = P2PClass.getInstance();
            byte[] gbks = url.getBytes("GBK");
            int doxadd = p2PClass.P2Pdoxadd(gbks);
            TaskVideoInfo task = new TaskVideoInfo();
            task.setUrl(url);
            task.setState(1);
            task.setDownSize(p2PClass.P2Pgetdownsize(doxadd));
            task.setTotalSize(p2PClass.P2Pgetfilesize(doxadd));
            task.setLocalSize(p2PClass.P2Pgetlocalfilesize(gbks));
            task.setSpeed(p2PClass.P2Pgetspeed(-1));
            task.setPercent(p2PClass.P2Pgetpercent());
            return task;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 流量情况下，是否允许下载
     * @return
     */
    private boolean is3G() {
        if (mContext == null) return false;
        return mContext.getSharedPreferences(P2PManager.PREFERENCES, Context.MODE_PRIVATE).getBoolean(P2PManager.TASK_3G, true);
    }

    /**
     * 暂停任务，不保存
     *
     * @param url
     */
    private void pauseNoSave(String url) {
        try {
            byte[] gbks = url.getBytes("GBK");
            P2PClass.getInstance().P2Pdoxpause(gbks);
            if (tastList == null) return;
            for (TaskVideoInfo info : tastList) {
                if (url.equals(info.getUrl())) {
                    info.setState(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除任务，但不保存到本地
     * @param url
     */
    private void removeNoSave(String url) {
        try {
            byte[] gbks = url.getBytes("GBK");
            P2PClass p2PClass = P2PClass.getInstance();
            p2PClass.P2Pdoxpause(gbks);
            p2PClass.P2Pdoxdel(gbks);
            TaskVideoInfo task = new TaskVideoInfo();
            task.setUrl(url);
            if (tastList == null) return;
            tastList.remove(task);
            String segment = Uri.parse(url).getLastPathSegment();
            String path = StorageUtils.getCachePath() + "/xigua/Downloads/" + segment;
            File file = new File(path);
            if (file.exists()) file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送本地代理播放地址
     * @param info
     */
    private void sendPlayUrl(TaskVideoInfo info) {
        try {
            String localFileName = Uri.parse(info.getUrl()).getLastPathSegment();
            String localFile = StorageUtils.getCachePath() + "/xigua/Downloads/" + localFileName;
            File file = new File(localFile);
            boolean isLocal = info.getLocalSize() > 0 && file.exists();
            String play_url = isLocal ? "file://" + localFile : "http://127.0.0.1:8083/" + URLEncoder.encode(localFileName, "GBK");
            sendMessage(P2PMessageWhat.MESSAGE_PLAY_URL,new Object[]{play_url,isLocal});
            Log.i(TAG, "sendPlayUrl:" + play_url);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void checkFreeSize() {
        try {
            if (mContext == null) return;
            if (StorageUtils.getFreeSize(StorageUtils.getCachePath()) >= 524288000L) return;
            getInstance().stopAll();
            sendMessage(P2PMessageWhat.MESSAGE_FREE_SIZE_NOT);
            sendBroadcastTaskList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空缓存
     */
    public void cleanCache() {
        StorageUtils.clearCache();
        P2PClass.getInstance().reinit();
        if (tastList == null) return;
        for (TaskVideoInfo info : tastList) {
            removeNoSave(info.getUrl());
        }
        saveTaskList();
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context) {
        mContext = context.getApplicationContext();
        upgradeLibrary();
        StorageUtils.init(mContext);
        P2PClass.getInstance().init();
        sendBroadcastInitFinished();
        infoThread = new InfoThread(mContext);
        infoThread.start();
        loadTaskList();
    }

    /**
     * 是否是wifi环境，是否可以下载
     * @return
     */
    @SuppressLint("MissingPermission")
    public boolean isWifi() {
        try {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == 1) {
                return true;
            } else {
                return is3G();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 加载下载列表
     */
    public void loadTaskList() {
        try{
            String json = mContext.getSharedPreferences(P2PManager.PREFERENCES, Context.MODE_PRIVATE).getString(P2PManager.TASK_LIST, "[]");
            JSONArray array = new JSONArray(json);
            for (int i = 0 ; i < array.length() ; i ++){
                String url = array.optString(i);
                TaskVideoInfo info = buildTaskInfo(url);
                tastList.add(info);
            }
            sendBroadcastTaskList();
            Log.i(TAG, "load task = :" + json);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 暂停任务
     *
     * @param paramString
     */
    public void pause(String paramString) {
        pauseNoSave(paramString);
        saveTaskList();
    }

    /**
     * 删除任务
     *
     * @param paramString
     */
    public void remove(String paramString) {
        removeNoSave(paramString);
        saveTaskList();
    }

    /**
     * 保存任务信息到本地
     */
    public void saveTaskList() {
        if (tastList == null || mContext == null) return;
        try {
            JSONArray jsonArray = new JSONArray();
            for (TaskVideoInfo info : tastList) {
                jsonArray.put(info.getUrl());
            }
            String s = jsonArray.toString();
            mContext.getSharedPreferences(P2PManager.PREFERENCES, Context.MODE_PRIVATE).edit().putString(P2PManager.TASK_LIST, s).apply();
            Log.i(TAG, "save task = " + s);
            sendBroadcastTaskList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送下载完成消息
     */
    public void sendBroadcastInitFinished() {
        try {
            sendMessage(P2PMessageWhat.MESSAGE_INIT_FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送下载失败消息
     * @param data
     */
    public void sendBroadcastTaskError(String data) {
        try {
            sendMessage(P2PMessageWhat.MESSAGE_TASK_MSG, data);
            Log.e(TAG, "sendBroadcastTaskError -> " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送下载列表消息
     */
    public void sendBroadcastTaskList() {
        try {
            if (tastList == null) return;
            ArrayList<TaskVideoInfo> list = new ArrayList<>();
            for (TaskVideoInfo info : tastList) {
                TaskVideoInfo task = buildTaskInfo(info.getUrl());
                if (task != null) {
                    task.setState(info.getState());
                    list.add(task);
                }
            }
            Collections.sort(list, comparator);
            sendMessage(P2PMessageWhat.MESSAGE_TASK_LIST, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始任务
     * @param url
     * @return
     */
    public boolean start(String url) {
        try {
            byte[] bytes = url.getBytes("GBK");
            P2PClass p2p = P2PClass.getInstance();
            TaskVideoInfo info = buildTaskInfo(url);
            sendPlayUrl(info);
            if(info != null && info.getLocalSize() > 0) {
                return false;
            }else if(!isWifi()) {
                sendBroadcastTaskError("当前非wifi环境，不能下载");
                return false;
            }else if(info != null) {
                stopAll();
                p2p.P2Pdoxstart(bytes);
                info.setState(2);
                tastList.remove(info);
                tastList.add(info);
            } else {
                Log.i(TAG, "添加任务出错");
                return false;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        saveTaskList();
        checkFreeSize();
        return true;
    }

    /**
     * 停止所有任务
     */
    public void stopAll() {
        if (tastList == null) return;
        for (TaskVideoInfo info : tastList) {
            pauseNoSave(info.getUrl());
        }
        saveTaskList();
    }

    /**
     * 释放资源
     */
    public void terminate() {
        stopAll();
        infoThread.stop();
        P2PClass.getInstance().P2Pdoxterminate();
        saveTaskList();
    }

    /**
     * 更新Library version
     */
    public void upgradeLibrary() {
        if (mContext == null) return;
        SharedPreferences preferences = mContext.getSharedPreferences(P2PManager.PREFERENCES, Context.MODE_PRIVATE);
        int version = preferences.getInt(P2PManager.SO_VERSION, 0);
        int pVersion = P2PClass.getInstance().getVersion();
        if (version != pVersion) {
            P2PClass.getInstance().reinit();
            preferences.edit().putInt(P2PManager.SO_VERSION, pVersion).apply();
        }
        Log.e(TAG, String.format("oldVersion = %d newVersion = %d", version, pVersion));
    }

    /**
     * 发送消息
     * @param what
     */
    private void sendMessage(int what) {
        sendMessage(P2PMessageWhat.P2P_CALLBACK, what, null);
    }

    /**
     * 发送消息
     * @param what
     * @param data
     */
    private void sendMessage(int what, Object data) {
        sendMessage(P2PMessageWhat.P2P_CALLBACK, what, data);
    }

    /**
     * 发送消息
     * @param action
     * @param what
     * @param data
     */
    private void sendMessage(String action, int what, Object data) {
        if (mContext == null) return;
        Intent intent = new Intent(action);
        intent.putExtra(P2PMessageWhat.WHAT, what);
        if (data != null && data instanceof String) {
            intent.putExtra(P2PMessageWhat.DATA, data.toString());
        } else if (data != null && data instanceof ArrayList) {
            intent.putParcelableArrayListExtra(P2PMessageWhat.DATA, (ArrayList<Parcelable>) data);
        } else if (data != null && data instanceof Object[]) {
            Object[] objects = (Object[]) data;
            intent.putExtra(P2PMessageWhat.PLAY_URL, objects[0].toString());
            intent.putExtra(P2PMessageWhat.LOCAL_FILE, (boolean) objects[1]);
        }
        mContext.sendBroadcast(intent);
    }

    private Comparator<TaskVideoInfo> comparator = new Comparator<TaskVideoInfo>() {

        @Override
        public int compare(TaskVideoInfo o1, TaskVideoInfo o2) {
            return o1.getUrl().compareToIgnoreCase(o2.getUrl());
        }

    };
}
