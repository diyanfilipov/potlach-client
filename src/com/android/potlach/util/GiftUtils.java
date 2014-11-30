package com.android.potlach.util;

import android.util.LruCache;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by diyanfilipov on 11/10/14.
 */
public class GiftUtils {
    // key: gift id, value: list of user names(hashcode representation) who touched the gift
    private static LruCache<Long, Set<String>> mGiftTouchesCache = createGiftTouchesCache();

    private static LruCache<Long, Set<String>> createGiftTouchesCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        LruCache<Long, Set<String>> mMemoryCache = new LruCache<Long, Set<String>>(cacheSize) {
            @Override
            protected int sizeOf(Long key, Set<String> users) {
                return users.size() * 8;
            }
        };
        return mMemoryCache;
    }

    public static void addUsernameToCache(Long key, String username) {
        Set<String> usernames = mGiftTouchesCache.get(key);
        if(usernames == null){
            usernames = new HashSet<String>();
            usernames.add(username);
            mGiftTouchesCache.put(key, usernames);
        }else if(!usernames.contains(username)){
            usernames.add(username);
            mGiftTouchesCache.put(key, usernames);
        }
    }

    public static boolean usernameIsCached(Long key, String username) {
        Set<String> usernames = mGiftTouchesCache.get(key);
        if(usernames != null && usernames.contains(username)){
            return true;
        }
        return false;
    }

    public static int giftTouches(Long key){
        Set<String> usernames = mGiftTouchesCache.get(key);
        if(usernames != null){
            return usernames.size();
        }
        return 0;
    }
}
