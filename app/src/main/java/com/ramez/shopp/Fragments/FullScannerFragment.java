package com.ramez.shopp.Fragments;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ramez.shopp.Activities.SearchActivity;
import com.ramez.shopp.Classes.Constants;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class FullScannerFragment extends Fragment implements ZBarScannerView.ResultHandler {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        mScannerView = new ZBarScannerView(getActivity());
//        if (state != null) {
//            mFlash = state.getBoolean(FLASH_STATE, false);
//            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
//            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
//            mCameraId = state.getInt(CAMERA_ID, -1);
//        } else {
//            mFlash = false;
//            mAutoFocus = true;
//            mSelectedIndices = null;
//            mCameraId = -1;
//        }
        setupFormats();
        return mScannerView;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setHasOptionsMenu(true);
    }


    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.setAutoFocus(true);
//        mScannerView.setFlash(mFlash);
        mScannerView.startCamera();
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        try {
//            outState.putBoolean(FLASH_STATE, mFlash);
//
//        } catch (Exception e) {
//            System.out.println("The 'try catch' is finished.");
//        }
//        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
//        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
//        outState.putInt(CAMERA_ID, mCameraId);
//    }

    @Override
    public void handleResult(Result rawResult) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
            r.play();


        } catch (Exception e) {

        }

        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.putExtra(Constants.CODE, rawResult.getContents());
//        intent.putExtra(Constants.CODE, "6221155068897");
        intent.putExtra(Constants.SEARCH_BY_CODE_byCode, true);
        startActivity(intent);
        getActivity().finish();
        Log.i("tag", "Log " + "Contents = " + rawResult.getContents() + ", Format = " + rawResult.getBarcodeFormat().getName());


    }


    public void closeFormatsDialog() {
        closeDialog("format_selector");
    }

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if (fragment != null) {
            fragment.dismiss();
        }
    }


    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<Integer>();
            for (int i = 0; i < BarcodeFormat.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for (int index : mSelectedIndices) {
            formats.add(BarcodeFormat.ALL_FORMATS.get(index));
        }
        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        closeFormatsDialog();
    }
}
