package com.xigua.p2p;

/**
 * P2PMessageWhat
 * Created by fanchen on 2018/10/19.
 */
public class P2PMessageWhat {
    public static final int CLEAN_CACHE = 257;
    public static final int DOWNLOAD = 2;
    public static final int PAUSE = 3;
    public static final int REMOVE = 4;
    public static final int RESTART_SERVICE = 256;
    public static final int START = 1;
    public static final int MESSAGE_FREE_SIZE_NOT = 4;
    public static final int MESSAGE_INIT_FINISHED = 257;
    public static final int MESSAGE_NETWORK_CHANGE = 5;
    public static final int MESSAGE_PLAY_URL = 258;
    public static final int MESSAGE_SPEED = 1;
    public static final int MESSAGE_TASK_LIST = 2;
    public static final int MESSAGE_TASK_MSG = 3;
    public static final String WHAT = "what";
    public static final String DATA = "data";
    public static final String PLAY_URL = "url";
    public static final String LOCAL_FILE = "local";
    public static final String P2P_CALLBACK = "com.broadcast.message.p2p";

    private P2PMessageWhat(){
    }
}
