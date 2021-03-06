package com.amour.shop.activities


import android.os.Bundle
import android.view.View
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.classes.Constants
import com.amour.shop.classes.GlobalData.errorDialog
import com.amour.shop.classes.GlobalData.hideProgressDialog
import com.amour.shop.classes.GlobalData.progressDialog
import com.amour.shop.classes.GlobalData.successDialog
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Dialogs.CheckLoginDialog
import com.amour.shop.Models.MemberModel
import com.amour.shop.Models.ResultAPIModel
import com.amour.shop.Models.ReviewModel
import com.amour.shop.R
import com.amour.shop.Utils.NumberHandler
import com.amour.shop.databinding.ActivityRatingBinding


class RatingActivity : ActivityBase() {
    lateinit var binding: ActivityRatingBinding
    var memberModel: MemberModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        setTitle(R.string.rate_app)
        memberModel = UtilityApp.getUserData()
        binding.rateBut.setOnClickListener { view1 ->
            val userId = if (memberModel != null && memberModel?.id != null)
                    memberModel?.id else 0

            if (userId != null) {
                if (userId > 0) {
                    val reviewModel = ReviewModel()
                    val note = NumberHandler.arabicToDecimal(binding.rateEt.text.toString())
                    reviewModel.comment = note
                    reviewModel.user_id = userId
                    reviewModel.rate = binding.ratingBr.rating.toInt()
                    when {
                        binding.ratingBr.rating == 0F -> {
                            Toast(R.string.please_fill_rate)
                            YoYo.with(Techniques.Shake).playOn(binding.ratingBr)
                            binding.rateBut.requestFocus()
                        }
                        binding.rateEt.text.toString().isEmpty() -> {
                            binding.rateEt.requestFocus()
                            binding.rateEt.error = getString(R.string.please_fill_comment)
                        }
                        else -> {
                            addRate(reviewModel)
                        }
                    }
                } else {
                    showDialogs(R.string.to_rate_app)
                }
            }
        }
    }

    private fun addRate(reviewModel: ReviewModel) {
        progressDialog(activity, R.string.rate_app, R.string.please_wait_sending)
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                var message = getString(R.string.fail_add_comment)
                val result = obj as ResultAPIModel<ReviewModel>?
                if (result != null) {
                    message = result.message
                }
                hideProgressDialog()
                if (func == Constants.ERROR) {
                    errorDialog(activity, R.string.rate_app, message)
                } else if (func == Constants.FAIL) {
                    errorDialog(activity, R.string.rate_app, message)
                } else if (func == Constants.NO_CONNECTION) {
                    errorDialog(
                        activity,
                        R.string.rate_app,
                        getString(R.string.no_internet_connection)
                    )
                } else {
                    if (IsSuccess) {
                        hideProgressDialog()
                        binding.rateEt.setText("")
                        binding.ratingBr.rating = 0F
                        successDialog(
                            activity,
                            getString(R.string.rate_app),
                            getString(R.string.success_rate_app)
                        )
                    } else {
                        hideProgressDialog()
                        errorDialog(
                            activity,
                            R.string.rate_app,
                            getString(R.string.fail_add_comment)
                        )
                    }
                }            }

        }).setAppRate(reviewModel)
    }

    private fun showDialogs(message: Int) {
        val checkLoginDialog =
            CheckLoginDialog(activity, R.string.LoginFirst, message, R.string.ok, R.string.cancel, null, null)
        checkLoginDialog.show()
        checkLoginDialog.show()
    }
}