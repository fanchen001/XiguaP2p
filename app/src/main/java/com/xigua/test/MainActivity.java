package com.xigua.test;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

import com.xigua.p2p.P2PManager;
import com.xigua.p2p.P2PMessageWhat;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //SDK 的初始化需要时间
        new Thread(){

            @Override
            public void run() {
                SystemClock.sleep(3000);
                String url = "xg://a.gbl.114s.com:20320/9847/魔法禁书目录第三季03.mp4";
                P2PManager.getInstance().play(url.replace("xg://","ftp://"));
            }
        }.start();
        registerReceiver(receiver,new IntentFilter(P2PMessageWhat.P2P_CALLBACK));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra(P2PMessageWhat.WHAT, 0);
            if(P2PMessageWhat.MESSAGE_TASK_LIST == intExtra){
                ((TextView)findViewById(R.id.text1)).setText("MESSAGE_TASK_LIST : " + intent.getParcelableArrayListExtra(P2PMessageWhat.DATA).toString());
            }else if(P2PMessageWhat.MESSAGE_PLAY_URL == intExtra){
                ((TextView)findViewById(R.id.text1)).setText("MESSAGE_PLAY_URL : " + intent.getStringExtra(P2PMessageWhat.PLAY_URL));
            }else if(P2PMessageWhat.MESSAGE_SPEED == intExtra){
                ((TextView)findViewById(R.id.text1)).setText( "MESSAGE_SPEED : " + intent.getParcelableExtra(P2PMessageWhat.DATA).toString());
            }
        }

    };
}
