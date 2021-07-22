package com.sc.idcardrecognition;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        initTessdata(getWorkDirectory());
    }

    // 工作目录
    public File getWorkDirectory() {
        return getExternalFilesDir(null);
    }

    // tess-two要求其traineddata文件存在名为tessdata的目录下。
    private void initTessdata(File workDir) {
        // 开一个线程后台写文件
        new Thread() {
            @Override
            public void run() {
                File tessdataDir = new File(workDir, "tessdata");
                if (!tessdataDir.exists()) {
                    if (!tessdataDir.mkdirs()) {
                        Log.e(TAG, "Failed to create directory: " + tessdataDir);
                        return;
                    }
                }

                String traineddata = "chi_sim.traineddata";
                InputStream in = null;
                FileOutputStream out = null;
                try {
                    in = getAssets().open(traineddata);
                    out = new FileOutputStream(new File(tessdataDir, traineddata));
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    if (in != null) {
                        try { in.close(); } catch (IOException e) { e.printStackTrace(); }
                    }
                    if (out != null) {
                        try { out.close(); } catch (IOException e) { e.printStackTrace(); }
                    }
                }

                Log.i(TAG, "initTessdata finish, tessdataDir: " + tessdataDir.getPath());
            }
        }.start();
    }
}
