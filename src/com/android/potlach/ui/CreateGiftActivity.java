package com.android.potlach.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.potlach.R;
import com.android.potlach.entity.Gift;
import com.android.potlach.task.ListGiftChainsTask;
import com.android.potlach.task.UploadGiftTask;
import com.android.potlach.security.SecurityUtils;
import com.android.potlach.util.BitmapUtils;
import com.android.potlach.util.StorageUtils;

import java.io.File;
import java.util.List;

public class CreateGiftActivity extends Activity implements GiftChainContainer{
    private static final String TAG = CreateGiftActivity.class.getSimpleName();

    private LinearLayout btnUploadGift;

    private ImageView imgViewGift;
    private EditText txtTitle;
    private EditText txtDescription;
    private AutoCompleteTextView autoCompleteTextView;
    private ProgressDialog mProgressDialog;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gift);

        final String imagePath = getIntent().getExtras().getString("imagePath");
        final String giftChain = getIntent().getExtras().getString("GIFT_CHAIN");

        btnUploadGift = (LinearLayout) findViewById(R.id.linearButton);
        btnUploadGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtTitle.getText().length() == 0){
                    txtTitle.setError(getString(R.string.error_gift_missing_title));
                    return;
                }
                if(TextUtils.isEmpty(autoCompleteTextView.getText())){
                    autoCompleteTextView.setError(getString(R.string.error_gift_gift_chain_missing));
                    return;
                }
                Gift gift = new Gift(txtTitle.getText().toString());
                if(!TextUtils.isEmpty(txtDescription.getText())) {
                    gift.setDescription(txtDescription.getText().toString());
                }
                final List<String> accountInfo = SecurityUtils.getAccountInfo(CreateGiftActivity.this);
                if(accountInfo != null){
                    UploadGiftTask task = new UploadGiftTask(
                            accountInfo.get(0),
                            accountInfo.get(1),
                            "mobile",
//                            spinnerGiftChain.getSelectedItem().toString(),
                            autoCompleteTextView.getText().toString(),
                            gift,
                            new File(imagePath), CreateGiftActivity.this);
                    task.execute();
                }
            }
        });

        imgViewGift = (ImageView) findViewById(R.id.imageView);
        final Bitmap imageBitmap = BitmapUtils.decodeSampledBitmapFromFile(imagePath, 500, 500, false);

        imgViewGift.setImageBitmap(imageBitmap);
        imgViewGift.setDrawingCacheEnabled(true);
        imgViewGift.buildDrawingCache();

        txtTitle = (EditText) findViewById(R.id.edit_title);
        txtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    txtTitle.setError(null);
                }
            }
        });

        txtDescription = (EditText) findViewById(R.id.edit_description);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.gift_chains);

        if(!TextUtils.isEmpty(giftChain)){
            autoCompleteTextView.setText(giftChain);
        }
        new ListGiftChainsTask(this).execute();

    }

    /**
     * Display the Dialog to the User.
     *
     * @param message
     *          The String to display what download method was used.
     */
    public void showDialog(String message) {
        mProgressDialog =
                ProgressDialog.show(this, "Posting Gift", message);
    }

    /**
     * Dismiss the Dialog
     */
    public void dismissDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

//    public void onResult(){
//        finish();
//        setResult(RESULT_OK);
//
//    }
//
//    @Override
//    public void finish() {
//        super.finish();
//
//    }

    @Override
    public void addGiftChains(List<String> giftChains){
        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, giftChains.toArray());
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.invalidate();
    }
}
