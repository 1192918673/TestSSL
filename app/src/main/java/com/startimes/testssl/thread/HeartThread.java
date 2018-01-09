package com.startimes.testssl.thread;

import android.os.Handler;
import android.util.Log;

import com.startimes.starOpenSSL.StarOpenSSL;
import com.startimes.testssl.utils.Contants;
import com.startimes.testssl.MainActivity;

/**
 * Created by startimes on 2017/12/21.
 */

public class HeartThread extends Thread {

    private static final String TAG = "TestSSL";
    private final Handler mHandler;
    private int mHeartSocket = -1;
    private HeartCallback mHeartCallback;
//    private boolean offLine = true;
    private int mCount = 1;

    public HeartThread(Handler handler) {
        super();
        this.mHandler = handler;

    }

    @Override
    public void run() {
        Log.d(TAG, "心跳线程启动。。。");
        mHandler.obtainMessage(MainActivity.MSG_HEART_START, "心跳线程启动。。。").sendToTarget();
        boolean offLine = true;
        while (offLine) {
            Log.d(TAG, "进入while循环，mHeartSocket =" + mHeartSocket);
            if (mHeartSocket == -1) {
                try {
                    this.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                connectHeartSocket();
            } else {
                if (mHeartCallback != null) {
                    mHeartCallback.onHeartBeat();
                    offLine = false;
                }
            }
        }
    }

    private void connectHeartSocket() {
        Log.d(TAG, "连接心跳... mHeartSocket = " + mHeartSocket);
        mHandler.obtainMessage(MainActivity.MSG_HEART_CONN, mCount++ + ":连接心跳... mHeartSocket = " + mHeartSocket).sendToTarget();
        try {
            mHeartSocket = StarOpenSSL.ClientInit(Contants.IP, Contants.PORT, Contants.KEYPATH);
            Log.d(TAG, "mHeartSocket = " + mHeartSocket);
        } catch (Exception e) {
            Log.d(TAG, "连接心跳异常");
            e.printStackTrace();
        }
    }

    public void setHeartCallback(HeartCallback heartCallback) {
        mHeartCallback = heartCallback;
    }

    public interface HeartCallback {
        void onHeartBeat();
    }
}
