package com.android.potlach.util;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by diyanfilipov on 10/27/14.
 */
public final class StorageUtils {

    public static Uri getOutputMediaFileUri(String fileName) {
        return Uri.fromFile(getOutputMediaFile(fileName));
    }

    public static File getOutputMediaFile(String fileName) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorage = getMediaStorage("Potlach");
        if(mediaStorage != null){
            File mediaFile = new File(mediaStorage.getPath() + File.separator + fileName + ".jpg");
            return mediaFile;
        }

        return null;
    }

    public static File getMediaStorage(String name){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // For future implementation: store videos in a separate directory
            File mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory(),
                    name);
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }
            return mediaStorageDir;
        }
        return null;
    }
}
