package com.android.potlach.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.android.potlach.ui.fragment.GiftsFragment;
import com.android.potlach.ui.fragment.TopGiftGiversFragment;

/**
 * Created by diyanfilipov on 10/25/14.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    private Fragment mCurrentPrimaryItem;

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                // Gifts fragment activity
                GiftsFragment giftsFragment = new GiftsFragment();

                Bundle args = new Bundle();
                args.putBoolean("showGiftChainSpinner", Boolean.TRUE);
                giftsFragment.setArguments(args);
                return giftsFragment;
            case 1:
                // Top Gift Givers fragment activity
                return new TopGiftGiversFragment();
            case 2:
                // My Gifts fragment
                GiftsFragment myGiftsFragment = new GiftsFragment();

                Bundle myGiftFragmentArgs = new Bundle();
                myGiftFragmentArgs.putBoolean("showGiftChainSpinner", Boolean.FALSE);
                myGiftsFragment.setArguments(myGiftFragmentArgs);
                return myGiftsFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }
    public Fragment getCurrentPrimaryItem(){
        return mCurrentPrimaryItem;
    }

}
