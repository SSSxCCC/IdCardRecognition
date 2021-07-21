package com.sc.idcardrecognition;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private Handler handler;
    private Map<Integer, EditText> tagViewMap;
    private int remainingTask;
    private Bitmap originalPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        final TextView stateTextView = findViewById(R.id.state); // 显示当前状态信息的文本框
        final long startTime = System.currentTimeMillis(); // 记录开始时间
        /*final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.recognizing));
        progressDialog.show();*/

        // 所有要检测的身份证参数
        tagViewMap = new HashMap<>();
        tagViewMap.put(R.string.name, (EditText) findViewById(R.id.name));
        tagViewMap.put(R.string.sex, (EditText) findViewById(R.id.sex));
        tagViewMap.put(R.string.ethnicity, (EditText) findViewById(R.id.ethnicity));
        tagViewMap.put(R.string.year, (EditText) findViewById(R.id.year));
        tagViewMap.put(R.string.month, (EditText) findViewById(R.id.month));
        tagViewMap.put(R.string.day, (EditText) findViewById(R.id.day));
        tagViewMap.put(R.string.number, (EditText) findViewById(R.id.number));
        tagViewMap.put(R.string.address, (EditText) findViewById(R.id.address));
        remainingTask = tagViewMap.size();

        // 任何参数检测完成时调用
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                tagViewMap.get(msg.what).setText((String) msg.obj);
                remainingTask--;
                if (remainingTask <= 0) {
                    //progressDialog.cancel();
                    float spendTime = (System.currentTimeMillis() - startTime) / 1000f;
                    stateTextView.setText(getResources().getString(R.string.spend_time) + spendTime + "s");
                    stateTextView.setTextColor(Color.GREEN);
                }
            }
        };

        // 读取原图
        originalPicture = BitmapFactory.decodeFile(Utility.getWorkDirectory() + "/" + getResources().getString(R.string.original_picture) + ".jpg");

        IdCard.Rect rect = Utility.idCard.getCardRect(); // 得到身份证裁剪信息
        Bitmap cardBitmap = Bitmap.createBitmap(originalPicture, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()); // 裁剪出身份证
        Utility.saveBitmap(cardBitmap, getResources().getString(rect.getTagId())); // 保存
        ((ImageView) findViewById(R.id.photo)).setImageBitmap(cardBitmap); // 显示裁剪的身份证

        // 对识别身份证的每个参数分别开线程并行处理
        for (int tagId : tagViewMap.keySet()) {
            new OcrThread(tagId).start();
        }
    }

    // 执行算法的线程
    public class OcrThread extends Thread {

        private int tagId;

        public OcrThread(int tagId) {
            this.tagId = tagId;
        }

        @Override
        public void run() {
            IdCard.Rect rect = Utility.idCard.getTagRectMap().get(tagId);
            Bitmap bitmap = Bitmap.createBitmap(originalPicture, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()); // 裁剪
            //bitmap = Utility.binary(bitmap); // 二值化
            Utility.saveBitmap(bitmap, getResources().getString(tagId)); // 保存
            String result = Utility.doOcr(bitmap); // 图像转换成文本
            result = Utility.fix(result, tagId, getResources()); // 错误修正

            Message message = new Message();
            message.what = tagId;
            message.obj = result;
            handler.sendMessage(message);
        }
    }
}
