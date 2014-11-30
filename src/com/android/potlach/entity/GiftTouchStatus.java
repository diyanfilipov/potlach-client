package com.android.potlach.entity;

/**
 * Created by diyanfilipov on 11/10/14.
 */
public class GiftTouchStatus {
    public enum TouchState {
        TOUCHED, UNTOUCHED;
    }

    private TouchState touchState;

    public GiftTouchStatus(TouchState touchState){
        this.touchState = touchState;
    }

    public TouchState getTouchState(){
        return touchState;
    }
}
