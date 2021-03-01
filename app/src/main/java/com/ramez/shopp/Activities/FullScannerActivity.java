package com.ramez.shopp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Fragments.HomeFragment;
import com.ramez.shopp.Fragments.SearchFragment;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class FullScannerActivity extends ActivityBase implements ZBarScannerView.ResultHandler {
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";

    private ZBarScannerView mScannerView;
    //    private boolean mFlash=true;
//    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    //    private final int mCameraId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZBarScannerView(getActiviy());
        setContentView(mScannerView);

    }

    @Override
    public void handleResult(Result rawResult) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActiviy().getApplicationContext(), notification);
            r.play();


        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent();
        intent.putExtra(Constants.SEARCH_BY_CODE_byCode, true);
        intent.putExtra(Constants.CODE, rawResult.getContents());
        setResult(Activity.RESULT_OK, intent);
        finish();

        Log.i("tag", "Log " + "Contents = " + rawResult.getContents() + ", Format = " + rawResult.getBarcodeFormat().getName());


    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.setAutoFocus(true);
//        mScannerView.setFlash(mFlash);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        closeFormatsDialog();
    }

    public void closeFormatsDialog() {
        closeDialog("format_selector");
    }

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if (fragment != null) {
            fragment.dismiss();
        }
    }
}