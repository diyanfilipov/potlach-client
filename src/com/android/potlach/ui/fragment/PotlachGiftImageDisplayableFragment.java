package com.android.potlach.ui.fragment;

import com.android.potlach.entity.Gift;
import com.android.potlach.entity.GiftImageData;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by diyanfilipov on 11/3/14.
 */
public interface PotlachGiftImageDisplayableFragment {

    void displayGifts(List<Gift> gifts, boolean overrideGifts);
}
