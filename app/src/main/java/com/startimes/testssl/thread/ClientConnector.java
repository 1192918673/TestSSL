package com.startimes.testssl.thread;

import android.os.Handler;
import android.util.Log;

import com.startimes.testssl.utils.Contants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import static com.startimes.testssl.MainActivity.MSG_TOAST;

/**
 * Created by startimes on 18-2-2.
 */

public class ClientConnector {

    private static final byte[] HOST_IP = {10, 66, 1, 3};
    private static final String TAG = "ClientConnector";
    private final Handler mHandler;
    private Socket mClient;
    /**
     * 服务端的ip
     */
    private String mDstName;
    /**
     * 服务端端口号
     */
    private int mDesPort;

    private ConnectLinstener mListener;


    public ClientConnector(Handler handler, String dstName, int dstPort) {
        this.mHandler = handler;
        this.mDstName = dstName;
        this.mDesPort = dstPort;
    }

    /**
     * 与服务端进行连接
     *
     * @throws IOException
     */
    public void connect() {
        new Thread() {
            @Override
            public void run() {
                if (mClient == null) {
                    try {
                        Log.d(TAG, "创建普通socket连接。。。");
                        mClient = new Socket(Contants.IP, Contants.PORT); //, InetAddress.getByAddress(HOST_IP), 5555
                    } catch (Exception e) {
                        Log.d(TAG, "普通socket连接异常！");
                        mHandler.obtainMessage(MSG_TOAST, "普通socket连接异常！").sendToTarget();
                        e.printStackTrace();
                    }
                }

                if (mClient != null) {
                    Log.d(TAG, "普通socket连接成功！");
                    //获取其他客户端发送过来的数据
                    InputStream inputStream = null;
                    try {
                        inputStream = mClient.getInputStream();

                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while ((len = inputStream.read(buffer)) != -1) {
                            String data = new String(buffer, 0, len);

                            //通过回调接口将获取到的数据推送出去
                            if (mListener != null) {
                                mListener.onReceiveData(data);
                            }
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "普通socket接受数据异常！");
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 认证方法，这个方法是用来进行客户端一对一发送消息的
     * 在实际项目中进行即时通讯时都需要进行登录，这里就是
     * 模拟客户端的账号
     *
     * @param authName
     */
    public void auth(String authName) throws IOException {
        if (mClient != null) {
            //将客户端账号发送给服务端，让服务端保存
            OutputStream outputStream = mClient.getOutputStream();
            //模拟认证格式，以#开头
            outputStream.write(("#" + authName).getBytes());
        }
    }

    /**
     * 将数据发送给指定的接收者
     *
     * @param receiver 信息接数者
     * @param data     需要发送的内容
     */
    public void send(String receiver, String data) throws IOException {
        OutputStream outputStream = mClient.getOutputStream();
        //模拟内容格式：receiver+  # + content
        outputStream.write((receiver + "#" + data).getBytes());
    }

    /**
     * 断开连接
     *
     * @throws IOException
     */
    public void disconnect() throws IOException {
        if (mClient != null) {
            mClient.close();
            mClient = null;
        }
    }


    public void setOnConnectLinstener(ConnectLinstener linstener) {
        this.mListener = linstener;
    }

    /**
     * 数据接收回调接口
     */
    public interface ConnectLinstener {
        void onReceiveData(String data);
    }
}
