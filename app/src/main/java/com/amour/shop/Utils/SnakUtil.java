package com.amour.shop.Utils;

import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnakUtil {

    private Context _con;
    public static View v;

    public SnakUtil (Context _con) {
        this._con = _con;
    }

    public static void snackBar(String msg,View v) {
        Snackbar.make(v, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

}
