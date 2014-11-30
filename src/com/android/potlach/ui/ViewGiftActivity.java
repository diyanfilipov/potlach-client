package com.android.potlach.ui;

import android.accounts.AccountManager;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.potlach.R;
import com.android.potlach.entity.Gift;
import com.android.potlach.entity.GiftObsceneStatus;
import com.android.potlach.entity.GiftTouchStatus;
import com.android.potlach.security.AuthenticationAction;
import com.android.potlach.security.SecurityUtils;
import com.android.potlach.task.MarkAsObsceneTask;
import com.android.potlach.task.TouchGiftTask;
import com.android.potlach.task.ViewGiftTask;
import com.android.potlach.util.BitmapUtils;

import java.util.List;

/**
 * Created by diyanfilipov on 11/8/14.
 */
public class ViewGiftActivity extends Activity implements DisplayableMessageAxtivity {
    private static final String TAG = ViewGiftActivity.class.getSimpleName();

    private ImageView imageView;
    private TextView giftDescriptionTextView;
    private TextView giftTouchesTextView;
    private LinearLayout touchButton;
    private MenuItem menuItem;

    private long giftId;
    private boolean isTouched;
    private boolean isObscene;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(null);
        giftId = getIntent().getExtras().getLong("giftId");
        if(giftId == 0){
            finish();
        }

        setContentView(R.layout.activity_view_gift);

        imageView = (ImageView) findViewById(R.id.imageView);
        giftDescriptionTextView = (TextView) findViewById(R.id.gift_description);
        giftTouchesTextView = (TextView) findViewById(R.id.gift_touches);
        touchButton = (LinearLayout) findViewById(R.id.btn_touch);
        touchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SecurityUtils.checkAuthentication(AccountManager.get(ViewGiftActivity.this),
                        new AuthenticationAction() {
                            @Override
                            public void execute(boolean valid) {
                                if(!isTouched && valid) {
                                    startGiftTouchTask();
                                }
                            }
                }, ViewGiftActivity.this, ViewGiftActivity.this);

            }
        });

        final List<String> accountInfo = SecurityUtils.getAccountInfo(ViewGiftActivity.this);
        String username = null;
        if(accountInfo != null){
            username = accountInfo.get(0);
        }

        new ViewGiftTask(giftId, username, this).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gift_activity_actions, menu);
        menuItem = menu.findItem(R.id.action_mark_gift_obscene);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_mark_gift_obscene:
                SecurityUtils.checkAuthentication(AccountManager.get(ViewGiftActivity.this),
                        new AuthenticationAction() {
                            @Override
                            public void execute(boolean valid) {
                                if(!isObscene && valid) {
                                    startMarkAsObsceneTask();
                                }
                            }
                        }, ViewGiftActivity.this, ViewGiftActivity.this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void displayGift(Gift gift, boolean touchedByUser, Bitmap bitmap) {
        if(gift != null) {
            getActionBar().setTitle(gift.getTitle());
            imageView.setImageBitmap(bitmap);
            giftDescriptionTextView.setText(gift.getDescription());
            giftTouchesTextView.setText(String.valueOf(gift.getTouches()));
            if(touchedByUser){
                touchButton.setBackgroundColor(getResources().getColor(R.color.gray));
                touchButton.setEnabled(Boolean.FALSE);
            }

            if(gift.isObscene()){
                RelativeLayout parent = (RelativeLayout) imageView.getParent();
                parent.setBackgroundColor(getResources().getColor(R.color.red));
                giftDescriptionTextView.setTextColor(getResources().getColor(R.color.black));
                giftTouchesTextView.setTextColor(getResources().getColor(R.color.black));
            }

            final List<String> accountInfo = SecurityUtils.getAccountInfo(ViewGiftActivity.this);
            if(accountInfo != null){
                if(gift.isObscene() && menuItem != null){
                    menuItem.setVisible(false);
                }
            }else{
                if(menuItem != null){
                    menuItem.setVisible(false);
                }
            }
        }
    }

    private void startMarkAsObsceneTask() {
        final List<String> accountInfo = SecurityUtils.getAccountInfo(ViewGiftActivity.this);
        if(accountInfo != null){
            new MarkAsObsceneTask(
                    giftId,
                    accountInfo.get(0),
                    accountInfo.get(1),
                    "mobile",
                    ViewGiftActivity.this).execute();
        }
    }

    private void startGiftTouchTask(){
        final List<String> accountInfo = SecurityUtils.getAccountInfo(ViewGiftActivity.this);
        if(accountInfo != null){
            new TouchGiftTask(
                    giftId,
                    accountInfo.get(0),
                    accountInfo.get(1),
                    "mobile",
                    ViewGiftActivity.this).execute();
        }
    }

    public void refreshGiftTouch(GiftTouchStatus giftTouchStatus, int touchCount) {
        final List<String> accountInfo = SecurityUtils.getAccountInfo(ViewGiftActivity.this);
        if(giftTouchStatus != null && accountInfo != null){
            if(giftTouchStatus.getTouchState() == GiftTouchStatus.TouchState.TOUCHED){
//                GiftUtils.addUsernameToCache(giftId, accountInfo.get(0));
                touchButton.setBackgroundColor(getResources().getColor(R.color.gray));
                touchButton.setEnabled(Boolean.FALSE);
                giftTouchesTextView.setText(String.valueOf(touchCount));
                isTouched = false;
            }
        }
    }

    public void refreshGiftObsceneStatus(GiftObsceneStatus giftObsceneStatus){
        if(giftObsceneStatus != null && giftObsceneStatus.getObsceneState() == GiftObsceneStatus.ObsceneState.MARKED){
            menuItem.setVisible(false);
            RelativeLayout parent = (RelativeLayout) imageView.getParent();
            parent.setBackgroundColor(getResources().getColor(R.color.red));
            giftDescriptionTextView.setTextColor(getResources().getColor(R.color.black));
            giftTouchesTextView.setTextColor(getResources().getColor(R.color.black));
            isObscene = true;
        }
    }

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
}
