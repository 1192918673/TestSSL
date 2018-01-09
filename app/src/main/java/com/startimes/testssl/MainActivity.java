package com.startimes.testssl;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.startimes.starOpenSSL.OpenSSLDate;
import com.startimes.starOpenSSL.StarOpenSSL;
import com.startimes.testssl.thread.HeartThread;
import com.startimes.testssl.thread.SodpThread;
import com.startimes.testssl.utils.Contants;
import com.startimes.testssl.utils.aes.AESCrypt;
import com.startimes.testssl.utils.aes.IOUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, HeartThread.HeartCallback, SodpThread.SodpCallback {

    private static final String TAG = "TestSSL";
    private static final byte[] ivBytes = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
    public static final int MSG_TOAST = 10;
    public static final int MSG_THREAD_STARTED = 11;
    public static final int MSG_WRITE = 12;
    public static final int MSG_READ = 13;
    public static final int MSG_UNINIT = 14;
    public static final int MSG_HEART_START = 15;
    public static final int MSG_HEART_CONN = 16;
    public static final int MSG_SODP_START = 17;
    public static final int MSG_SODP_CONN = 18;
    private static String[] mInSSLKey = {"openssl_key/ca_aes_enc.crt", "openssl_key/client_aes_enc.crt", "openssl_key/client_aes_enc.key"};
    private static String[] mOutSSLKey = {"ca.crt", "client.crt", "client.key"};
    private HeartThread mHeartThread;
    private SodpThread mSodpThread;
    private int mSocket = -1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TOAST:
                case MSG_UNINIT:
                case MSG_HEART_START:
                case MSG_SODP_START:
                case MSG_HEART_CONN:
                case MSG_SODP_CONN:
//                    Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    tv_result.setText((String) msg.obj);
                    break;
                case MSG_THREAD_STARTED:
//                    Toast.makeText(MainActivity.this, (String) msg.obj + "线程已经启动...", Toast.LENGTH_SHORT).show();
                    tv_result.setText((String) msg.obj + "线程已经启动...");
                    break;
                case MSG_READ:
//                    Toast.makeText(MainActivity.this, "读取数据" + (String) msg.obj, Toast.LENGTH_SHORT).show();
                    tv_result.setText("读取数据" + (String) msg.obj);
                    break;
                case MSG_WRITE:
//                    Toast.makeText(MainActivity.this, "发送数据" + (String) msg.obj, Toast.LENGTH_SHORT).show();
                    tv_result.setText("发送数据" + (String) msg.obj);
                    break;
            }
        }
    };
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_start = (Button) findViewById(R.id.btn_start_heart);
        btn_start.setOnClickListener(this);
        Button btn_sodp = (Button) findViewById(R.id.btn_start_sodp);
        btn_sodp.setOnClickListener(this);
        Button btn_read = (Button) findViewById(R.id.btn_read);
        btn_read.setOnClickListener(this);
        Button btn_write = (Button) findViewById(R.id.btn_write);
        btn_write.setOnClickListener(this);
        Button btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
        btn_disconnect.setOnClickListener(this);
        tv_result = (TextView) findViewById(R.id.tv_result);

        copyKeyFile();
    }

    private void copyKeyFile() {
        try {
            SecretKeySpec key = AESCrypt.generateKey("password");
            byte[] decryptData = IOUtil.readToByteArray(AESCrypt.decryptFile(key, ivBytes, getAssets().open(mInSSLKey[0])));
            IOUtil.saveFile(decryptData, Contants.KEYPATH, mOutSSLKey[0]);

            decryptData = IOUtil.readToByteArray(AESCrypt.decryptFile(key, ivBytes, getAssets().open(mInSSLKey[1])));
            IOUtil.saveFile(decryptData, Contants.KEYPATH, mOutSSLKey[1]);

            decryptData = IOUtil.readToByteArray(AESCrypt.decryptFile(key, ivBytes, getAssets().open(mInSSLKey[2])));
            IOUtil.saveFile(decryptData, Contants.KEYPATH, mOutSSLKey[2]);

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.toString());
        } catch (GeneralSecurityException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        Log.d(TAG, "开始注册密钥文件。。。");
        tv_result.setText("注册密钥文件。。。");
        StarOpenSSL.ClientRegistSecretKey(Contants.KEYPATH);
        StarOpenSSL.debugEnable(1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_heart:
                if (mHeartThread == null) {
                    mHeartThread = new HeartThread(mHandler);
                    mHeartThread.setHeartCallback(this);
                    mHeartThread.start();
                } else {
                    Log.d(TAG, "心跳线程已经启动...");
                    mHandler.obtainMessage(MSG_THREAD_STARTED, "心跳").sendToTarget();
                }
                break;
            case R.id.btn_start_sodp:
                if (mSodpThread == null) {
                    mSodpThread = new SodpThread(mHandler);
                    mSodpThread.start();
                    mSodpThread.setSodpCallback(this);
                } else {
                    Log.d(TAG, "Sodp线程已经启动...");
                    mHandler.obtainMessage(MSG_THREAD_STARTED, "SODP").sendToTarget();
                }
                break;
            case R.id.btn_read:
                read();
                break;
            case R.id.btn_write:
                write();
                break;
            case R.id.btn_disconnect:
                disConn();
                break;
        }
    }

    private void read() {
        Log.d(TAG, "read()...");
        if (-1 == mSocket) {
            Log.d(TAG, "读取数据::mSocket == -1");
            mHandler.obtainMessage(MSG_READ, "mSocket == -1").sendToTarget();
            return;
        }
        try {
            if (null != StarOpenSSL.ReadClientSocket(mSocket, 4096, 3)) {
                Log.d(TAG, "读取数据成功！");
                mHandler.obtainMessage(MSG_READ, "成功").sendToTarget();
            } else {
                Log.d(TAG, "读取到的数据为空！");
                mHandler.obtainMessage(MSG_READ, "为空").sendToTarget();
            }
        } catch (Exception e) {
            Log.d(TAG, "读取到的数据异常！");
            mHandler.obtainMessage(MSG_READ, "异常").sendToTarget();
            e.printStackTrace();
        }
    }

    private void write() {
        Log.d(TAG, "write()...");
        if (mSocket == -1) {
            Log.i(TAG, "发送数据::mSocket == -1");
            mHandler.obtainMessage(MSG_WRITE, "mSocket == -1").sendToTarget();
            return;
        }
        OpenSSLDate date = new OpenSSLDate();
        date.setSocket_data(new byte[]{1, 2});
        date.setLen(2);
        try {
            if (StarOpenSSL.WriteClientSocket(mSocket, date) == -1) {
                Log.i(TAG, "发送数据失败!");
                mHandler.obtainMessage(MSG_WRITE, "失败").sendToTarget();
            } else {
                Log.i(TAG, "发送数据成功!");
                mHandler.obtainMessage(MSG_WRITE, "成功").sendToTarget();
            }
        } catch (IllegalStateException e) {
            Log.i(TAG, "发送数据异常!");
            mHandler.obtainMessage(MSG_WRITE, "异常").sendToTarget();
            e.printStackTrace();
        }
    }

    private void disConn() {
        if (mHeartThread != null) {
            mHeartThread.interrupt();
            mHeartThread = null;
            Log.d(TAG, "UnInit: 心跳线程停止！");
            mHandler.obtainMessage(MSG_UNINIT, "心跳停止！").sendToTarget();
        } else {
            Log.d(TAG, "UnInit: 心跳线程是停止状态！");
        }
        if (mSodpThread != null) {
            mSodpThread.interrupt();
            mSodpThread = null;
            Log.d(TAG, "UnInit: Sodp线程停止！");
            mHandler.obtainMessage(MSG_UNINIT, "Sodp线程停止！").sendToTarget();
        } else {
            Log.d(TAG, "UnInit: Sodp线程是停止状态！");
        }
        if (mSocket != -1) {
            StarOpenSSL.ClientUnInit(mSocket);
            Log.d(TAG, "UnInit: 断开链接！");
            mSocket = -1;
            mHandler.obtainMessage(MSG_UNINIT, "断开链接！").sendToTarget();
        } else {
            Log.d(TAG, "UnInit: mSocket == -1");
        }
    }

    @Override
    public void onHeartBeat() {
        Log.d(TAG, "收到心跳，启动Sodp线程！");
        mHandler.obtainMessage(MSG_TOAST, "收到心跳，启动Sodp线程！").sendToTarget();
        if (mSodpThread == null) {
            mSodpThread = new SodpThread(mHandler);
            mSodpThread.start();
            mSodpThread.setSodpCallback(this);
        }
    }

    @Override
    public void onSodpRecv(int mSocket) {
        this.mSocket = mSocket;
        Log.d(TAG, "Sodp初始化成功！mSocket = " + mSocket);
        mHandler.obtainMessage(MSG_TOAST, "Sodp初始化成功！mSocket = " + mSocket).sendToTarget();
    }
}
