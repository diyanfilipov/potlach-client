package com.android.potlach.entity;

/**
 * Created by diyanfilipov on 11/18/14.
 */
public class GiftObsceneStatus {
    public enum ObsceneState {
        MARKED, UNMARKED
    }

    private ObsceneState obsceneState;

    public GiftObsceneStatus(ObsceneState obsceneState){
        this.obsceneState = obsceneState;
    }

    public ObsceneState getObsceneState() {
        return obsceneState;
    }
}
