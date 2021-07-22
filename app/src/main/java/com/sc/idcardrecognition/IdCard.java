package com.sc.idcardrecognition;

import android.graphics.Rect;

import java.util.HashMap;
import java.util.Map;

public class IdCard {

    // 由于身份证尺寸为85.6mm×54.0mm，所以取以下参考长宽，保证比例与身份证卡片相同
    public static final int CardReferenceWidth = 214;
    public static final int CardReferenceHeight = 135;

    private final Map<Integer, Rect> tagRectMap;

    public IdCard(int widthPixel, int heightPixel, float ratio) {
        if (ratio > 1f || ratio <= 0f) {
            throw new IllegalArgumentException("ratio must be between 0(exclusive) and 1(inclusive)");
        }

        int cardWidth, cardHeight;
        if ((float) widthPixel / heightPixel > (float) CardReferenceWidth / CardReferenceHeight) {  // 以height为主缩放
            cardHeight = (int) (heightPixel * ratio);
            cardWidth = cardHeight * CardReferenceWidth / CardReferenceHeight;
        } else {  // 以width为主缩放
            cardWidth = (int) (widthPixel * ratio);
            cardHeight = cardWidth * CardReferenceHeight / CardReferenceWidth;
        }

        int left = (widthPixel - cardWidth) / 2;
        int top = (heightPixel - cardHeight) / 2;
        Rect cardRect = new Rect(left, top, left + cardWidth, top + cardHeight);  // 卡

        tagRectMap = new HashMap<>();
        tagRectMap.put(R.string.id_card, cardRect);  // 卡
        tagRectMap.put(R.string.name, buildRect(cardRect, 0.174f, 0.115f, 0.432f, 0.107f));  // 姓名
        tagRectMap.put(R.string.sex, buildRect(cardRect, 0.176f, 0.250f, 0.068f, 0.100f));  // 性别
        tagRectMap.put(R.string.ethnicity, buildRect(cardRect, 0.379f, 0.256f, 0.225f, 0.093f));  // 民族
        tagRectMap.put(R.string.year, buildRect(cardRect, 0.178f, 0.380f, 0.099f, 0.080f));  // 年
        tagRectMap.put(R.string.month, buildRect(cardRect, 0.327f, 0.378f, 0.058f, 0.091f));  // 月
        tagRectMap.put(R.string.day, buildRect(cardRect, 0.418f, 0.376f, 0.058f, 0.089f));  // 日
        tagRectMap.put(R.string.address, buildRect(cardRect, 0.173f, 0.496f, 0.429f, 0.280f));  // 住址
        tagRectMap.put(R.string.number, buildRect(cardRect, 0.331f, 0.820f, 0.572f, 0.087f));  // 号码
    }

    public Map<Integer, Rect> getTagRectMap() {
        return tagRectMap;
    }

    private Rect buildRect(Rect cardRect, float xRatio, float yRatio, float widthRatio, float heightRatio) {
        int left = cardRect.left + (int) (cardRect.width() * xRatio);
        int top = cardRect.top + (int) (cardRect.height() * yRatio);
        int width = (int) (cardRect.width() * widthRatio);
        int height = (int) (cardRect.height() * heightRatio);
        return new Rect(left, top, left + width, top + height);
    }
}
