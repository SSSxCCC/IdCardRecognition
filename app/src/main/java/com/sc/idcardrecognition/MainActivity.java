package com.sc.idcardrecognition;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SurfaceView cameraSurfaceView;
    private SurfaceView topSurfaceView;
    private Camera camera;

    private boolean allowTakePicture = false; // 防止多次拍照

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        // 连接相机的SurfaceView
        cameraSurfaceView = findViewById(R.id.surfaceview);
        SurfaceHolder holder = cameraSurfaceView.getHolder();
        holder.setFixedSize(Utility.WidthPixel, Utility.HeightPixel);// 设置分辨率
        holder.setKeepScreenOn(true);
        holder.addCallback(new SurfaceHolder.Callback() { // SurfaceView只有当activity显示到了前台，该控件才会被创建。因此需要监听surfaceview的创建
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    camera = Camera.open();
                    Camera.Parameters params = camera.getParameters();
                    params.setJpegQuality(80); // 设置照片的质量
                    params.setPictureSize(Utility.WidthPixel, Utility.HeightPixel);
                    params.setPreviewFrameRate(10); // 预览帧率
                    params.setPreviewSize(Utility.WidthPixel, Utility.HeightPixel);
                    camera.setParameters(params); // 将参数设置给相机
                    camera.setPreviewDisplay(cameraSurfaceView.getHolder()); // 设置预览显示
                    camera.startPreview(); // 开启预览
                    camera.autoFocus(new Camera.AutoFocusCallback() { // 持续自动对焦
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
                            camera.autoFocus(this);
                        }
                    });
                    allowTakePicture = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if(camera != null){
                    camera.release();
                    camera = null;
                }
            }
        });

        // 顶层的SurfaceView，根据idCard对象里面的信息显示矩形区域
        topSurfaceView = findViewById(R.id.topSurfaceView);
        topSurfaceView.setZOrderOnTop(true); // 在顶层
        holder = topSurfaceView.getHolder();
        holder.setFixedSize(Utility.WidthPixel, Utility.HeightPixel);
        holder.setFormat(PixelFormat.TRANSPARENT); // 透明的
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Canvas canvas = holder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.STROKE);
                for (IdCard.Rect rect : Utility.idCard.getRects()) {
                    canvas.drawRect(rect.toRect(), paint);
                }
                holder.unlockCanvasAndPost(canvas);
            }
            @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }
            @Override public void surfaceDestroyed(SurfaceHolder holder) { }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "要使用此应用，你必须同意授权所有的权限！", Toast.LENGTH_LONG).show();
                System.exit(-1);
            }
        }
    }

    // 点击事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (allowTakePicture) {
            camera.takePicture(null, null, new Camera.PictureCallback() { // 拍照
                @Override
                public void onPictureTaken(byte[] data, Camera camera) { // 处理得到的照片
                    try {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length); // 原图
                        Utility.saveBitmap(bitmap, getResources().getString(R.string.original_picture)); // 保存

                        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            allowTakePicture = false;
        }
        return super.onTouchEvent(event);
    }
}
