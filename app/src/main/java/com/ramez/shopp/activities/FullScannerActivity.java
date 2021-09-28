package com.ramez.shopp.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.databinding.ActivityScanBarcodeBinding;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class FullScannerActivity extends ActivityBase implements ZBarScannerView.ResultHandler {

    //private ZBarScannerView mScannerView;
    ActivityScanBarcodeBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  binding.qrScannerView = new ZBarScannerView(getActiviy());
        binding = ActivityScanBarcodeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTitle("");

        //setContentView(mScannerView);

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

        Log.i(getClass().getSimpleName(), "Log " + "Contents = " + rawResult.getContents() + ", Format = " + rawResult.getBarcodeFormat().getName());


    }

    @Override
    public void onResume() {
        super.onResume();
        binding.qrScannerView.setResultHandler(this);
        binding.qrScannerView.setAutoFocus(true);
        binding.qrScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.qrScannerView.stopCamera();
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