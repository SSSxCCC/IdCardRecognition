package com.sc.idcardrecognition;

import android.content.res.Resources;
import android.graphics.Rect;

import java.util.HashMap;
import java.util.Map;

public class IdCard {

    // 由于身份证尺寸为85.6mm×54.0mm，所以取以下参考长宽，保证比例与身份证卡片相同
    public static final int CardReferenceWidth = 214;
    public static final int CardReferenceHeight = 135;

    private int widthPixel;
    private int heightPixel;

    public class Rect {

        private int left, right ,top, bottom, width, height;
        private int tagId;

        public Rect(int tagId, int left, int right, int top, int bottom) {
            this.tagId = tagId;
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
            this.width = right - left;
            this.height = bottom - top;
        }

        public Rect(int tagId, int x, int y, int width, int height, Object nul) {
            this(tagId, x, x + width, y, y + height);
        }

        public Rect(int tagId, float xRatio, float yRatio, float widthRatio, float heightRatio) {
            this(tagId, cardRect.getX() + (int) (cardRect.getWidth() * xRatio), cardRect.getY() + (int) (cardRect.getHeight() * yRatio),
                    (int) (cardRect.getWidth() * widthRatio), (int) (cardRect.getHeight() * heightRatio), null);
        }

        public android.graphics.Rect toRect() {
            return new android.graphics.Rect(left, top, right, bottom);
        }

        public int getLeft() {
            return left;
        }

        public int getRight() {
            return right;
        }

        public int getTop() {
            return top;
        }

        public int getBottom() {
            return bottom;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getX() {
            return left;
        }

        public int getY() {
            return top;
        }

        public int getTagId() {
            return tagId;
        }
    }

    private Rect cardRect; // 卡
    private Rect nameRect; // 姓名
    private Rect sexRect; // 性别
    private Rect ethnicityRect; // 民族
    private Rect yearRect; // 年
    private Rect monthRect; // 月
    private Rect dayRect; // 日
    private Rect addressRect; // 住址
    private Rect numberRect; // 号码

    private Rect[] rects;
    private Map<Integer, Rect> tagRectMap;

    public IdCard(int widthPixel, int heightPixel, float ratio) {
        this.widthPixel = widthPixel;
        this.heightPixel = heightPixel;

        if (ratio > 1f || ratio <= 0f) {
            throw new IllegalArgumentException("ratio must be between 0(exclusive) and 1(inclusive)");
        }

        int cardWidth, cardHeight;
        if ((float) widthPixel / heightPixel > (float) CardReferenceWidth / CardReferenceHeight) { // 以height为主缩放
            cardHeight = (int) (heightPixel * ratio);
            cardWidth = cardHeight * CardReferenceWidth / CardReferenceHeight;
        } else { // 以width为主缩放
            cardWidth = (int) (widthPixel * ratio);
            cardHeight = cardWidth * CardReferenceHeight / CardReferenceWidth;
        }

        int x = (widthPixel - cardWidth) / 2;
        int y = (heightPixel - cardHeight) / 2;
        cardRect = new Rect(R.string.id_card, x, y, cardWidth, cardHeight, null);

        nameRect = new Rect(R.string.name, 0.174f, 0.115f, 0.432f, 0.107f);
        sexRect = new Rect(R.string.sex, 0.176f, 0.250f, 0.068f, 0.100f);
        ethnicityRect = new Rect(R.string.ethnicity, 0.379f, 0.256f, 0.225f, 0.093f);
        yearRect = new Rect(R.string.year, 0.178f, 0.380f, 0.099f, 0.080f);
        monthRect = new Rect(R.string.month, 0.327f, 0.378f, 0.058f, 0.091f);
        dayRect = new Rect(R.string.day, 0.418f, 0.376f, 0.058f, 0.089f);
        addressRect = new Rect(R.string.address, 0.173f, 0.496f, 0.429f, 0.280f);
        numberRect = new Rect(R.string.number, 0.331f, 0.820f, 0.572f, 0.087f);

        rects = new Rect[] { cardRect, nameRect, sexRect, ethnicityRect, yearRect, monthRect, dayRect, addressRect, numberRect };
        tagRectMap = new HashMap<>();
        for (Rect rect : rects) {
            tagRectMap.put(rect.getTagId(), rect);
        }
    }

    public Rect getCardRect() {
        return cardRect;
    }

    public Rect[] getRects() {
        return rects;
    }

    public Map<Integer, Rect> getTagRectMap() {
        return tagRectMap;
    }
}
