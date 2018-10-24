package com.xigua.p2p;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * StorageUtils
 * Created by fanchen on 2018/10/19.
 */
public class StorageUtils {
	private static Context appContext = null;
	private static String defMaxPath = "";

    /**
     * 初始化
     * @param paramContext
     */
    public static void init(Context paramContext) {
        appContext = paramContext.getApplicationContext();
        defMaxPath = autoSelectMaxCache();
    }

	private static String autoSelectMaxCache() {
        String cachePath = Environment.getExternalStorageDirectory().getPath();
		try {
            cachePath = cachePath + File.separator + "Android" + File.separator + "data" + File.separator + appContext.getPackageName() + File.separator + "video";
			Iterator<String> localIterator = getStorageList().iterator();
			while (localIterator.hasNext()) {
				String str = localIterator.next();
				if (getFreeSize(cachePath) >= getFreeSize(str)) continue;
				cachePath = str;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cachePath;
	}

    /**
     * 清除缓存
     */
	public static void clearCache() {
		try {
			List<String> storageList = getStorageList();
			storageList.add(defMaxPath);
			Iterator<String> localIterator = storageList.iterator();
			while (localIterator.hasNext()) {
				File dir = new File(localIterator.next());
				if (!dir.exists())continue;
				File[] files = dir.listFiles();
				int i = 0;
				if (files != null && files.length > 0)
					while (i < files.length) {
						if (files[i].isFile())
							files[i].delete();
						i++;
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * 获取下载缓存目录
     * @return
     */
	public static String getCachePath() {
        String cachePath = Environment.getExternalStorageDirectory().getPath();
		try {
            cachePath = cachePath + File.separator +  "Android" + File.separator + "data" + File.separator +  appContext.getPackageName() + File.separator + "video";
			if (defMaxPath.length() > 0)cachePath = defMaxPath;
			cachePath = appContext.getSharedPreferences(P2PManager.PREFERENCES, Context.MODE_PRIVATE).getString(P2PManager.TASK_PATH, cachePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cachePath;
	}

    /**
     * 设置缓存目录
     * @param path
     * @return
     */
    public static boolean setCachePath(String path) {
        try {
            SharedPreferences preferences = appContext.getSharedPreferences(P2PManager.PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(P2PManager.TASK_PATH, path);
            return editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

	@SuppressWarnings("deprecation")
	public static long getFreeSize(String path) {
		long l2 = 10240L;
		long l1 = 10240L;
		try {
			StatFs statFs = new StatFs(path);
			if (Build.VERSION.SDK_INT >= 18) {
				l2 = statFs.getBlockSizeLong();
				l1 = statFs.getAvailableBlocksLong();
			} else {
				l2 = statFs.getBlockSize();
				l1 = statFs.getAvailableBlocks();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return l2 * l1;
	}

	public static List<String> getStorageList() {
		List<String> storageList = new ArrayList<String>();
		try {
            String str = "Android" + File.separator + "data" + File.separator + appContext.getPackageName() + File.separator + "video";
			StorageManager storageManager = (StorageManager) appContext.getSystemService(Context.STORAGE_SERVICE);
			Class<?> clazz = storageManager.getClass();
			Method getVolumePaths = clazz.getMethod("getVolumePaths",new Class[0]);
			String[] volumePaths = (String[]) getVolumePaths.invoke(storageManager, new Object[0]);
			for (String s : volumePaths) {
				Method getVolumeState = clazz.getMethod("getVolumeState",new Class[] { String.class });
				String state = (String) getVolumeState.invoke(storageManager,new Object[] { s });
				if ("mounted".equals(state)) {
					String path = s + File.separator + str;
					getExternalFilesDirs(appContext, null);
					File localFile = new File(path);
					if(!localFile.exists())localFile.mkdirs();
					if (localFile.exists())storageList.add(path);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return storageList;
	}

	public static File[] getExternalFilesDirs(Context context, String type) {
		if (Build.VERSION.SDK_INT >= 19) {
			return context.getExternalFilesDirs(type);
		} else {
			return new File[] { context.getExternalFilesDir(type) };
		}
	}
}