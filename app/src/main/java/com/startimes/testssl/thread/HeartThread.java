package com.startimes.testssl.thread;

import android.os.Handler;
import android.util.Log;

import com.startimes.starOpenSSL.OpenSSLDate;
import com.startimes.starOpenSSL.StarOpenSSL;
import com.startimes.testssl.utils.SodpPacket;
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
    public boolean offLine = true;
    private byte[] mRecvBuf;
    private int mCount = 0;

    public HeartThread(Handler handler) {
        super();
        this.mHandler = handler;

    }

    @Override
    public void run() {
        Log.d(TAG, "心跳线程启动。。。");
        mHandler.obtainMessage(MainActivity.MSG_HEART_START, "心跳线程启动。。。").sendToTarget();

        while (offLine) {
            if (mHeartSocket == -1) {
                try {
                    connectHeartSocket();
                } catch (Exception e) {
                    e.printStackTrace();
                    disableHeartBeatSocket();
                    try {
                        sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }


                OpenSSLDate openSSLDate = new OpenSSLDate();
                try {
                    Log.d(TAG, "3.心跳线程读取...");
                    mHandler.obtainMessage(MainActivity.MSG_HEART_CONN, mCount++ + "心跳线程读取..." + mHeartSocket).sendToTarget();
                    openSSLDate = StarOpenSSL.ReadClientSocket(mHeartSocket, 1024, 5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int num = 0;
                if (openSSLDate == null) {
                    num = -1;
                } else {
                    mRecvBuf = openSSLDate.getSocket_data();
                    num = openSSLDate.getLen();
                }

                while (offLine) {
                    if (num == -1) {
                        disableHeartBeatSocket();
                        break;
                    } else {
                        SodpPacket packet = new SodpPacket();
                        packet.setPacketData(mRecvBuf);
                        Log.d("TestSSL", "收到心跳反馈，回调...");
                        if (mHeartCallback != null) {
                            mHeartCallback.onHeartBeat(packet);
                            offLine = true;
                        }

                        if (num > packet.getLength() + 6) {
                            System.arraycopy(mRecvBuf, (packet.getLength() + 6), mRecvBuf, 0, num - (packet.getLength() + 6));
                            num -= (packet.getLength() + 6);
                            continue;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }
    /*public void run() {
        while (true) {
            try {
                disableHeartBeatSocket();
                connectHeartSocket();
            } catch (Exception e) {
                disableHeartBeatSocket();
                e.printStackTrace();
                try {
                    sleep(500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                continue;
            }
        }
    }*/

    private void connectHeartSocket() throws Exception {
        Log.d(TAG, mCount++ + "开始连接心跳... mHeartSocket = " + mHeartSocket);
        mHandler.obtainMessage(MainActivity.MSG_HEART_CONN, mCount++ + ":连接心跳... mHeartSocket = " + mHeartSocket).sendToTarget();
        try {
            mHeartSocket = StarOpenSSL.ClientInit(Contants.IP, Contants.PORT, Contants.KEYPATH);
            mHandler.obtainMessage(MainActivity.MSG_TOAST, "连接心跳成功。mHeartSocket = " + mHeartSocket).sendToTarget();
            Log.d(TAG, "连接心跳成功。mHeartSocket = " + mHeartSocket);
        } catch (IllegalStateException e) {
            Log.d(TAG, "连接心跳异常！");
            mHandler.obtainMessage(MainActivity.MSG_RESULT, "连接心跳异常！mHeartSocket = " + mHeartSocket).sendToTarget();
            e.printStackTrace();
            throw e;
        }
    }

    public void disableHeartBeatSocket() {
        Log.i(TAG, "开始断开连接::mHearSocket = " + mHeartSocket);
        if (mHeartCallback != null) {
//            mHeartCallback.onHeartBeatTimeout();
        }
        if (mHeartSocket != -1) {
            try {
                StarOpenSSL.ClientUnInit(mHeartSocket);
                mHeartSocket = -1;
            } catch (Exception e) {
                e.printStackTrace();
                mHeartSocket = -1;
            }
        }
    }

    public void setHeartCallback(HeartCallback heartCallback) {
        mHeartCallback = heartCallback;
    }

    public interface HeartCallback {
        void onHeartBeat(SodpPacket packet);

        void onHeartBeatTimeout();
    }
}
