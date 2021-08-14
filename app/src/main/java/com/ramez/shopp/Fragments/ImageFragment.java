package com.ramez.shopp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Dialogs.ShowImageDialog;
import com.ramez.shopp.Models.CartResultModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.FragmentProductImageBinding;
import com.ramez.shopp.databinding.FragmentProductsBinding;

public class ImageFragment extends FragmentBase {
    FragmentProductImageBinding binding;
    String imageUrl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductImageBinding.inflate(inflater, container, false);

        getExtraIntent();

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.slideImg.setOnClickListener(v -> {
            ShowImageDialog showImageDialog = new ShowImageDialog(getActivity(), imageUrl);
            showImageDialog.show();

        });
    }

    private void getExtraIntent() {

        Bundle bundle = getArguments();
        if (bundle != null) {
            imageUrl = bundle.getString(Constants.KEY_IMAGE_URL);
            GlobalData.GlideImgGif(getActivityy(), imageUrl, R.drawable.holder_image, binding.slideImg);

        }


    }

}