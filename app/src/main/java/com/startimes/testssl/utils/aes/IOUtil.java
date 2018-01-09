package com.startimes.testssl.utils.aes;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by THEROCK on 2015/11/9.
 */
public class IOUtil {
    static String TAG = IOUtil.class.getSimpleName();
    static final String UTF8 = "utf-8";
    static final String ISO = "ISO-8859-1";
    static final int BUFFER_SIZE = 8 * 1024;


    public static String readToString(InputStream is) throws IOException {
        if(is == null)return null;
        byte[] data = readToByteArray(is);
        return new String(data, ISO);
    }

    public static byte[] readToByteArray(InputStream is) throws IOException {
        Log.i("MAJH", " --------------------------------------readToByteArray-------------- ");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Log.i("MAJH", " --------------------------------------readToByteArray-------12------- ");
        byte[] buffer = new byte[BUFFER_SIZE];
        Log.i("MAJH", " --------------------------------------readToByteArray-------13------- ");
        int len;
        while ((len = is.read(buffer)) != -1) {
            Log.i("MAJH", " --------------------------------------len------1-------- " + len);
            baos.write(buffer, 0, len);
        }
        Log.i("MAJH", " --------------------------------------readToByteArray------1-------- ");
        return baos.toByteArray();
    }

    public static void bitmapToFile(Bitmap bm, String fileName) throws IOException {
        File file = new File(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    public static String readFilesToString(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists() || file.isDirectory())
            throw new FileNotFoundException();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String temp = null;
        StringBuffer sb = new StringBuffer();
        temp = br.readLine();
        while (temp != null) {
            sb.append(temp + "\n");
            temp = br.readLine();
        }
        return sb.toString();
    }

    public static void saveFile(InputStream is, String file) throws IOException {
        FileOutputStream fs = null;
        try {
            int bytesum = 0;
            int byteread = 0;
            if (is != null) {
                fs = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((byteread = is.read(buffer)) != -1) {
                    bytesum += byteread;
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                is.close();
            }
        } catch (Exception e) {
            System.out.println("Copy Failed");
            Log.e(TAG, e.toString());
        } finally {
            try {
                is.close();
                fs.close();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public static void saveFile(byte[] fileData,
                                String folderPath, String fileName) {
        File folder = new File(folderPath);
        folder.mkdirs();
        File file = new File(folderPath, fileName);
        ByteArrayInputStream is = new ByteArrayInputStream(fileData);
        OutputStream os = null;
        if (!file.exists()) {
            try {
                file.createNewFile();
                os = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = is.read(buffer))) {
                    os.write(buffer, 0, len);
                }
                os.flush();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            } finally {
                closeIO(is, os);
            }
        }
    }

    private static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public static boolean writeFile(String filePath, String content, boolean append) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    public static String getFolderName(String filePath) {
        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }
}
