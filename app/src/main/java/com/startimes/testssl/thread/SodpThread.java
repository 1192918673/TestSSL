package com.startimes.testssl.thread;

import android.os.Handler;
import android.util.Log;
import com.startimes.starOpenSSL.StarOpenSSL;
import com.startimes.testssl.utils.Contants;
import com.startimes.testssl.MainActivity;

/**
 * Created by startimes on 2017/12/21.
 */

public class SodpThread extends Thread {

    private static final String TAG = "TestSSL";
    private final Handler mHandler;
    public int mSocket = -1;
    private SodpCallback mSodpCallback;
    public boolean connected = false;
    private int mCount = 0;

    public SodpThread(Handler handler) {
        super();
        this.mHandler = handler;
    }

    @Override
    public void run() {
        Log.d(TAG, "Sodp线程启动。。。");
        mHandler.obtainMessage(MainActivity.MSG_SODP_START, "Sodp线程启动。。。").sendToTarget();
        connected = false;
        while (!connected) {
            if (mSocket == -1) {
                try {
                    this.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                connectSodpSocket();
            } else {
                if (mSodpCallback != null) {
                    mSodpCallback.onSodpRecv(mSocket);
                    connected = true;
                }
            }
        }
    }

    private void connectSodpSocket() {
        Log.d(TAG, mCount++ + "Sodp连接... mSocket = " + mSocket);
        mHandler.obtainMessage(MainActivity.MSG_SODP_CONN, mCount++ + "Sodp连接... mSocket = " + mSocket).sendToTarget();
        try {
            mSocket = StarOpenSSL.ClientInit(Contants.IP, Contants.PORT + 1, Contants.KEYPATH);
        } catch (Exception e) {
            Log.d(TAG, "SODP连接异常");
            mHandler.obtainMessage(MainActivity.MSG_RESULT, "SODP连接异常！mSocket = " + mSocket).sendToTarget();
            e.printStackTrace();
        }
    }

    public void setSodpCallback(SodpCallback sodpCallback) {
        mSodpCallback = sodpCallback;
    }

    public interface SodpCallback {
        void onSodpRecv(int mSocket);
    }
}
