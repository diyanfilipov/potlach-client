package com.android.potlach.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.potlach.R;
import com.android.potlach.security.AuthenticationAction;
import com.android.potlach.security.GeneralPotlachAccount;
import com.android.potlach.security.SecurityUtils;
import com.android.potlach.ui.adapter.TabsPagerAdapter;
import com.android.potlach.ui.fragment.GiftsFragment;
import com.android.potlach.ui.fragment.TopGiftGiversFragment;
import com.android.potlach.util.StorageUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, PreferenceChangeListener, DisplayableMessageAxtivity{
    public static boolean showObscene;

	private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CAMERA_PIC_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int CREATE_GIFT_REQUEST = 3;



    // Tab titles
    private final String[] tabs = { "Gifts", "Top Givers", "My Gifts" };
    private Uri imagePath;
    private int lastSelectedTab;

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private Menu menu;
//    private ProgressDialog mProgressDialog;

    private AccountManager mAccountManager;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
//        Toast.makeText(this, "Available memory: " + (int) (Runtime.getRuntime().maxMemory()/1024/1024) + "MB", Toast.LENGTH_LONG).show();

        setContentView(R.layout.activity_main);
        mAccountManager = AccountManager.get(this);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        /**
         * Turn off strict mode.
         *
         * Normally, if you try to do any networking from the main UI
         * thread, the Android framework will throw an exception and
         * stop working. However, part of this application uses a
         * synchronous AIDL interface to demonstrate how to execute
         * functions in services synchronously. For this purpose, we
         * turn off strict mode so that the Android framework will
         * work without complaining.
         *
         *  Please note that this is for demonstration purposes ONLY,
         *  and you should NEVER, EVER turn off strict mode in
         *  production code. You should also not do networking
         *  operations on the main thread, because it might cause your
         *  application to crash.
         */
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        showObscene = preferences.getBoolean("pref_showObsceneGifts", false);

	}

    @Override
    protected void onResume() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        lastSelectedTab = preferences.getInt("SELECTED_TAB", 0);
        actionBar.setSelectedNavigationItem(lastSelectedTab);
        super.onResume();

    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
		// Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    
	    // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        final Account availableAccounts[] = mAccountManager.getAccountsByType(GeneralPotlachAccount.ACCOUNT_TYPE);
        if(availableAccounts.length == 0) {
            configureLogoutMenuItemVisibility(false);
        }else if(availableAccounts.length == 1){
            SecurityUtils.getExistingAccountAuthToken(availableAccounts[0],
                mAccountManager, new AuthenticationAction() {
                        @Override
                        public void execute(boolean valid) {
                            configureLogoutMenuItemVisibility(valid);
                        }
                    }, this, this);
        }
        
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
            case R.id.action_upload_take_photo:
                SecurityUtils.checkAuthentication(mAccountManager, new AuthenticationAction() {
                    @Override
                    public void execute(boolean valid) {
                        configureLogoutMenuItemVisibility(valid);

                        launchCameraIntent();
                    }
                }, this, this);

                return true;
            case R.id.action_upload_from_gallery:
                SecurityUtils.checkAuthentication(mAccountManager, new AuthenticationAction() {
                    @Override
                    public void execute(boolean valid) {
                        configureLogoutMenuItemVisibility(valid);

                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
//                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                    }
                }, this, this);
                return true;
	        case R.id.action_search:
	            //openSearch();
	        	Log.d(TAG, "Search clicked");
	            return true;
	        case R.id.action_settings:
	        	Log.d(TAG, "Settings clicked");
                Intent settingsIntent = new Intent();
                settingsIntent.setClass(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
	            return true;
            case R.id.action_logout:
                final Account availableAccounts[] = mAccountManager.getAccountsByType(GeneralPotlachAccount.ACCOUNT_TYPE);
                SecurityUtils.invalidateAuthentication(mAccountManager, availableAccounts[0],
                        new AuthenticationAction() {
                            @Override
                            public void execute(boolean valid) {
                                configureLogoutMenuItemVisibility(valid);
                            }
                        }, this, this);
                return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}


    private void launchCreateGiftActivity(String imagePath){
        Intent createGiftIntent = new Intent();
        createGiftIntent.setClass(this, CreateGiftActivity.class);
        createGiftIntent.putExtra("imagePath", imagePath);

        int currentTab = viewPager.getCurrentItem();
        GiftsFragment fragment = (GiftsFragment) mAdapter.getCurrentPrimaryItem();
        if(currentTab == 0){
            createGiftIntent.putExtra("GIFT_CHAIN", fragment.getLastSelectedGiftChain());
        }
        //fragment.unBindService();
        startActivityForResult(createGiftIntent, CREATE_GIFT_REQUEST);
    }

    private void launchCameraIntent(){
        // Create a new intent to launch the MediaStore, Image capture function
        // Hint: use standard Intent from MediaStore class
        // See: http://developer.android.com/reference/android/provider/MediaStore.html
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create a media file name
        String imageName = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.US)
                .format(new Date());
        imageName = "IMG_" + imageName;

        // Set the imagePath for this image file using the pre-made function
        // getOutputMediaFile to create a new filename for this specific image;
        Uri outputFileName = StorageUtils.getOutputMediaFileUri(imageName);
        if(outputFileName != null){
            // Add the filename to the Intent as an extra. Use the Intent-extra name
            // from the MediaStore class, EXTRA_OUTPUT
            imagePath = outputFileName;
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileName);

            // Start a new activity for result, using the new intent and the request
            // code CAMERA_PIC_REQUEST
            startActivityForResult(pictureIntent, CAMERA_PIC_REQUEST);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "CreateGiftActivity onActivtyResult called. requestCode: "
                + requestCode + " resultCode:" + resultCode + "data:" + data);
        if(requestCode == CAMERA_PIC_REQUEST){
            if(resultCode == RESULT_OK){
                File mediaStorage = StorageUtils.getMediaStorage("Potlach");
                if(mediaStorage != null){
                    File image = new File(imagePath.getPath());
                    if(image.exists()){
                        launchCreateGiftActivity(imagePath.getPath());
                    }
                }
            }
        }else if(requestCode == PICK_IMAGE_REQUEST){
            if(resultCode == RESULT_OK && data != null && data.getData() != null){
                String imagePath = getRealPathFromURI(data.getData());
                launchCreateGiftActivity(imagePath);
            }
        }else if(requestCode == CREATE_GIFT_REQUEST){
            if(resultCode == RESULT_CANCELED){

                int currentTab = viewPager.getCurrentItem();
                switch (currentTab){
                    case 1: // top gift givers tab
                        TopGiftGiversFragment topGiftGiversFragment = (TopGiftGiversFragment) mAdapter.getCurrentPrimaryItem();
                        if(topGiftGiversFragment != null){
                            TopGiftGiversFragment.loadNewDataOnCreate = false;
                            topGiftGiversFragment.onRefresh();
                        }
                        break;
                    default: // others tab
                        GiftsFragment fragment = (GiftsFragment) mAdapter.getCurrentPrimaryItem();
                        if(fragment != null) {
                            GiftsFragment.loadNewDataOnCreate = false;
                            fragment.onRefresh();
                        }
                        break;
                }
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    protected void onPause() {
//        stopService(new Intent(this, PotlachService.class));
//        unbindService(mServiceConnection);
        super.onPause();

    }

    private void configureLogoutMenuItemVisibility(final boolean visibility){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                MenuItem logout = menu.findItem(R.id.action_logout);
                logout.setVisible(visibility);
                invalidateOptionsMenu();
//            }
//        });
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if(lastSelectedTab != tab.getPosition()){
            lastSelectedTab = tab.getPosition();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("SELECTED_TAB", lastSelectedTab);
            editor.commit();
        }
        Log.d(TAG, "onTabSelected " + tab.getPosition() + " last=" + lastSelectedTab);
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}

    @Override
    public void showMessage(final String msg) {
        if (TextUtils.isEmpty(msg))
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent preferenceChangeEvent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferenceChangeEvent.getKey().equals("pref_showObsceneGifts")){
            showObscene = preferences.getBoolean("pref_showObsceneGifts", false);
            // TODO: extract update period and restart service
            GiftsFragment fragment = (GiftsFragment) mAdapter.getCurrentPrimaryItem();
            if(fragment != null) {
                fragment.startService();
            }
        }
    }
}
