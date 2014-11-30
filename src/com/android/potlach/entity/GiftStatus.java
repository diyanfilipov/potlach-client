package com.android.potlach.entity;

/**
 * Created by diyanfilipov on 10/29/14.
 */
public class GiftStatus {
    public enum ImageState{
        READY, PROCESSING;
    }

    private ImageState imageState;

    public GiftStatus(ImageState state) {
        super();
        this.imageState = state;
    }

    public ImageState getState() {
        return imageState;
    }

    public void setState(ImageState state) {
        this.imageState = state;
    }
}
