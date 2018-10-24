package com.xigua.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * NetWorkChangeReceiver
 * Created by fanchen on 2018/10/19.
 */
public class NetWorkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent broadcast = new Intent();
        broadcast.setAction(P2PMessageWhat.P2P_CALLBACK);
        broadcast.putExtra(P2PMessageWhat.WHAT, P2PMessageWhat.MESSAGE_NETWORK_CHANGE);
        context.sendBroadcast(broadcast);
    }

}
