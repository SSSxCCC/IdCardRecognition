package com.sc.idcardrecognition;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Utility {

    public static final int WidthPixel = 1920;  // 水平像素
    public static final int HeightPixel = 1080;  // 垂直像素
    public static IdCard idCard = new IdCard(WidthPixel, HeightPixel, 0.8f);  // 身份证图像对齐信息

    // 保存图像
    public static void saveBitmap(Bitmap bitmap, File workDir, String name) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        try {
            baos.close();
            File file = new File(workDir,name + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 图像二值化
    public static Bitmap binary(Bitmap bitmap) {
        Bitmap binaryBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        int width = binaryBitmap.getWidth();
        int height = binaryBitmap.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = binaryBitmap.getPixel(i, j);
                int alpha = (pixel & 0xFF000000);
                int red = (pixel & 0x00FF0000) >> 16;
                int green = (pixel & 0x0000FF00) >> 8;
                int blue = (pixel & 0x000000FF);
                int gray = (int) (red * 0.3f + green * 0.59f + blue * 0.11f);
                if (gray <= 127) gray = 0;
                else gray = 255;
                int color = alpha | (gray << 16) | (gray << 8) | gray;
                binaryBitmap.setPixel(i, j, color);
            }
        }
        return binaryBitmap;
    }

    // OCR算法识别图像的文字
    public static String doOcr(Bitmap bitmap, String tessdataPath) {
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(tessdataPath, "chi_sim");
        baseApi.setImage(bitmap);
        String text = baseApi.getUTF8Text();
        baseApi.clear();
        baseApi.end();
        return text;
    }

    public static Map<Character, Character> DigitCorrectDictionary;  // 常见数字错误纠正词典
    static {
        DigitCorrectDictionary = new HashMap<>();
        DigitCorrectDictionary.put('D', '0');
        DigitCorrectDictionary.put('o', '0');
        DigitCorrectDictionary.put('O', '0');
        DigitCorrectDictionary.put('l', '1');
        DigitCorrectDictionary.put('I', '1');
        DigitCorrectDictionary.put('z', '2');
        DigitCorrectDictionary.put('Z', '2');
        DigitCorrectDictionary.put('S', '5');
        DigitCorrectDictionary.put('s', '5');
        DigitCorrectDictionary.put('g', '9');
    }

    // 修正常见的错误
    public static String fix(String text, int tagId, Resources resources) {
        switch (tagId) {
            case R.string.name:  // 姓名
                text = text.replace("\n", "");  // 不能换行
                break;
            case R.string.sex:  // 性别
                text = text.replace("\n", "");  // 不能换行
                text = text.replace(" ", "");  // 不能有空格
                if (text.startsWith("田") || text.endsWith("力")  // “男”被识别成“田力”
                        || text.contains("另"))  // “男”被识别成“另”
                    text = "男";
                break;
            case R.string.ethnicity:  // 民族
                text = text.replace("\n", "");  // 不能换行
                break;
            case R.string.year:  // 年
            case R.string.month:  // 月
            case R.string.day:  // 日
                text = text.replace("\n", "");  // 不能换行
                text = text.replace(" ", "");  // 不能有空格
                text = correctDigit(text);  // 数字纠正
                text = onlyDigit(text);  // 只留数字
                break;
            case R.string.number:  // 号码
                text = text.replace("\n", "");  // 不能换行
                text = text.replace(" ", "");  // 不能有空格
                text = correctDigit(text);  // 数字纠正
                break;
            case R.string.address:  // 住址
                break;
            default:
                Log.e("Fix", "发现不存在的身份证元素：" + resources.getString(tagId));
                break;
        }
        return text;
    }

    // 常见数字识别错误纠正
    private static String correctDigit(String text) {
        StringBuilder correctedText = new StringBuilder();
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (Utility.DigitCorrectDictionary.containsKey(c)) {
                correctedText.append(Utility.DigitCorrectDictionary.get(c));
            } else {
                correctedText.append(c);
            }
        }
        return correctedText.toString();
    }

    // 只留数字
    private static String onlyDigit(String text) {
        StringBuilder digitText = new StringBuilder();
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (c >= '0' && c <= '9')
                digitText.append(c);
        }
        return digitText.toString();
    }
}
