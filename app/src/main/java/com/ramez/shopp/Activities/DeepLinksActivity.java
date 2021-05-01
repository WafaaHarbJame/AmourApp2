package com.ramez.shopp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.R;

import java.util.ArrayList;
import java.util.List;

public class DeepLinksActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();

        if (data != null && data.getPathSegments() != null) {

            List<String> list = data.getPathSegments();
            if (list.size() > 0) {
                Log.i("Log all list ", "" + list.toString());
                Log.i("Log size", "" + list.size());
                Log.i("Log data", "" + data);
                Log.i("Log data getPath ", "" + data.getPath());
                Log.i("Log data", "" + data.getHost());
                Log.i("Log getHost", "" + data.getHost());
                Log.i("Log segment1", "" + list.get(0));
//                Log.i("Log segment2", "" + list.get(1));
//                Log.i("Log segment3", "" + list.get(2));
//                Log.i("Log segment4", "" + list.get(3));

//                if (list.get(0).equals("product")) {
//                    Intent intent = new Intent(getActiviy(), ProductDetailsActivity.class);
//                    intent.putExtra(Constants.isNotify, true);
//                    intent.putExtra(Constants.product_id, list.get(2));
//                    intent.putExtra(Constants.FROM_BROSHER, true);
//                    startActivity(intent);
//
//                    finish();
//
//                }


                if (list.get(0).equals("product")) {
                    Intent intent = new Intent(getActiviy(), ProductDetailsActivity.class);
                    intent.putExtra(Constants.isNotify, true);

                    if (data.getQueryParameter("id") != null) {
                        Log.i("segment id", "id" + data.getQueryParameter("id"));
                        String product_id = data.getQueryParameter("id");
                        intent.putExtra(Constants.product_id, product_id);
                        intent.putExtra(Constants.FROM_BROSHER, true);
                        startActivity(intent);

                        finish();
                    }


                }


            }


        }


    }
}