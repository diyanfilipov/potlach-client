package com.android.potlach.entity;

import android.graphics.Bitmap;

/**
 * Created by diyanfilipov on 11/3/14.
 */
public class GiftImageData {
    private Gift gift;
    private Bitmap bitmap;

    public GiftImageData(Gift gift, Bitmap bitmap){
        this.gift = gift;
        this.bitmap = bitmap;
    }

    public Gift getGift() {
        return gift;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
