package com.android.potlach.entity;

/**
 * Created by diyanfilipov on 11/10/14.
 */
public class GiftTouches {
    private long id;
    private long giftId;
    private String username;

    public GiftTouches(){}

    public GiftTouches(long giftId, String username){
        super();
        this.giftId = giftId;
        this.username = username;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getGiftId() {
        return giftId;
    }
    public void setGiftId(long giftId) {
        this.giftId = giftId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
