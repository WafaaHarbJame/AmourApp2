package com.ramez.shopp.Classes;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.ramez.shopp.Models.CountryModel;
import com.ramez.shopp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class GlobalData {

    public static final String BetaBaseURL = "https://risteh.com/";
    public static final String grocery = "/GroceryStoreApi/";
    public static final String BaseURL = BetaBaseURL;
    //public static final String ApiURL = BaseURL + "api/";
    public static final String Api = "api/";
    public static final String COUNTRY = "BH";
    public static final Boolean refresh_cart = false;
    public static Boolean refresh_points = false;


    public static int Position = 0;

    public static boolean EDIT_PROFILE = false;
    public static boolean REFRESH_ADV = false;
    public static boolean REFRESH_CART = false;
    public static int CHAT_ID_OPEN = 0;
    public static AwesomeErrorDialog errorDialog;
    public static AwesomeInfoDialog infoDialog;
    public static AwesomeSuccessDialog successDialog;
    static AwesomeProgressDialog progressDialog;

    //============================================================================

    public static void PicassoImg(String image, int placeholder, ImageView imageView) {
        String photoUrl = "";

        if (image != null && !image.isEmpty()) {
            photoUrl = image;
        } else {
            photoUrl = BetaBaseURL;
        }

        String finalPhotoUrl = photoUrl;

        Picasso.get().load(finalPhotoUrl).placeholder(placeholder).error(R.drawable.holder_image).into(imageView);

    }


    public static void PicassoOffLineImg(String image, int placeholder, ImageView imageView) {
        String photoUrl = "";

        if (image != null && !image.isEmpty()) {
            photoUrl = image;
        } else {
            photoUrl = BetaBaseURL;
        }

        String finalPhotoUrl = photoUrl;
        Picasso.get().load(photoUrl).placeholder(placeholder).error(R.drawable.holder_image).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(finalPhotoUrl).placeholder(placeholder).error(R.drawable.holder_image).into(imageView);
            }
        });

    }

    public static void GlideImg(Context c, String image, int placeholder, ImageView imageView) {
        String photoUrl = "";
        RequestOptions requestOptions = new RequestOptions();
        if (image != null && !image.isEmpty()) {
            photoUrl = image;
        } else {
            photoUrl = BetaBaseURL;
        }

        try {
            Glide.with(c).asBitmap().load(photoUrl)
                    .apply(requestOptions)
                    .thumbnail(0.5f)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .placeholder(placeholder).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void GlideImgGif(Context c, String image, int placeholder, ImageView imageView) {
        String photoUrl = "";

        if (image != null && !image.isEmpty()) {
            photoUrl = image;
        } else {
            photoUrl = BetaBaseURL;
        }

        try {
            Glide.with(c).load(photoUrl).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).placeholder(placeholder).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static void GlideImgWithTransform(Context c, String image, int placeholder, ImageView imageView) {
        String photoUrl = "";

        if (image != null && !image.isEmpty()) {
            photoUrl = image;
        } else {
            photoUrl = BetaBaseURL;
        }

        try {
            Glide.with(c).load(photoUrl)
                    .transform(new RoundedCorners(25))
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .placeholder(placeholder).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void GlideImgWeb(Context c, String image, int placeholder, ImageView imageView) {
        String photoUrl = "";

        if (image != null && !image.isEmpty()) {
            photoUrl = image;
        } else {
            photoUrl = BetaBaseURL;
        }

        try {

            Glide.with(c).load(photoUrl)
//                    .apply(requestOptions)
//                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).placeholder(placeholder).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void GlideImgGifSize(Context c, String image, int placeholder, ImageView imageView) {
        String photoUrl = "";

        if (image != null && !image.isEmpty()) {
            photoUrl = image;
        } else {
            photoUrl = BetaBaseURL;
        }

        try {
            Glide.with(c).load(photoUrl).placeholder(placeholder).apply(new RequestOptions().transform(new RoundedCorners((int) c.getResources().getDimension(R.dimen._8sdp))).error(R.drawable.holder_image).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void progressDialog(Context c, int title, int msg) {
        progressDialog = new AwesomeProgressDialog(c);
        progressDialog.setTitle(title).setMessage(msg).setColoredCircle(R.color.colorPrimaryDark).setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white).setCancelable(false);
        progressDialog.show();

    }

    public static void hideProgressDialog() {
        if (progressDialog != null) progressDialog.hide();

    }

    public static void errorDialog(Context c, int title, String msg) {
        errorDialog = new AwesomeErrorDialog(c);
        errorDialog.setTitle(title);
        errorDialog.setMessage(msg);
        errorDialog.setColoredCircle(R.color.dialogErrorBackgroundColor).setDialogIconAndColor(R.drawable.ic_dialog_error, R.color.white).setCancelable(true).setButtonBackgroundColor(R.color.dialogErrorBackgroundColor);
        errorDialog.show();

    }


    public static void errorDialogWithButton(Context c, String title, String msg) {
        errorDialog = new AwesomeErrorDialog(c);
        errorDialog.setTitle(title);
        errorDialog.setMessage(msg);
        errorDialog.setColoredCircle(R.color.dialogErrorBackgroundColor).
                setDialogIconAndColor(R.drawable.ic_dialog_error, R.color.white).setCancelable(true).
                setButtonBackgroundColor(R.color.dialogErrorBackgroundColor).setButtonText(c.getString(R.string.ok)).setErrorButtonClick(() -> errorDialog.hide())

        ;
        errorDialog.show();

    }

    public static void infoDialog(Context c, String title, String msg) {
        infoDialog = new AwesomeInfoDialog(c);
        infoDialog.setMessage(msg);
        infoDialog.setTitle(title);
        infoDialog.setColoredCircle(R.color.dialogInfoBackgroundColor).setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white).setCancelable(true);
        infoDialog.show();

    }


    public static void successDialog(Context c, String title, String msg) {
        successDialog = new AwesomeSuccessDialog(c);
        successDialog.setTitle(title).setMessage(msg).setColoredCircle(R.color.dialogSuccessBackgroundColor).setDialogIconAndColor(R.drawable.ic_check, R.color.white).setCancelable(true);
        successDialog.show();

    }


    public static void Toast(Context context, String msg) {

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void Toast(Context context, int resId) {

        Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show();
    }

    public static String getIntro(String countryCode) {
        String introStr = "3";
        HashMap<String, String> hashMap = new HashMap<String, String>();

        // country Id :4 ,country name Oman
        // country Id :17 ,country name Bahrain
        // country Id :117 ,country name Kuwait
        // country Id :178 ,country name Qatar
        // country Id :191 ,country name Saudi_Arabia
        // country Id :229 ,country name United_Arab_Emirates_ar

        hashMap.put("968", "9");
        hashMap.put("973", "3");
        hashMap.put("965", "6");
        hashMap.put("974", "5");
        hashMap.put("966", "5");
        hashMap.put("971", "5");

        if(countryCode.equals("966")||countryCode.equals("971")){
            introStr=hashMap.get(countryCode)+"xxxxxxxxx";

        }
        else {
            introStr=hashMap.get(countryCode)+ "xxxxxxxx";

        }

        return introStr;
    }


}
