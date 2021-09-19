package com.ramez.shopp.Activities;

import android.os.Bundle;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.CheckLoginDialog;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.Models.ReviewModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.ActivityRatingBinding;

public class RatingActivity extends ActivityBase {

    ActivityRatingBinding binding;
    MemberModel memberModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRatingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setTitle(R.string.rate_app);

        memberModel = UtilityApp.getUserData();

        binding.rateBut.setOnClickListener(view1 -> {
            int userId=memberModel!=null && memberModel.getId()!=null ?memberModel.getId():0;
            if(userId>0){
                ReviewModel reviewModel = new ReviewModel();
                String note = NumberHandler.arabicToDecimal(binding.rateEt.getText().toString());
                reviewModel.setComment(note);
                reviewModel.setUser_id(userId);
                reviewModel.setRate((int) binding.ratingBr.getRating());

                if (binding.ratingBr.getRating() == 0) {
                    Toast(R.string.please_fill_rate);
                    YoYo.with(Techniques.Shake).playOn(binding.ratingBr);
                    binding.rateBut.requestFocus();


                } else if (binding.rateEt.getText().toString().isEmpty()) {
                    binding.rateEt.requestFocus();
                    binding.rateEt.setError(getString(R.string.please_fill_comment));


                } else {
                    addRate(reviewModel);
                }
            }
            else {
                showDialogs(R.string.to_rate_app);

            }


        });


    }

    private void addRate(ReviewModel reviewModel) {

        GlobalData.progressDialog(getActiviy(), R.string.rate_app, R.string.please_wait_sending);

        new DataFeacher(false, (obj, func, IsSuccess) -> {

            String message = getString(R.string.fail_add_comment);

            ResultAPIModel<ReviewModel> result = (ResultAPIModel<ReviewModel>) obj;

            if (result != null) {

                message = result.message;

            }


            GlobalData.hideProgressDialog();

            if (func.equals(Constants.ERROR)) {

                GlobalData.errorDialog(getActiviy(), R.string.rate_app, message);


            } else if (func.equals(Constants.FAIL)) {
                GlobalData.errorDialog(getActiviy(), R.string.rate_app, message);


            } else if (func.equals(Constants.NO_CONNECTION)) {
                GlobalData.errorDialog(getActiviy(), R.string.rate_app, getString(R.string.no_internet_connection));

            } else {

                if (IsSuccess) {

                    GlobalData.hideProgressDialog();
                    binding.rateEt.setText("");
                    binding.ratingBr.setRating(0);
                    GlobalData.successDialog(getActiviy(), getString(R.string.rate_app), getString(R.string.success_rate_app));


                } else {
                    GlobalData.hideProgressDialog();
                    GlobalData.errorDialog(getActiviy(), R.string.rate_app, getString(R.string.fail_add_comment));
                }

            }

        }).setAppRate(reviewModel);
    }


    private void showDialogs(int message){
        CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActiviy(), R.string.LoginFirst, message, R.string.ok, R.string.cancel, null, null);
        checkLoginDialog.show();
        checkLoginDialog.show();
         }


}